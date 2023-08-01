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

package org.opensaml.saml.common.messaging.context;


/**
 * Subcontext that carries information about the SAML "presenter" entity, as defined in 
 * SAML Core, section 3.4.
 * 
 * <p>
 * This context will often contain subcontexts, whose data is construed to be scoped to that presenter entity.
 * </p>
 * 
 * @deprecated
 */
@Deprecated(forRemoval=true, since="5.0.0")
public final class SAMLPresenterEntityContext extends AbstractAuthenticatableSAMLEntityContext {

}