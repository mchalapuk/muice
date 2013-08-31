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

import pl.chalapuk.muice.BindingError;
import pl.chalapuk.muice.BindingModule;
import pl.chalapuk.muice.Injector;
import pl.chalapuk.muice.Muice;
import pl.chalapuk.muice.internal.InjectorBuilderImpl;

/**
 * Builds one instance of {@link Injector}. Library client uses this interface
 * to create injectors when using custom Muice instance.
 * <p>
 * Usage of the builder is divided in two stages:
 * <ol>
 * <li>processing binding modules (by calling {@link #withModules()} methods)
 * <li>build finalization (by calling {@link #build()} method)
 * </ol>
 * 
 * @see Muice#newInjector()
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public interface InjectorBuilder {

    /**
     * Processes given binding modules.
     * <p>
     * Passed modules will be processed in the same order as passed.
     * 
     * @param modules binding modules to be processed
     * @return {@code this}
     * @throws IllegalStateException if build was already finalized
     * @throws BindingError if binding configuration in passed modules contain
     *             binding error
     */
    InjectorBuilderImpl withModules(BindingModule... modules)
            throws IllegalStateException, BindingError;

    /**
     * Processes given binding modules.
     * <p>
     * Passed modules will be processed in the same order they are occur.
     * 
     * @param modules binding modules to be processed
     * @return {@code this}
     * @throws IllegalStateException if build was already finalized
     * @throws BindingError if binding configuration in passed modules contain
     */
    InjectorBuilderImpl withModules(Iterable<BindingModule> modules)
            throws IllegalStateException, BindingError;

    /**
     * Finalizes building and returns created injector.
     * 
     * @return new inejctor instance configured with bindings previously passed
     *         to {@link #withModules()} methods.
     * @throws IllegalStateException if build was already finalized
     */
    Injector build()
            throws IllegalStateException;
}
