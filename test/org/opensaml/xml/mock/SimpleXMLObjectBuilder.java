/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */

package org.opensaml.xml.mock;

import org.opensaml.xml.AbstractXMLObjectBuilder;

/**
 * Builder of {@link org.opensaml.xml.mock.SimpleXMLObject}s.
 */
public class SimpleXMLObjectBuilder extends AbstractXMLObjectBuilder<SimpleXMLObject> {
    
    public SimpleXMLObject buildObject(){
        return buildObject(SimpleXMLObject.NAMESPACE, SimpleXMLObject.LOCAL_NAME, SimpleXMLObject.NAMESPACE_PREFIX);
    }

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(java.lang.String, java.lang.String, java.lang.String)
     */
    public SimpleXMLObject buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new SimpleXMLObject(namespaceURI, localName, namespacePrefix);
    }
}