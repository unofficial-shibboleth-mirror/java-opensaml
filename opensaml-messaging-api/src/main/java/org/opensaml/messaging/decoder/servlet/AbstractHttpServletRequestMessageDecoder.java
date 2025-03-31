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

package org.opensaml.messaging.decoder.servlet;

import javax.annotation.Nullable;

import org.opensaml.messaging.decoder.AbstractMessageDecoder;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.NonnullSupplier;

/**
 * Abstract implementation of {@link HttpServletRequestMessageDecoder}.
 */
public abstract class AbstractHttpServletRequestMessageDecoder extends AbstractMessageDecoder
        implements HttpServletRequestMessageDecoder {

    /** Flag for whether to check for servlet request during init. */
    private boolean checkDuringInit;
    
    /** Current HTTP request, if available. */
    @NonnullAfterInit private NonnullSupplier<HttpServletRequest> httpServletRequestSupplier;

    /** Constructor. */
    public AbstractHttpServletRequestMessageDecoder() {
        checkDuringInit = true;
    }
    
    /**
     * Get whether {{@link #initialize()} should throw an exception if {@link #getHttpServletRequest()}
     * returns null.
     * 
     * @return whether a null request should fail initialization
     * 
     * @since 5.2.0
     */
    public boolean isCheckDuringInit() {
        return checkDuringInit;
    }
    
    /**
     * Set whether {{@link #initialize()} should throw an exception if {@link #getHttpServletRequest()}
     * returns null.
     * 
     * <p>Defaults to true.</p>
     * 
     * @param flag flag to set
     * 
     * @since 5.2.0
     */
    public void setCheckDuringInit(final boolean flag) {
        checkSetterPreconditions();
        
        checkDuringInit = flag;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>The return annotation is valid only in the default state, when {@link #isCheckDuringInit()} is true.</p>
     */
    @NonnullAfterInit public HttpServletRequest getHttpServletRequest() {
        if (httpServletRequestSupplier != null) {
            return httpServletRequestSupplier.get();
        }
        
        return null;
    }

    /**
     * Get the supplier for HTTP request if available.
     * 
     * <p>The return annotation is valid only in the default state, when {@link #isCheckDuringInit()} is true.</p>
     * 
     * @return current HTTP request
     */
    @NonnullAfterInit public NonnullSupplier<HttpServletRequest> getHttpServletRequestSupplier() {
        return httpServletRequestSupplier;
    }

    /** {@inheritDoc} */
    @Override
    public void setHttpServletRequestSupplier(@Nullable final NonnullSupplier<HttpServletRequest> requestSupplier) {
        checkSetterPreconditions();

        httpServletRequestSupplier = requestSupplier;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (isCheckDuringInit() && getHttpServletRequest() == null) {
            throw new ComponentInitializationException("HTTP Servlet request cannot be null");
        }
    }

}