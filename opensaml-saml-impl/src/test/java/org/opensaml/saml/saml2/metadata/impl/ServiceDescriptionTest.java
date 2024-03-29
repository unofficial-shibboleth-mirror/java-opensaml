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
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.metadata.ServiceDescription;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.ServiceDescription}.
 */
@SuppressWarnings({"null", "javadoc"})
public class ServiceDescriptionTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected description */
    /** Expected URL. */
    private String expectLocalizedDescription = "This is a description";
    private String expectLang = "Language" ;

   
    /**
     * Constructor
     */
    public ServiceDescriptionTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/ServiceDescription.xml";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final ServiceDescription description = (ServiceDescription) unmarshallElement(singleElementFile);
        assert description!=null;
        
        Assert.assertEquals(description.getValue(), expectLocalizedDescription, "Description was not expected value");
        Assert.assertEquals(description.getXMLLang(), expectLang, "xml:lamg was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        ServiceDescription description = (new ServiceDescriptionBuilder()).buildObject();
        
        description.setValue(expectLocalizedDescription);
        description.setXMLLang(expectLang);

        assertXMLEquals(expectedDOM, description);
    }
}