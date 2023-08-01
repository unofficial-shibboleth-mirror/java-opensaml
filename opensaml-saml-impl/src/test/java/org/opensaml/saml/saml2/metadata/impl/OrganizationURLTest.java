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
package org.opensaml.saml.saml2.metadata.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.OrganizationURL;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.OrganizationURL}.
 */
@SuppressWarnings({"null", "javadoc"})
public class OrganizationURLTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected URL. */
    private String expectValue = "http://example.org";
    private String expectLang = "Language" ;
    
    /**
     * Constructor
     */
    public OrganizationURLTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/OrganizationURL.xml";
    }
    
    /** {@inheritDoc} */

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final OrganizationURL url = (OrganizationURL) unmarshallElement(singleElementFile);
        assert url!=null;
        Assert.assertEquals(url.getURI(), expectValue, "URL was not expected value");
        Assert.assertEquals(url.getXMLLang(), expectLang, "langg was not expected value");
        url.hashCode();
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20MD_NS, OrganizationURL.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        final OrganizationURL url = (OrganizationURL) buildXMLObject(qname);
        
        url.setURI(expectValue);
        url.setXMLLang(expectLang);

        assertXMLEquals(expectedDOM, url);
    }

}