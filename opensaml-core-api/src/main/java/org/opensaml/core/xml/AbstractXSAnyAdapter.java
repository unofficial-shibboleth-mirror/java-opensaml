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
    @Override
    public void detach() {
        adapted.detach();
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public Element getDOM() {
        return adapted.getDOM();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public Element ensureDOM() {
        return adapted.ensureDOM();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public QName getElementQName() {
        return adapted.getElementQName();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public IDIndex getIDIndex() {
        return adapted.getIDIndex();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public NamespaceManager getNamespaceManager() {
        return adapted.getNamespaceManager();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public Set<Namespace> getNamespaces() {
        return adapted.getNamespaces();
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public String getNoNamespaceSchemaLocation() {
        return adapted.getNoNamespaceSchemaLocation();
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public List<XMLObject> getOrderedChildren() {
        return adapted.getOrderedChildren();
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public XMLObject getParent() {
        return adapted.getParent();
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public String getSchemaLocation() {
        return adapted.getSchemaLocation();
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public QName getSchemaType() {
        return adapted.getSchemaType();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChildren() {
        return adapted.hasChildren();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasParent() {
        return adapted.hasParent();
    }

    /** {@inheritDoc} */
    @Override
    public void releaseChildrenDOM(boolean propagateRelease) {
        adapted.releaseChildrenDOM(propagateRelease);
    }

    /** {@inheritDoc} */
    @Override
    public void releaseDOM() {
        adapted.releaseDOM();
    }

    /** {@inheritDoc} */
    @Override
    public void releaseParentDOM(boolean propagateRelease) {
        adapted.releaseParentDOM(propagateRelease);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public XMLObject resolveID(@Nonnull String id) {
        return adapted.resolveID(id);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public XMLObject resolveIDFromRoot(@Nonnull String id) {
        return adapted.resolveIDFromRoot(id);
    }

    /** {@inheritDoc} */
    @Override
    public void setDOM(@Nullable Element dom) {
        adapted.setDOM(dom);
    }

    /** {@inheritDoc} */
    @Override
    public void setNoNamespaceSchemaLocation(@Nullable String location) {
        adapted.setNoNamespaceSchemaLocation(location);
    }

    /** {@inheritDoc} */
    @Override
    public void setParent(@Nullable XMLObject parent) {
        adapted.setParent(parent);
    }

    /** {@inheritDoc} */
    @Override
    public void setSchemaLocation(@Nullable String location) {
        adapted.setSchemaLocation(location);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public Boolean isNil() {
        return adapted.isNil();
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public XSBooleanValue isNilXSBoolean() {
        return adapted.isNilXSBoolean();
    }

    /** {@inheritDoc} */
    @Override
    public void setNil(@Nullable Boolean newNil) {
        adapted.setNil(newNil);
    }

    /** {@inheritDoc} */
    @Override
    public void setNil(@Nullable XSBooleanValue newNil) {
        adapted.setNil(newNil);
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public LockableClassToInstanceMultiMap<Object> getObjectMetadata() {
        return adapted.getObjectMetadata();
    }

}
