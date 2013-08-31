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

import pl.chalapuk.muice.BindingModule;
import pl.chalapuk.muice.Muice;
import pl.chalapuk.muice.Scope;

/**
 * Builds customized instances of Muice.
 * <p>
 * Implementation of this interface provides default instances for customizable
 * factories, boot modules and default scope. {@code Muice.newMuice().build()}
 * will create Muice instance with default configuration. Calling any
 * {@code with...} method is optional.
 * 
 * @see Muice#newMuice()
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public interface MuiceBuilder {

    /**
     * @see Muice
     */
    MuiceBuilder withTypeInfoFactory(TypeInfoFactory factory);

    /**
     * @see Muice
     */
    MuiceBuilder withBindingCollectorFactory(BindingCollectorFactory factory);

    /**
     * @see Muice
     */
    MuiceBuilder withProducerFactory(ProducerFactory factory);

    /**
     * @see Muice
     */
    MuiceBuilder withBootModules(BindingModule... modules);

    /**
     * @see Muice
     */
    MuiceBuilder withoutBootModules();

    /**
     * @see Muice
     */
    MuiceBuilder withDefaultScope(Scope scope);

    /**
     * @see Muice
     */
    Muice build();
}
