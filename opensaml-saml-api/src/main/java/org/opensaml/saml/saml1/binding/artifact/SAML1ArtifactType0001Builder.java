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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.messaging.context.SAMLArtifactContext;
import org.opensaml.saml.saml1.core.Assertion;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Builder of SAML 1, type 0x001, artifacts.
 */
public class SAML1ArtifactType0001Builder implements SAML1ArtifactBuilder<SAML1ArtifactType0001> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAML1ArtifactType0001Builder.class);

    /** {@inheritDoc} */
    @Nullable public SAML1ArtifactType0001 buildArtifact(@Nonnull final byte[] artifact) {
        try {
            return SAML1ArtifactType0001.parseArtifact(artifact);
        } catch (final IllegalArgumentException e) {
            log.warn("Error parsing type 1 artifact", e);
            return null;
        }
    }

    /** {@inheritDoc} */
    @Nullable public SAML1ArtifactType0001 buildArtifact(@Nonnull final MessageContext requestContext,
            @Nonnull final Assertion assertion) {
        final String sourceId = getSourceEntityId(requestContext);
        if (sourceId == null) {
            return null;
        }
        
        try {
            final MessageDigest sha1Digester = MessageDigest.getInstance("SHA-1");
            final byte[] source = sha1Digester.digest(sourceId.getBytes());

            final SecureRandom handleGenerator = SecureRandom.getInstance("SHA1PRNG");
            final byte[] assertionHandle = new byte[20];
            handleGenerator.nextBytes(assertionHandle);

            assert source != null;
            return new SAML1ArtifactType0001(source, assertionHandle);
        } catch (final NoSuchAlgorithmException e) {
            log.warn("JVM does not support required cryptography algorithms", e);
            return null;
        }
    }
    
    /**
     * Get the artifact context.
     * 
     * @param requestContext the current message context
     * @return the SAML artifact context, or null
     */
    @Nullable protected SAMLArtifactContext getArtifactContext(@Nonnull final MessageContext requestContext) {
        return requestContext.getSubcontext(SAMLArtifactContext.class);
    }

    /**
     * Get the local entityId.
     * 
     * @param requestContext the message context
     * 
     * @return the local entityId
     */
    @Nullable protected String getSourceEntityId(@Nonnull final MessageContext requestContext) {
        final SAMLArtifactContext artifactContext = getArtifactContext(requestContext);
        if (artifactContext != null) {
            if (artifactContext.getSourceEntityId() != null) {
                return artifactContext.getSourceEntityId(); 
            }
            log.warn("SAMLArtifactContext did not contain a source entityID");
        } else {
            log.warn("Message context did not contain a SAMLArtifactContext");
        }
        return null;
    }
    
}