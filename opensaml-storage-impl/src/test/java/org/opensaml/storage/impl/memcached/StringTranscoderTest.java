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

package org.opensaml.storage.impl.memcached;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit test for {@link StringTranscoder}.
 */
public class StringTranscoderTest {

    private StringTranscoder transcoder = new StringTranscoder();

    @DataProvider(name = "testStrings")
    public static Object[][] testStrings() {
        return new Object[][] {
                new Object[] {"English"},
                new Object[] {"Российская aka Russian"},
                new Object[] {"官话 aka Mandarin"},
        };
    }

    @Test(dataProvider = "testStrings")
    public void testEncodeDecode(final String s) {
        assertEquals(transcoder.decode(transcoder.encode(s)), s);
    }
}