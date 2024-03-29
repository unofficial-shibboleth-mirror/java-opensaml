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

package org.opensaml.messaging.context.navigate;

import java.util.function.Function;

import org.opensaml.messaging.context.BaseContext;

/**
 * A {@link Function} that is used to navigate a {@link BaseContext} tree and extract data from it.
 * 
 * @param <F> type of the context from which data will be extracted
 * @param <T> type of data returned by the function
 */
public interface ContextDataLookupFunction<F extends BaseContext, T> extends Function<F, T> {

}