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

package org.opensaml.core.xml.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.BaseBearing;
import org.opensaml.core.xml.IdBearing;
import org.opensaml.core.xml.LangBearing;
import org.opensaml.core.xml.SpaceBearing;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.SpaceBearing.XMLSpaceEnum;

import net.shibboleth.shared.primitive.StringSupport;

/**
 * Helper methods for working with global attributes from the XML namespace. These are namely:
 * <ol>
 *   <li>xml:id</li>
 *   <li>xml:lang</li>
 *   <li>xml:base</li>
 *   <li>xml:space</li>
 * </ol>
 */
public final class XMLAttributeSupport {

    /**
     * Private constructor.
     */
    private XMLAttributeSupport() {
    }
    
    /**
     * Adds a <code>xml:id</code> attribute to the given XML object.
     * 
     * @param xmlObject the XML object to which to add the attribute
     * @param id the Id value
     */
    public static void addXMLId(@Nonnull final XMLObject xmlObject, @Nullable final String id) {
        if (xmlObject instanceof IdBearing downcast) {
            downcast.setXMLId(id);
        } else if (xmlObject instanceof AttributeExtensibleXMLObject downcast) {
            downcast.getUnknownAttributes().put(IdBearing.XML_ID_ATTR_NAME, id);
        } else {
            throw new IllegalArgumentException("Specified object was neither IdBearing nor AttributeExtensible");
        }
    }
    
    /**
     * Gets the <code>xml:id</code> attribute from a given XML object.
     * 
     * @param xmlObject the XML object from which to extract the attribute
     * 
     * @return the value of the xml:id attribute, or null if not present
     */
    @Nullable public static String getXMLId(@Nonnull final XMLObject xmlObject) {
        if (xmlObject instanceof IdBearing downcast) {
            final String value = StringSupport.trimOrNull(downcast.getXMLId());
            if (value != null) {
                return value;
            }
        }
        if (xmlObject instanceof AttributeExtensibleXMLObject downcast) {
            return StringSupport.trimOrNull(downcast.getUnknownAttributes().get(IdBearing.XML_ID_ATTR_NAME));
        }
        return null;
    }
    
    /**
     * Adds a <code>xml:lang</code> attribute to the given XML object.
     * 
     * @param xmlObject the XML object to which to add the attribute
     * @param lang the lang value
     */
    public static void addXMLLang(@Nonnull final XMLObject xmlObject, @Nullable final String lang) {
        if (xmlObject instanceof LangBearing downcast) {
            downcast.setXMLLang(lang);
        } else if (xmlObject instanceof AttributeExtensibleXMLObject downcast) {
            downcast.getUnknownAttributes().put(LangBearing.XML_LANG_ATTR_NAME, lang);
        } else {
            throw new IllegalArgumentException("Specified object was neither LangBearing nor AttributeExtensible");
        }
    }
    
    /**
     * Gets the <code>xml:lang</code> attribute from a given XML object.
     * 
     * @param xmlObject the XML object from which to extract the attribute
     * 
     * @return the value of the xml:lang attribute, or null if not present
     */
    @Nullable public static String getXMLLang(@Nonnull final XMLObject xmlObject) {
        String value = null;
        if (xmlObject instanceof LangBearing downcast) {
            value = StringSupport.trimOrNull(downcast.getXMLLang());
            if (value != null) {
                return value;
            }
        }
        if (xmlObject instanceof AttributeExtensibleXMLObject downcast) {
            value = StringSupport.trimOrNull(downcast.getUnknownAttributes().get(LangBearing.XML_LANG_ATTR_NAME));
            return value;
        }
        return null;
    }
    
    /**
     * Adds a <code>xml:base</code> attribute to the given XML object.
     * 
     * @param xmlObject the XML object to which to add the attribute
     * @param base the base value
     */
    public static void addXMLBase(@Nonnull final XMLObject xmlObject, @Nullable final String base) {
        if (xmlObject instanceof BaseBearing downcast) {
            downcast.setXMLBase(base);
        } else if (xmlObject instanceof AttributeExtensibleXMLObject downcast) {
            downcast.getUnknownAttributes().put(BaseBearing.XML_BASE_ATTR_NAME, base);
        } else {
            throw new IllegalArgumentException("Specified object was neither BaseBearing nor AttributeExtensible");
        }
    }
    
    /**
     * Gets the <code>xml:base</code> attribute from a given XML object.
     * 
     * @param xmlObject the XML object from which to extract the attribute
     * 
     * @return the value of the xml:base attribute, or null if not present
     */
    @Nullable public static String getXMLBase(@Nonnull final XMLObject xmlObject) {
        String value = null;
        if (xmlObject instanceof BaseBearing downcast) {
            value = StringSupport.trimOrNull(downcast.getXMLBase());
            if (value != null) {
                return value;
            }
        }
        if (xmlObject instanceof AttributeExtensibleXMLObject downcast) {
            value = StringSupport.trimOrNull(downcast.getUnknownAttributes().get(BaseBearing.XML_BASE_ATTR_NAME));
            return value;
        }
        return null;
    }
    
    /**
     * Adds a <code>xml:space</code> attribute to the given XML object.
     * 
     * @param xmlObject the XML object to which to add the attribute
     * @param space the space value
     */
    public static void addXMLSpace(@Nonnull final XMLObject xmlObject, @Nonnull final XMLSpaceEnum space) {
        if (xmlObject instanceof SpaceBearing downcast) {
            downcast.setXMLSpace(space);
        } else if (xmlObject instanceof AttributeExtensibleXMLObject downcast) {
            downcast.getUnknownAttributes().put(SpaceBearing.XML_SPACE_ATTR_NAME, space.toString());
        } else {
            throw new IllegalArgumentException("Specified object was neither SpaceBearing nor AttributeExtensible");
        }
    }
    
    /**
     * Gets the <code>xml:space</code> attribute from a given XML object.
     * 
     * @param xmlObject the XML object from which to extract the attribute
     * 
     * @return the value of the xml:space attribute, or null if not present
     */
    @Nullable public static XMLSpaceEnum getXMLSpace(@Nonnull final XMLObject xmlObject) {
        XMLSpaceEnum valueEnum = null;
        if (xmlObject instanceof SpaceBearing downcast) {
            valueEnum = downcast.getXMLSpace();
            if (valueEnum != null) {
                return valueEnum;
            }
        }
        String valueString = null;
        if (xmlObject instanceof AttributeExtensibleXMLObject downcast) {
            valueString = StringSupport.trimOrNull(downcast.getUnknownAttributes().get(
                    SpaceBearing.XML_SPACE_ATTR_NAME));
            if (valueString != null) {
                return XMLSpaceEnum.parseValue(valueString);
            }
        }
        return null;
    }
    
}