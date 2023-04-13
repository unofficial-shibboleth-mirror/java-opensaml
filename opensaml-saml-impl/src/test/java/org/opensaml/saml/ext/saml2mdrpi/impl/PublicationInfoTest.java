/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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
import org.opensaml.saml.ext.saml2mdrpi.PublicationInfo;
import org.opensaml.saml.ext.saml2mdrpi.UsagePolicy;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class PublicationInfoTest extends XMLObjectProviderBaseTestCase {

    private static String expectedPublisher = "publisher";
    private static String expectedPublicationId = "Ident";
    private static Instant expectedCreationInstant = Instant.parse("2010-08-11T14:59:01.002Z");

    private static String[] langs = {"en", "fr",};
    private static String[] uris = {"https://www.aai.dfn.de/en/join/","https://www.example.fr/fr/",};

    public PublicationInfoTest() {
        super();
        singleElementFile = "/org/opensaml/saml/ext/saml2mdrpi/PublicationInfo.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/ext/saml2mdrpi/PublicationInfoOptionalAttr.xml";
        childElementsFile = "/org/opensaml/saml/ext/saml2mdrpi/PublicationInfoChildren.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final PublicationInfo info = (PublicationInfo) unmarshallElement(singleElementFile);
        assert info != null;
        Assert.assertEquals(info.getPublisher(), expectedPublisher);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final PublicationInfo info = (PublicationInfo) unmarshallElement(singleElementOptionalAttributesFile);
        assert info != null;
        Assert.assertEquals(info.getPublisher(), expectedPublisher);
        Assert.assertEquals(info.getPublicationId(), expectedPublicationId);
        Assert.assertEquals(info.getCreationInstant(), expectedCreationInstant);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final PublicationInfo info = (PublicationInfo) buildXMLObject(PublicationInfo.DEFAULT_ELEMENT_NAME);

        info.setPublisher(expectedPublisher);

        assertXMLEquals(expectedDOM, info);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final PublicationInfo info = (PublicationInfo) buildXMLObject(PublicationInfo.DEFAULT_ELEMENT_NAME);

        info.setPublisher(expectedPublisher);
        info.setCreationInstant(expectedCreationInstant);
        info.setPublicationId(expectedPublicationId);

        assertXMLEquals(expectedOptionalAttributesDOM, info);
    }
    
    @Test
    public void testChildElementsUnmarshall() {
        final PublicationInfo info = (PublicationInfo) unmarshallElement(childElementsFile);
        assert info != null;
        Assert.assertEquals(info.getPublisher(), expectedPublisher);
        UsagePolicy policy = info.getUsagePolicies().get(0);
        Assert.assertEquals(policy.getXMLLang(), langs[0]);
        Assert.assertEquals(policy.getURI(), uris[0]);
        policy = info.getUsagePolicies().get(1);
        Assert.assertEquals(policy.getXMLLang(), langs[1]);
        Assert.assertEquals(policy.getURI(), uris[1]);
    }

    @Test
    public void testChildElementsMarshall() {
        final PublicationInfo info = (PublicationInfo) buildXMLObject(PublicationInfo.DEFAULT_ELEMENT_NAME);
        info.setPublisher(expectedPublisher);

        for (int i = 0; i < 2; i++) {
            final UsagePolicy policy = (UsagePolicy) buildXMLObject(UsagePolicy.DEFAULT_ELEMENT_NAME);
            policy.setURI(uris[i]);
            policy.setXMLLang(langs[i]);
            info.getUsagePolicies().add(policy);
        }
        assertXMLEquals(expectedChildElementsDOM, info);
    }
}
