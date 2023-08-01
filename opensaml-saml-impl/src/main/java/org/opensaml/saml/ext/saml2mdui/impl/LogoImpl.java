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

package org.opensaml.saml.ext.saml2mdui.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.ext.saml2mdui.Logo;
import org.opensaml.saml.saml2.metadata.impl.LocalizedURIImpl;

/**
 * Concrete implementation of {@link Logo}.
 * @author rod widdowson
 */
public class LogoImpl extends LocalizedURIImpl implements Logo {

    /** X-Dimension of the logo. */
    @Nullable private Integer width;

    /** Y-Dimension of the logo. */
    @Nullable private Integer height;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespaceURI
     * @param elementLocalName elementLocalName
     * @param namespacePrefix namespacePrefix
     */
    protected LogoImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public Integer getHeight() {
        return height;
    }

    /** {@inheritDoc} */
    public void setHeight(@Nullable final Integer newHeight) {
         height = prepareForAssignment(height, newHeight);
    }

    /** {@inheritDoc} */
    @Nullable public Integer getWidth() {
        return width;
    }

    /** {@inheritDoc} */
    public void setWidth(@Nullable final Integer newWidth) {
        width = prepareForAssignment(width, newWidth);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean equals(final Object obj) {
        return super.equals(obj);
    }
    
    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31 + (height != null ? height : 0);
        hash = hash * 31 + (width != null ? width : 0);
        return hash;
    }

}