/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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

import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.testing.BaseComplexSAMLObjectTestCase;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;

/**
 * Tests unmarshalling and marshalling for various response messages.
 */
public class ResponseSuccessAuthnAttribTest extends BaseComplexSAMLObjectTestCase {
    
    /**
     * Constructor
     */
    public ResponseSuccessAuthnAttribTest(){
        elementFile = "/org/opensaml/saml/saml2/core/ResponseSuccessAuthnAttrib.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testUnmarshall() {
        final Response response = (Response) unmarshallElement(elementFile);
        assert response != null;
        
        Assert.assertEquals(response.getID(), "_c7055387-af61-4fce-8b98-e2927324b306", "Response ID");
        Assert.assertEquals(response.getInResponseTo(), "_abcdef123456", "InResponseTo");
        Assert.assertEquals(response.getVersion(), SAMLVersion.VERSION_20, "Version");
        Assert.assertEquals(response.getIssueInstant(), Instant.parse("2006-01-26T13:35:05.000Z"), "IssueInstant");
        
        final Issuer issuer = response.getIssuer();
        assert issuer != null;
        Assert.assertEquals(issuer.getFormat(), NameIDType.ENTITY, "Issuer/@Format");
        
        final Status status = response.getStatus();
        assert status != null;
        final StatusCode code = status.getStatusCode();
        assert code != null;
        Assert.assertEquals(code.getValue(), StatusCode.SUCCESS, "Status/Statuscode/@Value");
        
        final Assertion assertion = response.getAssertions().get(0);
        Assert.assertNotNull(assertion, "Assertion[0] was null");
        Assert.assertEquals(assertion.getID(), "_a75adf55-01d7-40cc-929f-dbd8372ebdfc", "Assertion ID");
        Assert.assertEquals(assertion.getIssueInstant(), Instant.parse("2006-01-26T13:35:05.000Z"), "Assertion/@IssueInstant");
        Assert.assertEquals(assertion.getVersion(), SAMLVersion.VERSION_20, "Assertion/@Version");
        
        final Issuer aissuer = assertion.getIssuer();
        assert aissuer != null;
        Assert.assertEquals(aissuer.getFormat(), NameIDType.ENTITY, "Assertion/Issuer/@Format");
        
        final Subject subject = assertion.getSubject();
        assert subject != null;
        final NameID nameID = subject.getNameID();
        assert nameID != null;
        Assert.assertEquals(nameID.getFormat(), NameIDType.TRANSIENT, "Assertion/Subject/NameID/@Format");
        Assert.assertEquals(nameID.getValue(), "_820d2843-2342-8236-ad28-8ac94fb3e6a1", "Assertion/Subject/NameID contents");
        
        final SubjectConfirmation sc = subject.getSubjectConfirmations().get(0);
        Assert.assertEquals(sc.getMethod(), SubjectConfirmation.METHOD_BEARER, "Assertion/Subject/SubjectConfirmation/@Method");
        
        final Conditions cond = assertion.getConditions();
        assert cond != null;
        Assert.assertEquals(cond.getNotBefore(), Instant.parse("2006-01-26T13:35:05.000Z"), "Assertion/Condition/@NotBefore");
        Assert.assertEquals(cond.getNotOnOrAfter(), Instant.parse("2006-01-26T13:45:05.000Z"), "Assertion/Condition/@NotOnOrAfter");
        Audience audience = cond.getAudienceRestrictions().get(0).getAudiences().get(0);
        Assert.assertEquals(audience.getURI(), "https://sp.example.org", "Assertion/Conditions/AudienceRestriction/Audience contents");
        
        final AuthnStatement authnStatement = assertion.getAuthnStatements().get(0);
        Assert.assertEquals(authnStatement.getAuthnInstant(), Instant.parse("2006-01-26T13:35:05.000Z"), "Assertion/AuthnStatement/@AuthnInstant");
        final AuthnContext ac = authnStatement.getAuthnContext();
        assert ac != null;
        final AuthnContextClassRef acref = ac.getAuthnContextClassRef();
        assert acref != null;
        Assert.assertEquals(acref.getURI(), AuthnContext.PPT_AUTHN_CTX, "Assertion/AuthnStatement/AuthnContext/AuthnContextClassRef contents");
        
        AttributeStatement  attribStatement = assertion.getAttributeStatements().get(0);
        Attribute attrib = null;
        XSString value = null;
        
        attrib = attribStatement.getAttributes().get(0);
        Assert.assertEquals(attrib.getFriendlyName(), "fooAttrib", "Attribute/@FriendlyName");
        Assert.assertEquals(attrib.getName(), "urn:foo:attrib", "Attribute/@Name");
        Assert.assertEquals(attrib.getNameFormat(), Attribute.URI_REFERENCE, "Attribute/@NameFormat");
        Assert.assertEquals(attrib.getAttributeValues().size(), 2, "Number of fooAttrib AttributeValues");
        value = (XSString) attrib.getAttributeValues().get(0);
        Assert.assertEquals(value.getValue(), "SomeValue", "Attribute content");
        value = (XSString) attrib.getAttributeValues().get(1);
        Assert.assertEquals(value.getValue(), "SomeOtherValue", "Attribute content");
        
        attrib = attribStatement.getAttributes().get(1);
        Assert.assertEquals(attrib.getFriendlyName(), "eduPersonPrincipalName", "Attribute/@FriendlyName");
        Assert.assertEquals(attrib.getName(), "urn:oid:1.3.6.1.4.1.5923.1.1.1.6", "Attribute/@Name");
        Assert.assertEquals(attrib.getNameFormat(), Attribute.URI_REFERENCE, "Attribute/@NameFormat");
        Assert.assertEquals(attrib.getAttributeValues().size(), 1, "Number of ldapAttrib AttributeValues");
        value = (XSString) attrib.getAttributeValues().get(0);
        Assert.assertEquals(value.getValue(), "j.doe@idp.example.org", "Attribute content");
    }

    /** {@inheritDoc} */
    @Test
    public void testMarshall(){
        Response response = (Response) buildXMLObject(Response.DEFAULT_ELEMENT_NAME);
        response.setID("_c7055387-af61-4fce-8b98-e2927324b306");
        response.setInResponseTo("_abcdef123456");
        response.setIssueInstant(Instant.parse("2006-01-26T13:35:05.000Z"));
        
        Issuer rIssuer = (Issuer) buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
        rIssuer.setFormat(NameIDType.ENTITY);
        rIssuer.setValue("https://idp.example.org");
        
        Status status = (Status) buildXMLObject(Status.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = (StatusCode) buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue(StatusCode.SUCCESS);
        
        Assertion assertion = (Assertion) buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME);
        assertion.setID("_a75adf55-01d7-40cc-929f-dbd8372ebdfc");
        assertion.setIssueInstant(Instant.parse("2006-01-26T13:35:05.000Z"));
        
        Issuer aIssuer = (Issuer) buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
        aIssuer.setFormat(NameIDType.ENTITY);
        aIssuer.setValue("https://idp.example.org");
        
        Subject subject = (Subject) buildXMLObject(Subject.DEFAULT_ELEMENT_NAME);
        NameID nameID = (NameID) buildXMLObject(NameID.DEFAULT_ELEMENT_NAME);
        nameID.setFormat(NameIDType.TRANSIENT);
        nameID.setValue("_820d2843-2342-8236-ad28-8ac94fb3e6a1");
        
        SubjectConfirmation subjectConfirmation = (SubjectConfirmation) buildXMLObject(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
        
        Conditions conditions = (Conditions) buildXMLObject(Conditions.DEFAULT_ELEMENT_NAME);
        conditions.setNotBefore(Instant.parse("2006-01-26T13:35:05.000Z"));
        conditions.setNotOnOrAfter(Instant.parse("2006-01-26T13:45:05.000Z"));
        
        AudienceRestriction audienceRestriction = (AudienceRestriction) buildXMLObject(AudienceRestriction.DEFAULT_ELEMENT_NAME);
        Audience audience = (Audience) buildXMLObject(Audience.DEFAULT_ELEMENT_NAME);
        audience.setURI("https://sp.example.org");
        
        AuthnStatement authnStatement = (AuthnStatement) buildXMLObject(AuthnStatement.DEFAULT_ELEMENT_NAME);
        authnStatement.setAuthnInstant(Instant.parse("2006-01-26T13:35:05.000Z"));
        
        AuthnContext authnContext = (AuthnContext) buildXMLObject(AuthnContext.DEFAULT_ELEMENT_NAME);
        AuthnContextClassRef classRef = (AuthnContextClassRef) buildXMLObject(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        classRef.setURI(AuthnContext.PPT_AUTHN_CTX);
        
        AttributeStatement attribStatement = (AttributeStatement) buildXMLObject(AttributeStatement.DEFAULT_ELEMENT_NAME);
        XMLObjectBuilder<XSString> stringBuilder = builderFactory.ensureBuilder(XSString.TYPE_NAME);
        
        Attribute fooAttrib = (Attribute) buildXMLObject(Attribute.DEFAULT_ELEMENT_NAME);
        fooAttrib.setFriendlyName("fooAttrib");
        fooAttrib.setName("urn:foo:attrib");
        fooAttrib.setNameFormat(Attribute.URI_REFERENCE);
        XSString fooAttribValue = null;
        fooAttribValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        fooAttribValue.setValue("SomeValue");
        fooAttrib.getAttributeValues().add(fooAttribValue);
        fooAttribValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        fooAttribValue.setValue("SomeOtherValue");
        fooAttrib.getAttributeValues().add(fooAttribValue);
        
        Attribute ldapAttrib = (Attribute) buildXMLObject(Attribute.DEFAULT_ELEMENT_NAME);
        ldapAttrib.setFriendlyName("eduPersonPrincipalName");
        ldapAttrib.setName("urn:oid:1.3.6.1.4.1.5923.1.1.1.6");
        ldapAttrib.setNameFormat(Attribute.URI_REFERENCE);
        XSString ldapAttribValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        ldapAttribValue.setValue("j.doe@idp.example.org");
        ldapAttrib.getAttributeValues().add(ldapAttribValue);
        
        response.setIssuer(rIssuer);
        status.setStatusCode(statusCode);
        response.setStatus(status);
        response.getAssertions().add(assertion);
        
        assertion.setIssuer(aIssuer);
        subject.setNameID(nameID);
        subject.getSubjectConfirmations().add(subjectConfirmation);
        assertion.setSubject(subject);
        
        audienceRestriction.getAudiences().add(audience);
        conditions.getAudienceRestrictions().add(audienceRestriction);
        assertion.setConditions(conditions);
        
        authnContext.setAuthnContextClassRef(classRef);
        authnStatement.setAuthnContext(authnContext);
        assertion.getAuthnStatements().add(authnStatement);
        
        attribStatement.getAttributes().add(fooAttrib);
        attribStatement.getAttributes().add(ldapAttrib);
        assertion.getAttributeStatements().add(attribStatement);

        assertXMLEquals("Marshalled Response was not the expected value", expectedDOM, response);
    }
    
}