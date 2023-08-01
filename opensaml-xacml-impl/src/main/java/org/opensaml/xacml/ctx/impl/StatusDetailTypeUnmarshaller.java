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
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xacml.ctx.StatusDetailType;
import org.opensaml.xacml.impl.AbstractXACMLObjectUnmarshaller;

/** Unmarshaller for {@link StatusDetailType} objects. */
public class StatusDetailTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {

    /** Constructor. */
    public StatusDetailTypeUnmarshaller() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected void processChildElement(final XMLObject parentSAMLObject, final XMLObject childSAMLObject)
            throws UnmarshallingException {
        final StatusDetailType statusDetail = (StatusDetailType) parentSAMLObject;
        statusDetail.getUnknownXMLObjects().add(childSAMLObject);
    }

}