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

package org.opensaml.soap.wsfed.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.soap.wsfed.AppliesTo;
import org.opensaml.soap.wsfed.RequestSecurityTokenResponse;
import org.opensaml.soap.wsfed.RequestedSecurityToken;
import org.w3c.dom.Attr;

/** A thread-safe unmarshaller for {@link RequestSecurityTokenResponse} objects. */
public class RequestSecurityTokenResponseUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final RequestSecurityTokenResponse response = (RequestSecurityTokenResponse) parentXMLObject;

        if (childXMLObject instanceof RequestedSecurityToken) {
            response.getRequestedSecurityToken().add((RequestedSecurityToken) childXMLObject);
        } else if (childXMLObject instanceof AppliesTo) {
            response.setAppliesTo((AppliesTo) childXMLObject);
        }
    }

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
    }

    /** {@inheritDoc} */
    protected void processElementContent(@Nonnull final XMLObject fedObject, @Nonnull final String content) {

    }
}