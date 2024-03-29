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

package org.opensaml.xmlsec.keyinfo.impl;

import java.util.List;

import javax.annotation.Nonnull;

import org.opensaml.security.credential.impl.AbstractChainingCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;

import net.shibboleth.shared.annotation.ParameterName;

/**
 * A concrete implementation of {@link AbstractChainingCredentialResolver} which is scoped to 
 * the {@link KeyInfoCredentialResolver} type.
 */
public class ChainingKeyInfoCredentialResolver extends AbstractChainingCredentialResolver<KeyInfoCredentialResolver>
        implements KeyInfoCredentialResolver {

    /**
     * Constructor.
     *
     * @param resolverChain the chain of KeyInfo credential resolvers
     */
    public ChainingKeyInfoCredentialResolver(
            @Nonnull @ParameterName(name="resolverChain") final List<KeyInfoCredentialResolver> resolverChain) {
        super(resolverChain);
    }
    
}
