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
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An action that creates and populates a {@link ClientStorageSaveContext} with any storage operations
 * identified as required from the current session and in need of saving.
 * 
 * <p>The action will signal the {@link #SAVE_NOT_NEEDED} event if it is unnecessary to proceed with the
 * save operation.</p>
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @event {@link #SAVE_NOT_NEEDED}
 */
public class PopulateClientStorageSaveContext extends AbstractProfileAction {

    /** Event signaling that no load step is necessary. */
    @Nonnull @NotEmpty public static final String SAVE_NOT_NEEDED = "NoSaveNeeded";
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(PopulateClientStorageSaveContext.class);

    /** The storage service instances to check for a save requirement. */
    @Nonnull @NonnullElements private Collection<ClientStorageService> storageServices;
    
    /** Constructor. */
    public PopulateClientStorageSaveContext() {
        storageServices = CollectionSupport.emptyList();
    }
    
    /**
     * Set the {@link ClientStorageService} instances to check for saving.
     * 
     * @param services instances to check for saving
     */
    public void setStorageServices(@Nonnull @NonnullElements final Collection<ClientStorageService> services) {
        checkSetterPreconditions();
        
        storageServices = CollectionSupport.copyToList(
                Constraint.isNotNull(services, "StorageService collection cannot be null"));
    }
    
    /** {@inheritDoc} */
    @Override protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            ActionSupport.buildEvent(profileRequestContext, SAVE_NOT_NEEDED);
            return false;
        }
        
        if (storageServices.isEmpty()) {
            log.debug("{} No ClientStorageServices supplied, nothing to do", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, SAVE_NOT_NEEDED);
            return false;
        }
        
        return true;
    }

    /** {@inheritDoc} */
    @Override protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        final ClientStorageSaveContext saveCtx = new ClientStorageSaveContext();
        
        for (final ClientStorageService service : storageServices) {
            final ClientStorageServiceOperation operation = service.save();
            if (operation != null) {
                saveCtx.getStorageOperations().add(operation);
            }
        }
        
        if (saveCtx.getStorageOperations().isEmpty()) {
            log.debug("{} No ClientStorageServices require saving, nothing to do", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, SAVE_NOT_NEEDED);
        } else {
            profileRequestContext.addSubcontext(saveCtx, true);
            
            if (log.isDebugEnabled()) {
                final Collection<String> ids = saveCtx.getStorageOperations().stream().map(
                        ClientStorageServiceOperation::getStorageServiceID).collect(Collectors.toList());
                log.debug("{} ClientStorageServices requiring save: {}", getLogPrefix(), ids);
            }
        }
    }

}