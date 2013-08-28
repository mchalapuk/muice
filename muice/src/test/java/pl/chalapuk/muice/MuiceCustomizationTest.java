/*
 * Copyright (C) 2013 Maciej Chałapuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.chalapuk.muice;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.net.URI;

import javax.inject.Provider;

import org.junit.Test;

import pl.chalapuk.muice.customization.BindingCollector;
import pl.chalapuk.muice.customization.BindingCollectorFactory;
import pl.chalapuk.muice.customization.ConstructorInfo;
import pl.chalapuk.muice.customization.ProducerFactory;
import pl.chalapuk.muice.customization.RawTypeInfo;
import pl.chalapuk.muice.customization.TypeInfoException;
import pl.chalapuk.muice.customization.TypeInfoFactory;
import pl.chalapuk.muice.defaults.ExplicitCollectorFactory;
import pl.chalapuk.muice.internal.Scopes;

public class MuiceCustomizationTest {

    static class WithDefaultConstructor {
        // empty
    }

    @Test
    public void testInjectorUsesCustomBindingCollectorWhenInstantiatingObject() {
        final URI uri = URI.create("http://example.com/");

        BindingCollector collector = new ExplicitCollectorFactory().createCollector();
        collector.add(new Binding<>(Key.get(Object.class), new Producer<URI>() {

            @Override
            public URI newInstance(Injector injector) {
                return uri;
            }
        }, Scopes.NONE));

        Muice muice = Muice.newMuice()
                .withBindingCollectorFactory(factoryOf(collector))
                .withoutBootModules()
                .build();

        Injector injector = muice.newInjector()
                .build();

        assertSame(uri, injector.getInstance(Object.class));
    }

    @Test
    public void testInjectorUsesCustomBindingCollectorToStoreBindings() {
        final URI uri = URI.create("http://example.com/");

        BindingCollector collector = new ExplicitCollectorFactory().createCollector();

        Muice muice = Muice.newMuice()
                .withBindingCollectorFactory(factoryOf(collector))
                .withoutBootModules()
                .build();

        Injector injector = muice.newInjector()
                .withModules(new BindingModule() {

                    @Override
                    public void configure(Binder binder) {
                        binder.bind(Object.class).toInstance(uri);
                    }
                })
                .build();

        Binding<Object> binding = collector.get(Key.get(Object.class));
        assertSame(uri, binding.getTarget().newInstance(injector));
    }

    @Test
    public void testCustomTypeInfoFactoryUsedWhenBindingTypeToItself() {
        final ConstructorInfo<Object> constructoInfo = createNoArgConstructorInfo(WithDefaultConstructor.class);
        final RawTypeInfo<Object> typeInfo = new RawTypeInfo<>(constructoInfo, null);

        Muice muice = Muice.newMuice()
                .withTypeInfoFactory(new TypeInfoFactory() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public <T> RawTypeInfo<T> getRawTypeInfo(Class<? super T> rawType)
                            throws TypeInfoException {
                        return (RawTypeInfo<T>) typeInfo;
                    }

                    @Override
                    public <T> ConstructorInfo<T> getConstructorInfo(Constructor<T> constructor) {
                        throw new AssertionError("constructor binding should not be tested");
                    }
                })
                .withoutBootModules()
                .build();

        Injector injector = muice.newInjector()
                .withModules(new BindingModule() {

                    @Override
                    public void configure(Binder binder) {
                        binder.bind(Object.class);
                    }
                })
                .build();

        assertEquals(WithDefaultConstructor.class,
                injector.getInstance(Object.class).getClass());
    }

    @Test
    public void testCustomProducerFactoryUsedWhenBindingTypeToItself() {
        final WithDefaultConstructor instance = new WithDefaultConstructor();

        Producer<WithDefaultConstructor> producer = new Producer<WithDefaultConstructor>() {

            @Override
            public WithDefaultConstructor newInstance(Injector injector) {
                return instance;
            }
        };
        Muice muice = Muice.newMuice()
                .withProducerFactory(factoryOf(producer))
                .build();

        Injector injector = muice.newInjector()
                .withModules(new BindingModule() {

                    @Override
                    public void configure(Binder binder) {
                        binder.bind(Object.class);
                    }
                })
                .build();

        assertEquals(WithDefaultConstructor.class,
                injector.getInstance(Object.class).getClass());
    }

    @Test
    public void testInjectorContainsBindingsFromCustomBootModules() {
        Muice muice = Muice.newMuice()
                .withBootModules(new BindingModule() {

                    @Override
                    public void configure(Binder binder) {
                        binder.bind(WithDefaultConstructor.class);
                        binder.bind(Object.class).to(WithDefaultConstructor.class);
                    }
                })
                .build();

        Injector injector = muice.newInjector()
                .build();

        assertEquals(WithDefaultConstructor.class,
                injector.getInstance(Object.class).getClass());
    }

    @Test
    public void testCustomDefaultScopeUsedWhenBindingTypeWithoutScope() {
        final URI uri = URI.create("http://example.com/");
        final Provider<URI> provider = new Provider<URI>() {

            @Override
            public URI get() {
                return uri;
            }
        };
        
        Muice muice = Muice.newMuice()
                .withDefaultScope(new Scope() {
                    
                    @SuppressWarnings("unchecked")
                    @Override
                    public <T> Provider<? extends T> decorate(Key<T> key, Provider<? extends T> unscoped) {
                        return (Provider<? extends T>) provider;
                    }
                })
                .build();

        Injector injector = muice.newInjector()
                .withModules(new BindingModule() {

                    @Override
                    public void configure(Binder binder) {
                        binder.bind(Object.class);
                    }
                })
                .build();

        assertSame(uri, injector.getInstance(Object.class));
    }

    private static BindingCollectorFactory factoryOf(final BindingCollector collector) {
        return new BindingCollectorFactory() {

            @Override
            public BindingCollector createCollector() {
                return collector;
            }
        };
    }

    private static ConstructorInfo<Object> createNoArgConstructorInfo(final Class<?> type) {
        try {
            Constructor<Object> declaredConstructor =
                    (Constructor<Object>) type.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return new ConstructorInfo<>(declaredConstructor, new Key<?>[0]);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static ProducerFactory factoryOf(final Producer<?> producer) {
        return new ProducerFactory() {

            @SuppressWarnings("unchecked")
            @Override
            public <T> Producer<T> createProducer(ConstructorInfo<T> info) {
                return (Producer<T>) producer;
            }
        };
    }
}
