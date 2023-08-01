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

package org.opensaml.xmlsec.impl;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;

@SuppressWarnings("javadoc")
public class IncludedAlgorithmsPredicateTest {

    @Test
    public void testBasic() {
        IncludedAlgorithmsPredicate predicate = new IncludedAlgorithmsPredicate(CollectionSupport.listOf("A", "B", "C", "D"));
        
        Assert.assertTrue(predicate.test("A"));
        Assert.assertTrue(predicate.test("B"));
        Assert.assertTrue(predicate.test("C"));
        Assert.assertTrue(predicate.test("D"));
        
        Assert.assertFalse(predicate.test("X"));
        Assert.assertFalse(predicate.test("Y"));
        Assert.assertFalse(predicate.test("Z"));
        Assert.assertFalse(predicate.test("foo"));
        Assert.assertFalse(predicate.test("bar"));
        Assert.assertFalse(predicate.test("bax"));
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testNullArg() {
        IncludedAlgorithmsPredicate predicate = new IncludedAlgorithmsPredicate(CollectionSupport.listOf("A", "B", "C", "D"));
        predicate.test(null);
    }
    
}