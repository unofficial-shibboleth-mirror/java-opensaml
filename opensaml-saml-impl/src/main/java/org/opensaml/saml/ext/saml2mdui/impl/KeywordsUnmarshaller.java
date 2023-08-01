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

package org.opensaml.saml.ext.saml2mdui.impl;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.LangBearing;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.ext.saml2mdui.Keywords;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.XMLConstants;

/**
 * A thread-safe unmarshaller for {@link Keywords} objects.
 */
public class KeywordsUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        if (attribute.getLocalName().equals(LangBearing.XML_LANG_ATTR_LOCAL_NAME)
                && XMLConstants.XML_NS.equals(attribute.getNamespaceURI())) {
            final Keywords keywords = (Keywords) xmlObject;

            keywords.setXMLLang(attribute.getValue());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processElementContent(@Nonnull final XMLObject xmlObject, @Nonnull final String elementContent) {
        final Keywords keywords = (Keywords) xmlObject;
        final String[] words = elementContent.split("\\s+");
        final ArrayList<String> wordlist = new ArrayList<>(words.length);
        
        for (final String s : words) {
            wordlist.add(s);
        }

        keywords.setKeywords(wordlist);
    }

}