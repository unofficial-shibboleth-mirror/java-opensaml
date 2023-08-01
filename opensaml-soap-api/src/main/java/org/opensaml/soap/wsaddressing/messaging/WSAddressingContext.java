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

package org.opensaml.soap.wsaddressing.messaging;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;

import net.shibboleth.shared.primitive.StringSupport;

/**
 * A subcontext that carries information related to WS-Addressing processing.
 */
public class WSAddressingContext extends BaseContext {
    
    //TODO implement support for remaining items of WS-Addressing data model
    
    /** The Action URI value. */
    @Nullable private String actionURI;
    
    /** The Fault Action URI value. */
    @Nullable private String faultActionURI;
    
    /** The MessageID URI value. */
    @Nullable private String messageIDURI;
    
    /** The RelatesTo URI value. */
    @Nullable private String relatesToURI;
    
    /** The RelatesTo RelationshipType attribute value. */
    @Nullable private String relatesToRelationshipType;
    
    /**
     * Get the Action URI value.
     * 
     * @return the action URI.
     */
    @Nullable public String getActionURI() {
        return actionURI;
    }

    /**
     * Set the Action URI value.
     * 
     * @param uri the new Action URI value
     */
    public void setActionURI(@Nullable final String uri) {
        actionURI = StringSupport.trimOrNull(uri);
    }
    
    /**
     * Get the Fault Action URI value.
     * 
     * @return the fault action URI.
     */
    @Nullable public String getFaultActionURI() {
        return faultActionURI;
    }

    /**
     * Set the Fault Action URI value.
     * 
     * @param uri the new Fault Action URI value
     */
    public void setFaultActionURI(@Nullable final String uri) {
        faultActionURI = StringSupport.trimOrNull(uri);
    }
    
    /**
     * Get the MessageID URI value.
     * 
     * @return the MessageID URI
     */
    @Nullable public String getMessageIDURI() {
        return messageIDURI;
    }

    /**
     * Set the MessageID URI value.
     * 
     * @param uri the new MessageID URI value
     */
    public void setMessageIDURI(@Nullable final String uri) {
        messageIDURI = StringSupport.trimOrNull(uri);
    }

    /**
     * Get the RelatesTo URI value.
     * 
     * @return the RelatesTo URI
     */
    @Nullable public String getRelatesToURI() {
        return relatesToURI;
    }

    /**
     * Set the RelatesTo URI value.
     * 
     * @param uri the RelatesTo URI value
     */
    public void setRelatesToURI(@Nullable final String uri) {
        relatesToURI = StringSupport.trimOrNull(uri);
    }

    /**
     * Get the RelatesTo RelationshipType attribute value.
     * 
     * @return the RelatesTo RelationshipType attribute value
     */
    @Nullable public String getRelatesToRelationshipType() {
        return relatesToRelationshipType;
    }

    /**
     * Get the RelatesTo RelationshipType attribute value.
     * 
     * @param value the RelatesTo RelationshipType attribute value
     */
    public void setRelatesToRelationshipType(@Nullable final String value) {
        relatesToRelationshipType = StringSupport.trimOrNull(value);
    }

}
