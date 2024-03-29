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

import org.opensaml.xacml.XACMLConstants;
import org.opensaml.xacml.XACMLObject;

/** XACML CombinerParameter schema type. */
public interface CombinerParameterType extends XACMLObject {

    /** Local name of the element CombinerParameters. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "CombinerParameter";

    /** QName of the element CombinerParameters. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(XACMLConstants.XACML20_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            XACMLConstants.XACML_PREFIX);

    /** Local name of the XSI type. */
    public static final String SCHEMA_TYPE_LOCAL_NAME = "CombinerParameterType";

    /** QName of the XSI type. */
    public static final QName SCHEMA_TYPE_NAME = new QName(XACMLConstants.XACML20_NS, SCHEMA_TYPE_LOCAL_NAME,
            XACMLConstants.XACML_PREFIX);

    /** ParameterName attribute name. */
    public static final String PARAMETER_NAMEATTRIB_NAME = "ParameterName";

    /**
     * Gets the attribute value type for this parameter.
     * 
     * @return attribute value type for this parameter
     */
    public AttributeValueType getAttributeValue();

    /**
     * Sets the attribute value type for this parameter.
     * 
     * @param value attribute value type for this parameter
     */
    public void setAttributeValue(AttributeValueType value);

    /**
     * Gets the parameter name.
     * 
     * @return the parameter name
     */
    public String getParameterName();

    /**
     * Sets the parameter name.
     * 
     * @param name the parameter name
     */
    public void setParameterName(String name);
}