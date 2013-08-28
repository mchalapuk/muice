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

import java.lang.annotation.Annotation;

import static com.google.common.base.Preconditions.*;

import pl.chalapuk.muice.Binder;
import pl.chalapuk.muice.Binding;
import pl.chalapuk.muice.BindingError;
import pl.chalapuk.muice.BindingModule;
import pl.chalapuk.muice.Key;
import pl.chalapuk.muice.Scope;
import pl.chalapuk.muice.TypeLiteral;
import pl.chalapuk.muice.customization.BindingCollector;
import pl.chalapuk.muice.customization.ProducerFactory;
import pl.chalapuk.muice.customization.TypeInfoFactory;

public class BinderImpl implements Binder {
    private final BindingCollector mCollector;
    private final TypeInfoFactory mTypeInfoFactory;
    private final ProducerFactory mProducerFactory;
    private final ScopeMapping mScopeMapping;
    private final Scope mDefaultScope;

    private BindingBuilder<?> mCurrentBuilder;

    public BinderImpl(BindingCollector collector,
            TypeInfoFactory typeInfoFactory,
            ProducerFactory producerFactory,
            ScopeMapping scopeMapping,
            Scope defaultScope) {
        mCollector = checkNotNull(collector, "collector");
        mTypeInfoFactory = checkNotNull(typeInfoFactory, "typeInfoFactory");
        mProducerFactory = checkNotNull(producerFactory, "producerFactory");
        mScopeMapping = checkNotNull(scopeMapping, "scopeMapping");
        mDefaultScope = checkNotNull(defaultScope, "defaultScope");
    }

    @Override
    public <T> AnnotatingBuilder<T> bind(Class<T> type) {
        return bind(TypeLiteral.get(type));
    }

    @Override
    public <T> AnnotatingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
        return createBinding(Key.get(typeLiteral));
    }

    @Override
    public <T> LinkingBuilder<T> bind(Key<T> key) {
        return createBinding(checkNotNull(key));
    }

    @Override
    public void bindScope(Class<? extends Annotation> scopeAnnotation, Scope scope) {
        checkBindingCondition(
                mScopeMapping.get(checkNotNull(scopeAnnotation, "scopeAnnotation")) == null,
                "%s already bound to scope", scopeAnnotation.getName());
        checkBindingCondition(
                scopeAnnotation.getAnnotation(javax.inject.Scope.class) != null,
                "%s is not a valid scope annotation, annotate is with %s if you want to use it as such",
                scopeAnnotation.getName(), javax.inject.Scope.class.getName());

        mScopeMapping.mapScopeAnnotation(scopeAnnotation, checkNotNull(scope, "scope"));
    }

    @Override
    public void install(BindingModule module) {
        module.configure(this);
    }

    public void finishBuilding() {
        if (mCurrentBuilder != null) {
            Binding<?> binding = mCurrentBuilder.build();
            checkBindingCondition(mCollector.get(binding.getKey()) == null,
                    "binding for %s defined twice", binding.getKey());

            mCollector.add(binding);
            mCurrentBuilder = null;
        }
    }

    private <T> BindingBuilder<T> createBinding(Key<T> key) {
        finishBuilding();

        BindingBuilder<T> builder = new BindingBuilder<>(key,
                mCollector, mTypeInfoFactory, mScopeMapping, mDefaultScope, mProducerFactory);
        mCurrentBuilder = builder;
        return builder;
    }

    private static void checkBindingCondition(
            boolean conditionSatisfied, String messageTemplate, Object... args) {
        if (!conditionSatisfied) {
            throw new BindingError(String.format(messageTemplate, args));
        }
    }
}
