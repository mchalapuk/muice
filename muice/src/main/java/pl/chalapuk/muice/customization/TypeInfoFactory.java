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

import java.lang.reflect.Constructor;

/**
 * One of Muice customization points. Used by Muice to fetch information about
 * class' default injection points and constructor dependencies.
 * <p>
 * <b>NOTE:</b> Default implementation uses run-time reflection to build
 * returned data structures, which makes it a bottleneck. It is advisable to
 * provide custom compile-time generated implementation of this interface that
 * doesn't use reflection.
 * 
 * @see MuiceBuilder#withTypeInfoFactory(TypeInfoFactory)
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public interface TypeInfoFactory {

    /**
     * Provides RawTypeInfo for given class.
     * 
     * @param rawType class for which info will be returned
     * @return info representing passed type
     * @throws TypeInfoException if given raw type is not instantiable
     */
    <T> RawTypeInfo<T> getRawTypeInfo(Class<? super T> rawType) throws TypeInfoException;

    /**
     * Provides ConstructorInfo for given constructor.
     * 
     * @param constructor constructor for which information will be created
     * @return info representing passed constructor
     */
    <T> ConstructorInfo<T> getConstructorInfo(Constructor<T> constructor);
}
