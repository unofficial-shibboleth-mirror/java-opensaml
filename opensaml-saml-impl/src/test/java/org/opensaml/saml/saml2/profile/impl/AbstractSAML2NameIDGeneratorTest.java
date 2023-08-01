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

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.FunctionSupport;

import javax.annotation.Nonnull;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLException;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.profile.AbstractSAML2NameIDGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Test for {@link AbstractSAML2NameIDGenerator}. */
@SuppressWarnings({"null", "javadoc"})
public class AbstractSAML2NameIDGeneratorTest extends OpenSAMLInitBaseTestCase {

    @Nonnull private static final String NAME_QUALIFIER = "https://idp.example.org";

    @Nonnull private static final String SP_NAME_QUALIFIER = "https://sp.example.org";
    
    @Test
    public void testFull() throws ComponentInitializationException, SAMLException {
        final MockSAML2NameIDGenerator mock = new MockSAML2NameIDGenerator();
        mock.setFormat(NameID.X509_SUBJECT);
        mock.setIdPNameQualifier(NAME_QUALIFIER);
        mock.setSPNameQualifier(SP_NAME_QUALIFIER);
        mock.setSPProvidedId("bar");
        mock.initialize();
        
        final NameID nameId = mock.generate(new ProfileRequestContext(), mock.getFormat());
        assert nameId != null;
        Assert.assertEquals(nameId.getValue(), "foo");
        Assert.assertEquals(nameId.getSPProvidedID(), "bar");
        Assert.assertEquals(nameId.getNameQualifier(), NAME_QUALIFIER);
        Assert.assertEquals(nameId.getSPNameQualifier(), SP_NAME_QUALIFIER);
    }

    @Test
    public void testOmitSet() throws ComponentInitializationException, SAMLException {
        final MockSAML2NameIDGenerator mock = new MockSAML2NameIDGenerator();
        mock.setFormat(NameID.X509_SUBJECT);
        mock.setIdPNameQualifier(NAME_QUALIFIER);
        mock.setSPNameQualifier(SP_NAME_QUALIFIER);
        mock.setOmitQualifiers(true);
        mock.initialize();
        
        final NameID nameId = mock.generate(new ProfileRequestContext(), mock.getFormat());
        assert nameId != null;
        Assert.assertEquals(nameId.getValue(), "foo");
        Assert.assertNull(nameId.getNameQualifier());
        Assert.assertNull(nameId.getSPNameQualifier());
    }

    @Test
    public void testOmitUnset() throws ComponentInitializationException, SAMLException {
        final MockSAML2NameIDGenerator mock = new MockSAML2NameIDGenerator();
        mock.setFormat(NameID.X509_SUBJECT);
        mock.setOmitQualifiers(true);
        mock.initialize();
        
        final NameID nameId = mock.generate(new ProfileRequestContext(), mock.getFormat());
        assert nameId != null;
        Assert.assertEquals(nameId.getValue(), "foo");
        Assert.assertNull(nameId.getNameQualifier());
        Assert.assertNull(nameId.getSPNameQualifier());
    }

    @Test
    public void testDefaultQualifier() throws ComponentInitializationException, SAMLException {
        final MockSAML2NameIDGenerator mock = new MockSAML2NameIDGenerator();
        mock.setFormat(NameID.X509_SUBJECT);
        mock.initialize();
        
        final NameID nameId = mock.generate(new ProfileRequestContext(), mock.getFormat());
        assert nameId != null;
        Assert.assertEquals(nameId.getValue(), "foo");
        Assert.assertEquals(nameId.getNameQualifier(), NAME_QUALIFIER);
        Assert.assertEquals(nameId.getSPNameQualifier(), SP_NAME_QUALIFIER);
    }
    
    private class MockSAML2NameIDGenerator extends AbstractSAML2NameIDGenerator {

        public MockSAML2NameIDGenerator() {
            setId("test");
            setDefaultIdPNameQualifierLookupStrategy(FunctionSupport.constant(NAME_QUALIFIER));
            setDefaultSPNameQualifierLookupStrategy(FunctionSupport.constant(SP_NAME_QUALIFIER));
        }
        
        /** {@inheritDoc} */
        @Override
        protected String getIdentifier(@Nonnull final ProfileRequestContext profileRequestContext) throws SAMLException {
            return "foo";
        }
    }
}