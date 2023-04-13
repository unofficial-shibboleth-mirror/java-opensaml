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
import org.opensaml.saml.ext.saml2mdrpi.RegistrationInfo;
import org.opensaml.saml.ext.saml2mdrpi.RegistrationPolicy;
import org.testng.Assert;


@SuppressWarnings("javadoc")
public class RegistrationInfoTest extends XMLObjectProviderBaseTestCase {

    private static String expectedAuthority = "https://www.aai.dfn.de";

    private static Instant expectedRegistrationInstant = Instant.parse("2010-08-11T14:59:01.002Z");

    private static String[] langs = {"en", "de",};
    private static String[] uris = {"grhttps://www.aai.dfn.de/en/join/","https://www.aai.dfn.de/teilnahme/",};

    /**
     * Constructor.
     */
    public RegistrationInfoTest() {
        super();
        singleElementFile = "/org/opensaml/saml/ext/saml2mdrpi/RegistrationInfo.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/ext/saml2mdrpi/RegistrationInfoOptionalAttr.xml";
        childElementsFile = "/org/opensaml/saml/ext/saml2mdrpi/RegistrationInfoChildren.xml";
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        final RegistrationInfo info = (RegistrationInfo) unmarshallElement(singleElementFile);
        assert info != null;
        Assert.assertEquals(info.getRegistrationAuthority(), expectedAuthority);
    }

    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesUnmarshall() {
        final RegistrationInfo info = (RegistrationInfo) unmarshallElement(singleElementOptionalAttributesFile);
        assert info != null;
        Assert.assertEquals(info.getRegistrationAuthority(), expectedAuthority);
        Assert.assertEquals(info.getRegistrationInstant(), expectedRegistrationInstant);
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        final RegistrationInfo info = (RegistrationInfo) buildXMLObject(RegistrationInfo.DEFAULT_ELEMENT_NAME);

        info.setRegistrationAuthority(expectedAuthority);

        assertXMLEquals(expectedDOM, info);
    }

    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesMarshall() {
        final RegistrationInfo info = (RegistrationInfo) buildXMLObject(RegistrationInfo.DEFAULT_ELEMENT_NAME);

        info.setRegistrationAuthority(expectedAuthority);
        info.setRegistrationInstant(expectedRegistrationInstant);

        assertXMLEquals(expectedOptionalAttributesDOM, info);
    }
    public void testChildElementsUnmarshall() {
        final RegistrationInfo info = (RegistrationInfo) unmarshallElement(childElementsFile);
        assert info != null;
        Assert.assertEquals(info.getRegistrationAuthority(), expectedAuthority);
        Assert.assertEquals(info.getRegistrationInstant(), expectedRegistrationInstant);
        RegistrationPolicy policy = info.getRegistrationPolicies().get(0);
        Assert.assertEquals(policy.getXMLLang(), langs[0]);
        Assert.assertEquals(policy.getURI(), uris[0]);
        policy = info.getRegistrationPolicies().get(1);
        Assert.assertEquals(policy.getXMLLang(), langs[1]);
        Assert.assertEquals(policy.getURI(), uris[1]);
    }

    public void testChildElementsMarshall() {
        final RegistrationInfo info = (RegistrationInfo) buildXMLObject(RegistrationInfo.DEFAULT_ELEMENT_NAME);
        info.setRegistrationAuthority(expectedAuthority);
        info.setRegistrationInstant(expectedRegistrationInstant);

        for (int i = 0; i < 2; i++) {
            final RegistrationPolicy policy = (RegistrationPolicy) buildXMLObject(RegistrationPolicy.DEFAULT_ELEMENT_NAME);
            policy.setURI(uris[i]);
            policy.setXMLLang(langs[i]);
            info.getRegistrationPolicies().add(policy);
        }
        assertXMLEquals(expectedChildElementsDOM, info);
    }
}
