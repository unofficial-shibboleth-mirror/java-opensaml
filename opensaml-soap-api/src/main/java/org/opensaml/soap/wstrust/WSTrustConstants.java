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

package org.opensaml.soap.wstrust;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * The WS-Trust 1.3 constants.
 * 
 * @see "WS-Trust 1.3 Specification"
 * 
 */
public final class WSTrustConstants {

    //
    // WS-Trust
    //
    
    /** WS-Trust version. */
    @Nonnull @NotEmpty public static final String WST_VERSION= "1.3";

    /** WS-Trust namespace prefix. */
    @Nonnull @NotEmpty public static final String WST_PREFIX= "wst";

    /** WS-Trust 1.3 namespace. */
    @Nonnull @NotEmpty public static final String WST_NS= "http://docs.oasis-open.org/ws-sx/ws-trust/200512";

    //
    // WS-Addressing
    //
    
    //* WS-Addressing RequestSecurityToken (RST) action URIs.
    
    /** WS-Addressing RequestSecurityToken (RST) action URI 'Issue'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RST_ISSUE= WST_NS + "/RST" + "/Issue";

    /** WS-Addressing RequestSecurityToken (RST) action URI 'Cancel'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RST_CANCEL= WST_NS + "/RST" + "/Cancel";

    /** WS-Addressing RequestSecurityToken (RST) action URI 'STSCancel'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RST_STSCANCEL= WST_NS + "/RST" + "/STSCancel";

    /** WS-Addressing RequestSecurityToken (RST) action URI 'Validate'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RST_VALIDATE= WST_NS + "/RST" + "/Validate";

    /** WS-Addressing RequestSecurityToken (RST) action URI 'Renew'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RST_RENEW= WST_NS + "/RST" + "/Renew";
    
    /** WS-Addressing RequestSecurityToken (RST) action URI 'KET'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RST_KET= WST_NS + "/RST" + "/KET";
    

    // WS-Addressing RequestSecurityTokenResponse (RSTR) action URIs.
    
    /** WS-Addressing RequestSecurityTokenResponse (RSTR) action URI 'Issue'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RSTR_ISSUE= WST_NS + "/RSTR" + "/Issue";

    /** WS-Addressing RequestSecurityTokenResponse (RSTR) action URI 'Cancel'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RSTR_CANCEL= WST_NS + "/RSTR" + "/Cancel";
    
    /** WS-Addressing RequestSecurityTokenResponse (RSTR) action URI 'CancelFinal'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RSTR_CANCEL_FINAL= WST_NS + "/RSTR" + "/CancelFinal";

    /** WS-Addressing RequestSecurityTokenResponse (RSTR) action URI 'Validate'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RSTR_VALIDATE= WST_NS + "/RSTR" + "/Validate";

    /** WS-Addressing RequestSecurityTokenResponse (RSTR) action URI 'ValidateFinal'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RSTR_VALIDATE_FINAL= WST_NS + "/RSTR" + "/ValidateFinal";

    /** WS-Addressing RequestSecurityTokenResponse (RSTR) action URI 'Renew'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RSTR_RENEW= WST_NS + "/RSTR" + "/Renew";
    
    /** WS-Addressing RequestSecurityTokenResponse (RSTR) action URI 'RenewFinal'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RSTR_RENEW_FINAL= WST_NS + "/RSTR" + "/RenewFinal";
    
    /** WS-Addressing RequestSecurityTokenResponse (RSTR) action URI 'KET'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RSTR_KET= WST_NS + "/RSTR" + "/KET";
    
    /** WS-Addressing RequestSecurityTokenResponse (RSTR) action URI 'KETFinal'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RSTR_KET_FINAL= WST_NS + "/RSTR" + "/KETFinal";
    

    // WS-Addressing RequestSecurityTokenResponseCollection (RSTRC) action URIs.
    
    /** WS-Addressing RequestSecurityTokenResponseCollection (RSTRC) action URI 'Issue'. */
    @Nonnull @NotEmpty public static final String WSA_ACTION_RSTRC_ISSUE_FINAL= WST_NS + "/RSTRC" + "/IssueFinal";
    
    
    // SOAP fault codes.
    
    /** WS-Trust SOAP fault code: "wst:InvalidRequest". */
    @Nonnull public static final QName SOAP_FAULT_INVALID_REQUEST =
        new QName(WST_NS, "InvalidRequest", WST_PREFIX);
    
    /** WS-Trust SOAP fault code: "wst:FailedAuthentication". */
    @Nonnull public static final QName SOAP_FAULT_FAILED_AUTHENTICATION = 
        new QName(WST_NS, "FailedAuthentication", WST_PREFIX);
    
    /** WS-Trust SOAP fault code: "wst:RequestFailed". */
    @Nonnull public static final QName SOAP_FAULT_REQUEST_FAILED = 
        new QName(WST_NS, "RequestFailed", WST_PREFIX);
    
    /** WS-Trust SOAP fault code: "wst:InvalidSecurityToken". */
    @Nonnull public static final QName SOAP_FAULT_INVALID_SECURITY_TOKEN = 
        new QName(WST_NS, "InvalidSecurityToken", WST_PREFIX);
    
    /** WS-Trust SOAP fault code: "wst:AuthenticationBadElements". */
    @Nonnull public static final QName SOAP_FAULT_AUTHENTICATION_BAD_ELEMENTS =
        new QName(WST_NS, "AuthenticationBadElements", WST_PREFIX);
    
    /** WS-Trust SOAP fault code: "wst:BadRequest". */
    @Nonnull public static final QName SOAP_FAULT_BAD_REQUEST =
        new QName(WST_NS, "BadRequest", WST_PREFIX);
    
    /** WS-Trust SOAP fault code: "wst:ExpiredData". */
    @Nonnull public static final QName SOAP_FAULT_EXPIRED_DATA = 
        new QName(WST_NS, "ExpiredData", WST_PREFIX);
    
    /** WS-Trust SOAP fault code: "wst:InvalidTimeRange". */
    @Nonnull public static final QName SOAP_FAULT_INVALID_TIME_RANGE =
        new QName(WST_NS, "InvalidTimeRange", WST_PREFIX);
    
    /** WS-Trust SOAP fault code: "wst:InvalidScope". */
    @Nonnull public static final QName SOAP_FAULT_INVALID_SCOPE =
        new QName(WST_NS, "InvalidScope", WST_PREFIX);
    
    /** WS-Trust SOAP fault code: "wst:RenewNeeded". */
    @Nonnull public static final QName SOAP_FAULT_RENEW_NEEDED =
        new QName(WST_NS, "RenewNeeded", WST_PREFIX);
    
    /** WS-Trust SOAP fault code: "wst:UnableToRenew". */
    @Nonnull public static final QName SOAP_FAULT_UNABLE_TO_RENEW =
        new QName(WST_NS, "UnableToRenew", WST_PREFIX);
    
    /** Constructor. Private to prevent instantiation. */
    private WSTrustConstants() { }

}
