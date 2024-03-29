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
import org.opensaml.saml.saml1.core.AuthenticationQuery;
import org.opensaml.saml.saml1.core.Subject;

/**
 * Test class for org.opensaml.saml.saml1.core.AuthenticationQuery
 */
@SuppressWarnings({"null", "javadoc"})
public class AuthenticationQueryTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    private final String expectedAuthenticationMethod;

    /**
     * Constructor
     */
    public AuthenticationQueryTest() {
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleAuthenticationQuery.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleAuthenticationQueryAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml1/impl/AuthenticationQueryWithChildren.xml";
        expectedAuthenticationMethod = "Trust Me";
        qname = new QName(SAMLConstants.SAML10P_NS, AuthenticationQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {

        final AuthenticationQuery authenticationQuery;
        authenticationQuery = (AuthenticationQuery) unmarshallElement(singleElementFile);
        assert authenticationQuery!=null;
        Assert.assertNull(authenticationQuery.getAuthenticationMethod(), "AuthenticationQuery attribute present");;
        Assert.assertNull(authenticationQuery.getSubject(), "Subject element present");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final AuthenticationQuery authenticationQuery;
        authenticationQuery = (AuthenticationQuery) unmarshallElement(singleElementOptionalAttributesFile);
        assert authenticationQuery!=null;
        Assert.assertEquals(authenticationQuery.getAuthenticationMethod(), expectedAuthenticationMethod, "AuthenticationQuery attribute");;
        Assert.assertNull(authenticationQuery.getSubject(), "Subject element present");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final AuthenticationQuery authenticationQuery;
        authenticationQuery = (AuthenticationQuery) unmarshallElement(childElementsFile);
        assert authenticationQuery!=null;
        Assert.assertNotNull(authenticationQuery.getSubject(), "No Subject element found");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AuthenticationQuery authenticationQuery = (AuthenticationQuery) buildXMLObject(qname);

        authenticationQuery.setAuthenticationMethod(expectedAuthenticationMethod);
        assertXMLEquals(expectedOptionalAttributesDOM, authenticationQuery);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        AuthenticationQuery authenticationQuery = (AuthenticationQuery) buildXMLObject(qname);

        authenticationQuery.setSubject((Subject) buildXMLObject(new QName(SAMLConstants.SAML1_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX)));
        assertXMLEquals(expectedChildElementsDOM, authenticationQuery);

    }

}
