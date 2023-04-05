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

package org.opensaml.saml.ext.saml2mdui.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.LangBearing;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.ext.saml2mdui.Keywords;

import com.google.common.base.Strings;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link Keywords}.
 */
public class KeywordsImpl extends AbstractXMLObject implements Keywords {

    /** The language. */
    @Nullable private String lang;
    
    /** The data. */
    @Nullable private List<String> data;
    
    /**
     * Constructor.
     *
     * @param namespaceURI the URI
     * @param elementLocalName the local name
     * @param namespacePrefix the prefix
     */
    protected KeywordsImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getXMLLang() {
        return lang;
    }

    /** {@inheritDoc} */
    public void setXMLLang(@Nullable final String newLang) {
        final boolean hasValue = newLang != null && !Strings.isNullOrEmpty(newLang);
        lang = prepareForAssignment(lang, newLang);
        manageQualifiedAttributeNamespace(LangBearing.XML_LANG_ATTR_NAME, hasValue);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<String> getKeywords() {
        return data;
    }

    /** {@inheritDoc} */
    public void setKeywords(@Nullable final List<String> val) {
        if (val != null) {
            data = prepareForAssignment(data, CollectionSupport.copyToList(val));
        } else {
            data = prepareForAssignment(data, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = lang != null ? lang.hashCode() : 12;
        if (data != null) {
            for (final String s : data) {
                hash = hash * 31 + s.hashCode();
            }
        }
        return hash; 
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (!(obj instanceof Keywords)) {
            return false;
        }
        final Keywords other = (Keywords) obj;

        if (lang != null && !lang.equals(other.getXMLLang())) {
            return false;
        } else if (lang == null && other.getXMLLang() != null) {
            return false;
        }

        final List<String> ourList = getKeywords();
        final List<String> otherList = other.getKeywords();
        if (ourList == null) {
            return otherList == null;
        }
        
        return ourList.equals(otherList);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return null;
    }

}