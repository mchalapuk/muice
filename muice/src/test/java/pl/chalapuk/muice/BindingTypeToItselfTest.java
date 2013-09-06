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

import org.junit.Test;

import pl.chalapuk.muice.TestedTypes.Inner;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class BindingTypeToItselfTest {

    private static class PrivateConstrutorInPrivateClass {
        private PrivateConstrutorInPrivateClass() {
            // empty
        }
    }

    @Test
    public void testBindingObjectClassToItself() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class);
            }
        });

        assertNotNull(injector.getInstance(Object.class));
    }

    @Test
    public void testBindingGenericToItseltWithoutGenericParameterSpecified() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Generic.class);
            }
        });

        assertNotNull(injector.getInstance(Generic.class));
    }

    @Test
    public void testBindingGenericToItseltWithGenericParameterSpecified() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(TypeLiteral.get(Generic.class, Object.class));
            }
        });

        assertNotNull(injector.getProvider(TypeLiteral.get(Generic.class, Object.class)).get());
    }

    @Test
    public void testBindingClassWithExplicitNoArgContructorToItselt() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(WithExplicitConstructor.class);
            }
        });

        assertNotNull(injector.getInstance(WithExplicitConstructor.class));
    }

    @Test
    public void testBindingClassWithInjectAnnotatedNoArgContructorToItselt() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(WithInjectAnnotatedConstructor.class);
            }
        });

        assertNotNull(injector.getInstance(WithInjectAnnotatedConstructor.class));
    }

    @Test
    public void testBindingClassWithMultipleContructorsToItselt() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(WithMultipleConstructors.class);
            }
        });

        assertNotNull(injector.getInstance(WithMultipleConstructors.class));
    }

    @Test
    public void testBindingPrivateClassWithPrivateConstructorToItself() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(PrivateConstrutorInPrivateClass.class);
            }
        });

        assertNotNull(injector.getInstance(PrivateConstrutorInPrivateClass.class));
    }

    @Test
    public void testBindingPublicClassWithPrivateInjectAnnotatedConstructorToItself() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(PublicWithPrivateInjectAnnotatedConstrutor.class);
            }
        });

        assertNotNull(injector.getInstance(PublicWithPrivateInjectAnnotatedConstrutor.class));
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingInterfaceToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Interface.class);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingAbstractClassToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Abstract.class);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingInnerClassToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Inner.class);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingClassWithMultipleInjectAnnotatedConstructorsToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(WithMultipleInjectAnnotatedConstructors.class);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingClassWithNoAnnotatedConstructorToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(WithNotInjectAnnotatedConstructor.class);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingPackageScopedClassWithPrivateConstructorToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(PackageScopedWithPrivateConstrutor.class);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingProtectedClassWithPrivateConstructorToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(ProtectedWithPrivateConstrutor.class);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingPublicClassWithPrivateConstructorToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(PublicWithPrivateConstrutor.class);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingSameTypeToItselfTwice() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class);
                binder.bind(TypeLiteral.get(Object.class));
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenBindingNullTypeToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind((Class<?>) null);
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenBindingNullTypeLiteralToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind((TypeLiteral<?>) null);
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenBindingNullKeyToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind((Key<?>) null);
            }
        });
    }
}
