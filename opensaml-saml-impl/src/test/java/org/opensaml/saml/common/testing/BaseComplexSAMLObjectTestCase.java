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

package org.opensaml.saml.common.testing;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * Base test case for OpenSAML tests that work with {@link org.opensaml.saml.common.SAMLObject}s which represent full,
 * complex, typical "real world" examples of SAML documents.
 */
public abstract class BaseComplexSAMLObjectTestCase extends XMLObjectBaseTestCase {

    /** Location of file containing a single element with NO optional attributes. */
    protected String elementFile;

    /** The expected result of a marshalled single element with no optional attributes. */
    protected Document expectedDOM;

    @BeforeClass
    protected void initSAMLObjectTestSupport() throws Exception {
        if (elementFile != null) {
            expectedDOM = parserPool.parse(BaseComplexSAMLObjectTestCase.class
                    .getResourceAsStream(elementFile));
        }
    }

    /**
     * Tests unmarshalling a document.
     */
    @Test
    public abstract void testUnmarshall();

    /**
     * Tests marshalling the contents of a complex element to a DOM document.
     */
    @Test
    public abstract void testMarshall();

}