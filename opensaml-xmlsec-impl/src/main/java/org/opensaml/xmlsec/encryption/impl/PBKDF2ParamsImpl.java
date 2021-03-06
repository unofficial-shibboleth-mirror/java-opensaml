/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.IterationCount;
import org.opensaml.xmlsec.encryption.KeyLength;
import org.opensaml.xmlsec.encryption.PBKDF2Params;
import org.opensaml.xmlsec.encryption.PRF;
import org.opensaml.xmlsec.encryption.Salt;

/**
 * Concrete implementation of {@link org.opensaml.xmlsec.encryption.PBKDF2Params}.
 */
public class PBKDF2ParamsImpl extends AbstractXMLObject implements PBKDF2Params {
    
    /** Salt child element. */
    @Nullable private Salt salt;

    /** IterationCount child element. */
    @Nullable private IterationCount iterationCount;

    /** KeyLength child element. */
    @Nullable private KeyLength keyLength;

    /** PRF child element. */
    @Nullable private PRF prf;

    /**
     * Constructor.
     *
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected PBKDF2ParamsImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public Salt getSalt() {
        return salt;
    }

    /** {@inheritDoc} */
    public void setSalt(@Nullable final Salt newSalt) {
        salt = prepareForAssignment(salt, newSalt);
    }

    /** {@inheritDoc} */
    @Nullable public IterationCount getIterationCount() {
        return iterationCount;
    }

    /** {@inheritDoc} */
    public void setIterationCount(@Nullable final IterationCount count) {
        iterationCount = prepareForAssignment(iterationCount, count);
    }

    /** {@inheritDoc} */
    @Nullable public KeyLength getKeyLength() {
        return keyLength;
    }

    /** {@inheritDoc} */
    public void setKeyLength(@Nullable final KeyLength length) {
        keyLength = prepareForAssignment(keyLength, length);
    }

    /** {@inheritDoc} */
    @Nullable public PRF getPRF() {
        return prf;
    }

    /** {@inheritDoc} */
    public void setPRF(@Nullable final PRF newPRF) {
        prf = prepareForAssignment(prf, newPRF);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
       final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (salt != null) {
            children.add(salt);
        }
        if (iterationCount != null) {
            children.add(iterationCount);
        }
        if (keyLength != null) {
            children.add(keyLength);
        }
        if (prf != null) {
            children.add(prf);
        }
        
        if (children.size() == 0) {
            return null;
        }
        
        return Collections.unmodifiableList(children);
    }

}
