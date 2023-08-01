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

package org.opensaml.xacml.ctx;

import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.xacml.XACMLConstants;
import org.opensaml.xacml.XACMLObject;

/** XACML context Response schema type. */
public interface ResponseType extends XACMLObject {

    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Response";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(XACMLConstants.XACML20CTX_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, XACMLConstants.XACMLCONTEXT_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "ResponseType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME = new QName(XACMLConstants.XACML20CTX_NS, TYPE_LOCAL_NAME,
            XACMLConstants.XACMLCONTEXT_PREFIX);

    /**
     * Returns the list of results for the response.
     * 
     * @return the list of result for the response
     */
    public List<ResultType> getResults();
}