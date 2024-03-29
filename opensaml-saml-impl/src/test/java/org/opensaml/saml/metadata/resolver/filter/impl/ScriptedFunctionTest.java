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

package org.opensaml.saml.metadata.resolver.filter.impl;

import java.io.IOException;
import java.util.Set;

import javax.script.ScriptException;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resource.Resource;
import net.shibboleth.shared.spring.resource.ResourceHelper;

@SuppressWarnings("javadoc")
public class ScriptedFunctionTest extends XMLObjectBaseTestCase {
    
    static final String SCRIPT_8 = "JavaString=Java.type(\"java.lang.String\"); JavaSet = Java.type(\"java.util.HashSet\");set = new JavaSet();set.add(new JavaString(\"String\"));set";
    static final String FILE_8 = "/org/opensaml/saml/metadata/resolver/filter/impl/script8.js";
    
    private XMLObject makeObject() {
        final SAMLObjectBuilder<EntityDescriptor> builder = (SAMLObjectBuilder<EntityDescriptor>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<EntityDescriptor>ensureBuilder(
                        EntityDescriptor.DEFAULT_ELEMENT_NAME);
        return builder.buildObject();
    }
    
    @Test public void inlineScript() throws ScriptException, ComponentInitializationException {
        
        final Set<String> s = ScriptedTrustedNamesFunction.inlineScript(SCRIPT_8).apply(makeObject());
        assert s != null;
        Assert.assertEquals(s.size(), 1);
        Assert.assertTrue(s.contains("String"));
   }
    
    
    @Test public void fileScript() throws ScriptException, IOException, ComponentInitializationException {
        final Resource r = ResourceHelper.of(new ClassPathResource(FILE_8));
        final Set<String> result = ScriptedTrustedNamesFunction.resourceScript(r).apply(makeObject());
        assert result != null;
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains("String"));
    }
    
    @Test public void customScript() throws ScriptException, ComponentInitializationException {
        
        final ScriptedTrustedNamesFunction what = ScriptedTrustedNamesFunction.inlineScript("custom;");
        what.setCustomObject(CollectionSupport.singleton("String"));
        
        final Set<String> s = what.apply(makeObject());
        assert s != null;
        Assert.assertEquals(s.size(), 1);
        Assert.assertTrue(s.contains("String"));
   }

}