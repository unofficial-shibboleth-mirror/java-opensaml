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

package org.opensaml.core.xml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.xml.XMLConstants;

/**
 * Interface for element having a <code>@xml:space</code> attribute.
 * 
 */
public interface SpaceBearing {
    
    /** Enum representing the allowed values of the xml:space attribute. */
    enum XMLSpaceEnum {
        /** xml:space value "default". */
        DEFAULT,
        /** xml:space value "preserve". */
        PRESERVE;
        
        // Unfortunately "default" is a reserved word in Java, so the enum value above has to be upper case
        // and we have the mess below.
        
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
        
        /**
         * Parse a string value into an XMLSpaceEnum.
         * 
         * <p>
         * The legal values are "default" and "preserve".
         * </p>
         * 
         * @param value the value to parse
         * @return the corresponding XMLSpaceEnum
         */
        public static XMLSpaceEnum parseValue(final String value) {
            return XMLSpaceEnum.valueOf(value.toUpperCase());
        }
        
    }

    /** The <code>space</code> attribute local name. */
    @Nonnull @NotEmpty static final String XML_SPACE_ATTR_LOCAL_NAME = "space";

    /** The <code>xml:space</code> qualified attribute name. */
    @Nonnull static final QName XML_SPACE_ATTR_NAME =
        new QName(XMLConstants.XML_NS, XML_SPACE_ATTR_LOCAL_NAME, XMLConstants.XML_PREFIX);

    /**
     * Returns the <code>@xml:space</code> attribute value.
     * 
     * @return The <code>@xml:space</code> attribute value or <code>null</code>.
     */
    @Nullable XMLSpaceEnum getXMLSpace();

    /**
     * Sets the <code>@xml:space</code> attribute value.
     * 
     * @param newSpace The <code>@xml:space</code> attribute value
     */
    void setXMLSpace(@Nullable final XMLSpaceEnum newSpace);

}