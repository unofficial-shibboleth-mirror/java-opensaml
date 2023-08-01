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
package org.opensaml.saml.saml1.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml1.core.AttributeStatement;
import org.opensaml.saml.saml1.core.RespondWith;

/**
 * Test for org.opensaml.saml.saml1.core.RespondWith.
 */
@SuppressWarnings({"null", "javadoc"})
public class RespondWithTest extends XMLObjectProviderBaseTestCase {

    /** Expected QName element content. */
    private final QName expectedQName;

    /**
     * Constructor.
     *
     */
    public RespondWithTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml1/impl/RespondWith.xml";
        
        expectedQName = AttributeStatement.DEFAULT_ELEMENT_NAME;
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {
        final RespondWith respondWith = (RespondWith) unmarshallElement(singleElementFile);
        assert respondWith != null;
        
        Assert.assertEquals(respondWith.getValue(), expectedQName, "Unexpected QName content value");
    }


    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        RespondWith respondWith = (RespondWith) buildXMLObject(RespondWith.DEFAULT_ELEMENT_NAME);
        respondWith.setValue(expectedQName);
        
        assertXMLEquals(expectedDOM, respondWith);
    }
}
