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

package org.opensaml.xmlsec.encryption.support.tests;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.xmlsec.encryption.CipherData;
import org.opensaml.xmlsec.encryption.CipherReference;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.support.DefaultPreDecryptionValidator;
import org.opensaml.xmlsec.encryption.support.PreDecryptionValidationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings({"javadoc", "null"})
public class DefaultPreDecryptionValidatorTest extends XMLObjectBaseTestCase {
    
    private DefaultPreDecryptionValidator validator;
    
    private EncryptedData encryptedData;
    private EncryptedKey encryptedKey;
    
    @BeforeMethod
    public void setup() throws Exception {
        validator = new DefaultPreDecryptionValidator();
        
        encryptedData = (EncryptedData) XMLObjectSupport.buildXMLObject(EncryptedData.DEFAULT_ELEMENT_NAME);
        encryptedData.setCipherData((CipherData) XMLObjectSupport.buildXMLObject(CipherData.DEFAULT_ELEMENT_NAME));

        encryptedKey = (EncryptedKey) XMLObjectSupport.buildXMLObject(EncryptedKey.DEFAULT_ELEMENT_NAME);
        encryptedKey.setCipherData((CipherData) XMLObjectSupport.buildXMLObject(CipherData.DEFAULT_ELEMENT_NAME));
    }
    
    @Test
    public void goodEncryptedData() throws Exception {
       validator.validate(encryptedData); 
    }

    @Test
    public void goodEncryptedKey() throws Exception {
       validator.validate(encryptedKey); 
    }

    @Test(expectedExceptions=PreDecryptionValidationException.class)
    public void noCipherData() throws Exception {
        encryptedData.setCipherData(null); 
        validator.validate(encryptedData); 
    }

    @Test(expectedExceptions=PreDecryptionValidationException.class)
    public void encryptedDataWithCipherReference() throws Exception {
        final CipherData cdata = encryptedData.getCipherData();
        assert cdata != null;
        cdata.setCipherReference((CipherReference) XMLObjectSupport.buildXMLObject(CipherReference.DEFAULT_ELEMENT_NAME));
        validator.validate(encryptedData); 
    }

    @Test(expectedExceptions=PreDecryptionValidationException.class)
    public void encryptedKeyWithCipherReference() throws Exception {
        final CipherData cdata = encryptedKey.getCipherData();
        assert cdata != null;
        cdata.setCipherReference((CipherReference) XMLObjectSupport.buildXMLObject(CipherReference.DEFAULT_ELEMENT_NAME));
        validator.validate(encryptedKey); 
    }

}