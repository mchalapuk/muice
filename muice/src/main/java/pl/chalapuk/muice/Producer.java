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

package pl.chalapuk.muice;

import javax.inject.Inject;

/**
 * An object capable of creating new instances of type {@code T}.
 * <p>
 * Used as a main abstraction representing binding target in internals of Muice.
 * Custom producers may be implemented in client code and used as binding
 * targets, which may be more convenient than using providers in some cases.
 * 
 * @param <T> type of instantiated object
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public interface Producer<T> {

    /**
     * Uses given injector to create instance of {@code T}.
     * <p>
     * Typical implementation instantiates all dependencies of {@code T} with
     * given injector and invokes {@link Inject}-annotated constructor.
     * 
     * @param injector injector to be used for injecting dependencies
     * @return new instance of {@code T}
     * @throws InjectionError if an instance cannot be provided. Such exceptions
     *             include messages and throwables to describe why injection
     *             failed.
     */
    T newInstance(Injector injector);
}
