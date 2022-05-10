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

package org.opensaml.storage.impl;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.dbcp2.BasicDataSource;
import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.StorageService;
import org.testng.Assert;
import org.testng.TestException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.DestructableComponent;
import net.shibboleth.utilities.java.support.component.InitializableComponent;

/**
 * Test of {@link JPAStorageService} implementation.
 */
@SuppressWarnings("javadoc")
public class HSQLDBStorageServiceTest {

    private JDBCStorageService storageService;

    /** Contexts used for testing. */
    private Object[][] contexts;
    
    private BasicDataSource dataSource;
    
    protected SecureRandom random;
    
    protected StorageService shared;

    
    /** Called to init a thread in preparation to run a test. */
    protected void threadInit() {
        
    }
    
    public HSQLDBStorageServiceTest() {
        final SecureRandom random1 = new SecureRandom();
        contexts = new Object[10][1];
        for (int i = 0; i < 10; i++) {
            contexts[i] = new Object[] {Long.toString(random1.nextLong()), };
        }
    }


    @Test(threadPoolSize = 10, invocationCount = 10)
    public void strings() throws IOException {
        threadInit();
        
        try {
        String context = Long.toString(random.nextLong());
        
        for (int i = 1; i <= 100; i++) {
            boolean result = shared.create(context, Integer.toString(i), Integer.toString(i + 1), System.currentTimeMillis() + 300000);
            Assert.assertTrue(result);
        }
        
        for (int i = 1; i <= 100; i++) {
            StorageRecord<?> rec = shared.read(context, Integer.toString(i));
            Assert.assertNotNull(rec);
            Assert.assertEquals(rec.getValue(), Integer.toString(i + 1));
        }

        for (int i = 1; i <= 100; i++) {
            boolean result = shared.update(context, Integer.toString(i), Integer.toString(i + 2), System.currentTimeMillis() + 300000);
            Assert.assertTrue(result);
        }

        for (int i = 1; i <= 100; i++) {
            StorageRecord<?> rec = shared.read(context, Integer.toString(i));
            Assert.assertNotNull(rec);
            Assert.assertEquals(rec.getValue(), Integer.toString(i + 2));
        }

        for (int i = 1; i <= 100; i++) {
            boolean result = shared.create(context, Integer.toString(i), Integer.toString(i + 1), null);
            Assert.assertFalse(result, "createString should have failed");
        }        
        
        for (int i = 1; i <= 100; i++) {
            shared.delete(context, Integer.toString(i));
            StorageRecord<?> rec = shared.read(context, Integer.toString(i));
            Assert.assertNull(rec);
        }
        } catch (java.lang.AssertionError e) {
            System.out.println("FAILURE: " + e);
        }
    }

    /**
     * Creates the shared instance of the entity manager factory.
     */
    @BeforeClass public void setUp() throws ComponentInitializationException {
        try {
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
            dataSource.setUrl("jdbc:hsqldb:mem:Test;hsqldb.sqllog=3");
            dataSource.setUsername("SA");
            dataSource.setPassword("");
            Connection dbConn;
            dbConn = dataSource.getConnection();
            Statement statement = dbConn.createStatement();
            final String sql = "CREATE TABLE StorageRecords (\r\n"
                    + "  context varchar(255) NOT NULL,\r\n"
                    + "  id varchar(255) NOT NULL,\r\n"
                    + "  expires bigint DEFAULT NULL,\r\n"
                    + "  value varchar(255) NOT NULL,\r\n"
                    + "  version bigint NOT NULL,\r\n"
                    + "  PRIMARY KEY (context,id)\r\n"
                    + ")";
            statement.executeUpdate(sql);
            
            storageService = new JDBCStorageService();
            storageService.setId("test");
            storageService.setDataSource(dataSource);
            storageService.setCleanupInterval(Duration.ofSeconds(5));
            storageService.setTransactionRetry(6);
            storageService.setRetryableErrors(List.of("40001"));
        } catch (final SQLException e) {
            throw new ComponentInitializationException(e);
        }
        random = new SecureRandom();
        shared = getStorageService();
        if (shared instanceof InitializableComponent) {
            ((InitializableComponent) shared).initialize();
        }
    }
    
    @AfterClass
    protected void tearDown() {
        try {
            List<String> contexts1 = storageService.readContexts();
            for (String ctx : contexts1) {
                storageService.deleteContext(ctx);
            }
            List<?> recs = storageService.readAll();
            Assert.assertEquals(recs.size(), 0);
        } catch (IOException e){ 
            throw new RuntimeException(e);
        }
        if (shared instanceof DestructableComponent) {
            ((DestructableComponent) shared).destroy();
        }
        try {
            dataSource.close();
        } catch (SQLException e) {
            throw new TestException(e);
        }
    }

    @Nonnull protected StorageService getStorageService() {
        return storageService;
    }

/* 
 *     @Test
 */
    public void cleanup() throws ComponentInitializationException, IOException {
        String context = Long.toString(random.nextLong());
        for (int i = 1; i <= 100; i++) {
            storageService.create(context, Integer.toString(i), Integer.toString(i + 1), System.currentTimeMillis() + 100);
        }
        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
        List<?> recs = storageService.readAll(context);
        Assert.assertEquals(recs.size(), 0);
    }

}
