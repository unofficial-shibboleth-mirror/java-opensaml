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

package org.opensaml.core.xml.config.tests;

import javax.xml.namespace.QName;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.Initializer;
import org.opensaml.core.testing.XMLObjectProviderInitializerBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderInitializer;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;
import org.opensaml.core.xml.schema.XSString;

/**
 * Test XMLObject provider initializer for module "core".
 */
public class XMLObjectProviderInitializerTest extends XMLObjectProviderInitializerBaseTestCase {

    /** {@inheritDoc} */
    protected Initializer getTestedInitializer() {
        return new XMLObjectProviderInitializer();
    }

    /** {@inheritDoc} */
    protected QName[] getTestedProviders() {
        return new QName[] { 
                ConfigurationService.ensure(XMLObjectProviderRegistry.class).getDefaultProviderQName(), 
                XSString.TYPE_NAME, 
        };
    }

}
