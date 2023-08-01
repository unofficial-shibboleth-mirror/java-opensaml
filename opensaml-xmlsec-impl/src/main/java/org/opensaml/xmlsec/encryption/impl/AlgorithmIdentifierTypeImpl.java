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

package org.opensaml.xmlsec.encryption.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.AlgorithmIdentifierType;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Abstract implementation of {@link AlgorithmIdentifierType}.
 */
public abstract class AlgorithmIdentifierTypeImpl extends AbstractXMLObject implements AlgorithmIdentifierType {
    
    /** Algorithm attribute value. */
    @Nullable private String algorithm;
    
    /** Parameters child element. */
    @Nullable private XMLObject parameters;
    
    /**
     * Constructor.
     *
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected AlgorithmIdentifierTypeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getAlgorithm() {
        return algorithm;
    }

    /** {@inheritDoc} */
    public void setAlgorithm(@Nullable final String newAlgorithm) {
        algorithm = prepareForAssignment(this.algorithm, newAlgorithm);
    }

    /** {@inheritDoc} */
    @Nullable public XMLObject getParameters() {
        return parameters;
    }

    /** {@inheritDoc} */
    public void setParameters(@Nullable final XMLObject newParameters) {
        parameters = prepareForAssignment(parameters, newParameters);
    }
    
    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (parameters != null) {
            children.add(parameters);
        }
        
        return CollectionSupport.copyToList(children);
    }

}