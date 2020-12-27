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

package org.opensaml.xmlsec.agreement.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.xmlsec.agreement.CloneableKeyAgreementParameter;
import org.opensaml.xmlsec.agreement.XMLExpressableKeyAgreementParameter;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Key agreement parameter to support use of {@link org.opensaml.xmlsec.encryption.KANonce} values.
 */
public class KANonce implements XMLExpressableKeyAgreementParameter, CloneableKeyAgreementParameter {
    
    /** Base64-encoded nonce value. */
    @Nonnull private String value;

    /**
     * Constructor.
     *
     * @param newValue the new nonce value
     */
    public KANonce(@Nonnull final String newValue) {
        value = Constraint.isNotNull(StringSupport.trimOrNull(newValue), "Nonce value was null or empty");
    }
    
    /**
     * Get the Base64-encoded nonce value.
     * 
     * @return the nonce value
     */
    @Nonnull public String getValue() {
        return value;
    }

    /** {@inheritDoc} */
    public XMLObject buildXMLObject() {
        final org.opensaml.xmlsec.encryption.KANonce nonce =
                (org.opensaml.xmlsec.encryption.KANonce) XMLObjectSupport
                    .buildXMLObject(org.opensaml.xmlsec.encryption.KANonce.DEFAULT_ELEMENT_NAME);
        
        nonce.setValue(getValue());
        return nonce;
    }
    
    /** {@inheritDoc} */
    public KANonce clone() {
        try {
            return (KANonce ) super.clone();
        } catch (final CloneNotSupportedException e) {
            // We know we are, so this will never happen
            return null;
        }
    }

}
