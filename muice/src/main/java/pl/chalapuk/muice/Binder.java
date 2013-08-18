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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Used by {@linkplain BindingModule binding modules} to configure binding
 * information.
 * <p>
 * Muice provides this object to your modules so they may each contribute their
 * binding information to the injector.
 * <h3>The Guice Binding EDSL</h3>
 * <p>
 * Muice uses an the same <i>embedded domain-specific language</i>, or EDSL, as
 * Guice. This approach is great for overall usability, but it does come with a
 * small cost: <b>it is difficult to learn how to use the Binding EDSL by
 * reading method-level javadocs</b>. Instead, you should consult the series of
 * examples below. To save space, these examples omit the opening {@code binder}.
 * 
 * <pre>
 * bind(ServiceImpl.class);
 * </pre>
 * <p>
 * This statement binds the {@code ServiceImpl} class to "itself". It instructs
 * Muice to inspect {@code ServiceImpl} type and search for {@link Inject
 * Inject}-annotated constructors and create {@link Provider} that calls it.
 * Created provider will be used as a binding target of {@code ServiceImpl}.
 * It's a big difference from Guice, where binding type to itself is optional.
 * <p>
 * If {@code ServiceImpl} have dependencies, binding for those dependencies must
 * be configured before binding {@code ServiceImpl}. This implies that circular
 * dependencies are not supported in Muice by default, however it is still
 * possible to create circular dependencies using custom providers.
 * 
 * <pre>
 * bind(Service.class).to(ServiceImpl.class);
 * </pre>
 * <p>
 * Specifies that a request for a {@code Service} instance with no binding
 * annotations should be treated as if it were a request for a
 * {@code ServiceImpl} instance. It is imperative to bind {@code ServiceImpl} to
 * something (typically to itself) before binding {@code Service} to
 * {@code ServiceImpl}.
 * 
 * <pre>
 * bind(Service.class).toProvider(ServiceProvider.class);
 * </pre>
 * <p>
 * In this example, {@code ServiceProvider} must extend or implement
 * {@code Provider<Service>}. This binding specifies that Muice should resolve
 * an unannotated injection request for {@code Service} by first resolving an
 * instance of {@code ServiceProvider} in the regular way, then calling
 * {@link Provider#get get()} on the resulting Provider instance to obtain the
 * {@code Service} instance. Again, for above binding specification to work,
 * binding for {@code ServiceProvider} must be specified previously.
 * <p>
 * The {@link Provider} you use here does not have to be a "factory"; that is, a
 * provider which always <i>creates</i> each instance it provides. However, this
 * is generally a good practice to follow. You can then use concept of
 * {@link Scope scopes} to decorate your provider.
 * 
 * <pre>
 * bind(Service.class).annotatedWith(Red.class).to(ServiceImpl.class);
 * </pre>
 * <p>
 * Like the previous example, but only applies to injection requests that use
 * the binding annotation {@code @Red}. Bindings for particular <i>instances</i>
 * of the {@code @Red} annotation (see below) require massive reflection usage
 * and are not supported currently in Muice.
 * 
 * <pre>
 * bind(ServiceImpl.class).in(Singleton.class);
 * // or, alternatively
 * bind(ServiceImpl.class).in(Scopes.SINGLETON);
 * </pre>
 * <p>
 * Either of these statements places the {@code ServiceImpl} class into
 * singleton scope. Muice will create only one instance of {@code ServiceImpl}
 * and will reuse it for all injection requests of this type. Note that it is
 * still possible to bind another instance of {@code ServiceImpl} if the second
 * binding is qualified by an annotation as in the previous example.
 * <p>
 * <b>Note:</b> a scope specified in this way <i>overrides</i> any scope that
 * was specified with an annotation on the {@code ServiceImpl} class.
 * <p>
 * {@link Singleton} is the only scope annotation supported by default in Muice.
 * Binding module can use their own custom scopes and {@link #bindScope bind
 * custom scopes to scope annotations}.
 * 
 * <pre>
 * bind(TypeLiteral.get(PaymentService.class, CreditCard))
 *         .to(CreditCardPaymentService.class);
 * </pre>
 * <p>
 * This admittedly odd construct is the way to bind a parameterized type. It
 * tells Muice how to honor an injection request for an element of type
 * {@code PaymentService<CreditCard>}. The class
 * {@code CreditCardPaymentService} must implement the
 * {@code PaymentService<CreditCard>} interface. Muice cannot bind or inject a
 * generic type with type variable, such as {@code Set<E>}; all type parameters
 * must be fully specified.
 * 
 * <pre>
 * bind(Service.class).toInstance(new ServiceImpl());
 * // or, alternatively
 * bind(Service.class).toInstance(SomeLegacyRegistry.getService());
 * </pre>
 * <p>
 * In this example, your module itself, <i>not Muice</i>, takes responsibility
 * for obtaining a {@code ServiceImpl} instance, then asks Muice to always use
 * this single instance to fulfill all {@code Service} injection requests. In
 * this case any injectable constructor on {@code ServiceImpl} is simply
 * ignored. Note that using this approach results in "eager loading" behavior
 * that you can't control.
 * 
 * <pre>
 * bind(Service.class)
 *         .annotatedWith(Names.named(&quot;blue&quot;))
 *         .to(BlueService.class);
 * </pre>
 * <p>
 * Differentiating by names is a common enough use case that we provided a
 * standard annotation, {@link Named @Named}. Because of Muice's library
 * support, binding by name is quite easier than in the arbitrary binding
 * annotation case we just saw. However, remember that these names will live in
 * a single flat namespace with all the other names used in your application.
 * 
 * <pre>
 * Constructor&lt;ServiceImpl&gt; loneCtor = getLoneCtorFromServiceImplViaReflection();
 * bind(ServiceImpl.class)
 *         .toConstructor(loneCtor);
 * </pre>
 * <p>
 * Above example specifies concrete constructor to be uses by Muice when
 * instantiating {@code ServiceImpl}. Constructor bound this way doesn't need
 * {@literal @}Inject annotation. It is useful for cases where you cannot modify
 * existing classes and may be simpler than using a {@link Provider}.
 * <p>
 * The above list of examples is far from exhaustive. If you can think of how
 * the concepts of one example might coexist with the concepts from another, you
 * can most likely weave the two together. If the two concepts make no sense
 * with each other, you most likely won't be able to do it. In a few cases Muice
 * will let something bogus slip by, and will then inform you of the problems at
 * runtime, as soon as you try to create your Injector.
 * <p>
 * The other methods of Binder such as {@link #bindScope}, {@link #install} are
 * not part of the Binding EDSL; you can learn how to use these in the usual
 * way, from the method documentation.
 * 
 * @author crazybob@google.com (Bob Lee)
 * @author jessewilson@google.com (Jesse Wilson)
 * @author kevinb@google.com (Kevin Bourrillion)
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public interface Binder {

    /**
     * Binds scope instance to annotation type.
     * <p>
     * After the call given scope instance will be identified by given
     * annotation type. This means that:
     * <ul>
     * <li>If annotation of given type will be found on class passed to
     * {@link LinkingBuilder#to}, given scope will be used by the binding unless
     * overridden with call to one of {@link ScopingBuilder}s methods.
     * <li>Passing given annotation to {@link ScopingBuilder#in(Class)} will
     * result in usage of given scope in configured binding.
     * </ul>
     * 
     * @param scopeAnnotation annotation that will identify given scope
     * @param scope scope instance that will be identified with given annotation
     */
    void bindScope(Class<? extends Annotation> scopeAnnotation, Scope scope);

    /**
     * Uses the given module to configure more bindings.
     * 
     * @param binding module from which bindings will be extracted
     */
    void install(BindingModule module);

    /**
     * @see Binder documentation of Guice Binding EDSL
     */
    <T> AnnotatingBuilder<T> bind(Class<T> type);

    /**
     * @see Binder documentation of Guice Binding EDSL
     */
    <T> AnnotatingBuilder<T> bind(TypeLiteral<T> typeLiteral);

    /**
     * @see Binder documentation of Guice Binding EDSL
     */
    <T> LinkingBuilder<T> bind(Key<T> key);

    /**
     * Builder capable of specifying binding annotation of binding key.
     * 
     * @param <T> type of the key binding is defined for
     * @author maciej@chalapuk.pl (Maciej Chałapuk)
     */
    interface AnnotatingBuilder<T> extends LinkingBuilder<T> {

        /**
         * @see Binder documentation of Guice Binding EDSL
         */
        LinkingBuilder<T> annotatedWith(Class<? extends Annotation> qualifier);

        /**
         * @see Binder documentation of Guice Binding EDSL
         */
        LinkingBuilder<T> annotatedWith(Named qualifier);
    }

    /**
     * Builder capable of specifying binding target.
     * 
     * @param <T> type of the key binding is defined for
     * @author maciej@chalapuk.pl (Maciej Chałapuk)
     */
    interface LinkingBuilder<T> extends ScopingBuilder {

        /**
         * @see Binder documentation of Guice Binding EDSL
         */
        ScopingBuilder to(Class<? extends T> targetType);

        /**
         * @see Binder documentation of Guice Binding EDSL
         */
        ScopingBuilder to(TypeLiteral<? extends T> targetLiteral);

        /**
         * @see Binder documentation of Guice Binding EDSL
         */
        ScopingBuilder to(Key<? extends T> targetKey);

        /**
         * @see Binder documentation of Guice Binding EDSL
         */
        void toInstance(T instance);

        /**
         * @see Binder documentation of Guice Binding EDSL
         */
        ScopingBuilder toProvider(Class<? extends Provider<? extends T>> providerType);

        /**
         * @see Binder documentation of Guice Binding EDSL
         */
        ScopingBuilder toProvider(Provider<? extends T> provider);

        /**
         * @see Binder documentation of Guice Binding EDSL
         */
        ScopingBuilder toProvider(javax.inject.Provider<? extends T> provider);

        /**
         * @see Binder documentation of Guice Binding EDSL
         */
        ScopingBuilder toConstructor(Constructor<? extends T> constructor);

        /**
         * @see Binder documentation of Guice Binding EDSL
         */
        ScopingBuilder toProducer(Producer<? extends T> producer);
    }

    /**
     * Builder capable of specifying binding scope.
     * 
     * @param <T> type of the key binding is defined for
     * @author maciej@chalapuk.pl (Maciej Chałapuk)
     */
    interface ScopingBuilder {

        /**
         * @see Binder documentation of Guice Binding EDSL
         */
        void in(Class<? extends Annotation> scopeAnnotation);

        /**
         * @see Binder documentation of Guice Binding EDSL
         */
        void in(Scope scope);
    }
}
