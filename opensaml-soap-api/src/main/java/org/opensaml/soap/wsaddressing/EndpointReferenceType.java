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

package org.opensaml.soap.wsaddressing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.ElementExtensibleXMLObject;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface for element of type {@link EndpointReferenceType}.
 * 
 * @see "WS-Addressing 1.0 - Core"
 * 
 */
public interface EndpointReferenceType
        extends AttributeExtensibleXMLObject, ElementExtensibleXMLObject, WSAddressingObject {
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "EndpointReferenceType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSAddressingConstants.WSA_NS, TYPE_LOCAL_NAME, WSAddressingConstants.WSA_PREFIX);

    /**
     * Returns the &lt;wsa:Address&gt; child element.
     * 
     * @return the {@link Address} child element or <code>null</code>
     */
    @Nullable public Address getAddress();

    /**
     * Sets the &lt;wsa:Address&gt; child element.
     * 
     * @param address the {@link Address} child element to set.
     */
    public void setAddress(@Nullable final Address address);

    /**
     * Returns the optional &lt;wsa:Metadata&gt; child element.
     * 
     * @return the {@link Metadata} child element or <code>null</code>.
     */
    @Nullable public Metadata getMetadata();

    /**
     * Sets the &lt;wsa:Metadata&gt; child element.
     * 
     * @param metadata the {@link Metadata} child element to set.
     */
    public void setMetadata(@Nullable final Metadata metadata);

    /**
     * Returns the optional &lt;wsa:ReferenceParameters&gt; child element.
     * 
     * @return the {@link ReferenceParameters} child element or <code>null</code>.
     */
    @Nullable public ReferenceParameters getReferenceParameters();

    /**
     * Sets the &lt;wsa:ReferenceParameters&gt; child element.
     * 
     * @param referenceParameters the {@link ReferenceParameters} child element to set.
     */
    public void setReferenceParameters(@Nullable final ReferenceParameters referenceParameters);

}