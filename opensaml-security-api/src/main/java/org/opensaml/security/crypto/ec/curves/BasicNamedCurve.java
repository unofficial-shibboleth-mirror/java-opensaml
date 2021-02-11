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

package org.opensaml.security.crypto.ec.curves;

import java.security.spec.ECParameterSpec;

import javax.annotation.Nonnull;

import org.opensaml.security.crypto.ec.NamedCurve;

import com.google.common.base.MoreObjects;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Basic implementations of {@link NamedCurve} where all required properties are simply supplied at
 * construction time.
 */
public class BasicNamedCurve implements NamedCurve {
    
    /** Curve name. */
    @Nonnull private String name;
    
    /** Curve OID. */
    @Nonnull private String oid;
    
    /** Curve's parameters as an instance of {@link ECParameterSpec}. */
    @Nonnull private ECParameterSpec params;

    /**
     * Constructor.
     *
     * @param objectIdentifier the curve's object identifier (OID)
     * @param standardName the curve's standard name
     * @param parameters the curve's parameters as an {@link ECParameterSpec}
     */
    public BasicNamedCurve(@Nonnull final String objectIdentifier, @Nonnull final String standardName,
            @Nonnull final ECParameterSpec parameters) {
        oid = Constraint.isNotNull(StringSupport.trimOrNull(objectIdentifier), "Curve identifier was null");
        name = Constraint.isNotNull(StringSupport.trimOrNull(standardName), "Curve name was null");
        params = Constraint.isNotNull(parameters, "Curve parameters was null");
    }

    /** {@inheritDoc} */
    public String getObjectIdentifier() {
        return oid;
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public ECParameterSpec getParameterSpec() {
        return params;
    }
    
    /** {@inheritDoc} */
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", getName())
                .add("OID", getObjectIdentifier())
                .toString();
    }
    
}

