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

package org.opensaml.soap.wssecurity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface for element having a <code>@wsse:Usage</code> attribute.
 */
public interface UsageBearing {
    
    /** The wsse:@Usage attribute local name. */
    @Nonnull @NotEmpty public static final String WSSE_USAGE_ATTR_LOCAL_NAME = "Usage";

    /** The wsse:@Usage qualified attribute name. */
    @Nonnull public static final QName WSSE_USAGE_ATTR_NAME =
        new QName(WSSecurityConstants.WSSE_NS, WSSE_USAGE_ATTR_LOCAL_NAME, WSSecurityConstants.WSSE_PREFIX);

    /**
     * Returns the list of <code>wsse:@Usage</code> attributes value.
     * 
     * @return the list of attribute values
     */
    @Nullable public List<String> getWSSEUsages();

    /**
     * Sets the list of <code>wsse:@Usage</code> attributes value.
     * 
     * @param usages the list of attribute values
     */
    public void setWSSEUsages(@Nullable final List<String> usages);
    
}
