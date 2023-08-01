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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;
import org.slf4j.Logger;

import com.google.common.base.Strings;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.net.URISupport;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An action that loads any number of {@link ClientStorageService} instances from a POST submission
 * or cookies as applicable.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_PROFILE_CTX}
 */
public class LoadClientStorageServices extends AbstractProfileAction {
    
    /** Name of local storage form field containing local storage support indicator. */
    @Nonnull @NotEmpty public static final String SUPPORT_FORM_FIELD = "shib_idp_ls_supported";

    /** Name of local storage form field signaling success/failure of a read operation. */
    @Nonnull @NotEmpty public static final String SUCCESS_FORM_FIELD = "shib_idp_ls_success";

    /** Name of local storage form field containing value read. */
    @Nonnull @NotEmpty public static final String VALUE_FORM_FIELD = "shib_idp_ls_value";

    /** Name of local storage form field containing value read. */
    @Nonnull @NotEmpty public static final String EXCEPTION_FORM_FIELD = "shib_idp_ls_exception";

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(LoadClientStorageServices.class);
    
    /** Whether to allow for data loaded from local storage and submitted via POST. */
    private boolean useLocalStorage;
    
    /** The storage service instances to load. */
    @Nonnull private Map<String,ClientStorageService> storageServices;
    
    /** Context to drive storage load. */
    @Nullable private ClientStorageLoadContext clientStorageLoadCtx;
    
    /** Constructor. */
    public LoadClientStorageServices() {
        useLocalStorage = false;
        storageServices = CollectionSupport.emptyMap();
    }

    /**
     * Set whether to allow for data loaded from local storage and submitted via POST.
     * 
     * @param flag flag to set
     */
    public void setUseLocalStorage(final boolean flag) {
        checkSetterPreconditions();
        
        useLocalStorage = flag;
    }
    
    /**
     * Set the {@link ClientStorageService} instances to check for loading.
     * 
     * @param services instances to check for loading
     */
    public void setStorageServices(@Nullable final Collection<ClientStorageService> services) {
        checkSetterPreconditions();
        
        if (services != null) {
            storageServices = services.stream()
                    .collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableMap(
                            ClientStorageService::getStorageName, ss -> ss)))
                    .get();
        } else {
            storageServices = CollectionSupport.emptyMap();
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
        
        clientStorageLoadCtx = profileRequestContext.getSubcontext(ClientStorageLoadContext.class);
        if (clientStorageLoadCtx == null) {
            log.debug("{} No ClientStorageLoadContext found", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return false;
        }
        
        return true;
    }

    /** {@inheritDoc} */
    @Override protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final HttpServletRequest httpRequest = getHttpServletRequest(); 
        if (httpRequest == null) {
            log.error("{} HttpServletRequest not available", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return;
        }
        

        boolean useLS = useLocalStorage;
        if (useLS) {
            final String param = httpRequest.getParameter(SUPPORT_FORM_FIELD);
            if (param == null || !Boolean.valueOf(param)) {
                log.debug("{} Local storage not available, backing off to cookies", getLogPrefix());
                useLS = false;
            }
        }
        
        assert clientStorageLoadCtx != null;
        for (final String storageKey : clientStorageLoadCtx.getStorageKeys()) {
            
            final ClientStorageService storageService = storageServices.get(storageKey);
            if (storageService == null) {
                log.error("{} ClientStorageService with storage name '{}' missing from configuration", getLogPrefix(),
                        storageKey);
                continue;
            }
            
            if (useLS) {
                loadFromLocalStorage(httpRequest, storageService);
            } else {
                loadFromCookie(httpRequest, storageService, ClientStorageSource.COOKIE);
            }
        }
        
        assert clientStorageLoadCtx != null;
        profileRequestContext.removeSubcontext(clientStorageLoadCtx);
    }

    /**
     * Load the specified storage service from a cookie.
     * 
     * @param httpRequest servlet request
     * @param storageService service to load
     * @param source source to apply to load operation
     */
    private void loadFromCookie(@Nonnull final HttpServletRequest httpRequest,
            @Nonnull final ClientStorageService storageService, @Nonnull final ClientStorageSource source) {
        
        Optional<Cookie> cookie = Optional.empty();
        
        // Search for our cookie.
        final Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            cookie = Arrays.asList(cookies).stream().filter(
                    c -> c != null && c.getName().equals(storageService.getStorageName())
                    ).findFirst();
        }

        if (!cookie.isPresent() || Strings.isNullOrEmpty(cookie.get().getValue())) {
            log.debug("{} No cookie data present, initializing StorageService '{}' to empty state", getLogPrefix(),
                    storageService.getId());
            storageService.load(null, source);
        } else {
            log.debug("{} Initializing StorageService '{}' from cookie", getLogPrefix(), storageService.getId());
            storageService.load(URISupport.doURLDecode(cookie.orElseThrow().getValue()), source);
        }
    }
 
    /**
     * Load the specified storage service from local storage data supplied in the POST.
     * 
     * @param httpRequest servlet request
     * @param storageService service to load
     */
    private void loadFromLocalStorage(@Nonnull final HttpServletRequest httpRequest,
            @Nonnull final ClientStorageService storageService) {
        
        String param = httpRequest.getParameter(SUCCESS_FORM_FIELD + '.' + storageService.getStorageName());
        if (param == null || !Boolean.valueOf(param)) {
            param = httpRequest.getParameter(EXCEPTION_FORM_FIELD + '.' + storageService.getStorageName());
            log.debug("{} Load from local storage failed ({}), initializing StorageService '{}' to empty state",
                    getLogPrefix(), param, storageService.getId());
            storageService.load(null, ClientStorageSource.HTML_LOCAL_STORAGE);
            return;
        }
        
        param = httpRequest.getParameter(VALUE_FORM_FIELD + '.' + storageService.getStorageName());
        if (param == null || param.isEmpty()) {
            log.debug("{} No local storage data present, checking for a cookie set by older storage implementation",
                    getLogPrefix(), storageService.getId());
            loadFromCookie(httpRequest, storageService, ClientStorageSource.HTML_LOCAL_STORAGE);
        } else {
            log.debug("{} Initializing StorageService '{}' from local storage data", getLogPrefix(),
                    storageService.getId());
            storageService.load(param, ClientStorageSource.HTML_LOCAL_STORAGE);
        }
    }

}