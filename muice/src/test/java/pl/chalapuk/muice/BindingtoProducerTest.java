
package pl.chalapuk.muice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import pl.chalapuk.muice.TestedTypes.Generic;

public class BindingtoProducerTest {

    @Test
    public void testBindingObjectClassToProducer() {
        Object instance = new Object();

        final Producer<Object> mockProducer = mock(Producer.class);
        when(mockProducer.newInstance((Injector) any())).thenReturn(instance);

        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class).toProducer(mockProducer);
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

    @Test(expected = InjectionError.class)
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

        injector.getInstance(Generic.class);
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
