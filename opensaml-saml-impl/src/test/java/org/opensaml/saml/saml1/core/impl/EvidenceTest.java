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
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.AssertionIDReference;
import org.opensaml.saml.saml1.core.Evidence;

/**
 * Test for {@link EvidenceImpl}
 */
@SuppressWarnings({"null", "javadoc"})
public class EvidenceTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    /**
     * Constructor
     */

    public EvidenceTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleEvidence.xml";
        childElementsFile = "/org/opensaml/saml/saml1/impl/EvidenceWithChildren.xml";
        
        qname = new QName(SAMLConstants.SAML1_NS, Evidence.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {
        final Evidence evidence = (Evidence) unmarshallElement(singleElementFile);
        assert evidence !=null;

        Assert.assertEquals(evidence.getEvidence().size(), 0, "AssertionIDReference or Assertion element was present");
    }

    /** {@inheritDoc} */

    @Test
    public void testChildElementsUnmarshall() {
        final Evidence evidence = (Evidence) unmarshallElement(childElementsFile);
        assert evidence !=null;

        Assert.assertEquals(evidence.getEvidence().size(), 4, "Assertion and AssertionIDReference element count");
        Assert.assertEquals(evidence.getAssertionIDReferences().size(), 2, "AssertionIDReference element count");
        Assert.assertEquals(evidence.getAssertions().size(), 2, "Assertion element count");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */

    @Test
    public void testChildElementsMarshall() {
        final Evidence evidence = (Evidence) buildXMLObject(qname);

        final QName refQname = new QName(SAMLConstants.SAML1_NS, AssertionIDReference.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        final QName assertionQname = new QName(SAMLConstants.SAML1_NS, Assertion.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        
        evidence.getAssertionIDReferences().add((AssertionIDReference) buildXMLObject(refQname));
        evidence.getAssertions().add((Assertion) buildXMLObject(assertionQname));
        evidence.getAssertions().add((Assertion) buildXMLObject(assertionQname));
        evidence.getAssertionIDReferences().add((AssertionIDReference) buildXMLObject(refQname));

        assertXMLEquals(expectedChildElementsDOM, evidence);
    }
}
