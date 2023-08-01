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

package org.opensaml.security.crypto.ec;

import java.security.spec.ECParameterSpec;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A specialized subclass of {@link ECParameterSpec} which wraps an existing instance and implements
 * {@link #hashCode()} and {@link #equals(Object)} so that it may be used directly in hashtable-based collections,
 * as well as simplifying equality comparisons between 2 instances.
 */
public class EnhancedECParameterSpec extends ECParameterSpec {
    
    /** The original instance. */
    @Nonnull private final ECParameterSpec original;
    
    /**
     * Constructor.
     *
     * @param spec the parameter spec instance to wrap
     */
    public EnhancedECParameterSpec(@Nonnull final ECParameterSpec spec) {
        super(spec.getCurve(), spec.getGenerator(), spec.getOrder(), spec.getCofactor());
        original = spec;
    }
    
    /**
     * Get the original instance passed to the constructor.
     * 
     * @return the original instance
     */
    @Nonnull public ECParameterSpec getOriginal() {
        return original;
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return Objects.hash(getCurve(), getGenerator(), getOrder(), getCofactor());
    }

    /** {@inheritDoc} */
    public boolean equals(@Nullable final Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (obj instanceof ECParameterSpec other) {
            // Copying Santuario's logic here.  It seems curve's ECField is an interface and the impls
            // don't obviously take into account the field size equality.  This field size compare is maybe
            // redundant with ECField.equals(), but eval it explicitly to be safe.
            // Comparing 2 int values isn't expensive.
            return this.getCurve().getField().getFieldSize() == other.getCurve().getField().getFieldSize()
                    && this.getCurve().equals(other.getCurve())
                    && this.getGenerator().equals(other.getGenerator())
                    && this.getOrder().equals(other.getOrder())
                    && this.getCofactor() == other.getCofactor();
        }
        
        return false;
    }

}