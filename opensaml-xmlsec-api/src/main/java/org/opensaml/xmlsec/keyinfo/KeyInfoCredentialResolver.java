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

package org.opensaml.xmlsec.keyinfo;

import org.opensaml.security.credential.CredentialResolver;

/**
 * Specialized {@link CredentialResolver} marker interface for resolvers which resolve
 * credentials based on a {@link org.opensaml.xmlsec.signature.KeyInfo} element.
 * 
 * Implementations will typically require an instance of {@link KeyInfoCriterion} within the
 * criteria set which is supplied as input to the resolve methods.
 */
public interface KeyInfoCredentialResolver extends CredentialResolver {

}