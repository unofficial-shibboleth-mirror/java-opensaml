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

package org.opensaml.saml.metadata.resolver.filter;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;

/**
 * A processor of element nodes within a metadata tree.  A particular implementation may process or
 * handle more than one type of node.
 */
public interface MetadataNodeProcessor {
    
    /**
     * Process a metadata document node.
     * 
     * @param metadataNode the metadata node to process
     * @throws FilterException if a fatal error is encountered while processing the node
     */
    void process(@Nonnull XMLObject metadataNode) throws FilterException;
    
}