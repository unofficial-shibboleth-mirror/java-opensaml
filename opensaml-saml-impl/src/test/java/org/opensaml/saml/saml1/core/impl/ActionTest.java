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
import org.opensaml.saml.saml1.core.Action;

/**
 * Test for {@link org.opensaml.saml.saml1.core.impl.ActionImpl}
 */
@SuppressWarnings({"null", "javadoc"})
public class ActionTest extends XMLObjectProviderBaseTestCase {

    private final String expectedContents;
    private final String expectedNamespace;
    private final QName qname;

    /**
     * Constructor
     */
    public ActionTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleAction.xml";
        singleElementOptionalAttributesFile  = "/org/opensaml/saml/saml1/impl/singleActionAttributes.xml";    
        expectedNamespace = "namespace";
        expectedContents = "Action Contents";
        qname = new QName(SAMLConstants.SAML1_NS, Action.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }
    

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {
        final Action action = (Action) unmarshallElement(singleElementFile);
        assert action!=null;
        Assert.assertNull(action.getNamespace(), "namespace attribute present");
        Assert.assertNull(action.getValue(), "Contents present");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final Action action = (Action) unmarshallElement(singleElementOptionalAttributesFile);
        assert action!=null;
        Assert.assertEquals(action.getNamespace(), expectedNamespace, "namespace attribute ");
        Assert.assertEquals(action.getValue(), expectedContents, "Contents ");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        Action action =(Action) buildXMLObject(qname);
        action.setNamespace(expectedNamespace);
        action.setValue(expectedContents);
        assertXMLEquals(expectedOptionalAttributesDOM, action);
    }
}
