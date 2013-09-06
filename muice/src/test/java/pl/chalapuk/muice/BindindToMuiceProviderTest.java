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
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import pl.chalapuk.muice.TestedTypes.Generic;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class BindindToMuiceProviderTest {

    @Test
    public void testBindingObjectClassToProducer() {
        final Object instance = new Object();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProvider(new Provider<Object>() {

                    @Override
                    public Object get() {
                        return instance;
                    }

                    @Override
                    public void initialize(Injector unused) {
                        // empty
                    }
                });
            }
        });

        assertSame(instance, injector.getInstance(Object.class));
    }

    @Test
    public void testProviderGetCalledOnBoundProviderWhenCallingGetOnScopedProvider() {
        final Provider<Object> mockProvider = mock(Provider.class);

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProvider(mockProvider);
            }
        });

        injector.getProvider(Object.class).get();

        verify(mockProvider).get();
    }

    @Test
    public void testInitializeMethodCalledWhenInjecting() {
        final Provider<Object> mockProvider = mock(Provider.class);

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProvider(mockProvider);
            }
        });

        injector.getInstance(Object.class);

        verify(mockProvider).initialize((Injector) any());
    }

    @Test
    public void testInitializeMethodCalledWhenCallingGetOnScopedProvider() {
        final Provider<Object> mockProvider = mock(Provider.class);

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProvider(mockProvider);
            }
        });

        javax.inject.Provider<? extends Object> provider = injector.getProvider(Object.class);
        verifyZeroInteractions(mockProvider);

        provider.get();
        verify(mockProvider).initialize((Injector) any());
    }

    @Test
    public void testNotNullInjectorIsPassedToProviderWhenInjecting() {
        final Provider<Object> mockProvider = mock(Provider.class);

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProvider(mockProvider);
            }
        });

        injector.getInstance(Object.class);

        verify(mockProvider).initialize(notNull(Injector.class));
    }

    @Test
    public void testInjectorPassedToProviderContainTheSameBindingAsCreatedInjector() {
        final Provider<Object> mockProvider = mock(Provider.class);

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProvider(mockProvider);
            }
        });

        injector.getInstance(Object.class);

        ArgumentCaptor<Injector> captor = ArgumentCaptor.forClass(Injector.class);
        verify(mockProvider).initialize(captor.capture());
        assertEquals(injector.getBindings(), captor.getValue().getBindings());
    }

    @Test
    public void testInitializeMethodCalledOnlyBeforeFirstInjection() {
        final Provider<Object> mockProvider = mock(Provider.class);

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProvider(mockProvider);
            }
        });

        injector.getInstance(Object.class);
        injector.getProvider(Object.class).get();
        injector.getInstance(Object.class);
        injector.getProvider(Object.class).get();

        verify(mockProvider).initialize((Injector) any());
        verify(mockProvider, times(4)).get();
    }

    @Test
    public void testInjectionErrorWhenObjectOfIncompatibleTypeProvided() {

        Injector injector = Muice.createInjector(new BindingModule() {
            @SuppressWarnings({
                    "unchecked", "cast", "rawtypes"
            })
            @Override
            public void configure(Binder binder) {
                binder.bind(Generic.class)
                        .toProvider((Provider<Generic<?>>) (Provider) new Provider<Object>() {

                            @Override
                            public Object get() {
                                return new Object();
                            }

                            @Override
                            public void initialize(Injector unused) {
                                // empty
                            }
                        });
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
                binder.bind(Object.class)
                        .toProvider(new Provider<Object>() {

                            @Override
                            public Object get() {
                                throw new RuntimeException();
                            }

                            @Override
                            public void initialize(Injector unused) {
                                // empty
                            }
                        });
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
    public void testInjectionErrorWhenProviderInitializeThrowsRuntimeException() {

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProvider(new Provider<Object>() {

                            @Override
                            public Object get() {
                                return new Object();
                            }

                            @Override
                            public void initialize(Injector unused) {
                                throw new RuntimeException();
                            }
                        });
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
    public void testInjectionPropagatedWhenTHrownFromCustomProvider() {
        final InjectionError error = new InjectionError("test", new Exception());

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProvider(new Provider<Object>() {

                            @Override
                            public Object get() {
                                throw error;
                            }

                            @Override
                            public void initialize(Injector unused) {
                                // do nothing
                            }
                        });
            }
        });

        try {
            injector.getInstance(Object.class);
            fail("expected " + InjectionError.class);
        } catch (InjectionError e) {
            assertSame(error, e);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenBindingToNullProvider() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProvider((Provider<Object>) null);
            }
        });
    }
}
