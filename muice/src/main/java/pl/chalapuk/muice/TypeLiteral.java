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

import com.google.common.base.Objects;

/**
 * Represents a generic type {@code T}. Java doesn't yet provide a way to
 * represent generic types, so this class does.
 * <p>
 * Comparing to original Guice TypeLiteral, this class does not force clients to
 * create a subclass of it and doesn't use reflection to retain this information
 * at runtime. Runtime type argument information is still available, but at a
 * cost of less convenient way of creating type literals.
 * <p>
 * For example, following code create a type literal for {@code List<String>}:
 * 
 * <pre>
 * TypeLiteral&lt;List&lt;String&gt;&gt; listOfStrings = TypeLiteral.get(List.class, String.class);
 * </pre>
 * <p>
 * So first argument is the raw type, second is the type parameter. If there are
 * more type parameters, they should be passed in correct order.
 * 
 * <pre>
 * TypeLiteral&lt;Map&lt;String, URI&gt;&gt; stringsToURIMap = TypeLiteral.get(Map.class, String.class, URI.class);
 * </pre>
 * <p>
 * Things get complicated if type parameter is also generic.
 * 
 * <pre>
 * TypeLiteral&lt;List&lt;List&lt;String&gt;&gt;&gt; listOfListsOfStrings = TypeLiteral.get(
 *         List.class,
 *         TypeLiteral.get(List.class, String.class)
 *         );
 * </pre>
 * 
 * <h3>Known Drawbacks</h3>
 * <p>
 * The fact that static method is used to create type literals implies another
 * drawback. {@code TypeLiteral.get} has type arguments, so the resulting type
 * literal object is implicitly cast to actual type parameter of method
 * invocation. This leads to possibility of improper usage of
 * {@code TypeLiteral.get} method and eventually to cast exceptions at runtime.
 * <p>
 * Following code contain improper implicit casts:
 * 
 * <pre>
 * TypeLiteral&lt;List&lt;Object&gt;&gt; listOfURIs = TypeLiteral.get(List.class, URI.class);
 * TypeLiteral&lt;List&lt;Object&gt;&gt; listOfStrings = TypeLiteral.get(List.class, String.class);
 * </pre>
 * <p>
 * As both variables are of the same type, it is possible to bind the same
 * instance of {@code List<URI>} to both of them. Due to type erasure it will
 * get injected properly in both cases, but once as {@code List<URI>} and once
 * as {@code List<String>}. It may be even used without crashing for a while,
 * but VM will throw {@link ClassCastException} on first implicit cast between
 * {@code String} and {@code URI}.
 * <p>
 * Such situation can be very hard to debug since the exception may be thrown
 * long after execution of the code that contain error. There is no method for
 * preventing it. Just be careful when you change types around invocations of
 * {@code TypeLiteral.get}.
 * 
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public final class TypeLiteral<T> {
    private final Class<? super T> mRawType;
    private final TypeLiteral<?>[] mTypeArguments;

    /**
     * Creates type literal representing given rawType, which has no type
     * arguments.
     * 
     * @param rawType type to be represented
     * @return type literal representing given type
     */
    public static <T> TypeLiteral<T> get(Class<? super T> rawType) {
        return get(rawType, new TypeLiteral<?>[0]);
    }

    @SuppressWarnings({
            "unchecked",
            "rawtypes"
    })
    /**
     * Creates type literal representing given rawType with given type arguments.
     * 
     * @param rawType type to be represented
     * @param args actual type arguments
     * @return type literal representing given type with given type arguments
     */
    public static <T> TypeLiteral<T> get(Class<? super T> rawType, Class<?>... args) {
        TypeLiteral<?>[] converted = new TypeLiteral<?>[args.length];
        for (int i = 0; i < converted.length; ++i) {
            converted[i] = new TypeLiteral(args[i], new TypeLiteral<?>[0]);
        }
        return get(rawType, converted);
    }

    /**
     * Creates type literal representing given rawType with given type
     * arguments, which may be also generic.
     * 
     * @param rawType type to be represented
     * @param args actual type arguments
     * @return type literal representing given type with given type arguments
     */
    public static <T> TypeLiteral<T> get(Class<? super T> rawType, TypeLiteral<?>... args) {
        return new TypeLiteral<>(rawType, args);
    }

    private TypeLiteral(Class<? super T> rawType, TypeLiteral<?>[] typeArguments) {
        mRawType = checkNotNull(rawType);
        mTypeArguments = checkNotNull(typeArguments);
    }

    public Class<? super T> getRawType() {
        return mRawType;
    }

    public TypeLiteral<?> getTypeArgument(int index) {
        return mTypeArguments[index];
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mRawType.hashCode(), Arrays.deepHashCode(mTypeArguments));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TypeLiteral<?> other = (TypeLiteral<?>) obj;
        return mRawType.equals(other.mRawType) &&
                Arrays.equals(mTypeArguments, other.mTypeArguments);
    }

    @Override
    public String toString() {
        return Objects
                .toStringHelper(this)
                .add("class", mRawType.getName())
                .add("args", mTypeArguments.length > 0 ? Arrays.toString(mTypeArguments) : null)
                .omitNullValues()
                .toString();
    }
}
