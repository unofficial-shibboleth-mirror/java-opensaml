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
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.ConfirmationMethod;
import org.opensaml.saml.saml1.core.SubjectConfirmation;
import org.opensaml.saml.saml1.core.SubjectConfirmationData;
import org.w3c.dom.Document;

/**
 * Test for {@link org.opensaml.saml.saml1.core.SubjectConfirmation}
 */
@SuppressWarnings({"null", "javadoc"})
public class SubjectConfirmationTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    private String fullElementsFile;

    private Document expectedFullDOM;

    /**
     * Constructor
     */
    public SubjectConfirmationTest() {
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleSubjectConfirmation.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleSubjectConfirmation.xml";
        fullElementsFile = "/org/opensaml/saml/saml1/impl/SubjectConfirmationWithChildren.xml";
        qname = new QName(SAMLConstants.SAML1_NS, SubjectConfirmation.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedFullDOM = parserPool.parse(this.getClass().getResourceAsStream(fullElementsFile));
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final SubjectConfirmation subjectConfirmation = (SubjectConfirmation) unmarshallElement(singleElementFile);
        assert subjectConfirmation != null;
        Assert.assertEquals(subjectConfirmation
                .getConfirmationMethods().size(), 0, "Non zero number of child ConfirmationMethods elements");
        Assert.assertNull(subjectConfirmation
                .getSubjectConfirmationData(), "Non zero number of child SubjectConfirmationData elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        // No attributes
    }

    /**
     * Test an XML file with children
     */
    @Test
    public void testFullElementsUnmarshall() {
        final SubjectConfirmation subjectConfirmation = (SubjectConfirmation) unmarshallElement(fullElementsFile);
        assert subjectConfirmation != null;
        Assert.assertEquals(subjectConfirmation.getConfirmationMethods().size(), 2, "Number of ConfirmationMethods");
        Assert.assertNotNull(subjectConfirmation.getSubjectConfirmationData(), "Zero child SubjectConfirmationData elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        // No attributes
    }

    /*
     * Generate an subject with contents
     */

    @Test
    public void testFullElementsMarshall() {
        SubjectConfirmation subjectConfirmation = (SubjectConfirmationImpl) buildXMLObject(qname);

        QName oqname = new QName(SAMLConstants.SAML1_NS, ConfirmationMethod.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        subjectConfirmation.getConfirmationMethods().add((ConfirmationMethod) buildXMLObject(oqname));
        subjectConfirmation.getConfirmationMethods().add((ConfirmationMethod) buildXMLObject(oqname));
        
        final XMLObjectBuilder<XSAny> proxyBuilder = builderFactory.ensureBuilder(XSAny.TYPE_NAME);
        oqname = new QName(SAMLConstants.SAML1_NS, SubjectConfirmationData.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        subjectConfirmation.setSubjectConfirmationData(proxyBuilder.buildObject(oqname));

        assertXMLEquals(expectedFullDOM, subjectConfirmation);
    }
}
