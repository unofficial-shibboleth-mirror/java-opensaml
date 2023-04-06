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

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.AbstractAuthenticatableSAMLEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.security.messaging.impl.BaseClientCertAuthSecurityHandler;
import org.slf4j.Logger;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.ConstraintViolationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * SAML specialization of {@link BaseClientCertAuthSecurityHandler} which provides support for X509Credential 
 * trust engine validation based on SAML metadata.
 * 
 * <p>
 * The authenticatable entity data is read from and stored to the subcontext identified by 
 * {@link #getEntityContextClass()}, which defaults to {@link SAMLPeerEntityContext}.
 * </p>
 */
public class SAMLMDClientCertAuthSecurityHandler extends BaseClientCertAuthSecurityHandler {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(SAMLMDClientCertAuthSecurityHandler.class);
    
    /** The actual context class holding the authenticatable SAML entity. */
    @Nonnull private Class<? extends AbstractAuthenticatableSAMLEntityContext> entityContextClass;

    /**
     * Constructor.
     *
     */
    public SAMLMDClientCertAuthSecurityHandler() {
        entityContextClass = SAMLPeerEntityContext.class;
    }
    
    /**
     * Get the class type holding the authenticatable SAML entity data.
     * 
     * <p>Defaults to: {@link SAMLPeerEntityContext}.</p>
     * 
     * @return the entity context class type
     */
    @Nonnull public Class<? extends AbstractAuthenticatableSAMLEntityContext> getEntityContextClass() {
        return entityContextClass;
    }
    
    /**
     * Set the class type holding the authenticatable SAML entity data.
     * 
     * <p>Defaults to: {@link SAMLPeerEntityContext}.</p>
     * 
     * @param clazz the entity context class type
     */
    public void setEntityContextClass(@Nonnull final Class<? extends AbstractAuthenticatableSAMLEntityContext> clazz) {
        checkSetterPreconditions();
        entityContextClass = Constraint.isNotNull(clazz, "The SAML entity context class may not be null");
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull protected CriteriaSet buildCriteriaSet(@Nullable final String entityID,
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        CriteriaSet criteriaSet = super.buildCriteriaSet(entityID, messageContext);
        if (criteriaSet == null) {
            // Not expected...
            criteriaSet = new CriteriaSet();
        }
        
        try {
            log.trace("Attempting to build criteria based on contents of entity contxt class of type: {}", 
                    entityContextClass.getName());
            final AbstractAuthenticatableSAMLEntityContext entityContext = 
                    messageContext.ensureSubcontext(entityContextClass);
            criteriaSet.add(new EntityRoleCriterion(
                    Constraint.isNotNull(entityContext.getRole(), "SAML entity role was null")));
            
            final SAMLProtocolContext protocolContext = messageContext.ensureSubcontext(SAMLProtocolContext.class);
            criteriaSet.add(new ProtocolCriterion(
                    Constraint.isNotNull(protocolContext.getProtocol(), "SAML protocol was null")));
        } catch (final ConstraintViolationException e) {
            throw new MessageHandlerException(e);
        }

        return criteriaSet;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable protected String getCertificatePresenterEntityID(@Nonnull final MessageContext messageContext) {
        final AbstractAuthenticatableSAMLEntityContext entityContext = messageContext.getSubcontext(entityContextClass);
        if (entityContext != null) {
            log.trace("Found authenticatable entityID '{}' from context: {}", 
                    entityContext.getEntityId(), entityContext.getClass().getName());
            return entityContext.getEntityId();
        }
        log.trace("Authenticatable entityID context was not present: {}", entityContextClass.getName());
        return null;
    }

    /** {@inheritDoc} */
    @Override
    protected void setAuthenticatedCertificatePresenterEntityID(@Nonnull final MessageContext messageContext,
            @Nullable final String entityID) {
        log.trace("Storing authenticatable entityID '{}' in context: {}", entityID, entityContextClass);
        messageContext.ensureSubcontext(entityContextClass).setEntityId(entityID);
    }

    /** {@inheritDoc} */
    @Override
    protected void setAuthenticatedState(@Nonnull final MessageContext messageContext, final boolean authenticated) {
        log.trace("Storing authenticated entity state '{}' in context: {}", authenticated, entityContextClass);
        messageContext.ensureSubcontext(entityContextClass).setAuthenticated(authenticated);
    }
    
}