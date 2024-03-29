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

package org.opensaml.xmlsec.encryption;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;

/**
 * Builder for XMLObjects from {@link org.opensaml.xmlsec.encryption}.
 * 
 * @param <XMLEncryptionType> the type of XMLObject being built
 */
public interface XMLEncryptionBuilder<XMLEncryptionType extends XMLObject> extends XMLObjectBuilder<XMLEncryptionType> {
    
    /**
     * Builds an XMLObject using the default name and namespace information provided XML Encryption specifications.
     * 
     * @return built XMLObject
     */
    @Nonnull XMLEncryptionType buildObject();

}