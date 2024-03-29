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

package org.opensaml.saml.ext.samlec;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.MustUnderstandBearing;
import org.opensaml.xmlsec.signature.KeyInfo;

import net.shibboleth.shared.annotation.constraint.NotEmpty;


/**
 * SAML-EC GSS-API SessionKey element.
 */
public interface SessionKey extends SAMLObject, MustUnderstandBearing, ActorBearing {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "SessionKey";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAMLEC_GSS_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAMLEC_GSS_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "SessionKeyType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAMLEC_GSS_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAMLEC_GSS_PREFIX);

    /** Algorithm attribute name. */
    @Nonnull @NotEmpty static final String ALGORITHM_ATTRIB_NAME = "Algorithm";
    
    /**
     * Get the session key derivation algorithm.
     * 
     * @return the algorithm used to derive the session key
     */
    @Nullable String getAlgorithm();

    /**
     * Set the session key derivation algorithm.
     * 
     * @param newAlgorithm the algorithm used to derive the session key
     */
    void setAlgorithm(@Nullable final String newAlgorithm);
    
    /**
     * Get the session key encryption typed.
     * 
     * @return the encryption types of the session key
     */
    @Nonnull List<EncType> getEncTypes();

    /**
     * Get the KeyInfo object that describes the session key.
     * 
     * @return the KeyInfo object that describes the session key
     */
    @Nullable KeyInfo getKeyInfo();

    /**
     * Set the KeyInfo object that describes the session key.
     * 
     * @param newKeyInfo the KeyInfo object that describes the session key
     */
    void setKeyInfo(@Nullable final KeyInfo newKeyInfo);
}
