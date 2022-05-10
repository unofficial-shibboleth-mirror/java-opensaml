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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.opensaml.storage.AbstractStorageService;
import org.opensaml.storage.MutableStorageRecord;
import org.opensaml.storage.StorageCapabilitiesEx;
import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.VersionMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;


/**
 *
 */
public final class JDBCStorageService extends AbstractStorageService implements StorageCapabilitiesEx {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(JDBCStorageService.class);
    
    /** How many times do we try an operation before giving up? */
    private int transactionRetry = 3;
    
    /** Error messages that signal a transaction should be retried. */
    @Nonnull @NonnullElements private Collection<String> retryableErrors = Collections.emptyList();

    /** The Data Source. */
    @NonnullAfterInit private DataSource dataSource;
    
    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        Constraint.isNotNull(dataSource, "data source must be specified and nonnul");
        super.doInitialize();
    }
    
    /** set {@link #transactionRetry}.
     * @param count how many time to try before we bail.
     */
    public void setTransactionRetry(@Positive final int count) {
        transactionRetry = count;
        if (count < 0) {
            throw new ConstraintViolationException("transaction retry must be positive");
        }
    }
    
    /** Set the {@link DataSource}.
     * @param source what to set.
     */
    public void setDataSource(@Nonnull final DataSource source) {
        dataSource = source;
    }

    /** What errors do we retry?
     * @param errors what to set.
     */
    public void setRetryableErrors(@Nonnull @NonnullElements final Collection<String> errors) {
        retryableErrors = Constraint.isNotNull(errors, "errors must not be null");
        Constraint.noNullItems(errors, "errors must not have null members");
    }
    
    /**
     * Returns all contexts from the store (for testing only).
     * 
     * @return all contexts or an empty list
     * @throws IOException if errors occur in the read process
     */
    @Nonnull @NonnullElements protected List<String> readContexts() throws IOException {
        final List<String> result = new ArrayList<>();
        log.trace("Getting Context");
        try (final Connection connection = getConnection(true)) {
            final PreparedStatement query = connection.prepareStatement("SELECT context FROM StorageRecords");
            final ResultSet results = query.executeQuery();
            while (results.next()) {
                final String context = results.getString(1);
                log.trace("Context = {}", context);
                result.add(context);
            }
            return result;
            
        } catch (final SQLException e) {
            log.error("ReadAll()", e);
            throw new IOException(e);
        }
    }

    /**
     * Returns all records from the store (for testing only).
     * 
     * @return all records or an empty list
     * @throws IOException if errors occur in the read process
     */
    @Nonnull @NonnullElements protected List<?> readAll() throws IOException {
        final List<MyStorageRecord<?>> result = new ArrayList<>();
        log.trace("Getting all Records");
        try (final Connection connection = getConnection(true)) {
            final PreparedStatement query = connection.prepareStatement("SELECT context, id, expires, value, version FROM StorageRecords");
            final ResultSet results = query.executeQuery();
            while (results.next()) {
                final String context = results.getString(1);
                final String id = results.getString(2);
                final Long expires = getExpires(results, 3);
                final String value = results.getString(4);
                final Long version = results.getLong(5);
                log.trace("Record: Context = {}, Id = {}, value = {}, verion = {}, expires = {}",
                        context, id, value, version, expires == null ? "<never>": expires);
                result.add(new MyStorageRecord<>(value, expires, version));
            }
            return result;
            
        } catch (final SQLException e) {
            log.error("ReadAll()", e);
            throw new IOException(e);
        }
    }
    
    /**
     * Returns all records from the store for the supplied context (for testing only).
     * 
     * @param context a storage context label
     * 
     * @return all records in the context or an empty list
     * @throws IOException if errors occur in the read process
     */
    @Nonnull @NonnullElements protected List<?> readAll(@Nonnull @NotEmpty final String context)
            throws IOException {
        final List<MyStorageRecord<?>> result = new ArrayList<>();
        log.trace("Getting all Records for context {}", context);
        try (final Connection connection = getConnection(true)) {
            final PreparedStatement query = connection.prepareStatement("SELECT id, expires, value, version FROM StorageRecords WHERE context = ?");
            query.setString(1, context);
            final ResultSet results = query.executeQuery();
            while (results.next()) {
                final String id = results.getString(1);
                final Long expires = getExpires(results, 2);
                final String value = results.getString(3);
                final Long version = results.getLong(4);
                log.trace("Record: Id = {}, value = {}, verion = {}, expires = {}",
                        id, value, version, expires == null ? "<never>": expires);
                result.add(new MyStorageRecord<>(value, expires, version));
            }
            return result;
            
        } catch (final SQLException e) {
            log.error("ReadAll()", e);
            throw new IOException(e);
        }
    }


    /** {@inheritDoc} */
    public boolean create(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable @Positive final Long expiration) throws IOException {
        //
        // Constraints, Logging
        //
        int retries = transactionRetry;
        while(true) {
            try (final Connection connection = getConnection(false)) {
                // Does it exist?
                // If not insert
                // If so check expiration
                // If not expired complain
                // otherwise update
                final PreparedStatement query = connection.prepareStatement("SELECT expires FROM StorageRecords WHERE context =? AND id=?");
                query.setString(1, context);
                query.setString(2, key);
                log.debug("Querying {}", query);
                final ResultSet resultSet = query.executeQuery();
                if (!resultSet.next()) {
                    final PreparedStatement insert = connection.prepareStatement("INSERT INTO StorageRecords VALUES (?, ?, ?, ?, 1)");
                    insert.setString(1, context);
                    insert.setString(2, key);
                    setExpires(insert, 3, expiration);
                    insert.setString(4,value);
                    insert.executeUpdate();
                    connection.commit();
                    return true;
                }
                final Long returnedExpiration = getExpires(resultSet, 1);
                if (returnedExpiration == null || System.currentTimeMillis() < returnedExpiration) {
                    log.debug("Duplicate record '{}' in context '{}'", key, context);
                    return false;
                }
                final PreparedStatement update = connection.prepareStatement("UPDATE StorageRecords SET value=?, version=0, expires=? WHERE context=? AND id=?");
                update.setString(1, value);
                setExpires(update, 2, expiration);
                update.setString(3,context);
                update.setString(4,key);
                update.executeUpdate();
                connection.commit();
                return true;
            }
            catch (final SQLException e) {
                boolean retry = false;
                for (final String msg : retryableErrors) {
                    if (e.getSQLState() != null && e.getSQLState().contains(msg)) {
                        log.warn("Caught retryable SQL exception", e);
                        retry = true;
                        break;
                    }
                }
                
                if (retry) {
                    if (--retries < 0) {
                        log.warn("Error retryable, but retry limit exceeded");
                        throw new IOException(e);
                    }
                    log.info("Retrying JDBC Create operation");
                } else {
                    throw new IOException(e);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override @Nullable public <T> StorageRecord<T> read(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException {
        return this.<T>readImpl(context, key, null).getSecond();
    }

    /** {@inheritDoc} */
    @Override @Nonnull public <T> Pair<Long, StorageRecord<T>> read(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Positive final long version) throws IOException {
        return readImpl(context, key, version);
    }
    
    /**
     * Reads the record matching the supplied parameters. Returns an empty pair if the record cannot be found or is
     * expired.
     * 
     * @param <T> type of object
     * @param context to search for
     * @param key to search for
     * @param version to match
     * 
     * @return pair of version and storage record
     * @throws IOException if errors occur in the read process
     */
    @Nonnull protected <T> Pair<Long, StorageRecord<T>> readImpl(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Positive final Long version) throws IOException {
        //
        // Constraints, Logging
        //
        int retries = transactionRetry;
        while(true) {
            try (final Connection connection = getConnection(true)) {
                final PreparedStatement stmnt = connection.prepareStatement("SELECT version, expires, value FROM StorageRecords WHERE context =? AND id=?");
                stmnt.setString(1, context);
                stmnt.setString(2, key);
                log.debug("Querying {}", stmnt);
                final ResultSet resultSet = stmnt.executeQuery();
                if (!resultSet.next()) {
                    log.debug("Nothing returned");
                    return new Pair<>();
                }
                final Long returnedVersion = resultSet.getLong(1);
                final Long returnedExpires = getExpires(resultSet, 2);
                final String returnedValue = resultSet.getString(3);
                log.debug("Considering Version {}, Expires {}, Value {}", returnedVersion, returnedValue, returnedExpires);
                if (returnedExpires != null && System.currentTimeMillis() >= returnedExpires) {
                    log.debug("Read failed, key '{}' expired in context '{}'", key, context);
                    return new Pair<>();
                }
                if (version != null && returnedVersion == version) {
                    // Nothing's changed, so just echo back the version.
                    return new Pair<>(version, null);
                }
                if (resultSet.next()) {
                    log.error("Multiple values returned?");
                }
                final MutableStorageRecord<T> result = new MyStorageRecord<>(returnedValue, returnedExpires, returnedVersion);
                return new Pair<>(version, result);
            } catch (final SQLException e) {
                boolean retry = false;
                for (final String msg : retryableErrors) {
                    if (e.getSQLState() != null && e.getSQLState().contains(msg)) {
                        log.warn("Caught retryable SQL exception", e);
                        retry = true;
                        break;
                    }
                }
                
                if (retry) {
                    if (--retries < 0) {
                        log.warn("Error retryable, but retry limit exceeded");
                        throw new IOException(e);
                    }
                    log.info("Retrying JDBC Read operation");
                } else {
                    throw new IOException(e);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override public boolean update(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable @Positive final Long expiration) throws IOException {
        try {
            return updateImpl(null, context, key, value, expiration) != null;
        } catch (final VersionMismatchException e) {
            throw new IllegalStateException("Unexpected exception thrown by update.", e);
        }
    }

    /** {@inheritDoc} */
    @Override @Nullable public Long updateWithVersion(@Positive final long version,
            @Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable @Positive final Long expiration) throws IOException,
            VersionMismatchException {
        return updateImpl(version, context, key, value, expiration);
    }

    /** {@inheritDoc} */
    @Override public boolean updateExpiration(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nullable @Positive final Long expiration) throws IOException {
        try {
            return updateImpl(null, context, key, null, expiration) != null;
        } catch (final VersionMismatchException e) {
            throw new IllegalStateException("Unexpected exception thrown by updateExpiration.", e);
        }
    }
    
    /**
     * Updates the record matching the supplied parameters. Returns null if the record cannot be found or is expired.
     * 
     * @param version to check
     * @param context to search for
     * @param key to search for
     * @param value to update
     * @param expires to update
     * 
     * @return the version of the record after update, null if no record exists
     * @throws IOException if errors occur in the update process
     * @throws VersionMismatchException if the record found contains a version that does not match the parameter
     */
    @Nullable protected Long updateImpl(@Nullable final Long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nonnull @NotEmpty final String value,
            @Nullable @Positive final Long expires) throws IOException, VersionMismatchException {
        
        //
        // Constraints, Logging
        //        
        int retries = transactionRetry;
        while (true) {
            try (Connection connection = getConnection(false)) {
                final PreparedStatement selectStmnt = connection.prepareStatement("SELECT version, expires FROM StorageRecords WHERE context =? AND id=?");
                selectStmnt.setString(1, context);
                selectStmnt.setString(2, key);
                log.debug("Querying {}", selectStmnt);
                final String s = selectStmnt.toString();
                final ResultSet resultSet = selectStmnt.executeQuery();
                if (!resultSet.next()) {
                    log.debug("Nothing returned");
                    return null;
                }
                final Long returnedExpires = getExpires(resultSet, 2);
                final Long returnedVersion = resultSet.getLong(1);
                if (returnedExpires != null && System.currentTimeMillis() >= returnedExpires) {
                    log.debug("Update failed, key '{}' expired in context '{}'", key, context);
                    return null;
                }
    
                if (version != null && returnedVersion != version) {
                    // Caller is out of sync.
                    throw new VersionMismatchException();
                }
                final PreparedStatement updateStmnt = connection.prepareStatement("UPDATE StorageRecords SET value=?, version=?, expires=? WHERE context=? AND id=?");
                updateStmnt.setString(1, value);
                final Long newVersion = Long.valueOf(returnedVersion + 1);
                updateStmnt.setLong(2, newVersion);
                setExpires(updateStmnt, 3, expires);
                updateStmnt.setString(4, context);
                updateStmnt.setString(5, key);
                updateStmnt.executeUpdate();
                connection.commit();
                return newVersion;
            } catch (final SQLException e) {
                boolean retry = false;
                for (final String msg : retryableErrors) {
                    if (e.getSQLState() != null && e.getSQLState().contains(msg)) {
                        log.warn("Caught retryable SQL exception", e);
                        retry = true;
                        break;
                    }
                }
                
                if (retry) {
                    if (--retries < 0) {
                        log.warn("Error retryable, but retry limit exceeded");
                        throw new IOException(e);
                    }
                    log.info("Retrying JDBC Update Operation");
                } else {
                    throw new IOException(e);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override public boolean deleteWithVersion(@Positive final long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException, VersionMismatchException {
        return deleteImpl(version, context, key);
    }

    /** {@inheritDoc} */
    @Override public boolean delete(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key)
            throws IOException {
        try {
            return deleteImpl(null, context, key);
        } catch (final VersionMismatchException e) {
            throw new IllegalStateException("Unexpected exception thrown by delete.", e);
        }
    }
    
    /**
     * Deletes the record matching the supplied parameters.
     * 
     * @param version to check
     * @param context to search for
     * @param key to search for
     * 
     * @return whether the record was deleted
     * @throws IOException if errors occur in the delete process
     * @throws VersionMismatchException if the record found contains a version that does not match the parameter
     */
    protected boolean deleteImpl(@Nullable @Positive final Long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException, VersionMismatchException {
        //
        // Constraints, Logging
        //        
        int retries = transactionRetry;
        while (true) {
            try (Connection connection = getConnection(false)) {
                final PreparedStatement selectStmnt = connection.prepareStatement("SELECT version FROM StorageRecords WHERE context =? AND id=?");
                selectStmnt.setString(1, context);
                selectStmnt.setString(2, key);
                log.debug("Querying {}", selectStmnt);
                final String s = selectStmnt.toString();
                final ResultSet resultSet = selectStmnt.executeQuery();
                if (!resultSet.next()) {
                    log.debug("Nothing returned");
                    return false;
                }
                final Long returnedVersion = resultSet.getLong(1);
                if (version != null && returnedVersion != version) {
                    throw new VersionMismatchException();
                }
                final PreparedStatement deleteStmnt = connection.prepareStatement("DELETE FROM StorageRecords WHERE context=? AND id=?");
                deleteStmnt.setString(1, context);
                deleteStmnt.setString(2, key);
                deleteStmnt.execute();
                connection.commit();
                return true;
            } catch (final SQLException e) {
                boolean retry = false;
                for (final String msg : retryableErrors) {
                    if (e.getSQLState() != null && e.getSQLState().contains(msg)) {
                        log.warn("Caught retryable SQL exception", e);
                        retry = true;
                        break;
                    }
                }
                
                if (retry) {
                    if (--retries < 0) {
                        log.warn("Error retryable, but retry limit exceeded");
                        throw new IOException(e);
                    }
                    log.info("Retrying JDBC Delete Operation");
                } else {
                    throw new IOException(e);
                }
            }
        }
    }
    
    /**
     * Deletes every record with an expiration before the supplied expiration.
     * 
     * @param expiration of records to delete
     * 
     * @throws IOException if errors occur in the cleanup process
     */
    protected void deleteImpl(@Nonnull final Long expiration) throws IOException {
        //
        // Constraints, Logging
        //        
        int retries = transactionRetry;
        while (true) {
            try (Connection connection = getConnection(false)) {
                final PreparedStatement updateStmnt = connection.prepareStatement("DELETE FROM StorageRecords WHERE expires < ? ");
                updateStmnt.setLong(1, expiration);
                updateStmnt.execute();
                connection.commit();
                return;
            }
            catch (final SQLException e) {
                boolean retry = false;
                for (final String msg : retryableErrors) {
                    if (e.getSQLState() != null && e.getSQLState().contains(msg)) {
                        log.warn("Caught retryable SQL exception", e);
                        retry = true;
                        break;
                    }
                }
                
                if (retry) {
                    if (--retries < 0) {
                        log.warn("Error retryable, but retry limit exceeded");
                        throw new IOException(e);
                    }
                    log.info("Retrying JDBC DeletebyExpiration Operation");
                } else {
                    throw new IOException(e);
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void reap(String context) throws IOException {
        //
        // Constraints, Logging
        //
        int retries = transactionRetry;
        while (true) {
            try (Connection connection = getConnection(true)) {
                final PreparedStatement updateStmnt = connection.prepareStatement("DELETE FROM StorageRecords WHERE context = ? AND expires <= ?");
                updateStmnt.setString(1, context);
                setExpires(updateStmnt, 2, System.currentTimeMillis());
                updateStmnt.execute();
                connection.commit();
                return;
            }
            catch (final SQLException e) {
                boolean retry = false;
                for (final String msg : retryableErrors) {
                    if (e.getSQLState() != null && e.getSQLState().contains(msg)) {
                        log.warn("Caught retryable SQL exception", e);
                        retry = true;
                        break;
                    }
                }
                if (retry) {
                    if (--retries < 0) {
                        log.warn("Error retryable, but retry limit exceeded");
                        throw new IOException(e);
                    }
                    log.info("Retrying JDBC DeleteByContext Operation");
                } else {
                    throw new IOException(e);
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void updateContextExpiration(String context, Long expires) throws IOException {
        //
        // Constraints, Logging
        //
        int retries = transactionRetry;
        while (true) {
            try (Connection connection = getConnection(true)) {
                final PreparedStatement updateStmnt = connection.prepareStatement("UPDATE StorageRecords SET expires = ? WHERE context = ? AND expires > ? ");
                setExpires(updateStmnt, 1, expires);
                updateStmnt.setString(2, context);
                setExpires(updateStmnt, 3, System.currentTimeMillis());
                updateStmnt.execute();
                connection.commit();
                return;
            }
            catch (final SQLException e) {
                boolean retry = false;
                for (final String msg : retryableErrors) {
                    if (e.getSQLState() != null && e.getSQLState().contains(msg)) {
                        log.warn("Caught retryable SQL exception", e);
                        retry = true;
                        break;
                    }
                }
                if (retry) {
                    if (--retries < 0) {
                        log.warn("Error retryable, but retry limit exceeded");
                        throw new IOException(e);
                    }
                    log.info("Retrying JDBC DeleteByContext Operation");
                } else {
                    throw new IOException(e);
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void deleteContext(String context) throws IOException {
        //
        // Constraints, Logging
        //        
        int retries = transactionRetry;
        while (true) {
            try (Connection connection = getConnection(true)) {
                final PreparedStatement updateStmnt = connection.prepareStatement("DELETE FROM StorageRecords WHERE context = ? ");
                updateStmnt.setString(1, context);
                updateStmnt.execute();
                connection.commit();
                return;
            }
            catch (final SQLException e) {
                boolean retry = false;
                for (final String msg : retryableErrors) {
                    if (e.getSQLState() != null && e.getSQLState().contains(msg)) {
                        log.warn("Caught retryable SQL exception", e);
                        retry = true;
                        break;
                    }
                }
                
                if (retry) {
                    if (--retries < 0) {
                        log.warn("Error retryable, but retry limit exceeded");
                        throw new IOException(e);
                    }
                    log.info("Retrying JDBC DeleteByContext Operation");
                } else {
                    throw new IOException(e);
                }
            }
        }
    }
    
    /**
     * Obtain a connection from the data source.
     * 
     * <p>The caller must close the connection.</p>
     * 
     * @param autoCommit auto-commit setting to apply to the connection
     * 
     * @return a fresh connection
     * @throws SQLException if an error occurs
     */
    @Nonnull private Connection getConnection(final boolean autoCommit) throws SQLException {
        final Connection conn = dataSource.getConnection();
        conn.setAutoCommit(autoCommit);
        conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        return conn;
    }
    
    /** Return the value of expires in the supplied column of the supplied {@link ResultSet}.
     * @param results the results whose current row we want to inspect
     * @param columm the column
     * @return the expiration (converting an SQL null into a null)
     * @throws SQLException if the results interrogation fails
     */
    @Nullable private static Long getExpires(@Nonnull final ResultSet results, final int columm) throws SQLException {
        final long value = results.getLong(columm);
        if (results.wasNull()) {
            return null;
        }
        return value;
    }
    
    /** Set the value of expiration into the prepared statement at the suppiled column
     * converting java nulls into SQL nulls.
     * 
     * @param stmnt where to put it
     * @param column which column to put it in
     * @param expires
     * @throws SQLException 
     */
    private static void setExpires(@Nonnull final PreparedStatement stmnt,
            final int column, final @Nullable Long expires) throws SQLException {
        if (expires == null) {
            stmnt.setNull(column, Types.BIGINT);
        } else {
            stmnt.setLong(column, expires);
        }
    }


    /** {@inheritDoc} */
    public boolean isServerSide() {
        return true;
    }

    /** {@inheritDoc} */
    public boolean isClustered() {
        return false;
    }
    
    /** {@inheritDoc} */
    @Override @Nullable protected TimerTask getCleanupTask() {
        return new TimerTask() {

            /** {@inheritDoc} */
            @Override public void run() {
                final Long now = System.currentTimeMillis();
                log.debug("Running cleanup task at {}", now);
                try {
                    deleteImpl(now);
                } catch (final IOException e) {
                    log.error("Error running cleanup task for {}", now, e);
                }
                log.debug("Finished cleanup task for {}", now);
            }
        };
    }


    private static class MyStorageRecord<T> extends MutableStorageRecord<T> {

        /**
         * Constructor.
         *
         * @param val
         * @param exp
         */
        public MyStorageRecord(String val, Long exp, Long version) {
            super(val, exp);
            if (version != null) {
                setVersion(version);
            }
        }
        
    }

    
}
