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

package org.opensaml.xmlsec.signature;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.xmlsec.signature.support.SignatureConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * XMLObject representing XML Digital Signature, version 20020212, Q element.
 */
public interface Q extends CryptoBinary {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "Q";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SignatureConstants.XMLSIG_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG_PREFIX);

}