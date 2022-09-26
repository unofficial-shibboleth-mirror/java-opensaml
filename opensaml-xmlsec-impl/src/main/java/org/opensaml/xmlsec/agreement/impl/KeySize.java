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

import org.opensaml.xmlsec.agreement.KeyAgreementParameter;

import net.shibboleth.shared.logic.Constraint;

/**
 * Key agreement parameter used to explicitly represent the size of the derived key.
 */
public class KeySize implements KeyAgreementParameter {
    
    /** Key size. */
    @Nonnull private Integer size;
    
    /**
     * Constructor.
     *
     * @param keySize the key size, in bits
     */
    public KeySize(@Nonnull final Integer keySize) {
        size = Constraint.isNotNull(keySize, "Specified key size was null");
    }
    
    /**
     * Get the key size, in bits.
     * 
     * @return the key size in bits
     */
    @Nonnull public Integer getSize() {
       return size; 
    }

}
