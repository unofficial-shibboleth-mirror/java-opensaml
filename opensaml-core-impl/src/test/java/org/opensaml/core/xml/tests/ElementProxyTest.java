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

package org.opensaml.core.xml.tests;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.List;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSAny;
import org.w3c.dom.Document;

import net.shibboleth.shared.xml.XMLParserException;

/** Test unmarshalling content for which no specific object providers were registered. */
public class ElementProxyTest extends XMLObjectBaseTestCase {

    /**
     * Tests unmarshalling unknown content into the element proxy.
     * 
     * @throws XMLParserException ...
     * @throws UnmarshallingException ...
     */
    @Test
    public void testUnmarshallUnknownContent() throws XMLParserException, UnmarshallingException{
        String documentLocation = "/org/opensaml/core/xml/UnknownContent.xml";
        Document document = parserPool.parse(UnmarshallingTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.ensureUnmarshaller(XMLObjectProviderRegistrySupport.getDefaultProviderQName());
        XMLObject xmlobject = unmarshaller.unmarshall(document.getDocumentElement());
        
        Assert.assertEquals(xmlobject.getElementQName().getLocalPart(), "products", "Unexpted root element name");
        
        final List<XMLObject> children = xmlobject.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 2, "Unexpected number of children");
        
        final List<XMLObject> nestedChildren = children.get(1).getOrderedChildren();
        assert nestedChildren != null;
        Assert.assertEquals(((XSAny) nestedChildren.get(0)).getTextContent(),
                "<strong>XSLT Perfect IDE</strong>", "Unexpected CDATA content");
    }
}
