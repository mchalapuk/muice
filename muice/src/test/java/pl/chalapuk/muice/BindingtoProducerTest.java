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
import org.mockito.ArgumentCaptor;

import pl.chalapuk.muice.TestedTypes.Generic;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class BindingtoProducerTest {

    @Test
    public void testBindingObjectClassToProducer() {
        final Object instance = new Object();

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProducer(new Producer<Object>() {

                    @Override
                    public Object newInstance(Injector unused) {
                        return instance;
                    }
                });
            }
        });

        assertSame(instance, injector.getInstance(Object.class));
    }

    @Test
    public void testNotNullInjectorIsPassedToProducer() {
        final Producer<Object> mockProducer = mock(Producer.class);

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProducer(mockProducer);
            }
        });

        injector.getInstance(Object.class);

        verify(mockProducer).newInstance(notNull(Injector.class));
    }

    @Test
    public void testInjectorPassedToProducerContainTheSameBindingAsCreatedInjector() {
        final Producer<Object> mockProducer = mock(Producer.class);

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProducer(mockProducer);
            }
        });

        injector.getInstance(Object.class);

        ArgumentCaptor<Injector> captor = ArgumentCaptor.forClass(Injector.class);
        verify(mockProducer).newInstance(captor.capture());
        assertEquals(injector.getBindings(), captor.getValue().getBindings());
    }

    @Test
    public void testInjectionErrorWhenProviderGetThrowsRuntimeException() {

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class)
                        .toProducer(new Producer<Object>() {

                            @Override
                            public Object newInstance(Injector unused) {
                                throw new RuntimeException();
                            }
                        });
            }
        });

        try {
            injector.getInstance(Object.class);
            fail("expected "+ InjectionError.class);
        } catch (InjectionError e) {
            assertEquals(RuntimeException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testInjectionErrorWhenObjectOfIncompatibleTypeProduced() {

        Injector injector = Muice.createInjector(new BindingModule() {
            @SuppressWarnings({
                    "unchecked", "cast", "rawtypes", "hiding"
            })
            @Override
            public void configure(Binder binder) {
                binder.bind(Generic.class)
                        .toProducer((Producer<Generic<?>>) (Producer) new Producer<Object>() {

                            @Override
                            public Object newInstance(Injector injector) {
                                return new Object();
                            }
                        });
            }
        });

        try {
            injector.getInstance(Generic.class);
            fail("expected "+ InjectionError.class);
        } catch (InjectionError e) {
            assertEquals(ClassCastException.class, e.getCause().getClass());
        }
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenBindingToNullProducer() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProducer(null);
            }
        });
    }
}
