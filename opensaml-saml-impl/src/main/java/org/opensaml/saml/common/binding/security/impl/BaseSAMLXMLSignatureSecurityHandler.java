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

package org.opensaml.saml.common.binding.security.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.messaging.impl.BaseTrustEngineSecurityHandler;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureValidationParametersCriterion;

import com.google.common.base.Strings;

import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * Base class for SAML security message handlers which evaluate a signature with a signature trust engine.
 */
public abstract class BaseSAMLXMLSignatureSecurityHandler extends BaseTrustEngineSecurityHandler<Signature> {
    
    /** The context representing the SAML peer entity. */
    @Nullable private SAMLPeerEntityContext peerContext;
    
    /** The SAML protocol context in operation. */
    @Nullable private SAMLProtocolContext samlProtocolContext;

    /** The SAML protocol in use. */
    @Nullable private String samlProtocol;
    
    /** The SAML role in use. */
    @Nullable private QName samlRole;

    /** Parameters for signature validation. */
    @Nullable private SignatureValidationParameters signatureValidationParameters;
    
    /**
     * Get the {@link SAMLPeerEntityContext} associated with the message.
     * 
     * @return the peer context
     */
    @Nullable protected SAMLPeerEntityContext getSAMLPeerEntityContext() {
        return peerContext;
    }

    /**
     * Get the {@link SAMLProtocolContext} associated with the message.
     * 
     * @return the protocol context
     */
    @Nullable protected SAMLProtocolContext getSAMLProtocolContext() {
        return samlProtocolContext;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {

        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        peerContext = messageContext.getSubcontext(SAMLPeerEntityContext.class);
        samlRole = peerContext != null ? peerContext.getRole() : null;
        if (samlRole == null) {
            throw new MessageHandlerException("SAMLPeerEntityContext was missing or unpopulated");
        }
        
        samlProtocolContext = messageContext.getSubcontext(SAMLProtocolContext.class);
        samlProtocol = samlProtocolContext != null ? samlProtocolContext.getProtocol() : null;
        if (samlProtocol == null) {
            throw new MessageHandlerException("SAMLProtocolContext was missing or unpopulated");
        }
     
        // Shouldn't happen, as this is populated via superclass invoking trust engine lookup.
        if (signatureValidationParameters == null) {
            final SecurityParametersContext secParams = messageContext.getSubcontext(SecurityParametersContext.class);
            signatureValidationParameters = secParams != null ? secParams.getSignatureValidationParameters() : null;
        }
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable protected TrustEngine<Signature> resolveTrustEngine(@Nonnull final MessageContext messageContext) {

        if (signatureValidationParameters == null) {
            final SecurityParametersContext secParams = messageContext.getSubcontext(SecurityParametersContext.class);
            signatureValidationParameters = secParams != null ? secParams.getSignatureValidationParameters() : null;
        }

        if (signatureValidationParameters != null) {
            return signatureValidationParameters.getSignatureTrustEngine();
        }
        
        return null;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull protected CriteriaSet buildCriteriaSet(@Nullable final String entityID,
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        final CriteriaSet criteriaSet = new CriteriaSet();
        if (!Strings.isNullOrEmpty(entityID)) {
            assert entityID != null;
            criteriaSet.add(new EntityIdCriterion(entityID) );
        }

        assert samlRole != null;
        criteriaSet.add(new EntityRoleCriterion(samlRole));
        assert samlProtocol != null;
        criteriaSet.add(new ProtocolCriterion(samlProtocol));
        criteriaSet.add( new UsageCriterion(UsageType.SIGNING) );
        
        if (signatureValidationParameters != null) {
            criteriaSet.add(new SignatureValidationParametersCriterion(signatureValidationParameters));
        }
        
        return criteriaSet;
    }

}