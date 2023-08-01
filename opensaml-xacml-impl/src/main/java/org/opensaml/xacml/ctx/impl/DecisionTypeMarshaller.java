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

package org.opensaml.xacml.ctx.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.xacml.ctx.DecisionType;
import org.opensaml.xacml.impl.AbstractXACMLObjectMarshaller;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.ElementSupport;

/** Marshaller for {@link DecisionType} objects. */
public class DecisionTypeMarshaller extends AbstractXACMLObjectMarshaller {

    /** Constructor. */
    public DecisionTypeMarshaller() {
        super();
    }

    /** {@inheritDoc} */
    protected void marshallElementContent(final XMLObject samlObject, final Element domElement)
            throws MarshallingException {
        final DecisionType decision = (DecisionType) samlObject;
        ElementSupport.appendTextContent(domElement, decision.getDecision().toString());
    }

}