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

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.servlet.HttpServletRequestValidator;

/**
 * Unit test for {@link HttpServletRequestValidationHandler}.
 */
public class HttpServletRequestValidationHandlerTest {
    
    @Test(expectedExceptions = ComponentInitializationException.class)
    public void nullValidator() throws Exception {
        HttpServletRequestValidationHandler handler = new HttpServletRequestValidationHandler();
        handler.initialize();
    }
    
    @Test
    public void valid() throws Exception {
        HttpServletRequestValidationHandler handler = new HttpServletRequestValidationHandler();
        handler.setValidator(new MockValidator(true));
        handler.setHttpServletRequestSupplier(() -> new MockHttpServletRequest());
        handler.initialize();
        
        handler.invoke(new MessageContext());
    }

    @Test(expectedExceptions = MessageHandlerException.class)
    public void invalid() throws Exception {
        HttpServletRequestValidationHandler handler = new HttpServletRequestValidationHandler();
        handler.setValidator(new MockValidator(false));
        handler.setHttpServletRequestSupplier(() -> new MockHttpServletRequest());
        handler.initialize();
        
        handler.invoke(new MessageContext());
    }

    public class MockValidator implements HttpServletRequestValidator {
        
        private boolean valid;

        public MockValidator(boolean result) {
            valid = result ;
        }

        /** {@inheritDoc} */
        @Override
        public void validate(@Nonnull HttpServletRequest request) throws ServletException {
            if (!valid) {
                throw new ServletException("Request was invalid");
            }
            
        }
        
    }
}
