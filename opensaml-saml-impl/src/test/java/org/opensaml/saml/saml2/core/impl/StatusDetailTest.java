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

import org.testng.annotations.Test;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.StatusDetail;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.StatusDetailImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class StatusDetailTest extends XMLObjectProviderBaseTestCase {

    /**
     * Constructor.
     *
     */
    public StatusDetailTest() {
       singleElementFile = "/org/opensaml/saml/saml2/core/impl/StatusDetail.xml";
       childElementsFile = "/org/opensaml/saml/saml2/core/impl/StatusDetailChildElements.xml";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final StatusDetail statusDetail = (StatusDetail) buildXMLObject(StatusDetail.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, statusDetail);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final StatusDetail statusDetail = (StatusDetail) buildXMLObject(StatusDetail.DEFAULT_ELEMENT_NAME);
        final QName childQname = new QName("http://www.example.org/testObjects", "SimpleElement", "test");
        
        statusDetail.getUnknownXMLObjects().add(buildXMLObject(childQname));
        statusDetail.getUnknownXMLObjects().add(buildXMLObject(childQname));
        statusDetail.getUnknownXMLObjects().add(buildXMLObject(childQname));
        
        assertXMLEquals(expectedChildElementsDOM, statusDetail);
    }



    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final StatusDetail statusDetail = (StatusDetail) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(statusDetail);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final StatusDetail statusDetail = (StatusDetail) unmarshallElement(childElementsFile);
        assert statusDetail!=null;
        Assert.assertEquals(statusDetail.getUnknownXMLObjects().size(), 3);
    }
    
}