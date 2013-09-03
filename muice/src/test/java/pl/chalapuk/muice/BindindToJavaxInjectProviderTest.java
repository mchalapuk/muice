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
import static org.mockito.Mockito.*;

import org.junit.Test;

import pl.chalapuk.muice.TestedTypes.Generic;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class BindindToJavaxInjectProviderTest {

    @Test
    public void testBindingObjectClassToProducer() {
        final Object instance = new Object();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProvider(new javax.inject.Provider<Object>() {

                    @Override
                    public Object get() {
                        return instance;
                    }
                });
            }
        });

        assertSame(instance, injector.getInstance(Object.class));
    }

    @Test
    public void testProviderGetCalledOnBoundProviderWhenCallingGetOnScopedProvider() {
        final javax.inject.Provider<Object> mockProvider = mock(javax.inject.Provider.class);

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProvider(mockProvider);
            }
        });

        injector.getProvider(Object.class).get();

        verify(mockProvider).get();
    }

    @Test(expected = InjectionError.class)
    public void testInjectionErrorWhenObjectOfIncompatibleTypeProvided() {

        Injector injector = Muice.createInjector(new BindingModule() {
            @SuppressWarnings({
                    "unchecked", "cast", "rawtypes"
            })
            @Override
            public void configure(Binder binder) {
                binder.bind(Generic.class)
                        .toProvider(
                                (javax.inject.Provider<Generic<?>>) (javax.inject.Provider) new javax.inject.Provider<Object>() {

                                    @Override
                                    public Object get() {
                                        return new Object();
                                    }
                                });
            }
        });

        injector.getInstance(Generic.class);
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenBindingToNullProvider() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProvider((javax.inject.Provider<Object>) null);
            }
        });
    }
}
