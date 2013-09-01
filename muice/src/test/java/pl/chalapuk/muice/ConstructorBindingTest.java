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
public class ConstructorBindingTest {

    private static class PrivateConstrutorInPrivateClass {
        private PrivateConstrutorInPrivateClass() {
            // empty
        }
    }

    @Test
    public void testBindingClassToDefaultConstructor() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(WithDefaultConstructor.class)
                            .toConstructor(WithDefaultConstructor.class.getDeclaredConstructor());
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        assertNotNull(injector.getInstance(WithDefaultConstructor.class));
    }

    @Test
    public void testBindingGenericWithoutGenericParameterSpecifiedToDefaultConstructor() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(Generic.class)
                            .toConstructor(Generic.class.getDeclaredConstructor());
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        assertNotNull(injector.getInstance(Generic.class));
    }

    @Test
    public void testBindingGenericWithGenericParameterSpecifiedToDefaultConstructor() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(TypeLiteral.get(Generic.class, Object.class))
                            .toConstructor(Generic.class.getDeclaredConstructor());
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        assertNotNull(injector.getInstance(TypeLiteral.get(Generic.class, Object.class)));
    }

    @Test
    public void testBindingClassToExplicitNoArgContructor() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(WithExplicitConstructor.class)
                            .toConstructor(WithExplicitConstructor.class.getDeclaredConstructor());
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        assertNotNull(injector.getInstance(WithExplicitConstructor.class));
    }

    @Test
    public void testBindingClassToInjectAnnotatedNoArgContructor() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(WithInjectAnnotatedConstructor.class)
                            .toConstructor(
                                    WithInjectAnnotatedConstructor.class.getDeclaredConstructor());
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        assertNotNull(injector.getInstance(WithInjectAnnotatedConstructor.class));
    }

    @Test
    public void testBindingClassWithMultipleContructorsToOneOfThem() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(WithMultipleConstructors.class)
                            .toConstructor(WithMultipleConstructors.class.getDeclaredConstructor());
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        assertNotNull(injector.getInstance(WithMultipleConstructors.class));
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingAbstractClassToItsConstructor() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(Abstract.class)
                            .toConstructor(Abstract.class.getDeclaredConstructor());
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingInnerClassToItsConstructor() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(TestedTypes.class);
                    binder.bind(Inner.class)
                            .toConstructor(Inner.class.getDeclaredConstructor(TestedTypes.class));
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingPackageScopedClassToItsPrivateConstructor() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(PackageScopedWithPrivateConstrutor.class).toConstructor(
                            PackageScopedWithPrivateConstrutor.class.getDeclaredConstructor());
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingProtectedClassWithPrivateConstructorToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(ProtectedWithPrivateConstrutor.class).toConstructor(
                            ProtectedWithPrivateConstrutor.class.getDeclaredConstructor());
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingPublicClassWithPrivateConstructorToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(PublicWithPrivateConstrutor.class).toConstructor(
                            PublicWithPrivateConstrutor.class.getDeclaredConstructor());
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingSameTypeToConstructorTwice() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(Object.class)
                            .toConstructor(Object.class.getDeclaredConstructor());
                    binder.bind(TypeLiteral.get(Object.class));
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
