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

package org.opensaml.saml.saml2.common;

import java.time.Duration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * A functional interface for SAMLElements that provide cache duration information.
 *
 */
public interface CacheableSAMLObject extends SAMLObject{

    /** "cacheDuration" attribute name. */
    @Nonnull @NotEmpty static final String CACHE_DURATION_ATTRIB_NAME = "cacheDuration";

    /** "cacheDuration" attribute QName. */
    @Nonnull static final QName CACHE_DURATION_ATTRIB_QNAME =
            new QName(null, "cacheDuration", XMLConstants.DEFAULT_NS_PREFIX);
    
    /**
     * Gets the maximum time that this descriptor should be cached.
     *  
     * @return the maximum time that this descriptor should be cached
     */
    @Nullable Duration getCacheDuration();

    /**
     * Sets the maximum time that this descriptor should be cached.
     * 
     * @param duration the maximum time that this descriptor should be cached
     */
    void setCacheDuration(@Nullable final Duration duration);

}