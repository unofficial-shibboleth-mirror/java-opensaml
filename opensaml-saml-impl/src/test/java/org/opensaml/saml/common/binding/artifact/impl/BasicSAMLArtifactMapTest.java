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

package org.opensaml.saml.common.binding.artifact.impl;

import java.io.IOException;
import java.time.Duration;

import javax.annotation.Nonnull;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap.SAMLArtifactMapEntry;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

/**
 * Test the storage-backed SAML artifact map implementation.
 */
@SuppressWarnings({"null", "javadoc"})
public class BasicSAMLArtifactMapTest extends XMLObjectBaseTestCase {

    private BasicSAMLArtifactMap artifactMap;

    @Nonnull private final String artifact = "the-artifact";
    @Nonnull private final String issuerId = "urn:test:issuer";
    @Nonnull private final String rpId = "urn:test:rp";

    private SAMLObject samlObject;
    private Element origElement;

    @BeforeMethod
    protected void setUp() throws Exception {
        samlObject = (SAMLObject) unmarshallElement("/org/opensaml/saml/saml2/core/ResponseSuccessAuthnAttrib.xml");
        origElement = samlObject.getDOM();
        // Drop the DOM for a more realistic test, usually the artifact SAMLObject will be built, not unmarshalled
        samlObject.releaseChildrenDOM(true);
        samlObject.releaseDOM();

        artifactMap = new BasicSAMLArtifactMap();
        artifactMap.setArtifactLifetime(Duration.ofMinutes(5));
        artifactMap.initialize();
    }

    @Test
    public void testBasicPutGet() throws IOException, MarshallingException {
        Assert.assertFalse(artifactMap.contains(artifact));

        artifactMap.put(artifact, rpId, issuerId, samlObject);

        Assert.assertTrue(artifactMap.contains(artifact));

        final SAMLArtifactMapEntry entry = artifactMap.get(artifact);
        assert entry != null;

        Assert.assertEquals(entry.getArtifact(), artifact, "Invalid value for artifact");
        Assert.assertEquals(entry.getIssuerId(), issuerId, "Invalid value for issuer ID");
        Assert.assertEquals(entry.getRelyingPartyId(), rpId, "Invalid value for relying party ID");

        // Test SAMLObject reconstitution
        final SAMLObject retrievedObject = entry.getSamlMessage();
        final Element newElement = marshallerFactory.ensureMarshaller(retrievedObject).marshall(retrievedObject);
        
        final Diff diff = DiffBuilder.compare(origElement).withTest(newElement).checkForIdentical().ignoreWhitespace().build();
        Assert.assertFalse(diff.hasDifferences(), diff.toString());
    }

    @Test
    public void testRemove() throws IOException {
        Assert.assertFalse(artifactMap.contains(artifact));

        artifactMap.put(artifact, rpId, issuerId, samlObject);

        Assert.assertTrue(artifactMap.contains(artifact));

        artifactMap.remove(artifact);

        Assert.assertFalse(artifactMap.contains(artifact));

        SAMLArtifactMapEntry entry = artifactMap.get(artifact);
        Assert.assertNull(entry, "Entry was removed");
    }

    @Test
    public void testEntryExpiration() throws Exception {
        // lifetime of 1 second should do it
        artifactMap = new BasicSAMLArtifactMap();
        artifactMap.setArtifactLifetime(Duration.ofSeconds(1));
        artifactMap.initialize();

        Assert.assertFalse(artifactMap.contains(artifact));

        artifactMap.put(artifact, rpId, issuerId, samlObject);

        Assert.assertTrue(artifactMap.contains(artifact));

        // Sleep for 3 seconds, entry should expire
        Thread.sleep(3000);

        SAMLArtifactMapEntry entry = artifactMap.get(artifact);
        Assert.assertNull(entry, "Entry should have expired");
    }

}