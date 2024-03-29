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

package org.opensaml.saml.common.messaging.context;

import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.saml.common.binding.BindingDescriptor;

/**
 * Context for holding information related to the SAML binding in use.
 */
public final class SAMLBindingContext extends BaseContext {
    
    /** The relay state associated with the message. */
    @Nullable @NotEmpty private String relayState;
    
    /** The binding descriptor. */
    @Nullable private BindingDescriptor bindingDescriptor;
    
    /** The binding URI. */
    @Nullable @NotEmpty private String bindingUri;
    
    /** Flag indicating whether the message is signed at the binding level. */
    private boolean hasBindingSignature;
    
    /** Flag indicating whether the binding in use requires the presence within the message 
     * of information indicating the intended message destination endpoint URI. */
    private boolean isIntendedDestinationEndpointURIRequired;
    
    /**
     * Gets the relay state.
     * 
     * @return relay state associated with this protocol exchange, may be null
     */
    @Nullable @NotEmpty public String getRelayState() {
        return relayState;
    }

    /**
     * Sets the relay state.
     * 
     * @param state relay state associated with this protocol exchange
     */
    public void setRelayState(@Nullable final String state) {
        relayState = StringSupport.trimOrNull(state);
    }

    /**
     * Get the SAML binding URI.
     * 
     * @return Returns the bindingUri.
     */
    @Nullable @NotEmpty public String getBindingUri() {
        if (bindingUri != null) {
            return bindingUri;
        } else if (bindingDescriptor != null) {
            return bindingDescriptor.getId();
        } else {
            return null;
        }
    }

    /**
     * Set the SAML binding URI.
     * 
     * @param newBindingUri the new binding URI
     */
    public void setBindingUri(@Nullable final String newBindingUri) {
        bindingUri = StringSupport.trimOrNull(newBindingUri);
    }

    /**
     * Get the SAML binding descriptor.
     * 
     * @return the descriptor
     */
    @Nullable public BindingDescriptor getBindingDescriptor() {
        return bindingDescriptor;
    }

    /**
     * Set the SAML binding descriptor.
     * 
     * @param descriptor the new binding descriptor
     */
    public void setBindingDescriptor(@Nullable final BindingDescriptor descriptor) {
        bindingDescriptor = descriptor;
    }
    
    /**
     * Get the flag indicating whether the message is signed at the binding level.
     * 
     * @return true if message was signed at the binding level, otherwise false
     */
    public boolean hasBindingSignature() {
        return hasBindingSignature;
    }

    /**
     * Set the flag indicating whether the message is signed at the binding level.
     * 
     * @param flag true if message was signed at the binding level, otherwise false
     */
    public void setHasBindingSignature(final boolean flag) {
        hasBindingSignature = flag;
    }

    /**
     * Get the flag indicating whether the binding in use requires the presence within the message 
     * of information indicating the intended message destination endpoint URI.
     * 
     * @return true if required, false otherwise
     */
    public boolean isIntendedDestinationEndpointURIRequired() {
        return isIntendedDestinationEndpointURIRequired;
    }

    /**
     * Set the flag indicating whether the binding in use requires the presence within the message 
     * of information indicating the intended message destination endpoint URI.
     * 
     * @param flag true if required, false otherwise
     */
    public void setIntendedDestinationEndpointURIRequired(final boolean flag) {
        isIntendedDestinationEndpointURIRequired = flag;
    }

}