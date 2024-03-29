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

package org.opensaml.xacml.policy;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.xacml.XACMLConstants;

/** XACML AttributeSelector schema type. */
public interface AttributeSelectorType extends ExpressionType {

    /** Local name of the element AttributeSelector. */
    static final String DEFAULT_ELEMENT_LOCAL_NAME = "AttributeSelector";

    /** QName of the element AttributeSelector. */
    static final QName DEFAULT_ELEMENT_NAME =
            new QName(XACMLConstants.XACML20_NS, DEFAULT_ELEMENT_LOCAL_NAME, XACMLConstants.XACML_PREFIX);

    /** Local name of the XSI type. */
    static final String SCHEMA_TYPE_LOCAL_NAME = "AttributeSelectorType";

    /** QName of the XSI type. */
    static final QName SCHEMA_TYPE_NAME =
            new QName(XACMLConstants.XACML20_NS, SCHEMA_TYPE_LOCAL_NAME, XACMLConstants.XACML_PREFIX);

    /** RequestContextPath attribute name. */
    static final String REQUEST_CONTEXT_PATH_ATTRIB_NAME = "RequestContextPath";

    /** DataType attribute name. */
    static final String DATA_TYPE_ATTRIB_NAME = "DataType";

    /** MustBePresent attribute name. */
    static final String MUST_BE_PRESENT_ATTRIB_NAME = "MustBePresent";

    /**
     * Gets the request context path of the attribute to be selected.
     * 
     * @return request context path of the attribute to be selected
     */
    String getRequestContextPath();

    /**
     * Sets the request context path of the attribute to be selected.
     * 
     * @param path
     *                request context path of the attribute to be selected
     */
    void setRequestContextPath(String path);

    /**
     * Gets the data type of the attribute to be selected.
     * 
     * @return data type of the attribute to be selected
     */
    String getDataType();

    /**
     * Sets the data type of the attribute to be selected.
     * 
     * @param type
     *                data type of the attribute to be selected
     */
    void setDataType(String type);

    /**
     * Gets whether the attribute to be selected must be present.
     * 
     * @return whether the attribute to be selected must be present
     */
    Boolean getMustBePresent();

    /**
     * Gets whether the attribute to be selected must be present.
     * 
     * @return whether the attribute to be selected must be present
     */
    XSBooleanValue getMustBePresentXSBoolean();

    /**
     * Sets whether the attribute to be selected must be present.
     * 
     * @param present
     *                whether the attribute to be selected must be present
     */
    void setMustBePresent(Boolean present);

    /**
     * Sets whether the attribute to be selected must be present.
     * 
     * @param present
     *                whether the attribute to be selected must be present
     */
    void setMustBePresentXSBoolean(XSBooleanValue present);
}