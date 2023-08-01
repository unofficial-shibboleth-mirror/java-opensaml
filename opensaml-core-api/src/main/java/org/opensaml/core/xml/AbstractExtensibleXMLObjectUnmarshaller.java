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

package org.opensaml.core.xml;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.QNameSupport;

/**
 * AbstractExtensibleXMLObjectUnmarshaller unmarshalls element of type <code>xs:any</code> and with
 * <code>xs:anyAttribute</code> attributes.
 */
public abstract class AbstractExtensibleXMLObjectUnmarshaller extends AbstractElementExtensibleXMLObjectUnmarshaller {

    /**
     * Unmarshalls the <code>xs:anyAttribute</code> attributes.
     * 
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final AttributeExtensibleXMLObject anyAttribute = (AttributeExtensibleXMLObject) xmlObject;
        final QName attribQName = QNameSupport.getNodeQName(attribute);
        if (attribute.isId()) {
            anyAttribute.getUnknownAttributes().registerID(attribQName);
        }
        anyAttribute.getUnknownAttributes().put(attribQName, attribute.getValue());
    }

}