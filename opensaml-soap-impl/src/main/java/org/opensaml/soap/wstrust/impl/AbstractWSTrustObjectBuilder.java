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

package org.opensaml.soap.wstrust.impl;


import javax.annotation.Nonnull;

import org.opensaml.core.xml.AbstractXMLObjectBuilder;
import org.opensaml.soap.wstrust.WSTrustObject;
import org.opensaml.soap.wstrust.WSTrustObjectBuilder;

/**
 * AbstractWSTrustObjectBuilder.
 * 
 * @param <WSTrustObjectType> the type of WSTrustObject being built
 * 
 */
public abstract class AbstractWSTrustObjectBuilder<WSTrustObjectType extends WSTrustObject> 
        extends AbstractXMLObjectBuilder<WSTrustObjectType> 
        implements WSTrustObjectBuilder<WSTrustObjectType> {

    /** {@inheritDoc} */
    @Nonnull public abstract WSTrustObjectType buildObject();

}