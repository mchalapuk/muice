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
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;

import javax.inject.Singleton;

import org.junit.Test;

import pl.chalapuk.muice.TestedTypes.*;
import pl.chalapuk.muice.internal.Scopes;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class ScopedBindingTest {

    @Test
    public void testBindingClassToItselfInScopeInstance() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .in(Scopes.SINGLETON);
            }
        });

        assertSame(injector.getInstance(Object.class),
                injector.getInstance(Object.class));
    }

    @Test
    public void testBindingClassToItselfInScopeAnnotation() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .in(Singleton.class);
            }
        });

        assertSame(injector.getInstance(Object.class),
                injector.getInstance(Object.class));
    }

    @Test
    public void testBindingClassToProviderTypeInScopeInstance() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(ObjectProvider.class);
                binder.bind(Object.class)
                        .toProvider(ObjectProvider.class)
                        .in(Scopes.SINGLETON);
            }
        });

        assertSame(injector.getInstance(Object.class),
                injector.getInstance(Object.class));
    }

    @Test
    public void testBindingClassToProviderTypeInScopeAnnotation() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(ObjectProvider.class);
                binder.bind(Object.class)
                        .toProvider(ObjectProvider.class)
                        .in(Singleton.class);
            }
        });

        assertSame(injector.getInstance(Object.class),
                injector.getInstance(Object.class));
    }

    @Test
    public void testBindingClassToMuiceProviderInScopeInstance() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProvider(new Provider<Object>() {

                            @Override
                            public void initialize(Injector usused) {
                                // do nothing
                            }

                            @Override
                            public Object get() {
                                return new Object();
                            }
                        })
                        .in(Scopes.SINGLETON);
            }
        });

        assertSame(injector.getInstance(Object.class),
                injector.getInstance(Object.class));
    }

    @Test
    public void testBindingClassToMuiceProviderInScopeAnnotation() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProvider(new Provider<Object>() {

                            @Override
                            public void initialize(Injector usused) {
                                // do nothing
                            }

                            @Override
                            public Object get() {
                                return new Object();
                            }
                        })
                        .in(Singleton.class);
            }
        });

        assertSame(injector.getInstance(Object.class),
                injector.getInstance(Object.class));
    }

    @Test
    public void testBindingClassToJavaxInjectProviderInScopeInstance() {

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProvider(new javax.inject.Provider<Object>() {

                            @Override
                            public Object get() {
                                return new Object();
                            }
                        })
                        .in(Scopes.SINGLETON);
            }
        });

        assertSame(injector.getInstance(Object.class),
                injector.getInstance(Object.class));
    }

    @Test
    public void testBindingClassToJavaxInjectProviderInScopeAnnotation() {

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProvider(new javax.inject.Provider<Object>() {

                            @Override
                            public Object get() {
                                return new Object();
                            }
                        })
                        .in(Singleton.class);
            }
        });

        assertSame(injector.getInstance(Object.class),
                injector.getInstance(Object.class));
    }

    @Test
    public void testBindingClassToConstructorInScopeInstance() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(Object.class)
                            .toConstructor(Object.class.getDeclaredConstructor())
                            .in(Scopes.SINGLETON);
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        assertSame(injector.getInstance(Object.class),
                injector.getInstance(Object.class));
    }

    @Test
    public void testBindingClassToConstructorInScopeAnnotation() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(Object.class)
                            .toConstructor(Object.class.getDeclaredConstructor())
                            .in(Singleton.class);
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        assertSame(injector.getInstance(Object.class),
                injector.getInstance(Object.class));
    }

    @Test
    public void testBindingClassToProducerInScopeInstance() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProducer(new Producer<Object>() {

                            @Override
                            public Object newInstance(Injector unused) {
                                return new Object();
                            }
                        })
                        .in(Scopes.SINGLETON);
            }
        });

        assertSame(injector.getInstance(Object.class),
                injector.getInstance(Object.class));
    }

    @Test
    public void testBindingClassToProducerInScopeAnnotation() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProducer(new Producer<Object>() {

                            @Override
                            public Object newInstance(Injector unused) {
                                return new Object();
                            }
                        })
                        .in(Singleton.class);
            }
        });

        assertSame(injector.getInstance(Object.class),
                injector.getInstance(Object.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProviderScopedDuringInjectorCreation() {
        final Scope scope = mock(Scope.class);

        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .in(scope);
            }
        });

        verify(scope).decorate(eq(Key.get(Object.class)), notNull(Provider.class));
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenBindingInNullScope() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .in((Scope) null);
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenBindingInNullScopeAnnotation() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .in((Class<? extends Annotation>) null);
            }
        });
    }
}
