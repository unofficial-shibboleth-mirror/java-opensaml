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

package org.opensaml.saml.common.binding;

import org.opensaml.saml.saml2.metadata.Endpoint;

import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.Resolver;

/**
 * A resolver that is capable of resolving {@link Endpoint} instances
 * which meet certain supplied criteria.
 * 
 * At a minimum, an {@link EndpointResolver} implementation MUST support the following criteria:
 * <ul>
 * <li>{@link org.opensaml.saml.criterion.BindingCriterion}</li>
 * <li>{@link org.opensaml.saml.criterion.EndpointCriterion}</li>
 * <li>{@link org.opensaml.saml.criterion.RoleDescriptorCriterion}</li>
 * </ul>
 * 
 * @param <EndpointType> the type of endpoint to resolve
 */
public interface EndpointResolver<EndpointType extends Endpoint> extends Resolver<EndpointType, CriteriaSet> {
    
}