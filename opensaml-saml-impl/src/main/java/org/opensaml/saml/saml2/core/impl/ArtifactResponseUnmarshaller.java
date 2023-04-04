/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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

package org.opensaml.saml.saml2.core.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.core.ArtifactResponse;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.xmlsec.signature.Signature;

/**
 * A thread-safe Unmarshaller for {@link ArtifactResponse}.
 */
public class ArtifactResponseUnmarshaller extends StatusResponseTypeUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final ArtifactResponse artifactResponse = (ArtifactResponse) parentObject;

        if (childObject instanceof Issuer) {
            artifactResponse.setIssuer((Issuer) childObject);
        } else if (childObject instanceof Signature) {
            artifactResponse.setSignature((Signature) childObject);
        } else if (childObject instanceof Extensions) {
            artifactResponse.setExtensions((Extensions) childObject);
        } else if (childObject instanceof Status) {
            artifactResponse.setStatus((Status) childObject);
        } else {
            artifactResponse.setMessage((SAMLObject) childObject);
        }
    }
}