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

/**
 * 
 */
package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectQuery;

/**
 *
 */
public abstract class SubjectQueryTestBase extends RequestTestBase {

    /**
     * Constructor
     *
     */
    public SubjectQueryTestBase() {
        super();
    }
    

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
    }


    /** {@inheritDoc} */
    @Test
    public abstract void testSingleElementUnmarshall();

    /** {@inheritDoc} */
    @Test
    public abstract void testSingleElementMarshall();


    /** {@inheritDoc} */
    protected void populateChildElements(SAMLObject samlObject) {
        SubjectQuery sq = (SubjectQuery) samlObject;
        
        super.populateChildElements(sq);
        
        QName subjectQName = new QName(SAMLConstants.SAML20_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        sq.setSubject((Subject) buildXMLObject(subjectQName));
    }


    /** {@inheritDoc} */
    protected void populateOptionalAttributes(SAMLObject samlObject) {
        super.populateOptionalAttributes(samlObject);
    }


    /** {@inheritDoc} */
    protected void populateRequiredAttributes(SAMLObject samlObject) {
        super.populateRequiredAttributes(samlObject);
    }


    /** {@inheritDoc} */
    protected void helperTestChildElementsUnmarshall(SAMLObject samlObject) {
        SubjectQuery sq = (SubjectQuery) samlObject;
        super.helperTestChildElementsUnmarshall(sq);
        Assert.assertNotNull(sq.getSubject(), "Subject");
    }


    /** {@inheritDoc} */
    protected void helperTestSingleElementOptionalAttributesUnmarshall(SAMLObject samlObject) {
        super.helperTestSingleElementOptionalAttributesUnmarshall(samlObject);
    }


    /** {@inheritDoc} */
    protected void helperTestSingleElementUnmarshall(SAMLObject samlObject) {
        super.helperTestSingleElementUnmarshall(samlObject);
    }
}