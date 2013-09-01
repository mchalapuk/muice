package pl.chalapuk.muice;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;
import javax.inject.Qualifier;

public class TestedTypes {

    interface Interface {
        // marker
    }

    static abstract class Abstract {
        // empty
    }

    class Inner {
        // empty
    }

    static class WithDefaultConstructor {
        // empty
    }

    static class Generic<T> {
        // empty
    }

    static class WithExplicitConstructor {
        public WithExplicitConstructor() {
            // empty
        }
    }

    static class WithInjectAnnotatedConstructor {
        @Inject
        public WithInjectAnnotatedConstructor() {
            // empty
        }
    }

    static class WithMultipleConstructors {
        @Inject
        public WithMultipleConstructors() {
            // empty
        }

        @SuppressWarnings("unused")
        public WithMultipleConstructors(String unused) {
            // empty
        }
    }

    static class WithMultipleInjectAnnotatedConstructors {
        @Inject
        public WithMultipleInjectAnnotatedConstructors() {
            // empty
        }

        @Inject
        @SuppressWarnings("unused")
        public WithMultipleInjectAnnotatedConstructors(String unused) {
            // empty
        }
    }

    static class WithNotInjectAnnotatedConstructor {
        @SuppressWarnings("unused")
        public WithNotInjectAnnotatedConstructor(String unused) {
            // empty
        }
    }

    static class PackageScopedWithPrivateConstrutor {
        private PackageScopedWithPrivateConstrutor() {
            // empty
        }
    }

    protected static class ProtectedWithPrivateConstrutor {
        private ProtectedWithPrivateConstrutor() {
            // empty
        }
    }

    public static class PublicWithPrivateConstrutor {
        private PublicWithPrivateConstrutor() {
            // empty
        }
    }

    public static class PublicWithPrivateInjectAnnotatedConstrutor {
        @Inject
        private PublicWithPrivateInjectAnnotatedConstrutor() {
            // empty
        }
    }
    
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface QualifierAnnotationA {
        // marker
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface QualifierAnnotationB {
        // marker
    }
}
