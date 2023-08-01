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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/** XMLObject representing XML Digital Signature, version 20020212, DSAKeyValue element. */
public interface DSAKeyValue extends XMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "DSAKeyValue";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SignatureConstants.XMLSIG_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "DSAKeyValueType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SignatureConstants.XMLSIG_NS, TYPE_LOCAL_NAME,
            SignatureConstants.XMLSIG_PREFIX);

    /**
     * Get the P child element.
     * 
     * @return the P child element
     */
    @Nullable P getP();

    /**
     * Set the P child element.
     * 
     * @param newP the new P child element
     */
    void setP(@Nullable final P newP);

    /**
     * Get the Q child element.
     * 
     * @return the Q child element
     */
    @Nullable Q getQ();

    /**
     * Set the Q child element.
     * 
     * @param newQ the new Q child element
     */
    void setQ(@Nullable final Q newQ);

    /**
     * Get the G child element.
     * 
     * @return the G child element
     */
    @Nullable G getG();

    /**
     * Set the G child element.
     * 
     * @param newG the new G child element
     */
    void setG(@Nullable final G newG);

    /**
     * Get the Y child element.
     * 
     * @return the Y child element
     */
    @Nullable Y getY();

    /**
     * Set the Y child element.
     * 
     * @param newY the new Y child element
     */
    void setY(@Nullable final Y newY);

    /**
     * Get the J child element.
     * 
     * @return the J child element
     */
    @Nullable J getJ();

    /**
     * Set the J child element.
     * 
     * @param newJ the new J child element
     */
    void setJ(@Nullable final J newJ);

    /**
     * Get the Seed element.
     * 
     * @return the Seed element
     */
    @Nullable Seed getSeed();

    /**
     * Set the Seed element.
     * 
     * @param newSeed new Seed element
     */
    void setSeed(@Nullable final Seed newSeed);

    /**
     * Get the PgenCounter element.
     * 
     * @return the PgenCounter element
     */
    @Nullable PgenCounter getPgenCounter();

    /**
     * Set the PgenCounter element.
     * 
     * @param newPgenCounter new PgenCounter element
     */
    void setPgenCounter(@Nullable final PgenCounter newPgenCounter);

}