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

package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.NewEncryptedID;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.NewEncryptedIDImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class NewEncryptedIDTest extends XMLObjectProviderBaseTestCase {

    /** Count of EncryptedKey subelements. */
    private int encryptedKeyCount = 3;

    /** Constructor. */
    public NewEncryptedIDTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/NewEncryptedID.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/NewEncryptedIDChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        NewEncryptedID encElement = (NewEncryptedID) unmarshallElement(singleElementFile);

        assert encElement!=null;
        Assert.assertNull(encElement.getEncryptedData(), "EncryptedData child element");
        Assert.assertEquals(encElement.getEncryptedKeys().size(), 0, "# of EncryptedKey children");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        NewEncryptedID encElement = (NewEncryptedID) unmarshallElement(childElementsFile);
        
        assert encElement!=null;
        Assert.assertNotNull(encElement.getEncryptedData(), "EncryptedData child element");
        Assert.assertEquals(encElement.getEncryptedKeys().size(), encryptedKeyCount, "# of EncryptedKey children");

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        NewEncryptedID encElement = (NewEncryptedID) buildXMLObject(NewEncryptedID.DEFAULT_ELEMENT_NAME);

        assertXMLEquals(expectedDOM, encElement);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        NewEncryptedID encElement = (NewEncryptedID) buildXMLObject(NewEncryptedID.DEFAULT_ELEMENT_NAME);
        
        encElement.setEncryptedData((EncryptedData) buildXMLObject(EncryptedData.DEFAULT_ELEMENT_NAME));
        for (int i=0; i < encryptedKeyCount; i++) {
            encElement.getEncryptedKeys().add((EncryptedKey) buildXMLObject(EncryptedKey.DEFAULT_ELEMENT_NAME));
        }
        
        assertXMLEquals(expectedChildElementsDOM, encElement);
    }
}