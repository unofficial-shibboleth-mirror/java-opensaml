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

package org.opensaml.saml.common;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.opensaml.core.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * A thread safe abstract unmarshaller. This abstract unmarshaller only works with
 * {@link SAMLObject}.
 */
@ThreadSafe
public abstract class AbstractSAMLObjectUnmarshaller extends AbstractXMLObjectUnmarshaller {
    
    /**
     * Parse {@link SAMLVersion} instance from the specified DOM attribute.
     * 
     * @param attribute the DOM attribute to process
     * @return the parsed SAMLVersion instance
     * @throws UnmarshallingException if a SAMLVersion instance could not be successfully parsed
     */
    @Nonnull protected SAMLVersion parseSAMLVersion(@Nonnull final Attr attribute) throws UnmarshallingException {
        try {
            return SAMLVersion.valueOf(attribute.getValue());
        } catch (final RuntimeException e) {
            throw new UnmarshallingException(String.format("Could not parse SAMLVersion from DOM attribute value '%s'",
                    attribute.getValue()), e);
        }
    }

}