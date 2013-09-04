
package pl.chalapuk.muice;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.chalapuk.muice.TestedTypes.*;
import pl.chalapuk.muice.internal.Scopes;

public class BindingScopesTest {

    @Test
    public void testBindingScopeAnnotationToScopeInstance() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bindScope(ScopeAnnotationA.class, Scopes.SINGLETON);
                binder.bind(Object.class)
                        .in(ScopeAnnotationA.class);
            }
        });

        assertSame(injector.getInstance(Object.class),
                injector.getInstance(Object.class));
    }

    @Test(expected = BindingError.class)
    public void testBindingErrorWhenBindingSameBindingAnnotationsTwice() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bindScope(ScopeAnnotationA.class, Scopes.SINGLETON);
                binder.bindScope(ScopeAnnotationA.class, Scopes.NONE);
            }
        });
    }

    @Test(expected = BindingError.class)
    public void testIllegalArgumentWhenBindingSameBindingNotScopeAnnotation() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bindScope(QualifierAnnotationA.class, Scopes.SINGLETON);
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenBindingNullScopeAnnotation() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bindScope(null, Scopes.SINGLETON);
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenBindingNullScopeInstance() {
        Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bindScope(ScopeAnnotationA.class, null);
            }
        });
    }
}
