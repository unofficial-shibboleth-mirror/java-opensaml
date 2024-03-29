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
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.signature.X509SubjectName;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class X509SubjectNameTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedStringContent;

    /**
     * Constructor
     *
     */
    public X509SubjectNameTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/X509SubjectName.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedStringContent = "someX509SubjectName";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final X509SubjectName x509Element = (X509SubjectName) unmarshallElement(singleElementFile);
        
        assert x509Element != null;
        Assert.assertEquals(expectedStringContent, x509Element.getValue(), "X509SubjectName value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final X509SubjectName x509Element = (X509SubjectName) buildXMLObject(X509SubjectName.DEFAULT_ELEMENT_NAME);
        x509Element.setValue(expectedStringContent);
        
        assertXMLEquals(expectedDOM, x509Element);
    }

}
