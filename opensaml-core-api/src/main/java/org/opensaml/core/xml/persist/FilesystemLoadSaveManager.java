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

package org.opensaml.core.xml.persist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSource;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.xml.ParserPool;
import net.shibboleth.shared.xml.XMLParserException;
import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * Implementation of {@link XMLObjectLoadSaveManager} which uses a local filesystem to load and store serialized XML.
 * 
 * <p>
 * The primary required configuration is a base directory path under which files of serialized XML will be located.
 * The file name to use is simply the specified String index key, which is treated as an immediate child
 * file name of the base directory.
 * Callers are required to ensure that index keys are acceptable as file names on the platform(s) 
 * on which this manager is used.
 * </p>
 *
 * @param <T> the specific base XML object type being managed
 */
@NotThreadSafe
public class FilesystemLoadSaveManager<T extends XMLObject> extends AbstractConditionalLoadXMLObjectLoadSaveManager<T> {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(FilesystemLoadSaveManager.class);
    
    /** The base directory used for storing individual serialized XML files. */
    private File baseDirectory;
    
    /** Optional strategy function which produces the intermediate directory path(s) between
     * the <code>baseDirectory</code> and the actual file. */
    private Function<String, List<String>> intermediateDirectoryStrategy;

    /** Parser pool instance for deserializing XML from the filesystem. */
    private ParserPool parserPool;
    
    /**
     * Constructor.
     *
     * @param baseDir the base directory, must be an absolute path
     */
    public FilesystemLoadSaveManager(
            @ParameterName(name="baseDir") @Nonnull final String baseDir) {
        this(new File(Constraint.isNotNull(StringSupport.trimOrNull(baseDir), 
                "Base directory string instance was null or empty")),
                null,
                false,
                null);
    }

    /**
     * Constructor.
     *
     * @param baseDir the base directory, must be an absolute path
     * @param conditionalLoad whether {@link #load(String)} should behave
     *      as defined in {@link ConditionalLoadXMLObjectLoadSaveManager}
     */
    public FilesystemLoadSaveManager(
            @ParameterName(name="baseDir") @Nonnull final String baseDir,
            @ParameterName(name="conditionalLoad") final boolean conditionalLoad) {
        this(new File(Constraint.isNotNull(StringSupport.trimOrNull(baseDir), 
                "Base directory string instance was null or empty")),
                null,
                conditionalLoad,
                null);
    }
    
    /**
     * Constructor.
     *
     * @param baseDir the base directory, must be an absolute path
     */
    public FilesystemLoadSaveManager(
            @ParameterName(name="baseDirFile") @Nonnull final File baseDir) {
        this(baseDir, null, false, null);
    }
    
    /**
     * Constructor.
     *
     * @param baseDir the base directory, must be an absolute path
     * @param dirStrategy the intermediate directory strategy
     */
    public FilesystemLoadSaveManager(
            @ParameterName(name="baseDirFile") @Nonnull final File baseDir,
            @ParameterName(name="intermediateDirectoryStrategy")
                @Nullable final Function<String, List<String>> dirStrategy) {
        this(baseDir, null, false, dirStrategy);
    }

    /**
     * Constructor.
     *
     * @param baseDir the base directory, must be an absolute path
     * @param conditionalLoad whether {@link #load(String)} should behave 
     *      as defined in {@link ConditionalLoadXMLObjectLoadSaveManager}
     */
    public FilesystemLoadSaveManager(
            @ParameterName(name="baseDirFile") @Nonnull final File baseDir, 
            @ParameterName(name="conditionalLoad") final boolean conditionalLoad) {
        this(baseDir, null, conditionalLoad, null);
    }
    
    /**
     * Constructor.
     *
     * @param baseDir the base directory, must be an absolute path
     * @param conditionalLoad whether {@link #load(String)} should behave
     *      as defined in {@link ConditionalLoadXMLObjectLoadSaveManager}
     * @param dirStrategy the intermediate directory strategy
     */
    public FilesystemLoadSaveManager(
            @ParameterName(name="baseDirFile") @Nonnull final File baseDir,
            @ParameterName(name="conditionalLoad") final boolean conditionalLoad,
            @ParameterName(name="intermediateDirectoryStrategy")
                @Nullable final Function<String, List<String>> dirStrategy) {
        this(baseDir, null, conditionalLoad, dirStrategy);
    }

    /**
     * Constructor.
     *
     * @param baseDir the base directory, must be an absolute path
     * @param pp the parser pool instance to use
     */
    public FilesystemLoadSaveManager(
            @ParameterName(name="baseDir") @Nonnull final String baseDir, 
            @ParameterName(name="parserPool") @Nullable final ParserPool pp) {
        this(new File(Constraint.isNotNull(StringSupport.trimOrNull(baseDir), 
                "Base directory string instance was null or empty")),
                pp, 
                false,
                null);
    }

    /**
     * Constructor.
     *
     * @param baseDir the base directory, must be an absolute path
     * @param pp the parser pool instance to use
     * @param conditionalLoad whether {@link #load(String)} should behave 
     *      as defined in {@link ConditionalLoadXMLObjectLoadSaveManager}
     */
    public FilesystemLoadSaveManager(
            @ParameterName(name="baseDir") @Nonnull final String baseDir, 
            @ParameterName(name="parserPool") @Nullable final ParserPool pp,
            @ParameterName(name="conditionalLoad") final boolean conditionalLoad) {
        this(new File(Constraint.isNotNull(StringSupport.trimOrNull(baseDir), 
                "Base directory string instance was null or empty")),
                pp, conditionalLoad, null);
    }
    /**
     * Constructor.
     *
     * @param baseDir the base directory, must be an absolute path
     * @param pp the parser pool instance to use
     */
    public FilesystemLoadSaveManager(
            @ParameterName(name="baseDirFile") @Nonnull final File baseDir, 
            @ParameterName(name="parserPool") @Nullable final ParserPool pp) {
        this(baseDir, pp, false, null);
    }

    /**
     * Constructor.
     *
     * @param baseDir the base directory, must be an absolute path
     * @param pp the parser pool instance to use
     * @param conditionalLoad whether {@link #load(String)} should behave
     *      as defined in {@link ConditionalLoadXMLObjectLoadSaveManager}
     */
    public FilesystemLoadSaveManager(
            @ParameterName(name="baseDirFile") @Nonnull final File baseDir,
            @ParameterName(name="parserPool") @Nullable final ParserPool pp,
            @ParameterName(name="conditionalLoad") final boolean conditionalLoad) {
        this(baseDir, pp, conditionalLoad, null);
    }

    /**
     * Constructor.
     *
     * @param baseDir the base directory, must be an absolute path
     * @param pp the parser pool instance to use
     * @param conditionalLoad whether {@link #load(String)} should behave
     *      as defined in {@link ConditionalLoadXMLObjectLoadSaveManager}
     *  @param dirStrategy the intermediate directory strategy
     */
    public FilesystemLoadSaveManager(
            @ParameterName(name="baseDirFile") @Nonnull final File baseDir,
            @ParameterName(name="parserPool") @Nullable final ParserPool pp,
            @ParameterName(name="conditionalLoad") final boolean conditionalLoad,
            @ParameterName(name="intermediateDirectoryStrategy")
                @Nullable final Function<String, List<String>> dirStrategy) {

        super(conditionalLoad);
        
        baseDirectory = Constraint.isNotNull(baseDir, "Base directory File instance was null");
        Constraint.isTrue(baseDirectory.isAbsolute(), "Base directory specified was not an absolute path");
        if (baseDirectory.exists()) {
            Constraint.isTrue(baseDirectory.isDirectory(), "Existing base directory path was not a directory");
        } else {
            Constraint.isTrue(baseDirectory.mkdirs(), "Base directory did not exist and could not be created");
        }
        
        parserPool = pp;
        if (parserPool == null) {
            parserPool = Constraint.isNotNull(XMLObjectProviderRegistrySupport.getParserPool(),
                    "Specified ParserPool was null and global ParserPool was not available");
        }

        intermediateDirectoryStrategy = dirStrategy;
    }

    /** {@inheritDoc} */
    public Set<String> listKeys() throws IOException {
        return java.nio.file.Files.walk(baseDirectory.toPath())
                .filter(java.nio.file.Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toUnmodifiableSet());
    }

    /** {@inheritDoc} */
    public Iterable<Pair<String, T>> listAll() throws IOException {
        return new FileIterable(listKeys());
    }

    /** {@inheritDoc} */
    public boolean exists(final String key) throws IOException {
        return buildFile(key).exists();
    }

    /** {@inheritDoc} */
    public T load(final String key) throws IOException {
        final File file = buildFile(key);
        if (!file.exists()) {
            log.debug("Target file with key '{}' does not exist, path: {}", key, file.getAbsolutePath());
            clearLoadLastModified(key);
            return null;
        }
        if (isLoadConditionally() && isUnmodifiedSinceLastLoad(key)) {
            log.debug("Target file with key '{}' has not been modified since the last request, returning null: {}", 
                    key, file.getAbsolutePath());
            return null;
        }
        try (final FileInputStream fis = new FileInputStream(file)) {
            final byte[] source = ByteStreams.toByteArray(fis);
            try (final ByteArrayInputStream bais = new ByteArrayInputStream(source)) {
                final XMLObject xmlObject = XMLObjectSupport.unmarshallFromInputStream(parserPool, bais);
                xmlObject.getObjectMetadata().put(new XMLObjectSource(source));
                updateLoadLastModified(key, Instant.ofEpochMilli(file.lastModified()));
                //TODO via ctor, etc, does caller need to supply a Class so we can can test and throw an IOException, 
                // rather than an unchecked ClassCastException?
                return (T) xmlObject;
            } catch (final XMLParserException|UnmarshallingException e) {
                throw new IOException(String.format("Error loading file from path: %s", file.getAbsolutePath()), e);
            }
        }
    }
    
    /** {@inheritDoc} */
    protected synchronized boolean isUnmodifiedSinceLastLoad(@Nonnull final String key) throws IOException {
        final File file = buildFile(key);
        final Instant lastModified = Instant.ofEpochMilli(file.lastModified());
        log.trace("File '{}' last modified was: {}", file.getAbsolutePath(), lastModified);
        return getLoadLastModified(key) != null && !lastModified.isAfter(getLoadLastModified(key));
    }

    /** {@inheritDoc} */
    public void save(final String key, final T xmlObject) throws IOException {
        save(key, xmlObject, false);
    }

    /** {@inheritDoc} */
    public void save(final String key, final T xmlObject, final boolean overwrite) throws IOException {
        if (!overwrite && exists(key)) {
            throw new IOException(
                    String.format("Target file already exists for key '%s' and overwrite not indicated", key));
        }
        
        final File file = buildFile(key);

        checkAndCreateIntermediateDirectories(file);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            final List<XMLObjectSource> sources = xmlObject.getObjectMetadata().get(XMLObjectSource.class);
            if (sources.size() == 1) {
                log.debug("XMLObject contained 1 XMLObjectSource instance, persisting existing byte[]");
                final XMLObjectSource source = sources.get(0);
                fos.write(source.getObjectSource());
            } else {
                log.debug("XMLObject contained {} XMLObjectSource instances, persisting marshalled object", 
                        sources.size());
                try {
                    XMLObjectSupport.marshallToOutputStream(xmlObject, fos);
                } catch (final MarshallingException e) {
                    throw new IOException(String.format("Error saving target file: %s", file.getAbsolutePath()), e);
                }
            } 
            fos.flush();
        }
        
    }

    /** {@inheritDoc} */
    public boolean remove(final String key) throws IOException {
        final File file = buildFile(key);
        if (file.exists()) {
            final boolean success = file.delete();
            if (success) {
                clearLoadLastModified(key);
                return true;
            }
            throw new IOException(String.format("Error removing target file: %s", file.getAbsolutePath()));
        }
        return false;
    }

    /** {@inheritDoc} */
    public boolean updateKey(final String currentKey, final String newKey) throws IOException {
        final File currentFile = buildFile(currentKey);
        if (!currentFile.exists()) {
            return false;
        }
        
        final File newFile = buildFile(newKey);
        if (newFile.exists()) {
            throw new IOException(String.format("Specified new key already exists: %s", newKey));
        }

        checkAndCreateIntermediateDirectories(newFile);

        Files.move(currentFile, newFile);
        updateLoadLastModified(newKey, getLoadLastModified(currentKey));
        clearLoadLastModified(currentKey);
        return true;
    }

    /**
     * Check and create intermediate directories between the <code>baseDirectory</code> and the actual file,
     * if necessary.
     *
     * @param file the target file whose path is to be evaluated
     *
     * @throws IOException if the intermediate directory creation fails
     */
    protected void checkAndCreateIntermediateDirectories(@Nonnull final File file) throws IOException {
        final File parentDir = new File(file.getParent());

        if (!baseDirectory.equals(parentDir) && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException(String.format("Could not create intermediate directories for target file: %s",
                        file.getAbsolutePath()));
            }
        }
    }

    /**
     * Build the target file name from the specified index key and the configured base directory.
     * 
     * @param key the target file name index key
     * @return the constructed File instance for the target file
     * @throws IOException if there is a fatal error constructing or evaluating the candidate target path
     */
    public File buildFile(final String key) throws IOException {
        File parentDirectory = baseDirectory;
        if (intermediateDirectoryStrategy != null) {
            final List<String> intermediateDirs = intermediateDirectoryStrategy.apply(key);
            if (intermediateDirs != null && !intermediateDirs.isEmpty()) {
                for (final String dir : intermediateDirs) {
                    parentDirectory = new File(parentDirectory, dir);
                }
            }
        }

        final File path = new File(parentDirectory,
                Constraint.isNotNull(StringSupport.trimOrNull(key), "Input key was null or empty"));
        if (path.exists() && !path.isFile()) {
            throw new IOException(String.format("Path exists based on specified key, but is not a file: %s", 
                    path.getAbsolutePath()));
        }
        return path;
    }
    
    /**
     * Iterable which provides lazy iteration over the managed files.
     */
    private class FileIterable implements Iterable<Pair<String, T>> {
        
        /** Snapshot of filesystem keys at time of construction. */
        private Set<String> keys;

        /**
         * Constructor.
         *
         * @param filenames Snapshot of filesystem keys at time of construction
         */
        public FileIterable(@Nonnull final Collection<String> filenames) {
            keys = filenames.stream().filter(s -> s != null).collect(Collectors.toSet());
        }

        /** {@inheritDoc} */
        public Iterator<Pair<String, T>> iterator() {
            return new FileIterator(keys);
        }
        
    }
    
    /**
     * Iterator which provides lazy iteration over the managed files.
     */
    private class FileIterator implements Iterator<Pair<String, T>> {
        
        /** Iterator for the keys. */
        private Iterator<String> keysIter;
        
        /** Current value to return from next(). */
        private Pair<String, T> current;
        
        /**
         * Constructor.
         *
         * @param filenames Snapshot of filesystem keys at time of construction
         */
        public FileIterator(@Nonnull final Collection<String> filenames) {
            final Set<String> keys = filenames.stream().filter(s -> s != null).collect(Collectors.toSet());
            keysIter = keys.iterator();
        }

        /** {@inheritDoc} */
        public boolean hasNext() {
            if (current != null) {
                return true;
            }
            
            current = getNext();
            
            return current != null;
        }

        /** {@inheritDoc} */
        public Pair<String, T> next() {
            if (current != null) {
                final Pair<String, T> temp = current;
                current = null;
                return temp;
            }
            final Pair<String, T> temp = getNext();
            if (temp != null) {
                return temp;
            }
            throw new NoSuchElementException();
        }

        /** {@inheritDoc} */
        public void remove() {
            //TODO can we support?  Probably.
            throw new UnsupportedOperationException();
        }
        
        /**
         * Internal support to get the next item for iteration.
         * 
         * @return the next item for iteration, or null if no more items
         */
        private Pair<String, T> getNext() {
            while (keysIter.hasNext()) {
                final String key = keysIter.next();
                try {
                    final T xmlObject = load(key);
                    if (xmlObject != null) {
                        // This is to defensively guard against files being removed after files/keys are enumerated.
                        // Don't fail, just skip
                        return new Pair<>(key, xmlObject);
                    }
                    log.warn("Target file with key '{}' was removed since iterator creation, skipping", key);
                } catch (final IOException e) {
                    log.warn("Error loading target file with key '{}'", key, e);
                }
            }
            return null;
        }
        
    }

}
