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

package org.opensaml.saml.metadata.resolver.filter.impl;

import java.security.cert.X509Certificate;

import net.shibboleth.shared.annotation.constraint.NonnullBeforeTest;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resolver.CriteriaSet;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilterContext;
import org.opensaml.saml.metadata.resolver.filter.data.impl.MetadataSource;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509Support;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.SignatureValidationParametersCriterion;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * Unit tests for {@link SignatureValidationFilter}.
 */
@SuppressWarnings("javadoc")
public class SignatureValidationFilterExplicitKeyTest extends XMLObjectBaseTestCase {
    
    private final String switchMDFileValid = "/org/opensaml/saml/saml2/metadata/provider/metadata.aaitest_signed.xml";
    private final String switchMDFileInvalid = "/org/opensaml/saml/saml2/metadata/provider/metadata.aaitest_signed.invalid.xml";
    
    private Document switchMDDocumentValid;
    private Document switchMDDocumentInvalid;
    
    private SignatureTrustEngine switchSigTrustEngine;
    
    private String switchMDCertBase64 = 
        "MIICrzCCAhgCAQAwDQYJKoZIhvcNAQEEBQAwgZ8xCzAJBgNVBAYTAkNIMUAwPgYDVQQKEzdTV0lU" +
        "Q0ggLSBUZWxlaW5mb3JtYXRpa2RpZW5zdGUgZnVlciBMZWhyZSB1bmQgRm9yc2NodW5nMQwwCgYD" +
        "VQQLEwNBQUkxIjAgBgNVBAMTGVNXSVRDSGFhaSBNZXRhZGF0YSBTaWduZXIxHDAaBgkqhkiG9w0B" +
        "CQEWDWFhaUBzd2l0Y2guY2gwHhcNMDUwODAzMTEyMjUxWhcNMTUwODAxMTEyMjUxWjCBnzELMAkG" +
        "A1UEBhMCQ0gxQDA+BgNVBAoTN1NXSVRDSCAtIFRlbGVpbmZvcm1hdGlrZGllbnN0ZSBmdWVyIExl" +
        "aHJlIHVuZCBGb3JzY2h1bmcxDDAKBgNVBAsTA0FBSTEiMCAGA1UEAxMZU1dJVENIYWFpIE1ldGFk" +
        "YXRhIFNpZ25lcjEcMBoGCSqGSIb3DQEJARYNYWFpQHN3aXRjaC5jaDCBnzANBgkqhkiG9w0BAQEF" +
        "AAOBjQAwgYkCgYEAsmyBYNZ8mKYutdyQShzuOgnVxDP1UBZE+57S2ORZg1qi4JExOJEPnviHuh6H" +
        "EajljhAMGHxr656paDpfXkmGq/Ybk3xmXy2FTnFGpjFpZUV6dY/oJ82rve27C/NVcwZw2nYRl5C5" +
        "aCCgx/QlWsBTw+9972141+wBDH7dXlJ+UGkCAwEAATANBgkqhkiG9w0BAQQFAAOBgQCcLuNwTINk" +
        "fhBlVCIuTixR1R6mYu/+4KUJWtHlRCOUZhSLFept8HxEvfwnuX9xm+Q6Ju/sOgmI1INuSstUGWwV" +
        "y0AbpCphUDDmIh9A85ye8DrVaBHQrj5b/JEjCvkY0zhLJzgDzZ6btT40TuCnk2GpdAClu5SyCTiy" +
        "56+zDYqPqg==";
    
    private final String openIDFileValid = "/org/opensaml/saml/saml2/metadata/provider/openid-metadata.xml";
    private final String openIDFileInvalid = "/org/opensaml/saml/saml2/metadata/provider/openid-metadata-invalid.xml";
    
    private String openIDCertBase64 = 
        "MIICfTCCAeagAwIBAgIGAReueFpXMA0GCSqGSIb3DQEBBQUAMIGBMQswCQYDVQQGEwJVUzELMAkG" +
        "A1UECBMCQ0ExFDASBgNVBAcTC1NpbWkgVmFsbGV5MR4wHAYDVQQKExVSYXBhdHRvbmkgQ29ycG9y" +
        "YXRpb24xFDASBgNVBAsTC1NTTyBTdXBwb3J0MRkwFwYDVQQDExBtbHNzdGdzd21pY2hpZ2FuMB4X" +
        "DTA4MDEyNTAxMDMxOFoXDTA5MDEyNDAxMDMxOFowgYExCzAJBgNVBAYTAlVTMQswCQYDVQQIEwJD" +
        "QTEUMBIGA1UEBxMLU2ltaSBWYWxsZXkxHjAcBgNVBAoTFVJhcGF0dG9uaSBDb3Jwb3JhdGlvbjEU" +
        "MBIGA1UECxMLU1NPIFN1cHBvcnQxGTAXBgNVBAMTEG1sc3N0Z3N3bWljaGlnYW4wgZ8wDQYJKoZI" +
        "hvcNAQEBBQADgY0AMIGJAoGBAIOnt2MOfIYvvyhiKBS2yb5IXFx+SFEa/TLSUPkE9gZJCIe22GGf" +
        "iwzsC8ubpifebZUru1fespnaCE8rc7MtWXERW7x6Dp8wg/91NOgUB00eEUlA72DhDjelsYTJa+Az" +
        "ztBsWh6J3HFKNdNaSVTS+CqbmgdTlDW+BExbtHUfSP0RAgMBAAEwDQYJKoZIhvcNAQEFBQADgYEA" +
        "YT8js8O7gbLq4X/yuGCiuKHofQHFAE6pAWaxdTD+Bd2pu48GKICYAhFwHTqrG3bOqObfsILz4Pca" +
        "vCfzIS7/dk9oPnjeH7GqbxUZMsms4qDZzdNkNDUDWj82lJzIMfZyUKbn2waTsgg3mKja0dGw2UBy" +
        "urPV4NvVcNaIQZJunHI=";
    
    @NonnullBeforeTest private KeyInfoCredentialResolver kiResolver;
    
    @NonnullBeforeTest private MetadataFilterContext filterContext;

    @BeforeClass
    public void buildKeyInfoCredentialResolver() {
        kiResolver = DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver();
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        switchMDDocumentValid = parserPool.parse(SignatureValidationFilterExplicitKeyTest.class.getResourceAsStream(switchMDFileValid));
        switchMDDocumentInvalid = parserPool.parse(SignatureValidationFilterExplicitKeyTest.class.getResourceAsStream(switchMDFileInvalid));
        
        final X509Certificate switchCert = X509Support.decodeCertificate(switchMDCertBase64);
        final X509Credential switchCred = CredentialSupport.getSimpleCredential(switchCert, null);
        final StaticCredentialResolver switchCredResolver = new StaticCredentialResolver(switchCred);
        switchSigTrustEngine = new ExplicitKeySignatureTrustEngine(switchCredResolver, kiResolver);

        filterContext = new MetadataFilterContext();
    }

    @Test
    public void testValidSWITCHStandalone() throws Exception {
        final XMLObject xmlObject = unmarshallerFactory.ensureUnmarshaller(switchMDDocumentValid
                .getDocumentElement()).unmarshall(switchMDDocumentValid.getDocumentElement());
        
        final SignatureValidationFilter filter = new SignatureValidationFilter(switchSigTrustEngine);
        filter.initialize();
        try {
            filter.filter(xmlObject, filterContext);
        } catch (final FilterException e) {
            Assert.fail("Filter failed validation, should have succeeded: " + e.getMessage());
        }
    }
    
    @Test(expectedExceptions=FilterException.class)
    public void testSWITCHStandaloneBlacklistedSignatureAlgorithm() throws Exception {
        final XMLObject xmlObject = unmarshallerFactory.ensureUnmarshaller(switchMDDocumentValid
                .getDocumentElement()).unmarshall(switchMDDocumentValid.getDocumentElement());
        
        final SignatureValidationFilter filter = new SignatureValidationFilter(switchSigTrustEngine);
        
        final SignatureValidationParameters sigParams = new SignatureValidationParameters();
        sigParams.setExcludedAlgorithms(CollectionSupport.singleton(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1));
        final CriteriaSet defaultCriteriaSet = new CriteriaSet(new SignatureValidationParametersCriterion(sigParams));
        filter.setDefaultCriteria(defaultCriteriaSet);
        filter.initialize();
        
        filter.filter(xmlObject, filterContext);
    }
    
    @Test
    public void testInvalidSWITCHStandalone() throws Exception {
        final XMLObject xmlObject = unmarshallerFactory.ensureUnmarshaller(switchMDDocumentInvalid
                .getDocumentElement()).unmarshall(switchMDDocumentInvalid.getDocumentElement());
        
        final SignatureValidationFilter filter = new SignatureValidationFilter(switchSigTrustEngine);
        filter.initialize();
        try {
            filter.filter(xmlObject, filterContext);
            Assert.fail("Filter passed validation, should have failed");
        } catch (final FilterException e) {
            // do nothing, should fail
        }
    }

    @Test
    public void testInvalidSWITCHStandaloneWithRootSkip() throws Exception {
        // Goal here is to test the root signature skip (indicated by filter context data) by using a known invalid root signature.
        final XMLObject xmlObject = unmarshallerFactory.ensureUnmarshaller(switchMDDocumentInvalid
                .getDocumentElement()).unmarshall(switchMDDocumentInvalid.getDocumentElement());

        final MetadataSource metadataSource = new MetadataSource();
        metadataSource.setTrusted(true);
        filterContext.add(metadataSource);

        final SignatureValidationFilter filter = new SignatureValidationFilter(switchSigTrustEngine);
        filter.initialize();
        try {
            filter.filter(xmlObject, filterContext);
        } catch (final FilterException e) {
            Assert.fail("Filter failed validation, should have passed b/c we implicitly said to skip root signature");
        }
    }
    
    @Test
    public void testEntityDescriptor() throws Exception {
        final X509Certificate cert = X509Support.decodeCertificate(openIDCertBase64);
        final X509Credential cred = CredentialSupport.getSimpleCredential(cert, null);
        final StaticCredentialResolver credResolver = new StaticCredentialResolver(cred);
        final SignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
        
        final Document mdDoc = parserPool.parse(SignatureValidationFilterExplicitKeyTest.class.getResourceAsStream(openIDFileValid));
        final XMLObject xmlObject = 
            unmarshallerFactory.ensureUnmarshaller(mdDoc.getDocumentElement()).unmarshall(mdDoc.getDocumentElement());
        Assert.assertTrue(xmlObject instanceof EntityDescriptor);
        final EntityDescriptor ed = (EntityDescriptor) xmlObject;
        Assert.assertTrue(ed.isSigned());
        Assert.assertNotNull(ed.getSignature(), "Signature was null");
        
        final SignatureValidationFilter filter = new SignatureValidationFilter(trustEngine);
        filter.initialize();
        try {
            filter.filter(ed, filterContext);
        } catch (final FilterException e) {
            Assert.fail("Filter failed validation, should have succeeded: " + e.getMessage());
        }
    }
    
    @Test
    public void testEntityDescriptorInvalid() throws Exception {
        final X509Certificate cert = X509Support.decodeCertificate(openIDCertBase64);
        final X509Credential cred = CredentialSupport.getSimpleCredential(cert, null);
        final StaticCredentialResolver credResolver = new StaticCredentialResolver(cred);
        final SignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
        
        final Document mdDoc = parserPool.parse(SignatureValidationFilterExplicitKeyTest.class.getResourceAsStream(openIDFileInvalid));
        final XMLObject xmlObject = 
            unmarshallerFactory.ensureUnmarshaller(mdDoc.getDocumentElement()).unmarshall(mdDoc.getDocumentElement());
        Assert.assertTrue(xmlObject instanceof EntityDescriptor);
        final EntityDescriptor ed = (EntityDescriptor) xmlObject;
        Assert.assertTrue(ed.isSigned());
        Assert.assertNotNull(ed.getSignature(), "Signature was null");
        
        final SignatureValidationFilter filter = new SignatureValidationFilter(trustEngine);
        filter.initialize();
        try {
            filter.filter(xmlObject, filterContext);
            Assert.fail("Filter passed validation, should have failed");
        } catch (final FilterException e) {
            // do nothing, should fail
        }
    }

    @Test
    public void testEntityDescriptorInvalidWithRootSkip() throws Exception {
        // Goal here is to test the root signature skip (indicated by filter context data) by using a known invalid root signature.
        final X509Certificate cert = X509Support.decodeCertificate(openIDCertBase64);
        final X509Credential cred = CredentialSupport.getSimpleCredential(cert, null);
        final StaticCredentialResolver credResolver = new StaticCredentialResolver(cred);
        final SignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);

        final Document mdDoc = parserPool.parse(SignatureValidationFilterExplicitKeyTest.class.getResourceAsStream(openIDFileInvalid));
        final XMLObject xmlObject =
            unmarshallerFactory.ensureUnmarshaller(mdDoc.getDocumentElement()).unmarshall(mdDoc.getDocumentElement());
        Assert.assertTrue(xmlObject instanceof EntityDescriptor);
        final EntityDescriptor ed = (EntityDescriptor) xmlObject;
        Assert.assertTrue(ed.isSigned());
        Assert.assertNotNull(ed.getSignature(), "Signature was null");

        final MetadataSource metadataSource = new MetadataSource();
        metadataSource.setTrusted(true);
        filterContext.add(metadataSource);

        final SignatureValidationFilter filter = new SignatureValidationFilter(trustEngine);
        filter.initialize();
        try {
            filter.filter(xmlObject, filterContext);
        } catch (final FilterException e) {
            Assert.fail("Filter failed validation, should have passed b/c we implicitly said to skip root signature");
        }
    }
    
    @Test
    public void testEntityDescriptorWithProvider() throws Exception {
        final X509Certificate cert = X509Support.decodeCertificate(openIDCertBase64);
        final X509Credential cred = CredentialSupport.getSimpleCredential(cert, null);
        final StaticCredentialResolver credResolver = new StaticCredentialResolver(cred);
        final SignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
        
        final Document mdDoc = parserPool.parse(SignatureValidationFilterExplicitKeyTest.class.getResourceAsStream(openIDFileValid));
        
        final DOMMetadataResolver mdProvider = new DOMMetadataResolver(mdDoc.getDocumentElement());
        mdProvider.setParserPool(parserPool);
        mdProvider.setId("test");
        mdProvider.setRequireValidMetadata(false);
        
        final SignatureValidationFilter filter = new SignatureValidationFilter(trustEngine);
        filter.initialize();
        mdProvider.setMetadataFilter(filter);
        
        try {
            mdProvider.initialize();
        } catch (final ComponentInitializationException e) {
            Assert.fail("Failed when initializing metadata provider");
        }
    }
    
    @Test
    public void testInvalidEntityDescriptorWithProvider() throws Exception {
        final X509Certificate cert = X509Support.decodeCertificate(openIDCertBase64);
        final X509Credential cred = CredentialSupport.getSimpleCredential(cert, null);
        final StaticCredentialResolver credResolver = new StaticCredentialResolver(cred);
        SignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
        
        final Document mdDoc = parserPool.parse(SignatureValidationFilterExplicitKeyTest.class.getResourceAsStream(openIDFileInvalid));
        
        final DOMMetadataResolver mdProvider = new DOMMetadataResolver(mdDoc.getDocumentElement());
        mdProvider.setParserPool(parserPool);
        mdProvider.setRequireValidMetadata(false);
        
        final SignatureValidationFilter filter = new SignatureValidationFilter(trustEngine);
        filter.initialize();
        mdProvider.setId("test");
        mdProvider.setMetadataFilter(filter);
        
        try {
            mdProvider.initialize();
            Assert.fail("Metadata signature was invalid, provider initialization should have failed");
        } catch (final ComponentInitializationException e) {
            // do nothing, failure expected
        }
    }

    @Test
    public void testIsSkipRootSignatureEval() throws ComponentInitializationException {
        final MetadataFilterContext context = new MetadataFilterContext();
        final SignatureValidationFilter filter = new SignatureValidationFilter(switchSigTrustEngine);
        final MetadataSource metadataSource = new MetadataSource();

        Assert.assertFalse(filter.isSkipRootSignature(context));

        context.add(metadataSource);
        Assert.assertFalse(filter.isSkipRootSignature(context));

        metadataSource.setTrusted(true);
        Assert.assertTrue(filter.isSkipRootSignature(context));

        filter.setAlwaysVerifyTrustedSource(true);
        Assert.assertFalse(filter.isSkipRootSignature(context));
    }

}