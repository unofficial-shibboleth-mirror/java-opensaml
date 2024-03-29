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

package org.opensaml.xmlsec.signature.support;

import javax.annotation.Nullable;

/**
 * A specialization of {@link ContentReference} which allows some signature reference properties
 * to be specified.
 */
public interface ConfigurableContentReference extends ContentReference {
    
    /**
     * Gets the algorithm used to digest the content.
     * 
     * @return the algorithm used to digest the content
     */
    @Nullable String getDigestAlgorithm();

    /**
     * Sets the algorithm used to digest the content.
     * 
     * @param newAlgorithm the algorithm used to digest the content
     */
    void setDigestAlgorithm(@Nullable final String newAlgorithm);

}