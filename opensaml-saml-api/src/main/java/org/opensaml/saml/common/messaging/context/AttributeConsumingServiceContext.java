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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;

/**
 * Context, usually attached to {@link org.opensaml.saml.common.messaging.context.SAMLMetadataContext}
 * that carries a SAML {@link AttributeConsumingService} for use in later stages.
 */
public final class AttributeConsumingServiceContext extends BaseContext {

    /** The AttributeConsumingService. */
    @Nullable private AttributeConsumingService attributeConsumingService;

    /**
     * Gets the assertion to be validated.
     * 
     * @return the assertion to be validated
     */
    @Nullable public AttributeConsumingService getAttributeConsumingService() {
        return attributeConsumingService;
    }

    /**
     * Sets the assertion to be validated.
     * 
     * @param acs assertion to be validated
     * 
     * @return this context
     */
    @Nonnull public AttributeConsumingServiceContext setAttributeConsumingService(
            @Nullable final AttributeConsumingService acs) {
        attributeConsumingService = acs;
        return this;
    }
    
}