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

package pl.chalapuk.muice.internal;

import static com.google.common.base.Preconditions.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import javax.inject.Named;

import pl.chalapuk.muice.Binding;
import pl.chalapuk.muice.BindingError;
import pl.chalapuk.muice.Injector;
import pl.chalapuk.muice.Key;
import pl.chalapuk.muice.Producer;
import pl.chalapuk.muice.Provider;
import pl.chalapuk.muice.Scope;
import pl.chalapuk.muice.TypeLiteral;
import pl.chalapuk.muice.Binder.AnnotatingBuilder;
import pl.chalapuk.muice.Binder.LinkingBuilder;
import pl.chalapuk.muice.Binder.ScopingBuilder;
import pl.chalapuk.muice.customization.BindingCollector;
import pl.chalapuk.muice.customization.ConstructorInfo;
import pl.chalapuk.muice.customization.ProducerFactory;
import pl.chalapuk.muice.customization.RawTypeInfo;
import pl.chalapuk.muice.customization.TypeInfoException;
import pl.chalapuk.muice.customization.TypeInfoFactory;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class BindingBuilder<T> implements AnnotatingBuilder<T> {
    private final BindingCollector mBindingCollector;
    private final ScopeMapping mScopeMapping;

    private final Scope mDefaultScope;
    private final TypeInfoFactory mTypeInfoFactory;
    private final ProducerFactory mProducerFactory;

    private Key<T> mKey;
    private Producer<? extends T> mProducer;
    private Scope mScope;

    public BindingBuilder(Key<T> key, BindingCollector collector,
            TypeInfoFactory infoFactory, ScopeMapping scopeMapping,
            Scope defaultScope, ProducerFactory producerFactory) {
        mBindingCollector = collector;
        mScopeMapping = scopeMapping;

        mDefaultScope = defaultScope;
        mTypeInfoFactory = infoFactory;
        mProducerFactory = producerFactory;

        mKey = key;
    }

    @Override
    public LinkingBuilder<T> annotatedWith(Class<? extends Annotation> qualifier) {
        mKey = Key.get(mKey.getTypeLiteral(), qualifier);
        return this;
    }

    @Override
    public LinkingBuilder<T> annotatedWith(Named qualifier) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public ScopingBuilder to(Class<? extends T> targetType) {
        return to(Key.get(targetType));
    }

    @Override
    public ScopingBuilder to(TypeLiteral<? extends T> targetLiteral) {
        return to(Key.get(targetLiteral));
    }

    @Override
    public ScopingBuilder to(Key<? extends T> targetKey) {
        Binding<? extends T> targetBinding = mBindingCollector.get(targetKey);
        checkBindingCondition(targetBinding != null, "no binding for %s", targetKey);

        mProducer = targetBinding.getTarget();
        mScope = targetBinding.getScope();
        return this;
    }

    @Override
    public void toInstance(final T instance) {
        checkNotNull(instance, "instance");

        mProducer = new Producer<T>() {

            @Override
            public T newInstance(Injector unused) {
                return instance;
            }
        };
    }

    @Override
    public ScopingBuilder toProvider(
            final Class<? extends javax.inject.Provider<? extends T>> providerType) {
        final Key<? extends javax.inject.Provider<? extends T>> targetKey = Key.get(providerType);
        Binding<? extends javax.inject.Provider<? extends T>> targetBinding = mBindingCollector
                .get(targetKey);
        checkBindingCondition(targetBinding != null,
                "no binding for %s, please bind the provider type to itself first",
                targetKey);

        mProducer = new Producer<T>() {

            @Override
            public T newInstance(Injector injector) {
                return injector.getInstance(targetKey).get();
            }
        };
        return this;
    }

    @Override
    public ScopingBuilder toProvider(final javax.inject.Provider<? extends T> provider) {
        checkNotNull(provider, "provider");

        mProducer = new Producer<T>() {

            @Override
            public T newInstance(Injector unused) {
                return provider.get();
            }
        };
        return this;
    }

    @Override
    public ScopingBuilder toProvider(final Provider<? extends T> provider) {
        checkNotNull(provider, "provider");

        mProducer = new Producer<T>() {
            private boolean mInitialized = false; // change this to a strategy?

            @Override
            public T newInstance(Injector injector) {
                if (!mInitialized) {
                    provider.initialize(injector);
                    mInitialized = true;
                }
                return provider.get();
            }
        };
        return this;
    }

    @Override
    public ScopingBuilder toConstructor(Constructor<? extends T> constructor) {
        checkNotNull(constructor, "constructor");

        mProducer = producerFromConstructor(
                constructor.getDeclaringClass(),
                mTypeInfoFactory.getConstructorInfo(constructor)
                );
        return this;
    }

    @Override
    public ScopingBuilder toProducer(Producer<? extends T> producer) {
        mProducer = producer;
        return this;
    }

    @Override
    public void in(Class<? extends Annotation> scopeAnnotation) {
        mScope = mScopeMapping.get(checkNotNull(scopeAnnotation, "scopeAnnotation"));
        checkBindingCondition(mScope != null,
                "no scope bound to annotation %s", scopeAnnotation.getName());
    }

    @Override
    public void in(Scope scope) {
        mScope = checkNotNull(scope, "scope");
    }

    public Binding<T> build() {
        if (mProducer == null) {
            try {
                Class<? super T> rawType = mKey.getTypeLiteral().getRawType();
                RawTypeInfo<T> typeInfo = mTypeInfoFactory.getRawTypeInfo(rawType);
                mProducer = producerFromConstructor(rawType, typeInfo.getDefaultInjectionPoint());

                if (mScope == null) {
                    Class<? extends Annotation> scopeAnnotation = typeInfo.getScopeAnnotation();
                    if (scopeAnnotation != null) {
                        in(scopeAnnotation);
                    }
                }
            } catch (TypeInfoException e) {
                throw new BindingError("error while getting raw type information", e);
            }
        }

        if (mScope == null) {
            mScope = mDefaultScope;
        }

        return new Binding<T>() {

            @Override
            public Key<T> getKey() {
                return mKey;
            }

            @Override
            public Producer<? extends T> getTarget() {
                return mProducer;
            }

            @Override
            public Scope getScope() {
                return mScope;
            }
        };
    }

    private Producer<? extends T> producerFromConstructor(
            final Class<?> rawType,
            final ConstructorInfo<? extends T> constructorInfo
            ) {
        checkBindingCondition(
                isInstantiable(rawType),
                "no binding for %s; key cannot be bound to itself because %s is not instantiable",
                mKey, rawType.getName());
        checkBindingCondition(
                !isNotStaticInnerClass(rawType),
                "%s is (not static) inner class; only STATIC inner class can be bound",
                rawType.getName());

        mBindingCollector.checkProducerPreconditions(constructorInfo);
        return mProducerFactory.createProducer(constructorInfo);
    }

    private static void checkBindingCondition(
            boolean conditionSatisfied, String messageTemplate, Object... args) {
        if (!conditionSatisfied) {
            throw new BindingError(String.format(messageTemplate, args));
        }
    }

    private static boolean isInstantiable(Class<?> type) {
        return !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
    }

    private static boolean isNotStaticInnerClass(Class<?> type) {
        return type.getEnclosingClass() != null && !Modifier.isStatic(type.getModifiers());
    }
}
