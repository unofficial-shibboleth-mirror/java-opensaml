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

package org.opensaml.saml.common.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.validation.Schema;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.xml.ClasspathResolver;
import net.shibboleth.shared.xml.SchemaBuilder;

import org.opensaml.core.xml.XMLRuntimeException;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import com.google.common.io.ByteStreams;

/**
 * A convenience builder for creating {@link Schema}s for validating SAML 1.0, 1.1, and 2.0.
 * 
 * <p>Additional schemas may be included in the resulting object by supplying their locations
 * to an injected {@link SchemaBuilder} object.</p>
 */
@ThreadSafe
public class SAMLSchemaBuilder {
    
    /** Classpath relative location of basic XML schemas. */
    @Nonnull @NotEmpty private static String[] baseXMLSchemas = {
        SAMLConstants.XML_SCHEMA_LOCATION,
        SAMLConstants.XSD_SCHEMA_LOCATION,
        SAMLConstants.XMLSIG_SCHEMA_LOCATION,
        SAMLConstants.XMLENC_SCHEMA_LOCATION,
        SAMLConstants.XMLSIG11_SCHEMA_LOCATION,
        SAMLConstants.XMLENC11_SCHEMA_LOCATION,
        };

    /** Classpath relative location of SOAP 1_1 schemas. */
    @Nonnull @NotEmpty private static String[] soapSchemas = {
        SAMLConstants.SOAP11ENV_SCHEMA_LOCATION,
        };

    /** Classpath relative location of SAML 1_0 schemas. */
    @Nonnull @NotEmpty private static String[] saml10Schemas = {
        SAMLConstants.SAML10_SCHEMA_LOCATION,
        SAMLConstants.SAML10P_SCHEMA_LOCATION,
        };

    /** Classpath relative location of SAML 1_1 schemas. */
    @Nonnull @NotEmpty private static String[] saml11Schemas = {
        SAMLConstants.SAML11_SCHEMA_LOCATION,
        SAMLConstants.SAML11P_SCHEMA_LOCATION,
        };

    /** Classpath relative location of the invariant SAML 2.0 schemas. */
    @Nonnull @NotEmpty private static String[] saml20CommonSchemas = {
        SAMLConstants.SAML20AC_SCHEMA_LOCATION,
        "/schema/saml-schema-authn-context-auth-telephony-2.0.xsd",
        "/schema/saml-schema-authn-context-ip-2.0.xsd",
        "/schema/saml-schema-authn-context-ippword-2.0.xsd",
        "/schema/saml-schema-authn-context-kerberos-2.0.xsd",
        "/schema/saml-schema-authn-context-mobileonefactor-reg-2.0.xsd",
        "/schema/saml-schema-authn-context-mobileonefactor-unreg-2.0.xsd",
        "/schema/saml-schema-authn-context-mobiletwofactor-reg-2.0.xsd",
        "/schema/saml-schema-authn-context-mobiletwofactor-unreg-2.0.xsd",
        "/schema/saml-schema-authn-context-nomad-telephony-2.0.xsd",
        "/schema/saml-schema-authn-context-personal-telephony-2.0.xsd",
        "/schema/saml-schema-authn-context-pgp-2.0.xsd",
        "/schema/saml-schema-authn-context-ppt-2.0.xsd",
        "/schema/saml-schema-authn-context-pword-2.0.xsd",
        "/schema/saml-schema-authn-context-session-2.0.xsd",
        "/schema/saml-schema-authn-context-smartcard-2.0.xsd",
        "/schema/saml-schema-authn-context-smartcardpki-2.0.xsd",
        "/schema/saml-schema-authn-context-softwarepki-2.0.xsd",
        "/schema/saml-schema-authn-context-spki-2.0.xsd",
        "/schema/saml-schema-authn-context-srp-2.0.xsd",
        "/schema/saml-schema-authn-context-sslcert-2.0.xsd",
        "/schema/saml-schema-authn-context-telephony-2.0.xsd",
        "/schema/saml-schema-authn-context-timesync-2.0.xsd",
        "/schema/saml-schema-authn-context-x509-2.0.xsd",
        "/schema/saml-schema-authn-context-xmldsig-2.0.xsd",
        };

    /** Classpath relative location of SAML 2_0 schemas. */
    @Nonnull @NotEmpty private static String[] saml20Schemas = {
        SAMLConstants.SAML20_SCHEMA_LOCATION,
        SAMLConstants.SAML20P_SCHEMA_LOCATION,
        SAMLConstants.SAML20MD_SCHEMA_LOCATION,
        SAMLConstants.SAML20DCE_SCHEMA_LOCATION,
        SAMLConstants.SAML20ECP_SCHEMA_LOCATION,
        SAMLConstants.SAML20X500_SCHEMA_LOCATION,
        SAMLConstants.SAML20XACML_SCHEMA_LOCATION,
        // Start of extensions
        SAMLConstants.SAML20MDQUERY_SCHEMA_LOCATION,
        SAMLConstants.SAML20MDUI_SCHEMA_LOCATION,
        SAMLConstants.SAML20MDRPI_SCHEMA_LOCATION,
        SAMLConstants.SAML20ALG_SCHEMA_LOCATION,
        };

    /** Classpath relative location of SAML 2_0 schemas with strict string/anyURI types. */
    @Nonnull @NotEmpty private static String[] saml20StrictSchemas = {
        SAMLConstants.STRICT_SAML20_SCHEMA_LOCATION,
        SAMLConstants.STRICT_SAML20P_SCHEMA_LOCATION,
        SAMLConstants.STRICT_SAML20MD_SCHEMA_LOCATION,
        SAMLConstants.STRICT_SAML20DCE_SCHEMA_LOCATION,
        SAMLConstants.STRICT_SAML20ECP_SCHEMA_LOCATION,
        SAMLConstants.STRICT_SAML20X500_SCHEMA_LOCATION,
        SAMLConstants.STRICT_SAML20XACML_SCHEMA_LOCATION,
        // Start of extensions
        SAMLConstants.STRICT_SAML20MDQUERY_SCHEMA_LOCATION,
        SAMLConstants.STRICT_SAML20MDUI_SCHEMA_LOCATION,
        SAMLConstants.STRICT_SAML20MDRPI_SCHEMA_LOCATION,
        SAMLConstants.STRICT_SAML20ALG_SCHEMA_LOCATION,
        };
    
    /** Classpath relative location of SAML extension schemas. */
    @Nonnull @NotEmpty private static String[] baseExtSchemas = {
        SAMLConstants.SAML1MD_SCHEMA_LOCATION,
        SAMLConstants.SAML_IDP_DISCO_SCHEMA_LOCATION,
        SAMLConstants.SAML20PTHRPTY_SCHEMA_LOCATION,
        SAMLConstants.SAML20DEL_SCHEMA_LOCATION,
        SAMLConstants.SAML20MDATTR_SCHEMA_LOCATION,
        SAMLConstants.SAML20CB_SCHEMA_LOCATION,
        SAMLConstants.SAML20PASLO_SCHEMA_LOCATION,
        SAMLConstants.SAMLEC_GSS_SCHEMA_LOCATION,
        };
    
      
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(SAMLSchemaBuilder.class);
    
    /** Flag indicating whether the failure to resolve a schema resource should be considered fatal. */
    private boolean unresolvedSchemaFatal;

    /** Cached copy of the schema produced by the builder. */
    @Nullable private Schema cachedSchema;

    /** Reference to SAML 1.x schemas to apply. */
    @Nonnull private String[] saml1xSchemas;

    /** Reference to SAML 2.0 schemas to apply. */
    @Nonnull private String[] saml2Schemas;

    /** The builder to use. */
    private SchemaBuilder schemaBuilder;
    
    /** Identifies which SAML 1.x version is in use. */
    public enum SAML1Version {
        
        /** SAML 1.0. */
        SAML_10,
        
        /** SAML 1.1. */
        SAML_11,
    }
    
    /**
     * Constructor.
     * 
     * <p>A default {@link SchemaBuilder} is constructed, and injected with a
     * {@link ClasspathResolver} for resolving supplementary schemas.
     * 
     * @param ver   the SAML 1.x version to use
     */
    public SAMLSchemaBuilder(@Nonnull @ParameterName(name="ver") final SAML1Version ver) {
        this(ver, false);
    }

    /**
     * Constructor.
     * 
     * <p>A default {@link SchemaBuilder} is constructed, and injected with a
     * {@link ClasspathResolver} for resolving supplementary schemas.
     * 
     * @param ver   the SAML 1.x version to use
     * @param strict whether to apply modified schemas with strict string and anyURI type definitions
     * 
     * @since 4.1.0
     */
    public SAMLSchemaBuilder(@Nonnull @ParameterName(name="ver") final SAML1Version ver,
            @ParameterName(name="strict") final boolean strict) {
        unresolvedSchemaFatal = true;
        if (ver == SAML1Version.SAML_11) {
            saml1xSchemas = saml11Schemas;
        } else {
            saml1xSchemas = saml10Schemas;
        }
        if (strict) {
            saml2Schemas = saml20StrictSchemas;
        } else {
            saml2Schemas = saml20Schemas;
        }
    }

    /**
     * Set the flag indicating whether the failure to resolve a schema resource should be considered fatal.
     * 
     * <p>Default value: true.</p>
     * 
     * @param flag true if should be fatal, false if not
     */
    public void setUnresolvedSchemaFatal(final boolean flag) {
        unresolvedSchemaFatal = flag;
    }
    
    /**
     * Set a custom {@link SchemaBuilder} to use.
     * 
     * @param builder   SchemaBuilder to use
     */
    public synchronized void setSchemaBuilder(@Nonnull final SchemaBuilder builder) {
        schemaBuilder = Constraint.isNotNull(builder, "SchemaBuilder cannot be null");
        cachedSchema = null;
        configureBuilder();
    }

    /**
     * Get a schema that can validate SAML 1.x, 2.0, and all registered extensions.
     * 
     * @return schema
     * 
     * @throws SAXException thrown if a schema object cannot be created
     */
    @Nonnull public synchronized Schema getSAMLSchema() throws SAXException {
        if (cachedSchema == null) {
            if (schemaBuilder == null) {
                try {
                    schemaBuilder = new SchemaBuilder();
                    schemaBuilder.setResourceResolver(new ClasspathResolver());
                    configureBuilder();
                } catch (final RuntimeException e) {
                    schemaBuilder = null;
                    throw e;
                }
            }
            cachedSchema = schemaBuilder.buildSchema();
        }
        assert cachedSchema != null;
        return cachedSchema;
    }

    /**
     * Configure the appropriate {@link SchemaBuilder} with the right set of schemas.
     */
    private void configureBuilder() {
        for (final String source : baseXMLSchemas) {
            assert source != null;
            addSchemaToBuilder(source);
        }

        for (final String source : soapSchemas) {
            assert source != null;
            addSchemaToBuilder(source);
        }

        for (final String source : saml1xSchemas) {
            assert source != null;
            addSchemaToBuilder(source);
        }

        for (final String source : saml2Schemas) {
            assert source != null;
            addSchemaToBuilder(source);
        }

        for (final String source : saml20CommonSchemas) {
            assert source != null;
            addSchemaToBuilder(source);
        }
        
        for (final String source : baseExtSchemas) {
            assert source != null;
            addSchemaToBuilder(source);
        }
    }
    
    /**
     * Load the schema from the specified source and add it to the internal {@link SchemaBuilder}.
     * 
     * @param source the schema resource path
     */
    private void addSchemaToBuilder(@Nonnull final String source) {
        final Class<SAMLSchemaBuilder> clazz = SAMLSchemaBuilder.class;
        
        // To be safe, rather than pass the resource InputStream directly to the consumer,
        // copy the bytes and ensure that the resource stream is closed.
        try (final InputStream stream = clazz.getResourceAsStream(source)) {
            if (stream != null) {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ByteStreams.copy(stream, baos);
                schemaBuilder.addSchema(new ByteArrayInputStream(baos.toByteArray()));
            } else {
                log.error("Failed to locate schema resource: {}", source);
                if (unresolvedSchemaFatal) {
                    throw new XMLRuntimeException("Failed to locate schema resource: " + source);
                }
            }
        } catch (final IOException e) {
            if (unresolvedSchemaFatal) {
                throw new XMLRuntimeException("Error loading schema resource: " + source);
            }
        }
    }

}