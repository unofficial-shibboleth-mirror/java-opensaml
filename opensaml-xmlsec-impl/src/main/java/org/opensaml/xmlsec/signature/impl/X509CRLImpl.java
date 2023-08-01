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

package org.opensaml.xmlsec.signature.impl;

import java.lang.ref.Cleaner;
import java.lang.ref.Cleaner.Cleanable;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.IndexingObjectStore;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.CleanerSupport;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.X509CRL;

/** Concrete implementation of {@link X509CRL}. */
public class X509CRLImpl extends AbstractXMLObject implements X509CRL {

    /** Class-level index of Base64 encoded CRL values. */
    @Nonnull private static final IndexingObjectStore<String> B64_CRL_STORE = new IndexingObjectStore<>();

    /** The {@link Cleaner} instance to use. */
    @Nonnull private static final Cleaner CLEANER = CleanerSupport.getInstance(X509CRLImpl.class);

    /** The {@link Cleanable} representing the current instance's CRL value, as represented by the
     * current <code>b64CRLIndex</code> field value. */
    @Nullable private Cleaner.Cleanable cleanable;

    /** Index to a stored Base64 encoded CRL. */
    @Nullable private String b64CRLIndex;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected X509CRLImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getValue() {
        return B64_CRL_STORE.get(b64CRLIndex);
    }

    /** {@inheritDoc} */
    public void setValue(@Nullable final String newValue) {
        // Dump our cached DOM if the new value really is new
        final String currentCRL = B64_CRL_STORE.get(b64CRLIndex);
        final String newCRL = prepareForAssignment(currentCRL, newValue);

        // This is a new value, remove the old one, add the new one
        if (!Objects.equals(currentCRL, newCRL)) {
            if (cleanable != null) {
                cleanable.clean();
                cleanable = null;
            }
            b64CRLIndex = B64_CRL_STORE.put(newCRL);
            if (b64CRLIndex != null) {
                cleanable = CLEANER.register(this, new CleanerState(b64CRLIndex));
            }
        }
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        return null;
    }

    /**
     * The action to be taken when the current state must be cleaned.
     */
    static class CleanerState implements Runnable {

        /** The index to remove from the store. */
        @Nonnull private final String index;

        /**
         * Constructor.
         *
         * @param idx the index in the {@link X509CRLImpl#B64_CRL_STORE}.
         */
        public CleanerState(@Nonnull final String idx) {
            index = Constraint.isNotNull(idx, "Index cannot be null");
        }

        /** {@inheritDoc} */
        public void run() {
            X509CRLImpl.B64_CRL_STORE.remove(index);
        }

    }

}