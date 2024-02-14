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

package org.opensaml.spring.httpclient;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.httpclient.HttpClientSecurityParameters;
import org.springframework.beans.factory.FactoryBean;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;

/**
 * Factory bean which accepts a list of {@link HttpClientSecurityParameters} and merges their properties together
 * to produce a single instance.
 * 
 * <p>
 * Order of precedence is: first parameters instance in the list with a non-null value for the given property wins.
 * </p>
 */
public class HttpClientSecurityParametersMergingFactoryBean implements FactoryBean<HttpClientSecurityParameters> {
    
    /** The list of input parameters to merge. */
    @Nonnull @NonnullElements
    private List<HttpClientSecurityParameters> parameters = CollectionSupport.emptyList();

    /** {@inheritDoc} */
    @Override
    public Class<?> getObjectType() {
        return HttpClientSecurityParameters.class;
    }

    /**
     * Get the list of input parameters to merge. 
     * 
     * @return returns the list of input parameters to be merged
     */
    @Nonnull @NonnullElements
    public List<HttpClientSecurityParameters> getParameters() {
        return parameters;
    }

    /**
     * Set the list of input parameters to merge.
     * 
     * @param input the list of input parameters to merge
     */
    public void setParameters(@Nonnull @NonnullElements final List<HttpClientSecurityParameters> input) {
        parameters = Constraint.isNotNull(input, "Input paramaters list may not be null");
    }

    /** {@inheritDoc} */
    @SuppressWarnings("deprecation")
    @Override
    @Nullable
    public HttpClientSecurityParameters getObject() throws Exception {
        if (parameters.isEmpty()) {
            return null;
        }
        
        if (parameters.size() == 1) {
            return parameters.get(0);
        }
        
        final HttpClientSecurityParameters result = new HttpClientSecurityParameters();
        
        result.setCredentialsProvider(parameters.stream().map(HttpClientSecurityParameters::getCredentialsProvider)
                .filter(Objects::nonNull).findFirst().orElse(null));
        
        result.setPreemptiveBasicAuthMap(parameters.stream()
                .map(HttpClientSecurityParameters::getPreemptiveBasicAuthMap)
                .filter(Objects::nonNull).findFirst().orElse(null));
        
        result.setAuthCache(parameters.stream().map(HttpClientSecurityParameters::getAuthCache)
                .filter(Objects::nonNull).findFirst().orElse(null));
        
        // Weirdly, the generics of the TrustEngine property cause an issue with use of a method reference here,
        // so use an equivalent lambda instead. 
        result.setTLSTrustEngine(parameters.stream().map(t -> t.getTLSTrustEngine())
                .filter(Objects::nonNull).findFirst().orElse(null));
        
        result.setTLSCriteriaSet(parameters.stream().map(HttpClientSecurityParameters::getTLSCriteriaSet)
                .filter(Objects::nonNull).findFirst().orElse(null));
        
        result.setTLSProtocols(parameters.stream().map(HttpClientSecurityParameters::getTLSProtocols)
                .filter(Objects::nonNull).findFirst().orElse(null));
        
        result.setTLSCipherSuites(parameters.stream().map(HttpClientSecurityParameters::getTLSCipherSuites)
                .filter(Objects::nonNull).findFirst().orElse(null));
        
        result.setHostnameVerifier(parameters.stream().map(HttpClientSecurityParameters::getHostnameVerifier)
                .filter(Objects::nonNull).findFirst().orElse(null));
        
        result.setClientTLSCredential(parameters.stream().map(HttpClientSecurityParameters::getClientTLSCredential)
                .filter(Objects::nonNull).findFirst().orElse(null));
        
        result.setServerTLSFailureFatal(parameters.stream().map(HttpClientSecurityParameters::isServerTLSFailureFatal)
                .filter(Objects::nonNull).findFirst().orElse(null));
        
        return result;
    }


}
