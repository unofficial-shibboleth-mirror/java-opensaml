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

package org.opensaml.profile.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.MetricContext;
import org.opensaml.profile.context.PreviousEventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.shibboleth.shared.annotation.Prototype;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.primitive.NonnullSupplier;

/**
 * Base class for profile actions.
 * 
 * This base class is annotated with {@link Prototype} to indicate that it is stateful.
 */
@Prototype
public abstract class AbstractProfileAction extends AbstractInitializableComponent implements ProfileAction {

    /** Cached log prefix. */
    @Nullable private String logPrefix;
    
    /** Supplier for the Current HTTP request, if available. */
    @Nullable private NonnullSupplier<HttpServletRequest> httpServletRequestSupplier;

    /** Current HTTP response, if available. */
    @Nullable private  NonnullSupplier<HttpServletResponse> httpServletResponseSupplier;

    /** Has {@link #doPreExecute(ProfileRequestContext)} been called?. Only ever set to true */
    private boolean preExecuted;

    /**
     * Get the current HTTP request if available.
     * 
     * @return current HTTP request
     */
    @Nullable public HttpServletRequest getHttpServletRequest() {
        if (httpServletRequestSupplier != null) {
            return httpServletRequestSupplier.get();
        }
        
        return null;
    }
    
    /**
     * Get the current HTTP request if available, raising an {@link IllegalStateException} if absent.
     * 
     * @return current HTTP request
     * 
     * @since 5.0.0
     */
    @Nonnull public HttpServletRequest ensureHttpServletRequest() {
        final HttpServletRequest ret = getHttpServletRequest();
        if (ret != null) {
            return ret;
        }
        throw new IllegalStateException("HttpServletRequest was absent");
    }

    /**
     * Get the supplier for  HTTP request if available.
     *
     * @return current HTTP request
     */
    @Nullable public NonnullSupplier<HttpServletRequest> getHttpServletRequestSupplier() {
        return httpServletRequestSupplier;
    }

    /**
     * Set the current HTTP request Supplier.
     *
     * @param requestSupplier Supplier for the current HTTP request
     */
    public void setHttpServletRequestSupplier(@Nullable final NonnullSupplier<HttpServletRequest> requestSupplier) {
        checkSetterPreconditions();
        httpServletRequestSupplier = requestSupplier;
    }

    /**
     * Get the current HTTP response if available.
     *
     * @return current HTTP response or null
     */
    @Nullable public HttpServletResponse getHttpServletResponse() {
        if (httpServletResponseSupplier != null) {
            return httpServletResponseSupplier.get();
        }
        
        return null;
    }
    
    /**
     * Get the current HTTP response if available, raising an {@link IllegalStateException} if absent.
     * 
     * @return current HTTP response
     * 
     * @since 5.0.0
     */
    @Nonnull public HttpServletResponse ensureHttpServletResponse() {
        final HttpServletResponse ret = getHttpServletResponse();
        if (ret != null) {
            return ret;
        }
        
        throw new IllegalStateException("HttpServletResponse was absent");
    }

    /**
     * Get the current HTTP response supplier if available.
     *
     * @return current HTTP response supplier or null
     */
    @Nullable public NonnullSupplier<HttpServletResponse> getHttpServletResponseSupplier() {
        return httpServletResponseSupplier;
    }

    /**
     * Set the supplier of the current HTTP response.
     *
     * @param supplier what to set
     */
    public void setHttpServletResponseSupplier(@Nullable final NonnullSupplier<HttpServletResponse> supplier) {
        checkSetterPreconditions();

        httpServletResponseSupplier = supplier;
    }

    /** {@inheritDoc} */
    @Override public void execute(@Nonnull final ProfileRequestContext profileRequestContext) {
        checkComponentActive();
        
        // Clear any existing EventContext that might be hanging around, and if it exists,
        // copy the Event to a PreviousEventContext. Don't clear any existing PreviousEventContext
        // because it may be from an earlier error of interest to other actions.
        final EventContext previousEvent = profileRequestContext.getSubcontext(EventContext.class);
        if (previousEvent != null) {
            profileRequestContext.ensureSubcontext(PreviousEventContext.class).setEvent(previousEvent.getEvent());
            profileRequestContext.removeSubcontext(EventContext.class);
        }

        // The try/catch logic is designed to suppress a checked exception raised by
        // the doInvoke step by any unchecked errors in the doPostInvoke method.
        // The original exception is logged, and can be accessed from the suppressing
        // error object using the Java 7 API.

        if (doPreExecute(profileRequestContext)) {
            preExecuted = true;
            try {
                doExecute(profileRequestContext);
            } catch (final Throwable t) {
                try {
                    if (t instanceof Exception) {
                        doPostExecute(profileRequestContext, (Exception) t);
                    } else {
                        doPostExecute(profileRequestContext);
                    }
                } catch (final Throwable t2) {
                    LoggerFactory.getLogger(AbstractProfileAction.class).warn(
                            getLogPrefix() + " Unchecked exception/error thrown by doPostInvoke, "
                                    + "superseding earlier exception/error ", t);
                    t2.addSuppressed(t);
                    throw t2;
                }
                throw t;
            }

            doPostExecute(profileRequestContext);
        }
    }

    /**
     * Called prior to execution, actions may override this method to perform pre-processing for a request.
     * 
     * <p>
     * If false is returned, execution will not proceed, and the action should attach an
     * {@link org.opensaml.profile.context.EventContext} to the context tree to signal how to continue with overall
     * workflow processing.
     * </p>
     * 
     * <p>
     * If returning successfully, the last step should be to return the result of the superclass version of this method.
     * </p>
     * 
     * @param profileRequestContext the current IdP profile request context
     * @return true iff execution should proceed
     */
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final MetricContext metricCtx = profileRequestContext.getSubcontext(MetricContext.class);
        if (metricCtx != null) {
            final String name = getClass().getSimpleName();
            assert name != null;
            metricCtx.start(name);
        }
        
        return true;
    }

    /**
     * Performs this action. Actions must override this method to perform their work.
     * 
     * @param profileRequestContext the current IdP profile request context
     */
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
    }

    /**
     * Called after execution, actions may override this method to perform post-processing for a request.
     * 
     * <p>
     * Actions must not "fail" during this step and will not have the opportunity to signal events at this stage. This
     * method will not be called if {@link #doPreExecute} fails, but is called if an exception is raised by
     * {@link #doExecute}.
     * </p>
     * 
     * @param profileRequestContext the current IdP profile request context
     */
    protected void doPostExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final MetricContext metricCtx = profileRequestContext.getSubcontext(MetricContext.class);
        if (metricCtx != null) {
            final String name = getClass().getSimpleName();
            assert name != null;
            metricCtx.stop(name);
            metricCtx.inc(name);
        }
    }

    /**
     * Called after execution, actions may override this method to perform post-processing for a request.
     * 
     * <p>
     * Actions must not "fail" during this step and will not have the opportunity to signal events at this stage. This
     * method will not be called if {@link #doPreExecute} fails, but is called if an exception is raised by
     * {@link #doExecute}.
     * </p>
     * 
     * <p>
     * This version of the method will be called if an exception is raised during execution of the action. The overall
     * action result will be to raise this error, so any errors inadvertently raised by this method will be logged and
     * superseded.
     * </p>
     * 
     * <p>
     * The default implementation simply calls the error-less version of this method.
     * </p>
     * 
     * @param profileRequestContext the current IdP profile request context
     * @param e an exception raised by the {@link #doExecute} method
     */
    protected void doPostExecute(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final Exception e) {
        doPostExecute(profileRequestContext);
    }

    /**
     * Has the {@link #doPreExecute(ProfileRequestContext)} method been entirely called?
     *
     * Note the unsynchronized access.  The underlying field is only ever set true, so if true is
     * returned it is correct, if false is returned is is not safe to make any assumptions (even if
     * there was an call in flight.
     *
     * @return whether the preExecute hook was called 
     *
     * @since 5.0.0
     */
    protected boolean isPreExecuteCalled() {
        return preExecuted;
    }

    /**
     * Return a prefix for logging messages for this component.
     * 
     * @return a string for insertion at the beginning of any log messages
     */
    @Nonnull @NotEmpty protected String getLogPrefix() {
        if (logPrefix == null) {
            logPrefix = "Profile Action " + getClass().getSimpleName() + ":";
        }
        assert logPrefix != null;
        return logPrefix;
    }

}
