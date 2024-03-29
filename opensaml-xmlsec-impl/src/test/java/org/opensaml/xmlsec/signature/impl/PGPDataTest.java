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

package org.opensaml.xmlsec.signature.impl;


import org.testng.annotations.Test;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.xmlsec.signature.PGPData;
import org.opensaml.xmlsec.signature.PGPKeyID;
import org.opensaml.xmlsec.signature.PGPKeyPacket;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class PGPDataTest extends XMLObjectProviderBaseTestCase {
    
    
    /**
     * Constructor
     *
     */
    public PGPDataTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/PGPData.xml";
        childElementsFile = "/org/opensaml/xmlsec/signature/impl/PGPDataChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final PGPData pgpData = (PGPData) unmarshallElement(singleElementFile);
        
        assert pgpData != null;
        Assert.assertNull(pgpData.getPGPKeyID(), "PGPKeyID child element");
        Assert.assertNull(pgpData.getPGPKeyPacket(), "PGPKeyPacket child element");
        Assert.assertEquals(pgpData.getUnknownXMLObjects().size(), 0, "# of other XMLObject children");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final PGPData pgpData = (PGPData) unmarshallElement(childElementsFile);
        
        assert pgpData != null;
        Assert.assertNotNull(pgpData.getPGPKeyID(), "PGPKeyID child element");
        Assert.assertNotNull(pgpData.getPGPKeyPacket(), "PGPKeyPacket child element");
        Assert.assertEquals(pgpData.getUnknownXMLObjects().size(), 2, "# of other XMLObject children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final PGPData pgpData = (PGPData) buildXMLObject(PGPData.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, pgpData);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final PGPData pgpData = (PGPData) buildXMLObject(PGPData.DEFAULT_ELEMENT_NAME);
        
        pgpData.setPGPKeyID((PGPKeyID) buildXMLObject(PGPKeyID.DEFAULT_ELEMENT_NAME));
        pgpData.setPGPKeyPacket((PGPKeyPacket) buildXMLObject(PGPKeyPacket.DEFAULT_ELEMENT_NAME));
        pgpData.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        pgpData.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, pgpData);
    }

}
