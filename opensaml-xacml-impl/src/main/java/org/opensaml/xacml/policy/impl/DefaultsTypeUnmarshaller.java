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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.xacml.impl.AbstractXACMLObjectUnmarshaller;
import org.opensaml.xacml.policy.DefaultsType;

/** Unmarshaller for {@link DefaultsType}. */
public class DefaultsTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(final XMLObject parentXMLObject, final XMLObject childXMLObject)
            throws UnmarshallingException {
        if (childXMLObject instanceof XSString) {
            final DefaultsType defaultType = (DefaultsType) parentXMLObject;
            defaultType.setXPathVersion((XSString) childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}