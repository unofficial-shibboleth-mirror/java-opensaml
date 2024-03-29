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

package org.opensaml.saml.saml2.ecp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.IDPList;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.MustUnderstandBearing;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 ECP Request SOAP header.
 */
public interface Request extends SAMLObject, MustUnderstandBearing, ActorBearing {
    
    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "Request";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
        new QName(SAMLConstants.SAML20ECP_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20ECP_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "RequestType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
        new QName(SAMLConstants.SAML20ECP_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20ECP_PREFIX);

    /** ProviderName attribute name. */
    @Nonnull @NotEmpty static final String PROVIDER_NAME_ATTRIB_NAME = "ProviderName";

    /** IsPassive attribute name. */
    @Nonnull @NotEmpty static final String IS_PASSIVE_NAME_ATTRIB_NAME = "IsPassive";
    
    /**
     * Get the Issuer child elemet.
     * 
     * @return the Issuer child element
     */
    @Nullable Issuer getIssuer();
    
    /**
     * Set the Issuer child elemet.
     * 
     * @param newIssuer the new Issuer child element
     */
    void setIssuer(@Nullable final Issuer newIssuer);
    
    /**
     * Get the IDPList child element.
     * 
     * @return the IDPList child element
     */
    @Nullable IDPList getIDPList();
    
    /**
     * Set the IDPList child element.
     * 
     * @param newIDPList the new IDPList child element
     */
    void setIDPList(@Nullable final IDPList newIDPList);
    
    /**
     * Get the ProviderName attribute value.
     * 
     * @return the ProviderName attribute value
     */
    @Nullable String getProviderName();
    
    /**
     * Set the ProviderName attribute value.
     * 
     * @param newProviderName the new ProviderName attribute value
     */
    void setProviderName(@Nullable final String newProviderName);
    
    /**
     * Get the IsPassive attribute value.
     * 
     * @return the IsPassive attribute value
     */
    @Nullable Boolean isPassive();
    
    /**
     * Get the IsPassive attribute value.
     * 
     * @return the IsPassive attribute value
     */
    @Nullable XSBooleanValue isPassiveXSBoolean();
    
    /**
     * Set the IsPassive attribute value.
     * 
     * @param newIsPassive the new IsPassive attribute value
     */
    void setPassive(@Nullable final Boolean newIsPassive);
    
    /**
     * Set the IsPassive attribute value.
     * 
     * @param newIsPassive the new IsPassive attribute value
     */
    void setPassive(@Nullable final XSBooleanValue newIsPassive);

}
