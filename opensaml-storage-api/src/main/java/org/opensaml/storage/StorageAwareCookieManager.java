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

package org.opensaml.storage;

import java.io.IOException;
import java.time.Instant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.net.CookieManager;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An extended {@link CookieManager} that allows use of a {@link StorageService}.
 * 
 * <p>Reads are backed up by a read into the storage service, while writes are passed
 * through to it.</p>
 * 
 * <p>This is NOT suitable for use cases in which consistency of data is critical, as
 * there are few if any storage options (other than the client itself) that will provide
 * sufficient reliability and locking to avoid problems.</p>
 * 
 * @since 5.1.0
 */
public class StorageAwareCookieManager extends CookieManager {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(org.opensaml.storage.StorageAwareCookieManager.class);
    
    /** Optional storage service to backstop the cookie. */
    @Nullable private StorageService storageService;
    
    /** Storage context based on fixed value and cookie attributes. */
    @NonnullAfterInit private String storageContext;
    
    /**
     * Sets the {@link StorageService} to read/write.
     * 
     * @param ss storage service
     */
    public void setStorageService(@Nullable final StorageService ss) {
        checkSetterPreconditions();
        
        storageService = ss;
    }
    
    /**
     * Get the storage context used to hold the cookies.
     * 
     * @return storage context
     */
    @NonnullAfterInit public String getStorageContext() {
        return storageContext;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (getMaxAge() == -1 && storageService != null) {
            log.warn("Unsetting StorageService due to per-session max-age setting");
            storageService = null;
        }

        final StringBuilder contextBuilder = new StringBuilder(getClass().getName());
        contextBuilder.append('!');
        if (getCookieDomain() != null) {
            contextBuilder.append(getCookieDomain());
        }
        contextBuilder.append('!');
        if (getCookiePath() != null) {
            contextBuilder.append(getCookiePath());
        }
        
        storageContext = contextBuilder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public void addCookie(@Nonnull final String name, @Nonnull final String value) {
        super.addCookie(name, value);

        final Long exp = Instant.now().plusSeconds(getMaxAge()).toEpochMilli();
        
        final StorageService ss = storageService;
        if (ss != null) {
            try {
                if (ss.create(storageContext, name, value, exp)) {
                    log.trace("Created new cookie record {}", name);
                } else if (ss.update(storageContext, name, value, exp)) {
                    log.trace("Updated cookie record {}", name);
                }
            } catch (final IOException e) {
                log.warn("Error creating/updating cookie record in storage service", e);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void unsetCookie(@Nonnull final String name) {
        super.unsetCookie(name);
        
        if (storageService != null) {
            try {
                storageService.delete(storageContext, name);
            } catch (final IOException e) {
                log.warn("Error deleting cookie record from storage service", e);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public String getCookieValue(@Nonnull final String name, @Nullable final String defValue) {
        final String val = super.getCookieValue(name, defValue);
        if (val != null) {
            return val;
        }
        
        if (storageService != null) {
            try {
                final StorageRecord<String> record = storageService.read(storageContext, name);
                if (record != null) {
                    log.debug("Backfilling/setting missing cookie {} based on stored record", name);
                    final Long exp = record.getExpiration();
                    if (exp != null) {
                        // Uses protected hook to override max-age to backdate it.
                        super.addCookie(name, record.getValue(), (int) (exp - Instant.now().toEpochMilli()) / 1000);
                    } else {
                        // Won't ever happen, per init checking.
                        super.addCookie(name, record.getValue(), -1);
                    }
                    return record.getValue();
                }
            } catch (final IOException e) {
                log.warn("Error reading cookie record from storage service", e);
            }
        }
        
        return defValue;
    }
    
}