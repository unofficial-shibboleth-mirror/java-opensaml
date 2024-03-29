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
import org.opensaml.saml.saml1.core.AttributeQuery;
import org.opensaml.saml.saml1.core.AuthorityBinding;

/**
 *  Test for {@link org.opensaml.saml.saml1.core.AuthorityBinding}
 */
@SuppressWarnings({"null", "javadoc"})
public class AuthorityBindingTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    /** Value of AuthorityKind in test file */
    private final QName expectedAuthorityKind;

    /** Value of Location in test file */
    private final String expectedLocation;

    /** Value of Binding in test file */
    private final String expectedBinding;

    /**
     * Constructor
     */
    public AuthorityBindingTest() {
        //this attribute is a Schema QName type, e.g. AuthorityKind="samlp:AttributeQuery"
        expectedAuthorityKind = new QName(SAMLConstants.SAML10P_NS, AttributeQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
        expectedLocation = "here";
        expectedBinding = "binding";
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleAuthorityBinding.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleAuthorityBindingAttributes.xml";
        qname = new QName(SAMLConstants.SAML1_NS, AuthorityBinding.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final AuthorityBinding authorityBinding = (AuthorityBinding) unmarshallElement(singleElementFile);
        assert authorityBinding!=null;
        Assert.assertNull(authorityBinding.getAuthorityKind(), "AuthorityKind attribute present");
        Assert.assertNull(authorityBinding.getBinding(), "Binding attribute present");
        Assert.assertNull(authorityBinding.getLocation(), "Location attribute present");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final AuthorityBinding authorityBinding = (AuthorityBinding) unmarshallElement(singleElementOptionalAttributesFile);
        assert authorityBinding!=null;
        Assert.assertEquals(authorityBinding.getAuthorityKind(), expectedAuthorityKind, "AuthorityKind attribute");
        Assert.assertEquals(authorityBinding.getBinding(), expectedBinding, "Binding attribute");
        Assert.assertEquals(authorityBinding.getLocation(), expectedLocation, "Location attribute");        
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AuthorityBinding authorityBinding = (AuthorityBinding) buildXMLObject(qname);
        authorityBinding.setAuthorityKind(expectedAuthorityKind);
        authorityBinding.setBinding(expectedBinding);
        authorityBinding.setLocation(expectedLocation);
        assertXMLEquals(expectedOptionalAttributesDOM, authorityBinding);
    }

}
