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

import static com.google.common.base.Preconditions.*;

import java.lang.reflect.Constructor;
import java.util.Map;

import pl.chalapuk.muice.customization.ConstructorInfo;
import pl.chalapuk.muice.customization.RawTypeInfo;
import pl.chalapuk.muice.customization.TypeInfoException;
import pl.chalapuk.muice.customization.TypeInfoFactory;

import com.google.common.collect.Maps;

/**
 * Wrapper for {@link TypeInfoFactory} that implements caching of all queried
 * information.
 * 
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class CachedTypeInfoFactory implements TypeInfoFactory {
    private final Map<Class<?>, RawTypeInfo<?>> mRawTypeCache = Maps.newHashMap();
    private final Map<Constructor<?>, ConstructorInfo<?>> mConstructorCache = Maps.newHashMap();

    private final TypeInfoFactory mSource;

    public CachedTypeInfoFactory(TypeInfoFactory source) {
        mSource = checkNotNull(source);
    }

    @Override
    public <T> RawTypeInfo<T> getRawTypeInfo(Class<? super T> rawType) throws TypeInfoException {
        RawTypeInfo<T> info = (RawTypeInfo<T>) mRawTypeCache.get(rawType);
        if (info == null) {
            info = mSource.getRawTypeInfo(rawType);
        }
        return info;
    }

    @Override
    public <T> ConstructorInfo<T> getConstructorInfo(Constructor<T> constructor) {
        ConstructorInfo<T> info = (ConstructorInfo<T>) mConstructorCache.get(constructor);
        if (info == null) {
            info = mSource.getConstructorInfo(constructor);
        }
        return info;
    }
}
