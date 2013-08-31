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

package pl.chalapuk.muice.internal;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.google.common.collect.Maps;

import pl.chalapuk.muice.Scope;

/**
 * Maps scopes to annotations.
 * 
 * @author maciej@chalapuk.pl (Maciej Chałapuk)
 */
public class ScopeMapping {
    private final Map<Class<? extends Annotation>, Scope> mMapping = Maps.newHashMap();

    public void mapScopeAnnotation(Class<? extends Annotation> annotation, Scope scope) {
        mMapping.put(annotation, scope);
    }

    public Scope get(Class<? extends Annotation> annotation) {
        return mMapping.get(annotation);
    }
}
