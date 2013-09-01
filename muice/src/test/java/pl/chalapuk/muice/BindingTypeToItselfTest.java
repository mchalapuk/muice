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

import javax.inject.Inject;

import org.junit.Test;

public class BindingTypeToItselfTest {

    interface Interface {
        // marker
    }

    static abstract class Abstract {
        // empty
    }

    class Inner {
        // empty
    }

    static class Generic<T> {
        // empty
    }

    static class ExplicitConstructor {
        public ExplicitConstructor() {
            // empty
        }
    }

    static class InjectAnnotated {
        @Inject
        public InjectAnnotated() {
            // empty
        }
    }

    static class MultipleConstructors {
        @Inject
        public MultipleConstructors() {
            // empty
        }

        @SuppressWarnings("unused")
        public MultipleConstructors(String unused) {
            // empty
        }
    }

    static class MultipleInjectAnnotated {
        @Inject
        public MultipleInjectAnnotated() {
            // empty
        }

        @Inject
        @SuppressWarnings("unused")
        public MultipleInjectAnnotated(String unused) {
            // empty
        }
    }

    static class NotInjectAnnotated {
        @SuppressWarnings("unused")
        public NotInjectAnnotated(String unused) {
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

        assertNotNull(injector.getInstance(TypeLiteral.get(Generic.class, Object.class)));
    }

    @Test
    public void testBindingClassWithExplicitNoArgContructorToItselt() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(ExplicitConstructor.class);
            }
        });

        assertNotNull(injector.getInstance(ExplicitConstructor.class));
    }

    @Test
    public void testBindingClassWithInjectAnnotatedNoArgContructorToItselt() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(InjectAnnotated.class);
            }
        });

        assertNotNull(injector.getInstance(InjectAnnotated.class));
    }

    @Test
    public void testBindingClassWithMultipleContructorsToItselt() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(MultipleConstructors.class);
            }
        });

        assertNotNull(injector.getInstance(MultipleConstructors.class));
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
                binder.bind(MultipleInjectAnnotated.class);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingClassWithNoAnnotatedConstructorToItself() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(NotInjectAnnotated.class);
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
}
