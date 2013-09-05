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

/**
 * Builds the graphs of objects that make up your application. The injector
 * tracks the dependencies for each type and uses bindings to inject them.
 * <p>
 * Contains several default bindings:
 * <ul>
 * <li>This {@link Injector} instance itself
 * <li>A {@code Provider<T>} for each binding of type {@code T}
 * </ul>
 * Injectors are created using the facade class {@link Muice}.
 * 
 * @author crazybob@google.com (Bob Lee)
 * @author jessewilson@google.com (Jesse Wilson)
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public interface Injector {

    /**
     * Returns the appropriate instance for the given injection key; equivalent
     * to {@code getProvider(key).get()}. Calling this method will result with
     * recursive instantiation of all dependencies for given key.
     * 
     * @throws BindingError if binding for passed key is not found.
     * @throws InjectionError if there was a runtime failure while providing an
     *             instance.
     */
    <T> T getInstance(Key<T> key);

    /**
     * Returns the appropriate instance for the given injection type; equivalent
     * to {@code getProvider(type).get()}. Calling this method will result with
     * recursive instantiation of all dependencies for given type.
     * 
     * @throws BindingError if binding for passed key is not found.
     * @throws InjectionError if there was a runtime failure while providing an
     *             instance.
     */
    <T> T getInstance(TypeLiteral<T> typeLiteral);

    /**
     * Returns the appropriate instance for the given injection type; equivalent
     * to {@code getProvider(type).get()}. Calling this method will result with
     * recursive instantiation of all dependencies for given type.
     * 
     * @throws BindingError if binding for passed key is not found.
     * @throws InjectionError if there was a runtime failure while providing an
     *             instance.
     */
    <T> T getInstance(Class<T> type);

    /**
     * Returns the provider used to obtain instances for the given key.
     * 
     * @throws BindingError if binding for passed key is not found.
     */
    <T> javax.inject.Provider<? extends T> getProvider(Key<T> key);

    /**
     * Returns the provider used to obtain instances for the given type.
     * 
     * @throws BindingError if binding for passed type is not found.
     */
    <T> javax.inject.Provider<? extends T> getProvider(TypeLiteral<T> typeLiteral);

    /**
     * Returns the provider used to obtain instances for the given type.
     * 
     * @throws BindingError if binding for passed type is not found.
     */
    <T> javax.inject.Provider<? extends T> getProvider(Class<T> type);
    
    /**
     * @return all bindings used by the injector
     */
    Iterable<Binding<?>> getBindings();
}
