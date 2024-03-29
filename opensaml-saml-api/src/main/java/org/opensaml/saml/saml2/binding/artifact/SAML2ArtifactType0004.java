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

package org.opensaml.saml.saml2.binding.artifact;

import java.util.Arrays;

import javax.annotation.Nonnull;

import org.opensaml.saml.common.binding.artifact.SAMLSourceIDArtifact;

/**
 * SAML 2 Type 0x004 Artifact. SAML 2, type 4, artifacts contains a 2 byte type code with a value of 4 follwed by a 2
 * byte endpoint index followed by a 20 byte source ID followed by a 20 byte message handle.
 */
public class SAML2ArtifactType0004 extends AbstractSAML2Artifact implements SAMLSourceIDArtifact {

    /** SAML 2 artifact type code (0x0004). */
    @Nonnull public static final byte[] TYPE_CODE = { 0, 4 };

    /** 20 byte artifact source ID. */
    @Nonnull private byte[] sourceID;

    /** 20 byte message handle. */
    @Nonnull private byte[] messageHandle;

    /** Constructor. */
    public SAML2ArtifactType0004() {
        this(TYPE_CODE, new byte[20], new byte[20]);
    }

    /**
     * Constructor.
     * 
     * @param endpointIndex 2 byte endpoint index of the artifact
     * @param source 20 byte source ID of the artifact
     * @param handle 20 byte message handle of the artifact
     * 
     * @throws IllegalArgumentException thrown if the endpoint index, source ID, or message handle arrays are not of the
     *             right size
     */
    public SAML2ArtifactType0004(@Nonnull final byte[] endpointIndex, @Nonnull final byte[] source,
            @Nonnull final byte[] handle) {
        super(TYPE_CODE, endpointIndex);

        if (source.length != 20) {
            throw new IllegalArgumentException("Artifact source ID must be 20 bytes long");
        }
        sourceID = source;

        if (handle.length != 20) {
            throw new IllegalArgumentException("Artifact message handle must be 20 bytes long");
        }
        messageHandle = handle;
    }

    /**
     * Constructs a SAML 2 artifact from its byte array representation.
     * 
     * @param artifact the byte array representing the artifact
     * 
     * @return the type 0x0004 artifact created from the byte array
     * 
     * @throws IllegalArgumentException thrown if the artifact is not the right type or length (44 bytes)
     */
    public static SAML2ArtifactType0004 parseArtifact(final byte[] artifact) {
        if (artifact.length != 44) {
            throw new IllegalArgumentException("Artifact length must be 44 bytes it was " + artifact.length + "bytes");
        }

        final byte[] typeCode = { artifact[0], artifact[1] };
        if (!Arrays.equals(typeCode, TYPE_CODE)) {
            throw new IllegalArgumentException("Illegal artifact type code");
        }

        final byte[] endpointIndex = { artifact[2], artifact[3] };

        final byte[] sourceID = new byte[20];
        System.arraycopy(artifact, 4, sourceID, 0, 20);

        final byte[] messageHandle = new byte[20];
        System.arraycopy(artifact, 24, messageHandle, 0, 20);

        return new SAML2ArtifactType0004(endpointIndex, sourceID, messageHandle);
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
    public void setSourceID(final byte[] newSourceID) {
        if (newSourceID.length != 20) {
            throw new IllegalArgumentException("Artifact source ID must be 20 bytes long");
        }
        sourceID = newSourceID;
    }

    /**
     * Gets the 20 byte message handle of the artifact.
     * 
     * @return 20 byte message handle of the artifact
     */
    @Nonnull public byte[] getMessageHandle() {
        return messageHandle;
    }

    /**
     * Sets the 20 byte message handle of the artifact.
     * 
     * @param handle 20 byte message handle of the artifact
     */
    public void setMessageHandle(final byte[] handle) {
        if (handle.length != 20) {
            throw new IllegalArgumentException("Artifact message handle must be 20 bytes long");
        }
        messageHandle = handle;
    }

    /** {@inheritDoc} */
    @Nonnull public byte[] getRemainingArtifact() {
        final byte[] remainingArtifact = new byte[40];

        System.arraycopy(getSourceID(), 0, remainingArtifact, 0, 20);
        System.arraycopy(getMessageHandle(), 0, remainingArtifact, 20, 20);

        return remainingArtifact;
    }
}