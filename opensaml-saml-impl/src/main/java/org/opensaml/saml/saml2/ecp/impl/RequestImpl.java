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

package org.opensaml.saml.saml2.ecp.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.saml2.core.IDPList;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.ecp.Request;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.MustUnderstandBearing;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 *  A concrete implementation of {@link Request}.
 */
public class RequestImpl extends AbstractXMLObject implements Request {
    
    /** IDPList child element. */
    @Nullable private IDPList idpList;
    
    /** Issuer child element. */
    @Nullable private Issuer issuer;
    
    /** ProviderName attribute. */
    @Nullable private String providerName;
    
    /** IsPassive attribute value. */
    @Nullable private XSBooleanValue isPassive;
    
    /** soap11:actor attribute. */
    @Nullable private String soap11Actor;
    
    /** soap11:mustUnderstand. */
    @Nullable private XSBooleanValue soap11MustUnderstand;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected RequestImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public IDPList getIDPList() {
        return idpList;
    }
    
    /** {@inheritDoc} */
    public void setIDPList(@Nullable final IDPList newIDPList) {
        idpList = prepareForAssignment(idpList, newIDPList);
    }

    /** {@inheritDoc} */
    @Nullable public Issuer getIssuer() {
        return issuer;
    }
    
    /** {@inheritDoc} */
    public void setIssuer(@Nullable final Issuer newIssuer) {
        issuer = prepareForAssignment(issuer, newIssuer);
    }

    /** {@inheritDoc} */
    @Nullable public String getProviderName() {
        return providerName;
    }

    /** {@inheritDoc} */
    public void setProviderName(@Nullable final String newProviderName) {
        providerName = prepareForAssignment(providerName, newProviderName);
    }

    /** {@inheritDoc} */
    @Nullable public Boolean isPassive() {
        if (isPassive != null) {
            return isPassive.getValue();
        }

        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue isPassiveXSBoolean() {
        return isPassive;
    }

    /** {@inheritDoc} */
    public void setPassive(@Nullable final Boolean newIsPassive) {
        if (newIsPassive != null) {
            isPassive = prepareForAssignment(isPassive, new XSBooleanValue(newIsPassive, false));
        } else {
            isPassive = prepareForAssignment(isPassive, null);
        }
    }

    /** {@inheritDoc} */
    public void setPassive(@Nullable final XSBooleanValue newIsPassive) {
        this.isPassive = prepareForAssignment(this.isPassive, newIsPassive);
    }
    
    /** {@inheritDoc} */
    @Nullable public Boolean isSOAP11MustUnderstand() {
        if (soap11MustUnderstand != null) {
            return soap11MustUnderstand.getValue();
        }
        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue isSOAP11MustUnderstandXSBoolean() {
        return soap11MustUnderstand;
    }

    /** {@inheritDoc} */
    public void setSOAP11MustUnderstand(@Nullable final Boolean newMustUnderstand) {
        if (newMustUnderstand != null) {
            soap11MustUnderstand = prepareForAssignment(soap11MustUnderstand, 
                    new XSBooleanValue(newMustUnderstand, true));
        } else {
            soap11MustUnderstand = prepareForAssignment(soap11MustUnderstand, null);
        }
        manageQualifiedAttributeNamespace(MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_NAME, 
                soap11MustUnderstand != null);
    }

    /** {@inheritDoc} */
    public void setSOAP11MustUnderstand(@Nullable final XSBooleanValue newMustUnderstand) {
            soap11MustUnderstand = prepareForAssignment(soap11MustUnderstand, newMustUnderstand);
            manageQualifiedAttributeNamespace(MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_NAME, 
                    soap11MustUnderstand != null);
    }

    /** {@inheritDoc} */
    @Nullable public String getSOAP11Actor() {
        return soap11Actor;
    }

    /** {@inheritDoc} */
    public void setSOAP11Actor(@Nullable final String newActor) {
        soap11Actor = prepareForAssignment(soap11Actor, newActor);
        manageQualifiedAttributeNamespace(ActorBearing.SOAP11_ACTOR_ATTR_NAME, soap11Actor != null);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (issuer != null) {
            children.add(issuer);
        }
        
        if (idpList != null) {
            children.add(idpList);
        }
        
        return CollectionSupport.copyToList(children);
    }

}