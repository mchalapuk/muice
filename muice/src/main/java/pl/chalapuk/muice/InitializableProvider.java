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
import javax.inject.Provider;

/**
 * An object capable of providing instances of type {@code T} used as a primary
 * abstraction to represent binding target in Muice injectors.
 * <p>
 * Besides injector internals Providers are used in following ways:
 * <ul>
 * <li>Classes implementing {@code Provider<T>} may be used as binding target
 * for {@code Key<T>}. Provider implementations must be bound to themselves
 * before using them as a target just like usual types. Custom provider types
 * may use all features of Muice (e.g. {@literal @}{@link Inject}-annotated
 * constructor and scopes). By default, new instance of {@code Provider<T>} will
 * be created for each injected {@code T} instance. Use singleton scope when
 * binding provider type to itself to alter this behavior.
 * <li>{@code Provider<T>} instances may be used as a binding target for
 * {@code Key<T>}.
 * <li>{@code InitializableProvider<T>} instances may be used as a binding
 * target for {@code Key<T>}. Such binding configuration results in
 * {@link #initialize(Injector)} method call at the end of injector
 * initialization procedure.
 * <li>An implementation class may always choose to have a {@code Provider<T>}
 * instance injected, rather than having a {@code T} injected directly. This may
 * give you access to multiple instances of {@code T} (by calling
 * {@link Provider#get()} many times), instances you wish to safely mutate and
 * discard, or instances that will be initialized lazily.
 * <li>A custom {@link Scope} is implemented as a decorator of
 * {@code Provider<T>}, which decides when to delegate to the backing provider
 * and when to provide the instance some other way.
 * <li>The {@link Injector} offers access to the {@code Provider<T>} it uses to
 * fulfill requests for a given key, via the {@link Injector#getProvider}
 * methods.
 * </ul>
 * <p>
 * Muice doesn't support method injection, but this interface extends provider
 * with {@link #initialize(Injector)} method which work just as method
 * injection.
 * <p>
 * Each InitializableProvider instance can be used as a binding target within
 * only one injector. Using the same instance with multiple injectors will
 * result in multiple calls to {@link #initialize(Injector)} method and possible
 * usage of improper injector in implementation of {@link #get()} method.
 * 
 * @param <T> type of provided objects
 * @author crazybob@google.com (Bob Lee)
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public interface InitializableProvider<T> extends Provider<T> {

    /**
     * Invoked by Muice before first call to {@link #get()} method.
     * 
     * @param injector injector to be used by the provider
     */
    void initialize(Injector injector);

    /**
     * Provides an instance of {@code T}.
     * 
     * @throws InjectionError if an instance cannot be provided. Such exceptions
     *             include messages and throwables to describe why provision
     *             failed.
     */
    @Override
    T get();
}
