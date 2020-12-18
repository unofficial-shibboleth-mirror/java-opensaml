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

package org.opensaml.xmlsec.agreement;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.signature.KeyInfo;

/**
 * A key agreement parameter which is capable of expressing its own {@link XMLObject} representation.
 * 
 * <p>
 * Such parameter representations are typically used in populating the {@link AgreementMethod}
 * child of a {@link KeyInfo}.
 * </p>
 */
public interface XMLExpressableKeyAgreementParameter extends KeyAgreementParameter {
    
    /**
     * Build an {@link XMLObject} instance representing this parameter.
     * 
     * @return the XML object instance
     */
    @Nonnull public XMLObject buildXMLObject();

}
