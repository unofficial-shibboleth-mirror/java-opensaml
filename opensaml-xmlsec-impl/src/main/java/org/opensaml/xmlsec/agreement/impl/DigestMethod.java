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

import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Key agreement parameter to support use of {@link org.opensaml.xmlsec.signature.DigestMethod} values.
 */
public class DigestMethod extends AbstractInitializableComponent
    implements XMLExpressableKeyAgreementParameter, CloneableKeyAgreementParameter {
    
    /** Algorithm URI. */
    @Nullable private String algorithm;
    
    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        if (algorithm == null) {
            throw new ComponentInitializationException("DigestMethod algorithm was null");
        }
    }

    /**
     * Get the algorithm URI.
     * 
     * @return the algorithm URI
     */
    @Nullable public String getAlgorithm() {
        return algorithm;
    }
    
    /**
     * Set the algorithm URI.
     * 
     * @param newAlgorithm the algorithm URI
     */
    public void setAlgorithm(@Nullable final String newAlgorithm) {
        checkSetterPreconditions();
        algorithm = StringSupport.trimOrNull(newAlgorithm);
    }

    /** {@inheritDoc} */
    public DigestMethod clone() {
        ifDestroyedThrowDestroyedComponentException();
        try {
            return (DigestMethod ) super.clone();
        } catch (final CloneNotSupportedException e) {
            // We know we are, so this will never happen
            return null;
        }
    }

    /** {@inheritDoc} */
    @Nonnull public XMLObject buildXMLObject() {
        final org.opensaml.xmlsec.signature.DigestMethod digestMethod =
                (org.opensaml.xmlsec.signature.DigestMethod) XMLObjectSupport
                    .buildXMLObject(org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME);
        
        digestMethod.setAlgorithm(getAlgorithm());
        return digestMethod;
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
    @Nonnull public static DigestMethod fromXMLObject(
            @Nonnull final org.opensaml.xmlsec.signature.DigestMethod xmlObject)
                    throws ComponentInitializationException {
        Constraint.isNotNull(xmlObject, "XMLObject was null");
        
        final DigestMethod parameter = new DigestMethod();
        parameter.setAlgorithm(xmlObject.getAlgorithm());
        parameter.initialize();
        return parameter;
    }
    
    /**
     * Implementation of {@link KeyAgreementParameterParser}.
     */
    public static class Parser implements KeyAgreementParameterParser {

        /** {@inheritDoc} */
        public boolean handles(@Nonnull final XMLObject xmlObject) {
            return org.opensaml.xmlsec.signature.DigestMethod.class.isInstance(xmlObject);
        }

        /** {@inheritDoc} */
        public KeyAgreementParameter parse(@Nonnull final XMLObject xmlObject) throws KeyAgreementException {
            // Sanity check
            if (!handles(xmlObject)) {
                throw new KeyAgreementException("This implementation does not handle: "
                        + xmlObject.getClass().getName());
            }
            
            try {
                return fromXMLObject(org.opensaml.xmlsec.signature.DigestMethod.class.cast(xmlObject));
            } catch (final ComponentInitializationException e) {
                throw new KeyAgreementException(e);
            }
        }
        
    }
    
}
