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

package org.opensaml.saml.saml2.metadata.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.AffiliateMember;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.AffiliateMemberImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class AffiliateMemberTest extends XMLObjectProviderBaseTestCase {
    
    protected String expectedMemberID;
    
    public AffiliateMemberTest(){
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/AffiliateMember.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedMemberID = "urn:example.org:members:foo";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final AffiliateMember member = (AffiliateMember)unmarshallElement(singleElementFile);
        assert member!=null;
        final String memberID = member.getURI();
        Assert.assertEquals(memberID, expectedMemberID, "Affiliation memeber ID was " + memberID + ", expected " + expectedMemberID);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20MD_NS, AffiliateMember.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        
        AffiliateMember member = buildXMLObject(qname);
        
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 1026; i++) {
            stringBuilder.append(i);
        }

        try {
            member.setURI(stringBuilder.toString());
            Assert.fail();
        } catch (IllegalArgumentException e) {
            //OK
        }
        
        
        member = (new AffiliateMemberBuilder()).buildObject();
        
        member.setURI(expectedMemberID);
        
        assertXMLEquals(expectedDOM, member);
    }

}