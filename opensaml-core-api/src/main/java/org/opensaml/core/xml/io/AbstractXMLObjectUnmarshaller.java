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

package org.opensaml.core.xml.io;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.opensaml.core.config.ConfigurationProperties;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.Namespace;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.slf4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.xml.ParserPool;
import net.shibboleth.shared.xml.QNameSupport;
import net.shibboleth.shared.xml.XMLConstants;

/**
 * A thread safe abstract unmarshaller. This unmarshaller will:
 * <ul>
 * <li>Unmarshalling namespace declaration attributes</li>
 * <li>Unmarshalling schema instance type (xsi:type) declaration attributes</li>
 * <li>Delegating to child classes element, text, and attribute processing</li>
 * </ul>
 *
 * <p>
 * <strong>WARNING:</strong> As of OpenSAML v3.4 you must not surface comment or CDATA Node instances in the parsed DOM
 * which is to be unmarshalled. DOM elements containing either comment or CDATA Node children will be rejected,
 * resulting in a thrown {@link UnmarshallingException}. When using a JAXP parser, this may be accomplished by setting
 * both {@link DocumentBuilderFactory#setIgnoringComments(boolean)} and
 * {@link DocumentBuilderFactory#setCoalescing(boolean)} to <code>true</code>. Our default {@link ParserPool}
 * implementation defaults both of these appropriately and we highly recommend its use.
 * </p>
 *
 * <p>
 * <strong>WARNING:</strong> In the case of Text nodes this unmarshaller will use
 * {@link org.w3c.dom.Text#getData()} to retrieve the content. This is acceptable
 * if and only if XML parsing is done in a manner consistent with the above warning,
 * such that multiple adjacent Text nodes are not surfaced in the DOM.
 * If you need to deal with elements that contain multiple Text node children, or you intend to rely on your own XML
 * parser and/or JAXP implementation, you will need to override {@link #unmarshallTextContent(XMLObject, Text)}
 * and do "the right thing" for your implementation.
 * </p>
 * 
 * <p>
 * Failure to adhere to either of these warnings will very likely lead to security bugs and/or
 * incorrect unmarshalling behavior.
 * </p>
 */
public abstract class AbstractXMLObjectUnmarshaller implements Unmarshaller {
    
    /** Config property for controlling the use of strict mode. */
    @Nonnull @NotEmpty public static final String CONFIG_PROPERTY_XML_STRICT_MODE =
            "opensaml.config.xml.unmarshall.strictMode";

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractXMLObjectUnmarshaller.class);

    /** Factory for XMLObjectBuilders. */
    @Nonnull private final XMLObjectBuilderFactory xmlObjectBuilderFactory;

    /** Factory for creating unmarshallers for child elements. */
    @Nonnull private final UnmarshallerFactory unmarshallerFactory;
    
    /** Flag for strict mode which disallows unexpected content. */
    private final boolean strictMode;

    /**
     * Constructor.
     */
    protected AbstractXMLObjectUnmarshaller() {
        xmlObjectBuilderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
        unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();

        final ConfigurationProperties props = ConfigurationService.getConfigurationProperties(); 
        strictMode = Boolean.parseBoolean(props.getProperty(CONFIG_PROPERTY_XML_STRICT_MODE, "true"));
                
    }

    /** {@inheritDoc} */
    // Checkstyle: CyclomaticComplexity OFF
    @Override
    @Nonnull public XMLObject unmarshall(@Nonnull final Element domElement) throws UnmarshallingException {
        log.trace("Starting to unmarshall DOM element {}", QNameSupport.getNodeQName(domElement));

        final XMLObject xmlObject = buildXMLObject(domElement);

        if (log.isTraceEnabled()) {
            log.trace("Unmarshalling attributes of DOM Element {}", QNameSupport.getNodeQName(domElement));
        }
        final NamedNodeMap attributes = domElement.getAttributes();
        Node attribute;
        for (int i = 0; i < attributes.getLength(); i++) {
            attribute = attributes.item(i);

            // These should allows be attribute nodes, but just in case...
            if (attribute.getNodeType() == Node.ATTRIBUTE_NODE) {
                unmarshallAttribute(xmlObject, (Attr) attribute);
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("Unmarshalling other child nodes of DOM Element {}", QNameSupport.getNodeQName(domElement));
        }
        Node childNode = domElement.getFirstChild();
        while (childNode != null) {
            if (childNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                unmarshallAttribute(xmlObject, (Attr) childNode);
            } else if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                unmarshallChildElement(xmlObject, (Element) childNode);
            } else if (childNode.getNodeType() == Node.TEXT_NODE) {
                unmarshallTextContent(xmlObject, (Text) childNode);
            } else if (childNode.getNodeType() == Node.CDATA_SECTION_NODE) {
                throw new UnmarshallingException("Saw illegal CDATA node in parsed DOM, "
                        + "likely due to improper parser configuration");
            } else if (childNode.getNodeType() == Node.COMMENT_NODE) {
                throw new UnmarshallingException("Saw illegal Comment node in parsed DOM, "
                        + "likely due to improper parser configuration");
            }
            
            childNode = childNode.getNextSibling();
        }

        xmlObject.setDOM(domElement);
        return xmlObject;
    }
    // Checkstyle: CyclomaticComplexity ON

    /**
     * Constructs the XMLObject that the given DOM Element will be unmarshalled into. If the DOM element has an XML
     * Schema type defined this method will attempt to retrieve an XMLObjectBuilder, from the factory given at
     * construction time, using the schema type. If no schema type is present or no builder is registered with the
     * factory for the schema type, the elements QName is used. Once the builder is found the XMLObject is create by
     * invoking {@link XMLObjectBuilder#buildObject(String, String, String)}. Extending classes may wish to override
     * this logic if more than just schema type or element name (e.g. element attributes or content) need to be used to
     * determine which XMLObjectBuilder should be used to create the XMLObject.
     * 
     * @param domElement the DOM Element the created XMLObject will represent
     * 
     * @return the empty XMLObject that DOM Element can be unmarshalled into
     * 
     * @throws UnmarshallingException thrown if there is now XMLObjectBuilder registered for the given DOM Element
     */
    @Nonnull protected XMLObject buildXMLObject(@Nonnull final Element domElement) throws UnmarshallingException {
        if (log.isTraceEnabled()) {
            log.trace("Building XMLObject for {}", QNameSupport.getNodeQName(domElement));
        }

        XMLObjectBuilder<?> xmlObjectBuilder = xmlObjectBuilderFactory.getBuilder(domElement);
        if (xmlObjectBuilder == null) {
            xmlObjectBuilder = xmlObjectBuilderFactory.getBuilder(
                    XMLObjectProviderRegistrySupport.getDefaultProviderQName());
            if (xmlObjectBuilder == null) {
                final String errorMsg = "Unable to locate builder for " + QNameSupport.getNodeQName(domElement);
                log.error(errorMsg);
                throw new UnmarshallingException(errorMsg);
            }
            if (log.isTraceEnabled()) {
                log.trace("No builder was registered for {} but the default builder {} was available, using it.",
                        QNameSupport.getNodeQName(domElement), xmlObjectBuilder.getClass().getName());
            }
        }

        return xmlObjectBuilder.buildObject(domElement);
    }

    /**
     * Unmarshalls the attributes from the given DOM Attr into the given XMLObject. If the attribute is an XML namespace
     * declaration the attribute is passed to
     * {@link AbstractXMLObjectUnmarshaller#unmarshallNamespaceAttribute(XMLObject, Attr)}. If it is an schema type
     * declaration (xsi:type) it is ignored because this attribute is handled by {@link #buildXMLObject(Element)}. All
     * other attributes are passed to the {@link #processAttribute(XMLObject, Attr)}
     * 
     * @param attribute the attribute to be unmarshalled
     * @param xmlObject the XMLObject that will recieve information from the DOM attribute
     * 
     * @throws UnmarshallingException thrown if there is a problem unmarshalling an attribute
     */
    protected void unmarshallAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final QName attribName = QNameSupport.getNodeQName(attribute);
        log.trace("Pre-processing attribute {}", attribName);
        final String attributeNamespace = StringSupport.trimOrNull(attribute.getNamespaceURI());

        if (Objects.equals(attributeNamespace, XMLConstants.XMLNS_NS)) {
            unmarshallNamespaceAttribute(xmlObject, attribute);
        } else if (Objects.equals(attributeNamespace, XMLConstants.XSI_NS)) {
            unmarshallSchemaInstanceAttributes(xmlObject, attribute);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Attribute {} is neither a schema type nor namespace, calling processAttribute()",
                        QNameSupport.getNodeQName(attribute));
            }
            final String attributeNSURI = attribute.getNamespaceURI();
            String attributeNSPrefix;
            if (attributeNSURI != null) {
                attributeNSPrefix = attribute.lookupPrefix(attributeNSURI);
                if (attributeNSPrefix == null && XMLConstants.XML_NS.equals(attributeNSURI)) {
                    attributeNSPrefix = XMLConstants.XML_PREFIX;
                }
                xmlObject.getNamespaceManager().registerAttributeName(attribName);
            }

            checkIDAttribute(attribute);

            processAttribute(xmlObject, attribute);
        }
    }

    /**
     * Unmarshalls a namespace declaration attribute.
     * 
     * @param xmlObject the xmlObject to receive the namespace declaration
     * @param attribute the namespace declaration attribute
     */
    protected void unmarshallNamespaceAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute) {
        if (log.isTraceEnabled()) {
            log.trace("{} is a namespace declaration, adding it to the list of namespaces on the XMLObject",
                    QNameSupport.getNodeQName(attribute));
        }
        final Namespace namespace;
        if (Objects.equals(attribute.getLocalName(), XMLConstants.XMLNS_PREFIX)) {
            namespace = new Namespace(attribute.getValue(), null);
        } else {
            namespace = new Namespace(attribute.getValue(), attribute.getLocalName());
        }
        xmlObject.getNamespaceManager().registerNamespaceDeclaration(namespace);
    }

    /**
     * Unmarshalls the XSI type, schemaLocation, and noNamespaceSchemaLocation attributes.
     * 
     * @param xmlObject the xmlObject to recieve the namespace declaration
     * @param attribute the namespace declaration attribute
     */
    protected void unmarshallSchemaInstanceAttributes(@Nonnull final XMLObject xmlObject,
            @Nonnull final Attr attribute) {
        final QName attribName = QNameSupport.getNodeQName(attribute);
        if (XMLConstants.XSI_TYPE_ATTRIB_NAME.equals(attribName)) {
            if (log.isTraceEnabled()) {
                log.trace("Saw XMLObject {} with an xsi:type of: {}", xmlObject.getElementQName(),
                        attribute.getValue());
            }
        } else if (XMLConstants.XSI_SCHEMA_LOCATION_ATTRIB_NAME.equals(attribName)) {
            if (log.isTraceEnabled()) {
                log.trace("Saw XMLObject {} with an xsi:schemaLocation of: {}", xmlObject.getElementQName(),
                        attribute.getValue());
            }
            xmlObject.setSchemaLocation(attribute.getValue());
        } else if (XMLConstants.XSI_NO_NAMESPACE_SCHEMA_LOCATION_ATTRIB_NAME.equals(attribName)) {
            if (log.isTraceEnabled()) {
                log.trace("Saw XMLObject {} with an xsi:noNamespaceSchemaLocation of: {}", xmlObject.getElementQName(),
                        attribute.getValue());
            }
            xmlObject.setNoNamespaceSchemaLocation(attribute.getValue());
        } else if (XMLConstants.XSI_NIL_ATTRIB_NAME.equals(attribName)) {
            if (log.isTraceEnabled()) {
                log.trace("Saw XMLObject {} with an xsi:nil of: {}", xmlObject.getElementQName(), attribute.getValue());
            }
            xmlObject.setNil(XSBooleanValue.valueOf(attribute.getValue()));
        }
    }

    /**
     * Check whether the attribute's QName is registered in the global ID attribute registry. If it is, and the
     * specified attribute's DOM Level 3 Attr.isId() is false (due to lack of schema validation, for example), then
     * declare the attribute as an ID type in the DOM on the attribute's owning element. This is to handle cases where
     * the underlying DOM needs to accurately reflect an attribute's ID-ness, for example ID reference resolution within
     * the Apache XML Security library.
     * 
     * @param attribute the DOM attribute to be checked
     */
    protected void checkIDAttribute(@Nonnull final Attr attribute) {
        final QName attribName = QNameSupport.getNodeQName(attribute);
        if (XMLObjectProviderRegistrySupport.isIDAttribute(attribName) && !attribute.isId()) {
            attribute.getOwnerElement().setIdAttributeNode(attribute, true);
        }
    }

    /**
     * Unmarshalls given Element's children. For each child an unmarshaller is retrieved using
     * {@link UnmarshallerFactory#getUnmarshaller(Element)}. The unmarshaller is then used to unmarshall the child
     * element and the resultant XMLObject is passed to {@link #processChildElement(XMLObject, XMLObject)} for further
     * processing.
     * 
     * @param xmlObject the parent object of the unmarshalled children
     * @param childElement the child element to be unmarshalled
     * 
     * @throws UnmarshallingException thrown if an error occurs unmarshalling the chilren elements
     */
    protected void unmarshallChildElement(@Nonnull final XMLObject xmlObject, @Nonnull final Element childElement)
            throws UnmarshallingException {
        if (log.isTraceEnabled()) {
            log.trace("Unmarshalling child elements of XMLObject {}", xmlObject.getElementQName());
        }

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(childElement);

        if (unmarshaller == null) {
            unmarshaller = unmarshallerFactory.getUnmarshaller(
                    XMLObjectProviderRegistrySupport.getDefaultProviderQName());
            if (unmarshaller == null) {
                final String errorMsg =
                        "No unmarshaller available for " + QNameSupport.getNodeQName(childElement) + ", child of "
                                + xmlObject.getElementQName();
                log.error(errorMsg);
                throw new UnmarshallingException(errorMsg);
            }
            if (log.isTraceEnabled()) {
                log.trace("No unmarshaller was registered for {}, child of {}. Using default unmarshaller.",
                        QNameSupport.getNodeQName(childElement), xmlObject.getElementQName());
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("Unmarshalling child element {} with unmarshaller {}", QNameSupport.getNodeQName(childElement),
                    unmarshaller.getClass().getName());
        }
        processChildElement(xmlObject, unmarshaller.unmarshall(childElement));
    }

    /**
     * Unmarshalls the given Text node into a usable string by way of {@link Text#getData()} and passes it off to
     * {@link AbstractXMLObjectUnmarshaller#processElementContent(XMLObject, String)} if the string is not null and
     * contains something other than whitespace.
     * 
     * @param xmlObject the XMLObject receiving the element content
     * @param content the textual content
     * 
     * @throws UnmarshallingException thrown if there is a problem unmarshalling the text node
     */
    protected void unmarshallTextContent(@Nonnull final XMLObject xmlObject, @Nonnull final Text content)
            throws UnmarshallingException {
        final String textContent = StringSupport.trimOrNull(content.getData());
        if (textContent != null) {
            processElementContent(xmlObject, textContent);
        }
    }

    /**
     * Called after a child element has been unmarshalled so that it can be added to the parent XMLObject.
     * 
     * The default implementation of this method is a no-op.
     * 
     * @param parentXMLObject the parent XMLObject
     * @param childXMLObject the child XMLObject
     * 
     * @throws UnmarshallingException thrown if there is a problem adding the child to the parent
     */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {

        if (strictMode) {
            throw new UnmarshallingException(String.format("Saw invalid child element %s on parent %s",
                    childXMLObject.getElementQName(), parentXMLObject.getElementQName()));
        }

        log.debug("Ignoring unknown child element {} of parent {}", childXMLObject.getElementQName(),
                parentXMLObject.getElementQName());
    }

    /**
     * Called after an attribute has been unmarshalled so that it can be added to the XMLObject.
     * 
     * The default implementation of this method is a no-op
     * 
     * @param xmlObject the XMLObject
     * @param attribute the attribute
     * 
     * @throws UnmarshallingException thrown if there is a problem adding the attribute to the XMLObject
     */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {

        if (strictMode) {
            throw new UnmarshallingException(String.format("Saw invalid attribute '%s' on element %s",
                    QNameSupport.getNodeQName(attribute), xmlObject.getElementQName()));
        }

        log.debug("Ignoring unknown attribute '{}' on element {}", QNameSupport.getNodeQName(attribute),
                xmlObject.getElementQName());
    }

    /**
     * Called if the element being unmarshalled contained textual content so that it can be added to the XMLObject.
     * 
     * The default implementation of this method is a no-op
     * 
     * @param xmlObject XMLObject the content will be given to
     * @param elementContent the Element's content
     * 
     * @throws UnmarshallingException if there is a problem adding the element content to the XMLObject
     */
    protected void processElementContent(@Nonnull final XMLObject xmlObject, @Nonnull final String elementContent)
            throws UnmarshallingException {

        if (strictMode) {
            throw new UnmarshallingException(String.format("Saw invalid element content '%s' of element %s",
                    elementContent, xmlObject.getElementQName()));
        }

        log.debug("Ignoring unknown element content '{}' of element {}", elementContent, xmlObject.getElementQName());
    }
    
    /**
     * Called to store wildcard attributes, if the object supports that.  It is expected that the
     * object's unmarshaller will have checked and dealt with known attributes before calling this. 
     * @param xmlObject The object which support anyAttribute.
     * @param attribute The attribute in question.
     */
    protected void processUnknownAttribute(@Nonnull final AttributeExtensibleXMLObject xmlObject,
            @Nonnull final Attr attribute) {
        // TODO Add support for validating whether attribute's namespace is consistent with the
        // anyAttribute/@namespace.  Either via this method directly (new arguments) or in the 
        // below support method.
        XMLObjectSupport.unmarshallToAttributeMap(xmlObject.getUnknownAttributes(), attribute);
    }
}