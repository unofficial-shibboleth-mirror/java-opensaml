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

package org.opensaml.xmlsec.config.tests;

import java.io.InputStream;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.xmlsec.config.DecryptionParserPool;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import net.shibboleth.shared.xml.ParserPool;
import net.shibboleth.shared.xml.XMLParserException;

public class DecryptionParserPoolTest extends OpenSAMLInitBaseTestCase {
    
    private ParserPool pp;
    
    @BeforeMethod
    public void setUp() throws Exception {
        DecryptionParserPool dpp = ConfigurationService.get(DecryptionParserPool.class);
        Assert.assertNotNull(dpp);
        assert dpp != null;
        pp = dpp.getParserPool();
    }
    
    @Test
    public void basicParseSuccess() throws Exception {
        Assert.assertNotNull(pp);
        assert pp != null;
        try (final InputStream is = this.getClass().getResourceAsStream("AuthnRequest.xml")) {
            assert is != null;
            final Document doc = pp.parse(is);
            Assert.assertNotNull(doc);
        }
    }
    
    @Test(expectedExceptions = XMLParserException.class)
    public void elementAttributeLimit_OSJ442() throws Exception {
        Assert.assertNotNull(pp);
        assert pp != null;
        try (final InputStream is = this.getClass().getResourceAsStream("AuthnRequest_elementAttributeLimit.xml")) {
            assert is != null;
            final Document doc = pp.parse(is);
        }
    }

    @Test(expectedExceptions = XMLParserException.class)
    public void maxElementDepth_OSJ442() throws Exception {
        Assert.assertNotNull(pp);
        assert pp != null;
        try (final InputStream is = this.getClass().getResourceAsStream("AuthnRequest_maxElementDepth.xml")) {
            assert is != null;
            final Document doc = pp.parse(is);
        }
    }

}
