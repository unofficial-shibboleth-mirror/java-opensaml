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

package org.opensaml.profile.action.impl;

import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.ConstraintViolationException;

@SuppressWarnings("javadoc")
public class SetProfileIdTest {

    @Test
    public void testInstantiation() {
        new SetProfileId("foo");

        try {
            new SetProfileId("  ");
            Assert.fail();
        } catch (final ConstraintViolationException e) {
            // expected this
        }
    }

    @Test
    public void testExecute() throws ComponentInitializationException {
        ProfileRequestContext context = new ProfileRequestContext();

        SetProfileId action = new SetProfileId("foo");
        action.initialize();
        action.execute(context);
        Assert.assertEquals(context.getProfileId(), "foo");
        Assert.assertNull(context.getSubcontext(EventContext.class));
    }
}