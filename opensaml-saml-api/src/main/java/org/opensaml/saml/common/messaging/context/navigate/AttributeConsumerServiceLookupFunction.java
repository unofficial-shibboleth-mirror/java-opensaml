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

package org.opensaml.saml.common.messaging.context.navigate;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.navigate.ContextDataLookupFunction;
import org.opensaml.saml.common.messaging.context.AttributeConsumingServiceContext;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;

/**
 * {@link ContextDataLookupFunction} to return the {@link AttributeConsumingService}
 * from the {@link AttributeConsumingServiceContext}.
 */
public class AttributeConsumerServiceLookupFunction implements
        ContextDataLookupFunction<AttributeConsumingServiceContext, AttributeConsumingService> {

    /** {@inheritDoc} */
    @Nullable public AttributeConsumingService apply(@Nullable final AttributeConsumingServiceContext input) {
        if (null == input) {
            return null;
        }
        return input.getAttributeConsumingService();
    }

}