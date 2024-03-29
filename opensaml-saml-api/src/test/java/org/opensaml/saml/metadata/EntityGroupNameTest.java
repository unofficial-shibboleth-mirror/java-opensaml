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

package org.opensaml.saml.metadata;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.logic.ConstraintViolationException;

@SuppressWarnings("javadoc")
public class EntityGroupNameTest {
    
    @Test
    public void testConstructor() {
        EntityGroupName groupName = new EntityGroupName("foo");
        Assert.assertEquals(groupName.getName(), "foo");
        
        groupName = new EntityGroupName("    foo    ");
        Assert.assertEquals(groupName.getName(), "foo");
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testConstructorOnEmpty() {
        new EntityGroupName("            ");
    }
    
    @Test
    public void testHashCodeAndEquals() {
        EntityGroupName foo1 = new EntityGroupName("foo");
        EntityGroupName foo2 = new EntityGroupName("foo");
        EntityGroupName bar = new EntityGroupName("bar");
        
        Assert.assertTrue(foo1.equals(foo1));
        Assert.assertTrue(foo1.equals(foo2));
        Assert.assertFalse(foo1.equals(bar));
        
        Assert.assertTrue(foo1.hashCode() == foo2.hashCode());
    }

}
