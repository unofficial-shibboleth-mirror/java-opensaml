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

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A <code>MetadataProvider</code> implementation that retrieves metadata from a DOM <code>Element</code> as
 * supplied by the user.
 * 
 * It is the responsibility of the caller to re-initialize, via {@link #initialize()}, if any properties of this
 * provider are changed.
 */
public class DOMMetadataResolver extends AbstractBatchMetadataResolver {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(DOMMetadataResolver.class);

    /** Root metadata element exposed by this provider. */
    @Nonnull private Element metadataElement;

    /**
     * Constructor.
     * 
     * @param mdElement the metadata element
     */
    public DOMMetadataResolver(@Nonnull final Element mdElement) {
        metadataElement = Constraint.isNotNull(mdElement, "DOM Element cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override protected void initMetadataResolver() throws ComponentInitializationException {
        super.initMetadataResolver();
        
        try {
            final Unmarshaller unmarshaller = getUnmarshallerFactory().ensureUnmarshaller(metadataElement);
            final XMLObject metadataTemp = unmarshaller.unmarshall(metadataElement);
            final BatchEntityBackingStore newBackingStore = preProcessNewMetadata(metadataTemp);
            releaseMetadataDOM(metadataTemp);
            setBackingStore(newBackingStore);
        } catch (final UnmarshallingException e) {
            final String errorMsg = "Unable to unmarshall metadata element";
            log.error("{} {}: {}", getLogPrefix(), errorMsg, e.getMessage());
            throw new ComponentInitializationException(errorMsg, e);
        } catch (final FilterException e) {
            final String errorMsg = "Unable to filter metadata";
            log.error("{} {}: {}", getLogPrefix(), errorMsg, e.getMessage());
            throw new ComponentInitializationException(errorMsg, e);
        }
    }

}