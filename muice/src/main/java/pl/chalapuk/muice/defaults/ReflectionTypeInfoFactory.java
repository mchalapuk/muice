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

package pl.chalapuk.muice.defaults;

import static com.google.common.base.Preconditions.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.inject.Qualifier;

import pl.chalapuk.muice.BindingError;
import pl.chalapuk.muice.Key;
import pl.chalapuk.muice.TypeLiteral;
import pl.chalapuk.muice.customization.ConstructorInfo;
import pl.chalapuk.muice.customization.RawTypeInfo;
import pl.chalapuk.muice.customization.TypeInfoException;
import pl.chalapuk.muice.customization.TypeInfoFactory;

public class ReflectionTypeInfoFactory implements TypeInfoFactory {

    @Override
    public <T> RawTypeInfo<T> getRawTypeInfo(Class<? super T> rawType) throws TypeInfoException {
        Constructor<T> constructor = findInjectAnnotatedConstructor(checkNotNull(rawType));
        return new RawTypeInfo<>(getConstructorInfo(constructor), findScopeAnnotation(rawType));
    }

    @Override
    public <T> ConstructorInfo<T> getConstructorInfo(final Constructor<T> constructor) {
        return new ConstructorInfo<>(constructor, analyzeParameterKeys(constructor));
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> findInjectAnnotatedConstructor(Class<? super T> type)
            throws InjectionPointException {
        Constructor<T> selectedConstructor = null;

        for (Constructor<?> constructor : type.getConstructors()) {
            if (constructor.getAnnotation(Inject.class) != null
                    || constructor.getParameterTypes().length == 0) {
                if (selectedConstructor != null) {
                    throw new InjectionPointException(type.getName() + " have more than one "
                            + Inject.class.getName() + "-annotated constructors");
                }
                selectedConstructor = (Constructor<T>) constructor;
            }
        }
        if (selectedConstructor == null) {
            try {
                selectedConstructor = (Constructor<T>) type.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new InjectionPointException(Inject.class.getName()
                        + "-annotated constructor not found in " + type.getName());
            }
        }

        selectedConstructor.setAccessible(true);
        return selectedConstructor;
    }

    // TODO cache?
    private static Class<? extends Annotation> findScopeAnnotation(Class<?> type)
            throws TypeInfoException {
        Class<? extends Annotation> found = null;
        for (Annotation annotation : type.getAnnotations()) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType.getAnnotation(javax.inject.Scope.class) != null) {
                if (found != null) {
                    throw new TypeInfoException(
                            "multiple scope annotations found on type " + type.getName());
                }
                found = annotationType;
            }
        }
        return found;
    }

    // TODO cache?
    public static Class<? extends Annotation> findQualifierAnnotation(Annotation[] anontations)
            throws TypeInfoException {
        Class<? extends Annotation> found = null;
        for (Annotation annotation : anontations) {
            if (annotation.annotationType().getAnnotation(Qualifier.class) != null) {
                if (found != null) {
                    throw new TypeInfoException("multiple qualifiers found");
                }
                found = annotation.annotationType();
            }
        }
        return found;
    }

    private static <T> TypeLiteral<T> getTypeLiteralFromUnknownType(Type type)
            throws UnsupportedTypeException {

        if (type instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<T> simpleType = (Class<T>) type;
            return TypeLiteral.get(simpleType);

        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            TypeLiteral<?>[] typeLiteralArguments = new TypeLiteral<?>[actualTypeArguments.length];
            for (int i = 0; i < actualTypeArguments.length; ++i) {
                typeLiteralArguments[i] = getTypeLiteralFromUnknownType(actualTypeArguments[i]);
            }
            return TypeLiteral.get((Class<T>) parameterizedType.getRawType(), typeLiteralArguments);
        }

        throw new UnsupportedTypeException(type.getClass().getName() + " are not supported");
    }

    private static Key<?>[] analyzeParameterKeys(Constructor<?> constructor) {
        Type[] parameterTypes = constructor.getGenericParameterTypes();
        Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
        Key<?>[] dependencies = new Key<?>[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; ++i) {
            try {
                dependencies[i] = Key.get(
                        getTypeLiteralFromUnknownType(parameterTypes[i]),
                        findQualifierAnnotation(parameterAnnotations[i])
                        );
            } catch (TypeInfoException e) {
                throw new BindingError(
                        "error processing argument " + i + " of " + constructor, e);
            }
        }
        return dependencies;
    }
}
