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

import static org.junit.Assert.*;

import javax.inject.Singleton;

import org.junit.Test;

import com.google.common.collect.Iterables;

import pl.chalapuk.muice.internal.Scopes;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class DefaultImplementationTest {

    @Test
    public void testCreatingInjectorWithoutBindings() {
        assertNotNull("null injector created", Muice.createInjector());
    }

    @Test
    public void testCreatingInjectorWithoutBindingsWithDefaultMuice() {
        assertNotNull("null injector created", Muice.DEFAULT.newInjector().build());
    }

    @Test
    public void testInjectorCreatedWithStaticMethodsContainsOnlyOneBinding() {
        assertEquals(1, bindingCount(Muice.createInjector()));
    }

    @Test
    public void testInjectorCreatedWithDefaultMuiceContainsOnlyOneBinding() {
        assertEquals(1, bindingCount(Muice.DEFAULT.newInjector().build()));
    }

    @Test
    public void testInjectorInterfaceIsInjectable() {
        Injector injector = Muice.createInjector();
        assertSame(injector, injector.getInstance(Injector.class));
        assertSame(injector, injector.getProvider(Injector.class).get());
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenTryingToBindSingletonAnnotationToScope() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bindScope(Singleton.class, Scopes.SINGLETON);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenTryingToBindInjectorInterface() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Injector.class)
                        .in(Singleton.class);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenFetchingProviderOfUnboundType() {
        Muice.createInjector().getProvider(Object.class);
    }

    private static int bindingCount(Injector injector) {
        return Iterables.size(injector.getBindings());
    }
}
