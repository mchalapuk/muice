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
 * Client code must implement this interface to configure binding information
 * (using {@link Binder Guice Binding EDSL}) used to create {@link Injector}.
 * <p>
 * The same module instance may be used to configure multiple injectors.
 * Dependencies between bindings located in different modules are supported.
 * 
 * @see Muice#createInjector(BindingModule...)
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public interface BindingModule {

    /**
     * Called by Muice during injector creation process.
     * <p>
     * Implementation should use given binder to configure bindings.
     * 
     * @param binder binder connected with an {@link Injector} instance.
     */
    void configure(Binder binder);
}
