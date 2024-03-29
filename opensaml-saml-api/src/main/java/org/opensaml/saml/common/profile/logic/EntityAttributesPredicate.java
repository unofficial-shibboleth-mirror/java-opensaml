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

package org.opensaml.saml.common.profile.logic;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSBase64Binary;
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.XSDateTime;
import org.opensaml.core.xml.schema.XSInteger;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.XSURI;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.slf4j.Logger;

import com.google.common.collect.Iterables;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.xml.DOMTypeSupport;

import java.util.function.Predicate;

/**
 * Predicate to determine whether an {@link EntityDescriptor} or its parent groups contain an {@link EntityAttributes}
 * extension {@link Attribute} that matches the predicate's criteria.
 * 
 * <p>This class uses a nested helper class, {@link Candidate}, to capture the rules to check for, with each such
 * object representing a single condition that the predicate can combine either via an AND or OR semantic to produce
 * the final result. Each {@link Candidate}'s own matching rules must match entirely.</p>
 */
public class EntityAttributesPredicate implements Predicate<EntityDescriptor> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EntityAttributesPredicate.class);

    /** Whether to trim the values in the metadata before comparison. */
    private final boolean trimTags;
    
    /** Whether all the candidates must match. */
    private final boolean matchAll;
    
    /** Candidates to check for. */
    @Nonnull private final Collection<Candidate> candidateSet;

    /**
     * Constructor.
     * 
     * @param candidates the {@link Candidate} criteria to check for
     */
    public EntityAttributesPredicate(
            @Nonnull @ParameterName(name="candidates") final Collection<Candidate> candidates) {
        this(candidates, true, false);
    }

    /**
     * Constructor.
     * 
     * @param candidates the {@link Candidate} criteria to check for
     * @param trim true iff the values found in the metadata should be trimmed before comparison
     */
    public EntityAttributesPredicate(
            @Nonnull @ParameterName(name="candidates") final Collection<Candidate> candidates,
            @ParameterName(name="trim") final boolean trim) {
        this(candidates, trim, false);
    }
    
    /**
     * Constructor.
     * 
     * @param candidates the {@link Candidate} criteria to check for
     * @param trim true iff the values found in the metadata should be trimmed before comparison
     * @param all true iff all the criteria must match to be a successful test
     */
    public EntityAttributesPredicate(
            @Nonnull @ParameterName(name="candidates") final Collection<Candidate> candidates,
            @ParameterName(name="trim") final boolean trim, @ParameterName(name="all") final boolean all) {
        
        candidateSet = CollectionSupport.copyToList(
                Constraint.isNotNull(candidates, "Candidate collection cannot be null"));
        
        trimTags = trim;
        matchAll = all;
    }    
    
    /**
     * Get whether to trim tags for comparison.
     * 
     * @return  true iff tags are to be trimmed for comparison
     */
    public boolean getTrimTags() {
        return trimTags;
    }

    /**
     * Get whether all candidates must match.
     * 
     * @return  true iff all candidates have to match 
     */
    public boolean getMatchAll() {
        return matchAll;
    }
    
    /**
     * Get the candidate criteria.
     * 
     * @return  the candidate criteria
     */
    @Nonnull @Unmodifiable @NotLive public Collection<Candidate> getCandidates() {
        return candidateSet;
    }

// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    public boolean test(@Nullable final EntityDescriptor input) {
        if (input == null) {
            return false;
        }
        
        Collection<Attribute> entityAttributes = null;

        // Check for a tag match in the EntityAttributes extension of the entity and its parent(s).
        Extensions exts = input.getExtensions();
        if (exts != null) {
            final List<XMLObject> children = exts.getUnknownXMLObjects(EntityAttributes.DEFAULT_ELEMENT_NAME);
            if (!children.isEmpty() && children.get(0) instanceof EntityAttributes) {
                if (entityAttributes == null) {
                    entityAttributes = new ArrayList<>();
                }
                entityAttributes.addAll(((EntityAttributes) children.get(0)).getAttributes());
            }
        }

        EntitiesDescriptor group = (EntitiesDescriptor) input.getParent();
        while (group != null) {
            exts = group.getExtensions();
            if (exts != null) {
                final List<XMLObject> children = exts.getUnknownXMLObjects(EntityAttributes.DEFAULT_ELEMENT_NAME);
                if (!children.isEmpty() && children.get(0) instanceof EntityAttributes) {
                    if (entityAttributes == null) {
                        entityAttributes = new ArrayList<>();
                    }
                    entityAttributes.addAll(((EntityAttributes) children.get(0)).getAttributes());
                }
            }
            group = (EntitiesDescriptor) group.getParent();
        }

        if (entityAttributes == null || entityAttributes.isEmpty()) {
            log.trace("No Entity Attributes found for {}", input.getEntityID());
            return false;
        }
        
        log.trace("Checking for match against {} Entity Attributes for {}", entityAttributes.size(),
                input.getEntityID());
        
        // If we find a matching tag, we win. Each tag is treated in OR fashion.
        final EntityAttributesMatcher matcher = new EntityAttributesMatcher(entityAttributes);
        
        // Then we determine whether the overall set of tag containers is AND or OR.
        if (matchAll) {
            return Iterables.all(candidateSet, matcher::test);
        }
        if (Iterables.tryFind(candidateSet, matcher::test).isPresent()) {
            return true;
        }

        return false;
    }
    
    /**
     * An object to encapsulate the set of criteria that must be satisfied by an {@link EntityAttributes}
     * extension to satisfy the enclosing predicate.
     * 
     * <p>All of the value and regular expression criteria provided must match for the individual object's result
     * to be "true".</p>
     */
    public static class Candidate {
        
        /** Attribute Name. */
        @Nonnull @NotEmpty private final String nam;
        
        /** Attribute NameFormat. */
        @Nullable private final String nameFormat;
        
        /** Values that must match exactly. */
        @Nonnull private List<String> values;
        
        /** Regular expressions that must be satisfied. */
        @Nonnull private List<Pattern> regexps;

        /**
         * Constructor.
         *
         * @param name   Attribute Name to match
         */
        public Candidate(@Nonnull @NotEmpty @ParameterName(name="name") final String name) {
            nam = Constraint.isNotNull(StringSupport.trimOrNull(name), "Attribute Name cannot be null or empty");
            nameFormat = null;
            
            values = CollectionSupport.emptyList();
            regexps = CollectionSupport.emptyList(); 
        }

        /**
         * Constructor.
         *
         * @param name   Attribute Name to match
         * @param format Attribute NameFormat to match
         */
        public Candidate(@Nonnull @NotEmpty @ParameterName(name="name") final String name,
                @Nullable @ParameterName(name="format") final String format) {
            nam = Constraint.isNotNull(StringSupport.trimOrNull(name), "Attribute Name cannot be null or empty");
            if (Attribute.UNSPECIFIED.equals(format)) {
                nameFormat = null;
            } else {
                nameFormat = StringSupport.trimOrNull(format);
            }
            
            values = CollectionSupport.emptyList();
            regexps = CollectionSupport.emptyList(); 
        }

        /**
         * Get the Attribute Name to match.
         * 
         * @return Attribute Name to match
         */
        @Nonnull @NotEmpty public String getName() {
            return nam;
        }

        /**
         * Get the Attribute NameFormat to match.
         * 
         * @return Attribute NameFormat to match
         */
        @Nullable public String getNameFormat() {
            return nameFormat;
        }

        /**
         * Get the exact values to match.
         * 
         * @return the exact values to match
         */
        @Nonnull @Unmodifiable @NotLive public List<String> getValues() {
            return values;
        }

        /**
         * Set the exact values to match.
         * 
         * @param vals the exact values to match
         */
        public void setValues(@Nonnull final Collection<String> vals) {
            values = CollectionSupport.copyToList(Constraint.isNotNull(vals, "Values collection cannot be null"));
        }

        /**
         * Get the regular expressions to match.
         * 
         * @return the regular expressions to match.
         */
        @Nonnull @Unmodifiable @NotLive public List<Pattern> getRegexps() {
            return regexps;
        }

        /**
         * Set the regular expressions to match.
         * 
         * @param exps the regular expressions to match
         */
        public void setRegexps(@Nonnull final Collection<Pattern> exps) {
            regexps = CollectionSupport.copyToList(
                    Constraint.isNotNull(exps, "Regular expressions collection cannot be null"));
        }
    }
    
    /**
     * Determines whether an {@link Candidate} criterion is satisfied by the {@link Attribute}s
     * in an {@link EntityAttributes} extension.
     */
    private class EntityAttributesMatcher implements Predicate<Candidate> {
        
        /** Population to evaluate for a match. */
        @Nonnull private final Collection<Attribute> attributes;
        
        /**
         * Constructor.
         *
         * @param attrs population to evaluate for a match
         */
        public EntityAttributesMatcher(@Nonnull final Collection<Attribute> attrs) {
            attributes = Constraint.isNotNull(attrs, "Extension attributes cannot be null");
        }

// Checkstyle: MethodLength OFF
        /** {@inheritDoc} */
        public boolean test(@Nullable final Candidate input) {
            if (input == null) {
                return false;
            }
            
            final List<String> tagvals = input.values;
            final List<Pattern> tagexps = input.regexps;

            // Track whether we've found every match we need (possibly with arrays of 0 size).
            final boolean[] valflags = new boolean[tagvals.size()];
            final boolean[] expflags = new boolean[tagexps.size()];

            // Check each attribute/tag in the populated set.
            for (final Attribute a : attributes) {
                // Compare Name and NameFormat for a matching tag.
                final String name = a.getName();
                String nameFormat = a.getNameFormat();
                if (nameFormat == null) {
                    nameFormat = Attribute.UNSPECIFIED;
                }
                if (name != null && name.equals(input.getName())
                        && (input.getNameFormat() == null || nameFormat.equals(input.getNameFormat()))) {

                    final List<String> attributeValues = getPossibleAttributeValuesAsStrings(a);
                    // Check each tag value's simple content for a value match.
                    for (int tagindex = 0; tagindex < tagvals.size(); ++tagindex) {
                        final String tagvalstr = tagvals.get(tagindex);
                        for (final String cvalstr: attributeValues) {
                            if (tagvalstr != null && cvalstr != null) {
                                if (tagvalstr.equals(cvalstr)) {
                                    log.trace("Matched Entity Attribute ({}:{}) value {}", a.getNameFormat(),
                                            a.getName(), tagvalstr);
                                    valflags[tagindex] = true;
                                    break;
                                } else if (trimTags) {
                                    if (tagvalstr.equals(cvalstr.trim())) {
                                        log.trace("Matched Entity Attribute ({}:{}) value {}", a.getNameFormat(),
                                                a.getName(), tagvalstr);
                                        valflags[tagindex] = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    // Check each tag regular expression for a match.
                    for (int tagindex = 0; tagindex < tagexps.size(); ++tagindex) {
                        for (final String cvalstr: attributeValues) {
                            if (tagexps.get(tagindex) != null && cvalstr != null) {
                                if (tagexps.get(tagindex).matcher(cvalstr).matches()) {
                                    log.trace("Matched Entity Attribute ({}:{}) value {}", a.getNameFormat(),
                                            a.getName(), cvalstr);
                                    expflags[tagindex] = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            for (final boolean flag : valflags) {
                if (!flag) {
                    return false;
                }
            }

            for (final boolean flag : expflags) {
                if (!flag) {
                    return false;
                }
            }

            return true;
        }
// Checkstyle: MethodLength ON

        /** Get all possible strings values for the attribute.  This copes with the fact that
         * an attribute can return multiple values {@link Attribute#getAttributeValues()} and that some
         * type of value can have multiple values (for instance a boolean can be 1/0/true/false).
         *
         * @param attribute what to inspect
         * @return all possible values, as string.
         */
        @Nonnull @Live private List<String> getPossibleAttributeValuesAsStrings(final @Nonnull Attribute attribute) {
            final List<XMLObject> cvals = attribute.getAttributeValues();
            final List<String> result = new ArrayList<>(cvals.size()*2);
            for (final XMLObject cval : cvals) {
                assert cval != null;
                result.addAll(xmlObjectToStrings(cval));
            }
            return result;
        }
     
        /**
         * Convert an XMLObject to an array of String which can represent the type, if recognized.
         * 
         * @param object object to convert
         * @return the converted value, or null
         */
        @Nullable @Unmodifiable @NotLive private List<String> xmlObjectToStrings(@Nonnull final XMLObject object) {
            String toMatch = null;
            String toMatchAlt = null;
            if (object instanceof XSString xs) {
                toMatch = xs.getValue();
            } else if (object instanceof XSURI xs) {
                toMatch = xs.getURI();
            } else if (object instanceof XSBoolean xs) {
                final XSBooleanValue val = xs.getValue();
                if (val != null) {
                    toMatch = val.getValue() ? "1" : "0";
                    toMatchAlt = val.getValue() ? "true" : "false";
                }
            } else if (object instanceof XSInteger xs) {
                final Integer val = xs.getValue();
                if (val != null) {
                    toMatch = val.toString();
                }
            } else if (object instanceof XSDateTime) {
                final Instant dt = ((XSDateTime) object).getValue();
                if (dt != null) {
                    toMatch = DOMTypeSupport.instantToString(dt);
                }
            } else if (object instanceof XSBase64Binary) {
                toMatch = ((XSBase64Binary) object).getValue();
            } else if (object instanceof XSAny) {
                final XSAny wc = (XSAny) object;
                if (wc.getUnknownAttributes().isEmpty() && wc.getUnknownXMLObjects().isEmpty()) {
                    toMatch = wc.getTextContent();
                }
            }
            if (toMatch != null && toMatchAlt != null) {
                return CollectionSupport.listOf(toMatch, toMatchAlt);
            } else if (toMatch != null) {
                return CollectionSupport.singletonList(toMatch);
            }
            log.warn("Unrecognized XMLObject type ({}), unable to convert to a string for comparison",
                    object.getClass().getName());
            return CollectionSupport.emptyList();
        }
    }
// Checkstyle: CyclomaticComplexity OFF

}