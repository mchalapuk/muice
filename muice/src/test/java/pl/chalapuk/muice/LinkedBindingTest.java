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
import pl.chalapuk.muice.TestedTypes.WithDefaultConstructor;
import pl.chalapuk.muice.TestedTypes.*;
import org.junit.Test;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class LinkedBindingTest {

    @Test
    public void testLinkingToClassBoundToItself() {

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(WithDefaultConstructor.class);
                binder.bind(Object.class)
                        .to(WithDefaultConstructor.class);
            }
        });

        assertEquals(WithDefaultConstructor.class,
                injector.getInstance(Object.class).getClass());
    }

    @Test
    public void testLinkingToGenericBoundToItself() {

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(TypeLiteral.get(Generic.class, String.class));
                binder.bind(Object.class)
                        .to(TypeLiteral.get(Generic.class, String.class));
            }
        });

        assertEquals(Generic.class,
                injector.getInstance(Object.class).getClass());
    }

    @Test
    public void testLinkingToClassBoundToInstance() {
        final WithDefaultConstructor instance = new WithDefaultConstructor();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(WithDefaultConstructor.class)
                        .toInstance(instance);
                binder.bind(Object.class)
                        .to(WithDefaultConstructor.class);
            }
        });

        assertSame(instance, injector.getInstance(WithDefaultConstructor.class));
        assertSame(instance, injector.getInstance(Object.class));
    }

    @Test
    public void testLinkingToClassBoundToProviderType() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(ObjectProvider.class);
                binder.bind(Object.class)
                        .toProvider(ObjectProvider.class);
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationA.class)
                        .to(Object.class);
            }
        });

        assertEquals(Object.class,
                injector.getInstance(Key.get(Object.class, QualifierAnnotationA.class)).getClass());
    }

    @Test
    public void testLinkingToClassBoundToMuiceProvider() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProvider(new Provider<Object>() {
                            private Object mInstance;

                            @Override
                            public void initialize(Injector usused) {
                                mInstance = new WithDefaultConstructor();
                            }

                            @Override
                            public Object get() {
                                return mInstance;
                            }
                        });
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationA.class)
                        .to(Object.class);
            }
        });

        assertEquals(WithDefaultConstructor.class,
                injector.getInstance(Key.get(Object.class, QualifierAnnotationA.class)).getClass());
    }

    @Test
    public void testLinkingToClassBoundToJavaxInjectProvider() {
        final Object instance = new Object();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProvider(new javax.inject.Provider<Object>() {

                            @Override
                            public Object get() {
                                return instance;
                            }
                        });
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationA.class)
                        .to(Object.class);
            }
        });

        assertSame(instance,
                injector.getInstance(Key.get(Object.class, QualifierAnnotationA.class)));
    }

    @Test
    public void testLinkingToClassBoundToConstructor() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(WithDefaultConstructor.class)
                            .toConstructor(WithDefaultConstructor.class.getDeclaredConstructor());
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
                binder.bind(Object.class)
                        .to(WithDefaultConstructor.class);
            }
        });

        assertEquals(WithDefaultConstructor.class,
                injector.getInstance(Object.class).getClass());
    }

    @Test
    public void testLinkingToClassBoundToProducer() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProducer(new Producer<Object>() {

                            @Override
                            public Object newInstance(Injector unused) {
                                return new WithDefaultConstructor();
                            }
                        });
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationA.class)
                        .to(Object.class);
            }
        });

        assertEquals(WithDefaultConstructor.class,
                injector.getInstance(Key.get(Object.class, QualifierAnnotationA.class)).getClass());
    }

    @Test
    public void testLinkingToLinkedBinding() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProducer(new Producer<Object>() {

                            @Override
                            public Object newInstance(Injector unused) {
                                return new WithDefaultConstructor();
                            }
                        });
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationA.class)
                        .to(Object.class);
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationB.class)
                        .to(Key.get(Object.class, QualifierAnnotationA.class));
            }
        });

        assertEquals(WithDefaultConstructor.class,
                injector.getInstance(Key.get(Object.class, QualifierAnnotationB.class)).getClass());
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenLinkingToNotBoundKey() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .to(Interface.class);
            }
        });
    }
}
