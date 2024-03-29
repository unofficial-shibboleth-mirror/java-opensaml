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

package org.opensaml.xmlsec.signature;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/** XMLObject representing XML Digital Signature, version 20020212, KeyInfo element. */
public interface KeyInfo extends XMLObject {
    
    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "KeyInfo";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SignatureConstants.XMLSIG_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "KeyInfoType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SignatureConstants.XMLSIG_NS, TYPE_LOCAL_NAME,
            SignatureConstants.XMLSIG_PREFIX);

    /** Id attribute name. */
    @Nonnull @NotEmpty static final String ID_ATTRIB_NAME = "Id";

    /**
     * Get the Id attribute value.
     * 
     * @return the Id attribute value
     */
    @Nullable String getID();

    /**
     * Set the Id attribute value.
     * 
     * @param newID the new Id attribute value
     */
    void setID(@Nullable final String newID);

    /**
     * Get the list of all XMLObject children.
     * 
     * @return the list of XMLObject children
     */
    @Nonnull @Live List<XMLObject> getXMLObjects();

    /**
     * Get the list of XMLObject children whose type or element QName matches the specified QName.
     * 
     * @param typeOrName the QName of the desired elements
     * 
     * @return the matching list of XMLObject children
     */
    @Nonnull @Live List<XMLObject> getXMLObjects(@Nonnull final QName typeOrName);

    /**
     * Get the list of KeyName child elements.
     * 
     * @return the list of KeyName child elements
     */
    @Nonnull @Live List<KeyName> getKeyNames();

    /**
     * Get the list of KeyValue child elements.
     * 
     * @return the list of KeyValue child elements
     */
    @Nonnull @Live List<KeyValue> getKeyValues();

    /**
     * Get the list of DEREncodedKeyValue child elements.
     * 
     * @return the list of DEREncodedKeyValue child elements
     */
    @Nonnull @Live List<DEREncodedKeyValue> getDEREncodedKeyValues();
    
    /**
     * Get the list of RetrievalMethod child elements.
     * 
     * @return the list of RetrievalMethod child elements
     */
    @Nonnull @Live List<RetrievalMethod> getRetrievalMethods();

    /**
     * Get the list of KeyInfoReference child elements.
     * 
     * @return the list of KeyInfoReference child elements
     */
    @Nonnull @Live List<KeyInfoReference> getKeyInfoReferences();
    
    /**
     * Get the list of X509Data child elements.
     * 
     * @return the list of X509Data child elements
     */
    @Nonnull @Live List<X509Data> getX509Datas();

    /**
     * Get the list of PGPData child elements.
     * 
     * @return the list of PGPData child elements
     */
    @Nonnull @Live List<PGPData> getPGPDatas();

    /**
     * Get the list of SPKIData child elements.
     * 
     * @return the list of SPKIData child elements
     */
    @Nonnull @Live List<SPKIData> getSPKIDatas();

    /**
     * Get the list of MgmtData child elements.
     * 
     * @return the list of MgmtData child elements
     */
    @Nonnull @Live List<MgmtData> getMgmtDatas();

    /**
     * Get the list of AgreementMethod child elements.
     * 
     * Note: AgreementMethod is actually defined in the XML Encryption schema, and is not explicitly defined in the
     * KeyInfoType content model, but for convenience this named getter method is exposed.
     * 
     * @return the list of AgreementMethod child elements
     */
    @Nonnull @Live List<AgreementMethod> getAgreementMethods();

    /**
     * Get the list of EncryptedKey child elements
     * 
     * Note: EncryptedKey is actually defined in the XML Encryption schema, and is not explicitly defined in the
     * KeyInfoType content model, but for convenience this named getter method is exposed.
     * 
     * @return the list of EncryptedKey child elements
     */
    @Nonnull @Live List<EncryptedKey> getEncryptedKeys();

}