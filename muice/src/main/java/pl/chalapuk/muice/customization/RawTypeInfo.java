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

import java.lang.annotation.Annotation;
import java.util.Objects;

import static com.google.common.base.Preconditions.*;

public class RawTypeInfo<T> {
    private final ConstructorInfo<T> mConstructorInfo;
    private final Class<? extends Annotation> mScopeAnnotation;

    public RawTypeInfo(ConstructorInfo<T> constructorInfo,
            Class<? extends Annotation> scopeAnnotation) {
        mConstructorInfo = checkNotNull(constructorInfo);
        mScopeAnnotation = scopeAnnotation;
    }

    public ConstructorInfo<T> getDefaultInjectionPoint() {
        return mConstructorInfo;
    }

    public Class<? extends Annotation> getScopeAnnotation() {
        return mScopeAnnotation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mConstructorInfo, mScopeAnnotation);
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
        RawTypeInfo<T> other = (RawTypeInfo<T>) obj;
        return mConstructorInfo.equals(other.mConstructorInfo)
                && mScopeAnnotation.equals(other.mScopeAnnotation);
    }
}
