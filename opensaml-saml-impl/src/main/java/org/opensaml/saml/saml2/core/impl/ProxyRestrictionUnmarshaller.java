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

/**
 * 
 */

package org.opensaml.saml.saml2.core.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.ProxyRestriction;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link ProxyRestriction} objects.
 */
public class ProxyRestrictionUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final ProxyRestriction proxyRestriction = (ProxyRestriction) parentObject;

        if (childObject instanceof Audience) {
            proxyRestriction.getAudiences().add((Audience) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final ProxyRestriction proxyRestriction = (ProxyRestriction) xmlObject;

        if (attribute.getLocalName().equals(ProxyRestriction.COUNT_ATTRIB_NAME)
                && attribute.getNamespaceURI() == null) {
            proxyRestriction.setProxyCount(Integer.valueOf(attribute.getValue()));
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}