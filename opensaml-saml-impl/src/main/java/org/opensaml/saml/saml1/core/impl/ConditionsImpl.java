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

package org.opensaml.saml.saml1.core.impl;

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.AudienceRestrictionCondition;
import org.opensaml.saml.saml1.core.Condition;
import org.opensaml.saml.saml1.core.Conditions;
import org.opensaml.saml.saml1.core.DoNotCacheCondition;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * This is a concrete implementation of the {@link Conditions} interface.
 */
@SuppressWarnings("unchecked")
public class ConditionsImpl extends AbstractXMLObject implements Conditions {

    /** Value saved in the NotBefore attribute. */
    @Nullable private Instant notBefore;

    /** Value saved in the NotOnOrAfter attribute. */
    @Nullable private Instant notOnOrAfter;

    /** Set containing all the Conditions. */
    @Nonnull private final IndexedXMLObjectChildrenList<Condition> conditions;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected ConditionsImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        conditions = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getNotBefore() {
        return notBefore;
    }

    /** {@inheritDoc} */
    public void setNotBefore(@Nullable final Instant dt) {
        notBefore = prepareForAssignment(notBefore, dt);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getNotOnOrAfter() {
        return notOnOrAfter;
    }

    /** {@inheritDoc} */
    public void setNotOnOrAfter(@Nullable final Instant dt) {
        notOnOrAfter = prepareForAssignment(notOnOrAfter, dt);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Condition> getConditions() {
        return conditions;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Condition> getConditions(@Nonnull final QName typeOrName) {
        return (List<Condition>) conditions.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AudienceRestrictionCondition> getAudienceRestrictionConditions() {
        final QName qname = new QName(SAMLConstants.SAML1_NS, AudienceRestrictionCondition.DEFAULT_ELEMENT_LOCAL_NAME);
        return (List<AudienceRestrictionCondition>) conditions.subList(qname);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<DoNotCacheCondition> getDoNotCacheConditions() {
        final QName qname = new QName(SAMLConstants.SAML1_NS, DoNotCacheCondition.DEFAULT_ELEMENT_LOCAL_NAME);
        return (List<DoNotCacheCondition>) conditions.subList(qname);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(conditions);
    }

}