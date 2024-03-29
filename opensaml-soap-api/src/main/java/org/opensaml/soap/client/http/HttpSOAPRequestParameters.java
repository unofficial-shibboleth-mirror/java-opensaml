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

package org.opensaml.soap.client.http;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.opensaml.soap.client.SOAPClient.SOAPRequestParameters;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.StringSupport;

/** HTTP transported SOAP request parameters. */
@ThreadSafe
public class HttpSOAPRequestParameters implements SOAPRequestParameters {

    /** Name of the HTTP SOAPAction header. */
    @Nonnull @NotEmpty public static final String SOAP_ACTION_HEADER = "SOAPAction";

    /** HTTP SOAPAction header. */
    @Nullable private String soapAction;

    /**
     * Constructor.
     * 
     * @param action value for the SOAPAction HTTP header
     */
    public HttpSOAPRequestParameters(@Nullable final String action) {
        soapAction = StringSupport.trimOrNull(action);
    }

    /**
     * Gets the HTTP SOAPAction header.
     * 
     * @return HTTP SOAPAction header
     */
    @Nullable public String getSOAPAction() {
        return soapAction;
    }

}