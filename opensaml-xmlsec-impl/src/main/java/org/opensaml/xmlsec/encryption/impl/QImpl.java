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

package org.opensaml.xmlsec.encryption.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.xmlsec.encryption.Q;
import org.opensaml.xmlsec.signature.impl.CryptoBinaryImpl;


/**
 * Concrete implementation of {@link Q}.
 */
public class QImpl extends CryptoBinaryImpl implements Q {

    /**
     * Constructor.
     *
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected QImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

}