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
package org.opensaml.saml.saml2.core.impl;

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusMessage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.StatusImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class StatusTest extends XMLObjectProviderBaseTestCase {

    /**
     * Constructor
     *
     */
    public StatusTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/Status.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/StatusChildElements.xml";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final Status status = (Status) buildXMLObject(Status.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, status);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final Status status = (Status) buildXMLObject(Status.DEFAULT_ELEMENT_NAME);
        
        final QName statusCodeQName = new QName(SAMLConstants.SAML20P_NS, StatusCode.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        status.setStatusCode((StatusCode) buildXMLObject(statusCodeQName));
        
        final QName statusMessageQName = new QName(SAMLConstants.SAML20P_NS, StatusMessage.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        status.setStatusMessage((StatusMessage) buildXMLObject(statusMessageQName));
        
        assertXMLEquals(expectedChildElementsDOM, status);
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Status status = (Status) unmarshallElement(singleElementFile);
        assert status !=null;
        Assert.assertNull(status.getStatusCode(), "StatusCode child");
        Assert.assertNull(status.getStatusMessage(), "StatusMessage");
    }


    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final Status status = (Status) unmarshallElement(childElementsFile);
        assert status !=null;
        Assert.assertNotNull(status.getStatusCode(), "StatusCode of Status was null");
        Assert.assertNotNull(status.getStatusMessage(), "StatusMessage of Status was null");
    }
}