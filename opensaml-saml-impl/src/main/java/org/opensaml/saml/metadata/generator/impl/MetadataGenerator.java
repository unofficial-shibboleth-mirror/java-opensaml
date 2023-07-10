/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.metadata.generator.impl;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.Nonnull;

/**
 * Interface to a component that generates SAML metadata.
 * 
 * <p>TODO: this will eventually migrate into the API.</p>
 * 
 * @since 5.0.0
 */
public interface MetadataGenerator {

    /**
     * Generate metadata using the supplied parameters into the supplied destination.
     * 
     * <p>The writer must be open and will not be closed by this method.</p>
     * 
     * @param params input parameters
     * @param sink destination for output
     * 
     * @throws IOException on error
     */
    public void generate(@Nonnull final MetadataGeneratorParameters params, @Nonnull final Writer sink)
        throws IOException;

}