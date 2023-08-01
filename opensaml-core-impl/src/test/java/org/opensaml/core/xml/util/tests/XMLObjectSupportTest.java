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

package org.opensaml.core.xml.util.tests;

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.XMLRuntimeException;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.core.xml.util.XMLObjectSupport.CloneOutputOption;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests of XMLObjectHelper utility methods.
 */
public class XMLObjectSupportTest extends XMLObjectBaseTestCase {

    /** Tests cloning an XMLObject. */
    @Test
    public void testXMLObjectCloneWithDropDOM() {
        final XMLObjectBuilder<SimpleXMLObject> sxoBuilder =
                (XMLObjectBuilder<SimpleXMLObject>) builderFactory.<SimpleXMLObject>ensureBuilder(simpleXMLObjectQName);
        final SimpleXMLObject origChildObj = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME);

        origChildObj.setValue("FooBarBaz");
        
        final SimpleXMLObject origParentObj = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME);
        origParentObj.getSimpleXMLObjects().add(origChildObj);
        
        SimpleXMLObject clonedParentObj = null;
        try {
            clonedParentObj = XMLObjectSupport.cloneXMLObject(origParentObj, CloneOutputOption.DropDOM);
        } catch (MarshallingException e) {
            Assert.fail("Object cloning failed on marshalling: " + e.getMessage());
        } catch (UnmarshallingException e) {
            Assert.fail("Object cloning failed on unmarshalling: " + e.getMessage());
        }
        
        Assert.assertFalse(origParentObj == clonedParentObj, "Parent XMLObjects were the same reference");
        assert clonedParentObj != null;
        Assert.assertNull(clonedParentObj.getDOM(), "Cloned parent DOM node was not null");
        
        Assert.assertFalse(clonedParentObj.getSimpleXMLObjects().isEmpty(), "Cloned parent had no children");
        SimpleXMLObject clonedChildObj = clonedParentObj.getSimpleXMLObjects().get(0);
        
        Assert.assertFalse(origChildObj == clonedChildObj, "Child XMLObjects were the same reference");
        Assert.assertNull(clonedChildObj.getDOM(), "Cloned child DOM node was not null");
        
        Assert.assertEquals(clonedChildObj.getValue(), "FooBarBaz", "Text content of child was not the expected value");
    }
    
    /** Tests cloning an XMLObject. */
    @Test
    public void testXMLObjectCloneWithUnrootedDOM() {
        final XMLObjectBuilder<SimpleXMLObject> sxoBuilder =
                (XMLObjectBuilder<SimpleXMLObject>) builderFactory.<SimpleXMLObject>ensureBuilder(simpleXMLObjectQName);
        
        final SimpleXMLObject origChildObj = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME);
        origChildObj.setValue("FooBarBaz");
        
        final SimpleXMLObject origParentObj = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME);
        origParentObj.getSimpleXMLObjects().add(origChildObj);
        
        SimpleXMLObject clonedParentObj = null;
        try {
            clonedParentObj = XMLObjectSupport.cloneXMLObject(origParentObj, CloneOutputOption.UnrootedDOM);
        } catch (MarshallingException e) {
            Assert.fail("Object cloning failed on marshalling: " + e.getMessage());
        } catch (UnmarshallingException e) {
            Assert.fail("Object cloning failed on unmarshalling: " + e.getMessage());
        }
        
        final Element preCloneElement = origParentObj.getDOM();
        assert preCloneElement != null;
        assert clonedParentObj != null;
        
        Assert.assertFalse(origParentObj == clonedParentObj, "Parent XMLObjects were the same reference");
        Assert.assertNotNull(clonedParentObj.getDOM(), "Cloned parent DOM node was null");
        Assert.assertFalse(preCloneElement.isSameNode(clonedParentObj.getDOM()),
                "Parent DOM node was not cloned properly");
        
        Assert.assertFalse(clonedParentObj.getSimpleXMLObjects().isEmpty(), "Cloned parent had no children");
        SimpleXMLObject clonedChildObj = clonedParentObj.getSimpleXMLObjects().get(0);
        
        Assert.assertFalse(origChildObj == clonedChildObj, "Child XMLObjects were the same reference");
        Assert.assertNotNull(clonedChildObj.getDOM(), "Cloned child DOM node was null");
        Assert.assertFalse(preCloneElement.isSameNode(clonedChildObj.getDOM()),
                "Child DOM node was not cloned properly");
        
        Assert.assertEquals(clonedChildObj.getValue(), "FooBarBaz", "Text content of child was not the expected value");
    }
    
    /** Tests cloning an XMLObject. */
    @Test
    public void testXMLObjectCloneWithRootInNewDocument() {
        final XMLObjectBuilder<SimpleXMLObject> sxoBuilder =
                (XMLObjectBuilder<SimpleXMLObject>) builderFactory.<SimpleXMLObject>ensureBuilder(simpleXMLObjectQName);
        
        final SimpleXMLObject origChildObj = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME);
        origChildObj.setValue("FooBarBaz");
        
        final SimpleXMLObject origParentObj = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME);
        origParentObj.getSimpleXMLObjects().add(origChildObj);
        
        SimpleXMLObject clonedParentObj = null;
        try {
            clonedParentObj = XMLObjectSupport.cloneXMLObject(origParentObj, CloneOutputOption.RootDOMInNewDocument);
        } catch (MarshallingException e) {
            Assert.fail("Object cloning failed on marshalling: " + e.getMessage());
        } catch (UnmarshallingException e) {
            Assert.fail("Object cloning failed on unmarshalling: " + e.getMessage());
        }
        
        final Element preCloneElement = origParentObj.getDOM();
        assert preCloneElement != null;
        
        assert clonedParentObj != null;
        final Element clonedElement = clonedParentObj.getDOM();
        assert clonedElement != null;
        
        Assert.assertFalse(origParentObj == clonedParentObj, "Parent XMLObjects were the same reference");
        Assert.assertNotNull(clonedParentObj.getDOM(), "Cloned parent DOM node was null");
        Assert.assertFalse(preCloneElement.isSameNode(clonedParentObj.getDOM()),
                "Parent DOM node was not cloned properly");
        
        Assert.assertFalse(clonedParentObj.getSimpleXMLObjects().isEmpty(), "Cloned parent had no children");
        SimpleXMLObject clonedChildObj = clonedParentObj.getSimpleXMLObjects().get(0);
        
        Assert.assertFalse(origChildObj == clonedChildObj, "Child XMLObjects were the same reference");
        Assert.assertNotNull(clonedChildObj.getDOM(), "Cloned child DOM node was null");
        Assert.assertFalse(preCloneElement.isSameNode(clonedChildObj.getDOM()),
                "Child DOM node was not cloned properly");
        
        Assert.assertEquals(clonedChildObj.getValue(), "FooBarBaz", "Text content of child was not the expected value");
        
        // Test rootInNewDocument requirements
        Assert.assertFalse(preCloneElement.getOwnerDocument().isSameNode(clonedElement.getOwnerDocument()), 
                "Cloned objects DOM's were owned by the same Document");
        Assert.assertTrue(clonedElement.getOwnerDocument().getDocumentElement().isSameNode(clonedParentObj.getDOM()), 
                "Cloned object was not the new Document root");
    }
    
    @Test
    public void testXMLObjectCloneInputMarshalling() throws MarshallingException, UnmarshallingException {
        final XMLObjectBuilder<SimpleXMLObject> sxoBuilder =
                (XMLObjectBuilder<SimpleXMLObject>) builderFactory.<SimpleXMLObject>ensureBuilder(simpleXMLObjectQName);
            
            final SimpleXMLObject origChildObj = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME);
            origChildObj.setValue("FooBarBaz");
            
            final SimpleXMLObject origParentObj = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME);
            origParentObj.getSimpleXMLObjects().add(origChildObj);
            
            Assert.assertNull(origParentObj.getDOM());
            
            SimpleXMLObject clonedParentObj = XMLObjectSupport.cloneXMLObject(origParentObj, CloneOutputOption.DropDOM);
            Assert.assertNotNull(clonedParentObj);
            
            final Element preCloneElement = origParentObj.getDOM();
            assert preCloneElement != null;
            final Document preCloneDocument = preCloneElement.getOwnerDocument();
            
            clonedParentObj = XMLObjectSupport.cloneXMLObject(origParentObj, CloneOutputOption.DropDOM);
            Assert.assertNotNull(clonedParentObj);
            
            Assert.assertNotNull(origParentObj.getDOM());
            Assert.assertTrue(preCloneElement.isSameNode(origParentObj.getDOM()));
            Assert.assertTrue(preCloneDocument.isSameNode(preCloneElement.getOwnerDocument()));
    }
    
    @Test
    public void testBuildXMLObject() {
        try {
            XMLObjectSupport.buildXMLObject(simpleXMLObjectQName);
        } catch (Exception e) {
            Assert.fail("Expected XMLObject could not be built");
        }
        
        try {
            XMLObjectSupport.buildXMLObject(simpleXMLObjectQName, XSString.TYPE_NAME);
        } catch (Exception e) {
            Assert.fail("Expected XMLObject could not be built");
        }
        
        try {
            XMLObjectSupport.buildXMLObject(new QName("urn:test:bogus", "foo"));
            Assert.fail("buildXMLObject did not throw as expected");
        } catch (XMLRuntimeException e) {
            // expected
        }
        
        try {
            XMLObjectSupport.buildXMLObject(new QName("urn:test:bogus", "foo"), XSString.TYPE_NAME);
            Assert.fail("buildXMLObject did not throw as expected");
        } catch (XMLRuntimeException e) {
            // expected
        }
    }

}