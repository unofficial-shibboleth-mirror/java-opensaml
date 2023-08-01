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

package org.opensaml.saml.saml1.binding.artifact;

import java.util.Arrays;

import javax.annotation.Nonnull;

import org.opensaml.saml.common.binding.artifact.SAMLSourceIDArtifact;

/**
 * SAML 1.X Type 0x0001 Artifact. SAML 1, type 1, artifacts contains a 2 byte type code with a value of 1 followed by a
 * 20 byte source ID followed by a 20 byte assertion handle.
 */
public class SAML1ArtifactType0001 extends AbstractSAML1Artifact implements SAMLSourceIDArtifact {

    /** Artifact type code (0x0001). */
    @Nonnull public static final byte[] TYPE_CODE = { 0, 1 };

    /** 20 byte artifact source ID. */
    @Nonnull private byte[] sourceID;

    /** 20 byte assertion handle. */
    @Nonnull private byte[] assertionHandle;

    /** Constructor. */
    public SAML1ArtifactType0001() {
        this(new byte[20], new byte[20]);
    }

    /**
     * Constructor.
     * 
     * @param source 20 byte source ID of the artifact
     * @param handle 20 byte assertion handle of the artifact
     * 
     * @throws IllegalArgumentException thrown if the given source ID or message handle are not of the current length
     *             (20 bytes)
     */
    public SAML1ArtifactType0001(@Nonnull final byte[] source, @Nonnull final byte[] handle) {
        super(TYPE_CODE);

        if (source.length != 20) {
            throw new IllegalArgumentException("Artifact source ID must be 20 bytes long");
        }
        sourceID = source;

        if (handle.length != 20) {
            throw new IllegalArgumentException("Artifact assertion handle must be 20 bytes long");
        }
        assertionHandle = handle;
    }

    /**
     * Constructs a SAML 1 artifact from its byte array representation.
     * 
     * @param artifact the byte array representing the artifact
     * 
     * @return the artifact created from the byte array
     * 
     * @throws IllegalArgumentException thrown if the artifact is not the right type or lenght (42 bytes) or is not of
     *             the correct type (0x0001)
     */
    public static SAML1ArtifactType0001 parseArtifact(final byte[] artifact) {
        if (artifact.length != 42) {
            throw new IllegalArgumentException("Artifact length must be 42 bytes it was " + artifact.length + "bytes");
        }

        final byte[] typeCode = { artifact[0], artifact[1] };
        if (!Arrays.equals(typeCode, TYPE_CODE)) {
            throw new IllegalArgumentException("Artifact is not of appropriate type.");
        }

        final byte[] sourceID = new byte[20];
        System.arraycopy(artifact, 2, sourceID, 0, 20);

        final byte[] assertionHandle = new byte[20];
        System.arraycopy(artifact, 22, assertionHandle, 0, 20);

        return new SAML1ArtifactType0001(sourceID, assertionHandle);
    }

    /**
     * Gets the 20 byte source ID of the artifact.
     * 
     * @return the source ID of the artifact
     */
    @Nonnull public byte[] getSourceID() {
        return sourceID;
    }

    /**
     * Sets the 20 byte source ID of the artifact.
     * 
     * @param newSourceID 20 byte source ID of the artifact
     * 
     * @throws IllegalArgumentException thrown if the given source ID is not 20 bytes
     */
    protected void setSourceID(@Nonnull final byte[] newSourceID) {
        if (newSourceID.length != 20) {
            throw new IllegalArgumentException("Artifact source ID must be 20 bytes long");
        }
        sourceID = newSourceID;
    }

    /**
     * Gets the artifiact's 20 byte assertion handle.
     * 
     * @return artifiact's 20 byte assertion handle
     */
    @Nonnull public byte[] getAssertionHandle() {
        return assertionHandle;
    }

    /**
     * Sets the artifiact's 20 byte assertion handle.
     * 
     * @param handle artifiact's 20 byte assertion handle
     */
    public void setAssertionHandle(@Nonnull final byte[] handle) {
        if (handle.length != 20) {
            throw new IllegalArgumentException("Artifact assertion handle must be 20 bytes long");
        }
        assertionHandle = handle;
    }

    /** {@inheritDoc} */
    @Nonnull public byte[] getRemainingArtifact() {
        final byte[] remainingArtifact = new byte[40];

        System.arraycopy(getSourceID(), 0, remainingArtifact, 0, 20);
        System.arraycopy(getAssertionHandle(), 0, remainingArtifact, 20, 20);

        return remainingArtifact;
    }
}