/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml2.metadata.provider;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.opensaml.common.SAMLObjectTestCaseConfigInitializer;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.RoleDescriptor;

public class ChainingMetadataProviderTest extends SAMLObjectTestCaseConfigInitializer {

    private ChainingMetadataProvider metadataProvider;
    private String entityID;
    private String supportedProtocol;
    
    /**{@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
       
        entityID = "urn:mace:incommon:washington.edu";
        supportedProtocol ="urn:oasis:names:tc:SAML:1.1:protocol";
        
        metadataProvider = new ChainingMetadataProvider();
        
        String inCommonMDURL = "http://wayf.incommonfederation.org/InCommon/InCommon-metadata.xml";
        metadataProvider.addMetadataProvider(new URLMetadataProvider(inCommonMDURL, 1000 * 5));
        
        URL mdURL = FilesystemMetadataProviderTest.class.getResource("/data/org/opensaml/saml2/metadata/InCommon-metadata.xml");
        File mdFile = new File(mdURL.toURI());
        metadataProvider.addMetadataProvider(new FilesystemMetadataProvider(mdFile));
    }
    
    /**
     * Tests the {@link URLMetadataProvider#getEntityDescriptor(String)} method.
     */
    public void testGetEntityDescriptor(){
        EntityDescriptor descriptor = metadataProvider.getEntityDescriptor(entityID);
        assertNotNull("Retrieved entity descriptor was null", descriptor);
        assertEquals("Entity's ID does not match requested ID", entityID, descriptor.getEntityID());
    }
    
    /**
     * Tests the {@link URLMetadataProvider#getRole(String, javax.xml.namespace.QName) method.
     */
    public void testGetRole(){
        List<RoleDescriptor> roles = metadataProvider.getRole(entityID, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        assertNotNull("Roles for entity descriptor was null", roles);
        assertEquals("Unexpected number of roles", 1, roles.size());
    }
    
    /**
     * Test the {@link URLMetadataProvider#getRole(String, javax.xml.namespace.QName, String) method.
     */
    public void testGetRoleWithSupportedProtocol(){
        List<RoleDescriptor> roles = metadataProvider.getRole(entityID, IDPSSODescriptor.DEFAULT_ELEMENT_NAME, supportedProtocol);
        assertNotNull("Roles for entity descriptor was null", roles);
        assertEquals("Unexpected number of roles", 1, roles.size());
    }
}