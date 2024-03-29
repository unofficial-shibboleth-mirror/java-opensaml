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
import org.opensaml.saml.common.binding.BindingDescriptor;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;

/** {@link ContextDataLookupFunction} that returns the SAML binding from a {@link SAMLBindingContext}. */
public class SAMLBindingContextBindingFunction implements ContextDataLookupFunction<SAMLBindingContext,String> {

    /** Whether to extract the "short" name for the binding, if possible. */
    private boolean useShortName;
    
    /**
     * Set whether to extract the short name for binding, if available.
     * 
     * @param flag flag to set
     */
    public void setUseShortName(final boolean flag) {
        useShortName = flag;
    }
    
    /** {@inheritDoc} */
    @Nullable public String apply(@Nullable final SAMLBindingContext input) {
        if (input != null) {
            if (useShortName) {
                final BindingDescriptor descriptor = input.getBindingDescriptor();
                if (descriptor != null && descriptor.getShortName() != null) {
                    return descriptor.getShortName();
                }
            }
            return input.getBindingUri();
        }
        return null;
    }

}