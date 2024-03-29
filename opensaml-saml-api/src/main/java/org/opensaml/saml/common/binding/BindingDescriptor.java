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

package org.opensaml.saml.common.binding;

import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.profile.context.ProfileRequestContext;

import com.google.common.base.MoreObjects;

/**
 * A class that describes a SAML binding and encapsulates information required for
 * profile actions to interact with them.
 * 
 * <p>Configuration logic should expose the usable bindings to profile actions
 * through instances of this class.</p>
 */
public class BindingDescriptor extends AbstractIdentifiableInitializableComponent
        implements Predicate<ProfileRequestContext> {
    
    /** Predicate that must be true for this flow to be usable for a given request. */
    @Nonnull private Predicate<ProfileRequestContext> activationCondition;
    
    /** Identifies a binding that is direct request/response between two parties (i.e., SOAP). */
    private boolean synchronous;
    
    /** Identifies a binding that relies on SAML artifacts. */
    private boolean artifact;
    
    /** Indicates whether the binding provides a built-in signing mechanism. */
    private boolean signatureCapable;
    
    /** A short name for the binding. */
    @Nullable @NotEmpty private String shortName;
    
    /** Constructor. */
    public BindingDescriptor() {
        synchronous = false;
        artifact = false;
        signatureCapable = false;
        activationCondition = PredicateSupport.alwaysTrue();
    }
    
    /**
     * Set the activation condition in the form of a {@link Predicate} such that iff the condition
     * evaluates to true should the corresponding binding be allowed/possible.
     * 
     * @param condition predicate that controls activation of the binding
     */
    public void setActivationCondition(@Nonnull final Predicate<ProfileRequestContext> condition) {
        activationCondition = Constraint.isNotNull(condition, "Activation condition predicate cannot be null");
    }
    
    /**
     * Get whether the binding is synchronous (direct request/response, typically SOAP).
     * 
     * @return true iff the binding is synchronous
     */
    public boolean isSynchronous() {
       return synchronous; 
    }
    
    /**
     * Set whether the binding is synchronous (direct request/response, typically SOAP).
     * 
     * @param flag  flag to set
     */
    public void setSynchronous(final boolean flag) {
        synchronous = flag;
    }

    /**
     * Get whether the binding is artifact-based.
     * 
     * @return true iff the binding is artifact-based
     */
    public boolean isArtifact() {
       return artifact;
    }
    
    /**
     * Set whether the binding is artifact-based.
     * 
     * @param flag  flag to set
     */
    public void setArtifact(final boolean flag) {
        artifact = flag;
    }
    
    /**
     * Get whether the binding provides a message signature capability.
     * 
     * @return true iff the binding provides a message signature capability
     */
    public boolean isSignatureCapable() {
        return signatureCapable;
    }
    
    /**
     * Set whether the binding provides a message signature capability.
     * 
     * @param flag flag to set
     */
    public void setSignatureCapable(final boolean flag) {
        signatureCapable = flag;
    }
    
    /**
     * Get a short/concise name for the binding.
     * 
     * @return the short name
     */
    @Nullable @NotEmpty public String getShortName() {
        return shortName;
    }
    
    /**
     * Set a short/concise name for the binding.
     * 
     * @param name name to set
     */
    public void setShortName(@Nullable @NotEmpty final String name) {
        shortName = StringSupport.trimOrNull(name);
    }

    /** {@inheritDoc} */
    @Override
    public boolean test(@Nullable final ProfileRequestContext input) {
        return activationCondition.test(input);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return ensureId().hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof BindingDescriptor) {
            return ensureId().equals(((BindingDescriptor) obj).getId());
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("bindingId", ensureId())
                .add("shortName", shortName)
                .add("synchronous", synchronous)
                .add("artifact", artifact)
                .add("signatureCapable", signatureCapable)
                .toString();
    }

}