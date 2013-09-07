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
import static pl.chalapuk.muice.TestedTypes.*;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import pl.chalapuk.muice.TestedTypes.Generic;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class BindingToProviderTypeTest {

    static class ProviderWithDependency<T> implements javax.inject.Provider<T> {
        public static Object sDependency;

        @Inject
        public ProviderWithDependency(Object dependency) {
            sDependency = dependency;
        }

        @Override
        public T get() {
            return null;
        }
    }

    static class CountingProvider implements javax.inject.Provider<Object> {
        public static int sCount;

        public CountingProvider() {
            sCount += 1;
        }

        @Override
        public Object get() {
            return new Object();
        }
    }

    @Before
    public void nullifyDependency() {
        ProviderWithDependency.sDependency = null;
    }

    @Before
    public void resetCount() {
        CountingProvider.sCount = 0;
    }

    @Test
    public void testBindingObjectClassToProviderType() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(ObjectProvider.class);
                binder.bind(Object.class)
                        .toProvider(ObjectProvider.class);
            }
        });

        assertNotNull(injector.getInstance(Object.class));
    }

    @Test
    public void testNotNullInjectorIsPassedToProviderWithDependency() {
        final Object dependency = new Object();

        Injector injector = Muice.createInjector(new BindingModule() {

            @SuppressWarnings({
                    "unchecked", "rawtypes"
            })
            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toInstance(dependency);
                binder.bind(ProviderWithDependency.class);
                binder.bind(Generic.class)
                        .toProvider((Class) ProviderWithDependency.class);
            }
        });

        injector.getInstance(Generic.class);
        assertSame(dependency, ProviderWithDependency.sDependency);
    }

    @Test
    public void testProviderInstantiatedOnlyOnce() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(CountingProvider.class);
                binder.bind(Object.class)
                        .toProvider(CountingProvider.class);
            }
        });

        Object injected0 = injector.getInstance(Object.class);
        Object injected1 = injector.getProvider(Object.class).get();
        assertNotEquals(injected0, injected1);

        assertEquals(1, CountingProvider.sCount);
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorBindingToUnknownProviderType() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProvider(CountingProvider.class);
            }
        });
    }

    @Test
    public void testInjectionErrorWhenObjectOfIncompatibleTypeProvided() {

        Injector injector = Muice.createInjector(new BindingModule() {

            @SuppressWarnings({
                    "unchecked", "rawtypes"
            })
            @Override
            public void configure(Binder binder) {
                binder.bind(CountingProvider.class);
                binder.bind(Generic.class)
                        .toProvider((Class) CountingProvider.class);
            }
        });

        try {
            injector.getInstance(Generic.class);
            fail("expected " + InjectionError.class);
        } catch (InjectionError e) {
            assertEquals(ClassCastException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testInjectionErrorWhenProviderGetThrowsRuntimeException() {

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(ThrowingFromGetProvider.class);
                binder.bind(Object.class)
                        .toProvider(ThrowingFromGetProvider.class);
            }
        });

        try {
            injector.getInstance(Object.class);
            fail("expected " + InjectionError.class);
        } catch (InjectionError e) {
            assertEquals(RuntimeException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testInjectionErrorWhenProviderConstructorThrowsRuntimeException() {

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(ThrowingFromConstructorProvider.class);
                binder.bind(Object.class)
                        .toProvider(ThrowingFromConstructorProvider.class);
            }
        });

        try {
            injector.getInstance(Object.class);
            fail("expected " + InjectionError.class);
        } catch (InjectionError e) {
            assertEquals(RuntimeException.class, e.getCause().getClass());
        }
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenBindingToNullProvider() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProvider(
                        (Class<? extends javax.inject.Provider<? extends Object>>) null);
            }
        });
    }
}
