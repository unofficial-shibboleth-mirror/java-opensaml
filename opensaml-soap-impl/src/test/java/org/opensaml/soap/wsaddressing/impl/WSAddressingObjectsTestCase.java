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

package org.opensaml.soap.wsaddressing.impl;

import org.testng.annotations.Test;
import org.opensaml.soap.testing.WSBaseTestCase;
import org.opensaml.soap.wsaddressing.Action;
import org.opensaml.soap.wsaddressing.Address;
import org.opensaml.soap.wsaddressing.EndpointReference;
import org.opensaml.soap.wsaddressing.Metadata;
import org.opensaml.soap.wsaddressing.ReferenceParameters;

/**
 * WSAddressingObjectTestCase is the test case for the WS-Addressing objects.
 * 
 */
@SuppressWarnings("javadoc")
public class WSAddressingObjectsTestCase extends WSBaseTestCase {

    @Test
    public void testAction() throws Exception {
        Action action= buildXMLObject(Action.ELEMENT_NAME);
        action.setURI("urn:test:foo:bar");
        marshallAndUnmarshall(action);
    }

    @Test
    public void testAddress() throws Exception {
        Address address= buildXMLObject(Address.ELEMENT_NAME);
        address.setURI(Address.ANONYMOUS);
        marshallAndUnmarshall(address);
    }

    @Test
    public void testEndpointReference() throws Exception {
        EndpointReference epr= buildXMLObject(EndpointReference.ELEMENT_NAME);
        Address address= buildXMLObject(Address.ELEMENT_NAME);
        address.setURI(Address.ANONYMOUS);
        ReferenceParameters referenceParameters= buildXMLObject(ReferenceParameters.ELEMENT_NAME);
        Metadata metadata= buildXMLObject(Metadata.ELEMENT_NAME);
        epr.setAddress(address);
        epr.setMetadata(metadata);
        epr.setReferenceParameters(referenceParameters);
        marshallAndUnmarshall(epr);
    }
    
    @Test
    public void testFaultTo() {
        //TODO
    }

    @Test
    public void testFrom() {
        //TODO
    }

    @Test
    public void testMessageID() {
        //TODO
    }

    @Test
    public void testMetadata() throws Exception {
        Metadata metadata= buildXMLObject(Metadata.ELEMENT_NAME);
        // TODO: add some child elements

        marshallAndUnmarshall(metadata);
    }
    
    @Test
    public void testProblemAction() {
        //TODO
    }

    @Test
    public void testProblemHeaderQName() {
        //TODO
    }

    @Test
    public void testProblemIRI() {
        //TODO
    }
    
    @Test
    public void testReferenceParameters() throws Exception {
        ReferenceParameters referenceParameters= buildXMLObject(ReferenceParameters.ELEMENT_NAME);
        // TODO: add some child elements
        marshallAndUnmarshall(referenceParameters);
    }

    @Test
    public void testRelatesTo() {
        //TODO
    }

    @Test
    public void testReplyTo() {
        //TODO
    }

    @Test
    public void testRetryAfter() {
        //TODO
    }

    @Test
    public void testSoapAction() {
        //TODO
    }

    @Test
    public void testTo() {
        //TODO
    }

}
