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

package org.opensaml.soap.wssecurity.messaging;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.wssecurity.Security;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;


/**
 * Helper methods for working with WS-Security messaging.
 */
public final class WSSecurityMessagingSupport {

    /**
     * Private constructor.
     */
    private WSSecurityMessagingSupport() { }
    
    /**
     * Add a {@link Security} sub-header block to the SOAP envelope contained within the specified message context's
     * SOAP subcontext.
     * 
     * @param messageContext the message context to process
     * @param securityHeader the security sub-header block to add
     * @param mustUnderstand whether the sub-header should be added to a Security header block indicating
     *          that it must be understood
     */
    public static void addSecurityHeaderBlock(@Nonnull final MessageContext messageContext,
            @Nonnull final XMLObject securityHeader, final boolean mustUnderstand) {
        addSecurityHeaderBlock(messageContext, securityHeader, mustUnderstand, null, true);
    }
    
    /**
     * Add a {@link Security} sub-header block to the SOAP envelope contained within the specified message context's
     * SOAP subcontext.
     * 
     * @param messageContext the message context to process
     * @param securitySubHeader the security sub-header block to add
     * @param mustUnderstand whether the sub-header should be added to a Security header block indicating
     *          that it must be understood
     * @param targetNode the explicitly-specified SOAP node actor for which the header is desired
     * @param isFinalDestination true specifies that headers targeted for message final destination should be returned,
     *          false specifies they should not be returned
     */
    public static void addSecurityHeaderBlock(@Nonnull final MessageContext messageContext,
            @Nonnull final XMLObject securitySubHeader, final boolean mustUnderstand, 
            @Nullable final String targetNode, final boolean isFinalDestination) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        Constraint.isNotNull(securitySubHeader, "Security sub-header context cannot be null");

        final List<XMLObject> securityHeaders =
                SOAPMessagingSupport.getHeaderBlock(messageContext, Security.ELEMENT_NAME,
                targetNode != null ? CollectionSupport.singleton(targetNode) : null, 
                isFinalDestination);
        
        Security security = null;
        for (final XMLObject header : securityHeaders) {
            final Security candidate = (Security) header;
            assert candidate != null;
            final boolean candidateMustUnderstand = SOAPMessagingSupport.isMustUnderstand(messageContext, candidate);
            if (mustUnderstand == candidateMustUnderstand) {
                security = candidate;
                break;
            }
        }
        
        if (security == null) {
            security = (Security) XMLObjectSupport.buildXMLObject(Security.ELEMENT_NAME);
            if (mustUnderstand) {
                SOAPMessagingSupport.addMustUnderstand(messageContext, security, true);
            }
            if (targetNode != null) {
                SOAPMessagingSupport.addTargetNode(messageContext, security, targetNode);
            }
            SOAPMessagingSupport.addHeaderBlock(messageContext, security);
        }
        
        security.getUnknownXMLObjects().add(securitySubHeader);
    }
    
}
