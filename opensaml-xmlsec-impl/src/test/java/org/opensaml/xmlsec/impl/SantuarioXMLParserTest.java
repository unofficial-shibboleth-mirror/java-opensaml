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

package org.opensaml.xmlsec.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xml.security.parser.XMLParserException;
import org.opensaml.core.config.InitializationException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

public class SantuarioXMLParserTest {
    
    private SantuarioXMLParser parser;
    
    @BeforeClass
    public void setUpClass() throws InitializationException {
        parser = new SantuarioXMLParser();
    }
    
    @Test
    public void success() throws XMLParserException, IOException {
       try (final InputStream is = getClass().getResourceAsStream("/org/opensaml/xmlsec/signature/support/envelopedSignature.xml")) {
           final Document document = parser.parse(is, true);
           Assert.assertNotNull(document);
       }
    }

    @Test(expectedExceptions = XMLParserException.class)
    public void failOnInvalidDisallowDocTypeDeclarations() throws XMLParserException, IOException {
       try (final InputStream is = getClass().getResourceAsStream("/org/opensaml/xmlsec/signature/support/envelopedSignature.xml")) {
           parser.parse(is, false);
       }
    }

    @Test(expectedExceptions = XMLParserException.class)
    public void failOnInvalidXML() throws XMLParserException, IOException {
        try (final InputStream is = getClass().getResourceAsStream("/org/opensaml/xmlsec/impl/NotXML.txt")) {
           parser.parse(is, true);
       }
    }

}
