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

package org.opensaml.saml.saml2.metadata.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.xmlsec.signature.Signature;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 *
 */
public class RoleDescriptorXSAnyAdapterTest extends XMLObjectBaseTestCase {
    
    private static final QName SECURITY_TOKEN_SERVICE_TYPE = new QName("http://docs.oasis-open.org/wsfed/federation/200706", "SecurityTokenServiceType");
    private static final QName APPLICATION_SERVICE_TYPE = new QName("http://docs.oasis-open.org/wsfed/federation/200706", "ApplicationServiceType");

    private static final QName CLAIM_TYPES_REQUESTED_ELEMENT = new QName("http://docs.oasis-open.org/wsfed/federation/200706", "ClaimTypesRequested");
    private static final QName TARGET_SCOPES_ELEMENT = new QName("http://docs.oasis-open.org/wsfed/federation/200706", "TargetScopes");
    private static final QName APP_SERVICE_ENDPOINT_ELEMENT = new QName("http://docs.oasis-open.org/wsfed/federation/200706", "ApplicationServiceEndpoint");
    private static final QName PASSIVE_REQUESTOR_ENDPOINT_ELEMENT = new QName("http://docs.oasis-open.org/wsfed/federation/200706", "PassiveRequestorEndpoint");

    @Test
    public void basicEntityDescriptor() throws Exception {
        EntityDescriptor entityDescriptor;
        try (final InputStream in = getClass().getResourceAsStream("/org/opensaml/saml/saml2/metadata/adfs-metadata.xml")) {
            entityDescriptor = (EntityDescriptor) XMLObjectSupport.unmarshallFromInputStream(parserPool, in);
        }
    

        Assert.assertEquals(entityDescriptor.getRoleDescriptors().size(), 4);

        Assert.assertTrue(RoleDescriptorXSAnyAdapter.class.isInstance(entityDescriptor.getRoleDescriptors().get(0)));
        Assert.assertTrue(APPLICATION_SERVICE_TYPE.equals(entityDescriptor.getRoleDescriptors().get(0).getSchemaType()));

        Assert.assertTrue(RoleDescriptorXSAnyAdapter.class.isInstance(entityDescriptor.getRoleDescriptors().get(1)));
        Assert.assertTrue(SECURITY_TOKEN_SERVICE_TYPE.equals(entityDescriptor.getRoleDescriptors().get(1).getSchemaType()));

        Assert.assertTrue(SPSSODescriptor.class.isInstance(entityDescriptor.getRoleDescriptors().get(2)));
        Assert.assertTrue(IDPSSODescriptor.class.isInstance(entityDescriptor.getRoleDescriptors().get(3)));
        
        RoleDescriptorXSAnyAdapter appType = (RoleDescriptorXSAnyAdapter) entityDescriptor.getRoleDescriptors().get(0);
        Assert.assertEquals(appType.getUnknownAttributes().get(new QName("ServiceDisplayName")), "ESUE Authentication Service");
        Assert.assertEquals(appType.getSupportedProtocols(), CollectionSupport.listOf(
                "http://docs.oasis-open.org/ws-sx/ws-trust/200512",
                "http://schemas.xmlsoap.org/ws/2005/02/trust",
                "http://docs.oasis-open.org/wsfed/federation/200706"));
        Assert.assertEquals(appType.getKeyDescriptors().size(), 1);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().size(), 5);

        RoleDescriptorXSAnyAdapter secTokenType = (RoleDescriptorXSAnyAdapter) entityDescriptor.getRoleDescriptors().get(1);
        Assert.assertEquals(secTokenType.getUnknownAttributes().get(new QName("ServiceDisplayName")), "ESUE Authentication Service");
        Assert.assertEquals(secTokenType.getSupportedProtocols(), CollectionSupport.listOf(
                "http://docs.oasis-open.org/ws-sx/ws-trust/200512",
                "http://schemas.xmlsoap.org/ws/2005/02/trust",
                "http://docs.oasis-open.org/wsfed/federation/200706"));
        Assert.assertEquals(secTokenType.getKeyDescriptors().size(), 2);
        Assert.assertEquals(secTokenType.getAdapted().getUnknownXMLObjects().size(), 6);
        
    }

    @Test
    public void basicRoleDescriptor() throws Exception {
        XSAny xsAny;
        try (final InputStream in = getClass().getResourceAsStream("/org/opensaml/saml/saml2/metadata/adfs-role-descriptor.xml")) {
            xsAny = (XSAny) getUnmarshaller(XMLObjectProviderRegistrySupport.getDefaultProviderQName()).unmarshall(
                    parserPool.parse(in).getDocumentElement());
        }
        
        Assert.assertEquals(RoleDescriptor.DEFAULT_ELEMENT_NAME, xsAny.getElementQName());
        Assert.assertEquals(APPLICATION_SERVICE_TYPE, xsAny.getSchemaType());
        
        RoleDescriptorXSAnyAdapter appType = new RoleDescriptorXSAnyAdapter(xsAny);
        Assert.assertEquals(appType.getUnknownAttributes().get(new QName("ServiceDisplayName")), "ESUE Authentication Service");
        Assert.assertEquals(appType.getSupportedProtocols(), CollectionSupport.listOf(
                "http://docs.oasis-open.org/ws-sx/ws-trust/200512",
                "http://schemas.xmlsoap.org/ws/2005/02/trust",
                "http://docs.oasis-open.org/wsfed/federation/200706"));

        Assert.assertNull(appType.getSignature());
        Assert.assertNotNull(appType.getExtensions());
        Assert.assertEquals(appType.getKeyDescriptors().size(), 1);
        Assert.assertNotNull(appType.getOrganization());
        Assert.assertEquals(appType.getContactPersons().size(), 1);

        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().size(), 8);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(0).getElementQName(), Extensions.DEFAULT_ELEMENT_NAME);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(1).getElementQName(), KeyDescriptor.DEFAULT_ELEMENT_NAME);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(2).getElementQName(), Organization.DEFAULT_ELEMENT_NAME);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(3).getElementQName(), ContactPerson.DEFAULT_ELEMENT_NAME);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(4).getElementQName(), CLAIM_TYPES_REQUESTED_ELEMENT);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(5).getElementQName(), TARGET_SCOPES_ELEMENT);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(6).getElementQName(), APP_SERVICE_ENDPOINT_ELEMENT);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(7).getElementQName(), PASSIVE_REQUESTOR_ENDPOINT_ELEMENT);
    }
    
    @Test
    public void mutateRoleDescriptorChildren() throws Exception {
        XSAny xsAny;
        try (final InputStream in = getClass().getResourceAsStream("/org/opensaml/saml/saml2/metadata/adfs-role-descriptor.xml")) {
            xsAny = (XSAny) getUnmarshaller(XMLObjectProviderRegistrySupport.getDefaultProviderQName()).unmarshall(
                    parserPool.parse(in).getDocumentElement());
        }
        
        Assert.assertEquals(RoleDescriptor.DEFAULT_ELEMENT_NAME, xsAny.getElementQName());
        Assert.assertEquals(APPLICATION_SERVICE_TYPE, xsAny.getSchemaType());
        
        RoleDescriptorXSAnyAdapter appType = new RoleDescriptorXSAnyAdapter(xsAny);
        
        appType.setSignature((Signature) XMLObjectSupport.buildXMLObject(Signature.DEFAULT_ELEMENT_NAME));
        appType.setExtensions(null);
        List<KeyDescriptor> keys = new ArrayList<>();
        keys.add((KeyDescriptor) XMLObjectSupport.buildXMLObject(KeyDescriptor.DEFAULT_ELEMENT_NAME));
        keys.add((KeyDescriptor) XMLObjectSupport.buildXMLObject(KeyDescriptor.DEFAULT_ELEMENT_NAME));
        appType.getKeyDescriptors().addAll(keys);
        appType.getContactPersons().add((ContactPerson) XMLObjectSupport.buildXMLObject(ContactPerson.DEFAULT_ELEMENT_NAME));
        appType.getAdapted().getUnknownXMLObjects().add(XMLObjectSupport.buildXMLObject(simpleXMLObjectQName));

        Assert.assertNotNull(appType.getSignature());
        Assert.assertNull(appType.getExtensions());
        Assert.assertEquals(appType.getKeyDescriptors().size(), 3);
        Assert.assertNotNull(appType.getOrganization());
        Assert.assertEquals(appType.getContactPersons().size(), 2);

        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().size(), 12);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(0).getElementQName(), Signature.DEFAULT_ELEMENT_NAME);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(1).getElementQName(), KeyDescriptor.DEFAULT_ELEMENT_NAME);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(2).getElementQName(), KeyDescriptor.DEFAULT_ELEMENT_NAME);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(3).getElementQName(), KeyDescriptor.DEFAULT_ELEMENT_NAME);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(4).getElementQName(), Organization.DEFAULT_ELEMENT_NAME);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(5).getElementQName(), ContactPerson.DEFAULT_ELEMENT_NAME);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(6).getElementQName(), ContactPerson.DEFAULT_ELEMENT_NAME);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(7).getElementQName(), CLAIM_TYPES_REQUESTED_ELEMENT);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(8).getElementQName(), TARGET_SCOPES_ELEMENT);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(9).getElementQName(), APP_SERVICE_ENDPOINT_ELEMENT);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(10).getElementQName(), PASSIVE_REQUESTOR_ENDPOINT_ELEMENT);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(11).getElementQName(), simpleXMLObjectQName);
        
        appType.setSignature(null);
        appType.getKeyDescriptors().clear();
        appType.setOrganization(null);
        List<ContactPerson> persons = CollectionSupport.copyToList(appType.getContactPersons());
        for (ContactPerson cp : persons) {
            appType.getContactPersons().remove(cp);
        }
        appType.getAdapted().getUnknownXMLObjects().removeIf(t -> t.getElementQName().equals(simpleXMLObjectQName));
        
        Assert.assertNull(appType.getSignature());
        Assert.assertNull(appType.getExtensions());
        Assert.assertEquals(appType.getKeyDescriptors().size(), 0);
        Assert.assertNull(appType.getOrganization());
        Assert.assertEquals(appType.getContactPersons().size(), 0);

        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().size(), 4);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(0).getElementQName(), CLAIM_TYPES_REQUESTED_ELEMENT);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(1).getElementQName(), TARGET_SCOPES_ELEMENT);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(2).getElementQName(), APP_SERVICE_ENDPOINT_ELEMENT);
        Assert.assertEquals(appType.getAdapted().getUnknownXMLObjects().get(3).getElementQName(), PASSIVE_REQUESTOR_ENDPOINT_ELEMENT);
    }
    
}
