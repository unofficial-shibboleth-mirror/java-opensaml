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

package org.opensaml.core.xml;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.schema.XSAny;

/**
 * Interface for implementations which adapt/wrap an instance of {@link XSAny}.
 */
public interface XSAnyAdapter extends XMLObject {
    
    /**
     * Get the adapted instance of {@link XSAny}
     * 
     * @return the adapted instance of XSAny
     */
    @Nonnull XSAny getAdapted();

}
