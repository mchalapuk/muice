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

package pl.chalapuk.muice.customization;

import static com.google.common.base.Preconditions.*;

import java.lang.reflect.Constructor;

import pl.chalapuk.muice.Key;

/**
 * Holds information about constructor dependencies. Instances of this class are
 * produced by implementations of {@link TypeInfoFactory}.
 * <p>
 * Information held in ConstructorInfo objects typically comes from runtime
 * reflection analysis, however there is a possibility of implementing custom
 * {@link TypeInfoFactory} that uses information generated at compile time.
 * 
 * @param <T> type instantiated by the constructor
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class ConstructorInfo<T> {
    private final Constructor<? super T> mConstructor;
    private final Key<?>[] mParameterKeys;
    private final boolean mInjectAnnotated;

    public ConstructorInfo(Constructor<? super T> constructor,
            Key<?>[] parameterKeys, boolean injectAnnotated) {
        mConstructor = checkNotNull(constructor);
        mParameterKeys = checkNotNull(parameterKeys);
        mInjectAnnotated = injectAnnotated;
    }

    /**
     * @return constructor instance that should be used to instantiate {@code T}
     */
    public Constructor<? super T> getConstructor() {
        return mConstructor;
    }

    /**
     * @return array of {@link Key keys} that represents constructor parameters
     *         (order is relevant)
     */
    public Key<?>[] getParameterKeys() {
        return mParameterKeys;
    }

    public boolean isInjectAnnotated() {
        return mInjectAnnotated;
    }
}
