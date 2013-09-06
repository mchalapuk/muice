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

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.*;

/**
 * Holds information about default injection point and declared scope for a
 * class. RawTypeInfo instances are produced by implementations of
 * {@link TypeInfoFactory}.
 * <p>
 * Information held in RawTypeInfo objects typically comes from runtime
 * reflection analysis, however there is a possibility of implementing custom
 * {@link TypeInfoFactory} that uses information generated at compile time.
 * 
 * @param <T> type represented by raw type info
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class RawTypeInfo<T> {
    private final ConstructorInfo<T> mConstructorInfo;
    private final Class<? extends Annotation> mScopeAnnotation;

    public RawTypeInfo(ConstructorInfo<T> constructorInfo,
            @Nullable Class<? extends Annotation> scopeAnnotation) {
        mConstructorInfo = checkNotNull(constructorInfo);
        mScopeAnnotation = scopeAnnotation;
    }

    /**
     * @return constructor used to instantiate object in cases when type is
     *         bound to itself
     */
    public ConstructorInfo<T> getDefaultInjectionPoint() {
        return mConstructorInfo;
    }

    /**
     * @return scope annotation found on class declaration
     */
    @Nullable
    public Class<? extends Annotation> getScopeAnnotation() {
        return mScopeAnnotation;
    }
}
