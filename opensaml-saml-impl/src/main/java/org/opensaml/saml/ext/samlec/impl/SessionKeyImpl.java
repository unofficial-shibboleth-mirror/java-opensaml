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

package org.opensaml.saml.ext.samlec.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.ext.samlec.EncType;
import org.opensaml.saml.ext.samlec.SessionKey;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.MustUnderstandBearing;
import org.opensaml.xmlsec.signature.KeyInfo;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;


/**
 * Concrete implementation of {@link SessionKey}.
 */
public class SessionKeyImpl extends AbstractXMLObject implements SessionKey {

    /** soap11:actor attribute. */
    @Nullable private String soap11Actor;
    
    /** soap11:mustUnderstand. */
    @Nullable private XSBooleanValue soap11MustUnderstand;

    /** Algorithm attribute. */
    @Nullable private String algorithm;
    
    /** EncType children. */
    @Nonnull private final XMLObjectChildrenList<EncType> encTypes;

    /** KeyInfo child. */
    @Nullable private KeyInfo keyInfo;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected SessionKeyImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        
        encTypes = new XMLObjectChildrenList<>(this);
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
    public String getSOAP11Actor() {
        return soap11Actor;
    }

    /** {@inheritDoc} */
    public void setSOAP11Actor(@Nullable final String newActor) {
        soap11Actor = prepareForAssignment(soap11Actor, newActor);
        manageQualifiedAttributeNamespace(ActorBearing.SOAP11_ACTOR_ATTR_NAME, soap11Actor != null);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getAlgorithm() {
        return algorithm;
    }

    /** {@inheritDoc} */
    public void setAlgorithm(@Nullable final String newAlgorithm) {
        algorithm = prepareForAssignment(algorithm, newAlgorithm);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<EncType> getEncTypes() {
        return encTypes;
    }
    
    /** {@inheritDoc} */
    @Nullable public KeyInfo getKeyInfo() {
        return keyInfo;
    }

    /** {@inheritDoc} */
    public void setKeyInfo(@Nullable final KeyInfo newKeyInfo) {
        keyInfo = prepareForAssignment(keyInfo, newKeyInfo);
    }
    
    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        children.addAll(encTypes);
        
        if (keyInfo != null) {
            children.add(keyInfo);
        }

        return CollectionSupport.copyToList(children);
    }

}