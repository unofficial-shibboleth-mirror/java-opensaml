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

package org.opensaml.xmlsec.encryption.impl;


import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.KANonce;
import org.opensaml.xmlsec.encryption.OriginatorKeyInfo;
import org.opensaml.xmlsec.encryption.RecipientKeyInfo;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class AgreementMethodTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedAlgorithm;
    private int expectedNumUnknownChildren;
    
    /**
     * Constructor
     *
     */
    public AgreementMethodTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/AgreementMethod.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/AgreementMethodChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAlgorithm = "urn:string:foo";
        expectedNumUnknownChildren = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final AgreementMethod am = (AgreementMethod) unmarshallElement(singleElementFile);
        
        assert am != null;
        Assert.assertEquals(am.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
        Assert.assertNull(am.getKANonce(), "KA-Nonce child element");
        Assert.assertEquals(am.getUnknownXMLObjects().size(), 0, "Unknown children");
        Assert.assertNull(am.getOriginatorKeyInfo(), "OriginatorKeyInfo child element");
        Assert.assertNull(am.getRecipientKeyInfo(), "RecipientKeyInfo child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final AgreementMethod am = (AgreementMethod) unmarshallElement(childElementsFile);
        
        assert am != null;
        Assert.assertEquals(am.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
        Assert.assertNotNull(am.getKANonce(), "KA-Nonce child element");
        Assert.assertEquals(am.getUnknownXMLObjects().size(), expectedNumUnknownChildren, "Unknown children");
        Assert.assertNotNull(am.getOriginatorKeyInfo(), "OriginatorKeyInfo child element");
        Assert.assertNotNull(am.getRecipientKeyInfo(), "RecipientKeyInfo child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final AgreementMethod am = (AgreementMethod) buildXMLObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
        
        am.setAlgorithm(expectedAlgorithm);
        
        assertXMLEquals(expectedDOM, am);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final AgreementMethod am = (AgreementMethod) buildXMLObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
        
        am.setAlgorithm(expectedAlgorithm);
        am.setKANonce((KANonce) buildXMLObject(KANonce.DEFAULT_ELEMENT_NAME));
        am.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        am.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        am.setOriginatorKeyInfo((OriginatorKeyInfo) buildXMLObject(OriginatorKeyInfo.DEFAULT_ELEMENT_NAME));
        am.setRecipientKeyInfo((RecipientKeyInfo) buildXMLObject(RecipientKeyInfo.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, am);
    }

}
