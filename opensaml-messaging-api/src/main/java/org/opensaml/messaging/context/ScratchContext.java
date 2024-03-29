/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.messaging.context;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NullableElements;

/**
 * A context subclass for holding arbitrary data in a map.
 * 
 * @since 3.4.0
 */
public final class ScratchContext extends BaseContext {

    /** Map of scratch data. */
    @Nonnull @NullableElements private Map<Object,Object> map;

    /** Constructor. */
    public ScratchContext() {
        map = new HashMap<>();
    }
    
    /**
     * Get the map of scratch data.
     * 
     * @return the map
     */
    @Nonnull @NullableElements @Live public Map<Object,Object> getMap() {
        return map;
    }

}