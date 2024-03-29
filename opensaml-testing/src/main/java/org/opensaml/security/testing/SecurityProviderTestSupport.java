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

package org.opensaml.security.testing;

import java.security.Security;

import javax.annotation.Nonnull;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Testing utility class which providers various support functionality related to security providers and Java version, 
 * useful for testing cryptographic components. 
 * 
 * <p>
 * One major feature is the ability to check and load the Bouncy Castle security provider for tests which require
 * advanced crypto capabilities, and unload it afterwards, if it wasn't loaded originally.
 * The goal of this is to preserve the pre-test condition of whether BC was originally loaded or not,
 * so we don't muck with the environment of JVM in which the tests are running.
 * For example, the JVM may actually have been configured with BC deliberately, 
 * so we don't want to unload it by mistake.
 * </p>
 */
public class SecurityProviderTestSupport {
    
    /** Name of Bouncy Castle JCE provider. */
    @Nonnull @NotEmpty public static final String BC_PROVIDER_NAME = "BC";
    
    /** Name of Sun Elliptic Curve JCE provider. */
    @Nonnull @NotEmpty public static final String SUNEC_PROVIDER_NAME = "SunEC";
    
    /** BC flag. */
    private boolean hadBCOriginally;
    
    /** Constructor. */
    public SecurityProviderTestSupport() {
        hadBCOriginally = Security.getProvider(BC_PROVIDER_NAME) != null;
    }
    
    /**
     *  Conditionally load the Bouncy Castle provider, if it isn't already loaded.
     */
    public void loadBC() {
        // Only load if isn't already loaded
        if (!haveBC()) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    
    /**
     *  Conditionally unload the Bouncy Castle provider, if it wasn't loaded originally (outside of this class),
     *  and if it is currently loaded.
     */
    public void unloadBC() {
        // Only unload if wasn't loaded originally
        if (!hadBCOriginally && haveBC()) {
            Security.removeProvider(BC_PROVIDER_NAME);
        }
    }
    
    /**
     * Return whether the Bouncy Castle provider is currently available.
     * 
     * @return true or false
     */
    public boolean haveBC() {
        return Security.getProvider(BC_PROVIDER_NAME) != null; 
    }
    
    /**
     * Return whether the SunEC provider is currently available.
     * 
     * @return true or false
     */
    public boolean haveSunEC() {
        return Security.getProvider(SUNEC_PROVIDER_NAME) != null; 
    }
    
    /**
     * Determine if we're running on OpenJDK.
     * @return true or false
     */
    public boolean isOpenJDK() {
        return System.getProperty("java.runtime.name", "").startsWith("OpenJDK")
                || System.getProperty("java.vm.name", "").startsWith("OpenJDK");
    }

}
