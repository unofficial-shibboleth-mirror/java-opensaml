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

import org.opensaml.xmlsec.agreement.KeyAgreementParameter;

/**
 * Key agreement parameter whose presence indicates Static-Static mode is being used.
 * 
 * <p>
 * This is typically used in the encryption case for Static-Static mode, along with passing the originator's
 * (encrypting party's) credential via {@link PrivateCredential}.
 * </p>
 */
public class StaticStaticMode implements KeyAgreementParameter {
    

}
