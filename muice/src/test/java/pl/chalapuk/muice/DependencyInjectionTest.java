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

import org.junit.Test;

import pl.chalapuk.muice.TestedTypes.*;
import pl.chalapuk.muice.defaults.UnsupportedTypeException;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class DependencyInjectionTest {

    @Test
    public void testInjectingDependencyOfTypeObject() {
        final Object instance = new Object();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toInstance(instance);
                binder.bind(WithObjectDependency.class);
            }
        });

        WithObjectDependency tested = injector.getInstance(WithObjectDependency.class);
        assertSame(instance, tested.mInjected);
    }

    @Test
    public void testInjectingDependencyOfGenericType() {
        final Generic<Object> instance = new Generic<Object>() {
            // empty
        };

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(TypeLiteral.get(Generic.class, Object.class))
                        .toInstance(instance);
                binder.bind(WithGenericDependency.class);
            }
        });

        WithGenericDependency tested = injector.getInstance(WithGenericDependency.class);
        assertSame(instance, tested.mInjected);
    }

    @Test
    public void testInjectingDependencyOfTypeWithQualifier() {
        final Object instance = new Object();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationA.class)
                        .toInstance(instance);
                binder.bind(WithQualifiedDependency.class);
            }
        });

        WithQualifiedDependency tested = injector.getInstance(WithQualifiedDependency.class);
        assertSame(instance, tested.mInjected);
    }

    @Test
    public void testInjectingTwoDependenciesOfDifferentTypes() {
        final Object objectInstance = new Object();
        final Interface interfaceInstance = new Interface() {
            // empty
        };

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toInstance(objectInstance);
                binder.bind(Interface.class)
                        .toInstance(interfaceInstance);
                binder.bind(WithTwoDependencies.class);
            }
        });

        WithTwoDependencies tested = injector.getInstance(WithTwoDependencies.class);
        assertSame(objectInstance, tested.mInjectedObject);
        assertSame(interfaceInstance, tested.mInjectedInterface);
    }

    @Test
    public void testInjectingTwoDependenciesOfGenericTypeWithDifferentTypeParameter() {
        final Generic<Object> genericWithInterfaceInstance = new Generic<Object>() {
            // empty
        };
        final Generic<Object> genericWithObjectInstance = new Generic<Object>() {
            // empty
        };

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(TypeLiteral.get(Generic.class, Interface.class))
                        .toInstance(genericWithInterfaceInstance);
                binder.bind(TypeLiteral.get(Generic.class, Object.class))
                        .toInstance(genericWithObjectInstance);
                binder.bind(WithTwoGenericDependencies.class);
            }
        });

        WithTwoGenericDependencies tested = injector.getInstance(WithTwoGenericDependencies.class);
        assertSame(genericWithInterfaceInstance, tested.mInjectedInterface);
        assertSame(genericWithObjectInstance, tested.mInjectedObject);
    }

    @Test
    public void testInjectingTwoDependenciesOfSameTypeWithDifferentQualifier() {
        final Object qualifiedA = new Object();
        final Object qualifiedB = new Object();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationA.class)
                        .toInstance(qualifiedA);
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationB.class)
                        .toInstance(qualifiedB);
                binder.bind(WithTwoQualifiedDependencies.class);
            }
        });

        WithTwoQualifiedDependencies tested = injector
                .getInstance(WithTwoQualifiedDependencies.class);
        assertSame(qualifiedA, tested.mInjectedQualifiedA);
        assertSame(qualifiedB, tested.mInjectedQualifiedB);
    }

    @Test
    public void testInjectingThreeDependenciesOfGenericTypeWithTypeParamWithoutTypeParamAndWithQualifier() {
        final Generic<Object> withParam = new Generic<Object>() {
            // empty
        };
        @SuppressWarnings("rawtypes")
        final Generic withoutParam = new Generic() {
            // empty
        };
        @SuppressWarnings("rawtypes")
        final Generic withQualifier = new Generic() {
            // empty
        };

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(TypeLiteral.get(Generic.class, Object.class))
                        .toInstance(withParam);
                binder.bind(Generic.class)
                        .toInstance(withoutParam);
                binder.bind(Generic.class)
                        .annotatedWith(QualifierAnnotationA.class)
                        .toInstance(withQualifier);
                binder.bind(WithThreeDependencies.class);
            }
        });

        WithThreeDependencies tested = injector.getInstance(WithThreeDependencies.class);
        assertSame(withParam, tested.mInjectedWithParam);
        assertSame(withoutParam, tested.mInjectedWithoutParam);
        assertSame(withQualifier, tested.mInjectedWithQualifier);
    }

    @Test
    public void testInjectingDependencyWithTwoDependencies() {
        final Object qualifiedA = new Object();
        final Object qualifiedB = new Object();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationA.class)
                        .toInstance(qualifiedA);
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationB.class)
                        .toInstance(qualifiedB);
                binder.bind(WithTwoQualifiedDependencies.class);

                binder.bind(Object.class)
                        .to(WithTwoQualifiedDependencies.class);
                binder.bind(WithObjectDependency.class);
            }
        });

        WithObjectDependency tested = injector
                .getInstance(WithObjectDependency.class);
        assertEquals(WithTwoQualifiedDependencies.class, tested.mInjected.getClass());

        WithTwoQualifiedDependencies injected = (WithTwoQualifiedDependencies) tested.mInjected;

        assertSame(qualifiedA, injected.mInjectedQualifiedA);
        assertSame(qualifiedB, injected.mInjectedQualifiedB);
    }


    @Test
    public void testInjectingProviderDependency() {
        final Object instance = new Object();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toInstance(instance);
                binder.bind(WithProviderDependency.class);
            }
        });

        WithProviderDependency tested = injector.getInstance(WithProviderDependency.class);
        assertSame(instance, tested.mInjected.get());
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingTypeBeforeDependency() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(WithObjectDependency.class);
            }
        });
    }

    @Test
    public void testBindingErrorWhenBindingTypeWithDependencyContainingWildcard() {
        try {
            Muice.createInjector(new BindingModule() {

                @Override
                public void configure(Binder binder) {
                    binder.bind(WithWildcardDependency.class);
                }
            });
            fail("BindingError expected");
        } catch (BindingError e) {
            assertEquals(UnsupportedTypeException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testBindingErrorWhenBindingTypeWithDependencyContainingTypeVariable() {
        try {
            Muice.createInjector(new BindingModule() {

                @Override
                public void configure(Binder binder) {
                    binder.bind(WithTypeVariableDependency.class);
                }
            });
            fail("BindingError expected");
        } catch (BindingError e) {
            assertEquals(UnsupportedTypeException.class, e.getCause().getClass());
        }
    }
}
