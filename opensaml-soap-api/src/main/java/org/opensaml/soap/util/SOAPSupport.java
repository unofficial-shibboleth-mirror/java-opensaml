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

package org.opensaml.soap.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.shared.collection.LazyList;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.xml.XMLConstants;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.Detail;
import org.opensaml.soap.soap11.EncodingStyleBearing;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultActor;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.soap11.FaultString;
import org.opensaml.soap.soap11.MustUnderstandBearing;

/**
 * Helper methods for working with SOAP.
 */
public final class SOAPSupport {

    /**
     * Private constructor.
     */
    private SOAPSupport() {
    }

    /**
     * Adds a <code>soap11:mustUnderstand</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param mustUnderstand whether mustUnderstand is true or false
     */
    public static void addSOAP11MustUnderstandAttribute(@Nonnull final XMLObject soapObject,
            final boolean mustUnderstand) {
        if (soapObject instanceof MustUnderstandBearing) {
            ((MustUnderstandBearing) soapObject).setSOAP11MustUnderstand(new XSBooleanValue(mustUnderstand, true));
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_NAME,
                    new XSBooleanValue(mustUnderstand, true).toString());
        } else {
            throw new IllegalArgumentException(
                    "Specified object was neither MustUnderstandBearing nor AttributeExtensible");
        }
    }

    /**
     * Get the <code>soap11:mustUnderstand</code> attribute from a given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return value of the mustUnderstand attribute, or false if not present
     */
    public static boolean getSOAP11MustUnderstandAttribute(@Nonnull final XMLObject soapObject) {
        if (soapObject instanceof MustUnderstandBearing) {
            final XSBooleanValue value = ((MustUnderstandBearing) soapObject).isSOAP11MustUnderstandXSBoolean();
            if (value != null) {
                final Boolean flag = value.getValue();
                if (flag != null) {
                    return flag;
                }
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            final String value = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_NAME));
            return Objects.equals("1", value) || Objects.equals("true", value);
        }
        return false;
    }

    /**
     * Adds a <code>soap11:actor</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param actorURI the URI of the actor
     */
    public static void addSOAP11ActorAttribute(@Nonnull final XMLObject soapObject, @Nonnull final String actorURI) {
        final String value =
                Constraint.isNotNull(StringSupport.trimOrNull(actorURI), "Actor URI cannot be null or empty");
        if (soapObject instanceof ActorBearing) {
            ((ActorBearing) soapObject).setSOAP11Actor(value);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(ActorBearing.SOAP11_ACTOR_ATTR_NAME,
                    value);
        } else {
            throw new IllegalArgumentException("Specified object was neither ActorBearing nor AttributeExtensible");
        }
    }

    /**
     * Gets the <code>soap11:actor</code> attribute from a given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return the value of the actor attribute, or null if not present
     */
    @Nullable public static String getSOAP11ActorAttribute(@Nonnull final XMLObject soapObject) {
        String value = null;
        if (soapObject instanceof ActorBearing) {
            value = StringSupport.trimOrNull(((ActorBearing) soapObject).getSOAP11Actor());
            if (value != null) {
                return value;
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            value = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(ActorBearing.SOAP11_ACTOR_ATTR_NAME));
            return value;
        }
        return null;
    }

    /**
     * Adds a single encoding style to the given SOAP object. If an existing <code>soap11:encodingStyle</code> attribute
     * is present, the given style will be added to the existing list.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param encodingStyle the encoding style to add
     */
    public static void addSOAP11EncodingStyle(@Nonnull final XMLObject soapObject,
            @Nonnull final String encodingStyle) {
        final String value = Constraint.isNotNull(StringSupport.trimOrNull(encodingStyle),
                "Encoding style to add cannot be null or empty");
        if (soapObject instanceof EncodingStyleBearing) {
            final EncodingStyleBearing esb = (EncodingStyleBearing) soapObject;
            List<String> list = esb.getSOAP11EncodingStyles();
            if (list == null) {
                list = new LazyList<>();
                esb.setSOAP11EncodingStyles(list);
            }
            list.add(value);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            final AttributeMap am = ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes();
            String list = am.get(EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME);
            if (list == null) {
                list = value;
            } else {
                list = list + " " + value;
            }
            am.put(EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME, list);
        } else {
            throw new IllegalArgumentException(
                    "Specified object was neither EncodingStyleBearing nor AttributeExtensible");
        }
    }

    /**
     * Adds a <code>soap11:encodingStyle</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param encodingStyles the list of encoding styles to add
     */
    public static void addSOAP11EncodingStyles(@Nonnull final XMLObject soapObject,
            @Nonnull final List<String> encodingStyles) {
        Constraint.isNotEmpty(encodingStyles, "Encoding styles list cannot be empty");
        
        if (soapObject instanceof EncodingStyleBearing) {
            ((EncodingStyleBearing) soapObject).setSOAP11EncodingStyles(encodingStyles);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME,
                    StringSupport.listToStringValue(encodingStyles, " "));
        } else {
            throw new IllegalArgumentException(
                    "Specified object was neither EncodingStyleBearing nor AttributeExtensible");
        }
    }

    /**
     * Gets the list value of the <code>soap11:encodingStyle</code> attribute from the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return the list of encoding styles, or null if not present
     */
    @Nullable public static List<String> getSOAP11EncodingStyles(@Nonnull final XMLObject soapObject) {
        if (soapObject instanceof EncodingStyleBearing) {
            final List<String> value = ((EncodingStyleBearing) soapObject).getSOAP11EncodingStyles();
            if (value != null) {
                return value;
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            final String value = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(EncodingStyleBearing.SOAP11_ENCODING_STYLE_ATTR_NAME));
            if (value != null) {
                StringSupport.stringToList(value, XMLConstants.LIST_DELIMITERS);
            }
        }
        return null;
    }

    /**
     * Adds the <code>soap12:encodingStyle</code> attribute to the given soap object.
     * 
     * @param soapObject object to which the encoding style attribute should be added
     * @param style the encoding style
     */
    public static void addSOAP12EncodingStyleAttribute(@Nonnull final XMLObject soapObject,
            @Nonnull final String style) {
        final String value = Constraint.isNotNull(StringSupport.trimOrNull(style),
                "Encoding style to add cannot be null or empty");
        
        if (soapObject instanceof org.opensaml.soap.soap12.EncodingStyleBearing) {
            ((org.opensaml.soap.soap12.EncodingStyleBearing) soapObject).setSOAP12EncodingStyle(value);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    org.opensaml.soap.soap12.EncodingStyleBearing.SOAP12_ENCODING_STYLE_ATTR_NAME, value);
        } else {
            throw new IllegalArgumentException(
                    "Specified object was neither EncodingStyleBearing nor AttribtueExtensible");
        }
    }

    /**
     * Gets the <code>soap12:encodingStyle</code>.
     * 
     * @param soapObject the SOAP object which may contain the encoding style
     * 
     * @return the encoding style or null if it is not set on the object
     */
    @Nullable public static String getSOAP12EncodingStyleAttribute(@Nonnull final XMLObject soapObject) {
        String style = null;
        if (soapObject instanceof org.opensaml.soap.soap12.EncodingStyleBearing) {
            style = ((org.opensaml.soap.soap12.EncodingStyleBearing) soapObject).getSOAP12EncodingStyle();
        }

        if (style == null && soapObject instanceof AttributeExtensibleXMLObject) {
            style = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(
                            org.opensaml.soap.soap12.EncodingStyleBearing.SOAP12_ENCODING_STYLE_ATTR_NAME));
        }

        return style;
    }

    /**
     * Adds a <code>soap12:mustUnderstand</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param mustUnderstand whether mustUnderstand is true or false
     */
    public static void addSOAP12MustUnderstandAttribute(@Nonnull final XMLObject soapObject,
            final boolean mustUnderstand) {
        if (soapObject instanceof org.opensaml.soap.soap12.MustUnderstandBearing) {
            ((org.opensaml.soap.soap12.MustUnderstandBearing) soapObject)
                    .setSOAP12MustUnderstand(new XSBooleanValue(mustUnderstand, false));
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    org.opensaml.soap.soap12.MustUnderstandBearing.SOAP12_MUST_UNDERSTAND_ATTR_NAME,
                    new XSBooleanValue(mustUnderstand, false).toString());
        } else {
            throw new IllegalArgumentException(
                    "Specified object was neither MustUnderstandBearing nor AttributeExtensible");
        }
    }

    /**
     * Get the <code>soap12:mustUnderstand</code> attribute from a given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return value of the mustUnderstand attribute, or false if not present
     */
    public static boolean getSOAP12MustUnderstandAttribute(@Nonnull final XMLObject soapObject) {
        if (soapObject instanceof org.opensaml.soap.soap12.MustUnderstandBearing) {
            final XSBooleanValue value = ((org.opensaml.soap.soap12.MustUnderstandBearing) soapObject)
                    .isSOAP12MustUnderstandXSBoolean();
            if (value != null) {
                final Boolean flag = value.getValue();
                if (flag != null) {
                    return flag;
                }
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            final String value = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(
                            org.opensaml.soap.soap12.MustUnderstandBearing.SOAP12_MUST_UNDERSTAND_ATTR_NAME));
            return Objects.equals("1", value) || Objects.equals("true", value);
        }
        return false;
    }

    /**
     * Adds a <code>soap12:relay</code> attribute to the given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * @param relay whether relay is true or false
     */
    public static void addSOAP12RelayAttribute(@Nonnull final XMLObject soapObject, final boolean relay) {
        if (soapObject instanceof org.opensaml.soap.soap12.RelayBearing) {
            ((org.opensaml.soap.soap12.RelayBearing) soapObject).setSOAP12Relay(new XSBooleanValue(relay, false));
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    org.opensaml.soap.soap12.RelayBearing.SOAP12_RELAY_ATTR_NAME,
                    new XSBooleanValue(relay, false).toString());
        } else {
            throw new IllegalArgumentException("Specified object was neither RelayBearing nor AttributeExtensible");
        }
    }

    /**
     * Get the <code>soap12:relay</code> attribute from a given SOAP object.
     * 
     * @param soapObject the SOAP object to add the attribute to
     * 
     * @return value of the relay attribute, or false if not present
     */
    public static boolean getSOAP12RelayAttribute(@Nonnull final XMLObject soapObject) {
        if (soapObject instanceof org.opensaml.soap.soap12.RelayBearing) {
            final XSBooleanValue value = ((org.opensaml.soap.soap12.RelayBearing) soapObject).isSOAP12RelayXSBoolean();
            if (value != null) {
                final Boolean flag = value.getValue();
                if (flag != null) {
                    return flag;
                }
            }
        }
        if (soapObject instanceof AttributeExtensibleXMLObject) {
            final String value = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(org.opensaml.soap.soap12.RelayBearing.SOAP12_RELAY_ATTR_NAME));
            return Objects.equals("1", value) || Objects.equals("true", value);
        }
        return false;
    }
    
    /**
     * Adds the <code>soap12:role</code> attribute to the given soap object.
     * 
     * @param soapObject object to which the rol attribute should be added
     * @param role the role
     */
    public static void addSOAP12RoleAttribute(@Nonnull final XMLObject soapObject, @Nonnull final String role) {
        final String value = Constraint.isNotNull(StringSupport.trimOrNull(role), "Role cannot be null or empty");
        
        if (soapObject instanceof org.opensaml.soap.soap12.RoleBearing) {
            ((org.opensaml.soap.soap12.RoleBearing) soapObject).setSOAP12Role(value);
        } else if (soapObject instanceof AttributeExtensibleXMLObject) {
            ((AttributeExtensibleXMLObject) soapObject).getUnknownAttributes().put(
                    org.opensaml.soap.soap12.RoleBearing.SOAP12_ROLE_ATTR_NAME, value);
        } else {
            throw new IllegalArgumentException(
                    "Specified object was neither RoleBearing nor AttribtueExtensible");
        }
    }

    /**
     * Gets the <code>soap12:role</code>.
     * 
     * @param soapObject the SOAP object which may contain the role
     * 
     * @return the role or null if it is not set on the object
     */
    @Nullable public static String getSOAP12RoleAttribute(@Nonnull final XMLObject soapObject) {
        String role = null;
        if (soapObject instanceof org.opensaml.soap.soap12.RoleBearing) {
            role = ((org.opensaml.soap.soap12.RoleBearing) soapObject).getSOAP12Role();
        }

        if (role == null && soapObject instanceof AttributeExtensibleXMLObject) {
            role = StringSupport.trimOrNull(((AttributeExtensibleXMLObject) soapObject)
                    .getUnknownAttributes().get(org.opensaml.soap.soap12.RoleBearing.SOAP12_ROLE_ATTR_NAME));
        }

        return role;
    }

// Checkstyle: CyclomaticComplexity OFF
    /**
     * Build a SOAP 1.1. Fault element.
     * 
     * @param faultCode the 'faultcode' QName (required)
     * @param faultString the 'faultstring' value (required)
     * @param faultActor the 'faultactor' value (may be null)
     * @param detailChildren the 'detail' child elements
     * @param detailAttributes the 'detail' element attributes
     * @return the new Fault element object
     */
    public static Fault buildSOAP11Fault(@Nonnull final QName faultCode, @Nonnull final String faultString,
            @Nullable final String faultActor, @Nullable final List<XMLObject> detailChildren,
            @Nullable final Map<QName, String> detailAttributes) {
        Constraint.isNotNull(faultCode, "faultcode cannot be null");
        Constraint.isNotNull(faultString, "faultstring cannot be null");
        
        final XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory(); 
        
        final Fault faultObj =  (Fault) builderFactory.ensureBuilder(Fault.DEFAULT_ELEMENT_NAME)
            .buildObject(Fault.DEFAULT_ELEMENT_NAME);
        final FaultCode faultCodeObj =  (FaultCode) builderFactory.ensureBuilder(FaultCode.DEFAULT_ELEMENT_NAME)
            .buildObject(FaultCode.DEFAULT_ELEMENT_NAME);
        final FaultString faultStringObj =  (FaultString) builderFactory.ensureBuilder(FaultString.DEFAULT_ELEMENT_NAME)
            .buildObject(FaultString.DEFAULT_ELEMENT_NAME);
        
        faultCodeObj.setValue(faultCode);
        faultObj.setCode(faultCodeObj);

        faultStringObj.setValue(faultString);
        faultObj.setMessage(faultStringObj);
        
        if (faultActor != null) {
            final FaultActor faultActorObj =  (FaultActor) builderFactory.ensureBuilder(FaultActor.DEFAULT_ELEMENT_NAME)
                .buildObject(FaultActor.DEFAULT_ELEMENT_NAME);
            faultActorObj.setURI(faultActor);
            faultObj.setActor(faultActorObj);
        }
            
        Detail detailObj = null;
        if (detailChildren != null && !detailChildren.isEmpty()) {
            detailObj = (Detail) builderFactory.ensureBuilder(Detail.DEFAULT_ELEMENT_NAME)
                .buildObject(Detail.DEFAULT_ELEMENT_NAME);
            detailObj.getUnknownXMLObjects().addAll(detailChildren);
        }
        
        if (detailAttributes != null && !detailAttributes.isEmpty()) {
            if (detailObj == null) {
                detailObj = (Detail) builderFactory.ensureBuilder(Detail.DEFAULT_ELEMENT_NAME)
                    .buildObject(Detail.DEFAULT_ELEMENT_NAME);
            }
            for (final Entry<QName,String> entry : detailAttributes.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    detailObj.getUnknownAttributes().put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (detailObj != null && 
                (!detailObj.getUnknownXMLObjects().isEmpty() || !detailObj.getUnknownAttributes().isEmpty())) {
            faultObj.setDetail(detailObj);
        }
        
        return faultObj;
    }
// Checkstyle: CyclomaticComplexity ON
    
}