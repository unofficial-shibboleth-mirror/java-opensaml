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

package org.opensaml.storage.impl.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;
import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An action that logs the results of Local Storage-based {@link ClientStorageService} save operations.
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 */
public class LogLocalStorageSaveResults extends AbstractProfileAction {

    /** Name of local storage form field signaling success/failure of a read operation. */
    @Nonnull @NotEmpty public static final String SUCCESS_FORM_FIELD = "shib_idp_ls_success";

    /** Name of local storage form field containing value read. */
    @Nonnull @NotEmpty public static final String EXCEPTION_FORM_FIELD = "shib_idp_ls_exception";

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(LogLocalStorageSaveResults.class);
    
    /** Context to drive storage load. */
    @Nullable private ClientStorageSaveContext clientStorageSaveCtx;
    
    /** {@inheritDoc} */
    @Override protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        clientStorageSaveCtx = profileRequestContext.getSubcontext(ClientStorageSaveContext.class);
        if (clientStorageSaveCtx == null) {
            return false;
        }
        
        return true;
    }

    /** {@inheritDoc} */
    @Override protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        final HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            log.error("{} HttpServletRequest not available", getLogPrefix());
            return;
        }
        

        assert clientStorageSaveCtx != null;
        for (final ClientStorageServiceOperation operation : clientStorageSaveCtx.getStorageOperations()) {
            if (operation.getStorageSource() == ClientStorageSource.HTML_LOCAL_STORAGE) {
                String param = request.getParameter(
                        LoadClientStorageServices.SUCCESS_FORM_FIELD + '.' + operation.getKey());
                if (param != null || Boolean.valueOf(param)) {
                    log.debug("{} Save to local storage for StorageService '{}' succeeded", getLogPrefix(),
                            operation.getStorageServiceID());
                } else {
                    param = request.getParameter(EXCEPTION_FORM_FIELD + '.' + operation.getKey());
                    log.warn("{} Save to local storage for StorageService '{}' failed: {}",
                            getLogPrefix(), operation.getStorageServiceID(), param);
                }
            }
        }
    }
    
}