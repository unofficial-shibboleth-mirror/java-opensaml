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

package org.opensaml.xmlsec.encryption.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLRuntimeException;
import org.opensaml.xmlsec.encryption.ConcatKDFParams;
import org.opensaml.xmlsec.signature.DigestMethod;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link ConcatKDFParams}.
 */
public class ConcatKDFParamsImpl extends AbstractXMLObject implements ConcatKDFParams {
    
    /** DigestMethod. */
    @Nullable private DigestMethod digestMethod;
    
    /** AlgorithmID. */
    @Nullable private String algorithmID;
    
    /** PartyUInfo. */
    @Nullable private String partyUInfo;
   
    /** PartyVInfo. */
    @Nullable private String partyVInfo;

    /** SuppPubInfo. */
    @Nullable private String suppPubInfo;

    /** SuppPrivInfo. */
    @Nullable private String suppPrivInfo;

    /**
     * Constructor.
     *
     * @param namespaceURI namespace URI
     * @param elementLocalName element local name
     * @param namespacePrefix namespace prefix
     */
    protected ConcatKDFParamsImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public DigestMethod getDigestMethod() {
        return digestMethod;
    }

    /** {@inheritDoc} */
    public void setDigestMethod(@Nullable final DigestMethod newDigestMethod) {
        digestMethod = prepareForAssignment(digestMethod, newDigestMethod);
    }

    /** {@inheritDoc} */
    @Nullable public String getAlgorithmID() {
        return algorithmID;
    }

    /** {@inheritDoc} */
    public void setAlgorithmID(@Nullable final String newAlgorithmID) {
        algorithmID = prepareForAssignment(algorithmID, newAlgorithmID);
    }

    /** {@inheritDoc} */
    @Nullable public byte[] getAlgorithmIDBytes() {
        try {
            return algorithmID == null ? null : Hex.decodeHex(algorithmID);
        } catch (final DecoderException e) {
            throw new XMLRuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    public void setAlgorithmIDBytes(@Nullable final byte[] newAlgorithmID) {
        setAlgorithmID(newAlgorithmID == null ? null : Hex.encodeHexString(newAlgorithmID, false));
    }

    /** {@inheritDoc} */
    @Nullable public String getPartyUInfo() {
        return partyUInfo;
    }

    /** {@inheritDoc} */
    public void setPartyUInfo(@Nullable final String newPartyUInfo) {
        partyUInfo = prepareForAssignment(partyUInfo, newPartyUInfo);
    }

    /** {@inheritDoc} */
    @Nullable public byte[] getPartyUInfoBytes() {
        try {
            return partyUInfo == null ? null : Hex.decodeHex(partyUInfo);
        } catch (final DecoderException e) {
            throw new XMLRuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    public void setPartyUInfoBytes(@Nullable final byte[] newPartyUInfo) {
        setPartyUInfo(newPartyUInfo == null ? null : Hex.encodeHexString(newPartyUInfo, false));
    }

    /** {@inheritDoc} */
    @Nullable public String getPartyVInfo() {
        return partyVInfo;
    }

    /** {@inheritDoc} */
    public void setPartyVInfo(@Nullable final String newPartyVInfo) {
        partyVInfo = prepareForAssignment(partyVInfo, newPartyVInfo);
    }

    /** {@inheritDoc} */
    @Nullable public byte[] getPartyVInfoBytes() {
        try {
            return partyVInfo == null ? null : Hex.decodeHex(partyVInfo);
        } catch (final DecoderException e) {
            throw new XMLRuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    public void setPartyVInfoBytes(@Nullable final byte[] newPartyVInfo) {
        setPartyVInfo(newPartyVInfo == null ? null : Hex.encodeHexString(newPartyVInfo, false));
    }

    /** {@inheritDoc} */
    @Nullable public String getSuppPubInfo() {
        return suppPubInfo;
    }

    /** {@inheritDoc} */
    public void setSuppPubInfo(@Nullable final String newSuppPubInfo) {
        suppPubInfo = prepareForAssignment(suppPubInfo, newSuppPubInfo);
    }

    /** {@inheritDoc} */
    @Nullable public byte[] getSuppPubInfoBytes() {
        try {
            return suppPubInfo == null ? null : Hex.decodeHex(suppPubInfo);
        } catch (final DecoderException e) {
            throw new XMLRuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    public void setSuppPubInfoBytes(@Nullable final byte[] newSuppPubInfo) {
        setSuppPubInfo(newSuppPubInfo == null ? null : Hex.encodeHexString(newSuppPubInfo, false));
    }

    /** {@inheritDoc} */
    @Nullable public String getSuppPrivInfo() {
        return suppPrivInfo;
    }

    /** {@inheritDoc} */
    public void setSuppPrivInfo(@Nullable final String newSuppPrivInfo) {
        suppPrivInfo = prepareForAssignment(suppPrivInfo, newSuppPrivInfo);
    }

    /** {@inheritDoc} */
    @Nullable public byte[] getSuppPrivInfoBytes() {
        try {
            return suppPrivInfo == null ? null : Hex.decodeHex(suppPrivInfo);
        } catch (final DecoderException e) {
            throw new XMLRuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    public void setSuppPrivInfoBytes(@Nullable final byte[] newSuppPrivInfo) {
        setSuppPrivInfo(newSuppPrivInfo == null ? null : Hex.encodeHexString(newSuppPrivInfo, false));
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (digestMethod != null) {
            children.add(digestMethod); 
        }
        
        return CollectionSupport.copyToList(children);
    }

}