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

package org.opensaml.saml.saml1.core.tests;

import org.testng.annotations.Test;
import org.testng.Assert;
import java.io.InputStream;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml1.core.Response;
import org.w3c.dom.Document;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Tests unmarshalling and marshalling for various response messages.
 */
@SuppressWarnings({"null", "javadoc"})
public class ResponseTest extends XMLObjectBaseTestCase {

    /** Path to file with full response message */
    private String fullResponsePath;
    
    /**
     * Constructor
     */
    public ResponseTest(){
        fullResponsePath = "/org/opensaml/saml/saml1/core/FullResponse.xml";
    }
    
    /**
     * Tests unmarshalling a full response message.
     */
    @Test
    public void testResponseUnmarshall(){

        try {
            InputStream in = ResponseTest.class.getResourceAsStream(fullResponsePath);
            Document responseDoc = parserPool.parse(in);
            Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().ensureUnmarshaller(
                    responseDoc.getDocumentElement());

            Response response = (Response) unmarshaller.unmarshall(responseDoc.getDocumentElement());

            Assert.assertEquals(response.getElementQName().getLocalPart(), "Response",
                    "First element of response data was not expected Response");
        } catch (XMLParserException xe) {
            Assert.fail("Unable to parse XML file: " + xe);
        } catch (UnmarshallingException ue) {
            Assert.fail("Unable to unmarshall XML: " + ue);
        }
    }
    
    /**
     * Tests marshalling a full response message.
     */
    @Test
    public void testResponseMarshall(){
        //TODO
    }
}