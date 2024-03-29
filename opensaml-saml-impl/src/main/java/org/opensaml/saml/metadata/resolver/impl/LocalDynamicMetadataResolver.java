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

package org.opensaml.saml.metadata.resolver.impl;

import java.io.IOException;
import java.util.Timer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.persist.ConditionalLoadXMLObjectLoadSaveManager;
import org.opensaml.core.xml.persist.XMLObjectLoadSaveManager;
import org.slf4j.Logger;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * Resolver which dynamically resolves metadata from a local source managed by an instance
 * of {@link XMLObjectLoadSaveManager}.
 */
public class LocalDynamicMetadataResolver extends AbstractDynamicMetadataResolver {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(LocalDynamicMetadataResolver.class);
    
    /** The manager for the local store of metadata. */
    @Nonnull private XMLObjectLoadSaveManager<XMLObject> sourceManager;
    
    /** Function for generating the String key used with the source manager. */
    @Nonnull private Function<CriteriaSet, String> sourceKeyGenerator;

    /**
     * Constructor.
     * 
     * <p>
     * Source key generator will be an internal instance of {@link DefaultLocalDynamicSourceKeyGenerator},
     * with all default parameters.
     * </p>
     *
     * @param manager the manager for the local source of metadata
     */
    public LocalDynamicMetadataResolver(@Nonnull final XMLObjectLoadSaveManager<XMLObject> manager) {
        this(null, manager, null);
    }
    
    /**
     * Constructor.
     *
     * @param manager the manager for the local source of metadata
     * @param keyGenerator  the source key generator function
     */
    public LocalDynamicMetadataResolver(@Nonnull final XMLObjectLoadSaveManager<XMLObject> manager,
            @Nullable final Function<CriteriaSet, String> keyGenerator) {
        this(null, manager, keyGenerator);
    }
    
    /**
     * Constructor.
     *
     * <p>
     * If the supplied source key generator is null, an internal instance of 
     * {@link DefaultLocalDynamicSourceKeyGenerator}
     * will be used, with all default parameters.
     * </p>
     * @param backgroundTaskTimer timer for management of background tasks
     * @param manager the manager for the local source of metadata
     * @param keyGenerator the source key generator function
     */
    public LocalDynamicMetadataResolver(@Nullable final Timer backgroundTaskTimer, 
            @Nonnull final XMLObjectLoadSaveManager<XMLObject> manager,
            @Nullable final Function<CriteriaSet, String> keyGenerator) {
        
        super(backgroundTaskTimer);
        
        sourceManager = Constraint.isNotNull(manager, "Local source manager was null");
        
        if (keyGenerator == null) {
            sourceKeyGenerator = new DefaultLocalDynamicSourceKeyGenerator();
        } else {
            sourceKeyGenerator = keyGenerator;
        }
    }
    
    /** {@inheritDoc} */
    protected void removeByEntityID(@Nonnull final String entityID, @Nonnull final EntityBackingStore backingStore) {
        if (sourceManager instanceof ConditionalLoadXMLObjectLoadSaveManager) {
            final String key = sourceKeyGenerator.apply(new CriteriaSet(new EntityIdCriterion(entityID)));
            if (key != null) {
                ((ConditionalLoadXMLObjectLoadSaveManager<?>)sourceManager).clearLoadLastModified(key);
            }
        }
        
        super.removeByEntityID(entityID, backingStore);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable protected XMLObject fetchFromOriginSource(@Nullable final CriteriaSet criteria) throws IOException {
        final String key = sourceKeyGenerator.apply(criteria);
        if (key != null) {
            log.trace("{} Attempting to load from local source manager with generated key '{}'", getLogPrefix(), key);
            final XMLObject result = sourceManager.load(key);
            if (result != null) {
                log.trace("{} Successfully loaded target from local source manager source with key '{}' of type: {}",
                        getLogPrefix(), key, result.getElementQName());
            } else {
                log.trace("{} Found no target in local source manager with key '{}'", getLogPrefix(), key);
            }
            return result;
        }
        log.trace("{} Could not generate source key from criteria, can not resolve", getLogPrefix());
        return null;
    }
    
}