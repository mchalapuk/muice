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

import javax.inject.Singleton;

import pl.chalapuk.muice.Injector;
import pl.chalapuk.muice.Key;
import pl.chalapuk.muice.Provider;
import pl.chalapuk.muice.Scope;

import com.google.common.base.Function;

/**
 * Contains scopes supported in Muice by default.
 * 
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public enum Scopes implements Scope {

    /**
     * Used when no scope is provided for a binding.
     */
    NONE {
        @Override
        public <T> Provider<? extends T> decorate(Key<T> key, Provider<? extends T> unscoped) {
            return unscoped;
        }
    },

    /**
     * Bound by default to {@link Singleton} annotation.
     */
    SINGLETON {
        @Override
        public <T> Provider<? extends T> decorate(Key<T> key, final Provider<? extends T> unscoped) {

            return new Provider<T>() {
                private Function<Provider<? extends T>, T> mGetter = new Function<Provider<? extends T>, T>() {

                    @Override
                    public T apply(Provider<? extends T> p) {
                        final T val = p.get();
                        mGetter = new Function<Provider<? extends T>, T>() {

                            @Override
                            public T apply(Provider<? extends T> arg0) {
                                return val;
                            }
                        };
                        return val;
                    }
                };

                @Override
                public T get() {
                    return mGetter.apply(unscoped);
                }

                @Override
                public void initialize(Injector injector) {
                    unscoped.initialize(injector);
                }
            };
        }
    }
}
