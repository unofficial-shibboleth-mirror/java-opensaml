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

package org.opensaml.saml.metadata.generator.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.core.xml.Namespace;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import com.google.common.xml.XmlEscapers;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.velocity.Template;

/**
 * Implementation of SAML metadata generation using Velocity.
 * 
 * @since 5.0.0
 */
public class VelocityMetadataGenerator extends AbstractIdentifiableInitializableComponent implements MetadataGenerator {

    /** Velocity engine. */
    @NonnullAfterInit private VelocityEngine velocityEngine;
    
    /**
     * Set the Velocity engine to use.
     * 
     * @param engine velocity engine
     */
    public void setVelocityEngine(@Nonnull final VelocityEngine engine) {
        velocityEngine = Constraint.isNotNull(engine, "VelocityEngine cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (velocityEngine == null) {
            throw new ComponentInitializationException("VelocityEngine cannot be null");
        }
    }

    /** {@inheritDoc} */
    public void generate(@Nonnull final MetadataGeneratorParameters params, @Nonnull final Writer sink)
            throws IOException {
        try {
            if (params instanceof TemplateMetadataGeneratorParameters downcast) {
                Template.fromTemplateName(velocityEngine,
                        downcast.getTemplatePath() + "/EntityDescriptor.vm").merge(
                                getVelocityContext(downcast), sink);
            } else {
                throw new IllegalArgumentException("Parameters were not of the expected type");
            }
        } catch (final Exception e) {
            if (e instanceof IOException io) {
                throw io;
            }
            throw new IOException(e);
        }
    }

    /**
     * Builds the Velocity template context.
     * 
     * @param params the input parameters
     * 
     * @return the populated context
     */
    @Nonnull protected VelocityContext getVelocityContext(@Nonnull final TemplateMetadataGeneratorParameters params) {
        final VelocityContext context = new VelocityContext();
        
        context.put("params", params);
        
        // Namespace handling.
        if (!params.isOmitNamespaceDeclarations()) {
            final Map<String,String> prefixMap = new HashMap<>();
            
            prefixMap.put(SAMLConstants.SAML20MD_PREFIX, SAMLConstants.SAML20MD_NS);
            prefixMap.put(SignatureConstants.XMLSIG_PREFIX, SignatureConstants.XMLSIG_NS);
            
            final Set<Namespace> additionalNamespaces = params.getAdditionalNamespaces();
            if (additionalNamespaces != null) {
                for (final Namespace ns : additionalNamespaces) {
                    prefixMap.put(ns.getNamespacePrefix(), ns.getNamespaceURI());
                }
            }
            
            context.put("namespaces", prefixMap);
        }
        
        context.put("xmltext", XmlEscapers.xmlContentEscaper());
        context.put("xmlattr", XmlEscapers.xmlAttributeEscaper());
        
        return context;
    }

}