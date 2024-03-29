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

package org.opensaml.messaging.handler.impl;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.testng.annotations.Test;

import net.shibboleth.shared.logic.FunctionSupport;

/** Unit test for {@link CheckMandatoryAuthentication}. */
@SuppressWarnings("javadoc")
public class CheckMandatoryAuthenticationTest {

    @Test public void testAuthenticated() throws Exception {
        final CheckMandatoryAuthentication action = new CheckMandatoryAuthentication();
        action.setAuthenticationLookupStrategy(FunctionSupport.constant(true));
        action.initialize();

        final MessageContext mc = new MessageContext();
        action.invoke(mc);
    }

    @Test(expectedExceptions=MessageHandlerException.class) public void testNotAuthenticated() throws Exception {
        final CheckMandatoryAuthentication action = new CheckMandatoryAuthentication();
        action.setAuthenticationLookupStrategy(FunctionSupport.constant(false));
        action.initialize();

        final MessageContext mc = new MessageContext();
        action.invoke(mc);
    }

}