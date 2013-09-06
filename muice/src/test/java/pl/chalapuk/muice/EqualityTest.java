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

import java.util.Map;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import pl.chalapuk.muice.TestedTypes.*;

/**
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class EqualityTest {

    @Test
    public void testKeyEquality() {
        new EqualsTester()
                .addEqualityGroup(
                        Key.get(Object.class),
                        Key.get(TypeLiteral.get(Object.class))
                )
                .addEqualityGroup(
                        Key.get(Object.class,
                                QualifierAnnotationA.class),
                        Key.get(TypeLiteral.get(Object.class),
                                QualifierAnnotationA.class)
                )
                .addEqualityGroup(
                        Key.get(TypeLiteral.get(Generic.class, Object.class),
                                QualifierAnnotationA.class),
                        Key.get(TypeLiteral.get(Generic.class, Object.class),
                                QualifierAnnotationA.class)
                )
                .testEquals();
    }
    
    @Test
    public void testTypeLiteralEquality() {
        new EqualsTester()
                .addEqualityGroup(
                        TypeLiteral.get(Generic.class, Object.class),
                        TypeLiteral.get(Generic.class, Object.class)
                )
                .addEqualityGroup(
                        TypeLiteral.get(Generic.class, Interface.class),
                        TypeLiteral.get(Generic.class, Interface.class)
                )
                .addEqualityGroup(
                        TypeLiteral.get(Map.class, String.class, String.class),
                        TypeLiteral.get(Map.class, String.class, String.class)
                )
                .testEquals();
    }
    
    @Test
    public void testMuiceEquality() {
        new EqualsTester()
                .addEqualityGroup(
                        Muice.DEFAULT,
                        Muice.newMuice().build()
                )
                .addEqualityGroup(
                        Muice.newMuice().withoutBootModules().build(),
                        Muice.newMuice().withoutBootModules().build()
                )
                .testEquals();
    }
}
