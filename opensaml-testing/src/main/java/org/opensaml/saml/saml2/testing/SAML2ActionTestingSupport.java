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

package org.opensaml.saml.saml2.testing;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Artifact;
import org.opensaml.saml.saml2.core.ArtifactResolve;
import org.opensaml.saml.saml2.core.ArtifactResponse;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.IDPEntry;
import org.opensaml.saml.saml2.core.IDPList;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeQuery;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Scoping;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.core.SubjectLocality;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Helper methods for creating/testing SAML 2 objects within profile action tests. When methods herein refer to mock
 * objects they are always objects that have been created via Mockito unless otherwise noted.
 */
public class SAML2ActionTestingSupport {

    /** ID used for all generated {@link Response} objects. */
    @Nonnull public static final  String REQUEST_ID = "request";

    /** ID used for all generated {@link Response} objects. */
    @Nonnull public static final String RESPONSE_ID = "response";

    /** ID used for all generated {@link Assertion} objects. */
    @Nonnull public static final String ASSERTION_ID = "assertion";

    /**
     * Builds an empty response. The ID of the message is {@link ActionTestingSupport#OUTBOUND_MSG_ID}, the issue
     * instant is 1970-01-01T00:00:00Z and the SAML version is {@link SAMLVersion#VERSION_11}.
     * 
     * @return the constructed response
     */
    @Nonnull public static Response buildResponse() {
        final SAMLObjectBuilder<Response> responseBuilder = (SAMLObjectBuilder<Response>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Response>ensureBuilder(
                        Response.DEFAULT_ELEMENT_NAME);

        final Response response = responseBuilder.buildObject();
        response.setID(ActionTestingSupport.OUTBOUND_MSG_ID);
        response.setIssueInstant(Instant.EPOCH);
        response.setVersion(SAMLVersion.VERSION_20);

        return response;
    }

    /**
     * Builds an empty artifact response. The ID of the message is {@link ActionTestingSupport#OUTBOUND_MSG_ID},
     * the issue instant is 1970-01-01T00:00:00Z and the SAML version is {@link SAMLVersion#VERSION_11}.
     * 
     * @return the constructed response
     */
    @Nonnull public static ArtifactResponse buildArtifactResponse() {
        final SAMLObjectBuilder<ArtifactResponse> responseBuilder = (SAMLObjectBuilder<ArtifactResponse>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<ArtifactResponse>ensureBuilder(
                        ArtifactResponse.DEFAULT_ELEMENT_NAME);

        final ArtifactResponse response = responseBuilder.buildObject();
        response.setID(ActionTestingSupport.OUTBOUND_MSG_ID);
        response.setIssueInstant(Instant.ofEpochMilli(0));
        response.setVersion(SAMLVersion.VERSION_20);

        return response;
    }
    
    /**
     * Builds an {@link LogoutRequest}. If a {@link NameID} is given, it will be added to the constructed
     * {@link LogoutRequest}.
     * 
     * @param name the NameID to add to the request
     * 
     * @return the built request
     */
    @Nonnull public static LogoutRequest buildLogoutRequest(final @Nullable NameID name) {
        final SAMLObjectBuilder<Issuer> issuerBuilder = (SAMLObjectBuilder<Issuer>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Issuer>ensureBuilder(
                        Issuer.DEFAULT_ELEMENT_NAME);

        final SAMLObjectBuilder<LogoutRequest> reqBuilder = (SAMLObjectBuilder<LogoutRequest>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<LogoutRequest>ensureBuilder(
                        LogoutRequest.DEFAULT_ELEMENT_NAME);

        final Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue(ActionTestingSupport.INBOUND_MSG_ISSUER);

        final LogoutRequest req = reqBuilder.buildObject();
        req.setID(REQUEST_ID);
        req.setIssueInstant(Instant.EPOCH);
        req.setIssuer(issuer);
        req.setVersion(SAMLVersion.VERSION_20);

        if (name != null) {
            req.setNameID(name);
        }

        return req;
    }
    
    /**
     * Builds an empty logout response. The ID of the message is {@link ActionTestingSupport#OUTBOUND_MSG_ID}, the issue
     * instant is 1970-01-01T00:00:00Z and the SAML version is {@link SAMLVersion#VERSION_11}.
     * 
     * @return the constructed response
     */
    @Nonnull public static LogoutResponse buildLogoutResponse() {
        final SAMLObjectBuilder<LogoutResponse> responseBuilder = (SAMLObjectBuilder<LogoutResponse>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<LogoutResponse>ensureBuilder(
                        LogoutResponse.DEFAULT_ELEMENT_NAME);

        final LogoutResponse response = responseBuilder.buildObject();
        response.setID(ActionTestingSupport.OUTBOUND_MSG_ID);
        response.setIssueInstant(Instant.EPOCH);
        response.setVersion(SAMLVersion.VERSION_20);

        return response;
    }
    
    /**
     * Builds an empty assertion. The ID of the message is {@link #ASSERTION_ID}, the issue instant is
     * 1970-01-01T00:00:00Z and the SAML version is {@link SAMLVersion#VERSION_11}.
     * 
     * @return the constructed assertion
     */
    @Nonnull public static Assertion buildAssertion() {
        final SAMLObjectBuilder<Assertion> assertionBuilder = (SAMLObjectBuilder<Assertion>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Assertion>ensureBuilder(
                        Assertion.DEFAULT_ELEMENT_NAME);

        final Assertion assertion = assertionBuilder.buildObject();
        assertion.setID(ASSERTION_ID);
        assertion.setIssueInstant(Instant.EPOCH);
        assertion.setVersion(SAMLVersion.VERSION_20);

        return assertion;
    }

    /**
     * Builds an authentication statement. The authn instant is set to 1970-01-01T00:00:00Z.
     * 
     * @return the constructed statement
     */
    @Nonnull public static AuthnStatement buildAuthnStatement() {
        final SAMLObjectBuilder<AuthnStatement> statementBuilder = (SAMLObjectBuilder<AuthnStatement>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<AuthnStatement>ensureBuilder(
                        AuthnStatement.DEFAULT_ELEMENT_NAME);

        final AuthnStatement statement = statementBuilder.buildObject();
        statement.setAuthnInstant(Instant.EPOCH);

        return statement;
    }

    /**
     * Builds an authentication statement with specified timestamp and subject locality data
     * and context class ref.
     * 
     * @param ts authn time
     * @param address client address
     * @param classRef authentication context class ref
     * 
     * @return the constructed statement
     * 
     * @since 5.2.0
     */
    @Nonnull public static AuthnStatement buildAuthnStatement(@Nonnull final Instant ts, @Nonnull final String address,
            @Nonnull final String classRef) {
        final SAMLObjectBuilder<AuthnStatement> statementBuilder = (SAMLObjectBuilder<AuthnStatement>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<AuthnStatement>ensureBuilder(
                        AuthnStatement.DEFAULT_ELEMENT_NAME);
        final SAMLObjectBuilder<SubjectLocality> localityBuilder = (SAMLObjectBuilder<SubjectLocality>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<SubjectLocality>ensureBuilder(
                        SubjectLocality.DEFAULT_ELEMENT_NAME);
        final SAMLObjectBuilder<AuthnContext> contextBuilder = (SAMLObjectBuilder<AuthnContext>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<AuthnContext>ensureBuilder(
                        AuthnContext.DEFAULT_ELEMENT_NAME);
        final SAMLObjectBuilder<AuthnContextClassRef> classBuilder = (SAMLObjectBuilder<AuthnContextClassRef>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<AuthnContextClassRef>ensureBuilder(
                        AuthnContextClassRef.DEFAULT_ELEMENT_NAME);

        final SubjectLocality locality = localityBuilder.buildObject();
        locality.setAddress(address);
        
        final AuthnContextClassRef ref = classBuilder.buildObject();
        ref.setURI(classRef);
        
        final AuthnContext ac = contextBuilder.buildObject();
        ac.setAuthnContextClassRef(ref);
        
        final AuthnStatement statement = statementBuilder.buildObject();
        statement.setAuthnInstant(ts);
        statement.setSubjectLocality(locality);
        statement.setAuthnContext(ac);

        return statement;
    }

    /**
     * Builds an empty attribute statement.
     * 
     * @return the constructed statement
     */
    @Nonnull public static AttributeStatement buildAttributeStatement() {
        final SAMLObjectBuilder<AttributeStatement> statementBuilder = (SAMLObjectBuilder<AttributeStatement>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<AttributeStatement>ensureBuilder(
                        AttributeStatement.DEFAULT_ELEMENT_NAME);

        return statementBuilder.buildObject();
    }
    
    /**
     * Builds an attribute and values.
     * 
     * @param name attribute name
     * @param format name format
     * @param values value collection
     * 
     * @return the constructed attribute
     * 
     * @since 5.2.0
     */
    @Nonnull public static Attribute buildAttribute(@Nonnull final String name, @Nullable final String format,
            @Nonnull final Collection<String> values) {
        final SAMLObjectBuilder<Attribute> attributeBuilder = (SAMLObjectBuilder<Attribute>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Attribute>ensureBuilder(
                        Attribute.DEFAULT_ELEMENT_NAME);
        final Attribute attr = attributeBuilder.buildObject();
        attr.setName(name);
        attr.setNameFormat(format);

        final SAMLObjectBuilder<AttributeValue> valueBuilder = (SAMLObjectBuilder<AttributeValue>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<AttributeValue>ensureBuilder(
                        AttributeValue.DEFAULT_ELEMENT_NAME);
        
        values.forEach(v -> {
            final AttributeValue val = valueBuilder.buildObject();
            val.setTextContent(v);
            attr.getAttributeValues().add(val);
        });
        
        return attr;
    }

    /**
     * Builds a {@link Subject}. If a principal name is given a {@link NameID}, whose value is the given principal name,
     * will be created and added to the {@link Subject}.
     * 
     * @param principalName the principal name to add to the subject
     * 
     * @return the built subject
     */
    @Nonnull public static Subject buildSubject(final @Nullable String principalName) {
        final SAMLObjectBuilder<Subject> subjectBuilder = (SAMLObjectBuilder<Subject>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Subject>ensureBuilder(
                        Subject.DEFAULT_ELEMENT_NAME);
        final Subject subject = subjectBuilder.buildObject();

        if (principalName != null) {
            subject.setNameID(buildNameID(principalName));
        }

        return subject;
    }
    
    /**
     * Builds a {@link NameID}.
     * 
     * @param principalName the principal name to use in the NameID
     * 
     * @return the built NameID
     */
    @Nonnull public static NameID buildNameID(final @Nonnull @NotEmpty String principalName) {
        final SAMLObjectBuilder<NameID> nameIdBuilder = (SAMLObjectBuilder<NameID>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<NameID>ensureBuilder(
                        NameID.DEFAULT_ELEMENT_NAME);
        final NameID nameId = nameIdBuilder.buildObject();
        nameId.setValue(principalName);
        return nameId;
    }

    /**
     * Builds a {@link SubjectConfirmation}.
     * 
     * @param method confirmation method
     * @param recipient recipient value
     * @param address confirmation address
     * 
     * @return the built subject confirmation
     * 
     * @since 5.2.0
     */
    @Nonnull public static SubjectConfirmation buildSubjectConfirmation(@Nullable final String method,
            @Nullable final String recipient, @Nullable final String address) {
        final SAMLObjectBuilder<SubjectConfirmation> confBuilder = (SAMLObjectBuilder<SubjectConfirmation>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<SubjectConfirmation>ensureBuilder(
                        SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        final SAMLObjectBuilder<SubjectConfirmationData> dataBuilder = (SAMLObjectBuilder<SubjectConfirmationData>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<SubjectConfirmationData>ensureBuilder(
                        SubjectConfirmationData.DEFAULT_ELEMENT_NAME);

        final SubjectConfirmation conf = confBuilder.buildObject();
        conf.setMethod(method);

        if (method != null || recipient != null || address != null) {
            final SubjectConfirmationData data = dataBuilder.buildObject();
            data.setRecipient(recipient);
            data.setAddress(address);
            data.setNotBefore(Instant.now().minusSeconds(60));
            data.setNotOnOrAfter(Instant.now().plusSeconds(180));
            conf.setSubjectConfirmationData(data);
        }
        
        return conf;
    }
    
    /**
     * Builds a {@link Issuer}.
     * 
     * @param entityID the entity ID to use in the Issuer
     * 
     * @return the built Issuer
     */
    @Nonnull public static Issuer buildIssuer(final @Nonnull @NotEmpty String entityID) {
        final SAMLObjectBuilder<Issuer> issuerBuilder = (SAMLObjectBuilder<Issuer>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Issuer>ensureBuilder(
                        Issuer.DEFAULT_ELEMENT_NAME);
        final Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue(entityID);
        return issuer;
    }
    
    /**
     * Builds a {@link Conditions}, with optional content.
     * 
     * @param notBefore the NotBefore to set
     * @param notOnOrAfter the NotOnOrAfter to set
     * @param audience audience to place into {@link AudienceRestriction}
     * 
     * @return the object
     * 
     * @since 5.2.0
     */
    @Nonnull public static Conditions buildConditions(@Nullable final Instant notBefore,
            @Nullable final Instant notOnOrAfter, @Nullable final String audience) {
        final SAMLObjectBuilder<Conditions> conditionsBuilder = (SAMLObjectBuilder<Conditions>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Conditions>ensureBuilder(
                        Conditions.DEFAULT_ELEMENT_NAME);
        final SAMLObjectBuilder<AudienceRestriction> audienceCondBuilder = (SAMLObjectBuilder<AudienceRestriction>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<AudienceRestriction>ensureBuilder(
                        AudienceRestriction.DEFAULT_ELEMENT_NAME);
        final SAMLObjectBuilder<Audience> audienceBuilder = (SAMLObjectBuilder<Audience>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Audience>ensureBuilder(
                        Audience.DEFAULT_ELEMENT_NAME);
        
        final Conditions conditions = conditionsBuilder.buildObject();
        
        if (notBefore != null) {
            conditions.setNotBefore(notBefore);
        }
        
        if (notOnOrAfter != null) {
            conditions.setNotOnOrAfter(notOnOrAfter);
        }
        
        if (audience != null) {
            final Audience aud = audienceBuilder.buildObject();
            aud.setURI(audience);
            final AudienceRestriction cond = audienceCondBuilder.buildObject();
            cond.getAudiences().add(aud);
            conditions.getAudienceRestrictions().add(cond);
        }
        
        return conditions;
    }
    
    /**
     * Builds an {@link AttributeQuery}. If a {@link Subject} is given, it will be added to the constructed
     * {@link AttributeQuery}.
     * 
     * @param subject the subject to add to the query
     * 
     * @return the built query
     */
    @Nonnull public static AttributeQuery buildAttributeQueryRequest(final @Nullable Subject subject) {
        final SAMLObjectBuilder<Issuer> issuerBuilder = (SAMLObjectBuilder<Issuer>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Issuer>ensureBuilder(
                        Issuer.DEFAULT_ELEMENT_NAME);

        final SAMLObjectBuilder<AttributeQuery> queryBuilder = (SAMLObjectBuilder<AttributeQuery>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<AttributeQuery>ensureBuilder(
                        AttributeQuery.DEFAULT_ELEMENT_NAME);

        final Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue(ActionTestingSupport.INBOUND_MSG_ISSUER);

        final AttributeQuery query = queryBuilder.buildObject();
        query.setID(REQUEST_ID);
        query.setIssueInstant(Instant.EPOCH);
        query.setIssuer(issuer);
        query.setVersion(SAMLVersion.VERSION_20);

        if (subject != null) {
            query.setSubject(subject);
        }

        return query;
    }

    /**
     * Builds an {@link AuthnRequest}.
     * 
     * @return the built request
     */
    @Nonnull public static AuthnRequest buildAuthnRequest() {
        final SAMLObjectBuilder<Issuer> issuerBuilder = (SAMLObjectBuilder<Issuer>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Issuer>ensureBuilder(
                        Issuer.DEFAULT_ELEMENT_NAME);

        final SAMLObjectBuilder<AuthnRequest> requestBuilder = (SAMLObjectBuilder<AuthnRequest>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<AuthnRequest>ensureBuilder(
                        AuthnRequest.DEFAULT_ELEMENT_NAME);

        final Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue(ActionTestingSupport.INBOUND_MSG_ISSUER);

        final AuthnRequest request = requestBuilder.buildObject();
        request.setID(REQUEST_ID);
        request.setIssueInstant(Instant.EPOCH);
        request.setIssuer(issuer);
        request.setVersion(SAMLVersion.VERSION_20);

        return request;
    }
    
    /**
     * Build a {@link Scoping}.
     * 
     * @param count proxy count
     * @param idplist list of IdP entityIDs
     * 
     * @return populated {@link Scoping}
     * 
     * @since 4.0.0
     */
    @Nonnull public static Scoping buildScoping(@Nullable final Integer count, @Nullable final Set<String> idplist) {
        final SAMLObjectBuilder<Scoping> scopingBuilder = (SAMLObjectBuilder<Scoping>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Scoping>ensureBuilder(
                        Scoping.DEFAULT_ELEMENT_NAME);
        final SAMLObjectBuilder<IDPList> idpListBuilder = (SAMLObjectBuilder<IDPList>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<IDPList>ensureBuilder(
                        IDPList.DEFAULT_ELEMENT_NAME);
        final SAMLObjectBuilder<IDPEntry> idpBuilder = (SAMLObjectBuilder<IDPEntry>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<IDPEntry>ensureBuilder(
                        IDPEntry.DEFAULT_ELEMENT_NAME);
        
        final Scoping scoping = scopingBuilder.buildObject();
        scoping.setProxyCount(count);
        
        if (idplist != null && !idplist.isEmpty()) {
            final IDPList idps = idpListBuilder.buildObject();
            for (final String idp : idplist) {
                final IDPEntry entry = idpBuilder.buildObject();
                entry.setProviderID(idp);
                idps.getIDPEntrys().add(entry);
            }
            scoping.setIDPList(idps);
        }
        
        return scoping;
    }

    /**
     * Builds a {@link ArtifactResolve}.
     * 
     * @param artifact the artifact to add to the request
     * 
     * @return the built request
     */
    @Nonnull public static ArtifactResolve buildArtifactResolve(final @Nullable String artifact) {
        final SAMLObjectBuilder<ArtifactResolve> requestBuilder = (SAMLObjectBuilder<ArtifactResolve>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<ArtifactResolve>ensureBuilder(
                        ArtifactResolve.DEFAULT_ELEMENT_NAME);
        final ArtifactResolve request = requestBuilder.buildObject();
        request.setID(REQUEST_ID);
        request.setIssueInstant(Instant.EPOCH);
        request.setVersion(SAMLVersion.VERSION_11);
        
        if (artifact != null) {
            final SAMLObjectBuilder<Artifact> artifactBuilder = (SAMLObjectBuilder<Artifact>)
                    XMLObjectProviderRegistrySupport.getBuilderFactory().<Artifact>ensureBuilder(
                            Artifact.DEFAULT_ELEMENT_NAME);
            final Artifact art = artifactBuilder.buildObject();
            art.setValue(artifact);
            request.setArtifact(art);
        }

        return request;
    }
    
    /**
     * Builds a {@link Status}.
     * 
     * @param codeString status code string
     * @param subcodeString subcode string if any
     * 
     * @return the object
     * 
     * @aince 5.2.0
     */
    @Nonnull public static Status buildStatus(@Nonnull final String codeString, @Nullable final String subcodeString) {
        final SAMLObjectBuilder<StatusCode> codeBuilder = (SAMLObjectBuilder<StatusCode>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<StatusCode>ensureBuilder(
                        StatusCode.DEFAULT_ELEMENT_NAME);
        final SAMLObjectBuilder<Status> statusBuilder = (SAMLObjectBuilder<Status>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Status>ensureBuilder(
                        Status.DEFAULT_ELEMENT_NAME);

        final StatusCode code = codeBuilder.buildObject();
        code.setValue(codeString);
        
        if (subcodeString != null) {
            final StatusCode subcode = codeBuilder.buildObject();
            subcode.setValue(subcodeString);
            code.setStatusCode(subcode);
        }
        
        final Status status = statusBuilder.buildObject();
        status.setStatusCode(code);
        
        return status;
    }
    
}