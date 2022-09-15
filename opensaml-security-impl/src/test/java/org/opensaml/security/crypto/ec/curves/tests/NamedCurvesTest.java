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

package org.opensaml.security.crypto.ec.curves.tests;

import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.util.Set;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.crypto.ec.ECSupport;
import org.opensaml.security.crypto.ec.EnhancedECParameterSpec;
import org.opensaml.security.crypto.ec.NamedCurve;
import org.opensaml.security.crypto.ec.NamedCurveRegistry;
import org.opensaml.security.crypto.ec.curves.AbstractNamedCurve;
import org.opensaml.security.crypto.ec.tests.BaseNamedCurveTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class NamedCurvesTest extends BaseNamedCurveTest {
    
    private Logger log = LoggerFactory.getLogger(NamedCurvesTest.class);
    
    @Test(dataProvider = "namedCurves")
    public void globalRegistryCurves(String namedCurve) throws Exception {
        NamedCurveRegistry registry = ECSupport.getGlobalNamedCurveRegistry();
        Assert.assertNotNull(registry);
        
        ECParameterSpec bcSpec = ECSupport.convert(ECNamedCurveTable.getParameterSpec(namedCurve));
        Assert.assertNotNull(bcSpec);
        
        Assert.assertNotNull(registry.getByName(namedCurve));
        Assert.assertEquals(registry.getByName(namedCurve).getName(), namedCurve);
        
        Assert.assertNotNull(registry.getByParameterSpec(bcSpec));
        // Use Enhanced- as a simple way to test equality via #equals(...).
        // Wrap both. It seems TestNG does expected.equals(actual), but that may not always be true.
        Assert.assertEquals(new EnhancedECParameterSpec(registry.getByParameterSpec(bcSpec).getParameterSpec()),
                new EnhancedECParameterSpec(bcSpec));
        
        NamedCurve curve = registry.getByName(namedCurve);
        if (AbstractNamedCurve.class.isInstance(curve)) {
            // Test the equality of the curve's spec #buildParameterSpec() against both BC and brute force from key pair generation
            ECParameterSpec curveSpec = AbstractNamedCurve.class.cast(curve).buildParameterSpec();
            Assert.assertNotNull(curveSpec);
            
            ECParameterSpec jcaSpec =  ECPublicKey.class.cast(
                    KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, new ECGenParameterSpec(namedCurve), null)
                    .getPublic()).getParams();
            Assert.assertNotNull(jcaSpec);
            
            // Wrap both sides, per above.
            Assert.assertEquals(new EnhancedECParameterSpec(curveSpec), new EnhancedECParameterSpec(jcaSpec));
            Assert.assertEquals(new EnhancedECParameterSpec(curveSpec), new EnhancedECParameterSpec(bcSpec));
        }
        
    }
    
    /**
     * Use Bouncy Castle's curve table to sanity check all registered curves that OID and name match,
     * i.e. resolve to equivalent instances of ECParameterSpec, and that curve matches the NamedCurve's params.
     * 
     *<p>
     * This test assumes that all curves we'd ever register and ship with the library will be supported
     * by Bouncy Castle.  If this ever becomes untrue, we can always disable the test, or remove the 
     * curves if there is a security or other reason to stop supporting it.
     *</p>
     *
     * @throws Exception
     */
    @Test
    public void sanityCheckOIDAndNameAndParams() throws Exception {
        NamedCurveRegistry registry = ECSupport.getGlobalNamedCurveRegistry();
        Assert.assertNotNull(registry);
        
        Set<NamedCurve> registeredCurves = registry.getRegisteredCurves();
        
        for (NamedCurve curve : registeredCurves) {
            log.debug("Testing OID and name for curve: impl {}", curve.getClass().getName());
            
            ECParameterSpec specByOID = ECSupport.convert(ECNamedCurveTable.getParameterSpec(curve.getObjectIdentifier()));
            Assert.assertNotNull(specByOID);
            ECParameterSpec specByName = ECSupport.convert(ECNamedCurveTable.getParameterSpec(mapBCCurveName(curve.getName())));
            Assert.assertNotNull(specByName);
            
            
            // Params by OID and name match each other.
            Assert.assertEquals(new EnhancedECParameterSpec(specByOID), new EnhancedECParameterSpec(specByName));
            // And that curve from BC actually matches the NamedCurve's ECParameterSpec
            Assert.assertEquals(new EnhancedECParameterSpec(curve.getParameterSpec()), new EnhancedECParameterSpec(specByOID));
        }
    }
    
    //
    // Helpers
    //

    /**
     * BC sometimes has slightly different curve simple names from SunEC, so translate here...
     * @param standardName
     * @return the corresponding BC curve name
     */
    private String mapBCCurveName(String standardName) {
        if (standardName.startsWith("X9.62 ")) {
            return standardName.substring(6);
        }
        return standardName;
    }

}
