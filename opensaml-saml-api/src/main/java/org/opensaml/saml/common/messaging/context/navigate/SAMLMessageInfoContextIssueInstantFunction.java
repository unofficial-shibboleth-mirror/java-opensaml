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

package org.opensaml.saml.common.messaging.context.navigate;

import java.time.Instant;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.navigate.ContextDataLookupFunction;
import org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext;

/** {@link ContextDataLookupFunction} that returns {@link SAMLMessageInfoContext#getMessageIssueInstant()}. */
public class SAMLMessageInfoContextIssueInstantFunction
        implements ContextDataLookupFunction<SAMLMessageInfoContext,Instant> {

    /** {@inheritDoc} */
    @Nullable public Instant apply(@Nullable final SAMLMessageInfoContext input) {
        if (input != null) {
            return input.getMessageIssueInstant();
        }
        return null;
    }

}