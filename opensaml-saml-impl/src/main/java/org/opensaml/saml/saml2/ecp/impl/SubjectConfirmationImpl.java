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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.ecp.SubjectConfirmation;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.MustUnderstandBearing;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;


/**
 * Concrete implementation of {@link SubjectConfirmation}.
 */
public class SubjectConfirmationImpl extends AbstractXMLObject implements SubjectConfirmation {

    /** soap11:actor attribute. */
    @Nullable private String soap11Actor;
    
    /** soap11:mustUnderstand. */
    @Nullable private XSBooleanValue soap11MustUnderstand;

    /** Method of the Confirmation. */
    @Nullable private String method;

    /** SubjectConfirmationData of the Confirmation. */
    @Nullable private SubjectConfirmationData subjectConfirmationData;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected SubjectConfirmationImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
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
    @Nullable public String getMethod() {
        return method;
    }

    /** {@inheritDoc} */
    public void setMethod(@Nullable final String newMethod) {
        method = prepareForAssignment(method, newMethod);
    }

    /** {@inheritDoc} */
    @Nullable public SubjectConfirmationData getSubjectConfirmationData() {
        return subjectConfirmationData;
    }

    /** {@inheritDoc} */
    public void setSubjectConfirmationData(@Nullable final SubjectConfirmationData newSubjectConfirmationData) {
        subjectConfirmationData = prepareForAssignment(subjectConfirmationData, newSubjectConfirmationData);

    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        
        if (subjectConfirmationData != null) {
            return CollectionSupport.singletonList(subjectConfirmationData);
        }

        return null;
    }

}