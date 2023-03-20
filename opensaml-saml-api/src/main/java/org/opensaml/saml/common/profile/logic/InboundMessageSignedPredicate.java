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

package org.opensaml.saml.common.profile.logic;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.binding.SAMLBindingSupport;

/**
 * A predicate which evaluates whether an inbound SAML message is signed.
 */
public class InboundMessageSignedPredicate implements Predicate<ProfileRequestContext> {
    
    /** Flag indicating whether the presence of a non-null {@link org.opensaml.xmlsec.signature.Signature}
     *  member satisfies the evaluation. */
    private boolean presenceSatisfies;

    /**
     * Set whether the presence of a non-null {@link org.opensaml.xmlsec.signature.Signature}
     * member satisfies the evaluation.
     * 
     * @param flag whether the presence of a non-null {@link org.opensaml.xmlsec.signature.Signature} is considered
     */
    public void setPresenceSatisfies(final boolean flag) {
        presenceSatisfies = flag;
    }

    /** {@inheritDoc} */
    public boolean test(@Nullable final ProfileRequestContext input) {

        if (input != null && input.getInboundMessageContext() instanceof MessageContext mc) {
            return SAMLBindingSupport.isMessageSigned(mc, presenceSatisfies);
        }
        
        return false;
    }

}