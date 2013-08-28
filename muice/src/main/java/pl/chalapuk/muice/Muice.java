
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

public class Muice {
    private static final TypeInfoFactory sDefaultTypeInfoFactory =
            new CachedTypeInfoFactory(new ReflectionTypeInfoFactory());
    private static final BindingCollectorFactory sDefaultCollectorFactory = new ExplicitCollectorFactory();
    private static final ProducerFactory sDefaultProducerFactory = new ReflectionProducerFactory();
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

    public static Injector createInjector(BindingModule... modules) {
        return createInjector(Arrays.asList(modules));
    }

    public static Injector createInjector(Iterable<BindingModule> modules) {
        return DEFAULT
                .newInjector()
                .withModules(modules)
                .build();
    }

    public static MuiceBuilder newMuice() {
        return new MuiceBuilder() {
            private TypeInfoFactory mTypeInfoFactory = sDefaultTypeInfoFactory;
            private BindingCollectorFactory mCollectorFactory = sDefaultCollectorFactory;
            private ProducerFactory mProducerFactory = sDefaultProducerFactory;
            private BindingModule[] mBootModules = sDefaultBootModules;
            private Scope mDefaultScope = sDefaultScope;

            @Override
            public MuiceBuilder withBindingCollectorFactory(BindingCollectorFactory factory) {
                mCollectorFactory = checkNotNull(factory, "factory");
                return this;
            }

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
                return new Muice(mTypeInfoFactory, mCollectorFactory,
                        mProducerFactory, mBootModules, mDefaultScope);
            }
        };
    }

    private final TypeInfoFactory mTypeInfoFactory;
    private final BindingCollectorFactory mCollectorFactory;
    private final ProducerFactory mProducerFactory;
    private final BindingModule[] mBootModules;
    private final Scope mDefaultScope;

    private Muice(TypeInfoFactory typeInfoFactory, BindingCollectorFactory collectorFactory,
            ProducerFactory producerFactory, BindingModule[] bootModules, Scope defaultScope) {
        mTypeInfoFactory = typeInfoFactory;
        mCollectorFactory = collectorFactory;
        mProducerFactory = producerFactory;
        mBootModules = bootModules;
        mDefaultScope = defaultScope;
    }

    public InjectorBuilder newInjector() {
        BindingCollector collector = mCollectorFactory.createCollector();
        BinderImpl binder = new BinderImpl(
                collector, mTypeInfoFactory, mProducerFactory, new ScopeMapping(), mDefaultScope);
        return new InjectorBuilderImpl(collector, binder).withModules(mBootModules);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mTypeInfoFactory, mCollectorFactory, mProducerFactory,
                Arrays.hashCode(mBootModules), mTypeInfoFactory);
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
                && mCollectorFactory.equals(other.mCollectorFactory)
                && mProducerFactory.equals(other.mProducerFactory)
                && Arrays.equals(mBootModules, other.mBootModules)
                && mDefaultScope.equals(other.mDefaultScope));
    }
}
