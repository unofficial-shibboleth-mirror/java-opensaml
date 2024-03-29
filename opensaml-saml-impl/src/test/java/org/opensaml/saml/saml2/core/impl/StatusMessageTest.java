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
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.StatusMessage;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.StatusMessageImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class StatusMessageTest extends XMLObjectProviderBaseTestCase {
    
   /** The expected message*/ 
    protected String expectedMessage;
    
    /**
     * Constructor
     *
     */
    public StatusMessageTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/StatusMessage.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedMessage = "Status Message";
    }
    

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final StatusMessage message = (StatusMessage) buildXMLObject(StatusMessage.DEFAULT_ELEMENT_NAME);
        
        message.setValue(expectedMessage);
        
        assertXMLEquals(expectedDOM, message);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final StatusMessage message = (StatusMessage) unmarshallElement(singleElementFile);
        assert message!=null;

        Assert.assertEquals(message.getValue(), expectedMessage, "Unmarshalled status message was not the expected value");   
    }
}