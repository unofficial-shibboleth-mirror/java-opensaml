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

package org.opensaml.xmlsec.signature.support;

import javax.annotation.Nonnull;

import org.apache.xml.security.signature.XMLSignature;

/**
 * Interface for representing the references to the content that is digitally signed.
 * 
 * Individual implementations of this may with to expose properties, such as the ability to 
 * set the digest algorithm if it may vary based on runtime information.
 */
public interface ContentReference {
    
    /**
     * Called by the signature marshaller to allow references to be added to the signature. 
     *
     * @param signature the signature object
     */
    void createReference(@Nonnull final XMLSignature signature);
}