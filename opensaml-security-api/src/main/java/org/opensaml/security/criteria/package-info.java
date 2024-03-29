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

/**
 * Classes which model lookup criteria used as input to a
 * {@link net.shibboleth.shared.resolver.Resolver}.
 * Criteria are typically used by resolvers in a resolver-specific manner to either lookup or extract
 * information from a source, or to constrain or filter the type of information that will be returned.
 * 
 * <p>This package provides some implementations of {@link net.shibboleth.shared.resolver.Criterion}
 * which may have general applicability throughout the library. Criterion implementations which are more
 * specialized in nature may be found in other packages, such as {@link org.opensaml.security.x509}.</p>
 */
@NonnullElements
package org.opensaml.security.criteria;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
