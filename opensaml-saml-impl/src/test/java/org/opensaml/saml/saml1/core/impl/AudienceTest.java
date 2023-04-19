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

/**
 * 
 */
package org.opensaml.saml.saml1.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Audience;

/**
 * Test for org.opensaml.saml.saml1.core.Audience Objects
 */
@SuppressWarnings({"null", "javadoc"})
public class AudienceTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    private final String expectedUri;
    
    /**
     * Constructor
     */
    public AudienceTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleAudience.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleAudienceAttributes.xml";
        expectedUri = "urn:oasis:names:tc:SAML:1.0:assertion";
        qname = new QName(SAMLConstants.SAML1_NS, Audience.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {
        final Audience audience = (Audience) unmarshallElement(singleElementFile);
        assert audience!= null;
        Assert.assertNull(audience.getURI(), "Uri is non-null");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final Audience audience = (Audience) unmarshallElement(singleElementOptionalAttributesFile);
        assert audience!= null;
        Assert.assertEquals(audience.getURI(), expectedUri, "Uri");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final Audience audience = (Audience) buildXMLObject(qname);
        assert audience!= null;        
        audience.setURI(expectedUri);
        assertXMLEquals(expectedOptionalAttributesDOM, audience);
        
    }

}
