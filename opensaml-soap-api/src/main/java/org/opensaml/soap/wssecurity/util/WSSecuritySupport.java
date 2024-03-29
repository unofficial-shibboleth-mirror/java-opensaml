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

package org.opensaml.soap.wssecurity.util;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.LazyList;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.xml.XMLConstants;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.soap.wssecurity.IdBearing;
import org.opensaml.soap.wssecurity.TokenTypeBearing;
import org.opensaml.soap.wssecurity.UsageBearing;

/**
 * Helper methods for working with WS-Security.
 */
public final class WSSecuritySupport {

    /**
     * Private constructor.
     */
    private WSSecuritySupport() {
    }
    
    /**
     * Adds a <code>wsu:Id</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param id the Id value
     */
    public static void addWSUId(@Nonnull final XMLObject soapObject, @Nonnull @NotEmpty final String id) {
        if (soapObject instanceof IdBearing) {
            ((IdBearing)soapObject).setWSUId(id);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject)soapObject).getUnknownAttributes()
                .put(IdBearing.WSU_ID_ATTR_NAME, id);
        } else {
            throw new IllegalArgumentException("Specified object was neither IdBearing nor AttributeExtensible");
        }
    }
    
    /**
     * Gets the <code>wsu:Id</code> attribute from a given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return the value of the Id attribute, or null if not present
     */
    @Nullable public static String getWSUId(@Nonnull final XMLObject soapObject) {
        String value = null;
        if (soapObject instanceof IdBearing) {
            value = StringSupport.trimOrNull(((IdBearing)soapObject).getWSUId());
            if (value != null) {
                return value;
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            value = StringSupport.trimOrNull(((AttributeExtensibleXMLObject)soapObject)
                        .getUnknownAttributes().get(IdBearing.WSU_ID_ATTR_NAME));
            return value;
        }
        return null;
    }
    
    /**
     * Adds a <code>wsse11:TokenType</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param tokenType the tokenType value
     */
    public static void addWSSE11TokenType(@Nonnull final XMLObject soapObject, @Nullable final String tokenType) {
        if (soapObject instanceof TokenTypeBearing) {
            ((TokenTypeBearing)soapObject).setWSSE11TokenType(tokenType);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject)soapObject).getUnknownAttributes()
                .put(TokenTypeBearing.WSSE11_TOKEN_TYPE_ATTR_NAME, tokenType);
        } else {
            throw new IllegalArgumentException("Specified object was neither TokenTypeBearing nor AttributeExtensible");
        }
    }
    
    /**
     * Gets the <code>wsse11:TokenType</code> attribute from a given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return the value of the tokenType attribute, or null if not present
     */
    @Nullable public static String getWSSE11TokenType(@Nonnull final XMLObject soapObject) {
        String value = null;
        if (soapObject instanceof TokenTypeBearing) {
            value = StringSupport.trimOrNull(((TokenTypeBearing)soapObject).getWSSE11TokenType());
            if (value != null) {
                return value;
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            value = StringSupport.trimOrNull(((AttributeExtensibleXMLObject)soapObject)
                        .getUnknownAttributes().get(TokenTypeBearing.WSSE11_TOKEN_TYPE_ATTR_NAME));
            return value;
        }
        return null;
    }
    
    /**
     * Adds a single <code>wsse:Usage</code> value to the given SOAP object. If an existing <code>wsse:Usage</code>
     * attribute is present, the given usage will be added to the existing list.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param usage the usage to add
     */
    public static void addWSSEUsage(@Nonnull final XMLObject soapObject, @Nonnull @NotEmpty final String usage) {
        if (soapObject instanceof UsageBearing) {
            final UsageBearing usageBearing = (UsageBearing) soapObject;
            List<String> list = usageBearing.getWSSEUsages();
            if (list == null) {
                list = new LazyList<>();
                usageBearing.setWSSEUsages(list);
            }
            list.add(usage);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            final AttributeMap am =  ((AttributeExtensibleXMLObject)soapObject).getUnknownAttributes();
            String list = am.get(UsageBearing.WSSE_USAGE_ATTR_NAME);
            if (list == null) {
                list = usage;
            } else {
                list = list + " " + usage;
            }
            am.put(UsageBearing.WSSE_USAGE_ATTR_NAME, list);
        } else {
            throw new IllegalArgumentException("Specified object was neither UsageBearing nor AttributeExtensible");
        }
    }
    
    /**
     * Adds a <code>wsse:Usage</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param usages the list of usages to add
     */
    public static void addWSSEUsages(@Nonnull final XMLObject soapObject, @Nonnull final List<String> usages) {
        if (soapObject instanceof UsageBearing) {
            ((UsageBearing)soapObject).setWSSEUsages(usages);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject)soapObject).getUnknownAttributes()
                .put(UsageBearing.WSSE_USAGE_ATTR_NAME, 
                        StringSupport.listToStringValue(usages, " "));
        } else {
            throw new IllegalArgumentException("Specified object was neither UsageBearing nor AttributeExtensible");
        }
    }
    
    /**
     * Gets the list value of the <code>wsse:Usage</code> attribute from the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return the list of usages, or null if not present
     */
    @Nullable public static List<String> getWSSEUsages(@Nonnull final XMLObject soapObject) {
        if (soapObject instanceof UsageBearing) {
            final List<String> value = ((UsageBearing)soapObject).getWSSEUsages();
            if (value != null) {
                return value;
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            final String value = StringSupport.trimOrNull(((AttributeExtensibleXMLObject)soapObject)
                    .getUnknownAttributes().get(UsageBearing.WSSE_USAGE_ATTR_NAME));
            if (value != null) {
                StringSupport.stringToList(value, XMLConstants.LIST_DELIMITERS);
            }
        }
        return null;
    }
}
