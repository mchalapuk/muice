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

import java.util.Arrays;
import java.util.Map;

import javax.inject.Provider;

import com.google.common.collect.Maps;

import pl.chalapuk.muice.Binding;
import pl.chalapuk.muice.BindingError;
import pl.chalapuk.muice.BindingModule;
import pl.chalapuk.muice.Injector;
import pl.chalapuk.muice.Key;
import pl.chalapuk.muice.TypeLiteral;
import pl.chalapuk.muice.customization.BindingCollector;
import pl.chalapuk.muice.customization.InjectorBuilder;

public class InjectorBuilderImpl implements InjectorBuilder {
    private final BindingCollector mCollector;
    private final BinderImpl mBinder;

    public InjectorBuilderImpl(BindingCollector collector, BinderImpl binder) {
        mCollector = collector;
        mBinder = binder;
    }

    @Override
    public InjectorBuilderImpl withModules(BindingModule... modules) {
        return withModules(Arrays.asList(modules));
    }

    @Override
    public InjectorBuilderImpl withModules(Iterable<BindingModule> modules) {
        for (BindingModule module : modules) {
            module.configure(mBinder);
        }
        return this;
    }

    @Override
    public Injector build() {
        final Map<Key<?>, Provider<?>> scoped = Maps.newHashMap();

        Injector injector = new Injector() {

            @Override
            public <T> T getInstance(Key<T> key) {
                if (key.getRawType().equals(Provider.class)) {
                    TypeLiteral<?> providedType = key.getTypeLiteral().getTypeArgument(0);
                    @SuppressWarnings("unchecked")
                    T provider = (T) getProvider(Key.get(providedType, key.getQualifier()));
                    return provider;
                }
                return getProvider(key).get();
            }

            @Override
            public <T> T getInstance(TypeLiteral<T> typeLiteral) {
                return getInstance(Key.get(typeLiteral));
            }

            @Override
            public <T> T getInstance(Class<T> type) {
                return getInstance(Key.get(type));
            }

            @Override
            public <T> Provider<? extends T> getProvider(Key<T> key) {
                Provider<? extends T> provider = (Provider<? extends T>) scoped.get(key);
                if (provider == null) {
                    throw new BindingError("no binding for " + key);
                }
                return provider;
            }

            @Override
            public <T> Provider<? extends T> getProvider(TypeLiteral<T> typeLiteral) {
                return getProvider(Key.get(typeLiteral));
            }

            @Override
            public <T> Provider<? extends T> getProvider(Class<T> type) {
                return getProvider(Key.get(type));
            }

            @Override
            public Iterable<Binding<?>> getBindings() {
                return mCollector;
            }
        };

        mBinder.bind(Injector.class).toInstance(injector);
        mBinder.finishBuilding();

        for (Binding<?> binding : mCollector) {
            scoped.put(binding.getKey(), applyScope(binding, injector));
        }

        return injector;
    }

    private static <T> Provider<? extends T> applyScope(
            final Binding<T> binding, final Injector injector
            ) {
        return binding.getScope().decorate(binding.getKey(), new Provider<T>() {

            @Override
            public T get() {
                return binding.getTarget().newInstance(injector);
            }
        });
    }
}