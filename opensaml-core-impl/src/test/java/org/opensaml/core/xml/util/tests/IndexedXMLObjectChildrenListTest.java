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

import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.mock.SimpleXMLObjectBuilder;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;

/**
 * Test case for {@link org.opensaml.core.xml.util.IndexedXMLObjectChildrenList}. Note that this test only tests those
 * methods that modify the list because everything else is delegated to the
 * {@link org.opensaml.core.xml.util.XMLObjectChildrenList} which has it's own test cases that works all the other methods.
 * 
 */
public class IndexedXMLObjectChildrenListTest {

    private QName type1 = new QName("example.org/ns/type1", "Type1");

    private QName type2 = new QName("example.org/ns/type2", "Type2");

    private SimpleXMLObjectBuilder sxoBuilder = new SimpleXMLObjectBuilder();

    /**
     * Test the add method to make sure it creates the index correctly.
     */
    @Test
    public void testAdd() {
        final SimpleXMLObject parentObject = sxoBuilder.buildObject();
        final IndexedXMLObjectChildrenList<SimpleXMLObject> indexedList = new IndexedXMLObjectChildrenList<>(
                parentObject);

        final SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);
        Assert.assertEquals(indexedList.get(
                child1.getElementQName()).size(), 1, "List gotten by element QName index should have had 1 element");
        final QName type = child1.getSchemaType();
        assert type != null;
        Assert.assertEquals(indexedList.get(type).size(), 1, "List gotten by type QName index should have had 1 element");

        final SimpleXMLObject child2 = sxoBuilder.buildObject();
        indexedList.add(child2);
        Assert.assertEquals(indexedList.get(
                child1.getElementQName()).size(), 2, "List gotten by element QName index should have had 1 element");
        Assert.assertEquals(indexedList.get(type).size(), 1, "List gotten by type QName index should have had 1 element");
    }

    /**
     * Test the set method to make sure it removes items that have been replaced from the index.
     */
    @Test
    public void testSet() {
        final SimpleXMLObject parentObject = sxoBuilder.buildObject();
        final IndexedXMLObjectChildrenList<SimpleXMLObject> indexedList = new IndexedXMLObjectChildrenList<>(
                parentObject);

        final SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);

        final SimpleXMLObject child2 = sxoBuilder.buildObject();
        indexedList.set(0, child2);

        final QName type = child1.getSchemaType();
        assert type != null;

        Assert.assertEquals(indexedList.get(
                child1.getElementQName()).size(), 1, "List gotten by element QName index should have had 1 element");
        Assert.assertTrue(indexedList.get(type).isEmpty(), "List gotten by type QName index should have been empty");
    }

    /**
     * Test to ensure removed items are removed from the index.
     */
    @Test
    public void testRemove() {
        final SimpleXMLObject parentObject = sxoBuilder.buildObject();
        final IndexedXMLObjectChildrenList<SimpleXMLObject> indexedList = new IndexedXMLObjectChildrenList<>(
                parentObject);

        final SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);

        final SimpleXMLObject child2 = sxoBuilder.buildObject();
        indexedList.add(child2);

        final QName type = child1.getSchemaType();
        assert type != null;

        indexedList.remove(child1);
        Assert.assertEquals(indexedList.get(
                child1.getElementQName()).size(), 1, "List gotten by element QName index should have had 1 element");
        Assert.assertTrue(indexedList.get(type).isEmpty(), "List gotten by type QName index should have been empty");
    }

    /**
     * Tests the sublist functionality.
     */
    @Test
    public void testSublist() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        IndexedXMLObjectChildrenList<XMLObject> indexedList = new IndexedXMLObjectChildrenList<>(parentObject);

        SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);

        SimpleXMLObject child2 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type2);
        indexedList.add(child2);

        SimpleXMLObject child3 = sxoBuilder.buildObject();
        indexedList.add(child3);

        SimpleXMLObject child4 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type2);
        indexedList.add(child4);

        SimpleXMLObject child5 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child5);

        SimpleXMLObject child6 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child6);

        List<SimpleXMLObject> elementNameSublist = (List<SimpleXMLObject>) indexedList
                .subList(child1.getElementQName());
        List<SimpleXMLObject> type1SchemaSublist = (List<SimpleXMLObject>) indexedList.subList(type1);
        List<SimpleXMLObject> type2SchemaSublist = (List<SimpleXMLObject>) indexedList.subList(type2);

        Assert.assertEquals(elementNameSublist
                .size(), 6, "Element name index sublist did not have expected number of elements");
        Assert.assertEquals(type1SchemaSublist
                .size(), 3, "Schema Type1 index sublist did not have expected number of elements");
        Assert.assertEquals(type2SchemaSublist
                .size(), 2, "Schema Type2 index sublist did not have expected number of elements");

        elementNameSublist.clear();
        Assert.assertEquals(elementNameSublist
                .size(), 0, "Element name index sublist did not have expected number of elements");
        
        SimpleXMLObject child7 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        type1SchemaSublist.add(child7);
        Assert.assertTrue(type1SchemaSublist.contains(child7));
        Assert.assertTrue(indexedList.contains(child7));

        type1SchemaSublist.remove(child7);
        Assert.assertFalse(type1SchemaSublist.contains(child7));
        Assert.assertFalse(indexedList.contains(child7));

        try {
            type1SchemaSublist.set(0, child7);
            Assert.fail("Unsupported set operation did not throw proper exception");
        } catch (UnsupportedOperationException e) {

        }

        try {
            type1SchemaSublist.remove(0);
            Assert.fail("Unsupported remove operation did not throw proper exception");
        } catch (UnsupportedOperationException e) {

        }
    }

    /**
     *  Test sublist  indexOf method.
     */
    public void testSublistIndexOf() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        IndexedXMLObjectChildrenList<XMLObject> indexedList = new IndexedXMLObjectChildrenList<>(parentObject);

        SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);

        SimpleXMLObject child2 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type2);
        indexedList.add(child2);
        
        List<SimpleXMLObject> sublist = (List<SimpleXMLObject>) indexedList.subList(type2);
        Assert.assertTrue(child2 == sublist.get(sublist.indexOf(child2)));
    }
    
    /**
     *  Test sublist  lastIndexOf method.
     */
    public void testSublistLastIndexOf() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        IndexedXMLObjectChildrenList<XMLObject> indexedList = new IndexedXMLObjectChildrenList<>(parentObject);

        SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);

        SimpleXMLObject child2 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type2);
        indexedList.add(child2);
        
        SimpleXMLObject child3 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child3);
        
        List<SimpleXMLObject> sublist = (List<SimpleXMLObject>) indexedList.subList(type1);
        Assert.assertTrue(child3 == sublist.get(sublist.lastIndexOf(child3)));
    }
    
    public void testSublistClear() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        IndexedXMLObjectChildrenList<XMLObject> indexedList = new IndexedXMLObjectChildrenList<>(parentObject);

        SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);

        SimpleXMLObject child2 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type2);
        indexedList.add(child2);
        
        SimpleXMLObject child3 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child3);
        
        SimpleXMLObject child4 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child4);
        
        Assert.assertEquals(4, indexedList.size());
        
        List<SimpleXMLObject> sublist = (List<SimpleXMLObject>) indexedList.subList(type1);
        Assert.assertEquals(3, sublist.size());
        
        sublist.clear();
        Assert.assertEquals(0, sublist.size());
        Assert.assertEquals(1, indexedList.size());
        Assert.assertTrue(indexedList.contains(child2));
    }
}