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

package org.opensaml.saml.common.profile.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Function to return a set of candidate NameIdentifier/NameID Format values derived from an entity's
 * SAML metadata. 
 */
public class MetadataNameIdentifierFormatStrategy implements Function<ProfileRequestContext,List<String>> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(MetadataNameIdentifierFormatStrategy.class);
    
    /** Strategy function to lookup the {@link SSODescriptor} to read from. */
    @Nonnull private Function<ProfileRequestContext,SSODescriptor> ssoDescriptorLookupStrategy;
    
    /** Constructor. */
    public MetadataNameIdentifierFormatStrategy() {
        ssoDescriptorLookupStrategy = new MetadataLookupStrategy();
    }

    /**
     * Set the lookup strategy to use to obtain an {@link SSODescriptor}.
     * 
     * @param strategy  lookup strategy
     */
    public void setSSODescriptorLookupStrategy(@Nonnull final Function<ProfileRequestContext,SSODescriptor> strategy) {
        ssoDescriptorLookupStrategy = Constraint.isNotNull(strategy, "SSODescriptor lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Nonnull @Unmodifiable @NotLive public List<String> apply(@Nullable final ProfileRequestContext input) {
        final SSODescriptor role = ssoDescriptorLookupStrategy.apply(input);
        if (role != null) {
            final List<String> strings = new ArrayList<>();
            for (final NameIDFormat nif : role.getNameIDFormats()) {
                if (nif.getURI() != null) {
                    if (NameID.UNSPECIFIED.equals(nif.getURI())) {
                        log.warn("Ignoring NameIDFormat metadata that includes the 'unspecified' format");
                        return CollectionSupport.emptyList();
                    }
                    strings.add(nif.getURI());
                }
            }
            
            log.debug("Metadata specifies the following formats: {}", strings);
            return CollectionSupport.copyToList(strings);
        }
        
        return CollectionSupport.emptyList();
    }

    /**
     * Default lookup strategy for metadata, relies on the inbound message context.
     */
    private class MetadataLookupStrategy implements Function<ProfileRequestContext,SSODescriptor> {

        /** {@inheritDoc} */
        @Nullable public SSODescriptor apply(@Nullable final ProfileRequestContext input) {
            if (input != null) {
                final MessageContext mc = input.getInboundMessageContext();
                final SAMLPeerEntityContext peerCtx = mc != null ? mc.getSubcontext(SAMLPeerEntityContext.class) : null;
                if (peerCtx != null) {
                    final SAMLMetadataContext mdCtx = peerCtx.getSubcontext(SAMLMetadataContext.class);
                    if (mdCtx != null && mdCtx.getRoleDescriptor() != null
                            && mdCtx.getRoleDescriptor() instanceof SSODescriptor) {
                        return (SSODescriptor) mdCtx.getRoleDescriptor();
                    }
                    log.debug("No SAMLMetadataContext or SSODescriptor role available");
                } else {
                    log.debug("No SAMLPeerEntityContext available");
                }
            } else {
                log.debug("No inbound message context available");
            }
            
            return null;
        }
    }

}