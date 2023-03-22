/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.MustUnderstandBearing;
import org.opensaml.soap.soap12.RelayBearing;
import org.opensaml.soap.soap12.RoleBearing;
import org.opensaml.soap.wssecurity.EncryptedHeader;
import org.opensaml.soap.wssecurity.IdBearing;
import org.opensaml.xmlsec.encryption.EncryptedData;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Implementation of {@link EncryptedHeader}.
 */
public class EncryptedHeaderImpl extends AbstractWSSecurityObject implements EncryptedHeader {
    
    /** EncryptedData child element. */
    @Nullable private EncryptedData encryptedData;
    
    /** The <code>@wsu:Id</code> atribute. */
    @Nullable private String wsuId;
    
    /** The <code>@soap11:mustUnderstand</code> atribute. */
    @Nullable private XSBooleanValue soap11MustUnderstand;
    
    /** The <code>@soap11:actor</code> atribute. */
    @Nullable private String soap11Actor;
    
    /** The <code>@soap12:mustUnderstand</code> atribute. */
    @Nullable private XSBooleanValue soap12MustUnderstand;
    
    /** The <code>@soap12:role</code> atribute. */
    @Nullable private String soap12Role;
    
    /** The <code>@soap12:relay</code> atribute. */
    @Nullable private XSBooleanValue soap12Relay;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public EncryptedHeaderImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public EncryptedData getEncryptedData() {
        return encryptedData;
    }

    /** {@inheritDoc} */
    public void setEncryptedData(@Nullable final EncryptedData newEncryptedData) {
        encryptedData = prepareForAssignment(encryptedData, newEncryptedData);
    }

    /** {@inheritDoc} */
    @Nullable public String getWSUId() {
        return wsuId;
    }

    /** {@inheritDoc} */
    public void setWSUId(@Nullable final String newId) {
        final String oldId = wsuId;
        wsuId = prepareForAssignment(wsuId, newId);
        registerOwnID(oldId, wsuId);
        manageQualifiedAttributeNamespace(IdBearing.WSU_ID_ATTR_NAME, wsuId != null);
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
    @Nullable public Boolean isSOAP12MustUnderstand() {
        if (soap12MustUnderstand != null) {
            return soap12MustUnderstand.getValue();
        }
        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue isSOAP12MustUnderstandXSBoolean() {
        return soap12MustUnderstand;
    }

    /** {@inheritDoc} */
    public void setSOAP12MustUnderstand(@Nullable final Boolean newMustUnderstand) {
        if (newMustUnderstand != null) {
            soap12MustUnderstand = prepareForAssignment(soap12MustUnderstand, 
                    new XSBooleanValue(newMustUnderstand, false));
        } else {
            soap12MustUnderstand = prepareForAssignment(soap12MustUnderstand, null);
        }
        manageQualifiedAttributeNamespace(SOAP12_MUST_UNDERSTAND_ATTR_NAME, soap12MustUnderstand != null);
    }

    /** {@inheritDoc} */
    public void setSOAP12MustUnderstand(@Nullable final XSBooleanValue newMustUnderstand) {
            soap12MustUnderstand = prepareForAssignment(soap12MustUnderstand, newMustUnderstand);
            manageQualifiedAttributeNamespace(SOAP12_MUST_UNDERSTAND_ATTR_NAME,  soap12MustUnderstand != null);
    }

    /** {@inheritDoc} */
    @Nullable public String getSOAP12Role() {
        return soap12Role;
    }

    /** {@inheritDoc} */
    public void setSOAP12Role(@Nullable final String newRole) {
        soap12Role = prepareForAssignment(soap12Role, newRole);
        manageQualifiedAttributeNamespace(RoleBearing.SOAP12_ROLE_ATTR_NAME, soap12Role != null);
    }

    /** {@inheritDoc} */
    @Nullable public Boolean isSOAP12Relay() {
        if (soap12Relay != null) {
            return soap12Relay.getValue();
        }
        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue isSOAP12RelayXSBoolean() {
        return soap12Relay;
    }

    /** {@inheritDoc} */
    public void setSOAP12Relay(@Nullable final Boolean newRelay) {
        if (newRelay != null) {
            soap12Relay = prepareForAssignment(soap12Relay, 
                    new XSBooleanValue(newRelay, false));
        } else {
            soap12Relay = prepareForAssignment(soap12Relay, null);
        }
        manageQualifiedAttributeNamespace(RelayBearing.SOAP12_RELAY_ATTR_NAME, soap12Relay != null);
    }

    /** {@inheritDoc} */
    public void setSOAP12Relay(@Nullable final XSBooleanValue newRelay) {
            soap12Relay = prepareForAssignment(soap12Relay, newRelay);
            manageQualifiedAttributeNamespace(RelayBearing.SOAP12_RELAY_ATTR_NAME, soap12Relay != null);
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        
        if (encryptedData != null) {
            return CollectionSupport.singletonList(encryptedData);
        }
        
        return null;
    }

}