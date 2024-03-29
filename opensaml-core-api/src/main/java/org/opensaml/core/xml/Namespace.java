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

package org.opensaml.core.xml;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.xml.XMLConstants;

/** Data structure for representing XML namespace attributes. */
public class Namespace {

    /** URI of the namespace. */
    @Nullable private String namespaceURI;

    /** Prefix of the namespace. */
    @Nullable private String namespacePrefix;

    /** String representation of this namespace. */
    @Nullable private String nsStr;

    /**
     * Constructor.
     * 
     * @param uri the URI of the namespace
     * @param prefix the prefix of the namespace
     */
    public Namespace(@Nullable final String uri, @Nullable final String prefix) {
        namespaceURI = StringSupport.trimOrNull(uri);
        namespacePrefix = StringSupport.trimOrNull(prefix);
        nsStr = null;
    }

    /**
     * Gets the prefix of the namespace.
     * 
     * @return the prefix of the namespace, may be null if this is a default namespace
     */
    @Nullable public String getNamespacePrefix() {
        return namespacePrefix;
    }

    /**
     * Gets the URI of the namespace.
     * 
     * @return the URI of the namespace
     */
    @Nullable public String getNamespaceURI() {
        return namespaceURI;
    }
    
    /**
     * Get the URI of the namespace, raising an {@link IllegalStateException} if null.
     * 
     * @return namespace URI
     * 
     * @since 5.0.0
     */
    @Nonnull public String ensureNamespaceURI() {
        if (namespaceURI != null) {
            return namespaceURI;
        }
        throw new IllegalStateException("Namespace URI was not set");
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (nsStr == null) {
            constructStringRepresentation();
        }

        return nsStr;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + toString().hashCode();
        return hash;
    }

    /**
     * Checks if the given object is the same as this Namespace. This is true if:
     * <ul>
     * <li>The given object is of type {@link Namespace}</li>
     * <li>The given object's namespace URI is the same as this object's namespace URI</li>
     * <li>The given object's namespace prefix is the same as this object's namespace prefix</li>
     * </ul>
     * 
     * @param obj {@inheritDoc}
     * 
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if(obj == this){
            return true;
        }
        
        if (obj instanceof Namespace) {
            final Namespace otherNamespace = (Namespace) obj;
            if (Objects.equals(otherNamespace.getNamespaceURI(), getNamespaceURI())){
                if (Objects.equals(otherNamespace.getNamespacePrefix(), getNamespacePrefix())){
                    return true;
                }
            }
        }

        return false;
    }

    /** Constructs an XML namespace declaration string representing this namespace. */
    protected void constructStringRepresentation() {
        final StringBuffer stringRep = new StringBuffer();

        stringRep.append(XMLConstants.XMLNS_PREFIX);

        if (namespacePrefix != null) {
            stringRep.append(":");
            stringRep.append(namespacePrefix);
        }

        stringRep.append("=\"");

        if (namespaceURI != null) {
            stringRep.append(namespaceURI);
        }

        stringRep.append("\"");

        nsStr = stringRep.toString();
    }
    
}