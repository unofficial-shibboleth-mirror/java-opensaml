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

package org.opensaml.storage.impl.client;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

import org.opensaml.storage.MutableStorageRecord;
import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.security.DataSealerException;

/**
 * JSON-based storage for {@link ClientStorageService}.
 */
public class JSONClientStorageServiceStore extends AbstractClientStorageServiceStore {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(JSONClientStorageServiceStore.class);
        
    /** {@inheritDoc} */
    public void doLoad(@Nullable @NotEmpty final String raw) throws IOException {
        try {
            final JsonReader reader = Json.createReader(new StringReader(raw));
            final JsonStructure st = reader.read();
            if (!(st instanceof JsonObject)) {
                throw new JsonException("Found invalid data structure while parsing context map");
            }
            final JsonObject obj = (JsonObject) st;
            
            for (final Map.Entry<String,JsonValue> context : obj.entrySet()) {
                if (context.getValue().getValueType() != JsonValue.ValueType.OBJECT) {
                    throw new JsonException("Found invalid data structure while parsing context map");
                }
                
                // Create new context if necessary.
                Map<String,MutableStorageRecord<?>> dataMap = getContextMap().get(context.getKey());
                if (dataMap == null) {
                    dataMap = new HashMap<>();
                    getContextMap().put(context.getKey(), dataMap);
                }
                
                final JsonObject contextRecords = (JsonObject) context.getValue();
                for (final Map.Entry<String,JsonValue> record : contextRecords.entrySet()) {
                
                    final JsonObject fields = (JsonObject) record.getValue();
                    Long exp = null;
                    if (fields.containsKey("x")) {
                        exp = fields.getJsonNumber("x").longValueExact();
                    }
                    
                    dataMap.put(record.getKey(), new MutableStorageRecord<>(fields.getString("v"), exp));
                }
            }
            setDirty(false);
        } catch (final NullPointerException | ClassCastException | ArithmeticException | JsonException e) {
            log.error("Found invalid data structure while parsing context map", e);
            throw new IOException(e);
        }
    }

//Checkstyle: CyclomaticComplexity OFF        
    /** {@inheritDoc} */
    @Nullable public ClientStorageServiceOperation save(@Nonnull final ClientStorageService storageService)
            throws IOException {
        
        if (!isDirty()) {
            log.trace("{} Storage state has not been modified, save operation skipped", storageService.getLogPrefix());
            return null;
        }
        
        final ClientStorageSource source = getSource();
        if (source == null) {
            throw new IOException("Client storage medium not set");
        }
        
        if (getContextMap().isEmpty()) {
            log.trace("{} Data is empty", storageService.getLogPrefix());
            return new ClientStorageServiceOperation(storageService.ensureId(), storageService.getStorageName(), null,
                    source);
        }

        long exp = 0L;
        final long now = System.currentTimeMillis();
        boolean empty = true;

        try {
            final StringWriter sink = new StringWriter(128);
            final JsonGenerator gen = Json.createGenerator(sink);
            
            gen.writeStartObject();
            for (final Map.Entry<String,Map<String, MutableStorageRecord<?>>> context
                    : getContextMap().entrySet()) {
                if (!context.getValue().isEmpty()) {
                    gen.writeStartObject(context.getKey());
                    for (final Map.Entry<String,MutableStorageRecord<?>> entry : context.getValue().entrySet()) {
                        final MutableStorageRecord<?> record = entry.getValue();
                        final Long recexp = record.getExpiration();
                        if (recexp == null || recexp > now) {
                            empty = false;
                            gen.writeStartObject(entry.getKey())
                                .write("v", record.getValue());
                            if (recexp != null) {
                                gen.write("x", recexp);
                                exp = Math.max(exp, recexp);
                            }
                            gen.writeEnd();
                        }
                    }
                    gen.writeEnd();
                }
            }
            gen.writeEnd().close();

            if (empty) {
                log.trace("{} Data is empty", storageService.getLogPrefix());
                return new ClientStorageServiceOperation(storageService.getId(), storageService.getStorageName(), null,
                        source);
            }
            
            final String raw = sink.toString();
            
            log.trace("{} Size of data before encryption is {}", storageService.getLogPrefix(), raw.length());
            log.trace("{} Data before encryption is {}", storageService.getLogPrefix(), raw);
            try {
                final String wrapped = storageService.getDataSealer().wrap(raw,
                        exp > 0 ? Instant.ofEpochMilli(exp) : Instant.now().plus(Duration.ofDays(1)));
                log.trace("{} Size of data after encryption is {}", storageService.getLogPrefix(), wrapped.length());
                setDirty(false);
                return new ClientStorageServiceOperation(storageService.getId(), storageService.getStorageName(),
                        wrapped, source);
            } catch (final DataSealerException e) {
                throw new IOException(e);
            }
        } catch (final JsonException e) {
            throw new IOException(e);
        }
    }
//Checkstyle: CyclomaticComplexity ON
    
    /** Factory for JSON-backed store. */
    public static class JSONClientStorageServiceStoreFactory implements Factory {

        /** {@inheritDoc} */
        @Nonnull public ClientStorageServiceStore load(@Nullable @NotEmpty final String raw,
                @Nonnull final ClientStorageSource src) {
            final ClientStorageServiceStore store = new JSONClientStorageServiceStore();
            store.load(raw, src);
            return store;
        }
    }

}