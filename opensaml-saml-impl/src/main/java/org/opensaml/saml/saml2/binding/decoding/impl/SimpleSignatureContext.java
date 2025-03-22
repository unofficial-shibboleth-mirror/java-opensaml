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

package org.opensaml.saml.saml2.binding.decoding.impl;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;

/**
 * Context implementation holding data related to SAML 2 "simple signature" evaluation.
 */
public class SimpleSignatureContext extends BaseContext {
    
    /** The signed content over which signature evaluation will be performed. */
    @Nullable private byte[] signedContent;
    
    /**
     * Get the signed content over which signature evaluation will be performed.
     * 
     * @return the signed content
     */
    @Nullable public byte[] getSignedContent() {
        return signedContent;
    }
    
    /**
     * Set the signed content over which signature evaluation will be performed.
     * 
     * @param content the signed content
     */
    public void setSignedContent(@Nullable final byte[] content) {
        signedContent = content;
    }

}
