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

package org.opensaml.saml.ext.saml2delrestrict.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.ext.saml2delrestrict.Delegate;
import org.opensaml.saml.ext.saml2delrestrict.DelegationRestrictionType;

/**
 * Test case for creating, marshalling, and unmarshalling {@link Delegate}.
 */
public class DelegationRestrictionTypeTest extends XMLObjectProviderBaseTestCase {

    private int expectedDelegateChildren;
    

    /** Constructor */
    public DelegationRestrictionTypeTest() {
        singleElementFile = "/org/opensaml/saml/ext/saml2delrestrict/impl/DelegationRestrictionType.xml";
        childElementsFile = "/org/opensaml/saml/ext/saml2delrestrict/impl/DelegationRestrictionTypeChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedDelegateChildren = 3;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final DelegationRestrictionType drt = (DelegationRestrictionType) unmarshallElement(singleElementFile);

        Assert.assertNotNull(drt);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final DelegationRestrictionType drt = (DelegationRestrictionType) unmarshallElement(childElementsFile);
        assert drt != null;
        
        Assert.assertEquals(drt.getDelegates().size(), expectedDelegateChildren, "Incorrect # of Delegate Children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final SAMLObjectBuilder<DelegationRestrictionType> builder = (SAMLObjectBuilder<DelegationRestrictionType>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<DelegationRestrictionType>ensureBuilder(
                        DelegationRestrictionType.TYPE_NAME);
        
        final DelegationRestrictionType drt = builder.buildObject();

        assertXMLEquals(expectedDOM, drt);
    }


    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final SAMLObjectBuilder<DelegationRestrictionType> builder = (SAMLObjectBuilder<DelegationRestrictionType>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<DelegationRestrictionType>ensureBuilder(
                        DelegationRestrictionType.TYPE_NAME);
        
        final DelegationRestrictionType drt = builder.buildObject();
        
        drt.getDelegates().add((Delegate) buildXMLObject(Delegate.DEFAULT_ELEMENT_NAME));
        drt.getDelegates().add((Delegate) buildXMLObject(Delegate.DEFAULT_ELEMENT_NAME));
        drt.getDelegates().add((Delegate) buildXMLObject(Delegate.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, drt);
    }
}