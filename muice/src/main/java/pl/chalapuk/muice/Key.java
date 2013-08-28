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

import java.lang.annotation.Annotation;

import javax.inject.Qualifier;

import com.google.common.base.Objects;

/**
 * Binding key consisting of an injection type and an optional qualifier
 * annotation. Matches the type and annotation at a point of injection.
 * <p>
 * For example, {@code Key.get(Service.class, Transactional.class)} will match:
 * 
 * <pre>
 * class Service {
 *   {@literal @}Inject
 *   public Service({@literal @}Transactional Service service) {
 *     ...
 *   }
 *   ...
 * }
 * </pre>
 * <p>
 * {@code Key} supports generic types because it uses {@link TypeLiteral}. It
 * shares {@code TypeLiteral}s drawbacks regarding implicit type casts.
 * <p>
 * Unlike Guice original Key, this class does differentiate between primitive
 * types (int, char, etc.) and their corresponding wrapper types (Integer,
 * Character, etc.). Key returned from {@code Key.get(int.class)} is not equal
 * to key returned from {@code Key.get(Integer.class)}
 * 
 * @author crazybob@google.com (Bob Lee)
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public final class Key<T> {
    private final TypeLiteral<T> mTypeLiteral;
    private final Class<? extends Annotation> mQualifier;
    private final int mHashCode;

    /**
     * Creates a key for given class, which has no type arguments.
     * 
     * @param type type to create key for
     * @return key representing given type
     */
    public static <T> Key<T> get(Class<T> type) {
        return new Key<>(TypeLiteral.get(type), null);
    }

    /**
     * Creates a key for given type literal.
     * 
     * @param type type to create key for
     * @return key representing given type
     */
    public static <T> Key<T> get(TypeLiteral<T> typeLiteral) {
        return new Key<>(typeLiteral, null);
    }

    /**
     * Creates key with qualifier for given class, which has no type arguments.
     * 
     * @param type type to create key for
     * @param qualifier qualifier to be used in injection point
     * @return key representing given type
     * @throws IllegalArgumentException if passed annotation type is not
     *             annotated with {@link Qualifier}
     */
    public static <T> Key<T> get(Class<T> type, Class<? extends Annotation> qualifier)
            throws IllegalArgumentException {
        return new Key<>(TypeLiteral.get(type), qualifier);
    }

    /**
     * Creates key with qualifier for given type literal.
     * 
     * @param type type to create key for
     * @param qualifier qualifier to be used in injection point
     * @return key representing given type
     * @throws IllegalArgumentException if passed annotation type is not
     *             annotated with {@link Qualifier}
     */
    public static <T> Key<T> get(TypeLiteral<T> typeLiteral, Class<? extends Annotation> qualifier) {
        return new Key<>(typeLiteral, qualifier);
    }

    private Key(TypeLiteral<T> typeLiteral, Class<? extends Annotation> qualifier) {
        mTypeLiteral = checkNotNull(typeLiteral);
        mQualifier = checkIsValidQualifier(qualifier);
        mHashCode = Objects.hashCode(mTypeLiteral, mQualifier);
    }

    public TypeLiteral<T> getTypeLiteral() {
        return mTypeLiteral;
    }

    public Class<? extends Annotation> getQualifier() {
        return mQualifier;
    }

    public Class<? super T> getRawType() {
        return mTypeLiteral.getRawType();
    }

    @Override
    public int hashCode() {
        return mHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Key<?> other = (Key<?>) obj;
        return mTypeLiteral.equals(other.mTypeLiteral)
                && Objects.equal(mQualifier, other.mQualifier);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("type", mTypeLiteral)
                .add("qualifier", mQualifier)
                .omitNullValues()
                .toString();
    }

    private static Class<? extends Annotation> checkIsValidQualifier(
            Class<? extends Annotation> type) {
        if (type != null) {
            checkArgument(type.getAnnotation(Qualifier.class) != null,
                    "% MUST be annotated with %s to be valid qualifier",
                    type.getName(), Qualifier.class.getName());
        }
        return type;
    }
}
