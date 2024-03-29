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

/**
 * 
 */
package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.Terminate;

/**
 *
 */
@SuppressWarnings({"null", "javadoc"})
public class TerminateTest extends XMLObjectProviderBaseTestCase {

    /**
     * Constructor
     *
     */
    public TerminateTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/Terminate.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Terminate term = (Terminate) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(term, "Terminate");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        Terminate term = (Terminate) buildXMLObject(Terminate.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, term);
    }
}