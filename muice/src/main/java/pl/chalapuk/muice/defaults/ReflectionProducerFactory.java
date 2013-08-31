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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.google.common.base.Throwables;

import pl.chalapuk.muice.Injector;
import pl.chalapuk.muice.Key;
import pl.chalapuk.muice.Producer;
import pl.chalapuk.muice.customization.ConstructorInfo;
import pl.chalapuk.muice.customization.ProducerFactory;

/**
 * Creates producers that uses reflection to invoke desired constructor.
 * 
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class ReflectionProducerFactory implements ProducerFactory {

    @Override
    public <T> Producer<T> createProducer(final ConstructorInfo<T> info) {
        final Key<?>[] paramKeys = info.getParameterKeys();

        return new Producer<T>() {

            @Override
            public T newInstance(Injector injector) {
                Object[] args = new Object[paramKeys.length];
                for (int i = 0; i < paramKeys.length; ++i) {
                    args[i] = injector.getInstance(paramKeys[i]);
                }

                Constructor<? super T> constructor = info.getConstructor();

                try {
                    return (T) constructor.newInstance(args);
                } catch (InstantiationException e) {
                    throw new RuntimeException("BUG!", e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("BUG!", e);
                } catch (InvocationTargetException e) {
                    Throwables.propagateIfPossible(e.getCause());
                    throw new RuntimeException("exception when calling constructor "
                            + constructor, e.getTargetException());
                }
            }
        };
    }
}
