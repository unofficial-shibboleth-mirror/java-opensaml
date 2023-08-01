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

package org.opensaml.saml.saml2.core.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;

import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.core.Extensions;
import org.slf4j.Logger;
import org.w3c.dom.Attr;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A thread-safe Unmarshaller for {@link Extensions} objects.
 */
public class ExtensionsUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractSAMLObjectUnmarshaller.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final Extensions extensions = (Extensions) parentObject;

        extensions.getUnknownXMLObjects().add(childObject);
    }

    /**
     * {@inheritDoc}
     */
    protected void processAttribute(final XMLObject xmlObject, final Attr attribute) throws UnmarshallingException {
        log.debug("Ignorning unknown attribute {}", attribute.getLocalName());
    }

    /**
     * {@inheritDoc}
     */
    protected void processElementContent(final XMLObject xmlObject, final String elementContent) {
        log.debug("Ignoring element content {}", elementContent);
    }

}