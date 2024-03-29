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

package org.opensaml.xmlsec.signature.support.tests;

import java.security.KeyPair;

import javax.annotation.Nonnull;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObjectBuilder;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.DocumentInternalIDContentReference;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureValidator;
import org.opensaml.xmlsec.signature.support.Signer;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.xml.SerializeSupport;

/**
 * Test to verify {@link org.opensaml.xmlsec.signature.Signature} and its marshallers and unmarshallers.
 */
@SuppressWarnings({"javadoc", "null"})
public class EnvelopedSignatureRSASSA_PSSTest extends XMLObjectBaseTestCase {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EnvelopedSignatureRSASSA_PSSTest.class);

    /** Credential used to sign and verify. */
    private Credential goodCredential;
    
    /** Invalid credential for verification. */
    private Credential badCredential;

    /** Builder of mock XML objects. */
    private SignableSimpleXMLObjectBuilder sxoBuilder;

    /** Builder of Signature XML objects. */
    private XMLObjectBuilder<Signature> sigBuilder;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        KeyPair keyPair = KeySupport.generateKeyPair("RSA", 2048, null);
        goodCredential = CredentialSupport.getSimpleCredential(keyPair.getPublic(), keyPair.getPrivate());

        keyPair = KeySupport.generateKeyPair("RSA", 2048, null);
        badCredential = CredentialSupport.getSimpleCredential(keyPair.getPublic(), null);

        sxoBuilder = new SignableSimpleXMLObjectBuilder();
        sigBuilder = XMLObjectProviderRegistrySupport.getBuilderFactory().<Signature>ensureBuilder(
                Signature.DEFAULT_ELEMENT_NAME);
    }

    @DataProvider(name = "testAlgorithms")
    protected Object[][] testAlgorithms() {
        return new Object[][]  {
            // SHA-2 ones
            new Object[] { SignatureConstants.ALGO_ID_SIGNATURE_RSASSA_PSS_SHA1_MGF1 },
            new Object[] { SignatureConstants.ALGO_ID_SIGNATURE_RSASSA_PSS_SHA224_MGF1 },
            new Object[] { SignatureConstants.ALGO_ID_SIGNATURE_RSASSA_PSS_SHA256_MGF1 },
            new Object[] { SignatureConstants.ALGO_ID_SIGNATURE_RSASSA_PSS_SHA384_MGF1 },
            new Object[] { SignatureConstants.ALGO_ID_SIGNATURE_RSASSA_PSS_SHA512_MGF1 },

            // SHA-3 ones
            new Object[] { SignatureConstants.ALGO_ID_SIGNATURE_RSASSA_PSS_SHA3_224_MGF1 },
            new Object[] { SignatureConstants.ALGO_ID_SIGNATURE_RSASSA_PSS_SHA3_256_MGF1 },
            new Object[] { SignatureConstants.ALGO_ID_SIGNATURE_RSASSA_PSS_SHA3_384_MGF1 },
            new Object[] { SignatureConstants.ALGO_ID_SIGNATURE_RSASSA_PSS_SHA3_512_MGF1 },
        };
        
    }

    /**
     * Tests creating an enveloped signature and then verifying it.
     * 
     * @throws MarshallingException thrown if the XMLObject tree can not be marshalled
     * @throws SignatureException ...
     */
    @Test(dataProvider = "testAlgorithms")
    public void testSigningAndVerification(final String signatureAlgorithmURI) throws MarshallingException, SignatureException {
        SignableSimpleXMLObject sxo = getXMLObjectWithSignature(signatureAlgorithmURI);
        Signature signature = sxo.getSignature();

        Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().ensureMarshaller(sxo);
        Element signedElement = marshaller.marshall(sxo);

        assert signature != null;
        Signer.signObject(signature);

        if (log.isDebugEnabled()) {
            log.debug("Marshalled Signature: \n" + SerializeSupport.nodeToString(signedElement));
        }

        SignatureValidator.validate(signature, goodCredential);

        try {
            SignatureValidator.validate(signature, badCredential);
            Assert.fail("Validated signature with improper public key");
        } catch (SignatureException e) {
            // expected
        }
    }

    /**
     * Creates a XMLObject that has a Signature child element.
     * @param signatureAlgorithmURI 
     * 
     * @return a XMLObject that has a Signature child element
     */
    private SignableSimpleXMLObject getXMLObjectWithSignature(final String signatureAlgorithmURI) {
        SignableSimpleXMLObject sxo = sxoBuilder.buildObject();
        sxo.setId("FOO");

        Signature sig = sigBuilder.buildObject(Signature.DEFAULT_ELEMENT_NAME);
        sig.setSigningCredential(goodCredential);
        sig.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        sig.setSignatureAlgorithm(signatureAlgorithmURI);
        
        DocumentInternalIDContentReference contentReference = new DocumentInternalIDContentReference("FOO");
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_ENVELOPED_SIGNATURE);
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        contentReference.setDigestAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        sig.getContentReferences().add(contentReference);

        sxo.setSignature(sig);
        return sxo;
    }
}
