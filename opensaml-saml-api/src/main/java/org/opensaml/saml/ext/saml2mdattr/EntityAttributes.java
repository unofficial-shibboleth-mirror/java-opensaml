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

package org.opensaml.saml.ext.saml2mdattr;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/** SAML V2.0 Metadata Extension for Entity Attributes EntityAttributes SAML object. */
public interface EntityAttributes extends SAMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "EntityAttributes";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
        new QName(SAMLConstants.SAML20MDATTR_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MDATTR_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "EntityAttributesType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
        new QName(SAMLConstants.SAML20MDATTR_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20MDATTR_PREFIX);

    /**
     * Gets mutable list of the child objects of this extension.
     * 
     * @return mutable list of children
     * 
     * @since 4.0.0
     */
    @Nonnull @Live List<SAMLObject> getEntityAttributesChildren();
    
    /**
     * Gets the attributes about the entity.
     * 
     * <p>This list is modifiable "to a point" but not all mutation operations are supported.</p>
     * 
     * @return attributes about the entity
     */
    @Nonnull @Live List<Attribute> getAttributes();
    
    /**
     * Gets the assertions about the entity.
     * 
     * <p>This list is modifiable "to a point" but not all mutation operations are supported.</p>
     * 
     * @return assertions about the entity
     */
    @Nonnull @Live List<Assertion> getAssertions();
}