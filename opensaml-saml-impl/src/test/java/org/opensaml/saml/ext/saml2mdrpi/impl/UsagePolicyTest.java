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

package org.opensaml.saml.ext.saml2mdrpi.impl;

import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2mdrpi.UsagePolicy;

@SuppressWarnings("javadoc")
public class UsagePolicyTest extends XMLObjectProviderBaseTestCase {

    /**
     * Constructor.
     */
    public UsagePolicyTest() {
        singleElementFile = "/org/opensaml/saml/ext/saml2mdrpi/UsagePolicy.xml";
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        final UsagePolicy policy = (UsagePolicy) unmarshallElement(singleElementFile);
        assert policy != null;
        Assert.assertEquals(policy.getXMLLang(), "en");
        Assert.assertEquals(policy.getURI(), "https://www.aai.dfn.de/en/join/");
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        final UsagePolicy policy = (UsagePolicy) buildXMLObject(UsagePolicy.DEFAULT_ELEMENT_NAME);

        policy.setURI("https://www.aai.dfn.de/en/join/");
        policy.setXMLLang("en");

        assertXMLEquals(expectedDOM, policy);
    }

}
