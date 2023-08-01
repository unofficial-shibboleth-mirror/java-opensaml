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

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.SubjectImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class SubjectTest extends XMLObjectProviderBaseTestCase {

    /** Count of SubjectConfirmation subelements */
    protected int expectedSubjectConfirmationCount = 2;

    /** Constructor */
    public SubjectTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/Subject.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/SubjectChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Subject subject = (Subject) unmarshallElement(singleElementFile);

        Assert.assertNotNull(subject);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final Subject subject = (Subject) buildXMLObject(qname);

        assertXMLEquals(expectedDOM, subject);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final Subject subject = (Subject) unmarshallElement(childElementsFile);
        assert subject!=null;
        Assert.assertNotNull(subject.getNameID(), "Identifier element not present");
        Assert.assertEquals(subject
                .getSubjectConfirmations().size(), expectedSubjectConfirmationCount, "SubjectConfirmation Count not as expected");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final Subject subject = (Subject) buildXMLObject(qname);

        final QName nameIDQName = new QName(SAMLConstants.SAML20_NS, NameID.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        subject.setNameID((NameID) buildXMLObject(nameIDQName));
        
        final QName subjectConfirmationQName = new QName(SAMLConstants.SAML20_NS, SubjectConfirmation.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        for (int i = 0; i < expectedSubjectConfirmationCount; i++) {
            subject.getSubjectConfirmations().add((SubjectConfirmation) buildXMLObject(subjectConfirmationQName));
        }

        assertXMLEquals(expectedChildElementsDOM, subject);
    }
}