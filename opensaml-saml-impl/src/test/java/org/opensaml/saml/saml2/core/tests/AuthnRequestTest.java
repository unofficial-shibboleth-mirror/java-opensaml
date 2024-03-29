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

package org.opensaml.saml.saml2.core.tests;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.time.Instant;

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.testing.BaseComplexSAMLObjectTestCase;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.Subject;

/**
 * Tests unmarshalling and marshalling for various request messages.
 */
public class AuthnRequestTest extends BaseComplexSAMLObjectTestCase {

    /**
     * Constructor
     */
    public AuthnRequestTest(){
        elementFile = "/org/opensaml/saml/saml2/core/AuthnRequest.xml";
    }
    

    /** {@inheritDoc} */
    @Test
    public void testUnmarshall() {
        final AuthnRequest request = (AuthnRequest) unmarshallElement(elementFile);
        assert request != null;
        
        Assert.assertEquals(request.isForceAuthn(), Boolean.TRUE, "ForceAuthn");
        Assert.assertEquals(request.getAssertionConsumerServiceURL(), "http://www.example.com/", "AssertionConsumerServiceURL");
        Assert.assertEquals(request.getAttributeConsumingServiceIndex(), 0, "AttributeConsumingServiceIndex");
        Assert.assertEquals(request.getProviderName(), "SomeProvider", "ProviderName");
        Assert.assertEquals(request.getID(), "abe567de6", "ID");
        Assert.assertEquals(request.getVersion(), SAMLVersion.VERSION_20, "Version");
        Assert.assertEquals(request.getIssueInstant(), Instant.parse("2005-01-31T12:00:00.000Z"), "IssueInstant");
        Assert.assertEquals(request.getDestination(), "http://www.example.com/", "Destination");
        Assert.assertEquals(request.getConsent(), RequestAbstractType.OBTAINED_CONSENT, "Consent");
        
        final Subject subject = request.getSubject();
        assert subject != null;
        final NameID nameID = subject.getNameID();
        assert nameID != null;
        Assert.assertEquals(nameID.getFormat(), NameIDType.EMAIL, "Subject/NameID/@NameIdFormat");
        Assert.assertEquals(nameID.getValue(), "j.doe@company.com", "Subject/NameID contents");
        
        final Conditions cond = request.getConditions();
        assert cond != null;
        final Audience audience = cond.getAudienceRestrictions().get(0).getAudiences().get(0);
        Assert.assertEquals(audience.getURI(), "urn:foo:sp.example.org", "Conditions/AudienceRestriction[1]/Audience[1] contents");
        
        final RequestedAuthnContext rac = request.getRequestedAuthnContext();
        assert rac != null;
        final AuthnContextClassRef classRef = rac.getAuthnContextClassRefs().get(0);
        Assert.assertEquals(classRef.getURI(), AuthnContext.PPT_AUTHN_CTX, "RequestedAuthnContext/AuthnContextClassRef[1] contents");
    }

    /** {@inheritDoc} */
    @Test
    public void testMarshall() {
        NameID nameid = (NameID) buildXMLObject(NameID.DEFAULT_ELEMENT_NAME);
        nameid.setFormat(NameIDType.EMAIL);
        nameid.setValue("j.doe@company.com");
        
        Subject subject = (Subject) buildXMLObject(Subject.DEFAULT_ELEMENT_NAME);
        subject.setNameID(nameid);
        
        Audience audience = (Audience) buildXMLObject(Audience.DEFAULT_ELEMENT_NAME);
        audience.setURI("urn:foo:sp.example.org");
        
        AudienceRestriction ar = (AudienceRestriction) buildXMLObject(AudienceRestriction.DEFAULT_ELEMENT_NAME);
        ar.getAudiences().add(audience);
        
        Conditions conditions = (Conditions) buildXMLObject(Conditions.DEFAULT_ELEMENT_NAME);
        conditions.getAudienceRestrictions().add(ar);
        
        AuthnContextClassRef classRef = (AuthnContextClassRef) buildXMLObject(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        classRef.setURI(AuthnContext.PPT_AUTHN_CTX);
        
        RequestedAuthnContext rac = (RequestedAuthnContext) buildXMLObject(RequestedAuthnContext.DEFAULT_ELEMENT_NAME);
        rac.getAuthnContextClassRefs().add(classRef);
        
        AuthnRequest request = (AuthnRequest) buildXMLObject(AuthnRequest.DEFAULT_ELEMENT_NAME);
        request.setSubject(subject);
        request.setConditions(conditions);
        request.setRequestedAuthnContext(rac);
        
        request.setForceAuthn(XSBooleanValue.valueOf("true"));
        request.setAssertionConsumerServiceURL("http://www.example.com/");
        request.setAttributeConsumingServiceIndex(0);
        request.setProviderName("SomeProvider");
        request.setID("abe567de6");
        request.setVersion(SAMLVersion.VERSION_20);
        request.setIssueInstant(Instant.parse("2005-01-31T12:00:00.000Z"));
        request.setDestination("http://www.example.com/");
        request.setConsent(RequestAbstractType.OBTAINED_CONSENT);
        
        assertXMLEquals("Marshalled AuthnRequest", expectedDOM, request);
    }

}