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

package org.opensaml.xmlsec.signature.impl;

import java.math.BigInteger;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.schema.impl.XSBase64BinaryImpl;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.CryptoBinary;
import org.slf4j.Logger;

import com.google.common.base.Strings;

import net.shibboleth.shared.codec.DecodingException;
import net.shibboleth.shared.codec.EncodingException;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Concrete implementation of {@link CryptoBinary}.
 */
public class CryptoBinaryImpl extends XSBase64BinaryImpl implements CryptoBinary {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(CryptoBinaryImpl.class);
    
    /** The cached BigInteger representation of the element's base64-encoded value. */
    @Nullable private BigInteger bigIntValue;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected CryptoBinaryImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public BigInteger getValueBigInt() {
        if (bigIntValue == null) {
            try {
                final String value = getValue();
                if (!Strings.isNullOrEmpty(getValue())) {
                    assert value != null;
                    bigIntValue = KeyInfoSupport.decodeBigIntegerFromCryptoBinary(value);
                }
            } catch (final DecodingException e) {
                //can not decode big integer from invalid value, return original even if null.    
                log.warn("Could not decode big integer value, returning cached value", e);
            }
        }
        return bigIntValue;
    }

    /** {@inheritDoc} */
    public void setValueBigInt(@Nullable final BigInteger bigInt) throws EncodingException{
        if (bigInt == null) {
            setValue(null);
        } else {
            setValue(KeyInfoSupport.encodeCryptoBinaryFromBigInteger(bigInt));
        }
        bigIntValue = bigInt;
    }
    
    /** {@inheritDoc} */
    public void setValue(@Nullable final String newValue) {
        if (bigIntValue != null 
                && (!Objects.equals(getValue(), newValue) || newValue == null)) {
            // Just clear the cached value, my not be needed in big int form again,
            // let it be lazily recreated if necessary
            bigIntValue = null;
        }
        super.setValue(newValue);
    }

}