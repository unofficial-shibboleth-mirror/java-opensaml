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

package org.opensaml.soap.wssecurity.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.soap.wssecurity.Password;

/**
 * PasswordImpl.
 */
public class PasswordImpl extends AttributedStringImpl implements Password {

    /** wsse:Password/@Type attribute. */
    @Nullable private String type;

    /**
     * Constructor. Default Type attribute: <code>Password.TYPE_PASSWORD_TEXT</code>
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public PasswordImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        // set default type
        type = Password.TYPE_PASSWORD_TEXT;
    }

    /** {@inheritDoc} */
    @Nullable public String getType() {
        return type;
    }

    /** {@inheritDoc} */
    public void setType(@Nullable final String newType) {
        type = prepareForAssignment(type, newType);
    }

}
