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
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.AssertionArtifact;

/**
 * Test for {@link org.opensaml.saml.saml1.core.AssertionArtifact}
 */
@SuppressWarnings({"null", "javadoc"})
public class AssertionArtifactTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    private final String expectedAssertionArtifact;  
    
    /**
     * Constructor
     */
    public AssertionArtifactTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleAssertionArtifact.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleAssertionArtifactAttribute.xml";
        expectedAssertionArtifact = "Test Text";
        qname = new QName(SAMLConstants.SAML10P_NS, AssertionArtifact.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final AssertionArtifact artifact = (AssertionArtifact) unmarshallElement(singleElementFile);
        assert artifact!=null;
        Assert.assertNull(artifact.getValue(), "AssertionArtifact contents present");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final AssertionArtifact artifact = (AssertionArtifact) unmarshallElement(singleElementOptionalAttributesFile);
        assert artifact!=null;        
        Assert.assertEquals(artifact.getValue(), expectedAssertionArtifact, "AssertionArtifact contents present");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
       assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AssertionArtifact artifact = (AssertionArtifact) buildXMLObject(qname);
        artifact.setValue(expectedAssertionArtifact);
        assertXMLEquals(expectedOptionalAttributesDOM, artifact);
    }
}
