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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.ElementExtensibleXMLObject;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * XMLObject representing XML Encryption, version 20021210, EncryptionMethod element.
 */
public interface EncryptionMethod extends ElementExtensibleXMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "EncryptionMethod";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(EncryptionConstants.XMLENC_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, EncryptionConstants.XMLENC_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "EncryptionMethodType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(EncryptionConstants.XMLENC_NS, TYPE_LOCAL_NAME,
            EncryptionConstants.XMLENC_PREFIX);

    /** Algorithm attribute name. */
    @Nonnull @NotEmpty static final String ALGORITHM_ATTRIB_NAME = "Algorithm";

    /**
     * Gets the algorithm URI attribute used in this EncryptionMethod.
     * 
     * @return the Algorithm attribute URI attribute string
     */
    @Nullable String getAlgorithm();

    /**
     * Sets the algorithm URI attribute used in this EncryptionMethod.
     * 
     * @param newAlgorithm the new Algorithm URI attribute string
     */
    void setAlgorithm(@Nullable final String newAlgorithm);

    /**
     * Gets the KeySize child element.
     * 
     * @return the KeySize child element
     */
    @Nullable KeySize getKeySize();

    /**
     * Sets the KeySize child element.
     * 
     * @param newKeySize the new KeySize child element
     */
    void setKeySize(@Nullable final KeySize newKeySize);

    /**
     * Gets the OAEPparams child element.
     * 
     * @return the OAEPparams child element
     */
    @Nullable OAEPparams getOAEPparams();

    /**
     * Sets the OAEPparams child element.
     * 
     * @param newOAEPparams the new OAEPparams child element
     */
    void setOAEPparams(@Nullable final OAEPparams newOAEPparams);

}
