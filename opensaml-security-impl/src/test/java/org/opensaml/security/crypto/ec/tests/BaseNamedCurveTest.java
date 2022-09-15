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

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.testng.annotations.DataProvider;

/**
 * This mostly exists as a single place to define the common set of named curves we want to test in various tests.
 */
public class BaseNamedCurveTest extends OpenSAMLInitBaseTestCase {
    
    @DataProvider
    public Object[][] namedCurves() {
        return new Object[][] {
            new Object[] {"secp256r1"},
            new Object[] {"secp384r1"},
            new Object[] {"secp521r1"},
        };
    }

}
