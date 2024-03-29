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

package org.opensaml.profile.logic;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test for {@link MessageContextPredicateAdapter}.
 */
@SuppressWarnings("javadoc")
public class MessageContextPredicateAdapterTest {
    
    @Test
    public void testBasic() {
        MessageContext mc = new MessageContext();
        ProfileRequestContext prc = new ProfileRequestContext();
        
        MockPredicate wrapped = new MockPredicate();
        MessageContextPredicateAdapter adapter = new MessageContextPredicateAdapter(wrapped);
        
        mc.addSubcontext(new MockContext());
        prc.setOutboundMessageContext(mc);
        
        Assert.assertTrue(adapter.test(mc));
        
        mc.clearSubcontexts();
        Assert.assertFalse(adapter.test(mc));
        
        Assert.assertFalse(adapter.test(null));
        
        // No parent, unresolved PRC doesn't satisfy (default)
        mc = new MessageContext();
        mc.addSubcontext(new MockContext());
        Assert.assertFalse(adapter.test(mc));
        
        // No parent, unresolved PRC does satisfy
        adapter = new MessageContextPredicateAdapter(wrapped, true);
        Assert.assertTrue(adapter.test(mc));
    }
    
    // Helpers
    
    public static class MockContext extends BaseContext {
        public String value; 
    }
    
    public static class MockPredicate implements Predicate<ProfileRequestContext> {
        public boolean test(@Nullable ProfileRequestContext input) {
            if (input == null || input.getOutboundMessageContext() == null) {
                return false;
            }
            return input.ensureOutboundMessageContext().containsSubcontext(MockContext.class);
        }
    }
}
