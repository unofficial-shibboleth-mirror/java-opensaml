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

import org.opensaml.core.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;

/**
 * AbstractElementExtensibleUnmarshaller unmarshalls element of type <code>xs:any</code>, but without
 * <code>xs:anyAttribute</code> attributes or text content.
 */
public abstract class AbstractElementExtensibleXMLObjectUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /**
     * Unmarshalls all child elements in the <code>xs:any</code> list.
     * 
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final ElementExtensibleXMLObject any = (ElementExtensibleXMLObject) parentXMLObject;
        any.getUnknownXMLObjects().add(childXMLObject);
    }

}