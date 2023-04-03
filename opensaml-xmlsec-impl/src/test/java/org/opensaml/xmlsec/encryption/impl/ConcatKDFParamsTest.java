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

package org.opensaml.xmlsec.encryption.impl;


import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.XMLRuntimeException;
import org.opensaml.xmlsec.encryption.ConcatKDFParams;
import org.opensaml.xmlsec.signature.DigestMethod;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class ConcatKDFParamsTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedDigestMethod;
    private String expectedAlgorithmID;
    private String expectedPartyUInfo;
    private String expectedPartyVInfo;
    private String expectedSuppPubInfo;
    private String expectedSuppPrivInfo;
    
    /**
     * Constructor
     *
     */
    public ConcatKDFParamsTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/ConcatKDFParams.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/ConcatKDFParamsChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedDigestMethod = "urn:string:foo";
        expectedAlgorithmID = "00AA";
        expectedPartyUInfo = "00BB";
        expectedPartyVInfo = "00CC";
        expectedSuppPubInfo = "00DD";
        expectedSuppPrivInfo = "00EE";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final ConcatKDFParams params = (ConcatKDFParams) unmarshallElement(singleElementFile);
        
        assert params != null;
        Assert.assertEquals(params.getAlgorithmID(), expectedAlgorithmID);
        Assert.assertEquals(params.getPartyVInfo(), expectedPartyVInfo);
        Assert.assertEquals(params.getPartyUInfo(), expectedPartyUInfo);
        Assert.assertEquals(params.getSuppPubInfo(), expectedSuppPubInfo);
        Assert.assertEquals(params.getSuppPrivInfo(), expectedSuppPrivInfo);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final ConcatKDFParams params = (ConcatKDFParams) unmarshallElement(childElementsFile);
        
        assert params != null;
        Assert.assertEquals(params.getAlgorithmID(), expectedAlgorithmID);
        Assert.assertEquals(params.getPartyVInfo(), expectedPartyVInfo);
        Assert.assertEquals(params.getPartyUInfo(), expectedPartyUInfo);
        Assert.assertEquals(params.getSuppPubInfo(), expectedSuppPubInfo);
        Assert.assertEquals(params.getSuppPrivInfo(), expectedSuppPrivInfo);
        
        final DigestMethod method = params.getDigestMethod();
        assert method != null;
        Assert.assertEquals(method.getAlgorithm(), expectedDigestMethod);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final ConcatKDFParams params = (ConcatKDFParams) buildXMLObject(ConcatKDFParams.DEFAULT_ELEMENT_NAME);
        
        params.setAlgorithmID(expectedAlgorithmID);
        params.setPartyUInfo(expectedPartyUInfo);
        params.setPartyVInfo(expectedPartyVInfo);
        params.setSuppPubInfo(expectedSuppPubInfo);
        params.setSuppPrivInfo(expectedSuppPrivInfo);
        
        assertXMLEquals(expectedDOM, params);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final ConcatKDFParams params = (ConcatKDFParams) buildXMLObject(ConcatKDFParams.DEFAULT_ELEMENT_NAME);
        
        params.setAlgorithmID(expectedAlgorithmID);
        params.setPartyUInfo(expectedPartyUInfo);
        params.setPartyVInfo(expectedPartyVInfo);
        params.setSuppPubInfo(expectedSuppPubInfo);
        params.setSuppPrivInfo(expectedSuppPrivInfo);
        
        DigestMethod dm = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        dm.setAlgorithm(expectedDigestMethod);
        params.setDigestMethod(dm);
        
        assertXMLEquals(expectedChildElementsDOM, params);
    }
    
    @Test
    public void testStringBytesConversions() {
        ConcatKDFParams params = null;
        
        // AlgorithmID
        params = buildXMLObject(ConcatKDFParams.DEFAULT_ELEMENT_NAME);
        params.setAlgorithmID(expectedAlgorithmID);
        Assert.assertEquals(params.getAlgorithmIDBytes(), new byte[] {0x0, (byte) 0xAA});
        params.setAlgorithmID(null);
        Assert.assertNull(params.getAlgorithmIDBytes());
        params.setAlgorithmIDBytes(new byte[] {0x0, (byte) 0xAA});
        Assert.assertEquals(params.getAlgorithmID(), expectedAlgorithmID);
        params.setAlgorithmIDBytes(null);
        Assert.assertNull(params.getAlgorithmID());
        try {
            params.setAlgorithmID("FOO");
            params.getAlgorithmIDBytes();
            Assert.fail("Invalid hexBinary value, auto-decoding should have failed");
        } catch (XMLRuntimeException e) {
            // expected, do nothing
        }
        
        // PartyUInfo
        params = buildXMLObject(ConcatKDFParams.DEFAULT_ELEMENT_NAME);
        params.setPartyUInfo(expectedPartyUInfo);
        Assert.assertEquals(params.getPartyUInfoBytes(), new byte[] {0x0, (byte) 0xBB});
        params.setPartyUInfo(null);
        Assert.assertNull(params.getPartyUInfoBytes());
        params.setPartyUInfoBytes(new byte[] {0x0, (byte) 0xBB});
        Assert.assertEquals(params.getPartyUInfo(), expectedPartyUInfo);
        params.setPartyUInfoBytes(null);
        Assert.assertNull(params.getPartyUInfo());
        try {
            params.setPartyUInfo("FOO");
            params.getPartyUInfoBytes();
            Assert.fail("Invalid hexBinary value, auto-decoding should have failed");
        } catch (XMLRuntimeException e) {
            // expected, do nothing
        }
        
        // PartyVInfo
        params = buildXMLObject(ConcatKDFParams.DEFAULT_ELEMENT_NAME);
        params.setPartyVInfo(expectedPartyVInfo);
        Assert.assertEquals(params.getPartyVInfoBytes(), new byte[] {0x0, (byte) 0xCC});
        params.setPartyVInfo(null);
        Assert.assertNull(params.getPartyVInfoBytes());
        params.setPartyVInfoBytes(new byte[] {0x0, (byte) 0xCC});
        Assert.assertEquals(params.getPartyVInfo(), expectedPartyVInfo);
        params.setPartyVInfoBytes(null);
        Assert.assertNull(params.getPartyVInfo());
        try {
            params.setPartyVInfo("FOO");
            params.getPartyVInfoBytes();
            Assert.fail("Invalid hexBinary value, auto-decoding should have failed");
        } catch (XMLRuntimeException e) {
            // expected, do nothing
        }
        
        // SuppPubInfo
        params = buildXMLObject(ConcatKDFParams.DEFAULT_ELEMENT_NAME);
        params.setSuppPubInfo(expectedSuppPubInfo);
        Assert.assertEquals(params.getSuppPubInfoBytes(), new byte[] {0x0, (byte) 0xDD});
        params.setSuppPubInfo(null);
        Assert.assertNull(params.getSuppPubInfoBytes());
        params.setSuppPubInfoBytes(new byte[] {0x0, (byte) 0xDD});
        Assert.assertEquals(params.getSuppPubInfo(), expectedSuppPubInfo);
        params.setSuppPubInfoBytes(null);
        Assert.assertNull(params.getSuppPubInfo());
        try {
            params.setSuppPubInfo("FOO");
            params.getSuppPubInfoBytes();
            Assert.fail("Invalid hexBinary value, auto-decoding should have failed");
        } catch (XMLRuntimeException e) {
            // expected, do nothing
        }
        
        // SuppPrivInfo
        params = buildXMLObject(ConcatKDFParams.DEFAULT_ELEMENT_NAME);
        params.setSuppPrivInfo(expectedSuppPrivInfo);
        Assert.assertEquals(params.getSuppPrivInfoBytes(), new byte[] {0x0, (byte) 0xEE});
        params.setSuppPrivInfo(null);
        Assert.assertNull(params.getSuppPrivInfoBytes());
        params.setSuppPrivInfoBytes(new byte[] {0x0, (byte) 0xEE});
        Assert.assertEquals(params.getSuppPrivInfo(), expectedSuppPrivInfo);
        params.setSuppPrivInfoBytes(null);
        Assert.assertNull(params.getSuppPrivInfo());
        try {
            params.setSuppPrivInfo("FOO");
            params.getSuppPrivInfoBytes();
            Assert.fail("Invalid hexBinary value, auto-decoding should have failed");
        } catch (XMLRuntimeException e) {
            // expected, do nothing
        }
        
    }

}
