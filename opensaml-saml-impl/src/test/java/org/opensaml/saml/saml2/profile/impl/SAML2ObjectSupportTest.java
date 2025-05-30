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

package org.opensaml.saml.saml2.profile.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.tests.MockBaseID;
import org.opensaml.saml.saml2.profile.SAML2ObjectSupport;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 */
public class SAML2ObjectSupportTest extends XMLObjectBaseTestCase {
    
    @DataProvider
    public Object[][] subjects () {
       return new Object[][] {
           new Object[] {
                   "luke", NameID.UNSPECIFIED, null, null,
                   "luke", NameID.UNSPECIFIED, null, null,
                   Boolean.TRUE},
           new Object[] {
                   "luke", null, null, null,
                   "luke", NameID.UNSPECIFIED, null, null,
                   Boolean.TRUE},
           new Object[] {
                   "luke", NameID.UNSPECIFIED, null, null,
                   "luke", null, null, null,
                   Boolean.TRUE},
           new Object[] {
                   "luke", null, null, null,
                   "luke", null, null, null,
                   Boolean.TRUE},
           new Object[] {
                   "luke", null, "QualFoo", "SPQualFoo",
                   "luke", null, "QualFoo", "SPQualFoo",
                   Boolean.TRUE},

           new Object[] {
                   "luke", null, null, null,
                   "han", null, null, null,
                   Boolean.FALSE},
           new Object[] {
                   "luke", "FormatFoo", null, null,
                   "luke", "FormatBar", null, null,
                   Boolean.FALSE},
           new Object[] {
                   "luke", null, "QualFoo", null,
                   "luke", null, "QualBar", null,
                   Boolean.FALSE},
           new Object[] {
                   "luke", null, null, "SPQualFoo",
                   "luke", null, null, "SPQualBar",
                   Boolean.FALSE},
       };
    }
    
    @Test(dataProvider="subjects")
    public void matchSubject(String targetValue, String targetFormat, String targetNameQualifer, String targetSPNameQualifer,
                             String controlValue, String controlFormat, String controlNameQualifer, String controlSPNameQualifer,
                             Boolean matches) {
        
        Assert.assertEquals(SAML2ObjectSupport.matchSubject(
                buildSubject(targetValue, targetFormat, targetNameQualifer, targetSPNameQualifer),
                buildSubject(controlValue, controlFormat, controlNameQualifer, controlSPNameQualifer)),
                matches);
    }
    
    @Test
    public void matchSubjectSpecialCases() {
        Subject subj1 = buildSubject("luke", null, null, null);
        Subject subj2 = buildSubject("luke", null, null, null);
        
        Assert.assertEquals(SAML2ObjectSupport.matchSubject(subj1, subj2), Boolean.TRUE);
        
        subj1.setNameID(null);
        subj2 = buildSubject("luke", null, null, null);
        Assert.assertEquals(SAML2ObjectSupport.matchSubject(subj1, subj2), Boolean.FALSE);
        
        subj1 = buildSubject("luke", null, null, null);
        subj2.setNameID(null);
        Assert.assertEquals(SAML2ObjectSupport.matchSubject(subj1, subj2), Boolean.FALSE);
        
        subj1.setNameID(null);
        subj2.setNameID(null);
        Assert.assertEquals(SAML2ObjectSupport.matchSubject(subj1, subj2), Boolean.TRUE);
        
        subj2 = buildSubject("luke", null, null, null);

        subj1 = (Subject) XMLObjectSupport.buildXMLObject(Subject.DEFAULT_ELEMENT_NAME);
        subj1.setEncryptedID((EncryptedID) XMLObjectSupport.buildXMLObject(EncryptedID.DEFAULT_ELEMENT_NAME));
        try {
            SAML2ObjectSupport.matchSubject(subj1, subj2);
            Assert.fail("Subject match did not fail on presence of EncryptedID");
        } catch (IllegalArgumentException e) {
            //expected
        }
        
        subj1 = (Subject) XMLObjectSupport.buildXMLObject(Subject.DEFAULT_ELEMENT_NAME);
        subj1.setBaseID(new MockBaseID());
        try {
            SAML2ObjectSupport.matchSubject(subj1, subj2);
            Assert.fail("Subject match did not fail on presence of BaseID");
        } catch (IllegalArgumentException e) {
            //expected
        }
        
    }
    
    @Nonnull
    private Subject buildSubject(String value, String format, String nameQualifer, String spNameQualifer) {
        
        final NameID nameID = (NameID) XMLObjectSupport.buildXMLObject(NameID.DEFAULT_ELEMENT_NAME);
        nameID.setValue(value);
        nameID.setFormat(format);
        nameID.setNameQualifier(nameQualifer);
        nameID.setSPNameQualifier(spNameQualifer);

        final Subject subject = (Subject) XMLObjectSupport.buildXMLObject(Subject.DEFAULT_ELEMENT_NAME);
        subject.setNameID(nameID);
        return subject;
    }

}
