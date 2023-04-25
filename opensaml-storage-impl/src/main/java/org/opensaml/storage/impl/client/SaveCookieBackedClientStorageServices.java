/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.storage.impl.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;
import org.slf4j.Logger;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An action that performs any number of {@link ClientStorageServiceOperation} instances sourced from
 * cookies by issuing the necessary Set-Cookie headers.
 * 
 * <p>The {@link ClientStorageSaveContext} is also removed.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_PROFILE_CTX}
 * @post ProfileRequestContext.getSubcontext(ClientStorageSaveContext.class) == null
 */
public class SaveCookieBackedClientStorageServices
        extends AbstractProfileAction {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SaveCookieBackedClientStorageServices.class);
    
    /** The storage service instances to load, keyed by their bean ID. */
    @Nonnull @NonnullElements private Map<String,ClientStorageService> storageServices;
    
    /** Context to drive storage save. */
    @Nullable private ClientStorageSaveContext clientStorageSaveCtx;
    
    /** URL encoder. */
    @Nonnull private Escaper escaper;
    
    /** Constructor. */
    public SaveCookieBackedClientStorageServices() {
        storageServices = CollectionSupport.emptyMap();
        escaper = UrlEscapers.urlFormParameterEscaper();
    }
    
    /**
     * Set the {@link ClientStorageService} instances to check for loading.
     * 
     * @param services instances to check for loading
     */
    public void setStorageServices(@Nonnull @NonnullElements final Collection<ClientStorageService> services) {
        checkSetterPreconditions();
        
        Constraint.isNotNull(services, "StorageService collection cannot be null");
        storageServices = new HashMap<>(services.size());
        for (final ClientStorageService ss : services) {
            if (ss != null) {
                storageServices.put(ss.ensureId(), ss);
            }
        }
    }
    
    /** {@inheritDoc} */
    @Override protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        if (storageServices.isEmpty()) {
            log.debug("{} No ClientStorageServices supplied, nothing to do", getLogPrefix());
            return false;
        }
        
        clientStorageSaveCtx = profileRequestContext.getSubcontext(ClientStorageSaveContext.class);
        if (clientStorageSaveCtx == null) {
            log.debug("{} No ClientStorageSaveContext found", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return false;
        }
        
        assert clientStorageSaveCtx != null;
        if (!clientStorageSaveCtx.isSourceRequired(ClientStorageSource.COOKIE)) {
            log.debug("{} No cookie operations required", getLogPrefix());
            assert clientStorageSaveCtx != null;
            profileRequestContext.removeSubcontext(clientStorageSaveCtx);
            return false;
        }
        
        return true;
    }

    /** {@inheritDoc} */
    @Override protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        assert clientStorageSaveCtx != null;
        for (final ClientStorageServiceOperation operation : clientStorageSaveCtx.getStorageOperations()) {
            
            final ClientStorageService storageService = storageServices.get(operation.getStorageServiceID());
            if (storageService == null) {
                log.error("{} ClientStorageService with ID '{}' missing from configuration", getLogPrefix(),
                        operation.getStorageServiceID());
                continue;
            }
            if (operation.getValue() != null) {
                log.debug("{} Saving data for ClientStorageService '{}' to cookie named '{}'", getLogPrefix(),
                        operation.getStorageServiceID(), operation.getKey());
                storageService.getCookieManager().addCookie(operation.getKey(),
                        escaper.escape(operation.getValue()));
            } else {
                log.debug("{} Clearing data for ClientStorageService '{}' from cookie named '{}'", getLogPrefix(),
                        operation.getStorageServiceID(), operation.getKey());
                storageService.getCookieManager().unsetCookie(operation.getKey());
            }
        }
        
        assert clientStorageSaveCtx != null;
        profileRequestContext.removeSubcontext(clientStorageSaveCtx);
    }

}