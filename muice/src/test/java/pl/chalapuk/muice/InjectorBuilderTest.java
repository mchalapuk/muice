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

import java.util.Collections;

import org.junit.Test;

import pl.chalapuk.muice.customization.InjectorBuilder;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class InjectorBuilderTest {

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateWhenCallingBuildOnDisposedBuilder() {
        InjectorBuilder builder = Muice.DEFAULT.newInjector();
        builder.build();
        builder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateWhenPassingBindingModuleArrayOnDisposedBuilder() {
        InjectorBuilder builder = Muice.DEFAULT.newInjector();
        builder.build();
        builder.withModules(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                // empty
            }
        });
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateWhenPassingBindingModuleIterableOnDisposedBuilder() {
        InjectorBuilder builder = Muice.DEFAULT.newInjector();
        builder.build();
        builder.withModules(Collections.<BindingModule> singleton(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                // empty
            }
        }));
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenPassingNullBindingModuleArray() {
        Muice.DEFAULT
                .newInjector()
                .withModules((BindingModule[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenPassingNullBindingModuleInArray() {
        Muice.DEFAULT
                .newInjector()
                .withModules((BindingModule) null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenPassingNullBindingModuleIterable() {
        Muice.DEFAULT
                .newInjector()
                .withModules((Iterable<BindingModule>) null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerWhenPassingNullBindingModuleInIterable() {
        Muice.DEFAULT
                .newInjector()
                .withModules(Collections.<BindingModule> singleton(null));
    }
}
