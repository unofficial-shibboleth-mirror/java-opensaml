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

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.common.xml.SAMLSchemaBuilder;
import org.opensaml.saml.common.xml.SAMLSchemaBuilder.SAML1Version;
import org.opensaml.saml.metadata.resolver.impl.ResourceBackedMetadataResolver;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.spring.resource.ResourceHelper;

/**
 * Unit tests for {@link SchemaValidationFilter}.
 */
@SuppressWarnings("javadoc")
public class SchemaValidationFilterTest extends XMLObjectBaseTestCase {

    @Test
    public void testValid() throws Exception {
        final ResourceBackedMetadataResolver metadataProvider = new ResourceBackedMetadataResolver(
                ResourceHelper.of(new ClassPathResource("org/opensaml/saml/saml2/metadata/valid-metadata.xml")));
        
        final SchemaValidationFilter filter = new SchemaValidationFilter(new SAMLSchemaBuilder(SAML1Version.SAML_11));
        filter.initialize();
        
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");
        metadataProvider.setMetadataFilter(filter);
        metadataProvider.initialize();
    }
    
    @Test
    public void testStrict() throws Exception {
        final ResourceBackedMetadataResolver metadataProvider = new ResourceBackedMetadataResolver(
                ResourceHelper.of(new ClassPathResource("org/opensaml/saml/saml2/metadata/valid-metadata.xml")));
        final SchemaValidationFilter filter = new SchemaValidationFilter(new SAMLSchemaBuilder(SAML1Version.SAML_11, true));
        filter.initialize();

        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");
        metadataProvider.setMetadataFilter(filter);
        metadataProvider.initialize();
    }
    
    @Test(expectedExceptions=ComponentInitializationException.class)
    public void testStrictInvalid() throws Exception {
        final ResourceBackedMetadataResolver metadataProvider = new ResourceBackedMetadataResolver(
                ResourceHelper.of(new ClassPathResource("org/opensaml/saml/saml2/metadata/invalid-metadata.xml")));
        final SchemaValidationFilter filter = new SchemaValidationFilter(new SAMLSchemaBuilder(SAML1Version.SAML_11, true));
        filter.initialize();

        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");
        metadataProvider.setMetadataFilter(filter);
        metadataProvider.initialize();
        Assert.fail("Should have raised schema validation error");
    }

}