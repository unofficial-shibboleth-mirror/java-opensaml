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

package org.opensaml.soap.wstrust.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.soap.wstrust.Renewing;

/**
 * RenewingImpl.
 * 
 */
public class RenewingImpl extends AbstractWSTrustObject implements Renewing {
    
    /** The Allow attribute value. */
    @Nullable private XSBooleanValue allow;
    
    /** The OK attribute value. */
    @Nullable private XSBooleanValue ok;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public RenewingImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public Boolean isAllow() {
        if (allow != null) {
            return allow.getValue();
        }

        // Note: Default is true here, rather than the more common default of false.
        return Boolean.TRUE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue isAllowXSBoolean() {
        return allow;
    }

    /** {@inheritDoc} */
    public void setAllow(@Nullable final Boolean newAllow) {
        if (newAllow != null) {
            allow = prepareForAssignment(allow, new XSBooleanValue(newAllow, false));
        } else {
            allow = prepareForAssignment(allow, null);
        }        
    }

    /** {@inheritDoc} */
    public void setAllow(@Nullable final XSBooleanValue newAllow) {
        allow = prepareForAssignment(allow, newAllow);
    }

    /** {@inheritDoc} */
    @Nullable public Boolean isOK() {
        if (ok != null) {
            return ok.getValue();
        }

        // Default is false.
        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue isOKXSBoolean() {
        return ok;
    }

    /** {@inheritDoc} */
    public void setOK(@Nullable final Boolean newOK) {
        if (newOK != null) {
            ok = prepareForAssignment(ok, new XSBooleanValue(newOK, false));
        } else {
            ok = prepareForAssignment(ok, null);
        }        
    }

    /** {@inheritDoc} */
    public void setOK(@Nullable final XSBooleanValue newOK) {
        ok = prepareForAssignment(ok, newOK);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        return null;
    }

}