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

/**
 * 
 */

package org.opensaml.saml.saml2.core.impl;

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.OneTimeUse;
import org.opensaml.saml.saml2.core.ProxyRestriction;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link Conditions}.
 */
@SuppressWarnings("unchecked")
public class ConditionsImpl extends AbstractXMLObject implements Conditions {

    /** A Condition. */
    @Nonnull private final IndexedXMLObjectChildrenList<Condition> conditions;

    /** Not Before conditions. */
    @Nullable private Instant notBefore;

    /** Not On Or After conditions. */
    @Nullable private Instant notOnOrAfter;

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
    @Nonnull @Live public List<Condition> getConditions() {
        return conditions;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Condition> getConditions(@Nonnull final QName typeOrName) {
        return (List<Condition>) conditions.subList(typeOrName);
    }
    
    /** {@inheritDoc} */
    @Nonnull @Live public List<AudienceRestriction> getAudienceRestrictions() {
        final QName conditionQName = new QName(SAMLConstants.SAML20_NS, AudienceRestriction.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20_PREFIX);
        return (List<AudienceRestriction>) conditions.subList(conditionQName);
    }

    /** {@inheritDoc} */
    @Nullable public OneTimeUse getOneTimeUse() {
        final QName conditionQName = new QName(SAMLConstants.SAML20_NS, OneTimeUse.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20_PREFIX);
        final List<OneTimeUse> list = (List<OneTimeUse>) conditions.subList(conditionQName);
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /** {@inheritDoc} */
    @Nullable public ProxyRestriction getProxyRestriction() {
        final QName conditionQName = new QName(SAMLConstants.SAML20_NS, ProxyRestriction.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20_PREFIX);
        final List<ProxyRestriction> list = (List<ProxyRestriction>) conditions.subList(conditionQName);
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /** {@inheritDoc} */
    @Nullable public Instant getNotBefore() {
        return notBefore;
    }

    /** {@inheritDoc} */
    public void setNotBefore(@Nullable final Instant newNotBefore) {
        this.notBefore = prepareForAssignment(this.notBefore, newNotBefore);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getNotOnOrAfter() {
        return notOnOrAfter;
    }

    /** {@inheritDoc} */
    public void setNotOnOrAfter(@Nullable final Instant newNotOnOrAfter) {
        this.notOnOrAfter = prepareForAssignment(this.notOnOrAfter, newNotOnOrAfter);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(conditions);
    }

}