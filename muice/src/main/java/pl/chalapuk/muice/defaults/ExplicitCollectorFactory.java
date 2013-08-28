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

import java.util.Iterator;
import java.util.Map;

import pl.chalapuk.muice.Binding;
import pl.chalapuk.muice.Key;
import pl.chalapuk.muice.customization.BindingCollector;
import pl.chalapuk.muice.customization.BindingCollectorFactory;

import com.google.common.collect.Maps;

public class ExplicitCollectorFactory implements BindingCollectorFactory {

    @Override
    public BindingCollector createCollector() {
        return new BindingCollector() {
            private final Map<Key<?>, Binding<?>> mBindings = Maps.newHashMap();

            @Override
            public void add(Binding<?> binding) {
                mBindings.put(binding.getKey(), binding);
            }

            @Override
            public <T> Binding<T> get(Key<T> key) {
                return (Binding<T>) mBindings.get(key);
            }

            @Override
            public Iterator<Binding<?>> iterator() {
                return mBindings.values().iterator();
            }
        };
    }
}
