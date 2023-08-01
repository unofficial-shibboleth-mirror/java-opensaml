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

package org.opensaml.xmlsec.signature.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xmlsec.signature.Transform;
import org.opensaml.xmlsec.signature.Transforms;

/**
 * A thread-safe Unmarshaller for {@link org.opensaml.xmlsec.signature.Transforms} objects.
 */
public class TransformsUnmarshaller extends AbstractXMLSignatureUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final Transforms transforms = (Transforms) parentXMLObject;

        if (childXMLObject instanceof Transform) {
            transforms.getTransforms().add((Transform) childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
