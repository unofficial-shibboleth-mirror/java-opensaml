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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.SubjectLocality;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * A concrete implementation of {@link SubjectLocality}.
 */
public class SubjectLocalityImpl extends AbstractXMLObject implements SubjectLocality {

    /** The Address of the assertion. */
    @Nullable private String address;

    /** The DNS Name of the assertion. */
    @Nullable private String dnsName;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected SubjectLocalityImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getAddress() {
        return address;
    }

    /** {@inheritDoc} */
    public void setAddress(@Nullable final String newAddress) {
        this.address = prepareForAssignment(this.address, newAddress);
    }

    /** {@inheritDoc} */
    @Nullable public String getDNSName() {
        return dnsName;
    }

    /** {@inheritDoc} */
    public void setDNSName(@Nullable final String newDNSName) {
        this.dnsName = prepareForAssignment(this.dnsName, newDNSName);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return null;
    }

}