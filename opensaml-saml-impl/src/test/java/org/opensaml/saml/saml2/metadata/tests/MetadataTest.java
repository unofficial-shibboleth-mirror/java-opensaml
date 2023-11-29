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

package org.opensaml.saml.saml2.metadata.tests;

import java.io.IOException;
import java.io.InputStream;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Test cases that parses real, "in-the-wild", metadata files. Currently uses the InCommon and SWITCH federation
 * metadata files (current as of the time this test was written).
 */
@SuppressWarnings({"null"})
public class MetadataTest extends XMLObjectBaseTestCase {

    /**
     * Constructor
     */
    public MetadataTest() {

    }

    /**
     * Tests unmarshalling an InCommon metadata document.
     * 
     * @throws XMLParserException ...
     * @throws UnmarshallingException ...
     */
    @Test
    public void testInCommonUnmarshall() throws XMLParserException, UnmarshallingException {
        String inCommonMDFile = "/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml";

        try (final InputStream in = MetadataTest.class.getResourceAsStream(inCommonMDFile)) {
            Document inCommonMDDoc = parserPool.parse(in);
            Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().ensureUnmarshaller(
                    inCommonMDDoc.getDocumentElement());

            XMLObject inCommonMD = unmarshaller.unmarshall(inCommonMDDoc.getDocumentElement());

            Assert.assertEquals(inCommonMD.getElementQName().getLocalPart(), "EntitiesDescriptor",
                    "First element of InCommon data was not expected EntitiesDescriptor");
        } catch (XMLParserException xe) {
            Assert.fail("Unable to parse XML file: " + xe);
        } catch (UnmarshallingException ue) {
            Assert.fail("Unable to unmarshall XML: " + ue);
        } catch (IOException ue) {
            Assert.fail("Unable to close stream: " + ue);
        }
    }

    /**
     * Tests unmarshalling an SWITCH metadata document.
     */
    @Test
    public void testSWITCHUnmarshall() {
        String switchMDFile = "/org/opensaml/saml/saml2/metadata/metadata.switchaai_signed.xml";

        try (final InputStream in = MetadataTest.class.getResourceAsStream(switchMDFile)) {
            Document switchMDDoc = parserPool.parse(in);
            Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().ensureUnmarshaller(
                    switchMDDoc.getDocumentElement());

            XMLObject switchMD = unmarshaller.unmarshall(switchMDDoc.getDocumentElement());

            Assert.assertEquals(switchMD.getElementQName().getLocalPart(), "EntitiesDescriptor",
                    "First element of SWITCH data was not expected EntitiesDescriptor");
        } catch (XMLParserException xe) {
            Assert.fail("Unable to parse XML file: " + xe);
        } catch (UnmarshallingException ue) {
            Assert.fail("Unable to unmarshall XML: " + ue);
        } catch (final IOException ue) {
            Assert.fail("Unable to close stream: " + ue);
        }
    }
    
    /**
     * Tests unmarshalling a UKFed metadata document.
     */
    @Test
    public void testUKFedUnmarshall() {
        String ukMDFile = "/org/opensaml/saml/saml2/metadata/ukfederation-metadata.xml";

        try (final InputStream in = MetadataTest.class.getResourceAsStream(ukMDFile)) {
            Document ukFedDoc = parserPool.parse(in);            
            Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().ensureUnmarshaller(
                    ukFedDoc.getDocumentElement());
            XMLObject ukFedMD = unmarshaller.unmarshall(ukFedDoc.getDocumentElement());

            Assert.assertEquals(ukFedMD.getElementQName().getLocalPart(), "EntitiesDescriptor",
                    "First element of UK Federation data was not expected EntitiesDescriptor");
        } catch (XMLParserException xe) {
            Assert.fail("Unable to parse XML file: " + xe);
        } catch (UnmarshallingException ue) {
            Assert.fail("Unable to unmarshall XML: " + ue);
        } catch (final IOException ue) {
            Assert.fail("Unable to close stream: " + ue);
        }
    }

    /** Tests unmarshalling an ADFS metadata document with their "fun" extensions. See OSJ-392. */
    @Test
    public void testADFSUnmarshall() {
        String adfsMDFile = "/org/opensaml/saml/saml2/metadata/adfs-metadata.xml";

        try (final InputStream in = MetadataTest.class.getResourceAsStream(adfsMDFile)) {
            Document adfsDoc = parserPool.parse(in);            
            Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().ensureUnmarshaller(
                    adfsDoc.getDocumentElement());
            XMLObject ukFedMD = unmarshaller.unmarshall(adfsDoc.getDocumentElement());

            Assert.assertEquals(ukFedMD.getElementQName().getLocalPart(), "EntityDescriptor",
                    "First element of ADFS metadata was not expected EntityDescriptor");
        } catch (XMLParserException xe) {
            Assert.fail("Unable to parse XML file: " + xe);
        } catch (UnmarshallingException ue) {
            Assert.fail("Unable to unmarshall XML: " + ue);
        } catch (final IOException ue) {
            Assert.fail("Unable to close stream: " + ue);
        }
    }
    
}