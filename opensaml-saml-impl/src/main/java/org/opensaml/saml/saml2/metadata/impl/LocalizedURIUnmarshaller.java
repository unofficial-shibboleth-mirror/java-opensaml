/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.saml2.metadata.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.LangBearing;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.impl.XSURIUnmarshaller;
import org.opensaml.saml.saml2.metadata.LocalizedURI;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.XMLConstants;

/**
 * A thread-safe unmarshaller for {@link LocalizedURI} objects.
 */
public class LocalizedURIUnmarshaller extends XSURIUnmarshaller {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        
        if (attribute.getLocalName().equals(LangBearing.XML_LANG_ATTR_LOCAL_NAME)
                && XMLConstants.XML_NS.equals(attribute.getNamespaceURI())) {
            final LocalizedURI name = (LocalizedURI) xmlObject;

            name.setXMLLang(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}