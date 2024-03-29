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

package org.opensaml.messaging.context;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the BaseContext implementation.
 */
@Test
public class BaseContextTest {
    
    /**
     * Test the no-arg constructor.
     */
    public void testNoArgConstructor() {
        TestContext context = new TestContext();
        Assert.assertNull(context.getParent());
    }
    
    /**
     *  Test basic adding and removing of subcontexts.
     */
    public void testAddRemoveSubcontexts() {
        TestContext parent = new TestContext();
        TestContext child = new TestContext();
        
        Assert.assertNull(parent.getParent());
        Assert.assertNull(child.getParent());
        
        parent.addSubcontext(child);
        
        Assert.assertNull(parent.getParent());
        Assert.assertNotNull(child.getParent());
        Assert.assertTrue(child.getParent() == parent, "Parent of child is not the expected value");
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child, "Child of parent is not the expected value");
        
        // Here test removal by class.
        parent.removeSubcontext(TestContext.class);
        
        Assert.assertNull(parent.getParent());
        Assert.assertNull(child.getParent());
        
        parent.addSubcontext(child);
        
        Assert.assertNull(parent.getParent());
        Assert.assertNotNull(child.getParent());
        Assert.assertTrue(child.getParent() == parent, "Parent of child is not the expected value");
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child, "Child of parent is not the expected value");
        
        // Here test removal by instance.
        parent.removeSubcontext(child);
        
        Assert.assertNull(parent.getParent());
        Assert.assertNull(child.getParent());
    }
    
    /**
     *  Test clearing all subcontexts from the parent.
     */
    public void testClearSubcontexts() {
        TestContext parent = new TestContext();
        TestContext child = new TestContext();
        
        Assert.assertNull(parent.getParent());
        Assert.assertNull(child.getParent());
        
        parent.addSubcontext(child);
        
        Assert.assertNull(parent.getParent());
        Assert.assertNotNull(child.getParent());
        Assert.assertTrue(child.getParent() == parent, "Parent of child is not the expected value");
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child, "Child of parent is not the expected value");
        
        parent.clearSubcontexts();
        
        Assert.assertNull(parent.getParent());
        Assert.assertNull(child.getParent());
    }
    
    /**
     *  Test basic iteration of subcontexts.
     */
    public void testBasicIteration() {
        TestContext parent = new TestContext();
        TestContext child = new TestContext();
        
        Assert.assertNull(child.getParent());
        
        parent.addSubcontext(child);
        
        Assert.assertTrue(child.getParent() == parent, "Parent of child is not the expected value");
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child, "Child of parent is not the expected value");
        
        Iterator<BaseContext> iterator = parent.iterator();
        
        Assert.assertTrue(iterator.hasNext());
        BaseContext returnedContext = iterator.next();
        Assert.assertTrue(returnedContext == child);
        
        Assert.assertFalse(iterator.hasNext());
    }
    
    /**
     *  Test that calling remove() on the iterator throws the expected exception.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testNoRemoveIterator() {
        TestContext parent = new TestContext();
        Iterator<BaseContext> iterator = parent.iterator();
        iterator.remove();
    }
        
    /**
     *  Test case of attempting to add a duplicate subcontext class to a parent,
     *  when replace is in effect.
     */
    public void testDuplicateAddWithReplace() {
        TestContext parent = new TestContext();
        Assert.assertFalse(parent.containsSubcontext(TestContext.class));
        
        TestContext child1 = new TestContext();
        parent.addSubcontext(child1);
        Assert.assertTrue(parent.containsSubcontext(TestContext.class));
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child1);
        Assert.assertTrue(child1.getParent() == parent);
        
        TestContext child2 = new TestContext();
        parent.addSubcontext(child2, true);
        Assert.assertTrue(parent.containsSubcontext(TestContext.class));
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child2);
        Assert.assertTrue(child2.getParent() == parent);
        Assert.assertNull(child1.getParent());
    }
    
    /**
     *  Test case of attempting to add a duplicate subcontext class to a parent,
     *  when replace is not in effect.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDuplicateAddWithoutReplace() {
        TestContext parent = new TestContext();
        Assert.assertFalse(parent.containsSubcontext(TestContext.class));
        
        TestContext child1 = new TestContext();
        parent.addSubcontext(child1);
        Assert.assertTrue(parent.containsSubcontext(TestContext.class));
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child1);
        Assert.assertTrue(child1.getParent() == parent);
        
        TestContext child2 = new TestContext();
        parent.addSubcontext(child2, false);
    }
    
    /**
     *  Test adding a subcontext to a parent, and then adding it to another parent.
     */
    public void testAddWith2Parents() {
        TestContext parent1 = new TestContext();
        Assert.assertFalse(parent1.containsSubcontext(TestContext.class));
        
        TestContext child = new TestContext();
        parent1.addSubcontext(child);
        Assert.assertTrue(parent1.containsSubcontext(TestContext.class));
        Assert.assertTrue(parent1.getSubcontext(TestContext.class, false) == child);
        Assert.assertTrue(child.getParent() == parent1);
        
        TestContext parent2 = new TestContext();
        Assert.assertFalse(parent2.containsSubcontext(TestContext.class));
        parent2.addSubcontext(child);
        Assert.assertTrue(parent2.containsSubcontext(TestContext.class));
        Assert.assertTrue(parent2.getSubcontext(TestContext.class, false) == child);
        Assert.assertTrue(child.getParent() == parent2);
        Assert.assertFalse(parent1.containsSubcontext(TestContext.class));
        Assert.assertNull(parent1.getSubcontext(TestContext.class, false));
    }

    /**
     * Test accessing context by class name.
     */
    public void testStringAccess() {
        TestContext parent = new TestContext();
        parent.addSubcontext(new TestContext());
        
        BaseContext child = parent.getSubcontext("org.opensaml.messaging.context.TestContext");
        Assert.assertNotNull(child);
        Assert.assertTrue(child instanceof TestContext);
    }

    /**
     * Test accessing missing context by class name.
     */
    public void testStringAccessMissing() {
        TestContext parent = new TestContext();
        parent.addSubcontext(new TestContext());
        
        BaseContext child = parent.getSubcontext("org.opensaml.messaging.context.MessageContext");
        Assert.assertNull(child);
    }

    /**
     * Test accessing context by truncated class name.
     */
    public void testSimpleStringAccess() {
        TestContext parent = new TestContext();
        parent.addSubcontext(new TestContext());
        
        BaseContext child = parent.getSubcontext("TestContext");
        Assert.assertNotNull(child);
        Assert.assertTrue(child instanceof TestContext);
    }

    /**
     * Test accessing context using non-existent truncated class name.
     */
    @Test
    public void testSimpleStringError() {
        TestContext parent = new TestContext();
        parent.addSubcontext(new TestContext());
        
        // This threw in V4, is now returning null.
        Assert.assertNull(parent.getSubcontext("NoContext"));
    }
    
}