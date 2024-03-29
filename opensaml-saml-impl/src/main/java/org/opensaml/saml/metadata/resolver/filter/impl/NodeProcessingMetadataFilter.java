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

package org.opensaml.saml.metadata.resolver.filter.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.filter.AbstractMetadataFilter;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilterContext;
import org.opensaml.saml.metadata.resolver.filter.MetadataNodeProcessor;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.logic.Constraint;

/**
 * An implementation of {@link MetadataFilter} which applies a {@link MetadataNodeProcessor} to each element node in the
 * metadata document tree. The node processors will be applied in the order of {@link List} provided by
 * {@link #setNodeProcessors(List)}. The metadata document tree is traversed depth-first.
 */
public class NodeProcessingMetadataFilter extends AbstractMetadataFilter {

    /** The ordered list of metadata node processors. */
    @Nonnull private List<MetadataNodeProcessor> processors;
    
    /** Constructor. */
    public NodeProcessingMetadataFilter() {
        processors = new ArrayList<>();
    }

    /**
     * Get the list of metadata node processors.
     * 
     * @return the list of metadata node processors
     */
    @Nonnull @Live public List<MetadataNodeProcessor> getNodeProcessors() {
        return processors;
    }

    /**
     * Set the list of metadata node processors.
     * 
     * @param newProcessors the new list of processors to set.
     */
    public void setNodeProcessors(@Nonnull final List<MetadataNodeProcessor> newProcessors) {
        checkSetterPreconditions();
        Constraint.isNotNull(newProcessors, "MetadataNodeProcessor list cannot be null");

        processors = new ArrayList<>(newProcessors);
    }

    /** {@inheritDoc} */
    @Nullable public XMLObject filter(@Nullable final XMLObject metadata, @Nonnull final MetadataFilterContext context)
            throws FilterException {
        checkComponentActive();

        if (metadata == null) {
            return null;
        }

        processNode(metadata);

        return metadata;
    }

    /**
     * Process an individual metadata node.
     * 
     * @param node the metadata node to process.
     * 
     * @throws FilterException if a fatal error is encountered while processing a node
     */
    protected void processNode(@Nonnull final XMLObject node) throws FilterException {
        
        for (final MetadataNodeProcessor processor : getNodeProcessors()) {
            processor.process(node);
        }

        final List<XMLObject> children = node.getOrderedChildren();
        if (children != null) {
            for (final XMLObject child : children) {
                assert child != null;
                processNode(child);
            }
        }
    }

}