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
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.DoNotCacheCondition;

/**
 * Test class for {@link org.opensaml.saml.saml1.core.impl.DoNotCacheConditionImpl} objects
 */
@SuppressWarnings({"null", "javadoc"})
public class DoNotCacheConditionTest extends XMLObjectProviderBaseTestCase {

    /**
     * Constructor
     */
    public DoNotCacheConditionTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleDoNotCacheCondition.xml";
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {
        @SuppressWarnings("unused") DoNotCacheCondition doNotCacheCondition;
        
        doNotCacheCondition = (DoNotCacheCondition) unmarshallElement(singleElementFile);
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(new QName(SAMLConstants.SAML1_NS, DoNotCacheCondition.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX)));
    }

}
