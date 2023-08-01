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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.Endpoint;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.logic.Constraint;

/**
 * Support for parsing a binding/endpoint pair into an endpoint of a particular type.
 * 
 * <p>The input format is a binding token, forward slash, and an endpoint. The endpoint MAY omit the
 * scheme, in which case 'https://' is prepended.</p>
 * 
 * <p>The SAML 2.0 bindings are represented by the tokens
 * "Redirect", "POST", "SimpleSign", "Artifact", "SOAP", and "PAOS".</p>
 * 
 * <p>The SAML 1.1 bindings are represented by the tokens
 * "Redirect1", "POST1", "Artifact1", and "SOAP1". The first token
 * is actually the proprietary Shibboleth request protocol.</p>
 * 
 * <p>The second input parameter is mutated to maintain the list of protocols applicable to the surrounding role.</p>
 * 
 * @param <T> endpoint type
 * 
 * @since 5.0.0
 */
public abstract class AbstractEndpointConverter<T extends Endpoint> implements BiFunction<String,List<String>,T> {

    /** Map of binding shortcuts to constants. */
    @Nonnull private static Map<String,Pair<String,String>> bindingMap;

    /** Object builder. */
    @Nonnull private final SAMLObjectBuilder<T> builder;
    
    /**
     * Constructor.
     *
     * @param theBuilder builder to use
     */
    public AbstractEndpointConverter(@Nonnull final SAMLObjectBuilder<T> theBuilder) {
        builder = Constraint.isNotNull(theBuilder, "Builder cannot be null");
    }
    
    /**
     * Process an endpoint expression into an absolute URL.
     * 
     * <p>For now, this merely detects the http schemes and if absent, adds the https scheme.</p>
     * 
     * @param protocols live list of protocol strings
     * @param input the argument
     * 
     * @return the endpoint object
     */
    @Nonnull protected T getProcessedEndpoint(@Nullable @Live final List<String> protocols,
            @Nullable final String input) {
        
        if (input == null) {
            throw new IllegalArgumentException("Argument was null");
        }
        
        final Pair<String,String> pair = getProtocolAndBinding(input);
        final String loc = getLocation(input);
        
        final T endpoint = builder.buildObject();
        
        if (loc.startsWith("https://") || loc.startsWith("http://")) {
            endpoint.setLocation(loc);
        } else {
            endpoint.setLocation("https://" + loc);
        }
        
        endpoint.setBinding(pair.getSecond());
        
        if (protocols != null) {
            protocols.add(pair.getFirst());
        }
        
        return endpoint;
    }
    
    /**
     * Parse out the binding shortcut and map to a protocol and binding constant.
     * 
     * @param input the argument
     * 
     * @return the mapped constant
     */
    @Nonnull protected Pair<String,String> getProtocolAndBinding(@Nonnull final String input) {
        final int sep = input.indexOf('/');
        if (sep == -1) {
            throw new IllegalArgumentException("No separator found in string.");
        }
        
        final Pair<String,String> binding;
        synchronized(bindingMap) {
            binding = bindingMap.get(input.substring(0, sep));
        }
        if (binding == null) {
            throw new IllegalArgumentException("Binding " + input.substring(0, sep) + " did not match a known value.");
        }
        
        return binding;
    }

    /**
     * Parse out the endpoint location.
     * 
     * @param input the argument
     * 
     * @return the endpoint location
     */
    @Nonnull protected String getLocation(@Nonnull final String input) {
        final int sep = input.indexOf('/');
        if (sep == -1 || input.length() == sep + 1) {
            throw new IllegalArgumentException("No separator found in string.");
        }
        
        return input.substring(sep + 1);
    }
    
    /**
     * Add a new mapping to the static set of protocol/binding mappings. 
     * 
     * @param token token used in strings converted into endpoints
     * @param protocol protocol support string for binding
     * @param binding binding constant
     */
    public static void addBinding(@Nonnull final String token, @Nonnull final String protocol,
            @Nonnull final String binding) {
        synchronized(bindingMap) {
            bindingMap.put(token, new Pair<>(protocol, binding));
        }
    }
    
    static {
        bindingMap = new HashMap<>();
        bindingMap.put("Redirect", new Pair<>(SAMLConstants.SAML20P_NS, SAMLConstants.SAML2_REDIRECT_BINDING_URI));
        bindingMap.put("POST", new Pair<>(SAMLConstants.SAML20P_NS, SAMLConstants.SAML2_POST_BINDING_URI));
        bindingMap.put("SimpleSign",
                new Pair<>(SAMLConstants.SAML20P_NS, SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI));
        bindingMap.put("Artifact", new Pair<>(SAMLConstants.SAML20P_NS, SAMLConstants.SAML2_ARTIFACT_BINDING_URI));
        bindingMap.put("SOAP", new Pair<>(SAMLConstants.SAML20P_NS, SAMLConstants.SAML2_SOAP11_BINDING_URI));
        bindingMap.put("PAOS", new Pair<>(SAMLConstants.SAML20P_NS, SAMLConstants.SAML2_PAOS_BINDING_URI));
        
        bindingMap.put("POST1", new Pair<>(SAMLConstants.SAML11P_NS, SAMLConstants.SAML1_POST_BINDING_URI));
        bindingMap.put("Artifact1", new Pair<>(SAMLConstants.SAML11P_NS, SAMLConstants.SAML1_ARTIFACT_BINDING_URI));
        bindingMap.put("SOAP1", new Pair<>(SAMLConstants.SAML11P_NS, SAMLConstants.SAML1_SOAP11_BINDING_URI));
    }

}