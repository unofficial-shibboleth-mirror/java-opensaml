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

package org.opensaml.security.crypto.ec.curves;

import javax.annotation.Nonnull;

/**
 * Descriptor for named curve 'brainpoolP256r1', OID: 1.3.36.3.3.2.8.1.1.7.
 */
public class BrainpoolP256r1 extends AbstractNamedCurve  {

    /** {@inheritDoc} */
    @Nonnull public String getObjectIdentifier() {
        return "1.3.36.3.3.2.8.1.1.7";
    }

    /** {@inheritDoc} */
    @Nonnull public String getName() {
        return "brainpoolP256r1";
    }

}
