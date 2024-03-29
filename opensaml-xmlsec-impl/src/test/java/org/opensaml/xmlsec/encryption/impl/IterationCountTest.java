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


import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.encryption.IterationCount;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class IterationCountTest extends XMLObjectProviderBaseTestCase {
    
    private Integer expectedIntegerContent;

    /**
     * Constructor
     *
     */
    public IterationCountTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/IterationCount.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedIntegerContent = 2020;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final IterationCount ic = (IterationCount) unmarshallElement(singleElementFile);
        
        assert ic != null;
        Assert.assertEquals(expectedIntegerContent, ic.getValue(), "IterationCount value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final IterationCount ic = (IterationCount) buildXMLObject(IterationCount.DEFAULT_ELEMENT_NAME);
        ic.setValue(expectedIntegerContent);
        
        assertXMLEquals(expectedDOM, ic);
    }

}
