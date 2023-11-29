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

package org.opensaml.saml.saml2.core.impl;

import org.opensaml.core.xml.AbstractXSAnyAdapter;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.saml.saml2.core.Condition;

/**
 * Component that adapts an instance of {@link XSAny} to the interface {@link Condition}.
 */
public class ConditionXSAnyAdapter extends AbstractXSAnyAdapter implements Condition {

    /**
     * Constructor.
     *
     * @param xsAny the adapted instance
     */
    public ConditionXSAnyAdapter(XSAny xsAny) {
        super(xsAny);
    }

}
