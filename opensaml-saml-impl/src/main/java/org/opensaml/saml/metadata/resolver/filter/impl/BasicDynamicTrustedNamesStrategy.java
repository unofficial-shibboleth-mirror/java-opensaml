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

package org.opensaml.saml.metadata.resolver.filter.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.metadata.AffiliationDescriptor;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;

/**
 * Function which implements a basic strategy for extracting trusted names for PKIX trust engine evaluation.
 * 
 * <p>
 * Names are extracted as follows from these signed metadata element types:
 * </p>
 * <ul>
 * <li><code>EntityDescriptor</code>: the <code>entityID</code> attribute</li> 
 * <li><code>EntitiesDescriptor</code>: the <code>Name</code> attribute</li> 
 * <li><code>RoleDescriptor</code>: the <code>entityID</code> attribute of the parent 
 *     <code>EntityDescriptor</code></li> 
 * <li><code>AffiliationDescriptor</code>: 1) the <code>affiliationOwnerID</code> attribute and 
 *     2) the <code>entityID</code> attribute of the parent <code>EntityDescriptor</code></li> 
 * </ul>
 */
public class BasicDynamicTrustedNamesStrategy implements Function<XMLObject, Set<String>> {

// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Nonnull public Set<String> apply(@Nullable final XMLObject input) {
        if (input == null) {
            return CollectionSupport.emptySet();
        }
        
        Set<String> rawResult = null;
        
        if (input instanceof EntityDescriptor entity) {
            final String entityID = entity.getEntityID();
            if (entityID != null) {
                rawResult = CollectionSupport.singleton(entityID);
            }
        } else if (input instanceof EntitiesDescriptor entities) {
            final String name = entities.getName();
            if (name != null) {
                rawResult = CollectionSupport.singleton(name);
            }
        } else if (input instanceof RoleDescriptor) {
            final XMLObject parent = input.getParent();
            if (parent instanceof EntityDescriptor entity) {
                final String entityID = entity.getEntityID();
                if (entityID != null) {
                    rawResult = CollectionSupport.singleton(entityID);
                }
            }
        } else if (input instanceof AffiliationDescriptor affil) {
            rawResult = new HashSet<>();
            
            final String owner = affil.getOwnerID();
            if (owner != null) {
                rawResult.add(owner);
            }
            
            final XMLObject parent = input.getParent();
            if (parent instanceof EntityDescriptor entity) {
                final String entityID = entity.getEntityID();
                if (entityID != null) {
                    rawResult.add(entityID);
                }
            }
        }
        
        if (rawResult != null) {
            return CollectionSupport.copyToSet(StringSupport.normalizeStringCollection(rawResult));
        }
        
        return CollectionSupport.emptySet();
    }
// Checkstyle: CyclomaticComplexity ON

}