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

import pl.chalapuk.muice.Producer;

/**
 * One of Muice customization points. Used by Muice to create
 * {@linkplain Producer producers} for each bound constructor and type bound to
 * itself.
 * <p>
 * <b>NOTE:</b> Default implementation creates producers that uses reflection to
 * invoke desired constructor, which makes it a bottleneck. It is advisable to
 * provide custom implementation of this interface returns compile-time
 * generated producers that doesn't use reflection.
 * 
 * @see MuiceBuilder#withProducerFactory(ProducerFactory)
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public interface ProducerFactory {

    /**
     * Creates producer for given constructor info.
     * 
     * @param info info representing constructor to be invoked
     * @return producer capable of creating {@code T}
     */
    <T> Producer<T> createProducer(ConstructorInfo<T> info);
}
