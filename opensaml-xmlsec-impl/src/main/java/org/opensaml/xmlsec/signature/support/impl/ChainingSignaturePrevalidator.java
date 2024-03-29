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

package org.opensaml.xmlsec.signature.support.impl;

import java.util.List;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignaturePrevalidator;
import org.slf4j.Logger;

/**
 * A signature prevalidator implementation which chains execution of a list of {@link SignaturePrevalidator} instances.
 */
public class ChainingSignaturePrevalidator implements SignaturePrevalidator {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(ChainingSignaturePrevalidator.class);
    
    /** The chain of SignaturePrevalidator instances to execute. */
    @Nonnull private List<SignaturePrevalidator> validators;
    
    /**
     * Constructor.
     *
     * @param validatorChain the chain of SignaturePrevalidator instances to execute
     */
    public ChainingSignaturePrevalidator(
            @Nonnull @ParameterName(name="validatorChain") final List<SignaturePrevalidator> validatorChain) {
        validators = CollectionSupport.copyToList(
                Constraint.isNotNull(validatorChain, "SignaturePrevalidator list cannot be null"));
    }

    /** {@inheritDoc} */
    @Override
    public void validate(@Nonnull final Signature signature) throws SignatureException {
        for (final SignaturePrevalidator validator : validators) {
            log.debug("Validating signature using prevalidator: {}", validator.getClass().getName());
            validator.validate(signature);
        }
        
    }

}