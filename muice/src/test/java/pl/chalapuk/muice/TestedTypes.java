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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Scope;
import javax.inject.Singleton;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
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

    static class WithThrowingConstructor {
        public WithThrowingConstructor() throws Exception {
            throw new Exception();
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

    public static class ObjectProvider implements javax.inject.Provider<Object> {

        @Override
        public Object get() {
            return new Object();
        }
    }

    static class ThrowingFromGetProvider implements javax.inject.Provider<Object> {

        @Override
        public Object get() {
            throw new RuntimeException();
        }
    }

    static class ThrowingFromConstructorProvider implements javax.inject.Provider<Object> {

        public ThrowingFromConstructorProvider() {
            throw new RuntimeException();
        }

        @Override
        public Object get() {
            return null;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Scope
    public @interface ScopeAnnotationA {
        // empty
    }

    @Singleton
    static class SingletonScoped {
        // empty
    }

    public static class WithObjectDependency {
        public final Object mInjected;

        @Inject
        public WithObjectDependency(Object object) {
            mInjected = object;
        }
    }

    public static class WithGenericDependency {
        public final Generic<Object> mInjected;

        @Inject
        public WithGenericDependency(Generic<Object> object) {
            mInjected = object;
        }
    }

    public static class WithQualifiedDependency {
        public final Object mInjected;

        @Inject
        public WithQualifiedDependency(@QualifierAnnotationA Object object) {
            mInjected = object;
        }
    }

    public static class WithTwoDependencies {
        public final Object mInjectedObject;
        public final Interface mInjectedInterface;

        @Inject
        public WithTwoDependencies(Object object, Interface inter) {
            mInjectedObject = object;
            mInjectedInterface = inter;
        }
    }

    public static class WithTwoGenericDependencies {
        public final Generic<Interface> mInjectedInterface;
        public final Generic<Object> mInjectedObject;

        @Inject
        public WithTwoGenericDependencies(Generic<Interface> withInterface,
                Generic<Object> withObject) {
            mInjectedInterface = withInterface;
            mInjectedObject = withObject;
        }
    }

    public static class WithTwoQualifiedDependencies {
        public final Object mInjectedQualifiedA;
        public final Object mInjectedQualifiedB;

        @Inject
        public WithTwoQualifiedDependencies(
                @QualifierAnnotationA Object qualifiedA,
                @QualifierAnnotationB Object qualifiedB) {
            mInjectedQualifiedA = qualifiedA;
            mInjectedQualifiedB = qualifiedB;
        }
    }

    @SuppressWarnings("rawtypes")
    public static class WithThreeDependencies {
        public final Generic<Object> mInjectedWithParam;
        public final Generic<?> mInjectedWithoutParam;
        public final Generic<?> mInjectedWithQualifier;

        @Inject
        public WithThreeDependencies(
                Generic<Object> withParam,
                Generic withoutParam,
                @QualifierAnnotationA Generic withQualifier) {
            this.mInjectedWithParam = withParam;
            this.mInjectedWithoutParam = withoutParam;
            this.mInjectedWithQualifier = withQualifier;
        }
    }
}
