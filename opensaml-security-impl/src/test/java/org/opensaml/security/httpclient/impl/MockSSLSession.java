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

package org.opensaml.security.httpclient.impl;

import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;

/**
 *  Mock impl of {@link SSLSession} used in testing hostname verifiers and SSL socket factories.
 */
public class MockSSLSession implements SSLSession {
    
    @Nonnull private final List<Certificate> peerCertificates;
    @Nonnull private final String peerHost;
    
    /**
     * Constructor.
     *
     * @param certs certs
     * @param host peer host
     */
    public MockSSLSession(@Nonnull final List<Certificate> certs, @Nonnull final String host) {
        this.peerCertificates = certs;
        this.peerHost = host;
    }
    
    @Override
    public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
        return peerCertificates.toArray(new Certificate[0]);
    }

    @Override
    public String getPeerHost() {
        return peerHost;
    }

    
    
    // Methods below here are just unimplemented stubs    

    @Override
    public int getApplicationBufferSize() {
        return 0;
    }

    @Override
    public String getCipherSuite() {
        return null;
    }

    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public byte[] getId() {
        return null;
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public Certificate[] getLocalCertificates() {
        return null;
    }

    @Override
    public Principal getLocalPrincipal() {
        return null;
    }

    @Override
    public int getPacketBufferSize() {
        return 0;
    }

    @Deprecated
    @Override
    public javax.security.cert.X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
        return null;
    }

    @Override
    public int getPeerPort() {
        return 0;
    }

    @Override
    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        return null;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public SSLSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getValue(String name) {
        return null;
    }

    @Override
    public String[] getValueNames() {
        return null;
    }

    @Override
    public void invalidate() {
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void putValue(String name, Object value) {
    }

    @Override
    public void removeValue(String name) {
    }
    
}
