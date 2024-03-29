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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.xacml.impl.AbstractXACMLObject;
import org.opensaml.xacml.policy.DefaultsType;

/** Implementation for {@link DefaultsType}. */
public class DefaultsTypeImpl extends AbstractXACMLObject implements DefaultsType {

    /** XPath version. */
    private XSString xPathVersion;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected DefaultsTypeImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public XSString getXPathVersion() {
        return xPathVersion;
    }

    /** {@inheritDoc} */
    public void setXPathVersion(final XSString version) {
        this.xPathVersion = prepareForAssignment(this.xPathVersion, version);

    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (xPathVersion != null) {
            children.add(xPathVersion);
        }
        return Collections.unmodifiableList(children);

    }
}