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

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Status;
import org.opensaml.saml.saml1.core.StatusCode;
import org.opensaml.saml.saml1.core.StatusMessage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * org.opensaml.saml.saml1.core.Status.
 */
@SuppressWarnings({"null", "javadoc"})
public class StatusTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects. */
    private final QName qname;

    /**
     * Constructor.
     */
    public StatusTest() {
        childElementsFile = "/org/opensaml/saml/saml1/impl/FullStatus.xml";
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleStatus.xml";

        qname = new QName(SAMLConstants.SAML10P_NS, Status.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {

        final Status status = (Status) unmarshallElement(singleElementFile);
        assert status!= null;
        Assert.assertNotNull(status.getStatusCode(), "StatusCode");
        Assert.assertNull(status.getStatusMessage(), "StatusMessage");
        Assert.assertNull(status.getStatusDetail(), "StatusDetail");
    }

    /**
     * Test an Response file with children.
     */
    @Test
    public void testChildElementsUnmarshall() {
        final Status status = (Status) unmarshallElement(childElementsFile);
        assert status!= null;
        Assert.assertNotNull(status.getStatusCode(), "StatusCode");
        Assert.assertNotNull(status.getStatusMessage(), "StatusMessage");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        final Status status = (Status) buildXMLObject(qname);
        assert status!= null;
        StatusCode statusCode = (StatusCode) buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue(StatusCode.SUCCESS);
        status.setStatusCode(statusCode);

        assertXMLEquals(expectedDOM, status);
    }

    /** {@inheritDoc} */

    @Test
    public void testChildElementsMarshall() {
        Status status = (Status) buildXMLObject(qname);

        StatusCode statusCode = (StatusCode) buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue(StatusCode.SUCCESS);
        status.setStatusCode(statusCode);

        StatusMessage statusMessage = (StatusMessage) buildXMLObject(StatusMessage.DEFAULT_ELEMENT_NAME);
        status.setStatusMessage(statusMessage);

        assertXMLEquals(expectedChildElementsDOM, status);
    }
}