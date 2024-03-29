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
import javax.xml.namespace.QName;

import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.AbstractHeaderGeneratingMessageHandler;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.wsaddressing.Action;
import org.opensaml.soap.wsaddressing.WSAddressingConstants;
import org.opensaml.soap.wsaddressing.messaging.WSAddressingContext;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Handler implementation that adds a wsa:Action header to the outbound SOAP envelope.
 * 
 *<p>
 * If a {@link Fault} is registered in the context, the value from 
 * {@link WSAddressingContext#getFaultActionURI()} will be used, if present. If not,
 * then the locally-configured value from {@link #getFaultActionURI()} will be used.
 * If neither is present, then a value will be selected based on the {@link Fault#getCode()}
 * via {@link #selectActionURIForFault(Fault)}.
 * </p> 
 * 
 * <p>
 * The value from {@link WSAddressingContext#getActionURI()} will be used, if present. If not,
 * then the locally-configured value from {@link #getActionURI()} will be used.  If neither is present,
 * no header will be added.
 * </p>
 */
public class AddActionHandler extends AbstractHeaderGeneratingMessageHandler {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AddActionHandler.class);
    
    /** The configured Action URI value. */
    @Nullable private String actionURI;
    
    /** The configured Fault Action URI value. */
    @Nullable private String faultActionURI;
    
    /** The actual calculated Action URI to send. */
    @Nullable private String sendURI;
    
    /**
     * Get the Action URI.
     * 
     * @return the URI, or null
     */
    @Nullable public String getActionURI() {
        return actionURI;
    }

    /**
     * Set the expected Action URI value. 
     * 
     * @param uri the new URI value
     */
    public void setActionURI(@Nullable final String uri) {
        checkSetterPreconditions();
        actionURI = StringSupport.trimOrNull(uri);
    }
    
    /**
     * Get the Fault Action URI.
     * 
     * @return the URI, or null
     */
    @Nullable public String getFaultActionURI() {
        return faultActionURI;
    }

    /**
     * Set the Fault Action URI value. 
     * 
     * @param uri the new URI value
     */
    public void setFaultActionURI(@Nullable final String uri) {
        checkSetterPreconditions();
        faultActionURI = StringSupport.trimOrNull(uri);
    }
    
    /** {@inheritDoc} */
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        final WSAddressingContext addressingContext = messageContext.getSubcontext(WSAddressingContext.class);
        
        final Fault fault = SOAPMessagingSupport.getSOAP11Fault(messageContext);
        if (fault != null) {
            log.debug("Saw SOAP Fault registered in message context, selecting Fault Action URI");
            if (addressingContext != null && addressingContext.getFaultActionURI() != null) {
                sendURI = addressingContext.getFaultActionURI();
            } else if (faultActionURI != null) {
                sendURI = faultActionURI;
            } else {
                sendURI = selectActionURIForFault(fault);
            }
        } else {
            if (addressingContext != null && addressingContext.getActionURI() != null) {
                sendURI = addressingContext.getActionURI();
            } else {
                sendURI = actionURI;
            }
        }
        
        if (sendURI == null) {
            log.debug("No WS-Addressing Action URI found locally or in message context, skipping further processing");
            return false;
        }
        
        return true;
    }
    
    /**
     * Select the Action URI value to return for the given {@link Fault}.
     * 
     * @param fault the fault
     * @return the selected Action URI
     */
    @Nonnull protected String selectActionURIForFault(@Nonnull final Fault fault) {
        QName faultCode =  null;
        final FaultCode fcode = fault.getCode();
        if (fcode != null) {
            faultCode = fcode.getValue(); 
        }
        if (faultCode != null && WSAddressingConstants.WS_ADDRESSING_FAULTS.contains(faultCode)) {
            return WSAddressingConstants.ACTION_URI_FAULT;
        }
        return WSAddressingConstants.ACTION_URI_SOAP_FAULT;
    }

    /** {@inheritDoc} */
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        log.debug("Issuing WS-Addressing Action header with URI value: {}", sendURI);
        final Action action = (Action) XMLObjectSupport.buildXMLObject(Action.ELEMENT_NAME);
        action.setURI(sendURI);
        decorateGeneratedHeader(messageContext, action);
        SOAPMessagingSupport.addHeaderBlock(messageContext, action);
    }

}
