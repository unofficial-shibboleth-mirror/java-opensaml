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

package org.opensaml.core.xml.schema.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.core.xml.schema.XSBase64Binary;

import net.shibboleth.shared.primitive.StringSupport;

/**
 * Thread-safe unmarshaller for {@link XSBase64Binary} objects.
 */
public class XSBase64BinaryUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processElementContent(@Nonnull final XMLObject xmlObject, @Nonnull final String elementContent) {
        final XSBase64Binary xsBase64Binary = (XSBase64Binary) xmlObject;

        xsBase64Binary.setValue(StringSupport.trimOrNull(elementContent));
    }
}