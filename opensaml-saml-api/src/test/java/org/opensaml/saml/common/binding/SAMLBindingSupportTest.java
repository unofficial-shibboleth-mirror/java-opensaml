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

package org.opensaml.saml.common.binding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.decoder.MessageDecodingException;
import org.slf4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.io.NoWrapAutoEndDeflaterOutputStream;
import net.shibboleth.shared.logic.ConstraintViolationException;
import net.shibboleth.shared.primitive.LoggerFactory;

@SuppressWarnings("javadoc")
public class SAMLBindingSupportTest {
    
    private Logger log = LoggerFactory.getLogger(SAMLBindingSupportTest.class);
    
    @Test
    public void testConvertSAML2ArtifactEndpointIndex() {
        Assert.assertEquals(SAMLBindingSupport.convertSAML2ArtifactEndpointIndex(new byte[] {0x00, 0x00}), 0);
        Assert.assertEquals(SAMLBindingSupport.convertSAML2ArtifactEndpointIndex(new byte[] {0x00, 0x01}), 1);
        Assert.assertEquals(SAMLBindingSupport.convertSAML2ArtifactEndpointIndex(new byte[] {0x00, 0x02}), 2);
        
        Assert.assertEquals(SAMLBindingSupport.convertSAML2ArtifactEndpointIndex(new byte[] {0x00, 0x10}), 16);
        
        Assert.assertEquals(SAMLBindingSupport.convertSAML2ArtifactEndpointIndex(new byte[] {0x00, (byte) 0xFE}), 254);
        Assert.assertEquals(SAMLBindingSupport.convertSAML2ArtifactEndpointIndex(new byte[] {0x00, (byte) 0xFF}), 255);
        
        // This is the largest unsigned value supported
        Assert.assertEquals(SAMLBindingSupport.convertSAML2ArtifactEndpointIndex(new byte[] {(byte) 0x7F, (byte) 0xFF}), 32767);
        
        try {
            SAMLBindingSupport.convertSAML2ArtifactEndpointIndex(new byte[] {(byte) 0x80, (byte) 0x00});
            Assert.fail("Should have failed on input resulting in negative int");
        } catch (ConstraintViolationException e) {
            // expected
        }
        
        try {
            SAMLBindingSupport.convertSAML2ArtifactEndpointIndex(new byte[] {0x02});
            Assert.fail("Should have failed on too short input");
        } catch (ConstraintViolationException e) {
            // expected
        }
        
        try {
            SAMLBindingSupport.convertSAML2ArtifactEndpointIndex(new byte[] {0x00, 0x00, 0x02});
            Assert.fail("Should have failed on too long input");
        } catch (ConstraintViolationException e) {
            // expected
        }
    }
    
    @DataProvider()
    public Object[][] getMessageTypeData() {
        return new Object[][] {
            new Object[] { Map.of("SAMLRequest", List.of("abc123"), "test", List.of("foo")), MessageType.REQUEST},
            new Object[] { Map.of("SAMLResponse", List.of("abc123"), "test", List.of("foo")), MessageType.RESPONSE},
            new Object[] { Map.of("SAMLart", List.of("abc123"), "test", List.of("foo")), MessageType.ARTIFACT},

            // These are invalid, and should cause failure in the tested method
            new Object[] { Map.of(), null},
            new Object[] { Map.of("SAMLRequest", List.of("abc123"), "SAMLResponse", List.of("def456")), null},
            
            // These are invalid, but shouldn't cause failure in the tested method
            new Object[] { Map.of("SAMLRequest", List.of("abc123", "def456")), MessageType.REQUEST},
            new Object[] { Map.of("SAMLResponse", List.of("abc123", "def456")), MessageType.RESPONSE},
            new Object[] { Map.of("SAMLart", List.of("abc123", "def456")), MessageType.ARTIFACT},
        };
    }
    
    @Test(dataProvider = "getMessageTypeData")
    public void testGetMessageType(@Nonnull final Map<String,List<String>> httpParams, @Nullable final MessageType expected) {
        final MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpParams.forEach((name, values) -> 
            values.forEach(value ->
                httpRequest.addParameter(name, value)));
        
        final MessageType messageType = SAMLBindingSupport.getMessageType(httpRequest);
        Assert.assertEquals(messageType, expected);
    }

    @DataProvider()
    public Object[][] validateMessageTypeValuesData() {
        return new Object[][] {
            new Object[] { Map.of("SAMLRequest", List.of("abc123")), MessageType.REQUEST, true},
            new Object[] { Map.of("SAMLResponse", List.of("abc123")), MessageType.RESPONSE, true},
            new Object[] { Map.of("SAMLart", List.of("abc123")), MessageType.ARTIFACT, true},

            // These are invalid, but shouldn't cause failure in the tested method
            new Object[] { Map.of("SAMLRequest", List.of("abc123", "def456")), MessageType.REQUEST, false},
            new Object[] { Map.of("SAMLResponse", List.of("abc123", "def456")), MessageType.RESPONSE, false},
            new Object[] { Map.of("SAMLart", List.of("abc123", "def456")), MessageType.ARTIFACT, false},
            
            // These are invalid, but shouldn't cause failure in the tested method
            new Object[] { Map.of("SAMLRequest", List.of()), MessageType.REQUEST, true},
            new Object[] { Map.of("SAMLResponse", List.of()), MessageType.RESPONSE, true},
            new Object[] { Map.of("SAMLart", List.of()), MessageType.ARTIFACT, true},
        };
    }

    @Test(dataProvider = "validateMessageTypeValuesData")
    public void testValidateMessageTypeValues(@Nonnull final Map<String,List<String>> httpParams, @Nonnull final MessageType messageType,
            final boolean expectedSuccess) {

        final MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpParams.forEach((name, values) -> 
            values.forEach(value ->
                httpRequest.addParameter(name, value)));
        
        try {
            SAMLBindingSupport.validateMessageTypeValues(httpRequest, messageType);
            if (!expectedSuccess) {
                Assert.fail("Evaluation succeeded when should have failed");
            }
        } catch (final MessageDecodingException e) {
             if (expectedSuccess) {
                Assert.fail("Evaluation failed when should have succeeded ");
            }
        }
    }
    
    @DataProvider
    public Object[][] evaluateMessageSizeLimitData() {
        return new Object[][] {
            // Success cases
            new Object[] {false, 100, 50, true},
            new Object[] {false, null, null, true},
            new Object[] {false, 100, null, true},
            new Object[] {false, null, 50, true},
            new Object[] {true, 100, 50, true },

            // Fail cases
            new Object[] {true, 50, 100, false},
            new Object[] {true, null, null, false},
            new Object[] {true, null, 100, false},
            new Object[] {true, 50, null, false},
        };
    }

    @Test(dataProvider = "evaluateMessageSizeLimitData")
    public void testEvaluateMessageSizeLimit(final boolean enabled, @Nullable final Integer limit, @Nullable final Integer size,
            final boolean expectedSuccess) {
        
        try {
            SAMLBindingSupport.evaluateMessageSizeLimit(enabled, limit, size);
            if (!expectedSuccess) {
                Assert.fail("Evaluation succeeded when should have failed");
            }
        } catch (final MessageDecodingException e) {
            if (expectedSuccess) {
                Assert.fail("Evaluation failed when should have succeeded ");
            }
        }
    }
    
    @DataProvider
    public Iterator<Object[]> getBase64SizeData() throws Exception {
        ArrayList<Object[]> args = new ArrayList<>();
        
        final List<String> testValues = List.of(
                "",
                "a",
                "ab",
                "abc",
                "abcd",
                "abcde",
                "abcdef",
                "abcdefg",
                "abcdefgh",
                "abcdefghi",
                "abcdefghij",
                "abcdefghijk",
                "abcdefghijkl",
                "abcdefghijklm",
                "abcdefghijklmn",
                "abcdefghijklmno",
                "abcdefghijklmnop",
                "abcdefghijklmnopq",
                "abcdefghijklmnopqr",
                "abcdefghijklmnopqrs",
                "abcdefghijklmnopqrst",
                "abcdefghijklmnopqrstu",
                "abcdefghijklmnopqrstuv",
                "abcdefghijklmnopqrstuvw",
                "abcdefghijklmnopqrstuvwx",
                "abcdefghijklmnopqrstuvwxy",
                "abcdefghijklmnopqrstuvwxyz",
                "abcdefghijklmnopqrstuvwxyz0",
                "abcdefghijklmnopqrstuvwxyz01",
                "abcdefghijklmnopqrstuvwxyz012",
                "abcdefghijklmnopqrstuvwxyz0123",
                "abcdefghijklmnopqrstuvwxyz01234",
                "abcdefghijklmnopqrstuvwxyz012345",
                "abcdefghijklmnopqrstuvwxyz0123456",
                "abcdefghijklmnopqrstuvwxyz01234567",
                "abcdefghijklmnopqrstuvwxyz012345678",
                "abcdefghijklmnopqrstuvwxyz0123456789"
                );
        
        for (final String value : testValues) {
            byte[] bytes = value.getBytes("UTF-8");
            assert bytes != null;
            final String encoded = Base64Support.encode(bytes, Base64Support.UNCHUNKED);
            final Integer size = Base64Support.decode(encoded).length;
            args.add(new Object[] {encoded, size});
        }

        return args.iterator();
    }
    
    @Test(dataProvider = "getBase64SizeData")
    public void testGetBase64Size(@Nonnull final String encoded, @Nonnull final Integer expectedSize) {
        Assert.assertEquals(SAMLBindingSupport.getBase64Size(encoded), expectedSize); 
    }
    
    @DataProvider
    public Iterator<Object[]> getDeflatedSizeData() throws Exception {
        ArrayList<Object[]> args = new ArrayList<>();
        
        // First pair element is path of data file, second is inflationFactor for estimated mode
        final List<Pair<String,Float>> testData = List.of(
                // This one is highly compressible because it's just repeated lines
                new Pair<String,Float>("100bytes.txt", 7.0f), 
                // These are less compressible because they are complex strings
                new Pair<String,Float>("LoremIpsum.txt", 2.25f),
                new Pair<String,Float>("AuthnRequest.xml", 1.75f)
                );
        
        for (final Pair<String,Float> item : testData) {
            byte[] bytes = readFile(item.getFirst());
            assert bytes != null;
            try (final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                    final DeflaterOutputStream deflaterStream =
                            new NoWrapAutoEndDeflaterOutputStream(bytesOut, Deflater.DEFLATED)) {

                deflaterStream.write(bytes);
                deflaterStream.finish();
                
                String encoded = Base64Support.encode(bytesOut.toByteArray(), Base64Support.UNCHUNKED);

                args.add(new Object[] {encoded, false, null, bytes.length});
                args.add(new Object[] {encoded, true, item.getSecond(), bytes.length});
            }
        }
                
        return args.iterator(); 
    }
    
    @Test(dataProvider = "getDeflatedSizeData")
    public void testGetDeflatedSize(@Nonnull final String encoded, final boolean estimated, @Nullable final Float inflationFactor,
            @Nonnull final Integer expectedSize) throws MessageDecodingException {

        final Integer calculatedSize = SAMLBindingSupport.getDeflatedSize(encoded, estimated, inflationFactor);

        if (!estimated) {
            Assert.assertEquals(calculatedSize, expectedSize); 
        } else {
            // Estimation should be within 10% in either direction of actual size
            Integer lower = (int) Math.floor(expectedSize * 0.90);
            Integer upper = (int) Math.ceil(expectedSize * 1.10);
            
            log.trace("Lower bound is {}, upper bound is {}, calculated size is {}", lower, upper, calculatedSize);
            
            if (calculatedSize < lower || calculatedSize > upper) {
                Assert.fail(String.format("Calculated size %d was not in the expected range %d to %d (rounded from +/- 10%% of %d)",
                        calculatedSize, lower, upper, expectedSize));
            }
        }
    }
    
    public byte[] readFile(final String path) throws IOException {
        try (final InputStream is = this.getClass().getResourceAsStream(path)) {
            return is.readAllBytes();
        }
    }

}
