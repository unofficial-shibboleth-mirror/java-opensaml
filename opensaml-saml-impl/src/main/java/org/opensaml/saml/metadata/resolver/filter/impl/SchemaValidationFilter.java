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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Validator;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.xml.SAMLSchemaBuilder;
import org.opensaml.saml.metadata.resolver.filter.AbstractMetadataFilter;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilterContext;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A metadata filter that schema validates an incoming metadata file.
 */
public class SchemaValidationFilter extends AbstractMetadataFilter {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SchemaValidationFilter.class);

    /** SAML schema source. */
    @Nonnull private SAMLSchemaBuilder samlSchemaBuilder;
        
    /**
     * Constructor.
     * 
     * @param builder SAML schema source to use
     */
    public SchemaValidationFilter(@Nonnull @ParameterName(name="builder") final SAMLSchemaBuilder builder) {
        samlSchemaBuilder = Constraint.isNotNull(builder, "SAMLSchemaBuilder cannot be null");
    }

    /** {@inheritDoc} */
    @Nullable public XMLObject filter(@Nullable final XMLObject metadata, @Nonnull final MetadataFilterContext context)
            throws FilterException {
        checkComponentActive();
        
        if (metadata == null) {
            return null;
        }
        
        final Validator schemaValidator;
        try {
            schemaValidator = samlSchemaBuilder.getSAMLSchema().newValidator();
        } catch (final SAXException e) {
            log.error("Unable to build metadata validation schema: {}", e.getMessage());
            throw new FilterException("Unable to build metadata validation schema", e);
        }

        try {
            schemaValidator.validate(new DOMSource(metadata.getDOM()));
        } catch (final Exception e) {
            log.error("Incoming metadata was not schema valid: {}", e.getMessage());
            throw new FilterException("Incoming metadata was not schema valid", e);
        }
        
        return metadata;
    }
    
}