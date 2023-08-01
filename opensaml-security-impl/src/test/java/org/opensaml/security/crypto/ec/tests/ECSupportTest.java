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

package org.opensaml.security.crypto.ec.tests;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.util.Set;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.crypto.ec.ECSupport;
import org.opensaml.security.crypto.ec.NamedCurve;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class ECSupportTest extends BaseNamedCurveTest {
    
    @Test(dataProvider="namedCurves")
    public void generateCompatibleKeyPair(String namedCurve) throws Exception {
        final KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(JCAConstants.KEY_ALGO_EC);
        kpGenerator.initialize(new ECGenParameterSpec(namedCurve));
        final KeyPair origKeyPair = kpGenerator.generateKeyPair();
        Assert.assertNotNull(origKeyPair);
        Assert.assertTrue(ECPublicKey.class.isInstance(origKeyPair.getPublic()));
        final ECPublicKey origPublicKey = ECPublicKey.class.cast(origKeyPair.getPublic());
        assert origPublicKey != null;
        
        final KeyPair generatedKeyPair = ECSupport.generateCompatibleKeyPair(origPublicKey, null);
        
        Assert.assertNotNull(generatedKeyPair);
        Assert.assertTrue(ECPublicKey.class.isInstance(generatedKeyPair.getPublic()));
        Assert.assertTrue(ECPrivateKey.class.isInstance(generatedKeyPair.getPrivate()));
    }
    
    @Test(dataProvider="namedCurves")
    public void performKeyAgreement(String namedCurve) throws Exception {
        final KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(JCAConstants.KEY_ALGO_EC);
        kpGenerator.initialize(new ECGenParameterSpec(namedCurve));
        final KeyPair publicKeyPair = kpGenerator.generateKeyPair();
        final ECPublicKey publicKey = ECPublicKey.class.cast(publicKeyPair.getPublic());
        
        final KeyPair privateKeyPair = ECSupport.generateCompatibleKeyPair(publicKey, null);
        
        final ECPrivateKey privateKey = ECPrivateKey.class.cast(privateKeyPair.getPrivate());
        
        byte[] secret = ECSupport.performKeyAgreement(publicKey, privateKey, null);
        Assert.assertNotNull(secret);
    }
    
    @Test(dataProvider="namedCurves")
    public void convertParameterSpec(String namedCurve) throws Exception {
        final ECParameterSpec control = ECPublicKey.class.cast(
                KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, new ECGenParameterSpec(namedCurve), null).getPublic()).getParams();
        
        final ECParameterSpec target = ECSupport.convert(ECNamedCurveTable.getParameterSpec(namedCurve));
        assert target != null;
        
        Assert.assertNotSame(target, control);
        
        Assert.assertEquals(target.getCurve().getField().getFieldSize(), control.getCurve().getField().getFieldSize());
        Assert.assertEquals(target.getCurve(), control.getCurve());
        Assert.assertEquals(target.getGenerator(), control.getGenerator());
        Assert.assertEquals(target.getOrder(), control.getOrder());
        Assert.assertEquals(target.getCofactor(), control.getCofactor());
    }

    @Test(dataProvider="namedCurves")
    public void encodeAndDecodeECPoint(String namedCurve) throws Exception {
        final ECParameterSpec spec = ECPublicKey.class.cast(
                KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, new ECGenParameterSpec(namedCurve), null).getPublic()).getParams();
        
        //  Do this differently (and clearer) than in the actual code just so check the latter.
        int fieldSizeBits = spec.getCurve().getField().getFieldSize();
        int fieldSizeBytes = (fieldSizeBits % 8) == 0 ? (fieldSizeBits / 8) : (fieldSizeBits / 8) + 1;
        
        final byte[] encoded = ECSupport.encodeECPointUncompressed(spec.getGenerator(), spec.getCurve());
        Assert.assertNotNull(encoded);
        Assert.assertEquals(encoded.length, (fieldSizeBytes * 2) + 1);
        Assert.assertEquals(encoded[0], 0x04);
        Assert.assertEquals(new BigInteger(1, encoded, 1, fieldSizeBytes),
                spec.getGenerator().getAffineX());
        Assert.assertEquals(new BigInteger(1, encoded, fieldSizeBytes+1, fieldSizeBytes),
                spec.getGenerator().getAffineY());
        
        final ECPoint decoded = ECSupport.decodeECPoint(encoded, spec.getCurve());
        Assert.assertNotNull(decoded);
        Assert.assertEquals(decoded, spec.getGenerator());
    }
    
    @Test
    public void getCurvesFromBouncyCastle() {
        final Set<NamedCurve> curves = ECSupport.getCurvesFromBouncyCastle();
        Assert.assertNotNull(curves);
        Assert.assertFalse(curves.isEmpty());
    }
}
