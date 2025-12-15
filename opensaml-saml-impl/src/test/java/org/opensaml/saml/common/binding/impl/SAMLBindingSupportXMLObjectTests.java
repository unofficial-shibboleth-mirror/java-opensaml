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

package org.opensaml.saml.common.binding.impl;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.saml1.core.Request;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class SAMLBindingSupportXMLObjectTests extends XMLObjectBaseTestCase {
    
    @Test
    public void checkSAML1MessageType()  {
        final Request request = (Request) XMLObjectSupport.buildXMLObject(Request.DEFAULT_ELEMENT_NAME);
        final org.opensaml.saml.saml1.core.Response response =
                (org.opensaml.saml.saml1.core.Response) XMLObjectSupport.buildXMLObject(
                        org.opensaml.saml.saml1.core.Response.DEFAULT_ELEMENT_NAME);
        
        // Success cases
        try {
            SAMLBindingSupport.checkSAML1MessageType(true, request);
        } catch (final MessageDecodingException e) {
            Assert.fail("Message was Response and a response was expected", e);
        }
        try {
            SAMLBindingSupport.checkSAML1MessageType(false, response);
        } catch (final MessageDecodingException e) {
            Assert.fail("Message was Response and a response was expected", e);
        }
        
        // Failure cases
        try {
            SAMLBindingSupport.checkSAML1MessageType(true, response);
            Assert.fail("Message was Response and a request was expected");
        } catch (final MessageDecodingException e) {
            // this is the valid result
        }
        try {
            SAMLBindingSupport.checkSAML1MessageType(false, request);
            Assert.fail("Message was AuthnRequest and a response was expected");
        } catch (final MessageDecodingException e) {
            // this is the valid result
        }
    }

    @Test
    public void checkSAML2MessageType()  {
        final AuthnRequest authnRequest = (AuthnRequest) XMLObjectSupport.buildXMLObject(AuthnRequest.DEFAULT_ELEMENT_NAME);
        final Response response = (Response) XMLObjectSupport.buildXMLObject(Response.DEFAULT_ELEMENT_NAME);
        
        // Success cases
        try {
            SAMLBindingSupport.checkSAML2MessageType(true, authnRequest);
        } catch (final MessageDecodingException e) {
            Assert.fail("Message was Response and a response was expected", e);
        }
        try {
            SAMLBindingSupport.checkSAML2MessageType(false, response);
        } catch (final MessageDecodingException e) {
            Assert.fail("Message was Response and a response was expected", e);
        }
        
        // Failure cases
        try {
            SAMLBindingSupport.checkSAML2MessageType(true, response);
            Assert.fail("Message was Response and a request was expected");
        } catch (final MessageDecodingException e) {
            // this is the valid result
        }
        try {
            SAMLBindingSupport.checkSAML2MessageType(false, authnRequest);
            Assert.fail("Message was AuthnRequest and a response was expected");
        } catch (final MessageDecodingException e) {
            // this is the valid result
        }
    }

}
