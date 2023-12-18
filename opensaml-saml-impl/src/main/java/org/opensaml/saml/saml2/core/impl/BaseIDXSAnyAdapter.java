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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXSAnyAdapter;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.saml.saml2.core.BaseID;

/**
 * Component that adapts an instance of {@link XSAny} to the interface {@link BaseID}.
 */
public class BaseIDXSAnyAdapter extends AbstractXSAnyAdapter implements BaseID {

    /**
     * Constructor.
     *
     * @param xsAny the adapted instance
     */
    public BaseIDXSAnyAdapter(@Nonnull final XSAny xsAny) {
        super(xsAny);
    }

    /** {@inheritDoc} */
    @Override
    public String getNameQualifier() {
        return getAdapted().getUnknownAttributes().get(new QName(BaseID.NAME_QUALIFIER_ATTRIB_NAME));
    }

    /** {@inheritDoc} */
    @Override
    public void setNameQualifier(@Nullable final String newNameQualifier) {
        getAdapted().getUnknownAttributes().put(new QName(BaseID.NAME_QUALIFIER_ATTRIB_NAME), newNameQualifier);
    }

    /** {@inheritDoc} */
    @Override
    public String getSPNameQualifier() {
        return getAdapted().getUnknownAttributes().get(new QName(BaseID.SP_NAME_QUALIFIER_ATTRIB_NAME));
    }

    /** {@inheritDoc} */
    @Override
    public void setSPNameQualifier(@Nullable final String newSPNameQualifier) {
        getAdapted().getUnknownAttributes().put(new QName(BaseID.SP_NAME_QUALIFIER_ATTRIB_NAME), newSPNameQualifier);
    }

}
