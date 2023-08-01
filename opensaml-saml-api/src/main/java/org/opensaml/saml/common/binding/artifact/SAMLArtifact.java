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

package org.opensaml.saml.common.binding.artifact;

import javax.annotation.Nonnull;

/**
 * Interface for all SAML artifacts.
 */
public interface SAMLArtifact {

    /**
     * Gets the bytes for the artifact.
     * 
     * @return the bytes for the artifact
     */
    @Nonnull byte[] getArtifactBytes();

    /**
     * Gets the 2 byte type code for this artifact.
     * 
     * @return the type code for this artifact
     */
    @Nonnull byte[] getTypeCode();

}