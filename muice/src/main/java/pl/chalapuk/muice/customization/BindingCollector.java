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

import pl.chalapuk.muice.Binding;
import pl.chalapuk.muice.BindingError;
import pl.chalapuk.muice.Key;

/**
 * Used by Muice to collect bindings during injector creation process.
 * <p>
 * Instance of binding collector is created for each injector. As bindings are
 * configured they are added to the collector. After all binding modules for the
 * injector are processed, collected bindings are fetched from binding collector
 * with a call to {@link #getBindings()}.
 * 
 * @see BindingCollectorFactory
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public interface BindingCollector {

    /**
     * Called after each successfully finished binding configuration.
     * <p>
     * Given binding should be stored by collector and returned in future calls
     * to {@link #get()} and {@link #getBindings()} methods.
     * 
     * @param binding binding to be added to collector
     * @throws BindingError if binding with the same key has been already
     *             configured
     */
    void add(Binding<?> binding) throws BindingError;

    /**
     * Called when binding for given key is to be used as binding target.
     * <p>
     * Whether returned binding should be already configured or not at point of
     * this method call is implementation-defined. Returned binding may be
     * incomplete, it will not be used before full injector initialization.
     * Returning <code>null</code> causes BindingError to be thrown.
     * 
     * @param key key for which binding is to be returned
     * @return binding for given key
     */
    <T> Binding<T> get(Key<T> key);

    /**
     * Called before creating creating producer for each bound constructor or
     * class bound to itself.
     * <p>
     * <b>NOTE:</b> Implementation of this method may check preconditions
     * immediately or just store passed ConstructorInfo to check preconditions
     * for all constructors in implementation of {@link #getBindings()} method.
     * 
     * @param info constructor info that will be used to construct producer
     * @throws BindingError if some precondition is not met
     */
    void checkProducerPreconditions(ConstructorInfo<?> info) throws BindingError;

    /**
     * Called after all binding configuration for the injector is processed.
     * <p>
     * All returned bindings must be complete at this point. If configuration is
     * incomplete method implementation should throw BindingError.
     * <p>
     * After call to this method binding collector will be disposed - no other
     * calls will be made.
     * 
     * @return all configured bindings
     * @throws BindingError if binding configuration is incomplete
     */
    Iterable<Binding<?>> getBindings() throws BindingError;
}
