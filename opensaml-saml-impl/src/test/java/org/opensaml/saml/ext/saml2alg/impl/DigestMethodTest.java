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

package org.opensaml.saml.ext.saml2alg.impl;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Unit test for {@link DigestMethod}. */
public class DigestMethodTest extends XMLObjectProviderBaseTestCase {

    /** Constructor. */
    public DigestMethodTest() {
        singleElementFile = "/org/opensaml/saml/ext/saml2alg/impl/DigestMethod.xml";
        childElementsFile = "/org/opensaml/saml/ext/saml2alg/impl/DigestMethodChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final DigestMethod digestMethod = (DigestMethod) unmarshallElement(singleElementFile);
        assert digestMethod != null;
        Assert.assertEquals(digestMethod.getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final DigestMethod digestMethod = (DigestMethod) unmarshallElement(childElementsFile);
        assert digestMethod != null;
        Assert.assertEquals(digestMethod.getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertEquals(digestMethod.getUnknownXMLObjects().size(), 3);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final DigestMethod digestMethod = (DigestMethod) buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);

        assertXMLEquals(expectedDOM, digestMethod);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final DigestMethod digestMethod = (DigestMethod) buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        
        digestMethod.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        digestMethod.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        digestMethod.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, digestMethod);
    }
}