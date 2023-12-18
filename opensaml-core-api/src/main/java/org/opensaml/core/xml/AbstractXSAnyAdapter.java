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

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.IDIndex;
import org.w3c.dom.Element;

import net.shibboleth.shared.collection.LockableClassToInstanceMultiMap;
import net.shibboleth.shared.logic.Constraint;

/**
 * Abstract base class for implementations that adapt/wrap an instance of {@link XSAny}.
 */
public abstract class AbstractXSAnyAdapter implements XSAnyAdapter {
    
    /** The adapted XSAny instance. */
    @Nonnull private XSAny adapted;

    /**
     * Constructor.
     *
     * @param xsAny the adapted XSAny instance
     */
    protected AbstractXSAnyAdapter(@Nonnull final XSAny xsAny) {
        adapted = Constraint.isNotNull(xsAny, "Adapted XSAny may not be null");
    }
    
    /**
     * Get the adapted {@link XSAny} instance.
     * 
     * @return the adapted XSAny
     */
    @Nonnull
    public XSAny getAdapted() {
        return adapted;
    }

    /** {@inheritDoc} */
    public void detach() {
        adapted.detach();
    }

    /** {@inheritDoc} */
    @Nullable
    public Element getDOM() {
        return adapted.getDOM();
    }

    /** {@inheritDoc} */
    @Nonnull
    public Element ensureDOM() {
        return adapted.ensureDOM();
    }

    /** {@inheritDoc} */
    @Nonnull
    public QName getElementQName() {
        return adapted.getElementQName();
    }

    /** {@inheritDoc} */
    @Nonnull
    public IDIndex getIDIndex() {
        return adapted.getIDIndex();
    }

    /** {@inheritDoc} */
    @Nonnull
    public NamespaceManager getNamespaceManager() {
        return adapted.getNamespaceManager();
    }

    /** {@inheritDoc} */
    @Nonnull
    public Set<Namespace> getNamespaces() {
        return adapted.getNamespaces();
    }

    /** {@inheritDoc} */
    @Nullable
    public String getNoNamespaceSchemaLocation() {
        return adapted.getNoNamespaceSchemaLocation();
    }

    /** {@inheritDoc} */
    @Nullable
    public List<XMLObject> getOrderedChildren() {
        return adapted.getOrderedChildren();
    }

    /** {@inheritDoc} */
    @Nullable
    public XMLObject getParent() {
        return adapted.getParent();
    }

    /** {@inheritDoc} */
    @Nullable
    public String getSchemaLocation() {
        return adapted.getSchemaLocation();
    }

    /** {@inheritDoc} */
    @Nullable
    public QName getSchemaType() {
        return adapted.getSchemaType();
    }

    /** {@inheritDoc} */
    public boolean hasChildren() {
        return adapted.hasChildren();
    }

    /** {@inheritDoc} */
    public boolean hasParent() {
        return adapted.hasParent();
    }

    /** {@inheritDoc} */
    public void releaseChildrenDOM(final boolean propagateRelease) {
        adapted.releaseChildrenDOM(propagateRelease);
    }

    /** {@inheritDoc} */
    public void releaseDOM() {
        adapted.releaseDOM();
    }

    /** {@inheritDoc} */
    public void releaseParentDOM(final boolean propagateRelease) {
        adapted.releaseParentDOM(propagateRelease);
    }

    /** {@inheritDoc} */
    @Nullable
    public XMLObject resolveID(@Nonnull final String id) {
        return adapted.resolveID(id);
    }

    /** {@inheritDoc} */
    @Nullable
    public XMLObject resolveIDFromRoot(@Nonnull final String id) {
        return adapted.resolveIDFromRoot(id);
    }

    /** {@inheritDoc} */
    @Override
    public void setDOM(@Nullable final Element dom) {
        adapted.setDOM(dom);
    }

    /** {@inheritDoc} */
    public void setNoNamespaceSchemaLocation(@Nullable final String location) {
        adapted.setNoNamespaceSchemaLocation(location);
    }

    /** {@inheritDoc} */
    public void setParent(@Nullable final XMLObject parent) {
        adapted.setParent(parent);
    }

    /** {@inheritDoc} */
    public void setSchemaLocation(@Nullable final String location) {
        adapted.setSchemaLocation(location);
    }

    /** {@inheritDoc} */
    @Nullable
    public Boolean isNil() {
        return adapted.isNil();
    }

    /** {@inheritDoc} */
    @Nullable
    public XSBooleanValue isNilXSBoolean() {
        return adapted.isNilXSBoolean();
    }

    /** {@inheritDoc} */
    public void setNil(@Nullable final Boolean newNil) {
        adapted.setNil(newNil);
    }

    /** {@inheritDoc} */
    public void setNil(@Nullable final XSBooleanValue newNil) {
        adapted.setNil(newNil);
    }

    /** {@inheritDoc} */
    @Nonnull
    public LockableClassToInstanceMultiMap<Object> getObjectMetadata() {
        return adapted.getObjectMetadata();
    }

}
