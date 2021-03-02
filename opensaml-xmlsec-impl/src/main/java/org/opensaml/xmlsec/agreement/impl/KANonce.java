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
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.xmlsec.agreement.CloneableKeyAgreementParameter;
import org.opensaml.xmlsec.agreement.KeyAgreementException;
import org.opensaml.xmlsec.agreement.KeyAgreementParameter;
import org.opensaml.xmlsec.agreement.XMLExpressableKeyAgreementParameter;

import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Key agreement parameter to support use of {@link org.opensaml.xmlsec.encryption.KANonce} values.
 */
public class KANonce extends AbstractInitializableComponent
    implements XMLExpressableKeyAgreementParameter, CloneableKeyAgreementParameter {
    
    /** Base64-encoded nonce value. */
    @Nullable private String value;
    
    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        if (value == null) {
            throw new ComponentInitializationException("KANonce value was null");
        }
    }

    /**
     * Get the Base64-encoded nonce value.
     * 
     * @return the nonce value
     */
    @Nullable public String getValue() {
        return value;
    }
    
    /**
     * Set the Base64-encoded nonce value.
     * 
     * @param newValue the nonce value
     */
    public void setValue(@Nullable final String newValue) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        value = StringSupport.trimOrNull(newValue);
    }

    /** {@inheritDoc} */
    public KANonce clone() {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        try {
            return (KANonce ) super.clone();
        } catch (final CloneNotSupportedException e) {
            // We know we are, so this will never happen
            return null;
        }
    }
    
    /** {@inheritDoc} */
    @Nonnull public XMLObject buildXMLObject() {
        final org.opensaml.xmlsec.encryption.KANonce nonce =
                (org.opensaml.xmlsec.encryption.KANonce) XMLObjectSupport
                    .buildXMLObject(org.opensaml.xmlsec.encryption.KANonce.DEFAULT_ELEMENT_NAME);
        
        nonce.setValue(getValue());
        return nonce;
    }
    
    /**
     * Create and initialize a new instance from the specified {@link XMLObject}.
     * 
     * @param xmlObject the XML object
     * 
     * @return new parameter instance
     * 
     * @throws ComponentInitializationException if component initialization fails
     */
    @Nonnull public static KANonce fromXMLObject(@Nonnull final org.opensaml.xmlsec.encryption.KANonce xmlObject) 
            throws ComponentInitializationException {
        Constraint.isNotNull(xmlObject, "XMLObject was null");
        
        final KANonce parameter = new KANonce();
        parameter.setValue(xmlObject.getValue());
        parameter.initialize();
        return parameter;
    }
    
    /**
     * Implementation of {@link KeyAgreementParameterParser}.
     */
    public static class Parser implements KeyAgreementParameterParser {

        /** {@inheritDoc} */
        public boolean handles(@Nonnull final XMLObject xmlObject) {
            return org.opensaml.xmlsec.encryption.KANonce.class.isInstance(xmlObject);
        }

        /** {@inheritDoc} */
        public KeyAgreementParameter parse(@Nonnull final XMLObject xmlObject) throws KeyAgreementException {
            // Sanity check
            if (!handles(xmlObject)) {
                throw new KeyAgreementException("This implementation does not handle: "
                        + xmlObject.getClass().getName());
            }
            
            try {
                return fromXMLObject(org.opensaml.xmlsec.encryption.KANonce.class.cast(xmlObject));
            } catch (final ComponentInitializationException e) {
                throw new KeyAgreementException(e);
            }
        }
        
    }

}
