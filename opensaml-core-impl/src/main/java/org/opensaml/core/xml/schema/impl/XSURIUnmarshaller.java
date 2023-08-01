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
import org.opensaml.core.xml.schema.XSURI;

/**
 * A thread-safe unmarshaller for {@link XSURI}.
 */
public class XSURIUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processElementContent(@Nonnull final XMLObject xmlObject, @Nonnull final String elementContent) {
        final XSURI uri = (XSURI) xmlObject;
        uri.setURI(elementContent);
    }
}