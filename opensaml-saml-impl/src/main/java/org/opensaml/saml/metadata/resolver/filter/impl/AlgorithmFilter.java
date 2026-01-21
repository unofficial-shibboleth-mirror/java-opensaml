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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.metadata.resolver.filter.AbstractMetadataFilter;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilterContext;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.algorithm.AlgorithmDescriptor.AlgorithmType;
import org.opensaml.xmlsec.algorithm.AlgorithmRegistry;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.slf4j.Logger;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A filter that adds algorithm extension content to entities in order to drive software
 * behavior based on them.
 * 
 * <p>The entities to annotate are identified with a {@link Predicate}, and multiple algorithms can be
 * associated with each.</p>
 */
public class AlgorithmFilter extends AbstractMetadataFilter {

    /** Used as a value in place of an empty collection when removing only. */
    @Nonnull @NotEmpty private static final String GUARD_VALUE = "_EMPTY";
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AlgorithmFilter.class);

    /** Registry for sanity checking algorithms. */
    @Nullable private AlgorithmRegistry registry = AlgorithmSupport.getGlobalAlgorithmRegistry();
    
    /** Flag controlling removal of existing DigestMethod extensions. */
    private boolean removeExistingDigestMethods;

    /** Flag controlling removal of existing SigningMethod extensions. */
    private boolean removeExistingSigningMethods;

    /** Flag controlling removal of existing EncryptionMethod extensions. */
    private boolean removeExistingEncryptionMethods;
    
    /** Rules for adding algorithms. */
    @Nonnull private Multimap<Predicate<EntityDescriptor>,XMLObject> applyMap;
    
    /** Builder for {@link Extensions}. */
    @Nonnull private final SAMLObjectBuilder<Extensions> extBuilder;
    
    /** Stands in for an empty collection when removing only. */
    @Nonnull private final XSString guardObject;

    /** Constructor. */
    public AlgorithmFilter() {
        guardObject = XMLObjectProviderRegistrySupport.getBuilderFactory().<XSString>ensureBuilder(
                XSString.TYPE_NAME).buildObject(null, GUARD_VALUE, null);
        guardObject.setValue(GUARD_VALUE);
        extBuilder = (SAMLObjectBuilder<Extensions>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Extensions>ensureBuilder(
                        Extensions.DEFAULT_ELEMENT_NAME);
        applyMap = ArrayListMultimap.create();
    }
    
    /**
     * Sets whether to remove existing DigestMethod extensions.
     * 
     * @param flag flag to set
     */
    public void setRemoveExistingDigestMethods(final boolean flag) {
        checkSetterPreconditions();
        removeExistingDigestMethods = flag;
    }

    /**
     * Sets whether to remove existing SigningMethod extensions.
     * 
     * @param flag flag to set
     */
    public void setRemoveExistingSigningMethods(final boolean flag) {
        checkSetterPreconditions();
        removeExistingSigningMethods = flag;
    }

    /**
     * Sets whether to remove existing EncryptionMethod extensions.
     * 
     * @param flag flag to set
     */
    public void setRemoveExistingEncryptionMethods(final boolean flag) {
        checkSetterPreconditions();
        removeExistingEncryptionMethods = flag;
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /**
     * Set the mappings from {@link Predicate} to extensions of various types to apply.
     * 
     * @param rules rules to apply
     */
    public void setRules(@Nonnull final Map<Predicate<EntityDescriptor>,Collection<XMLObject>> rules) {
        checkSetterPreconditions();
        Constraint.isNotNull(rules, "Rules map cannot be null");

        rules.values().stream()
            .flatMap(Collection::stream)
            .filter(DigestMethod.class::isInstance)
            .map(DigestMethod.class::cast)
            .map(DigestMethod::getAlgorithm)
            .distinct()
            .forEach(this::checkDigestMethod);

        rules.values().stream()
            .flatMap(Collection::stream)
            .filter(SigningMethod.class::isInstance)
            .map(SigningMethod.class::cast)
            .map(SigningMethod::getAlgorithm)
            .distinct()
            .forEach(this::checkSigningMethod);

        rules.values().stream()
            .flatMap(Collection::stream)
            .filter(EncryptionMethod.class::isInstance)
            .map(EncryptionMethod.class::cast)
            .map(EncryptionMethod::getAlgorithm)
            .distinct()
            .forEach(this::checkEncryptionMethod);

        applyMap = ArrayListMultimap.create(rules.size(), 1);
        for (final Map.Entry<Predicate<EntityDescriptor>,Collection<XMLObject>> entry : rules.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                applyMap.putAll(entry.getKey(),
                        entry.getValue().isEmpty() ? CollectionSupport.singletonList(guardObject)
                                : CollectionSupport.copyToList(entry.getValue()));
            }
        }
    }
// Checkstyle: CyclomaticComplexity ON

    /** {@inheritDoc} */
    @Nullable public XMLObject filter(@Nullable final XMLObject metadata, @Nonnull final MetadataFilterContext context)
            throws FilterException {
        checkComponentActive();
        
        if (metadata == null) {
            return null;
        }

        if (metadata instanceof EntitiesDescriptor) {
            filterEntitiesDescriptor((EntitiesDescriptor) metadata);
        } else {
            filterEntityDescriptor((EntityDescriptor) metadata);
        }
        
        return metadata;
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /**
     * Filters entity descriptor.
     * 
     * @param descriptor entity descriptor to filter
     */
    protected void filterEntityDescriptor(@Nonnull final EntityDescriptor descriptor) {

        Set<String> existingDigests = CollectionSupport.emptySet();
        Set<String> existingSignings = CollectionSupport.emptySet();
        final Extensions exts = descriptor.getExtensions();
        if (exts != null) {
            if (!removeExistingDigestMethods) {
                existingDigests = exts.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME)
                        .stream()
                        .filter(DigestMethod.class::isInstance)
                        .map(DigestMethod.class::cast)
                        .map(DigestMethod::getAlgorithm)
                        .distinct()
                        .collect(Collectors.toUnmodifiableSet());
            }
            if (!removeExistingSigningMethods) {
                existingSignings = exts.getUnknownXMLObjects(SigningMethod.DEFAULT_ELEMENT_NAME)
                        .stream()
                        .filter(SigningMethod.class::isInstance)
                        .map(SigningMethod.class::cast)
                        .map(SigningMethod::getAlgorithm)
                        .distinct()
                        .collect(Collectors.toUnmodifiableSet());
            }
        }
        
        for (final Map.Entry<Predicate<EntityDescriptor>,Collection<XMLObject>> entry : applyMap.asMap().entrySet()) {
            if (entry.getKey().test(descriptor)) {
                
                // Only acts if needed.
                removeExistingMethods(descriptor);
                
                for (final XMLObject xmlObject : entry.getValue()) {
                    // Check for guard object to break loop since we were only removing. 
                    if (xmlObject instanceof XSString) {
                        break;
                    }
                    
                    try {
                        if (xmlObject instanceof DigestMethod) {
                            if (existingDigests.contains(((DigestMethod) xmlObject).getAlgorithm())) {
                                log.debug("Skipping pre-existing DigestMethod ({}) on EntityDescriptor ({})",
                                        ((DigestMethod) xmlObject).getAlgorithm(), descriptor.getEntityID());
                            } else {
                                log.info("Adding DigestMethod ({}) to EntityDescriptor ({})",
                                        ((DigestMethod) xmlObject).getAlgorithm(), descriptor.getEntityID());
                                getExtensions(descriptor).getUnknownXMLObjects().add(
                                        XMLObjectSupport.cloneXMLObject(xmlObject));
                            }
                        } else if (xmlObject instanceof SigningMethod) {
                            if (existingSignings.contains(((SigningMethod) xmlObject).getAlgorithm())) {
                                log.debug("Skipping pre-existing SigningMethod ({}) on EntityDescriptor ({})",
                                        ((SigningMethod) xmlObject).getAlgorithm(), descriptor.getEntityID());
                            } else {
                                log.info("Adding SigningMethod ({}) to EntityDescriptor ({})",
                                        ((SigningMethod) xmlObject).getAlgorithm(), descriptor.getEntityID());
                                getExtensions(descriptor).getUnknownXMLObjects().add(
                                        XMLObjectSupport.cloneXMLObject(xmlObject));
                            }
                        } else if (xmlObject instanceof EncryptionMethod) {
                            addEncryptionMethod(descriptor, (EncryptionMethod) xmlObject);
                        }
                        
                    } catch (final MarshallingException | UnmarshallingException e) {
                        log.error("Error cloning XMLObject", e);
                    }
                }
            }
        }
    }
// Checkstyle: CyclomaticComplexity ON

    
    /**
     * Filters entities descriptor.
     * 
     * @param descriptor entities descriptor to filter
     */
    protected void filterEntitiesDescriptor(@Nonnull final EntitiesDescriptor descriptor) {
        
        // First we check any contained EntitiesDescriptors.
        for (final EntitiesDescriptor group : descriptor.getEntitiesDescriptors()) {
            assert group != null;
            filterEntitiesDescriptor(group);
        }
        
        // Next, check contained EntityDescriptors.
        for (final EntityDescriptor entity : descriptor.getEntityDescriptors()) {
            assert entity != null;
            filterEntityDescriptor(entity);
        }
    }
    
    /**
     * Return existing {@link Extensions} object or create it first.
     * 
     * @param descriptor the surrounding entity
     * 
     * @return new or existing extension block
     */
    @Nonnull protected Extensions getExtensions(@Nonnull final EntityDescriptor descriptor) {
        
        Extensions extensions = descriptor.getExtensions();
        if (extensions == null) {
            extensions = extBuilder.buildObject();
            descriptor.setExtensions(extensions);
        }
        
        return extensions;
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /**
     * Performs removal of existing methods if directed.
     * 
     * @param descriptor descriptor to filter
     */
    protected void removeExistingMethods(@Nonnull final EntityDescriptor descriptor) {
        Extensions exts = descriptor.getExtensions();
        // Remove existing objects first (if directed).
        if (exts != null) {
            if (removeExistingDigestMethods) {
                log.debug("Clearing existing entity-level DigestMethod extensions on EntityDescriptor '{}'",
                        descriptor.getEntityID());
                exts.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME).clear();
            }
            if (removeExistingSigningMethods) {
                log.debug("Clearing existing entity-level SigningMethod extensions on EntityDescriptor '{}'",
                        descriptor.getEntityID());
                exts.getUnknownXMLObjects(SigningMethod.DEFAULT_ELEMENT_NAME).clear();
            }
        }
        
        if (removeExistingDigestMethods || removeExistingSigningMethods || removeExistingEncryptionMethods) {
            for (final RoleDescriptor role : descriptor.getRoleDescriptors()) {
                exts = role.getExtensions();
                if (exts != null) {
                    if (removeExistingDigestMethods) {
                        log.debug("Clearing existing role-level DigestMethod extensions on EntityDescriptor '{}'",
                                descriptor.getEntityID());
                        exts.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME).clear();
                    }
                    if (removeExistingSigningMethods) {
                        log.debug("Clearing existing role-level SigningMethod extensions on EntityDescriptor '{}'",
                                descriptor.getEntityID());
                        exts.getUnknownXMLObjects(SigningMethod.DEFAULT_ELEMENT_NAME).clear();
                    }
                }
                if (removeExistingEncryptionMethods) {
                    for (final KeyDescriptor key : role.getKeyDescriptors()) {
                        if (key.getUse() == null || key.getUse() != UsageType.SIGNING) {
                            log.debug(
                                    "Clearing existing key-level EncryptionMethod extensions on EntityDescriptor '{}'",
                                    descriptor.getEntityID());
                            key.getEncryptionMethods().clear();
                        }
                    }
                }
            }
        }
    }
 // Checkstyle: CyclomaticComplexity ON

    /**
     * Add {@link EncryptionMethod} extension to every {@link KeyDescriptor} found in
     * an entity.
     * 
     * @param descriptor the entity to modify
     * @param encryptionMethod extension to add
     */
    protected void addEncryptionMethod(@Nonnull final EntityDescriptor descriptor,
            @Nonnull final EncryptionMethod encryptionMethod) {
        
        for (final RoleDescriptor role : descriptor.getRoleDescriptors()) {
            for (final KeyDescriptor key : role.getKeyDescriptors()) {
                if (key.getUse() == null || key.getUse() != UsageType.SIGNING) {

                    // Check if here already.
                    final List<EncryptionMethod> existingMethods = key.getEncryptionMethods();
                    for (final EncryptionMethod method : existingMethods) {
                        if (Objects.equals(method.getAlgorithm(), encryptionMethod.getAlgorithm())) {
                            log.debug("Skipping pre-existing EncryptionMethod ({}) on EntityDescriptor ({})",
                                    encryptionMethod.getAlgorithm(), descriptor.getEntityID());
                            return;
                        }
                    }
                    
                    try {
                        log.info("Adding EncryptionMethod ({}) to EntityDescriptor ({})",
                                encryptionMethod.getAlgorithm(), descriptor.getEntityID());
                        existingMethods.add(XMLObjectSupport.cloneXMLObject(encryptionMethod));
                    } catch (final MarshallingException|UnmarshallingException e) {
                        log.error("Error cloning EncryptionMethod", e);
                    }
                }
            }
        }
    }
    
    /**
     * Check the input method for "known" and "supported" status for logging purposes.
     * 
     * @param uri input method
     */
    private void checkDigestMethod(@Nullable @NotEmpty final String uri) {
        if (uri == null) {
            return;
        }
        
        final AlgorithmRegistry local = registry;
        if (local != null) {
            if (!local.getRegisteredURIsByType(AlgorithmType.MessageDigest).contains(uri)) {
                log.warn("DigestMethod {} unrecognized by algorithm registry", uri);
            } else if (!local.isRuntimeSupported(uri)) {
                log.warn("DigestMethod {} unsupported by runtime", uri);
            }
        }
    }
    
    /**
     * Check the input method for "known" and "supported" status for logging purposes.
     * 
     * @param uri input method
     */
    private void checkSigningMethod(@Nullable @NotEmpty final String uri) {
        if (uri == null) {
            return;
        }

        final AlgorithmRegistry local = registry;
        if (local != null) {
            if (!local.getRegisteredURIsByType(AlgorithmType.Signature).contains(uri) &&
                    !local.getRegisteredURIsByType(AlgorithmType.Mac).contains(uri)) {
                log.warn("SigningMethod {} unrecognized by algorithm registry", uri);
            } else if (!local.isRuntimeSupported(uri)) {
                log.warn("SigningMethod {} unsupported by runtime", uri);
            }
        }
    }

    /**
     * Check the input method for "known" and "supported" status for logging purposes.
     * 
     * @param uri input method
     */
    private void checkEncryptionMethod(@Nullable @NotEmpty final String uri) {
        if (uri == null) {
            return;
        }

        final AlgorithmRegistry local = registry;
        if (local != null) {
            if (!local.getRegisteredURIsByType(AlgorithmType.BlockEncryption).contains(uri) &&
                    !local.getRegisteredURIsByType(AlgorithmType.KeyTransport).contains(uri) &&
                    !local.getRegisteredURIsByType(AlgorithmType.KeyAgreement).contains(uri) &&
                    !local.getRegisteredURIsByType(AlgorithmType.SymmetricKeyWrap).contains(uri)) {
                log.warn("EncryptionMethod {} unrecognized by algorithm registry", uri);
            } else if (!local.isRuntimeSupported(uri)) {
                log.warn("EncryptionMethod {} unsupported by runtime", uri);
            }
        }
    }

}