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

package org.opensaml.soap.messaging;

import static org.opensaml.soap.util.SOAPVersion.SOAP_1_1;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.LazyList;
import net.shibboleth.shared.logic.Constraint;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.soap.messaging.context.InboundSOAPContext;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.Header;
import org.opensaml.soap.util.SOAPSupport;
import org.opensaml.soap.util.SOAPVersion;

/**
 * Support class for SOAP messaging.
 */
public final class SOAPMessagingSupport {
    
    /** Constructor. */
    private SOAPMessagingSupport() {}
    
    /**
     * Get the current {@link InboundSOAPContext} for the given {@link MessageContext}. 
     * 
     * @param messageContext the current message context
     * 
     * @return the current inbound SOAP context. May be null if autoCreate=false, otherwise will be non-null
     */
    @Nonnull public static InboundSOAPContext getInboundSOAPContext(@Nonnull final MessageContext messageContext) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        return messageContext.ensureSubcontext(InboundSOAPContext.class);
    }

    /**
     * Get the current {@link SOAP11Context} for the given {@link MessageContext}. 
     * 
     * @param messageContext the current message context
     * @param autoCreate whether to auto-create the context if it does not exist
     * 
     * @return the current SOAP 1.1 context. May be null if autoCreate=false, otherwise will be non-null
     * 
     * @deprecated
     */
    @Deprecated(since="5.0.0", forRemoval=true)
    @Nullable public static SOAP11Context getSOAP11Context(@Nonnull final MessageContext messageContext,
            final boolean autoCreate) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        return messageContext.getSubcontext(SOAP11Context.class, autoCreate);
    }
    
    /**
     * Get the current {@link SOAP11Context} for the given {@link MessageContext}. 
     * 
     * @param messageContext the current message context
     * 
     * @return the current SOAP 1.1 context or null
     */
    @Nullable public static SOAP11Context getSOAP11Context(@Nonnull final MessageContext messageContext) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        return messageContext.getSubcontext(SOAP11Context.class);
    }

    /**
     * Get the current {@link SOAP11Context} for the given {@link MessageContext}, or create one if
     * necessary. 
     * 
     * @param messageContext the current message context
     * 
     * @return the current SOAP 1.1 context.
     * 
     * @since 5.0.0
     */
    @Nonnull public static SOAP11Context ensureSOAP11Context(@Nonnull final MessageContext messageContext) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        return messageContext.ensureSubcontext(SOAP11Context.class);
    }

    /**
     * Register a header as understood.
     * 
     * @param msgContext the current message context
     * @param header the header that was understood
     */
    public static void registerUnderstoodHeader(@Nonnull final MessageContext msgContext, 
            @Nonnull final XMLObject header) {
        final InboundSOAPContext inboundContext = getInboundSOAPContext(msgContext);
        
        inboundContext.getUnderstoodHeaders().add(header);
    }
    
    /**
     * Check whether a header was understood.
     * 
     * @param msgContext the current message context
     * @param header the header that is to be checked for understanding
     * @return true if header was understood, false otherwise
     */
    public static boolean checkUnderstoodHeader(@Nonnull final MessageContext msgContext,
            @Nonnull final XMLObject header) {
        final InboundSOAPContext inboundContext = getInboundSOAPContext(msgContext);
        
        return inboundContext.getUnderstoodHeaders().contains(header);
    }
    
    /**
     * Determine whether the message represented by the message context 
     * contains a SOAP Envelope.
     * 
     * @param messageContext the current message context
     * @return true iff the message context contains a SOAP Envelope
     */
    public static boolean isSOAPMessage(@Nonnull final MessageContext messageContext) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        
        final SOAPVersion version = getSOAPVersion(messageContext);
        return version != null;
    }
    
    /**
     * Determine whether the message represented by the message context 
     * contains a SOAP 1.1. Envelope.
     * 
     * @param messageContext the current message context
     * @return true iff the message context contains a SOAP 1.1 Envelope
     */
    public static boolean isSOAP11Message(@Nonnull final MessageContext messageContext) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        
        final SOAPVersion version = getSOAPVersion(messageContext);
        if (version != null && SOAP_1_1.equals(version)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Determine the SOAP version of the message represented by the message context.
     * 
     * @param messageContext the current message context
     * @return the SOAP version.  May be null if the version could not be determined.
     */
    @Nullable public static SOAPVersion getSOAPVersion(
            @Nonnull final MessageContext messageContext) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        
        final SOAP11Context soap11 = getSOAP11Context(messageContext, false);
        
        // SOAP 1.1 Envelope
        if (soap11 != null && soap11.getEnvelope() != null) {
            return SOAP_1_1;
        }
        
        //TODO SOAP 1.2 support when object providers are implemented
        
        return null;
    }
    
    /**
     * Check whether the specified header block is indicated as mustUnderstand == true.
     * 
     * @param messageContext the message context being processed
     * @param headerBlock the header block to check
     * @return true if header is indicated as mustUnderstand==true, false if not
     */
    public static boolean isMustUnderstand(@Nonnull final MessageContext messageContext,
            @Nonnull final XMLObject headerBlock) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        Constraint.isNotNull(headerBlock, "Header block context cannot be null");
        
        final SOAPVersion soapVersion = SOAPMessagingSupport.getSOAPVersion(messageContext);
        if (soapVersion == null) {
            throw new IllegalArgumentException("Could not determine SOAP version for message context");
        }
        
        switch(soapVersion) {
            case SOAP_1_1:
                return SOAPSupport.getSOAP11MustUnderstandAttribute(headerBlock);
            case SOAP_1_2:
                return SOAPSupport.getSOAP12MustUnderstandAttribute(headerBlock);
            default:
                throw new IllegalArgumentException("Saw unsupported SOAP version: " + soapVersion);
        }
    }
    
    /**
     * Add whether the specified header block is indicated as mustUnderstand.
     * 
     * @param messageContext the message context being processed
     * @param headerBlock the header block to check
     * @param mustUnderstand true if header must be understood, false if not
     */
    public static void addMustUnderstand(@Nonnull final MessageContext messageContext,
            @Nonnull final XMLObject headerBlock, final boolean mustUnderstand) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        Constraint.isNotNull(headerBlock, "Header block context cannot be null");
        
        final SOAPVersion soapVersion = SOAPMessagingSupport.getSOAPVersion(messageContext);
        if (soapVersion == null) {
            throw new IllegalArgumentException("Could not determine SOAP version for message context");
        }
        
        switch(soapVersion) {
            case SOAP_1_1:
                SOAPSupport.addSOAP11MustUnderstandAttribute(headerBlock, mustUnderstand);
                break;
            case SOAP_1_2:
                SOAPSupport.addSOAP12MustUnderstandAttribute(headerBlock, mustUnderstand);
                break;
            default:
                throw new IllegalArgumentException("Saw unsupported SOAP version: " + soapVersion);
        }
    }
    
    /**
     * Add the target node info to the header block, either <code>soap11:actor</code>,
     * or <code>soap12:role</code>.
     * 
     * @param messageContext the message context being processed
     * @param headerBlock the header block to check
     * @param targetNode the node to add
     */
    public static void addTargetNode(@Nonnull final MessageContext messageContext,
            @Nonnull final XMLObject headerBlock, @Nullable final String targetNode) {
        if (targetNode == null) {
            return;
        }
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        Constraint.isNotNull(headerBlock, "Header block context cannot be null");
        
        final SOAPVersion soapVersion = SOAPMessagingSupport.getSOAPVersion(messageContext);
        if (soapVersion == null) {
            throw new IllegalArgumentException("Could not determine SOAP version for message context");
        }
        
        switch(soapVersion) {
            case SOAP_1_1:
                SOAPSupport.addSOAP11ActorAttribute(headerBlock, targetNode);
                break;
            case SOAP_1_2:
                SOAPSupport.addSOAP12RoleAttribute(headerBlock, targetNode);
                break;
            default:
                throw new IllegalArgumentException("Saw unsupported SOAP version: " + soapVersion);
        }
    }
    
    /**
     * Get a header block from the SOAP envelope contained within the specified message context's
     * SOAP subcontext.
     * 
     * @param messageContext the message context being processed
     * @param headerName the name of the header block to return 
     * 
     * @return the list of matching header blocks
     */
    @Nonnull public static List<XMLObject> getInboundHeaderBlock(
            @Nonnull final MessageContext messageContext, @Nonnull final QName headerName) {
            
        final InboundSOAPContext inboundContext = getInboundSOAPContext(messageContext);
        
        return getHeaderBlock(messageContext, headerName, 
                inboundContext.getNodeActors(), inboundContext.isFinalDestination());
    }
    
    /**
     * Get a header block from the SOAP envelope contained within the specified message context's
     * SOAP subcontext.
     * 
     * @param messageContext the message context being processed
     * @param headerName the name of the header block to return 
     * 
     * @return the list of matching header blocks
     */
    @Nonnull public static List<XMLObject> getOutboundHeaderBlock(
            @Nonnull final MessageContext messageContext, @Nonnull final QName headerName) {
            
        return getHeaderBlock(messageContext, headerName, null, true);
    }
    
    /**
     * Get a header block from the SOAP envelope contained within the specified message context's
     * SOAP subcontext.
     * 
     * @param messageContext the message context being processed
     * @param headerName the name of the header block to return 
     * @param targetNodes the explicitly specified SOAP node actors (1.1) or roles (1.2) for which the header is desired
     * @param isFinalDestination true specifies that headers targeted for message final destination should be returned,
     *          false means they should not be returned
     *          
     * @return the list of matching header blocks
     */
    @Nonnull public static List<XMLObject> getHeaderBlock(
            @Nonnull final MessageContext messageContext, @Nonnull final QName headerName,
            @Nullable final Set<String> targetNodes, final boolean isFinalDestination) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        
        final SOAP11Context soap11 = getSOAP11Context(messageContext);
        
        // SOAP 1.1 Envelope
        if (soap11 != null) {
            final Envelope env = soap11.getEnvelope();
            if (env != null) {
                return getSOAP11HeaderBlock(env, headerName, targetNodes, isFinalDestination);
            }
        }
        
        //TODO SOAP 1.2 support when object providers are implemented
        return CollectionSupport.emptyList();
    }
    
    /**
     * Get a header block from the SOAP 1.1 envelope.
     * 
     * @param envelope the SOAP 1.1 envelope to process 
     * @param headerName the name of the header block to return 
     * @param targetNodes the explicitly specified SOAP node actors for which the header is desired
     * @param isFinalDestination true specifies that headers targeted for message final destination should be returned,
     *          false specifies they should not be returned
     * @return the list of matching header blocks
     */
    @Nonnull public static List<XMLObject> getSOAP11HeaderBlock(@Nonnull final Envelope envelope,
            @Nonnull final QName headerName, @Nullable final Set<String> targetNodes,
            final boolean isFinalDestination) {
        Constraint.isNotNull(envelope, "Envelope cannot be null");
        Constraint.isNotNull(headerName, "Header name cannot be null");
        
        final Header envelopeHeader = envelope.getHeader();
        if (envelopeHeader == null) {
            return CollectionSupport.emptyList();
        }
        
        final LazyList<XMLObject> headers = new LazyList<>();
        for (final XMLObject header : envelopeHeader.getUnknownXMLObjects(headerName)) {
            assert header != null;
            if (isSOAP11HeaderTargetedToNode(header, targetNodes, isFinalDestination)) {
                headers.add(header);
            }
        }
        
        return headers;
    }
    
    /**
     * Evaluate whether the specified header block is targeted to a SOAP 1.1 node given the specified 
     * parameters.
     * 
     * @param header the header to evaluate
     * @param nodeActors the explicitly specified node actors for which the header is desired
     * @param isFinalDestination true specifies that headers targeted for message final destination should be returned,
     *          false specifies they should not be returned
     * @return the list of matching header blocks
     */
    public static boolean isSOAP11HeaderTargetedToNode(@Nonnull final XMLObject header,
            @Nullable final Set<String> nodeActors, final boolean isFinalDestination) {
        final String headerActor = SOAPSupport.getSOAP11ActorAttribute(header);
        if (headerActor == null) {
            if (isFinalDestination) {
                return true;
            }
        } else if (ActorBearing.SOAP11_ACTOR_NEXT.equals(headerActor)) {
            return true;
        } else if (nodeActors != null && nodeActors.contains(headerActor)) {
            return true;
        }
        return false;
    }
    
    /**
     * Add a header block to the SOAP envelope contained within the specified message context's
     * SOAP subcontext.
     * 
     * @param messageContext the message context being processed
     * @param headerBlock the header block to add
     */
    public static void addHeaderBlock(@Nonnull final MessageContext messageContext,
            @Nonnull final XMLObject headerBlock) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        
        // SOAP 1.1 Envelope
        final SOAP11Context soap11 = getSOAP11Context(messageContext);
        
        if (soap11 != null) {
            final Envelope env = soap11.getEnvelope();
            if (env != null) {
                addSOAP11HeaderBlock(env, headerBlock);
            }
        } else {
            //TODO SOAP 1.2 support when object providers are implemented
            throw new IllegalArgumentException("Message context did not contain a SOAP Envelope");
        }
    }

    /**
     * Add a header to the SOAP 1.1 Envelope.
     * 
     * @param envelope the SOAP 1.1 envelope to process
     * @param headerBlock the header to add
     */
    public static void addSOAP11HeaderBlock(@Nonnull final Envelope envelope, @Nonnull final XMLObject headerBlock) {
        Constraint.isNotNull(envelope, "Envelope cannot be null");
        Constraint.isNotNull(headerBlock, "Header block cannot be null");
        
        Header envelopeHeader = envelope.getHeader();
        if (envelopeHeader == null) {
            envelopeHeader = (Header) XMLObjectProviderRegistrySupport.getBuilderFactory().ensureBuilder(
                    Header.DEFAULT_ELEMENT_NAME).buildObject(Header.DEFAULT_ELEMENT_NAME);
            envelope.setHeader(envelopeHeader);
        }
        
        envelopeHeader.getUnknownXMLObjects().add(headerBlock);
    }
    
    /**
     * Register a SOAP 1.1. fault based on its constituent information items.
     * 
     * @param messageContext the current message context
     * @param faultCode the fault code QName (required)
     * @param faultString the fault message string value (required)
     * @param faultActor the fault actor value (may be null)
     * @param detailChildren the detail child elements
     * @param detailAttributes the detail element attributes
     */
    // Checkstyle: ParameterNumber OFF -- fault just has that many constituent parts
    public static void registerSOAP11Fault(@Nonnull final MessageContext messageContext, 
            @Nonnull final QName faultCode, @Nonnull final String faultString, @Nullable final String faultActor, 
            @Nullable final List<XMLObject> detailChildren, @Nullable final Map<QName, String> detailAttributes) {
        
        registerSOAP11Fault(messageContext, 
                SOAPSupport.buildSOAP11Fault(faultCode, faultString, faultActor, detailChildren, detailAttributes));
    }
    // Checkstyle: ParameterNumber
    
    /**
     * Register a SOAP 1.1 fault.
     * 
     * @param messageContext the current message context
     * @param fault the fault to register
     */
    public static void registerSOAP11Fault(@Nonnull final MessageContext messageContext, @Nullable final Fault fault) {
        final SOAP11Context soap11Context = ensureSOAP11Context(messageContext);
        
        soap11Context.setFault(fault);
    }
    
    /**
     * Get the registered SOAP 1.1 fault, if any.
     * 
     * @param messageContext the current message context
     * @return the registered fault, or null
     */
    public static Fault getSOAP11Fault(@Nonnull final MessageContext messageContext) {
        final SOAP11Context soap11Context = getSOAP11Context(messageContext);
        if (soap11Context != null) {
            return soap11Context.getFault();
        }
        return null;
    }
    
    /**
     * Clear the currently registered SOAP fault, if any.
     * 
     * @param messageContext the current message context
     */
    public static void clearFault(@Nonnull final MessageContext messageContext) {
        final SOAP11Context soap11Context = getSOAP11Context(messageContext);
        if (soap11Context != null) {
            soap11Context.setFault(null);
        }
        //TODO SOAP 1.2
    }
    
}