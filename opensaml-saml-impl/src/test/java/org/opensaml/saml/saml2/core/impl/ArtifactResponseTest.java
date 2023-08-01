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

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.ArtifactResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 *
 */
@SuppressWarnings({"null", "javadoc"})
public class ArtifactResponseTest extends StatusResponseTestBase {

    /**
     * Constructor
     *
     */
    public ArtifactResponseTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/ArtifactResponse.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/ArtifactResponseOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/ArtifactResponseChildElements.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, ArtifactResponse.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        ArtifactResponse ar = (ArtifactResponse) buildXMLObject(qname);
        
        super.populateRequiredAttributes(ar);

        assertXMLEquals(expectedDOM, ar);
    }
    
    /**
     * Test marshalling of attribute IDness.
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    @Test
    public void testAttributeIDnessMarshall() throws MarshallingException, XMLParserException {
        XMLObject target = buildXMLObject(ArtifactResponse.DEFAULT_ELEMENT_NAME);

        ((ArtifactResponse)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, ArtifactResponse.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        ArtifactResponse ar = (ArtifactResponse) buildXMLObject(qname);
        
        super.populateRequiredAttributes(ar);
        super.populateOptionalAttributes(ar);
        
        assertXMLEquals(expectedOptionalAttributesDOM, ar);
    }


    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, ArtifactResponse.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        ArtifactResponse ar = (ArtifactResponse) buildXMLObject(qname);
        
        super.populateChildElements(ar);
        
        assertXMLEquals(expectedChildElementsDOM, ar);
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        ArtifactResponse ar = (ArtifactResponse) unmarshallElement(singleElementFile);
        
        super.helperTestSingleElementUnmarshall(ar);
    }

  

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        ArtifactResponse ar = (ArtifactResponse) unmarshallElement(singleElementOptionalAttributesFile);
        
        super.helperTestSingleElementOptionalAttributesUnmarshall(ar);

    }



    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        ArtifactResponse ar = (ArtifactResponse) unmarshallElement(childElementsFile);
        
        super.helperTestChildElementsUnmarshall(ar);
    }
}