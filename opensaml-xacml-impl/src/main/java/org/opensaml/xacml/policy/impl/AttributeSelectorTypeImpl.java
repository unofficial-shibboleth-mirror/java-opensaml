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

package org.opensaml.xacml.policy.impl;

import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.xacml.impl.AbstractXACMLObject;
import org.opensaml.xacml.policy.AttributeSelectorType;

import net.shibboleth.shared.collection.LazyList;

/**
 * Implementation {@link AttributeSelectorType}.
 */
public class AttributeSelectorTypeImpl extends AbstractXACMLObject implements AttributeSelectorType {

    /** Datatype. */
    private String dataType;

    /** Issuer. */
    private String requestContextPath;

    /** Must be present. Default = false. */
    private XSBooleanValue mustBePresentXS;

    /**
     * Constructor.
     * 
     * @param namespaceURI
     *                the namespace the element is in
     * @param elementLocalName
     *                the local name of the XML element this Object represents
     * @param namespacePrefix
     *                the prefix for the given namespace
     */
    protected AttributeSelectorTypeImpl(final String namespaceURI, final String elementLocalName,
            final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        mustBePresentXS = XSBooleanValue.valueOf("false");
    }

    /** {@inheritDoc} */
    public String getDataType() {
        return dataType;
    }

    /** {@inheritDoc} */
    public Boolean getMustBePresent() {
        if (mustBePresentXS != null) {
            return mustBePresentXS.getValue();
        }
        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public XSBooleanValue getMustBePresentXSBoolean() {
        return mustBePresentXS;
    }

    /** {@inheritDoc} */
    public String getRequestContextPath() {
        return requestContextPath;
    }

    /** {@inheritDoc} */
    public void setDataType(final String type) {
        dataType = prepareForAssignment(dataType, type);
    }

    /** {@inheritDoc} */
    public void setMustBePresentXSBoolean(final XSBooleanValue present) {
        mustBePresentXS = prepareForAssignment(mustBePresentXS, present);
    }

    /** {@inheritDoc} */
    public void setMustBePresent(final Boolean present) {
        if (present != null) {
            mustBePresentXS = prepareForAssignment(mustBePresentXS, new XSBooleanValue(present, false));
        } else {
            mustBePresentXS = prepareForAssignment(mustBePresentXS, null);
        }
    }

    /** {@inheritDoc} */
    public void setRequestContextPath(final String path) {
        requestContextPath = prepareForAssignment(this.requestContextPath, path);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        return new LazyList<>();
    }

}