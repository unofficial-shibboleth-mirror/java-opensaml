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

package org.opensaml.security.crypto.ec;

import java.security.spec.ECParameterSpec;

import javax.annotation.Nonnull;

/**
 * Descriptor for an EC named curve.
 */
public interface NamedCurve {
    
    /**
     * Get the curve's object identifier (OID).
     * 
     * @return the OID
     */
    @Nonnull String getObjectIdentifier();
    
    /**
     * Get the curve's URI.
     * 
     * @return the URI
     */
    @Nonnull default String getURI() {
        return "urn:oid:" + getObjectIdentifier(); 
    }
    
    /**
     * Get the curve's canonical name by which it is known to the Java Cryptography Architecture (JCA).
     * 
     * @return the name
     */
    @Nonnull String getName();
    
    /**
     * Get the curve's {@link ECParameterSpec}.
     * 
     * @return the parameter spec instance
     */
    @Nonnull ECParameterSpec getParameterSpec();
    
    /**
     * Get the length of a key based on the curve.
     * 
     * @return the key length, in bits
     */
    default int getKeyLength() {
        return getParameterSpec().getCurve().getField().getFieldSize();
    }

}