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

package org.opensaml.saml.ext.saml1md.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.ext.saml1md.SourceID;

/**
 * Tests {@link SourceIDImpl}
 */
public class SourceIDTest extends XMLObjectProviderBaseTestCase {

    /** Expected source ID value */
    private String expectedValue;

    /** Constructor */
    public SourceIDTest() {
        singleElementFile = "/org/opensaml/saml/ext/saml1md/impl/SourceID.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedValue = "9392kjc98";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final SAMLObjectBuilder<SourceID> builder = (SAMLObjectBuilder<SourceID>)
                builderFactory.<SourceID>ensureBuilder(SourceID.DEFAULT_ELEMENT_NAME);

        final SourceID sourceID = builder.buildObject();
        sourceID.setValue(expectedValue);

        assertXMLEquals(expectedDOM, sourceID);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final SourceID sourceID = (SourceID) unmarshallElement(singleElementFile);

        assert sourceID != null;
        Assert.assertEquals(sourceID.getValue(), expectedValue);
    }
}