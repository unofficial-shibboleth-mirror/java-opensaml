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

package org.opensaml.xacml.policy.impl;

import org.opensaml.core.xml.schema.impl.XSStringImpl;
import org.opensaml.xacml.policy.IdReferenceType;

/**
 * Implementation of {@link IdReferenceType}.
 */
public class IdReferenceTypeImpl extends XSStringImpl implements IdReferenceType {

    /**Value of the earliest version.*/
    private String earliestVersion;
    
    /**Value of the latest version.*/
    private String latestVersion;
    
    /**Value of this version.*/
    private String version;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected IdReferenceTypeImpl(final String namespaceURI, final String elementLocalName,
            final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    
    /** {@inheritDoc} */
    public String getEarliestVersion() {
        return earliestVersion;
    }

    /** {@inheritDoc} */
    public String getLatestVersion() {
        return latestVersion;
    }

    /** {@inheritDoc} */
    public String getVersion() {
        return version;
    }

    /** {@inheritDoc} */
    public void setEarliestVersion(final String newEarliestVersion) {
        this.earliestVersion = prepareForAssignment(this.earliestVersion,newEarliestVersion);
    }

    /** {@inheritDoc} */
    public void setLatestVersion(final String newLastestVersion) {
        this.latestVersion = prepareForAssignment(this.latestVersion,newLastestVersion);
    }

    /** {@inheritDoc} */
    public void setVersion(final String newVersion) {
       this.version = prepareForAssignment(this.version,newVersion);
    }

}
