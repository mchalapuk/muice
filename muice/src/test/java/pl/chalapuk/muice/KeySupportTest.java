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

import javax.inject.Singleton;

import org.junit.Test;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class KeySupportTest {

    @Test
    public void testClassTypeLiteralAndKeyRefersToTheSameBinding() {
        final Object instance = new Object();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toInstance(instance);
            }
        });

        assertSame(instance, injector.getInstance(Object.class));
        assertSame(instance, injector.getInstance(TypeLiteral.get(Object.class)));
        assertSame(instance, injector.getInstance(Key.get(Object.class)));
    }

    @Test
    public void testDistinctionBetweenBindingWithAnnotationAndWithout() {
        final Object with = new Object();
        final Object without = new Object();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationA.class)
                        .toInstance(with);
                binder.bind(Object.class)
                        .toInstance(without);
            }
        });

        assertSame(with,
                injector.getInstance(Key.get(Object.class, QualifierAnnotationA.class)));
        assertSame(without,
                injector.getInstance(Object.class));
    }

    @Test
    public void testDistinctionBetweenBindingWithTwoDifferentBindingAnnotations() {
        final Object annotationA = new Object();
        final Object annotationB = new Object();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationA.class)
                        .toInstance(annotationA);
                binder.bind(Object.class)
                        .annotatedWith(QualifierAnnotationB.class)
                        .toInstance(annotationB);
            }
        });

        assertSame(annotationA,
                injector.getInstance(Key.get(Object.class, QualifierAnnotationA.class)));
        assertSame(annotationB,
                injector.getInstance(Key.get(Object.class, QualifierAnnotationB.class)));
    }

    @Test
    public void testDistinctionBetweenLiteralWithTypeParamenterAndWithout() {
        final Generic<Object> with = new Generic<>();
        @SuppressWarnings("rawtypes")
        final Generic without = new Generic();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(TypeLiteral.get(Generic.class, Object.class))
                        .toInstance(with);
                binder.bind(Generic.class)
                        .toInstance(without);
            }
        });

        assertSame(with,
                injector.getInstance(TypeLiteral.get(Generic.class, Object.class)));
        assertSame(without,
                injector.getInstance(Generic.class));
    }

    @Test
    public void testDistinctionBetweenBindingWithTwoDifferentTypeParameters() {
        final Generic<Object> objectParam = new Generic<>();
        final Generic<Generic<Object>> genericObjectParam = new Generic<>();

        final TypeLiteral<Generic<Object>> objectTypeLiteral =
                TypeLiteral.get(Generic.class, Object.class);
        final TypeLiteral<Generic<Generic<Object>>> genericTypeLiteral =
                TypeLiteral.get(Generic.class, objectTypeLiteral);

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(objectTypeLiteral)
                        .toInstance(objectParam);
                binder.bind(genericTypeLiteral)
                        .toInstance(genericObjectParam);
            }
        });

        assertSame(objectParam, injector.getInstance(objectTypeLiteral));
        assertSame(genericObjectParam, injector.getInstance(genericTypeLiteral));
    }

    @Test
    public void testDistinctionBetweenSimpleAndBoxedBooleans() {
        final boolean simple = true;
        final Boolean boxed = false;

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(boolean.class)
                        .toInstance(simple);
                binder.bind(Boolean.class)
                        .toInstance(boxed);
            }
        });

        assertEquals(simple, injector.getInstance(boolean.class));
        assertEquals(boxed, injector.getInstance(Boolean.class));
    }

    @Test
    public void testDistinctionBetweenSimpleAndBoxedIntegers() {
        final int simple = 0;
        final Integer boxed = 1;

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(int.class)
                        .toInstance(simple);
                binder.bind(Integer.class)
                        .toInstance(boxed);
            }
        });

        assertEquals((Integer) simple, injector.getInstance(int.class));
        assertEquals(boxed, injector.getInstance(Integer.class));
    }

    @Test
    public void testDistinctionBetweenSimpleAndBoxedLongs() {
        final long simple = 0;
        final Long boxed = 1l;

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(long.class)
                        .toInstance(simple);
                binder.bind(Long.class)
                        .toInstance(boxed);
            }
        });

        assertEquals((Long) simple, injector.getInstance(long.class));
        assertEquals(boxed, injector.getInstance(Long.class));
    }

    @Test
    public void testDistinctionBetweenSimpleAndBoxedFloats() {
        final float simple = 0;
        final Float boxed = 1f;

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(float.class)
                        .toInstance(simple);
                binder.bind(Float.class)
                        .toInstance(boxed);
            }
        });

        assertEquals((Float) simple, injector.getInstance(float.class));
        assertEquals(boxed, injector.getInstance(Float.class));
    }

    @Test
    public void testDistinctionBetweenSimpleAndBoxedDoubles() {
        final double simple = 0;
        final Double boxed = 1d;

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(double.class)
                        .toInstance(simple);
                binder.bind(Double.class)
                        .toInstance(boxed);
            }
        });

        assertEquals((Double) simple, injector.getInstance(double.class));
        assertEquals(boxed, injector.getInstance(Double.class));
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingTypeLiteralRepresentingAlreadyBoundClass() {
        final Object instance = new Object();

        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toInstance(instance);
                binder.bind(TypeLiteral.get(Object.class))
                        .toInstance(instance);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingTypeLiteralRepresentingAlreadyBoundKey() {
        final Object instance = new Object();

        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Key.get(Object.class))
                        .toInstance(instance);
                binder.bind(TypeLiteral.get(Object.class))
                        .toInstance(instance);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingKeyRepresentingAlreadyBoundClass() {
        final Object instance = new Object();

        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toInstance(instance);
                binder.bind(Key.get(Object.class))
                        .toInstance(instance);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingClassRepresentingAlreadyBoundTypeLiteral() {
        final Object instance = new Object();

        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(TypeLiteral.get(Object.class))
                        .toInstance(instance);
                binder.bind(Object.class)
                        .toInstance(instance);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingClassRepresentingAlreadyBoundKey() {
        final Object instance = new Object();

        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Key.get(Object.class))
                        .toInstance(instance);
                binder.bind(Object.class)
                        .toInstance(instance);
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentWhenCreatingKeyFromNotQualifierAnnotation() {
        Key.get(Object.class, Singleton.class);
    }
}
