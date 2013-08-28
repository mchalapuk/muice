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
import java.util.Arrays;

import com.google.common.base.Objects;

import pl.chalapuk.muice.Key;

public class ConstructorInfo<T> {
    private final Constructor<? super T> mConstructor;
    private final Key<?>[] mParameterKeys;

    public ConstructorInfo(Constructor<? super T> constructor, Key<?>[] parameterKeys) {
        mConstructor = checkNotNull(constructor);
        mParameterKeys = checkNotNull(parameterKeys);
    }

    public Constructor<? super T> getConstructor() {
        return mConstructor;
    }

    public Key<?>[] getConstructorParameterKeys() {
        return mParameterKeys;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mConstructor, Arrays.hashCode(mParameterKeys));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        @SuppressWarnings("unchecked")
        ConstructorInfo<T> other = (ConstructorInfo<T>) obj;
        return mConstructor.equals(other.mConstructor)
                && Arrays.equals(mParameterKeys, other.mParameterKeys);
    }
}
