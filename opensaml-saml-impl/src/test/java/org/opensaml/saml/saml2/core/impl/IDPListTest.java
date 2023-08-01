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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.GetComplete;
import org.opensaml.saml.saml2.core.IDPEntry;
import org.opensaml.saml.saml2.core.IDPList;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.IDPEntryImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class IDPListTest extends XMLObjectProviderBaseTestCase {
    
    /** The expected number of IDPEntry children */
    private int expectedNumIDPEntryChildren;

    /**
     * Constructor
     */
    public IDPListTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/IDPList.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/IDPListChildElements.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedNumIDPEntryChildren = 3;
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final IDPList list = (IDPList) buildXMLObject(IDPList.DEFAULT_ELEMENT_NAME);

        assertXMLEquals(expectedDOM, list);
    }
 

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final  IDPList list = (IDPList) buildXMLObject(IDPList.DEFAULT_ELEMENT_NAME);
        
        final QName idpEntryQName = new QName(SAMLConstants.SAML20P_NS, IDPEntry.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        for (int i=0; i<expectedNumIDPEntryChildren; i++){
            list.getIDPEntrys().add((IDPEntry) buildXMLObject(idpEntryQName));
        }
        
        final QName getCompelteQName = new QName(SAMLConstants.SAML20P_NS, GetComplete.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        list.setGetComplete((GetComplete) buildXMLObject(getCompelteQName));
        
        assertXMLEquals(expectedChildElementsDOM, list);
        
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final IDPList list = (IDPList) unmarshallElement(singleElementFile);
        assert list !=null;
        Assert.assertNotNull(list, "IDPList");
        Assert.assertEquals(list.getIDPEntrys().size(), 0, "IDPEntry count");
        Assert.assertNull(list.getGetComplete(), "GetComplete");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final IDPList list = (IDPList) unmarshallElement(childElementsFile);
        assert list !=null;        
        Assert.assertEquals(list.getIDPEntrys().size(), expectedNumIDPEntryChildren, "IDPEntry count");
        Assert.assertNotNull(list.getGetComplete(), "GetComplete");
    }
}