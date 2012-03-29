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

package org.opensaml.saml.saml2.core.validator;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.validation.ValidationException;
import org.opensaml.saml.common.BaseSAMLObjectValidatorTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.NameID;

/**
 * Test case for {@link org.opensaml.saml.saml2.core.validator.NameIDSchemaValidator}.
 */
public class NameIDSchemaTest extends BaseSAMLObjectValidatorTestCase {

    /** Constructor */
    public NameIDSchemaTest() {
        targetQName = new QName(SAMLConstants.SAML20_NS, NameID.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        validator = new NameIDSchemaValidator();
    }

    protected void populateRequiredData() {
        super.populateRequiredData();
        NameID nameID = (NameID) target;
        nameID.setValue("name");
    }

    /**
     * Tests Name failure.
     * 
     * @throws ValidationException
     */
    public void testNameFailure() throws ValidationException {
        NameID nameID = (NameID) target;

        nameID.setValue(null);
        assertValidationFail("Name was null, should raise a Validation Exception");

        nameID.setValue("");
        assertValidationFail("Name was empty string, should raise a Validation Exception");
        
        nameID.setValue("    ");
        assertValidationFail("Name was white space, should raise a Validation Exception");
    }
}