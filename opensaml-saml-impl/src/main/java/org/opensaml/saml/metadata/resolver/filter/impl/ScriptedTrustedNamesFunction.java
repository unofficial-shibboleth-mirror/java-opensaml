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
import java.io.InputStream;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.script.ScriptContext;
import javax.script.ScriptException;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resource.Resource;
import net.shibboleth.shared.scripting.AbstractScriptEvaluator;
import net.shibboleth.shared.scripting.EvaluableScript;

import org.opensaml.core.xml.XMLObject;

/**
 * A scripted {@link Function} which can be injected into
 * {@link SignatureValidationFilter#setDynamicTrustedNamesStrategy(Function)}.
 */
public class ScriptedTrustedNamesFunction extends AbstractScriptEvaluator implements Function<XMLObject, Set<String>> {

    /**
     * Constructor.
     * 
     * @param theScript the script we will evaluate.
     * @param extraInfo debugging information.
     */
    protected ScriptedTrustedNamesFunction(@Nonnull final EvaluableScript theScript, @Nullable final String extraInfo) {
        super(theScript);
        setOutputType(Set.class);
        setHideExceptions(true);
        setLogPrefix("Scripted Function from " + extraInfo + ":");
    }

    /**
     * Constructor.
     * 
     * @param theScript the script we will evaluate.
     */
    protected ScriptedTrustedNamesFunction(@Nonnull final EvaluableScript theScript) {
        super(theScript);
        setOutputType(Set.class);
        setHideExceptions(true);
        setLogPrefix("Anonymous Scripted Function:");
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public Object getCustomObject() {
        return super.getCustomObject();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nullable public Set<String> apply(@Nullable final XMLObject context) {
        return (Set<String>) evaluate(context);
    }
    
    /** {@inheritDoc} */
    protected void prepareContext(@Nonnull final ScriptContext scriptContext, @Nullable final Object... input) {
        scriptContext.setAttribute("profileContext", input != null ? input[0] : null, ScriptContext.ENGINE_SCOPE);
    }

    /**
     * Factory to create {@link ScriptedTrustedNamesFunction} for {@link SignatureValidationFilter}s from a
     * {@link Resource}.
     * 
     * @param engineName the language
     * @param resource the resource to look at
     * @return the function
     * @throws ScriptException if the compile fails
     * @throws IOException if the file doesn't exist.
     * @throws ComponentInitializationException if the scripting initialization fails
     */
    @Nonnull static ScriptedTrustedNamesFunction resourceScript(
            @Nonnull @NotEmpty @ParameterName(name="engineName") final String engineName,
            @Nonnull @ParameterName(name="resource") final Resource resource)
                    throws ScriptException, IOException, ComponentInitializationException {
        try (final InputStream is = resource.getInputStream()) {
            final EvaluableScript script = new EvaluableScript();
            script.setEngineName(engineName);
            script.setScript(is);
            script.initialize();
            return new ScriptedTrustedNamesFunction(script, resource.getDescription());
        }
    }

    /**
     * Factory to create {@link ScriptedTrustedNamesFunction} from a {@link Resource}.
     * 
     * @param resource the resource to look at
     * @return the function
     * @throws ScriptException if the compile fails
     * @throws IOException if the file doesn't exist.
     * @throws ComponentInitializationException if the scripting initialization fails
     */
    @Nonnull static ScriptedTrustedNamesFunction resourceScript(
            @Nonnull @ParameterName(name="resource") final Resource resource)
                    throws ScriptException, IOException, ComponentInitializationException {
        return resourceScript(DEFAULT_ENGINE, resource);
    }

    /**
     * Factory to create {@link ScriptedTrustedNamesFunction} for {@link SignatureValidationFilter}s from inline data.
     * 
     * @param scriptSource the script, as a string
     * @param engineName the language
     * @return the function
     * @throws ScriptException if the compile fails
     * @throws ComponentInitializationException if the scripting initialization fails
     */
    @Nonnull static ScriptedTrustedNamesFunction inlineScript(
            @Nonnull @NotEmpty @ParameterName(name="engineName") final String engineName,
            @Nonnull @NotEmpty @ParameterName(name="scriptSource") final String scriptSource)
                    throws ScriptException, ComponentInitializationException {
        final EvaluableScript script = new EvaluableScript();
        script.setEngineName(engineName);
        script.setScript(scriptSource);
        script.initialize();
        return new ScriptedTrustedNamesFunction(script, "Inline");
    }

    /**
     * Factory to create {@link ScriptedTrustedNamesFunction} for {@link SignatureValidationFilter}s from inline data.
     * 
     * @param scriptSource the script, as a string
     * @return the function
     * @throws ScriptException if the compile fails
     * @throws ComponentInitializationException if the scripting initialization fails
     */
    @Nonnull static ScriptedTrustedNamesFunction inlineScript(
            @Nonnull @NotEmpty @ParameterName(name="scriptSource") final String scriptSource)
                    throws ScriptException, ComponentInitializationException {
        return inlineScript(DEFAULT_ENGINE, scriptSource);
    }

}