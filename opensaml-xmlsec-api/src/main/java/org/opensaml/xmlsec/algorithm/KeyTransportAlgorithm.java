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

package org.opensaml.xmlsec.algorithm;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface for key transport algorithm descriptors.
 */
public interface KeyTransportAlgorithm extends KeySpecifiedAlgorithm {
    
    /**
     * Get the JCA cipher mode specified by this algorithm.
     * 
     * @return the cipher mode
     */
    @Nonnull @NotEmpty String getCipherMode();
    
    /**
     * Get the JCA padding algorithm specified by this algorithm.
     * 
     * @return the padding algorithm
     */
    @Nonnull @NotEmpty String getPadding();

}