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

package org.opensaml.xmlsec.testing;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.BasicProviderKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.DEREncodedKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.DSAKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.ECKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.InlineX509DataProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.RSAKeyValueProvider;

/**
 * Helper methods for security-related requirements.
 */
public final class XMLSecurityTestingSupport {
    
    private XMLSecurityTestingSupport() { }

    /**
     * Get a basic KeyInfo credential resolver which can process standard inline
     * data - RSAKeyValue, DSAKeyValue, X509Data.
     * 
     * @return a new KeyInfoCredentialResolver instance
     */
    @Nonnull public static KeyInfoCredentialResolver buildBasicInlineKeyInfoResolver() {
        return new BasicProviderKeyInfoCredentialResolver(getBasicInlineKeyInfoProviders());
    }
    
    @Nonnull public static List<KeyInfoProvider> getBasicInlineKeyInfoProviders() {
        final List<KeyInfoProvider> providers = new ArrayList<>();
        providers.add( new RSAKeyValueProvider() );
        providers.add( new DSAKeyValueProvider() );
        providers.add( new ECKeyValueProvider() );
        providers.add( new DEREncodedKeyValueProvider() );
        providers.add( new InlineX509DataProvider() );
        return providers;
    }

}