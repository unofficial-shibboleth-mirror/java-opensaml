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

package org.opensaml.soap.wssecurity.impl;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.soap.testing.WSBaseTestCase;
import org.opensaml.soap.wssecurity.BinarySecurityToken;
import org.opensaml.soap.wssecurity.Created;
import org.opensaml.soap.wssecurity.Embedded;
import org.opensaml.soap.wssecurity.EncryptedHeader;
import org.opensaml.soap.wssecurity.Expires;
import org.opensaml.soap.wssecurity.Iteration;
import org.opensaml.soap.wssecurity.Nonce;
import org.opensaml.soap.wssecurity.Password;
import org.opensaml.soap.wssecurity.Reference;
import org.opensaml.soap.wssecurity.Salt;
import org.opensaml.soap.wssecurity.Timestamp;
import org.opensaml.soap.wssecurity.Username;
import org.opensaml.soap.wssecurity.UsernameToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

/**
 * WSSecurityObjectsTestCase is the base test case for the WS-Security
 * objects.
 * 
 */
@SuppressWarnings("javadoc")
public class WSSecurityObjectsTestCase extends WSBaseTestCase {

    public Logger log= LoggerFactory.getLogger(WSSecurityObjectsTestCase.class);

    protected void unmarshallAndMarshall(String filename) throws Exception {
        // TODO implementation
    }

    @Test
    public void testBinarySecurityToken() throws Exception {
        BinarySecurityToken token= buildXMLObject(BinarySecurityToken.ELEMENT_NAME);
        token.setWSUId("BinarySecurityToken-" + System.currentTimeMillis());
        token.setValue("Base64Encoded_X509_CERTIFICATE...");
        token.setValueType("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
        // check default encoding type
        Assert.assertEquals(token.getEncodingType(), BinarySecurityToken.ENCODING_TYPE_BASE64_BINARY);
    
        marshallAndUnmarshall(token);
    
    }
    
    @Test
    public void testCreated() throws Exception {
        //TODO
    }

    @Test
    public void testEmbedded() throws Exception {
        Embedded embedded= buildXMLObject(Embedded.ELEMENT_NAME);
    
        UsernameToken usernameToken= createUsernameToken("EmbeddedUT",
                                                         "EmbeddedUT");
    
        embedded.setValueType("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#UsernameToken");
        embedded.getUnknownXMLObjects().add(usernameToken);
    
        marshallAndUnmarshall(embedded);
    
    }

    @Test
    public void testEncryptedHeader() throws Exception {
        EncryptedHeader eh = buildXMLObject(EncryptedHeader.ELEMENT_NAME);
        eh.setWSUId("abc123");
        eh.setSOAP11MustUnderstand(true);
        eh.setSOAP11Actor("urn:test:soap11actor");
        eh.setSOAP12MustUnderstand(true);
        eh.setSOAP12Role("urn:test:soap12role");
        eh.setSOAP12Relay(true);
        marshallAndUnmarshall(eh);
    }

    @Test
    public void testExpires() throws Exception {
        //TODO
    }

    @Test
    public void testIteration() throws Exception {
        Iteration iteration= buildXMLObject(Iteration.ELEMENT_NAME);
        iteration.setValue(Integer.valueOf(1000));
        marshallAndUnmarshall(iteration);
    }
    
    @Test
    public void testKeyIdentifier() throws Exception {
        //TODO
    }

    @Test
    public void testNonce() throws Exception {
        Nonce nonce= buildXMLObject(Nonce.ELEMENT_NAME);
        nonce.setValue("Base64EncodedValue...");
        marshallAndUnmarshall(nonce);
    }

    @Test
    public void testPassword() throws Exception {
    
        Password password= buildXMLObject(Password.ELEMENT_NAME);
        password.setValue("test");
        // check default
        Assert.assertEquals(password.getType(), Password.TYPE_PASSWORD_TEXT);
        marshallAndUnmarshall(password);
    }

    @Test
    public void testReference() throws Exception {
        Reference reference= buildXMLObject(Reference.ELEMENT_NAME);
    
        reference.setValueType("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#UsernameToken");
        reference.setURI("#UsernameToken-0000001");
    
        marshallAndUnmarshall(reference);
    }

    @Test
    public void testSalt() throws Exception {
        Salt salt= buildXMLObject(Salt.ELEMENT_NAME);
        salt.setValue("Base64Encoded_Salt_VALUE...");
        marshallAndUnmarshall(salt);
    }

    @Test
    public void testSecurity() throws Exception {
        //TODO
    }
    
    @Test
    public void testSecurityTokenReference() throws Exception {
        //TODO
    }
    
    @Test
    public void testSignatureConfirmation() throws Exception {
        //TODO
    }

    @Test
    public void testTimestamp() throws Exception {
        Timestamp timestamp= buildXMLObject(Timestamp.ELEMENT_NAME);
        Created created= buildXMLObject(Created.ELEMENT_NAME);
        Instant now= Instant.now();
        created.setDateTime(now);
        timestamp.setCreated(created);

        Expires expires= buildXMLObject(Expires.ELEMENT_NAME);
        expires.setDateTime(now.plus(10, ChronoUnit.MINUTES));
        timestamp.setExpires(expires);

        timestamp.setWSUId("Timestamp-" + System.currentTimeMillis());

        marshallAndUnmarshall(timestamp);
    }
    
    @Test
    public void testTransformationParameters() throws Exception {
        //TODO
    }

    @Test
    public void testUsername() throws Exception {
        Username username= buildXMLObject(Username.ELEMENT_NAME);
        username.setValue("test");
        marshallAndUnmarshall(username);
    }

    @Test
    public void testUsernameToken() throws Exception {
        String refId= "UsernameToken-007";
        String refDateTimeStr= "2007-12-19T09:53:08.335Z";

        final UsernameToken usernameToken= createUsernameToken("test", "test");
        usernameToken.setWSUId(refId);
        final Instant refDateTime= Instant.parse(refDateTimeStr);
        final Created usernameCreated = (Created) usernameToken.getUnknownXMLObjects(Created.ELEMENT_NAME).get(0);
        usernameCreated.setDateTime(refDateTime);

        // check default password type
        final Password password= (Password) usernameToken.getUnknownXMLObjects(Password.ELEMENT_NAME).get(0);
        Assert.assertNotNull(password);
        Assert.assertEquals(password.getType(), Password.TYPE_PASSWORD_TEXT);

        final List<XMLObject> children= usernameToken.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 3);

        marshallAndUnmarshall(usernameToken);

        // TODO impl unmarshallAndMarshall method
        // UsernameToken refUsernameToken=
        // unmarshallXML("/data/usernametoken.xml");
        // Document refDocument= refUsernameToken.getDOM().getOwnerDocument();
        // refUsernameToken.releaseDOM();
        final Element refElement = parseXMLDocument("/org/opensaml/soap/wssecurity/impl/UsernameToken.xml").getDocumentElement();
        //System.out.println("XXX: " + XMLHelper.nodeToString(refDocument.getDocumentElement()));

        final Marshaller marshaller= getMarshaller(usernameToken);
        final Element element= marshaller.marshall(usernameToken);
        
        // compare with XMLUnit
        final Diff diff = DiffBuilder.compare(refElement).withTest(element).checkForIdentical().ignoreWhitespace().build();
        Assert.assertFalse(diff.hasDifferences(), diff.toString());

        // unmarshall directly from file
        final UsernameToken ut= unmarshallElement("/org/opensaml/soap/wssecurity/impl/UsernameToken.xml");
        assert ut != null;
        final Username u = ut.getUsername();
        assert u != null;
        Assert.assertEquals(u.getValue(), "test");
        final Password utPassword = (Password) ut.getUnknownXMLObjects(Password.ELEMENT_NAME).get(0);
        Assert.assertNotNull(utPassword);
        Assert.assertEquals(utPassword.getValue(), "test");
        final Created utCreated = (Created) ut.getUnknownXMLObjects(Created.ELEMENT_NAME).get(0);
        Assert.assertNotNull(utCreated);
        final Instant created= utCreated.getDateTime();
        System.out.println(created);

    }

    protected UsernameToken createUsernameToken(String user, String pass)
            throws Exception {
        UsernameToken usernameToken= buildXMLObject(UsernameToken.ELEMENT_NAME);
        Username username= buildXMLObject(Username.ELEMENT_NAME);
        username.setValue(user);
        Password password= buildXMLObject(Password.ELEMENT_NAME);
        password.setValue(pass);
        Created created= buildXMLObject(Created.ELEMENT_NAME);
        created.setDateTime(Instant.now());

        String id= "UsernameToken-" + System.currentTimeMillis();
        usernameToken.setWSUId(id);
        usernameToken.setUsername(username);
        usernameToken.getUnknownXMLObjects().add(password);
        usernameToken.getUnknownXMLObjects().add(created);

        return usernameToken;

    }

}
