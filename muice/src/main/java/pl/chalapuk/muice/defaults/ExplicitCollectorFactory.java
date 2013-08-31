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

import java.util.Map;

import pl.chalapuk.muice.Binding;
import pl.chalapuk.muice.BindingError;
import pl.chalapuk.muice.Key;
import pl.chalapuk.muice.Provider;
import pl.chalapuk.muice.TypeLiteral;
import pl.chalapuk.muice.customization.BindingCollector;
import pl.chalapuk.muice.customization.BindingCollectorFactory;
import pl.chalapuk.muice.customization.ConstructorInfo;

import com.google.common.collect.Maps;

/**
 * Creates binding collectors that checks producer preconditions immediately.
 * 
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class ExplicitCollectorFactory implements BindingCollectorFactory {

    @Override
    public BindingCollector createCollector() {
        return new BindingCollector() {
            private final Map<Key<?>, Binding<?>> mBindings = Maps.newHashMap();

            @Override
            public void add(Binding<?> binding) {
                if (mBindings.containsKey(binding.getKey())) {
                    throw new BindingError("binding for %s defined twice" + binding.getKey());
                }
                mBindings.put(binding.getKey(), binding);
            }

            @Override
            public <T> Binding<T> get(Key<T> key) {
                return (Binding<T>) mBindings.get(key);
            }

            @Override
            public void checkProducerPreconditions(ConstructorInfo<?> info) {
                final Key<?>[] paramKeys = info.getParameterKeys();
                for (int i = 0; i < paramKeys.length; ++i) {
                    Key<?> paramKey = paramKeys[i];
                    if (paramKey.getRawType().equals(Provider.class)) {
                        TypeLiteral<?> providedType = paramKey.getTypeLiteral().getTypeArgument(0);
                        paramKey = Key.get(providedType, paramKey.getQualifier());
                    }

                    if (!mBindings.containsKey(paramKey)) {
                        throw new BindingError("no binding for " + paramKey
                                + " required in argument " + i + " of " + info.getConstructor());
                    }
                }
            }

            @Override
            public Iterable<Binding<?>> getBindings() {
                return mBindings.values();
            }
        };
    }
}
