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

package org.opensaml.saml.saml2.profile;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.saml1.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.BaseID;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Subject;
import org.slf4j.Logger;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A helper class for working with SAMLObjects.
 */
public final class SAML2ObjectSupport {
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(SAML2ObjectSupport.class);
    
    /** Constructor. */
    private SAML2ObjectSupport() {
        
    }
    
    /**
     * Return true iff the two input {@link NameID} formats are equivalent for SAML 2.0
     * purposes.
     * 
     * @param format1   first format to check
     * @param format2   second format to check
     * 
     * @return  true iff the two format values should be viewed as equivalent
     */
    public static boolean areNameIDFormatsEquivalent(@Nullable final String format1,
            @Nullable final String format2) {
        
        return Objects.equals(
                format1 != null ? format1 : NameID.UNSPECIFIED,
                format2 != null ? format2 : NameID.UNSPECIFIED);
    }

    /**
     * Return true iff the two input {@link NameID} objects are equivalent for SAML 2.0 purposes, with
     * the assumption that the qualifier attributes must match exactly.
     * 
     * @param name1   first NameID to check
     * @param name2   second NameID to check
     * 
     * @return  true iff the two values should be viewed as equivalent
     */
    public static boolean areNameIDsEquivalent(@Nonnull final NameID name1, @Nonnull final NameID name2) {
        return areNameIDFormatsEquivalent(name1.getFormat(), name2.getFormat())
                && Objects.equals(name1.getValue(), name2.getValue())
                && Objects.equals(name1.getNameQualifier(), name2.getNameQualifier())
                && Objects.equals(name1.getSPNameQualifier(), name2.getSPNameQualifier());
    }

    /**
     * Return true iff the two input {@link NameID} objects are equivalent for SAML 2.0 purposes, allowing
     * thw qualifier attributes to assume default values if not otherwise set.
     * 
     * @param name1   first NameID to check
     * @param name2   second NameID to check
     * @param assertingParty optional name of asserting party to default in as NameQualifier
     * @param relyingParty optional name of relying party to default in as SPNameQualifier 
     * 
     * @return  true iff the two values should be viewed as equivalent
     * 
     * @since 3.4.0
     */
    public static boolean areNameIDsEquivalent(@Nonnull final NameID name1, @Nonnull final NameID name2,
            @Nullable final String assertingParty, @Nullable final String relyingParty) {
        
        if (!areNameIDFormatsEquivalent(name1.getFormat(), name2.getFormat())
                || !Objects.equals(name1.getValue(), name2.getValue())) {
            return false;
        }
        
        String name1qual = name1.getNameQualifier();
        String name2qual = name2.getNameQualifier();
        if (name1qual == null) {
            name1qual = assertingParty;
        }
        if (name2qual == null) {
            name2qual = assertingParty;
        }
        if (!Objects.equals(name1qual, name2qual)) {
            return false;
        }

        name1qual = name1.getSPNameQualifier();
        name2qual = name2.getSPNameQualifier();
        if (name1qual == null) {
            name1qual = relyingParty;
        }
        if (name2qual == null) {
            name2qual = relyingParty;
        }
        return Objects.equals(name1qual, name2qual);
    }
    
    /**
     * Match a target {@link Subject} against a control instance according to the requirements specified
     * in SAML Core 3.3.4.
     * 
     * <p>
     * Any {@link EncryptedID} instances which were originally present must have already been decrypted
     * and stored in-place on the Subject. {@link BaseID} is currently unsupported. Presence of either
     * in either target or control subject will throw {@link IllegalArgumentException}.
     * </p>
     * 
     * @param target the target subject to evaluate
     * @param control the control subject against which to evaluate the target
     * 
     * @return true if target matches the control, otherwise false
     * 
     * @throws IllegalArgumentException if EncryptedID or BaseID is present in either Subject instance
     */
    public static boolean matchSubject(@Nonnull final Subject target, @Nonnull final Subject control) {
        return matchSubject(target, control, true);
    }

    /**
     * Match a target {@link Subject} against a control instance according to the requirements specified
     * in SAML Core 3.3.4.
     * 
     * <p>
     * Any {@link EncryptedID} instances which were originally present must have already been decrypted
     * and stored in-place on the Subject. {@link BaseID} is currently unsupported. Presence of either
     * in either target or control subject will throw {@link IllegalArgumentException}.
     * </p>
     * 
     * @param target the target subject to evaluate
     * @param control the control subject against which to evaluate the target
     * @param processConfirmation flag controlling whether to process matching of {@link SubjectConfirmation}
     * 
     * @return true if target matches the control, otherwise false
     * 
     * @throws IllegalArgumentException if EncryptedID or BaseID is present in either Subject instance
     */
    public static boolean matchSubject(@Nonnull final Subject target, @Nonnull final Subject control,
            final boolean processConfirmation) {
        Constraint.isNotNull(target, "Target Subject was null");
        Constraint.isNotNull(control, "Control Subject was null");
        
        if (processConfirmation) {
            //TODO implement SubjectConfirmation support. Need registry of method URI -> SC matchers or similar.
            if (!target.getSubjectConfirmations().isEmpty()) {
                LOG.warn("Target Subject contains SubjectConfirmation, currently not supported and eval is skipped");
            }
        }
        
        if (target.getEncryptedID() != null || control.getEncryptedID() != null) {
            throw new IllegalArgumentException("Saw EncryptedID in Subject, matching not supported");
        }
        
        if (target.getBaseID() != null || control.getBaseID() != null) {
            throw new IllegalArgumentException("Saw BaseID in Subject, matching not supported");
        }
        
        final NameID targetNameID = target.getNameID();
        final NameID controlNameID = control.getNameID();
        
        if (targetNameID == null && controlNameID == null) {
            LOG.debug("Both target and control NameIDs are null, trivially match");
            return true;
        }
        if (targetNameID == null || controlNameID == null) {
            LOG.debug("One NameID is null ({}), the other is not, trivially do not match",
                    targetNameID == null ? "target" : "control");
            return false;
        }

        return areNameIDsEquivalent(targetNameID, controlNameID);
    }

}