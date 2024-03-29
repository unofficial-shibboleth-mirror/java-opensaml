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

package org.opensaml.xmlsec.signature;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;

/**
 * Builder for XMLObjects from {@link org.opensaml.xmlsec.signature}.
 * 
 * @param <XMLSignatureType> the type of XMLObject being built
 */
public interface XMLSignatureBuilder<XMLSignatureType extends XMLObject> extends XMLObjectBuilder<XMLSignatureType> {
    
    /**
     * Builds an XMLObject using the default name and namespace information provided by the XML Signature
     * specifications.
     * 
     * @return built XMLObject
     */
    @Nonnull XMLSignatureType buildObject();

}