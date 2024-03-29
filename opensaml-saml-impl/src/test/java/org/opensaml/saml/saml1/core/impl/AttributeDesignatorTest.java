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

package org.opensaml.saml.saml1.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.AttributeDesignator;

/**
 * 
 */
@SuppressWarnings({"null", "javadoc"})
public class AttributeDesignatorTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    /** Value from test file */
    private final String expectedAttributeName;

    /** Value from test file */
    private final String expectedAttributeNamespace;

    /**
     * Constructor
     */
    public AttributeDesignatorTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleAttributeDesignator.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleAttributeDesignatorAttributes.xml";
        expectedAttributeName = "AttributeName";
        expectedAttributeNamespace = "namespace";
        qname = new QName(SAMLConstants.SAML1_NS, AttributeDesignator.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AttributeDesignator ad = (AttributeDesignator) unmarshallElement(singleElementFile);
        assert ad!=null;

        Assert.assertNull(ad.getAttributeName(), "AttributeName");
        Assert.assertNull(ad.getAttributeNamespace(), "AttributeNamespace");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AttributeDesignator ad = (AttributeDesignator) unmarshallElement(singleElementOptionalAttributesFile);
        assert ad!=null;

        Assert.assertEquals(ad.getAttributeName(), expectedAttributeName, "AttributeName");
        Assert.assertEquals(ad.getAttributeNamespace(), expectedAttributeNamespace, "AttributeNamespace");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AttributeDesignator ad = (AttributeDesignator) buildXMLObject(qname);

        ad.setAttributeName(expectedAttributeName);
        ad.setAttributeNamespace(expectedAttributeNamespace);
        assertXMLEquals(expectedOptionalAttributesDOM, ad);
    }
   
}
