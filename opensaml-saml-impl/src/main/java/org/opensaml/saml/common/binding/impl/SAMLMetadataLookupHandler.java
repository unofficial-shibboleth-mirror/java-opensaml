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

package org.opensaml.saml.common.binding.impl;

import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ParentContextLookup;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.criterion.ProfileRequestContextCriterion;
import org.opensaml.saml.common.messaging.context.AbstractSAMLEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataLookupParametersContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.metadata.criteria.entity.DetectDuplicateEntityIDsCriterion;
import org.opensaml.saml.metadata.resolver.RoleDescriptorResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * Handler for inbound SAML protocol messages that attempts to locate SAML metadata for
 * a SAML entity, and attaches it as a {@link SAMLMetadataContext} child of a
 * pre-existing concrete instance of {@link AbstractSAMLEntityContext}.
 *
 * <p>
 * The entity context class is configurable and defaults to {@link SAMLPeerEntityContext}.
 * The handler will no-op in the absence of an existing {@link AbstractSAMLEntityContext}
 * child of the message context with non-null values for both entityID and role.
 * </p>
 * 
 * <p>
 * If the optional copy strategy is configured via {@link #setCopyContextStrategy(Function)},
 * and if that lookup finds an existing metadata context with compatible data (matching entityID and role),
 * then its data will be re-used.
 * </p>
 *
 * <p>
 * Otherwise an attempt to resolve metadata will be performed with the configured {@link RoleDescriptorResolver}.
 * A protocol from a {@link SAMLProtocolContext} will be added to the lookup, if available.
 * </p>
 */
public class SAMLMetadataLookupHandler extends AbstractMessageHandler {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAMLMetadataLookupHandler.class);

    /** Resolver used to look up SAML metadata. */
    @NonnullAfterInit private RoleDescriptorResolver metadataResolver;
    
    /** The context class representing the SAML entity whose data is to be resolved. 
     * Defaults to: {@link SAMLPeerEntityContext}. */
    @Nonnull private Class<? extends AbstractSAMLEntityContext> entityContextClass;

    /** Optional strategy for resolving an existing metadata context from which to copy data. */
    @Nullable private Function<MessageContext,SAMLMetadataContext> copyContextStrategy;
    
    /** Optional but defaulted strategy for locating a PRC. */
    @Nullable private Function<MessageContext,ProfileRequestContext> profileRequestContextLookupStrategy;
    
    /** Constructor. */
    public SAMLMetadataLookupHandler() {
        entityContextClass = SAMLPeerEntityContext.class;
        profileRequestContextLookupStrategy = new ParentContextLookup<>(ProfileRequestContext.class);
    }

    /**
     * Set the optional strategy for resolving an existing metadata context from which to copy data.
     *
     * @param strategy the strategy function
     */
    public void setCopyContextStrategy(@Nullable final Function<MessageContext, SAMLMetadataContext> strategy) {
        checkSetterPreconditions();
        copyContextStrategy = strategy;
    }

    /**
     * Set the class type holding the SAML entity data.
     * 
     * <p>Defaults to: {@link SAMLPeerEntityContext}.</p>
     * 
     * @param clazz the entity context class type
     */
    public void setEntityContextClass(@Nonnull final Class<? extends AbstractSAMLEntityContext> clazz) {
        checkSetterPreconditions();
        entityContextClass = Constraint.isNotNull(clazz, "SAML entity context class may not be null");
    }

    /**
     * Set the {@link RoleDescriptorResolver} to use.
     * 
     * @param resolver  the resolver to use
     */
    public void setRoleDescriptorResolver(@Nonnull final RoleDescriptorResolver resolver) {
        checkSetterPreconditions();
        metadataResolver = Constraint.isNotNull(resolver, "RoleDescriptorResolver cannot be null");
    }
    
    /**
     * Set optional lookup strategy for locating {@link ProfileRequestContext}.
     * 
     * <p>Defaults to parent lookup. If set and found, a {@link ProfileRequestContextCriterion} will be included
     * in the attempt.</p>
     * 
     * @param strategy
     * 
     * @since 5.0.0
     */
    public void setProfileRequestContextLookupStrategy(
            @Nullable final Function<MessageContext,ProfileRequestContext> strategy) {
        checkSetterPreconditions();
        profileRequestContextLookupStrategy = strategy;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (metadataResolver == null) {
            throw new ComponentInitializationException("RoleDescriptorResolver cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        checkComponentActive();

        final AbstractSAMLEntityContext entityCtx = messageContext.getSubcontext(entityContextClass);

        if (entityCtx == null || entityCtx.getEntityId() == null || entityCtx.getRole() == null) {
            log.info("{} SAML entity context class '{}' missing or did not contain an entityID or role", getLogPrefix(),
                    entityContextClass.getName());
            return;
        }

        final SAMLMetadataContext existingMetadataCtx = resolveExisting(messageContext,
                entityCtx.getEntityId(), entityCtx.getRole());
        if (existingMetadataCtx != null) {
            log.info("{} Resolved existing metadata context, re-using it", getLogPrefix());
            entityCtx.addSubcontext(existingMetadataCtx);
            return;
        }

        final CriteriaSet criteria = buildLookupCriteria(messageContext);
        
        try {
            final RoleDescriptor roleMetadata = metadataResolver.resolveSingle(criteria);
            if (roleMetadata == null) {
                if (criteria.contains(ProtocolCriterion.class)) {
                    log.info("{} No metadata returned for {} in role {} with protocol {}",
                            getLogPrefix(), entityCtx.getEntityId(), entityCtx.getRole(),
                            criteria.get(ProtocolCriterion.class).getProtocol());
                } else {
                    log.info("{} No metadata returned for {} in role {}",
                            getLogPrefix(), entityCtx.getEntityId(), entityCtx.getRole());
                }
                return;
            }

            final SAMLMetadataContext metadataCtx = new SAMLMetadataContext();
            metadataCtx.setEntityDescriptor((EntityDescriptor) roleMetadata.getParent());
            metadataCtx.setRoleDescriptor(roleMetadata);

            entityCtx.addSubcontext(metadataCtx);

            log.debug("{} {} added to MessageContext as child of {}", getLogPrefix(), 
                    SAMLMetadataContext.class.getName(), entityContextClass.getName());
        } catch (final ResolverException e) {
            log.error("{} ResolverException thrown during metadata lookup", getLogPrefix(), e);
        }
    }

    /**
     * Build the lookup criteria from the message context data.
     * 
     * @param messageContext the current message context
     * 
     * @return the new lookup criteria
     */
    protected CriteriaSet buildLookupCriteria(final MessageContext messageContext) {
        
        // This must be present in the message context, but is already checked in the calling method
        final AbstractSAMLEntityContext entityCtx = messageContext.getSubcontext(entityContextClass);
        
        final EntityIdCriterion entityIdCriterion = new EntityIdCriterion(entityCtx.getEntityId());
        final EntityRoleCriterion roleCriterion = new EntityRoleCriterion(entityCtx.getRole());
        
        ProtocolCriterion protocolCriterion = null;
        final SAMLProtocolContext protocolCtx = messageContext.getSubcontext(SAMLProtocolContext.class);
        if (protocolCtx != null && protocolCtx.getProtocol() != null) {
            protocolCriterion = new ProtocolCriterion(protocolCtx.getProtocol());
        }
        
        final SAMLMetadataLookupParametersContext lookupParamsContext =
                messageContext.getSubcontext(SAMLMetadataLookupParametersContext.class); 
        
        DetectDuplicateEntityIDsCriterion detectDuplicatesCriterion = null;
        if (lookupParamsContext != null && lookupParamsContext.getDetectDuplicateEntityIDs() != null) {
            detectDuplicatesCriterion =
                    new DetectDuplicateEntityIDsCriterion(lookupParamsContext.getDetectDuplicateEntityIDs()); 
        }
        
        ProfileRequestContextCriterion prcCriterion = null;
        if (profileRequestContextLookupStrategy != null) {
            final ProfileRequestContext prc = profileRequestContextLookupStrategy.apply(messageContext);
            if (prc != null) {
                prcCriterion = new ProfileRequestContextCriterion(prc);
            }
        }
        
        final CriteriaSet criteria = new CriteriaSet(entityIdCriterion, protocolCriterion, roleCriterion,
                detectDuplicatesCriterion, prcCriterion);
        return criteria;
    }

    /**
     * Attempt to resolve an existing {@link SAMLMetadataContext} from which to copy.
     *
     * <p>
     * The returned context will always be a fresh parent-less instance, suitable for the caller to
     * directly store in the current message context.
     * </p>
     *
     * @param messageContext the current message context
     * @param entityID the entityID against which to match
     * @param role the entity role against which to match
     *
     * @return a new instance of {@link SAMLMetadataContext}, or null if one can not be resolved
     */
    @Nullable protected SAMLMetadataContext resolveExisting(@Nonnull final MessageContext messageContext,
            @Nonnull final String entityID, @Nonnull final QName role) {

        if (copyContextStrategy == null) {
            return null;
        }

        final SAMLMetadataContext existing = copyContextStrategy.apply(messageContext);
        if (existing != null) {
            if (existing.getEntityDescriptor() != null && existing.getRoleDescriptor() != null) {
                // Validate that existing data has the same entityID and role
                if (Objects.equals(existing.getEntityDescriptor().getEntityID(), entityID)
                        && (Objects.equals(existing.getRoleDescriptor().getElementQName(), role)
                                || Objects.equals(existing.getRoleDescriptor().getSchemaType(), role))
                        ) {
                    log.debug("{} Found an existing and suitable SAMLMetadataContext from which to copy ",
                            getLogPrefix());
                    final SAMLMetadataContext copy = new SAMLMetadataContext();
                    copy.setEntityDescriptor(existing.getEntityDescriptor());
                    copy.setRoleDescriptor(existing.getRoleDescriptor());
                    return copy;
                }
                log.debug("{} Existing SAMLMetadataContext was resolved, but was either the entityID "
                        + "or role did not match the entity context data", getLogPrefix());
            }
            log.debug("{} Existing SAMLMetadataContext was resolved, but was missing EntityDescriptor "
                    + "or RoleDescriptor data", getLogPrefix());
        } else {
            log.debug("{} No existing SAMLMetadataContext was resolved", getLogPrefix());
        }
        return null;
    }

}