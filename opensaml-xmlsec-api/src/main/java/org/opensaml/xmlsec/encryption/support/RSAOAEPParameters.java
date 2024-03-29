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

package org.opensaml.xmlsec.encryption.support;

import javax.annotation.Nullable;

import net.shibboleth.shared.primitive.StringSupport;

/**
 * Class for representing parameter inputs to the RSA-OAEP key transport algorithm.
 */
public class RSAOAEPParameters {
    
    /** Digest method algorithm URI. */
    @Nullable private String digestMethod;
    
    /** Mask generation function (MGF) algorithm URI. */
    @Nullable private String maskGenerationFunction;
    
    /** Base64-encoded OAEPParams value. */
    @Nullable private String oaepParams;

    /** Constructor. */
    public RSAOAEPParameters() { }
    
    /**
     * Constructor.
     *
     * @param digest the digest method
     * @param  mgf the mask generation function (MGF)
     * @param params the OAEPParms (base64-encoded)
     */
    public RSAOAEPParameters(@Nullable final String digest, @Nullable final String mgf, @Nullable final String params) {
        setDigestMethod(digest);
        setMaskGenerationFunction(mgf);
        setOAEPparams(params);
    }
    
    /**
     * Determine whether the parameters instance is empty, meaning it has no values populated for any data properties.
     * 
     * @return true if all property values are null, false otherwise
     */
    public boolean isEmpty() {
        return getDigestMethod() == null && getMaskGenerationFunction() == null && getOAEPParams() == null;
    }

    /**
     * Determine whether the parameters instance is complete, meaning values are populated for all data properties.
     * 
     * @return true if all property values are populated, false otherwise
     */
    public boolean isComplete() {
        return getDigestMethod() != null && getMaskGenerationFunction() != null && getOAEPParams() != null;
    }

    /**
     * Get the digest method algorithm URI.
     * 
     * @return the digest method URI
     */
    @Nullable public String getDigestMethod() {
        return digestMethod;
    }

    /**
     * Set the digest method algorithm URI.
     * 
     * @param value the new digest method URI
     */
    public void setDigestMethod(@Nullable final String value) {
        digestMethod = StringSupport.trimOrNull(value);
    }

    /**
     * Get the mask generation function (MGF) algorithm URI.
     * 
     * @return the MGF URI
     */
    @Nullable public String getMaskGenerationFunction() {
        return maskGenerationFunction;
    }

    /**
     * Set the mask generation function (MGF) algorithm URI.
     * 
     * @param value the new MGF algorithm URI
     */
    public void setMaskGenerationFunction(@Nullable final String value) {
        maskGenerationFunction = StringSupport.trimOrNull(value);
    }

    /**
     * Get the base64-encoded OAEPParams value.
     * 
     * @return the base64-encoded OAEPParams
     */
    @Nullable public String getOAEPParams() {
        return oaepParams;
    }

    /**
     * Set the base64-encoded OAEPParams value.
     * 
     * @param value the new base64-encoded OAEPParams value
     */
    public void setOAEPparams(@Nullable final String value) {
        oaepParams = StringSupport.trimOrNull(value);
    }

}