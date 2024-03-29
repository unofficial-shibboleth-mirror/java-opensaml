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
import org.opensaml.saml.saml2.metadata.ServiceName;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.ServiceDescription}.
 */
@SuppressWarnings({"null", "javadoc"})
public class ServiceNameTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected description. */
    private String expectValue ="Name";
    private String expectLang = "Language" ;

    /**
     * Constructor
     */
    public ServiceNameTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/ServiceName.xml";
    }
    


    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        ServiceName name = (ServiceName) unmarshallElement(singleElementFile);
        assert name!=null;

        Assert.assertEquals(name.getXMLLang(), expectLang, "xml:lamg was not expected value");
        Assert.assertEquals(name.getValue(), expectValue, "Name was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, ServiceName.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        ServiceName name = (ServiceName) buildXMLObject(qname);
        
        name.setValue(expectValue);
        name.setXMLLang(expectLang);

        assertXMLEquals(expectedDOM, name);
    }
}