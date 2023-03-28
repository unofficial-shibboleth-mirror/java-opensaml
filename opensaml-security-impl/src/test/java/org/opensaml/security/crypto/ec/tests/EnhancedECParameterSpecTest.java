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

package org.opensaml.security.crypto.ec.tests;

import java.security.KeyPairGenerator;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.ec.ECSupport;
import org.opensaml.security.crypto.ec.EnhancedECParameterSpec;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class EnhancedECParameterSpecTest extends BaseNamedCurveTest {
    
    @Test(dataProvider = "namedCurves")
    public void hashCodeAndEquals(String name) throws Exception {
        // Use BC curve table + conversion as the control, so that we know the one we're getting isn't
        // the same object that whatever provider is in effect produces when generating a KeyPair, etc.
        // SunEC seems to produce the same object instance for all key pairs of a given curve, presumably
        // they have an internal table of name -> ECParameterSpec.
        final ECParameterSpec controlInput = ECSupport.convert(ECNamedCurveTable.getParameterSpec(name));
        assert controlInput != null;
        
        EnhancedECParameterSpec control = new EnhancedECParameterSpec(controlInput);
        
        // As above, SunEC seems to generally always return the same ECParamterSpec for a given curve.
        // However, this may not *always* be true, or another provider may be in use.
        // So here just brute force and generate a bunch of key pairs and eval them against the control.
        for (int i=0; i<25; i++) {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(JCAConstants.KEY_ALGO_EC);
            kpg.initialize(new ECGenParameterSpec(name));
            EnhancedECParameterSpec target = new EnhancedECParameterSpec(
                    ECPublicKey.class.cast(kpg.generateKeyPair().getPublic()).getParams());
            Assert.assertNotSame(target.getOriginal(), control.getOriginal());
            // This can't be guaranteed really, although it's almost always probably true.
            //Assert.assertNotEquals(target.getOriginal().hashCode(), control.getOriginal().hashCode());
            // This should always be true because the default impl just does reference equality, per default in Object.
            Assert.assertNotEquals(target.getOriginal(), control.getOriginal());
            
            Assert.assertNotSame(target, control);
            Assert.assertEquals(target, control);
            Assert.assertEquals(target.hashCode(), target.hashCode());
        }
        
    }

}
