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

package org.opensaml.security.x509;

/**
 * Options which may be supplied to influence the processing behavior of a {@link PKIXTrustEvaluator}.
 */
public class PKIXValidationOptions {

    /** Flag as to whether empty CRLs will be processed. */
    private boolean processEmptyCRLs;
    
    /** Flag as to whether expired CRLs will be processed. */
    private boolean processExpiredCRLs;
    
    /** Flag as to whether CRLs supplied in the untrusted credential being evaluated will be processed. */
    private boolean processCredentialCRLs;
    
    /** Default verification depth. */
    private int defaultVerificationDepth;

    /** Constructor. */
    public PKIXValidationOptions() {
        processEmptyCRLs = true;
        processExpiredCRLs = true;
        processCredentialCRLs = true;
        
        defaultVerificationDepth = 1;
    }

    /**
     * Whether empty CRLs should be processed.
     * 
     * <p>Default is: <b>true</b></p>
     * 
     * @return whether empty CRLs should be processed
     */
    public boolean isProcessEmptyCRLs() {
        return processEmptyCRLs;
    }

    /**
     * Whether empty CRLs should be processed.
     * 
     * <p>Default is: <b>true</b></p>
     * 
     * @param flag whether to process empty CRLs
     */
    public void setProcessEmptyCRLs(final boolean flag) {
        processEmptyCRLs = flag;
    }

    /**
     * Whether expired CRLs should be processed.
     * 
     * <p>Default is: <b>true</b></p>
     * 
     * @return whether expired CRLs should be processsed
     */
    public boolean isProcessExpiredCRLs() {
        return processExpiredCRLs;
    }

    /**
     * Whether expired CRLs should be processed.
     * 
     * <p>Default is: <b>true</b></p>
     * 
     * @param flag whether expired CRLs should be processed
     */
    public void setProcessExpiredCRLs(final boolean flag) {
        processExpiredCRLs = flag;
    }

    /**
     * Whether CRLs supplied within the untrusted {@link X509Credential} being evaluated should be processed.
     * 
     * <p>Default is: <b>true</b></p>
     * 
     * @return whether to process CRLs from an untrusted credential
     */
    public boolean isProcessCredentialCRLs() {
        return processCredentialCRLs;
    }

    /**
     * Whether CRLs supplied within the untrusted {@link X509Credential} being evaluated should be processed.
     * 
     * <p>Default is: <b>true</b></p>
     * 
     * @param flag whether to process CRLs from an untrusted credential
     */
    public void setProcessCredentialCRLs(final boolean flag) {
        processCredentialCRLs = flag;
    }

    /**
     * The default PKIX maximum path verification depth, if not supplied in the 
     * {@link PKIXValidationInformation} being evaluated.
     * 
     * <p>Default is: <b>1</b></p>
     * 
     * @return Returns the defaultVerificationDepth.
     */
    public int getDefaultVerificationDepth() {
        return defaultVerificationDepth;
    }

    /**
     * The default PKIX maximum path verification depth, if not supplied in the 
     * {@link PKIXValidationInformation} being evaluated.
     * 
     * <p>Default is: <b>1</b></p>
     * 
     * @param depth default verification depth to set
     */
    public void setDefaultVerificationDepth(final int depth) {
        defaultVerificationDepth = depth;
    }

}