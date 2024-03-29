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

package org.opensaml.saml.common.messaging.soap;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.MessageException;
import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.RecursiveTypedParentContextLookup;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.messaging.HttpClientSecurityContext;
import org.opensaml.soap.client.SOAPClientContext;
import org.opensaml.soap.client.security.SOAPClientSecurityContext;

import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.CriteriaSet;

//TODO when impl finished, document required vs optional data and derivation rules

/**
 * Builder {@link InOutOperationContext} instances for SAML SOAP client use cases.
 * 
 * @param <InboundMessageType> the inbound message type
 * @param <OutboundMessageType> the outbound message type
 */
public class SAMLSOAPClientContextBuilder<InboundMessageType extends SAMLObject, 
        OutboundMessageType extends SAMLObject> {
    
    /** The outbound message. **/
    @Nullable private OutboundMessageType outboundMessage;
    
    /** The SAML protocol in use. */
    @Nullable private String protocol;
    
    /** The SAML self entityID. **/
    @Nullable private String selfEntityID;
    
    /** The SAML peer entityID. **/
    @Nullable private String peerEntityID;
    
    /** The SAML peer entity role. **/
    @Nullable private QName peerEntityRole;
    
    /** The SAML peer EntityDescriptor. **/
    @Nullable private EntityDescriptor peerEntityDescriptor;
    
    /** The SAML peer RoleDescriptor. **/
    @Nullable private RoleDescriptor peerRoleDescriptor;
    
    /** TLS CriteriaSet strategy. */
    @Nullable private Function<MessageContext,CriteriaSet> tlsCriteriaSetStrategy;
    
    /** SOAP client message pipeline name. */
    @Nullable private String pipelineName;
    
    /** SOAP client security configuration profile ID. */
    @Nullable private String securityConfigurationProfileId;
    
    /**
     * Get the outbound message.
     * 
     * @return the outbound message
     */
    @Nullable public OutboundMessageType getOutboundMessage() {
        return outboundMessage;
    }

    /**
     * Set the outbound message.
     * 
     * @param message the outbound message
     * @return this builder instance
     */
    @Nonnull public SAMLSOAPClientContextBuilder<InboundMessageType, OutboundMessageType> setOutboundMessage(
            final OutboundMessageType message) {
        outboundMessage = message;
        return this;
    }

    /**
     * Get the SAML protocol URI.
     * 
     * @return the SAML protocol URI
     */
    @Nullable public String getProtocol() {
        return protocol;
    }

    /**
     * Set the SAML protocol URI.
     * 
     * @param uri the SAML protocol.
     * @return this builder instance
     */
    @Nonnull public SAMLSOAPClientContextBuilder<InboundMessageType, OutboundMessageType> setProtocol(
            final String uri) {
        protocol = uri;
        return this;
    }

    /**
     * Get the SAML self entityID.
     * 
     * @return the SAML self entityID
     */
    @Nullable public String getSelfEntityID() {
        return selfEntityID;
    }

    /**
     * Set the SAML self entityID.
     * 
     * @param entityID the SAML self entityID.
     * @return this builder instance
     */
    @Nonnull public SAMLSOAPClientContextBuilder<InboundMessageType, OutboundMessageType> setSelfEntityID(
            final String entityID) {
        selfEntityID = entityID;
        return this;
    }

    /**
     * Get the SAML peer entityID.
     * 
     * @return the SAML peer entityID
     */
    @Nullable public String getPeerEntityID() {
        if (peerEntityID != null) {
            return peerEntityID;
        }

        final EntityDescriptor ed = getPeerEntityDescriptor();
        if (ed != null) {
            return ed.getEntityID();
        } else {
            return null;
        }
    }

    /**
     * Set the SAML peer entityID.
     * 
     * @param entityID the SAML peer entityID
     * @return this builder instance
     */
    @Nonnull public SAMLSOAPClientContextBuilder<InboundMessageType, OutboundMessageType> setPeerEntityID(
            final String entityID) {
        peerEntityID = entityID;
        return this;
    }

    /**
     * Get the SAML peer role.
     * 
     * @return the SAML peer role
     */
    @Nullable public QName getPeerEntityRole() {
        if (peerEntityRole != null) {
            return peerEntityRole;
        }
        
        final RoleDescriptor rd = getPeerRoleDescriptor();
        if (rd != null) {
            if (rd.getSchemaType() != null) {
                return rd.getSchemaType();
            }
            return rd.getElementQName();
        }
        
        return null;
    }

    /**
     * Set the SAML peer role.
     * 
     * @param role the SAML peer role
     * @return this builder instance
     */
    @Nonnull public SAMLSOAPClientContextBuilder<InboundMessageType, OutboundMessageType> setPeerEntityRole(
            final QName role) {
        peerEntityRole = role;
        return this;
    }

    /**
     * Get the SAML peer EntityDscriptor.
     * 
     * @return the SAML peer EntityDescriptor
     */
    @Nullable public EntityDescriptor getPeerEntityDescriptor() {
        if (peerEntityDescriptor != null) {
            return peerEntityDescriptor;
        }
        
        final RoleDescriptor rd = getPeerRoleDescriptor();
        if (rd != null) {
            final XMLObject roleParent = rd.getParent();
            if (roleParent instanceof EntityDescriptor ed) {
                return ed;
            }
        }
        
        return null;
    }

    /**
     * Set the SAML peer EntityDescriptor.
     * 
     * @param entityDescriptor the SAML peer EntityDescriptor
     * @return this builder instance
     */
    @Nonnull public SAMLSOAPClientContextBuilder<InboundMessageType, OutboundMessageType> setPeerEntityDescriptor(
            final EntityDescriptor entityDescriptor) {
        peerEntityDescriptor = entityDescriptor;
        return this;
    }

    /**
     * Get the SAML peer RoleDescriptor.
     * 
     * @return the SAML peer RoleDescriptor
     */
    @Nullable public RoleDescriptor getPeerRoleDescriptor() {
        return peerRoleDescriptor;
    }

    /**
     * Set the SAML peer RoleDescriptor.
     * 
     * @param roleDescriptor the SAML peer RoleDescriptor.
     * @return this builder instance
     */
    @Nonnull public SAMLSOAPClientContextBuilder<InboundMessageType, OutboundMessageType> setPeerRoleDescriptor(
            final RoleDescriptor roleDescriptor) {
        peerRoleDescriptor = roleDescriptor;
        return this;
    }

    /**
     * Get the TLS CriteriaSet strategy.
     * 
     * @return the TLS CriteriaSet strategy, or null
     */
    @Nullable public Function<MessageContext,CriteriaSet> getTLSCriteriaSetStrategy() {
        if (tlsCriteriaSetStrategy != null) {
            return tlsCriteriaSetStrategy;
        }
        return new DefaultTLSCriteriaSetStrategy();
    }

    /**
     * Set the TLS CriteriaSet strategy.
     * 
     * @param strategy the strategy
     * @return this builder instance 
     */
    @Nonnull public SAMLSOAPClientContextBuilder<InboundMessageType, OutboundMessageType> 
            setTLSCriteriaSetStrategy(@Nullable final Function<MessageContext,CriteriaSet> strategy) {
        tlsCriteriaSetStrategy = strategy;
        return this;
    }

    /**
     * Get the SOAP client message pipeline name to use.
     * 
     * @return the pipeline name, or null
     */
    @Nullable public String getPipelineName() {
        return pipelineName;
    }

    /**
     * Set the SOAP client message pipeline name to use.
     * 
     * @param name the pipeline name, or null
     * @return this builder instance
     */
    @Nonnull public SAMLSOAPClientContextBuilder<InboundMessageType, OutboundMessageType>
            setPipelineName(@Nullable final String name) {
        pipelineName = StringSupport.trimOrNull(name);
        return this;
    }
    
    /**
     * Get the SOAP client security configuration profile ID to use.
     * 
     * @return the client security configuration profile ID, or null
     */
    @Nullable public String getSecurityConfigurationProfileId() {
        return securityConfigurationProfileId;
    }

    /**
     * Set the SOAP client security configuration profile ID to use.
     * 
     * @param profileId the profile ID, or null
     * @return this builder instance
     */
    @Nonnull public SAMLSOAPClientContextBuilder<InboundMessageType, OutboundMessageType>
            setSecurityConfigurationProfileId(@Nullable final String profileId) {
        securityConfigurationProfileId = StringSupport.trimOrNull(profileId);
        return this;
    }

    /**
     * Build the new operation context.
     * 
     * @return the operation context
     * 
     * @throws MessageException if any required data is not supplied and can not be derived from other supplied data
     */
    @Nonnull public InOutOperationContext build() throws MessageException {
        if (getOutboundMessage() == null) {
            errorMissingData("Outbound message");
        }
        final MessageContext outboundContext = new MessageContext();
        outboundContext.setMessage(getOutboundMessage());
        
        final Function<MessageContext,CriteriaSet> tlsStrategy = getTLSCriteriaSetStrategy();
        if (tlsStrategy != null) {
            outboundContext.ensureSubcontext(HttpClientSecurityContext.class)
                .setTLSCriteriaSetStrategy(tlsStrategy);
        }
        
        final InOutOperationContext opContext = new InOutOperationContext(null, outboundContext);
        
        // This is just so it's easy to change.
        final BaseContext parent = opContext;
        
        if (getProtocol() != null) {
            parent.ensureSubcontext(SAMLProtocolContext.class).setProtocol(getProtocol());
        }
        
        if (getPipelineName() != null) {
            parent.ensureSubcontext(SOAPClientContext.class).setPipelineName(getPipelineName());
        }
        
        if (getSecurityConfigurationProfileId() != null) {
            parent.ensureSubcontext(SOAPClientSecurityContext.class).setSecurityConfigurationProfileId(
                    getSecurityConfigurationProfileId());
        }
        
        //TODO is this required always?
        final String selfID = getSelfEntityID();
        if (selfID != null) {
            final SAMLSelfEntityContext selfContext = parent.ensureSubcontext(SAMLSelfEntityContext.class);
            selfContext.setEntityId(selfID);
        }
        
        // Both of these required, either supplied or derived
        final String peerID = getPeerEntityID();
        if (peerID == null) {
            errorMissingData("Peer entityID");
        }
        final QName peerRoleName = getPeerEntityRole();
        if (peerRoleName == null) {
            errorMissingData("Peer role");
        }
        final SAMLPeerEntityContext peerContext = parent.ensureSubcontext(SAMLPeerEntityContext.class);
        peerContext.setEntityId(peerID);
        peerContext.setRole(peerRoleName);
        
        //  Both optional, could be resolved in SOAP handling pipeline by handler(s)
        final SAMLMetadataContext metadataContext = peerContext.ensureSubcontext(SAMLMetadataContext.class);
        metadataContext.setEntityDescriptor(getPeerEntityDescriptor());
        metadataContext.setRoleDescriptor(getPeerRoleDescriptor());
        
        return opContext;
    }

    /**
     * Convenience method to report out an error due to missing required data.
     * 
     * @param details the error details
     * @throws MessageException the error to be reported out
     */
    private void errorMissingData(@Nonnull final String details) throws MessageException {
        throw new MessageException("Required context data was not supplied or derivable: " + details);
    }
    
    /** Default TLS CriteriaSet strategy function. */
    public static class DefaultTLSCriteriaSetStrategy implements Function<MessageContext,CriteriaSet> {

// Checkstyle: CyclomaticComplexity OFF
        /** {@inheritDoc} */
        @Nullable public CriteriaSet apply(@Nullable final MessageContext messageContext) {
            final CriteriaSet criteria = new CriteriaSet();
            criteria.add(new UsageCriterion(UsageType.SIGNING));
            
            if (messageContext == null) {
                return criteria;
            }
            
            // This should be consistent with what build() does above.
            final BaseContext parent = new RecursiveTypedParentContextLookup<>(InOutOperationContext.class)
                    .apply(messageContext);
            if (parent == null) {
                return criteria;
            }
            
            final SAMLProtocolContext protocolContext = parent.getSubcontext(SAMLProtocolContext.class);
            if (protocolContext != null && protocolContext.getProtocol() != null) {
                final String protocol = protocolContext.getProtocol();
                if (protocol != null) {
                    criteria.add(new ProtocolCriterion(protocol));
                }
            }
            
            final SAMLPeerEntityContext peerContext = parent.getSubcontext(SAMLPeerEntityContext.class);
            if (peerContext != null) {
                final String entityID = peerContext.getEntityId();
                if (entityID != null) {
                    criteria.add(new EntityIdCriterion(entityID));
                }
                
                final QName role = peerContext.getRole();
                if (role != null) {
                    criteria.add(new EntityRoleCriterion(role));
                }
                
                final SAMLMetadataContext metadataContext = peerContext.getSubcontext(SAMLMetadataContext.class);
                if (metadataContext != null) {
                    final RoleDescriptor rd = metadataContext.getRoleDescriptor();
                    if (rd != null) {
                        criteria.add(new RoleDescriptorCriterion(rd));
                    }
                }
            }
            
            return criteria;
        }
// Checkstyle: CyclomaticComplexity OFF
    }

}