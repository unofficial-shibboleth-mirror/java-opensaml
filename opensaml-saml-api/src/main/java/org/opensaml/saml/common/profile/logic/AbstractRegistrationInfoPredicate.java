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

package org.opensaml.saml.common.profile.logic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.ext.saml2mdrpi.RegistrationInfo;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;

import java.util.function.Predicate;

/**
 * Base class for predicate that acts on {@link RegistrationInfo} content.
 */
public abstract class AbstractRegistrationInfoPredicate implements Predicate<EntityDescriptor> {
    
    /** What to do if no extension data exists. */
    private boolean matchIfMetadataSilent;
    
    /**
     * Set whether a predicate should evaluate to true if the data being matched on does not exist.
     * 
     * @param flag  flag to set
     */
    public void setMatchIfMetadataSilent(final boolean flag) {
        matchIfMetadataSilent = flag;
    }
    
    /**
     * Get whether a predicate should evaluate to true if the data being matched on does not exist.
     * 
     * @return true iff missing data should evaluate to true
     */
    public boolean getMatchIfMetadataSilent() {
        return matchIfMetadataSilent;
    }
    
    /**
     * Get the {@link RegistrationInfo} extension associated with an entity, if any.
     * 
     * @param entity the entity to examine
     * 
     * @return  the associated extension, or null
     */
    @Nullable protected RegistrationInfo getRegistrationInfo(@Nullable final EntityDescriptor entity) {
        
        if (null == entity) {
            return null;
        }
        
        Extensions extensions = entity.getExtensions();
        if (null != extensions) {
            for (final XMLObject object : extensions.getUnknownXMLObjects(RegistrationInfo.DEFAULT_ELEMENT_NAME)) {
                if (object instanceof RegistrationInfo) {
                    return (RegistrationInfo) object;
                }
            }
        }

        EntitiesDescriptor group = (EntitiesDescriptor) entity.getParent();
        while (null != group) {
            extensions = group.getExtensions();
            if (null != extensions) {
                for (final XMLObject object : extensions.getUnknownXMLObjects(RegistrationInfo.DEFAULT_ELEMENT_NAME)) {
                    if (object instanceof RegistrationInfo) {
                        return (RegistrationInfo) object;
                    }
                }
            }
            group = (EntitiesDescriptor) group.getParent();
        }
        
        return null;
    }
    
    /** {@inheritDoc} */
    public boolean test(@Nullable final EntityDescriptor input) {
        
        final RegistrationInfo info = getRegistrationInfo(input);
        if (info != null) {
            return doApply(info);
        }
        
        return matchIfMetadataSilent;
    }

    /**
     * Override this method to implement the predicate.
     * 
     * @param info the information to evaluate
     * 
     * @return the result of the predicate
     */
    protected abstract boolean doApply(@Nonnull final RegistrationInfo info);
    
}