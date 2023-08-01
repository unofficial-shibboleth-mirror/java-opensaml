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

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2mdrpi.Publication;
import org.opensaml.saml.ext.saml2mdrpi.PublicationPath;
import org.testng.Assert;


@SuppressWarnings("javadoc")
public class PublicationPathTest extends XMLObjectProviderBaseTestCase {

    private static String[] publishers = {"pub1", "pub2",};

    /**
     * Constructor.
     */
    public PublicationPathTest() {
        super();
        singleElementFile = "/org/opensaml/saml/ext/saml2mdrpi/PublicationPath.xml";
        childElementsFile = "/org/opensaml/saml/ext/saml2mdrpi/PublicationPathChildren.xml";
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        final PublicationPath pPath = (PublicationPath) unmarshallElement(singleElementFile);
        assert pPath != null;
        Assert.assertEquals(pPath.getPublications().size(), 0);
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        PublicationPath pPath = (PublicationPath) buildXMLObject(PublicationPath.DEFAULT_ELEMENT_NAME);

        assertXMLEquals(expectedDOM, pPath);
    }

    public void testChildElementsUnmarshall() {
        final PublicationPath pPath = (PublicationPath) unmarshallElement(childElementsFile);
        assert pPath != null;
        Assert.assertEquals(pPath.getPublications().size(), 2);
        Publication pub = pPath.getPublications().get(0);
        Assert.assertEquals(pub.getPublisher(), publishers[0]);
        pub = pPath.getPublications().get(1);
        Assert.assertEquals(pub.getPublisher(), publishers[1]);
    }

    public void testChildElementsMarshall() {
        final PublicationPath pPath = (PublicationPath) buildXMLObject(PublicationPath.DEFAULT_ELEMENT_NAME);

        for (int i = 0; i < 2; i++) {
            final Publication pub = (Publication) buildXMLObject(Publication.DEFAULT_ELEMENT_NAME);
            pub.setPublisher(publishers[i]);
            pPath.getPublications().add(pub);
        }
        assertXMLEquals(expectedChildElementsDOM, pPath);
    }
}
