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

import java.lang.reflect.Constructor;

import javax.inject.Provider;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import pl.chalapuk.muice.customization.BindingCollector;
import pl.chalapuk.muice.customization.BindingCollectorFactory;
import pl.chalapuk.muice.customization.ConstructorInfo;
import pl.chalapuk.muice.customization.ProducerFactory;
import pl.chalapuk.muice.customization.RawTypeInfo;
import pl.chalapuk.muice.customization.TypeInfoException;
import pl.chalapuk.muice.customization.TypeInfoFactory;

public class MuiceBuilderTest {
    private static BindingCollectorFactory sFakeBindingCollectorFactory;
    private static TypeInfoFactory sFakeTypeInfoFactory;
    private static ProducerFactory sFakeProducerFactory;
    private static BindingModule sFakeBootModule;
    private static Scope sFakeScope;

    @BeforeClass
    public static void initFakeObjects() {
        sFakeBindingCollectorFactory = new BindingCollectorFactory() {

            @Override
            public BindingCollector createCollector() {
                return null;
            }
        };

        sFakeTypeInfoFactory = new TypeInfoFactory() {

            @Override
            public <T> RawTypeInfo<T> getRawTypeInfo(Class<? super T> rawType)
                    throws TypeInfoException {
                return null;
            }

            @Override
            public <T> ConstructorInfo<T> getConstructorInfo(Constructor<T> constructor) {
                return null;
            }
        };

        sFakeProducerFactory = new ProducerFactory() {

            @Override
            public <T> Producer<T> createProducer(ConstructorInfo<T> info) {
                return null;
            }
        };

        sFakeBootModule = new BindingModule() {

            @Override
            public void configure(Binder binder) {
                // nothing
            }
        };

        sFakeScope = new Scope() {

            @Override
            public <T> Provider<? extends T> decorate(Key<T> key, Provider<? extends T> unscoped) {
                return null;
            }
        };
    }

    @AfterClass
    public static void removeFakeObjects() {
        sFakeBindingCollectorFactory = null;
        sFakeTypeInfoFactory = null;
        sFakeProducerFactory = null;
        sFakeBootModule = null;
        sFakeScope = null;
    }

    @Test
    public void testBuildingNewMuiceWithDefaultConfiguration() {
        assertNotNull("null muice created", Muice.newMuice().build());
    }

    @Test
    public void testNewMuiceWithDefaultConfigurationEqualsMuiceDEFAULT() {
        assertEquals(Muice.DEFAULT, Muice.newMuice().build());
    }

    @Test
    public void testMuiceInstanceWithCustomBindingCollectorNotEqualsDEFAULT() {
        Muice muice = Muice.newMuice()
                .withBindingCollectorFactory(sFakeBindingCollectorFactory)
                .build();

        assertNotEquals(Muice.DEFAULT, muice);
    }

    @Test
    public void testMuiceInstanceWithCustomTypeInfoFactoryNotEqualsDEFAULT() {
        Muice muice = Muice.newMuice()
                .withTypeInfoFactory(sFakeTypeInfoFactory)
                .build();

        assertNotEquals(Muice.DEFAULT, muice);
    }

    @Test
    public void testMuiceInstanceWithCustomProducerFactoryNotEqualsDEFAULT() {
        Muice muice = Muice.newMuice()
                .withProducerFactory(sFakeProducerFactory)
                .build();

        assertNotEquals(Muice.DEFAULT, muice);
    }

    @Test
    public void testMuiceInstanceWithCustomBootModulesNotEqualsDEFAULT() {
        Muice muice = Muice.newMuice()
                .withBootModules(sFakeBootModule)
                .build();

        assertNotEquals(Muice.DEFAULT, muice);
    }

    @Test
    public void testMuiceInstanceWithoutBootModulesNotEqualsDEFAULT() {
        Muice muice = Muice.newMuice()
                .withoutBootModules()
                .build();

        assertNotEquals(Muice.DEFAULT, muice);
    }

    @Test
    public void testMuiceInstanceWithCustomDefaultScopeNotEqualsDEFAULT() {
        Muice muice = Muice.newMuice()
                .withDefaultScope(sFakeScope)
                .build();

        assertNotEquals(Muice.DEFAULT, muice);
    }

    @Test
    public void testTwoMuiceInstancesEqualIfBuiltWithTheSameConfiguration() {
        Muice muice0 = Muice.newMuice()
                .withBindingCollectorFactory(sFakeBindingCollectorFactory)
                .withTypeInfoFactory(sFakeTypeInfoFactory)
                .withProducerFactory(sFakeProducerFactory)
                .withBootModules(sFakeBootModule)
                .withDefaultScope(sFakeScope)
                .build();
        Muice muice1 = Muice.newMuice()
                .withBindingCollectorFactory(sFakeBindingCollectorFactory)
                .withTypeInfoFactory(sFakeTypeInfoFactory)
                .withProducerFactory(sFakeProducerFactory)
                .withBootModules(sFakeBootModule)
                .withDefaultScope(sFakeScope)
                .build();

        assertEquals(muice0, muice1);
    }

    @Test(expected = NullPointerException.class)
    public void testNullBindingCollectorFactoryNotAccepted() {
        Muice.newMuice().withBindingCollectorFactory(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullTypeInfoFactoryNotAccepted() {
        Muice.newMuice().withTypeInfoFactory(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullProducerFactoryNotAccepted() {
        Muice.newMuice().withProducerFactory(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullBootModulesNotAccepted() {
        Muice.newMuice().withBootModules((BindingModule[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullDefaultScopeNotAccepted() {
        Muice.newMuice().withDefaultScope(null);
    }
}
