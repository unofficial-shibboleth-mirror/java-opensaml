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

package org.opensaml.xmlsec.agreement;

import java.util.Collection;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.collection.ClassIndexedSet;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.InitializableComponent;

/**
 * Specialized collection type for holding sets of parameters to key agreement operations.
 */
public class KeyAgreementParameters extends ClassIndexedSet<KeyAgreementParameter> {
    
    /**
     * Constructor.
     *
     */
    public KeyAgreementParameters() {
        super();
    }

    /**
     * Copy constructor.
     *
     * @param source the source set from which to copy
     */
    public KeyAgreementParameters(@Nonnull final Collection<KeyAgreementParameter> source) {
        this(source, false);
    }
    
    /**
     * Copy constructor with parameter clone option.
     *
     * @param source the source set from which to copy
     * @param clone if true each parameter which is a {@link CloneableKeyAgreementParameter}
     *              will be cloned before being added
     */
    public KeyAgreementParameters(@Nonnull final Collection<KeyAgreementParameter> source, final boolean clone) {
        this();
        for (final KeyAgreementParameter param : source) {
            if (clone && CloneableKeyAgreementParameter.class.isInstance(param)) {
                add(CloneableKeyAgreementParameter.class.cast(param).clone());
            } else {
                add(param);
            }
        }
    }
    
    /**
     * A convenience method for initializing all parameters which are initializable.
     * 
     * @throws KeyAgreementException if any parameters fail initialization
     */
    public void initializeAll() throws KeyAgreementException {
        for (final KeyAgreementParameter param : this) {
            if (InitializableComponent.class.isInstance(param)) {
               final InitializableComponent component = InitializableComponent.class.cast(param);
               if (!component.isInitialized()) {
                   try {
                       component.initialize();
                   } catch (final ComponentInitializationException e) {
                       throw new KeyAgreementException("Error initializing KeyAgreementParameter: "
                               + component.getClass().getName(), e);
                   }
               }
            }
        }
    }

}
