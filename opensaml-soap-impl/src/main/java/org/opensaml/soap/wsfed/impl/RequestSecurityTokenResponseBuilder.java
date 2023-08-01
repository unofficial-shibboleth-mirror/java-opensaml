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
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObjectBuilder;
import org.opensaml.soap.wsfed.RequestSecurityTokenResponse;
import org.opensaml.soap.wsfed.WSFedConstants;
import org.opensaml.soap.wsfed.WSFedObjectBuilder;

/** Builder of {@link RequestSecurityTokenResponseImpl} objects. */
public class RequestSecurityTokenResponseBuilder extends AbstractXMLObjectBuilder<RequestSecurityTokenResponse>
        implements WSFedObjectBuilder<RequestSecurityTokenResponse> {

    /** Constructor. */
    public RequestSecurityTokenResponseBuilder() {

    }

    /** {@inheritDoc} */
    @Nonnull public RequestSecurityTokenResponse buildObject() {
        return buildObject(WSFedConstants.WSFED11P_NS, RequestSecurityTokenResponse.DEFAULT_ELEMENT_LOCAL_NAME,
                WSFedConstants.WSFED1P_PREFIX);
    }

    /** {@inheritDoc} */
    @Nonnull public RequestSecurityTokenResponse buildObject(@Nullable final String namespaceURI,
            @Nonnull final String localName, @Nullable final String namespacePrefix) {
        return new RequestSecurityTokenResponseImpl(namespaceURI, localName, namespacePrefix);
    }
}