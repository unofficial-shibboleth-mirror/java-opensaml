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
import org.opensaml.xmlsec.signature.SPKIData;
import org.opensaml.xmlsec.signature.SPKISexp;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class SPKIDataTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     *
     */
    public SPKIDataTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/SPKIData.xml";
        childElementsFile = "/org/opensaml/xmlsec/signature/impl/SPKIDataChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final SPKIData spkiData = (SPKIData) unmarshallElement(singleElementFile);
        
        assert spkiData != null;
        Assert.assertEquals(spkiData.getXMLObjects().size(), 0, "Total # of XMLObject child elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final SPKIData spkiData = (SPKIData) unmarshallElement(childElementsFile);
        
        assert spkiData != null;
        Assert.assertEquals(spkiData.getXMLObjects().size(), 4, "Total # of XMLObject child elements");
        Assert.assertEquals(spkiData.getSPKISexps().size(), 2, "# of SPKISexp child elements");
        Assert.assertEquals(spkiData.getXMLObjects(SimpleXMLObject.ELEMENT_NAME).size(), 2, "# of SimpleElement child elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final SPKIData spkiData = (SPKIData) buildXMLObject(SPKIData.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, spkiData);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final SPKIData spkiData = (SPKIData) buildXMLObject(SPKIData.DEFAULT_ELEMENT_NAME);
        
        spkiData.getXMLObjects().add(buildXMLObject(SPKISexp.DEFAULT_ELEMENT_NAME));
        spkiData.getXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        spkiData.getXMLObjects().add(buildXMLObject(SPKISexp.DEFAULT_ELEMENT_NAME));
        spkiData.getXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, spkiData);
    }

}
