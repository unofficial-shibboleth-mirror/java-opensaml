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

package org.opensaml.xmlsec.encryption;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * XMLObject representing XML Encryption, version 20021210, EncryptionProperties element.
 */
public interface EncryptionProperties extends XMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "EncryptionProperties";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(EncryptionConstants.XMLENC_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, EncryptionConstants.XMLENC_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "EncryptionPropertiesType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(EncryptionConstants.XMLENC_NS, TYPE_LOCAL_NAME,
            EncryptionConstants.XMLENC_PREFIX);

    /** ID attribute name. */
    @Nonnull @NotEmpty static final String ID_ATTRIB_NAME = "Id";

    /**
     * Get the ID attribute that uniquely identifies this element.
     * 
     * @return ID attribute
     */
    @Nullable String getID();

    /**
     * Set the ID attribute that uniquely identifies this element.
     * 
     * @param newID the new ID attribute value
     */
    void setID(@Nullable final String newID);

    /**
     * Get the list of EncryptionProperty child elements.
     * 
     * @return the List of EncryptionProperty child elements
     */
    @Nonnull @Live List<EncryptionProperty> getEncryptionProperties();

}
