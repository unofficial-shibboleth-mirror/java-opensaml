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

package org.opensaml.messaging.handler.impl;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A handler that schema validates an XML-based message.
 * 
 * @pre <pre>MessageContext.getMessage().getDOM() != null</pre>
 */
public class SchemaValidateXMLMessage extends AbstractMessageHandler {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(SchemaValidateXMLMessage.class);

    /** Schema used to validate incoming messages. */
    @Nonnull private final Schema validationSchema;

    /**
     * Constructor.
     * 
     * @param schema schema used to validate incoming messages
     */
    public SchemaValidateXMLMessage(@Nonnull final Schema schema) {
        validationSchema = Constraint.isNotNull(schema, "Schema cannot be null");
    }

    /**
     * Gets the schema used to validate incoming messages.
     * 
     * @return schema used to validate incoming messages, not null after action is initialized
     */
    @Nonnull public Schema getValidationSchema() {
        return validationSchema;
    }

    /** {@inheritDoc} */
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext)
            throws MessageHandlerException {
        
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        if (!(messageContext.getMessage() instanceof XMLObject)) {
            log.debug("{} Message context did not contain an XMLObject, unable to proceed", getLogPrefix());
            throw new MessageHandlerException("Message context did not contain an XMLObject, unable to proceed.");
        }
        
        return true;
    }
    
    /** {@inheritDoc} */
    protected void doInvoke(@Nonnull final MessageContext messageContext)
            throws MessageHandlerException {

        log.debug("{} Attempting to schema validate incoming message", getLogPrefix());
        
        final XMLObject message = (XMLObject) messageContext.ensureMessage();
        if (message.getDOM() == null) {
            log.debug("{} Message doesn't contain a DOM, unable to proceed", getLogPrefix());
            throw new MessageHandlerException("Message doesn't contain a DOM, unable to proceed.");
        }
        
        
        try {
            final Validator schemaValidator = validationSchema.newValidator();
            schemaValidator.validate(new DOMSource(message.getDOM()));
        } catch (final SAXException e) {
            log.debug("{} Message {} is not schema-valid: {}", getLogPrefix(), message.getElementQName(),
                    e.getMessage());
            throw new MessageHandlerException("Message is not schema-valid.", e);
        } catch (final IOException e) {
            log.debug("{} Unable to read message: {}", getLogPrefix(), e.getMessage());
            throw new MessageHandlerException("Unable to read message.", e);
        }

        log.debug("{} Message {} is valid", getLogPrefix(), message.getElementQName());
    }
    
}