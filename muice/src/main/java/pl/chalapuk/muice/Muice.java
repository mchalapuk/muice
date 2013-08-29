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

import static com.google.common.base.Preconditions.*;

import java.util.Arrays;

import javax.inject.Singleton;

import com.google.common.base.Objects;

import pl.chalapuk.muice.customization.BindingCollector;
import pl.chalapuk.muice.customization.BindingCollectorFactory;
import pl.chalapuk.muice.customization.InjectorBuilder;
import pl.chalapuk.muice.customization.MuiceBuilder;
import pl.chalapuk.muice.customization.ProducerFactory;
import pl.chalapuk.muice.customization.TypeInfoFactory;
import pl.chalapuk.muice.defaults.CachedTypeInfoFactory;
import pl.chalapuk.muice.defaults.ExplicitCollectorFactory;
import pl.chalapuk.muice.defaults.ReflectionProducerFactory;
import pl.chalapuk.muice.defaults.ReflectionTypeInfoFactory;
import pl.chalapuk.muice.internal.BinderImpl;
import pl.chalapuk.muice.internal.InjectorBuilderImpl;
import pl.chalapuk.muice.internal.ScopeMapping;
import pl.chalapuk.muice.internal.Scopes;

/**
 * The entry point to the Muice library. Creates {@linkplain Injector injectors}
 * from {@linkplain BindingModule binding modules}.
 * <p>
 * Muice (just like original Guice) draws clear boundaries between APIs and
 * Implementations of these APIs. Connections between them are configured inside
 * BindingModules, which are contained in your Application. Application
 * typically defines your {@code main()} method, that bootstraps the Injector
 * using the {@code Muice} class, as in this example:
 * 
 * <pre>
 * public class FooApplication {
 *     public static void main(String[] args) {
 *         Injector injector = Muice.createInjector(
 *             new BindingModuleA(),
 *             new BindingModuleB(),
 *             . . .
 *             new FooApplicationFlagsModule(args)
 *         );
 * 
 *         // Now just bootstrap the application and you're done
 *         FooStarter starter = injector.getInstance(FooStarter.class);
 *         starter.runApplication();
 *     }
 * }
 * 
 * 
 * </pre>
 * <p>
 * Muice provides also a possibility to customize injector creation process (to
 * differentiate from Guice behavior) by building an instance of {@link Muice}.
 * There are 3 main customizable abstractions:
 * <ol>
 * <li>{@link TypeInfoFactory} - provides information about class' default
 * injection points and constructor dependencies. Default implementation uses
 * reflection to build returned data structures.
 * <li>{@link ProducerFactory} - provides {@linkplain Producer producers} used
 * to instantiate classes. Default implementation uses reflection to invoke
 * constructor.
 * <li>{@link BindingCollector} - provides {@linkplain BindingCollector binding
 * collector} instance for each created injector. Collector aggregates
 * {@link Binding} object during injector configuration. Default implementation
 * simply aggregates bindings and return null if asked for binding that was not
 * configured before.
 * </ol>
 * <p>
 * Besides factories, Muice customization supports:
 * <ul>
 * <li>setting boot modules - loaded as first binding modules. Default boot
 * module configures binding of {@link Singleton} annotation to
 * {@link Scopes#SINGLETON} scope.
 * <li>setting default binding scope - used when no scope is configures for a
 * binding. By default it is {@link Scopes#NONE}.
 * </ul>
 * <p>
 * Folowing code is a Muice customization example.
 * 
 * <pre>
 * Muice customizedMuice = Muice.newMuice()
 *         .withTypeInfoFactory(new CustomTypeInforFactory())
 *         .withProducerFactory(new CustomProducerFactory())
 *         .withBindingCollector(new CustomBindingCollector())
 *         .withBootModules(new CustomBootModule())
 *         .withDefaultScope(new CustomDefaultScope)
 *         .build();
 * Injector injector = customizedMuice.newInjector()
 *         .withModules(new BindingModuleA())
 *         .withModules(new BindingModuleB())
 *         .build();
 * </pre>
 * 
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class Muice {
    private static final TypeInfoFactory sDefaultTypeInfoFactory =
            new CachedTypeInfoFactory(new ReflectionTypeInfoFactory());
    private static final ProducerFactory sDefaultProducerFactory = new ReflectionProducerFactory();
    private static final BindingCollectorFactory sDefaultCollectorFactory = new ExplicitCollectorFactory();
    private static final BindingModule[] sDefaultBootModules = new BindingModule[] {
            new BindingModule() {

                @Override
                public void configure(Binder binder) {
                    binder.bindScope(Singleton.class, Scopes.SINGLETON);
                }
            }
    };
    private static final Scope sDefaultScope = Scopes.NONE;

    public static final Muice DEFAULT = newMuice()
            .build();

    /**
     * Creates an injector for given modules.
     * 
     * @param modules modules to be used to configure injector
     * @return configured injector
     */
    public static Injector createInjector(BindingModule... modules) {
        return createInjector(Arrays.asList(modules));
    }

    /**
     * Creates an injector for given modules.
     * 
     * @param modules modules to be used to configure injector
     * @return configured injector
     */
    public static Injector createInjector(Iterable<BindingModule> modules) {
        return DEFAULT
                .newInjector()
                .withModules(modules)
                .build();
    }

    /**
     * Start new Muice building process.
     * 
     * @return builder capable of creating new Muice.
     */
    public static MuiceBuilder newMuice() {
        return new MuiceBuilder() {
            private TypeInfoFactory mTypeInfoFactory = sDefaultTypeInfoFactory;
            private ProducerFactory mProducerFactory = sDefaultProducerFactory;
            private BindingCollectorFactory mCollectorFactory = sDefaultCollectorFactory;
            private BindingModule[] mBootModules = sDefaultBootModules;
            private Scope mDefaultScope = sDefaultScope;

            @Override
            public MuiceBuilder withTypeInfoFactory(TypeInfoFactory factory) {
                mTypeInfoFactory = checkNotNull(factory, "factory");
                return this;
            }

            @Override
            public MuiceBuilder withProducerFactory(ProducerFactory factory) {
                mProducerFactory = checkNotNull(factory, "factory");
                return this;
            }

            @Override
            public MuiceBuilder withBindingCollectorFactory(BindingCollectorFactory factory) {
                mCollectorFactory = checkNotNull(factory, "factory");
                return this;
            }

            @Override
            public MuiceBuilder withBootModules(BindingModule... modules) {
                mBootModules = checkNotNull(modules, "modules");
                return this;
            }

            @Override
            public MuiceBuilder withoutBootModules() {
                mBootModules = new BindingModule[0];
                return this;
            }

            @Override
            public MuiceBuilder withDefaultScope(Scope scope) {
                mDefaultScope = checkNotNull(scope, "scope");
                return this;
            }

            @Override
            public Muice build() {
                return new Muice(mTypeInfoFactory, mProducerFactory,
                        mCollectorFactory, mBootModules, mDefaultScope);
            }
        };
    }

    private final TypeInfoFactory mTypeInfoFactory;
    private final ProducerFactory mProducerFactory;
    private final BindingCollectorFactory mCollectorFactory;
    private final BindingModule[] mBootModules;
    private final Scope mDefaultScope;

    private Muice(TypeInfoFactory typeInfoFactory, ProducerFactory producerFactory,
            BindingCollectorFactory collectorFactory, BindingModule[] bootModules,
            Scope defaultScope) {
        mTypeInfoFactory = typeInfoFactory;
        mProducerFactory = producerFactory;
        mCollectorFactory = collectorFactory;
        mBootModules = bootModules;
        mDefaultScope = defaultScope;
    }

    /**
     * Starts new Injector building process.
     * 
     * @return builder capable of creating injector.
     */
    public InjectorBuilder newInjector() {
        BindingCollector collector = mCollectorFactory.createCollector();
        BinderImpl binder = new BinderImpl(
                collector, mTypeInfoFactory, mProducerFactory, new ScopeMapping(), mDefaultScope);
        return new InjectorBuilderImpl(collector, binder).withModules(mBootModules);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mTypeInfoFactory, mProducerFactory, mCollectorFactory,
                Arrays.hashCode(mBootModules), mDefaultScope);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;
        Muice other = (Muice) obj;

        return (mTypeInfoFactory.equals(other.mTypeInfoFactory)
                && mProducerFactory.equals(other.mProducerFactory)
                && mCollectorFactory.equals(other.mCollectorFactory)
                && Arrays.equals(mBootModules, other.mBootModules)
                && mDefaultScope.equals(other.mDefaultScope));
    }
}
