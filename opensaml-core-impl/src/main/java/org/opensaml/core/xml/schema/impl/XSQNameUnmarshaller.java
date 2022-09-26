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

package org.opensaml.core.xml.schema.impl;

import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.xml.ElementSupport;
import net.shibboleth.shared.xml.QNameSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSQName;
import org.w3c.dom.Text;

/**
 * A thread-safe unmarshaller for {@link org.opensaml.core.xml.schema.XSQName}s.
 */
public class XSQNameUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(final XMLObject parentXMLObject, final XMLObject childXMLObject)
            throws UnmarshallingException {
        // no child elements
        // left this in to bypass the "ignore" logging message since we're not in fact ignoring the content
    }

    /** {@inheritDoc} */
    protected void unmarshallTextContent(final XMLObject xmlObject, final Text content) throws UnmarshallingException {
        final String textContent = StringSupport.trimOrNull(content.getData());
        if (textContent != null) {
            final XSQName qname = (XSQName) xmlObject;
            qname.setValue(QNameSupport.constructQName(ElementSupport.getElementAncestor(content), textContent));
        }
    }
}