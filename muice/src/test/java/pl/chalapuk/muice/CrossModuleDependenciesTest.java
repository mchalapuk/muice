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

import org.junit.Test;

import pl.chalapuk.muice.TestedTypes.WithObjectDependency;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class CrossModuleDependenciesTest {

    @Test
    public void testBindingDependencyInFirstModuleDependentTypeInSecond() {
        Injector injector = Muice.createInjector(
                new BindingModule() {

                    @Override
                    public void configure(Binder binder) {
                        binder.bind(Object.class);
                    }
                },
                new BindingModule() {

                    @Override
                    public void configure(Binder binder) {
                        binder.bind(WithObjectDependency.class);
                    }
                });

        assertNotNull(injector.getInstance(WithObjectDependency.class));
    }

    @Test
    public void testBindingDependencyInInstalledModuleDependentTypeInRoot() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.install(new BindingModule() {

                    @Override
                    public void configure(Binder binder2) {
                        binder2.bind(Object.class);
                    }
                });
                binder.bind(WithObjectDependency.class);
            }
        });

        assertNotNull(injector.getInstance(WithObjectDependency.class));
    }

    @Test
    public void testBindingDependencyInRootModuleDependentTypeInInstalled() {
        Injector injector = Muice.createInjector(new BindingModule() {

            @Override
            public void configure(Binder binder) {
                binder.bind(Object.class);
                binder.install(new BindingModule() {

                    @Override
                    public void configure(Binder binder2) {
                        binder2.bind(WithObjectDependency.class);
                    }
                });
            }
        });

        assertNotNull(injector.getInstance(WithObjectDependency.class));
    }
}
