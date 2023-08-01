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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.w3c.dom.Document;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.SubjectConfirmationImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class SubjectConfirmationTest extends XMLObjectProviderBaseTestCase {

    /** Expected Method value */
    private String expectedMethod;
    
    /** File with test data for EncryptedID use case. */
    private String childElementsWithEncryptedIDFile;

    /** Constructor */
    public SubjectConfirmationTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/SubjectConfirmation.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/SubjectConfirmationChildElements.xml";
        childElementsWithEncryptedIDFile = "/org/opensaml/saml/saml2/core/impl/SubjectConfirmationChildElementsWithEncryptedID.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedMethod = "conf method";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final SubjectConfirmation subjectConfirmation = (SubjectConfirmation) unmarshallElement(singleElementFile);
        assert subjectConfirmation!=null;
        final String method = subjectConfirmation.getMethod();
        Assert.assertEquals(method, expectedMethod, "Method not as expected");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, SubjectConfirmation.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final SubjectConfirmation subjectConfirmation = (SubjectConfirmation) buildXMLObject(qname);

        subjectConfirmation.setMethod(expectedMethod);
        assertXMLEquals(expectedDOM, subjectConfirmation);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final SubjectConfirmation subjectConfirmation = (SubjectConfirmation) unmarshallElement(childElementsFile);
        assert subjectConfirmation!=null;
        Assert.assertNotNull(subjectConfirmation.getNameID(), "Identifier elemement not present");
        Assert.assertNotNull(subjectConfirmation.getSubjectConfirmationData(), "SubjectConfirmationData element not present");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, SubjectConfirmation.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final SubjectConfirmation subjectConfirmation = (SubjectConfirmation) buildXMLObject(qname);

        final QName nameIDQName = new QName(SAMLConstants.SAML20_NS, NameID.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        subjectConfirmation.setNameID((NameID) buildXMLObject(nameIDQName));
        
        final  QName subjectConfirmationDataQName = new QName(SAMLConstants.SAML20_NS, SubjectConfirmationData.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        subjectConfirmation.setSubjectConfirmationData((SubjectConfirmationData) buildXMLObject(subjectConfirmationDataQName));

        assertXMLEquals(expectedChildElementsDOM, subjectConfirmation);
    }
    
    @Test
    public void testChildElementsWithEncryptedIDUnmarshall() {
        final SubjectConfirmation subjectConfirmation = (SubjectConfirmation) unmarshallElement(childElementsWithEncryptedIDFile);
        assert subjectConfirmation!=null;
        Assert.assertNull(subjectConfirmation.getBaseID(), "BaseID element present");
        Assert.assertNull(subjectConfirmation.getNameID(), "NameID element present");
        Assert.assertNotNull(subjectConfirmation.getEncryptedID(), "EncryptedID element not present");
        Assert.assertNotNull(subjectConfirmation.getSubjectConfirmationData(), "SubjectConfirmationData element not present");
    }

    @Test
    public void testChildElementsWithEncryptedIDMarshall() throws XMLParserException {
        final QName qname = new QName(SAMLConstants.SAML20_NS, SubjectConfirmation.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final SubjectConfirmation subjectConfirmation = (SubjectConfirmation) buildXMLObject(qname);

        final QName encryptedIDQName = new QName(SAMLConstants.SAML20_NS, EncryptedID.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        subjectConfirmation.setEncryptedID((EncryptedID) buildXMLObject(encryptedIDQName));
        
        final QName subjectConfirmationDataQName = new QName(SAMLConstants.SAML20_NS, SubjectConfirmationData.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        subjectConfirmation.setSubjectConfirmationData((SubjectConfirmationData) buildXMLObject(subjectConfirmationDataQName));
        
        final Document expectedChildElementsWithEncryptedID = parserPool.parse(SubjectConfirmationTest.class
                .getResourceAsStream(childElementsWithEncryptedIDFile));
        assertXMLEquals(expectedChildElementsWithEncryptedID, subjectConfirmation);
    }
}