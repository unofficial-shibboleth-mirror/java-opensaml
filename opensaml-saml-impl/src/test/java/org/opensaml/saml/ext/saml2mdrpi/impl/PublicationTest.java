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

package org.opensaml.saml.ext.saml2mdrpi.impl;

import java.time.Instant;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2mdrpi.Publication;
import org.testng.Assert;

@SuppressWarnings("javadoc")
public class PublicationTest extends XMLObjectProviderBaseTestCase {

    private static String expectedPublisher = "publisher";
    private static String expectedPublicationId = "Ident";
    private static Instant expectedCreationInstant = Instant.parse("2010-08-11T14:59:01.002Z");

    /**
     * Constructor.
     */
    public PublicationTest() {
        super();
        singleElementFile = "/org/opensaml/saml/ext/saml2mdrpi/Publication.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/ext/saml2mdrpi/PublicationOptionalAttr.xml";
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        final Publication info = (Publication) unmarshallElement(singleElementFile);
        assert info != null;
        Assert.assertEquals(info.getPublisher(), expectedPublisher);
    }

    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesUnmarshall() {
        final Publication info = (Publication) unmarshallElement(singleElementOptionalAttributesFile);
        assert info != null;
        Assert.assertEquals(info.getPublisher(), expectedPublisher);
        Assert.assertEquals(info.getPublicationId(), expectedPublicationId);
        Assert.assertEquals(info.getCreationInstant(), expectedCreationInstant);
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        final Publication info = (Publication) buildXMLObject(Publication.DEFAULT_ELEMENT_NAME);

        info.setPublisher(expectedPublisher);

        assertXMLEquals(expectedDOM, info);
    }

    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesMarshall() {
        final Publication info = (Publication) buildXMLObject(Publication.DEFAULT_ELEMENT_NAME);

        info.setPublisher(expectedPublisher);
        info.setCreationInstant(expectedCreationInstant);
        info.setPublicationId(expectedPublicationId);

        assertXMLEquals(expectedOptionalAttributesDOM, info);
    }
}
