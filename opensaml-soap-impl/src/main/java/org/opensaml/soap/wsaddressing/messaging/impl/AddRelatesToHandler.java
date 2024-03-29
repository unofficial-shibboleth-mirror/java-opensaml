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

package org.opensaml.soap.wsaddressing.messaging.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ContextDataLookupFunction;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.AbstractHeaderGeneratingMessageHandler;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.wsaddressing.RelatesTo;
import org.opensaml.soap.wsaddressing.messaging.WSAddressingContext;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Handler implementation that adds a wsa:RelatesTo header to the outbound SOAP envelope.
 */
public class AddRelatesToHandler extends AbstractHeaderGeneratingMessageHandler {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AddRelatesToHandler.class);
    
    /** Optional lookup function for obtaining the RelatesTo URI value. */
    @Nullable private ContextDataLookupFunction<MessageContext, String> relatesToURILookup;
    
    /** The effective RelatesTo URI value to use. */
    @Nullable private String relatesToURI;
    
    /** The effective RelatesTo RelationshipType attribute value to use. */
    @Nullable private String relationshipType;
    
    /**
     * Get the function for looking up the RelatesTo URI value.
     * 
     * @return the lookup function
     */
    @Nullable public ContextDataLookupFunction<MessageContext, String> getRelatesToURILookup() {
        return relatesToURILookup;
    }

    /**
     * Set the function for looking up the RelatesTo URI value.
     * 
     * @param lookup the lookup function
     */
    public void setRelatesToURILookup(@Nullable final ContextDataLookupFunction<MessageContext, String> lookup) {
        checkSetterPreconditions();
        relatesToURILookup = lookup;
    }

    /**
     * Get the RelatesTo RelationshipType attribute value to use.
     * 
     * @return the relationship type
     */
    @Nullable public String getRelationshipType() {
        return relationshipType;
    }

    /**
     * Set the RelatesTo RelationshipType attribute value to use.
     * 
     * @param value the relationship type
     */
    public void setRelationshipType(@Nullable final String value) {
        checkSetterPreconditions();
        relationshipType = StringSupport.trimOrNull(value);
    }

    /** {@inheritDoc} */
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        final WSAddressingContext addressing = messageContext.getSubcontext(WSAddressingContext.class);
        if (addressing != null) {
            relatesToURI = addressing.getRelatesToURI();
            if (relationshipType == null) {
                relationshipType = addressing.getRelatesToRelationshipType();
            }
        }
        
        if (relatesToURI == null && relatesToURILookup != null) {
            relatesToURI = relatesToURILookup.apply(messageContext);
        }
        
        if (relatesToURI == null) {
            log.debug("No WS-Addressing RelatesTo value found in message context, skipping further processing");
            return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        log.debug("Issuing WS-Addressing RelatesTo header with URI '{}' and RelationshipType '{}'", 
                relatesToURI, relationshipType);
        final RelatesTo relatesTo = (RelatesTo) XMLObjectSupport.buildXMLObject(RelatesTo.ELEMENT_NAME);
        relatesTo.setURI(relatesToURI);
        relatesTo.setRelationshipType(relationshipType);
        decorateGeneratedHeader(messageContext, relatesTo);
        SOAPMessagingSupport.addHeaderBlock(messageContext, relatesTo);
    }

}