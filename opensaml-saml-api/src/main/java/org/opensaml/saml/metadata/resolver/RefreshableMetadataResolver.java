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

package org.opensaml.saml.metadata.resolver;

import java.time.Instant;

import javax.annotation.Nullable;

import net.shibboleth.shared.resolver.ResolverException;

/**
 * Specialization of {@link MetadataResolver} that supports on-demand refresh.
 */
public interface RefreshableMetadataResolver extends MetadataResolver {
    
    /**
     * Refresh the data exposed by the resolver.
     * 
     * <p>
     * An implementation of this method should typically be either <code>synchronized</code>
     * or make use other locking mechanisms to protect against concurrent access.
     * </p>
     * 
     * @throws ResolverException if the refresh operation was unsuccessful
     */
    void refresh() throws ResolverException;

    /**
     * Gets the time the last refresh cycle occurred.
     * 
     * @return time the last refresh cycle occurred
     */
    @Nullable Instant getLastRefresh();

    /**
     * Get the time that the currently available metadata was last updated. Note, this may be different than the time
     * retrieved by {@link #getLastRefresh()} is the metadata was known not to have changed during the last refresh
     * cycle.
     * 
     * @return time when the currently metadata was last updated, null if metadata has never successfully been read in
     */
    @Nullable Instant getLastUpdate();
    
    /**
     * Gets the time the last successful refresh cycle occurred.
     * 
     * @return time the last successful refresh cycle occurred
     */
    @Nullable Instant getLastSuccessfulRefresh();

    /**
     * Gets whether the last refresh cycle was successful.
     * 
     * @return true if last refresh cycle was successful, false if not
     */
    @Nullable Boolean wasLastRefreshSuccess();
    
    /**
     * Gets the reason the last refresh failed.
     *
     * @return reason the last refresh failed or null if the last refresh was successful
     */
    @Nullable Throwable getLastFailureCause();

}