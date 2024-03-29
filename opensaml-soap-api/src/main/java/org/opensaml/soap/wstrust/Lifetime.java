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

package org.opensaml.soap.wstrust;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.soap.wssecurity.Created;
import org.opensaml.soap.wssecurity.Expires;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * The wst:Lifetime element.
 * 
 */
public interface Lifetime extends WSTrustObject {

    /** Element local name. */
    @Nonnull @NotEmpty public static final String ELEMENT_LOCAL_NAME = "Lifetime";

    /** Default element name. */
    @Nonnull public static final QName ELEMENT_NAME =
        new QName(WSTrustConstants.WST_NS, ELEMENT_LOCAL_NAME, WSTrustConstants.WST_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "LifetimeType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSTrustConstants.WST_NS, TYPE_LOCAL_NAME, WSTrustConstants.WST_PREFIX);

    /**
     * Returns the wsu:Created child element.
     * 
     * @return the {@link Created} child element or <code>null</code>.
     */
    @Nullable public Created getCreated();

    /**
     * Sets the wsu:Created child element.
     * 
     * @param created the {@link Created} child element to set.
     */
    public void setCreated(@Nullable final Created created);

    /**
     * Returns the wsu:Expires child element.
     * 
     * @return the {@link Expires} child element or <code>null</code>.
     */
    @Nullable public Expires getExpires();

    /**
     * Sets the wsu:Expires child element.
     * 
     * @param expires the {@link Expires} child element.
     */
    public void setExpires(@Nullable final Expires expires);

}
