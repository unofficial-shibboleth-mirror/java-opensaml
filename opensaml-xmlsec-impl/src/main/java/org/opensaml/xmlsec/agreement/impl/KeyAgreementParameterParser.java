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
import org.opensaml.xmlsec.agreement.KeyAgreementException;
import org.opensaml.xmlsec.agreement.KeyAgreementParameter;

/**
 * A component which parses an {@link XMLObject} into an instance of {@link KeyAgreementParameter}.
 */
public interface KeyAgreementParameterParser {
    
    /**
     * Evaluate whether the specified {@link XMLObject} is handled by the implementation.
     * 
     * @param xmlObject the XML object to evaluate
     * 
     * @return true if handles, false if not
     */
    boolean handles(@Nonnull final XMLObject xmlObject);
    
    /**
     * Parse the specified {@link XMLObject} into a {@link KeyAgreementParameter}.
     * 
     * @param xmlObject the XMLObject to be parsed
     * 
     * @return the new key agreement parameter instance
     * 
     * @throws KeyAgreementException if parameter parsing of the supplied object fails
     */
    KeyAgreementParameter parse(@Nonnull final XMLObject xmlObject) throws KeyAgreementException;

}
