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

import java.time.Instant;

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.RequestAbstractType;

/**
 * 
 */
@SuppressWarnings({"null", "javadoc"})
public abstract class RequestTestBase extends XMLObjectProviderBaseTestCase {

    /** Expected ID value */
    protected String expectedID;

    /** Expected SAML version */
    protected SAMLVersion expectedSAMLVersion;

    /** Expected IssueInstant attribute */
    protected Instant expectedIssueInstant;

    /** Expected Destination attribute */
    protected String expectedDestination;

    /** Expected Consent attribute */
    protected String expectedConsent;

    /** Expected Issuer child element */
    protected Issuer expectedIssuer;

    /**
     * Constructor
     * 
     */
    public RequestTestBase() {

    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedID = "abc123";
        expectedSAMLVersion = SAMLVersion.VERSION_20;
        expectedIssueInstant = Instant.parse("2006-02-21T16:40:00.000Z");
        expectedDestination = "http://idp.example.org/endpoint";
        expectedConsent = "urn:string:consent";

        final QName issuerQName = new QName(SAMLConstants.SAML20_NS, Issuer.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20_PREFIX);
        expectedIssuer = (Issuer) buildXMLObject(issuerQName);
    }

    /** {@inheritDoc} */
    @Test
    public abstract void testSingleElementUnmarshall();

    /** {@inheritDoc} */
    @Test
    public abstract void testSingleElementMarshall();

    /**
     * Used by subclasses to populate the required attribute values that this test expects.
     * 
     * @param samlObject ...
     */
    protected void populateRequiredAttributes(SAMLObject samlObject) {
        RequestAbstractType req = (RequestAbstractType) samlObject;

        req.setID(expectedID);
        req.setIssueInstant(expectedIssueInstant);
        // NOTE: the SAML Version attribute is set automatically by the impl superclass

    }

    /**
     * Used by subclasses to populate the optional attribute values that this test expects.
     * 
     * @param samlObject ...
     */
    protected void populateOptionalAttributes(SAMLObject samlObject) {
        RequestAbstractType req = (RequestAbstractType) samlObject;

        req.setConsent(expectedConsent);
        req.setDestination(expectedDestination);

    }

    /**
     * Used by subclasses to populate the child elements that this test expects.
     * 
     * @param samlObject ...
     */
    protected void populateChildElements(SAMLObject samlObject) {
        RequestAbstractType req = (RequestAbstractType) samlObject;

        req.setIssuer(expectedIssuer);

    }

    protected void helperTestSingleElementUnmarshall(SAMLObject samlObject) {
        final RequestAbstractType req = (RequestAbstractType) samlObject;
        assert req!=null; 

        Assert.assertEquals(req.getID(), expectedID, "Unmarshalled ID attribute was not the expected value");
        final SAMLVersion ver = req.getVersion();
        assert ver!=null;
        Assert.assertEquals(ver.toString(), expectedSAMLVersion.toString(), "Unmarshalled Version attribute was not the expected value");
        Assert.assertEquals(expectedIssueInstant
                .compareTo(req.getIssueInstant()), 0, "Unmarshalled IssueInstant attribute was not the expected value");

        Assert.assertNull(req.getConsent(), "Consent was not null");
        Assert.assertNull(req.getDestination(), "Destination was not null");

    }

    protected void helperTestSingleElementOptionalAttributesUnmarshall(SAMLObject samlObject) {
        final RequestAbstractType req = (RequestAbstractType) samlObject;

        Assert.assertEquals(req.getID(), expectedID, "Unmarshalled ID attribute was not the expected value");
        final SAMLVersion ver = req.getVersion();
        assert ver!=null;
        Assert.assertEquals(ver.toString(), expectedSAMLVersion.toString(), "Unmarshalled Version attribute was not the expected value");
        Assert.assertEquals(expectedIssueInstant
                .compareTo(req.getIssueInstant()), 0, "Unmarshalled IssueInstant attribute was not the expected value");

        Assert.assertEquals(req.getConsent(), expectedConsent, "Unmarshalled Consent attribute was not the expected value");
        Assert.assertEquals(req
                .getDestination(), expectedDestination, "Unmarshalled Destination attribute was not the expected value");

    }

    protected void helperTestChildElementsUnmarshall(SAMLObject samlObject) {
        final RequestAbstractType req = (RequestAbstractType) samlObject;

        Assert.assertNotNull(req.getIssuer(), "Issuer was null");

    }
}
