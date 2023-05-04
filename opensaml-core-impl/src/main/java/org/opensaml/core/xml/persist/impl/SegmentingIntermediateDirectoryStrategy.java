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

package org.opensaml.core.xml.persist.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLRuntimeException;
import org.opensaml.core.xml.persist.FilesystemLoadSaveManager;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Strategy function for producing intermediate directories from an input key.
 *
 * <p>
 * Typically used with {@link FilesystemLoadSaveManager}.
 * </p>
 */
public class SegmentingIntermediateDirectoryStrategy implements Function<String, List<String>> {

    /** Logger. **/
    @Nonnull private Logger log = LoggerFactory.getLogger(SegmentingIntermediateDirectoryStrategy.class);

    /** Strategy function for generating the source data from the input key.*/
    @Nonnull private Function<String,String> sourceStrategy;

    /** The number of segments to produce. **/
    private int segmentNumber;

    /** The length of each produced segment. **/
    private int segmentLength;

    /**
     * Constructor.
     * @param number number of segments
     * @param length length of each segment
     * @param source source strategy function
     */
    public SegmentingIntermediateDirectoryStrategy(
            @ParameterName(name="segmentNumber") final int number,
            @ParameterName(name="segmentLength") final int length,
            @ParameterName(name="sourceStrategy") final @Nonnull Function<String,String> source) {
        segmentNumber = Constraint.isGreaterThan(0, number, "Number of segments was zero");
        segmentLength = Constraint.isGreaterThan(0, length, "Length of segments was zero");
        sourceStrategy = Constraint.isNotNull(source, "Source strategy was null");
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<String> apply(final String key) {
        final String source = sourceStrategy.apply(key);
        if (source == null || source.length() == 0) {
            log.trace("Source strategy returned null or empty, returning null");
            return null;
        }

        log.trace("Resolved source: {}", source);

        if (source.length() < segmentNumber * segmentLength) {
            final String msg = String.format("Source length %d is less than number (%d) * length (%d) of segments: %s",
                    source.length(), segmentNumber, segmentLength, source);
            log.warn(msg);
            throw new XMLRuntimeException(msg);
        }

        final ArrayList<String> segments = new ArrayList<>();
        for (int i=0; i<segmentNumber; i++) {
            final int startIndex = i * segmentLength;
            final int endIndex = startIndex + segmentLength;
            final String segment = key.substring(startIndex, endIndex);
            log.trace("Produced directory segment: {}", segment);
            segments.add(segment);
        }

        return segments;
    }

}