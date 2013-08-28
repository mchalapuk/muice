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

import java.util.Objects;

import static com.google.common.base.Preconditions.*;

/**
 * A mapping from a key (type and optional annotation) to the strategy for
 * getting instances of the type.
 * <p>
 * In Muice bindings are always created explicitly in binding module, via
 * {@code bind()} statements.
 * 
 * <pre>
 * bind(Service.class).annotatedWith(Red.class).to(ServiceImpl.class);
 * </pre>
 * 
 * They are primary data structure used by injectors.
 * 
 * @param <T> the bound type. The injected is always assignable to this type.
 * @author crazybob@google.com (Bob Lee)
 * @author jessewilson@google.com (Jesse Wilson)
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class Binding<T> {
    private final Key<T> mKey;
    private final Producer<? extends T> mProducer;
    private final Scope mScope;

    public Binding(Key<T> key, Producer<? extends T> producer, Scope scope) {
        mKey = checkNotNull(key);
        mProducer = checkNotNull(producer);
        mScope = checkNotNull(scope);
    }

    public Key<T> getKey() {
        return mKey;
    }

    public Producer<? extends T> getTarget() {
        return mProducer;
    }

    public Scope getScope() {
        return mScope;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mKey, mProducer, mScope);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        @SuppressWarnings("unchecked")
        Binding<T> other = (Binding<T>) obj;
        return mKey.equals(other.mKey)
                && mProducer.equals(other.mProducer)
                && mScope.equals(other.mScope);
    }
}
