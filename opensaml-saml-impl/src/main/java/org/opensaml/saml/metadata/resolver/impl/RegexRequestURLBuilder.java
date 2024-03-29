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

package org.opensaml.saml.metadata.resolver.impl;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.CriteriaSet;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.slf4j.Logger;

/**
 * Function which produces a URL by evaluating a supplied regular expression against the criteria entity ID, 
 * and applying the result to a supplied replacement string.
 * 
 * <p>
 * The function uses standard Java regular expression components from the <code>java.util.regex</code> package.
 * It is therefore helpful to have an understanding of the use of these Java classes.
 * </p>
 * 
 * <p>
 * The runtime logic is effectively:
 * </p>
 * <blockquote><pre>
 * Pattern pattern = Pattern.compile(regex);
 * Matcher matcher = pattern.matcher(entityID);
 * if (matcher.matches()) {
 *   return matcher.replaceAll(replacement);
 * else {
 *   return null;
 * }
 * </pre></blockquote>
 * 
 * <p>
 * For supported regular expression syntax see {@link Pattern}. For details on the replacement operation,
 * see {@link Matcher#replaceAll(String)}.
 * </p>
 * 
 * <p>
 * It is expected that the typical use case is that the supplied replacement string will be a combination of 
 * literal text combined with back references to the regular expression match groups, e.g. $1, $2, etc.
 * </p>
 * 
 * <p>
 * If the regular expression does not match the entity ID, or if there is an error in evaluating the 
 * regular expression, then null is returned.
 * </p>
 * 
 */
public class RegexRequestURLBuilder implements Function<CriteriaSet, String> {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(RegexRequestURLBuilder.class);
    
    /** The compiled pattern. */
    @Nonnull private final Pattern pattern;
    
    /** The replacement template. */
    @Nonnull private final String template;
   
    /**
     * Constructor.
     *
     * @param regex the regular expression against which to evaluate the entity ID
     * @param replacement the replacement template string.
     */
    public RegexRequestURLBuilder(@Nonnull @NotEmpty final String regex, @Nonnull @NotEmpty final String replacement) {
        final String regexTemp = Constraint.isNotNull(StringSupport.trimOrNull(regex), "Regex was null or empty");
        /*
        // TODO: Defensively add start and end anchors if not supplied?
        if (!regexTemp.startsWith("^")) {
            regexTemp = "^" + regexTemp;
        }
        if (!regexTemp.endsWith("$")) {
            regexTemp = regexTemp + "$";
        }
        */
        pattern = Pattern.compile(regexTemp);
        
        template = Constraint.isNotNull(StringSupport.trimOrNull(replacement), 
                "Replacement template was null or empty");
    }

    /** {@inheritDoc} */
    @Nullable public String apply(@Nullable final CriteriaSet criteria) {
        
        final EntityIdCriterion criterion = criteria != null ? criteria.get(EntityIdCriterion.class) : null;
        if (criterion == null) {
            log.trace("Criteria did not contain entity ID, unable to build request URL");
            return null;
        }
        final String entityID = criterion.getEntityId();
        
        try {
            final Matcher matcher = pattern.matcher(entityID);
            if (matcher.matches()) {
                final String result = matcher.replaceAll(template);
                log.debug("Regular expression '{}' matched successfully against entity ID '{}', returning '{}'", 
                        pattern.pattern(), entityID, result);
                return result;
            }
            log.debug("Regular expression '{}' did not match against entity ID '{}', returning null", 
                    pattern.pattern(), entityID);
            return null;
        } catch (final Throwable t) {
            log.warn("Error evaluating regular expression '{}' against entity ID '{}'", pattern.pattern(), entityID, t);
            return null;
        }
    }

}