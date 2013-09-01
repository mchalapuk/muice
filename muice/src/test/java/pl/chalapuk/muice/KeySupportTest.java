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
}
