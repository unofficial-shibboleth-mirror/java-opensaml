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

package org.opensaml.saml.security.impl;

import java.util.List;

import javax.annotation.Nullable;

import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.security.credential.CredentialContext;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A credential context for credentials resolved from a {@link org.opensaml.xmlsec.signature.KeyInfo} that was found in
 * SAML 2 metadata.
 */
public class SAMLMDCredentialContext implements CredentialContext {

    /** Key descriptor which contained the KeyInfo used. */
    @Nullable private KeyDescriptor keyDescriptor;

    /** Role in which credential was resolved. */
    @Nullable private RoleDescriptor role;

    /** Encryption methods associated with the credential. */
    @Nullable private List<EncryptionMethod> encMethods;

    /**
     * Constructor.
     * 
     * @param descriptor the KeyDescriptor context from which a credential was resolved
     */
    public SAMLMDCredentialContext(final KeyDescriptor descriptor) {
        keyDescriptor = descriptor;
        if (descriptor != null) {
            // KeyDescriptor / EncryptionMethod
            encMethods = CollectionSupport.copyToList(descriptor.getEncryptionMethods());
            // KeyDescriptor -> RoleDescriptor
            role = (RoleDescriptor) descriptor.getParent();
        }
    }

    /**
     * Get the key descriptor context.
     * 
     * @return key descriptor
     */
    @Nullable public KeyDescriptor getKeyDescriptor() {
        return keyDescriptor;
    }

    /**
     * Return the list of {@link EncryptionMethod}'s associated with credential context.
     * 
     * @return a list of SAML metadata encryption method associated with this context
     */
    @Nullable @NotLive @Unmodifiable public List<EncryptionMethod> getEncryptionMethods() {
        return encMethods;
    }

    /**
     * Get the role descriptor context.
     * 
     * @return role descriptor
     */
    @Nullable public RoleDescriptor getRoleDescriptor() {
        return role;
    }

}