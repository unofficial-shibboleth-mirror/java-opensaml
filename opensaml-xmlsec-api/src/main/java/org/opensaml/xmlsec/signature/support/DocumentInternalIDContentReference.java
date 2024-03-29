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

package org.opensaml.xmlsec.signature.support;

import javax.annotation.Nonnull;


/**
 * A content reference that references Elements withing the same document by ID attribute. That is the reference is
 * <code>#ID</code> where ID is the value of the ID attribute of the Element.
 */
public class DocumentInternalIDContentReference extends URIContentReference {

    /**
     * Constructor. The anchor designator (#) must not be included in the ID.
     * 
     * @param referenceID the reference ID of the element to be signed
     */
    public DocumentInternalIDContentReference(@Nonnull final String referenceID) {        
        super("#" + referenceID);
    }
}