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

package org.opensaml.soap.common;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObjectBuilder;

/**
 * Builder for SOAPObjects.
 *
 * @param <SOAPObjectType> the type of SOAPObject being built
 */
public interface SOAPObjectBuilder<SOAPObjectType extends SOAPObject> extends XMLObjectBuilder<SOAPObjectType> {

    /**
     * Builds a SOAPObject using the default name and namespace information provided SOAP specifications.
     * 
     * @return built SAMLObject
     */
    @Nonnull public SOAPObjectType buildObject();

}