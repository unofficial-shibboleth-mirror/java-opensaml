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

package org.opensaml.security.x509;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.opensaml.security.SecurityException;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.crypto.KeySupportTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the X509Support utility methods.
 */
public class X509SupportTest {

    private PrivateKey entityPrivateKey;

    private String entityPrivKeyBase64 = "MIICXQIBAAKBgQCewxYtt+tEFlhy7+5V3kdCGDATiiXP/A0r2Q+amujuDz0aauvf"
            + "j8fcEsIm1JJf7KDeIeYncybi71Fd/2iPFVUKJIUuXmPxfwXnn7eqzspQRNnS1zf+"
            + "y8oe4V7Y6/DBD0vt+tMCjMCiaAfsyQshaje78hZbh0m3fWrdJMClpcTXAQIDAQAB"
            + "AoGAENG8JMXKT+FKJ4sRpdkxlWf4l+lXziv2vUF2rLtil+3XXFgdewbBdqgqF3EH"
            + "vM/VzxKqTl2drgcKiLnJOvdYldq721OglkBfCEwI1yKjf/xWo/B2IuHsbkKrodBW"
            + "LpzWhoYb8WLSePFPaYvIyRUxAiDSTUcySn1piHKh2L6dFj0CQQDTC5/3GZaFL1lK"
            + "Tp2/WOzp1a+W5pinNVYugQz6ZUWurIdkePiJswCeERa7IHnAfa8iHYWZhn2Y9jub"
            + "l1W+aLgXAkEAwJRu+JFpW4AtiwRGRHpTLMGJh40eUI668RwXiBePLUFdWJMPsuZC"
            + "//fWRLIZQ/Ukv5XLcFdQeAPh8crVQGlApwJAXSy2tRtg7vAWlc3bq00RW7Nx0EeC"
            + "gd/0apejKTFo8FNPezZFVFXpIeAdjwQpfKiAl6k9AKj17oBXlLvdqTEGhQJBALgs"
            + "PIST7EKJrwSILftHUUw4OyLbnuZD2hzEVOzeOxt4q6EN47Gf7OuHRe+ks+z+AQsI"
            + "YuspVdexPuBSrudOwXkCQQDEdI7texImU7o6tXAt9mmVyVik9ibRaTnpbJJh+ox+"
            + "EwYAMfQ7HqW8el3XH+q5tNNDNuR+voIuWJPRD30nOgb5";

    private X509Certificate entityCert;

    private String entityCertBase64 = "MIICvzCCAiigAwIBAgIJALQ1JXkgPO25MA0GCSqGSIb3DQEBBQUAMEoxCzAJBgNV"
            + "BAYTAkNIMQ8wDQYDVQQIEwZadXJpY2gxFDASBgNVBAoTC2V4YW1wbGUub3JnMRQw"
            + "EgYDVQQDEwtleGFtcGxlLm9yZzAeFw0wODEyMDQwNzUzNDBaFw0wOTEyMDQwNzUz"
            + "NDBaMEoxCzAJBgNVBAYTAkNIMQ8wDQYDVQQIEwZadXJpY2gxFDASBgNVBAoTC2V4"
            + "YW1wbGUub3JnMRQwEgYDVQQDEwtleGFtcGxlLm9yZzCBnzANBgkqhkiG9w0BAQEF"
            + "AAOBjQAwgYkCgYEAnsMWLbfrRBZYcu/uVd5HQhgwE4olz/wNK9kPmpro7g89Gmrr"
            + "34/H3BLCJtSSX+yg3iHmJ3Mm4u9RXf9ojxVVCiSFLl5j8X8F55+3qs7KUETZ0tc3"
            + "/svKHuFe2OvwwQ9L7frTAozAomgH7MkLIWo3u/IWW4dJt31q3STApaXE1wECAwEA"
            + "AaOBrDCBqTAdBgNVHQ4EFgQU0lf1wYwRJhvGZYL2WpMOykDNdeUwegYDVR0jBHMw"
            + "cYAU0lf1wYwRJhvGZYL2WpMOykDNdeWhTqRMMEoxCzAJBgNVBAYTAkNIMQ8wDQYD"
            + "VQQIEwZadXJpY2gxFDASBgNVBAoTC2V4YW1wbGUub3JnMRQwEgYDVQQDEwtleGFt"
            + "cGxlLm9yZ4IJALQ1JXkgPO25MAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQAD"
            + "gYEAlhsuXNm5WMq7mILnbS+Xr+oi/LVezr4Yju+Qdh9AhYwbDaXnsZITHiAmfYhO"
            + "5nTjstWMAHc6JZs7h8wDvqY92RvLY+Vx78MoJXIwqqLFH4oHm2UKpvsNivrNfD/q"
            + "WPiKEYrXVVkDXUVA2yKupX1VtCru8kaJ42kAlCN9Bg4wezU=";

    private X509Certificate entityCert3AltNamesDNS_URL_IP;

    private String entityCert3AltNamesDNS_URL_IPBase64 =
            "MIIDzjCCAragAwIBAgIBMTANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl"
                    + "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyMTE4MjM0MFoX"
                    + "DTE3MDUxODE4MjM0MFowMTESMBAGA1UEChMJSW50ZXJuZXQyMRswGQYDVQQDExJm"
                    + "b29iYXIuZXhhbXBsZS5vcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB"
                    + "AQDNWnkFmhy1vYa6gN/xBRKkZxFy3sUq2V0LsYb6Q3pe9Qlb6+BzaM5DrN8uIqqr"
                    + "oBE3Wp0LtrgKuQTpDpNFBdS2p5afiUtOYLWBDtizTOzs3Z36MGMjIPUYQ4s03IP3"
                    + "yPh2ud6EKpDPiYqzNbkRaiIwmYSit5r+RMYvd6fuKvTOn6h7PZI5AD7Rda7VWh5O"
                    + "VSoZXlRx3qxFho+mZhW0q4fUfTi5lWwf4EhkfBlzgw/k5gf4cOi6rrGpRS1zxmbt"
                    + "X1RAg+I20z6d04g0N2WsK5stszgYKoIROJCiXwjraa8/SoFcILolWQpttVHBIUYl"
                    + "yDlm8mIFleZf4ReFpfm+nUYxAgMBAAGjgfQwgfEwCQYDVR0TBAIwADAsBglghkgB"
                    + "hvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE"
                    + "FDgRgTkjaKoK6DoZfUZ4g9LDJUWuMFUGA1UdIwROMEyAFNXuZVPeUdqHrULqQW7y"
                    + "r9buRpQLoTGkLzAtMRIwEAYDVQQKEwlJbnRlcm5ldDIxFzAVBgNVBAMTDmNhLmV4"
                    + "YW1wbGUub3JnggEBMEAGA1UdEQQ5MDeCEmFzaW1vdi5leGFtcGxlLm9yZ4YbaHR0"
                    + "cDovL2hlaW5sZWluLmV4YW1wbGUub3JnhwQKAQIDMA0GCSqGSIb3DQEBBQUAA4IB"
                    + "AQBLiDMyQ60ldIytVO1GCpp1S1sKJyTF56GVxHh/82hiRFbyPu+2eSl7UcJfH4ZN"
                    + "bAfHL1vDKTRJ9zoD8WRzpOCUtT0IPIA/Ex+8lFzZmujO10j3TMpp8Ii6+auYwi/T"
                    + "osrfw1YCxF+GI5KO49CfDRr6yxUbMhbTN+ssK4UzFf36UbkeJ3EfDwB0WU70jnlk"
                    + "yO8f97X6mLd5QvRcwlkDMftP4+MB+inTlxDZ/w8NLXQoDW6p/8r91bupXe0xwuyE"
                    + "vow2xjxlzVcux2BZsUZYjBa07ZmNNBtF7WaQqH7l2OBCAdnBhvme5i/e0LK3Ivys" + "+hcVyvCXs5XtFTFWDAVYvzQ6";

    private X509Certificate entityCert3AltNamesDNS_URN_IP;

    private String entityCert3AltNamesDNS_URN_IPBase64 =
            "MIIDyjCCArKgAwIBAgIBLDANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl"
                    + "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyMTA0NDQzOVoX"
                    + "DTE3MDUxODA0NDQzOVowMTESMBAGA1UEChMJSW50ZXJuZXQyMRswGQYDVQQDExJm"
                    + "b29iYXIuZXhhbXBsZS5vcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB"
                    + "AQDNWnkFmhy1vYa6gN/xBRKkZxFy3sUq2V0LsYb6Q3pe9Qlb6+BzaM5DrN8uIqqr"
                    + "oBE3Wp0LtrgKuQTpDpNFBdS2p5afiUtOYLWBDtizTOzs3Z36MGMjIPUYQ4s03IP3"
                    + "yPh2ud6EKpDPiYqzNbkRaiIwmYSit5r+RMYvd6fuKvTOn6h7PZI5AD7Rda7VWh5O"
                    + "VSoZXlRx3qxFho+mZhW0q4fUfTi5lWwf4EhkfBlzgw/k5gf4cOi6rrGpRS1zxmbt"
                    + "X1RAg+I20z6d04g0N2WsK5stszgYKoIROJCiXwjraa8/SoFcILolWQpttVHBIUYl"
                    + "yDlm8mIFleZf4ReFpfm+nUYxAgMBAAGjgfAwge0wCQYDVR0TBAIwADAsBglghkgB"
                    + "hvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE"
                    + "FDgRgTkjaKoK6DoZfUZ4g9LDJUWuMFUGA1UdIwROMEyAFNXuZVPeUdqHrULqQW7y"
                    + "r9buRpQLoTGkLzAtMRIwEAYDVQQKEwlJbnRlcm5ldDIxFzAVBgNVBAMTDmNhLmV4"
                    + "YW1wbGUub3JnggEBMDwGA1UdEQQ1MDOCEmFzaW1vdi5leGFtcGxlLm9yZ4YXdXJu"
                    + "OmZvbzpleGFtcGxlLm9yZzppZHCHBAoBAgMwDQYJKoZIhvcNAQEFBQADggEBAH7L"
                    + "RnOWJbP5p50lLvBaW6G0593OMChQIXVim9kf6Um4HQjC8/3BZPltyNMxn+xtUnRY"
                    + "AaKPDjbpr0CkM5lggJd8Q69XJiPTch9UQlcX+Ry7CXV+GsTnn6kgE5IW0ULqrp/i"
                    + "vVQVu6Af/dBS1+K+TddYOatNnABLr0lco5ppZ4v9HFIsoLljTrkdW4XrlYmW1Hx0"
                    + "SUVrYsbv2uRP3n1jEEYldvZOdhEGoEADSt46zE+HCG/ytfTYSDyola6OErB09e/o"
                    + "FDzzWGsOve69UV11bdeFgaMQJYloFHXq9MRKOCaKQLWxjwMd1MRJLJX6WpwZS600" + "t2pJYMLFu19LDRfgX4M=";

    private X509Certificate entityCert1AltNameDNS;

    private String entityCert1AltNameDNSBase64 = "MIIDqzCCApOgAwIBAgIBLTANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl"
            + "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyMTE3MzM0M1oX"
            + "DTE3MDUxODE3MzM0M1owMTESMBAGA1UEChMJSW50ZXJuZXQyMRswGQYDVQQDExJm"
            + "b29iYXIuZXhhbXBsZS5vcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB"
            + "AQDNWnkFmhy1vYa6gN/xBRKkZxFy3sUq2V0LsYb6Q3pe9Qlb6+BzaM5DrN8uIqqr"
            + "oBE3Wp0LtrgKuQTpDpNFBdS2p5afiUtOYLWBDtizTOzs3Z36MGMjIPUYQ4s03IP3"
            + "yPh2ud6EKpDPiYqzNbkRaiIwmYSit5r+RMYvd6fuKvTOn6h7PZI5AD7Rda7VWh5O"
            + "VSoZXlRx3qxFho+mZhW0q4fUfTi5lWwf4EhkfBlzgw/k5gf4cOi6rrGpRS1zxmbt"
            + "X1RAg+I20z6d04g0N2WsK5stszgYKoIROJCiXwjraa8/SoFcILolWQpttVHBIUYl"
            + "yDlm8mIFleZf4ReFpfm+nUYxAgMBAAGjgdEwgc4wCQYDVR0TBAIwADAsBglghkgB"
            + "hvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE"
            + "FDgRgTkjaKoK6DoZfUZ4g9LDJUWuMFUGA1UdIwROMEyAFNXuZVPeUdqHrULqQW7y"
            + "r9buRpQLoTGkLzAtMRIwEAYDVQQKEwlJbnRlcm5ldDIxFzAVBgNVBAMTDmNhLmV4"
            + "YW1wbGUub3JnggEBMB0GA1UdEQQWMBSCEmFzaW1vdi5leGFtcGxlLm9yZzANBgkq"
            + "hkiG9w0BAQUFAAOCAQEAjSRiOpLAbrxkqQ0Yh+mUWCVA2ChSDBnFFDe4a3Z/87Tw"
            + "7QEzU6U1xejCH6kGGZSmHLBMPLg31+QNiWwXDnQTqa8w/16oncUuw3olIK+C/r+F"
            + "5uhakJcPq6LK8ZhSDi85YGMn1vPHP8FsC9/HMZS0Y/ouzDeZYwXc9ZwF8uMxh+vn"
            + "KWUbyVDGuoTI4x0SIMgyrA917xpSG/1m9lJVVvF9S6/+n+ZpkIhpmvmOHGNicBoX"
            + "sNk3tgHPzGTkn/DDx9SGmBUfyBEOTwlDHX36zqGRozWRVqGVYMb58L7dxLjnWkO5"
            + "0eVKajcKvJ1zBowSoiDQ50drULm5FSVzix3gUO1p6g==";

    private X509Certificate entityCert1AltNameURN;

    private String entityCert1AltNameURNBase64 = "MIIDsDCCApigAwIBAgIBLjANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl"
            + "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyMTE3NDYyNVoX"
            + "DTE3MDUxODE3NDYyNVowMTESMBAGA1UEChMJSW50ZXJuZXQyMRswGQYDVQQDExJm"
            + "b29iYXIuZXhhbXBsZS5vcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB"
            + "AQDNWnkFmhy1vYa6gN/xBRKkZxFy3sUq2V0LsYb6Q3pe9Qlb6+BzaM5DrN8uIqqr"
            + "oBE3Wp0LtrgKuQTpDpNFBdS2p5afiUtOYLWBDtizTOzs3Z36MGMjIPUYQ4s03IP3"
            + "yPh2ud6EKpDPiYqzNbkRaiIwmYSit5r+RMYvd6fuKvTOn6h7PZI5AD7Rda7VWh5O"
            + "VSoZXlRx3qxFho+mZhW0q4fUfTi5lWwf4EhkfBlzgw/k5gf4cOi6rrGpRS1zxmbt"
            + "X1RAg+I20z6d04g0N2WsK5stszgYKoIROJCiXwjraa8/SoFcILolWQpttVHBIUYl"
            + "yDlm8mIFleZf4ReFpfm+nUYxAgMBAAGjgdYwgdMwCQYDVR0TBAIwADAsBglghkgB"
            + "hvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE"
            + "FDgRgTkjaKoK6DoZfUZ4g9LDJUWuMFUGA1UdIwROMEyAFNXuZVPeUdqHrULqQW7y"
            + "r9buRpQLoTGkLzAtMRIwEAYDVQQKEwlJbnRlcm5ldDIxFzAVBgNVBAMTDmNhLmV4"
            + "YW1wbGUub3JnggEBMCIGA1UdEQQbMBmGF3Vybjpmb286ZXhhbXBsZS5vcmc6aWRw"
            + "MA0GCSqGSIb3DQEBBQUAA4IBAQA6REOOby69uy/zvgidjEuZRK/oacIKvjVm+1K0"
            + "HSKbGdroCHRRMQS6s5IGRE2ef+wiwus1367/crxYEqa+Tu9iewyVNFkZjWm9ra+T"
            + "kgoghA5DteoC0tYzUhWooWhA6FW7Ktn8yAdmGPV+bhMTwnrm9DiM9mAZr0Ew8qP7"
            + "8HWziw2qWM48LhdfuO2kiWzvinRx1wqKJjur9nY9piUOO32aTlzXZy2yLiOYVKUw"
            + "2dKdxMmvwYxNYCEzNx2ERmDSbHoNZLn75WidNTnHpkn0rBh2J9ZS8j2swyoVoVp3"
            + "rQRHDSQ9CJCNKVXWh/WnjgqnLpBzXKCLv/zrQ3t47OL2Jyso";

    private X509Certificate entityCert1AltNameURL;

    private String entityCert1AltNameURLBase64 = "MIIDtDCCApygAwIBAgIBMDANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl"
            + "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyMTE4MTMwOFoX"
            + "DTE3MDUxODE4MTMwOFowMTESMBAGA1UEChMJSW50ZXJuZXQyMRswGQYDVQQDExJm"
            + "b29iYXIuZXhhbXBsZS5vcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB"
            + "AQDNWnkFmhy1vYa6gN/xBRKkZxFy3sUq2V0LsYb6Q3pe9Qlb6+BzaM5DrN8uIqqr"
            + "oBE3Wp0LtrgKuQTpDpNFBdS2p5afiUtOYLWBDtizTOzs3Z36MGMjIPUYQ4s03IP3"
            + "yPh2ud6EKpDPiYqzNbkRaiIwmYSit5r+RMYvd6fuKvTOn6h7PZI5AD7Rda7VWh5O"
            + "VSoZXlRx3qxFho+mZhW0q4fUfTi5lWwf4EhkfBlzgw/k5gf4cOi6rrGpRS1zxmbt"
            + "X1RAg+I20z6d04g0N2WsK5stszgYKoIROJCiXwjraa8/SoFcILolWQpttVHBIUYl"
            + "yDlm8mIFleZf4ReFpfm+nUYxAgMBAAGjgdowgdcwCQYDVR0TBAIwADAsBglghkgB"
            + "hvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE"
            + "FDgRgTkjaKoK6DoZfUZ4g9LDJUWuMFUGA1UdIwROMEyAFNXuZVPeUdqHrULqQW7y"
            + "r9buRpQLoTGkLzAtMRIwEAYDVQQKEwlJbnRlcm5ldDIxFzAVBgNVBAMTDmNhLmV4"
            + "YW1wbGUub3JnggEBMCYGA1UdEQQfMB2GG2h0dHA6Ly9oZWlubGVpbi5leGFtcGxl"
            + "Lm9yZzANBgkqhkiG9w0BAQUFAAOCAQEAQRJHMwtHvzdaTKg/GdSdt1u6H+tkspYE"
            + "SeDOFS0Ni9bm2nPrKLPHzWwVFriMwqtWT0ik7Sx8TK1jA2q3Wxgj+xS9kAvFtGyy"
            + "pq1HEMdVXwcQlyopSZEd3Oi7Bfam6eSy1ehVKkEwG9pry+0v6I1Z3gShPHBm/Tcj"
            + "EV3FIv6CTYgW9jZIBPKfI54xyQ7Ef07V608S6lpPGEOmjZPccQmiqu2fXTvmSxmD"
            + "eXUY9lfn7SR3afmHOeDuovoa+sPZnyBmtsWcllmI328ZkSukaOXhLDLFLt2UA55L"
            + "uy4/1cWTxEqyuizzTvjbHvvw7HF4/yBkNggcumQqr9gWqxNvvXFsNw==";

    private X509Certificate entityCert1AltNameIP;

    private String entityCert1AltNameIPBase64 = "MIIDnTCCAoWgAwIBAgIBLzANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl"
            + "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyMTE3NDgwMloX"
            + "DTE3MDUxODE3NDgwMlowMTESMBAGA1UEChMJSW50ZXJuZXQyMRswGQYDVQQDExJm"
            + "b29iYXIuZXhhbXBsZS5vcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB"
            + "AQDNWnkFmhy1vYa6gN/xBRKkZxFy3sUq2V0LsYb6Q3pe9Qlb6+BzaM5DrN8uIqqr"
            + "oBE3Wp0LtrgKuQTpDpNFBdS2p5afiUtOYLWBDtizTOzs3Z36MGMjIPUYQ4s03IP3"
            + "yPh2ud6EKpDPiYqzNbkRaiIwmYSit5r+RMYvd6fuKvTOn6h7PZI5AD7Rda7VWh5O"
            + "VSoZXlRx3qxFho+mZhW0q4fUfTi5lWwf4EhkfBlzgw/k5gf4cOi6rrGpRS1zxmbt"
            + "X1RAg+I20z6d04g0N2WsK5stszgYKoIROJCiXwjraa8/SoFcILolWQpttVHBIUYl"
            + "yDlm8mIFleZf4ReFpfm+nUYxAgMBAAGjgcMwgcAwCQYDVR0TBAIwADAsBglghkgB"
            + "hvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE"
            + "FDgRgTkjaKoK6DoZfUZ4g9LDJUWuMFUGA1UdIwROMEyAFNXuZVPeUdqHrULqQW7y"
            + "r9buRpQLoTGkLzAtMRIwEAYDVQQKEwlJbnRlcm5ldDIxFzAVBgNVBAMTDmNhLmV4"
            + "YW1wbGUub3JnggEBMA8GA1UdEQQIMAaHBAoBAgMwDQYJKoZIhvcNAQEFBQADggEB"
            + "AIgpJnJ9Pid+ldf/jvO/BRQkHdRkuzMP3AwLvzSIJPcJAw4Dvzqm57VQaJDnfqqX"
            + "SN9POAPlpsBzBE8Xdtpp5TemJt7X2wjuCHTlvGY/HaPPvb3QielWsU4As6Xdk1xY"
            + "ovTPtGnbh+gsPT5jdrA+d5PKEsXicZEVqGOIRVINuDUhZsl0Y26SJmskWNKAb7l4"
            + "7jPQj8U2kkWUEWXkOv5FsyiB2KdxYGbJSpGwGLRWZNDbuVUjnuzQ29EWWbNwHxTb"
            + "GMRjrI9Q4WynZ2IOcnG1hMjCU6L4uk4JfryIw4IBHGa8uUtskHqJ7TFJ/4taWyV/" + "UB0djqOPjMACQpMBhEVRSBU=";


    private String altNameDNS, altNameURN, altNameURL, altNameIP;

    private Integer altNameTypeDNS, altNameTypeURI, altNameTypeIP;

    private static String subjectAltNameExtensionOID = "2.5.29.17";

    /** A PEM encoded cert. */
    private String certPEM = "/data/certificate.pem";

    /** A PEM encoded cert. */
    private String certDER = "/data/certificate.der";

    /** A PEM encoded CRL. */
    private String crlPEM = "/data/crl.pem";

    /** A PEM encoded CRL. */
    private String crlDER = "/data/crl.der";
    
    /** An EC certificate. */
    private String certEC = "/data/ec-certificate.pem";
    
    /** An EC private key. */
    private String keyEC = "/data/ec-privkey-nopass.pem";
    
    /** Invalid base64 string as it has invalid trailing digits. */
    private final static String INVALID_BASE64_TRAILING = "AB==";

    @BeforeMethod
    protected void setUp() throws Exception {
        entityPrivateKey = KeySupport.buildJavaRSAPrivateKey(entityPrivKeyBase64);
        entityCert = X509Support.decodeCertificate(entityCertBase64);

        entityCert3AltNamesDNS_URL_IP = X509Support.decodeCertificate(entityCert3AltNamesDNS_URL_IPBase64);
        entityCert3AltNamesDNS_URN_IP = X509Support.decodeCertificate(entityCert3AltNamesDNS_URN_IPBase64);
        entityCert1AltNameDNS = X509Support.decodeCertificate(entityCert1AltNameDNSBase64);
        entityCert1AltNameURN = X509Support.decodeCertificate(entityCert1AltNameURNBase64);
        entityCert1AltNameURL = X509Support.decodeCertificate(entityCert1AltNameURLBase64);
        entityCert1AltNameIP = X509Support.decodeCertificate(entityCert1AltNameIPBase64);

        altNameDNS = "asimov.example.org";
        altNameURN = "urn:foo:example.org:idp";
        altNameURL = "http://heinlein.example.org";
        altNameIP = "10.1.2.3";

        altNameTypeIP = X509Support.IP_ADDRESS_ALT_NAME;
        altNameTypeURI = X509Support.URI_ALT_NAME;
        altNameTypeDNS = X509Support.DNS_ALT_NAME;
    }
    
    /**
     *  Test common name (CN) extraction from X500Principal.
     */
    @Test
    public void testGetCommonNames() {
        List<String> commonNames;
        
        // 1 component
        commonNames = X509Support.getCommonNames(new X500Principal("cn=foo.example.org"));
        assert commonNames != null;
        Assert.assertEquals(commonNames.size(), 1);
        Assert.assertEquals(commonNames.get(0), "foo.example.org");
        
        // 2 components, 1 cn
        commonNames = X509Support.getCommonNames(new X500Principal("cn=foo.example.org, o=MyOrg"));
        assert commonNames != null;
        Assert.assertEquals(commonNames.size(), 1);
        Assert.assertEquals(commonNames.get(0), "foo.example.org");
        
        // 2 components each with cn
        commonNames = X509Support.getCommonNames(new X500Principal("cn=foo.example.org, cn=MyOrg"));
        assert commonNames != null;
        Assert.assertEquals(commonNames.size(), 2);
        Assert.assertEquals(commonNames.get(0), "foo.example.org");
        Assert.assertEquals(commonNames.get(1), "MyOrg");
        
        // 4 components, 3 cn
        commonNames = X509Support.getCommonNames(new X500Principal("cn=foo.example.org, cn=WebServers, cn=Hosts, o=MyOrg"));
        assert commonNames != null;
        Assert.assertEquals(commonNames.size(), 3);
        Assert.assertEquals(commonNames.get(0), "foo.example.org");
        Assert.assertEquals(commonNames.get(1), "WebServers");
        Assert.assertEquals(commonNames.get(2), "Hosts");
        
        // 4 components, 2 cn, a cn is not first nor last
        commonNames = X509Support.getCommonNames(new X500Principal("uid=foo, cn=Admins, cn=People, o=MyOrg"));
        assert commonNames != null;
        Assert.assertEquals(commonNames.size(), 2);
        Assert.assertEquals(commonNames.get(0), "Admins");
        Assert.assertEquals(commonNames.get(1), "People");
        
        // 2 components, one of them with multiple cn AVAs
        // Note: The set of AVAs in a DN component is unordered, so can't test returned ordering.
        commonNames = X509Support.getCommonNames(new X500Principal("cn=foo.example.org+cn=bar.example.org+cn=baz.example.org, o=MyOrg"));
        assert commonNames != null;
        Assert.assertEquals(commonNames.size(), 3);
        Assert.assertTrue(commonNames.contains("foo.example.org"));
        Assert.assertTrue(commonNames.contains("bar.example.org"));
        Assert.assertTrue(commonNames.contains("baz.example.org"));
        
        // 2 components, both with multiple cn AVAs
        // Note: The set of AVAs in a DN component is unordered, so can't test returned ordering.
        commonNames = X509Support.getCommonNames(new X500Principal("cn=foo.example.org+cn=bar.example.org+cn=baz.example.org, cn=Org1+cn=Org2"));
        assert commonNames != null;
        Assert.assertEquals(commonNames.size(), 5);
        Assert.assertTrue(commonNames.contains("foo.example.org"));
        Assert.assertTrue(commonNames.contains("bar.example.org"));
        Assert.assertTrue(commonNames.contains("baz.example.org"));
        Assert.assertTrue(commonNames.contains("Org1"));
        Assert.assertTrue(commonNames.contains("Org2"));
        
        // No cn at all
        commonNames = X509Support.getCommonNames(new X500Principal("uid=foo, o=MyOrg"));
        assert commonNames != null;
        Assert.assertEquals(commonNames.size(), 0);
        
        // Test input of raw OID
        commonNames = X509Support.getCommonNames(new X500Principal("2.5.4.3=foo.example.org"));
        assert commonNames != null;
        Assert.assertEquals(commonNames.size(), 1);
        Assert.assertEquals(commonNames.get(0), "foo.example.org");
        
        // Test attack DNs per CVE-2014-3577
        commonNames = X509Support.getCommonNames(new X500Principal("cn=foo.example.org, o=foo \\,cn=www.apache.org"));
        assert commonNames != null;
        Assert.assertEquals(commonNames.size(), 1);
        Assert.assertFalse(commonNames.contains("www.apache.org"));
        Assert.assertEquals(commonNames.get(0), "foo.example.org");
        
        commonNames = X509Support.getCommonNames(new X500Principal("cn=foo.example.org, o=cn=www.apache.org\\, foo"));
        assert commonNames != null;
        Assert.assertEquals(commonNames.size(), 1);
        Assert.assertFalse(commonNames.contains("www.apache.org"));
        Assert.assertEquals(commonNames.get(0), "foo.example.org");
    }
    
    /**
     * Test Subject Key Identifier (SKI) extraction from certificate.
     * 
     * @throws DecoderException ...
     */
    @Test
    public void testGetSubjectKeyIdentifier() throws DecoderException {
        // This is the cert SKI according to OpenSSL 'openssl x509 -in entity.crt -noout -text'
        String hexSKI = "D2:57:F5:C1:8C:11:26:1B:C6:65:82:F6:5A:93:0E:CA:40:CD:75:E5";
        byte[] controlSKI = Hex.decodeHex(hexSKI.replaceAll(":", "").toCharArray());
        byte[] certSKI = X509Support.getSubjectKeyIdentifier(entityCert);
        Assert.assertEquals(certSKI, controlSKI);
    }

    /**
     * Tests that the entity cert is correctly identified in the collection.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDetermineEntityCertificate() throws Exception {
        ArrayList<X509Certificate> certs = new ArrayList<>();
        certs.add(entityCert3AltNamesDNS_URL_IP);
        certs.add(entityCert1AltNameDNS);
        certs.add(entityCert);

        final X509Certificate cert = X509Support.determineEntityCertificate(certs, entityPrivateKey);
        assert cert != null;
        Assert.assertTrue(cert.equals(entityCert));
    }

    /**
     * Test 1 alt name: DNS.
     * 
     * @throws SecurityException ...
     * @throws CertificateParsingException ...
     */
    @Test
    public void testGetSubjectAltNames1NameDNS() throws SecurityException, CertificateParsingException {
        X509Certificate cert = entityCert1AltNameDNS;
        // Sanity checks
        byte[] extensionValue = cert.getExtensionValue(subjectAltNameExtensionOID);
        Assert.assertNotNull(extensionValue, "Entity cert's Java native getExtensionValue() was null");
        Assert.assertTrue(extensionValue.length > 0, "Entity cert's extension value was empty");

        Set<Integer> nameTypes = new HashSet<>();
        nameTypes.add(altNameTypeDNS);

        List<?> altNames = getAltNames(cert, nameTypes);
        Assert.assertNotNull(altNames, "X509Support.getAltNames() returned null");

        Assert.assertTrue(altNames.contains(altNameDNS), "Failed to find expected KeyName value");
    }

    /**
     * Test 1 alt name: URI (URN).
     * 
     * @throws SecurityException ...
     * @throws CertificateParsingException ...
     */
    @Test
    public void testGetSubjectAltNames1NameURN() throws SecurityException, CertificateParsingException {
        X509Certificate cert = entityCert1AltNameURN;
        // Sanity checks
        byte[] extensionValue = cert.getExtensionValue(subjectAltNameExtensionOID);
        Assert.assertNotNull(extensionValue, "Entity cert's Java native getExtensionValue() was null");
        Assert.assertTrue(extensionValue.length > 0, "Entity cert's extension value was empty");

        Set<Integer> nameTypes = new HashSet<>();
        nameTypes.add(altNameTypeURI);

        List<?> altNames = getAltNames(cert, nameTypes);
        Assert.assertNotNull(altNames, "X509Support.getAltNames() returned null");

        Assert.assertTrue(altNames.contains(altNameURN), "Failed to find expected KeyName value");
    }

    /**
     * Test 1 alt name: URI (URL).
     * 
     * @throws SecurityException ...
     * @throws CertificateParsingException ...
     */
    @Test
    public void testGetSubjectAltNames1NameURL() throws SecurityException, CertificateParsingException {
        X509Certificate cert = entityCert1AltNameURL;
        // Sanity checks
        byte[] extensionValue = cert.getExtensionValue(subjectAltNameExtensionOID);
        Assert.assertNotNull(extensionValue, "Entity cert's Java native getExtensionValue() was null");
        Assert.assertTrue(extensionValue.length > 0, "Entity cert's extension value was empty");

        Set<Integer> nameTypes = new HashSet<>();
        nameTypes.add(altNameTypeURI);

        List<?> altNames = getAltNames(cert, nameTypes);
        Assert.assertNotNull(altNames, "X509Support.getAltNames() returned null");

        Assert.assertTrue(altNames.contains(altNameURL), "Failed to find expected KeyName value");
    }

    /**
     * Test 1 alt name: IP.
     * 
     * @throws SecurityException ...
     * @throws CertificateParsingException ...
     */
    @Test
    public void testGetSubjectAltNames1NameIP() throws SecurityException, CertificateParsingException {
        X509Certificate cert = entityCert1AltNameIP;
        // Sanity checks
        byte[] extensionValue = cert.getExtensionValue(subjectAltNameExtensionOID);
        Assert.assertNotNull(extensionValue, "Entity cert's Java native getExtensionValue() was null");
        Assert.assertTrue(extensionValue.length > 0, "Entity cert's extension value was empty");

        Set<Integer> nameTypes = new HashSet<>();
        nameTypes.add(altNameTypeIP);

        List<?> altNames = getAltNames(cert, nameTypes);
        Assert.assertNotNull(altNames, "X509Support.getAltNames() returned null");

        Assert.assertTrue(altNames.contains(altNameIP), "Failed to find expected KeyName value");
    }

    /**
     * Test 3 alt names: DNS, URI (URL), IP.
     * 
     * @throws SecurityException ...
     * @throws CertificateParsingException ...
     */
    @Test
    public void testGetSubjectAltNames3NamesDNS_URL_IP() throws SecurityException, CertificateParsingException {
        X509Certificate cert = entityCert3AltNamesDNS_URL_IP;
        // Sanity checks
        byte[] extensionValue = cert.getExtensionValue(subjectAltNameExtensionOID);
        Assert.assertNotNull(extensionValue, "Entity cert's Java native getExtensionValue() was null");
        Assert.assertTrue(extensionValue.length > 0, "Entity cert's extension value was empty");

        Set<Integer> nameTypes = new HashSet<>();
        nameTypes.add(altNameTypeDNS);
        nameTypes.add(altNameTypeURI);
        nameTypes.add(altNameTypeIP);

        List<?> altNames = getAltNames(cert, nameTypes);
        Assert.assertNotNull(altNames, "X509Support.getAltNames() returned null");

        Assert.assertTrue(altNames.contains(altNameDNS), "Failed to find expected KeyName value");
        Assert.assertTrue(altNames.contains(altNameURL), "Failed to find expected KeyName value");
        Assert.assertTrue(altNames.contains(altNameIP), "Failed to find expected KeyName value");
    }

    /**
     * Test 3 alt names: DNS, URI (URN), IP.
     * 
     * @throws SecurityException ...
     * @throws CertificateParsingException ...
     */
    @Test
    public void testGetSubjectAltNames3NamesDNS_URN_IP() throws SecurityException, CertificateParsingException {
        X509Certificate cert = entityCert3AltNamesDNS_URN_IP;
        // Sanity checks
        byte[] extensionValue = cert.getExtensionValue(subjectAltNameExtensionOID);
        Assert.assertNotNull(extensionValue, "Entity cert's Java native getExtensionValue() was null");
        Assert.assertTrue(extensionValue.length > 0, "Entity cert's extension value was empty");

        Set<Integer> nameTypes = new HashSet<>();
        nameTypes.add(altNameTypeDNS);
        nameTypes.add(altNameTypeURI);
        nameTypes.add(altNameTypeIP);

        List<?> altNames = getAltNames(cert, nameTypes);
        Assert.assertNotNull(altNames, "X509Support.getAltNames() returned null");

        Assert.assertTrue(altNames.contains(altNameDNS), "Failed to find expected KeyName value");
        Assert.assertTrue(altNames.contains(altNameURN), "Failed to find expected KeyName value");
        Assert.assertTrue(altNames.contains(altNameIP), "Failed to find expected KeyName value");
    }

    /**
     * Test decoding a PEM encoded cert.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDecodeCertPEM() throws Exception {
        InputStream certInS = X509SupportTest.class.getResourceAsStream(certPEM);

        byte[] certBytes = new byte[certInS.available()];
        certInS.read(certBytes);

        Collection<X509Certificate> certs = X509Support.decodeCertificates(certBytes);
        Assert.assertNotNull(certs);
        Assert.assertEquals(certs.size(), 2);
    }
    
    /**
     * Test decoding invalid cert encoding.
     * 
     * @throws CertificateException
     * @throws CRLException
     */
    @Test(expectedExceptions = CRLException.class)
    public void testDecodeCRLWithInvalidBase64() throws CertificateException, CRLException {
        X509Support.decodeCRL(INVALID_BASE64_TRAILING);
    }

    /**
     * Test decoding a DER encoded cert.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDecodeCertDER() throws Exception {
        InputStream certInS = X509SupportTest.class.getResourceAsStream(certDER);

        byte[] certBytes = new byte[certInS.available()];
        certInS.read(certBytes);

        Collection<X509Certificate> certs = X509Support.decodeCertificates(certBytes);
        Assert.assertNotNull(certs);
        Assert.assertEquals(certs.size(), 1);
        
        X509Certificate cert = X509Support.decodeCertificate(certBytes);
        Assert.assertNotNull(cert);
    }

    /**
     * Test decoding a PEM encoded CRL.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDecodeCRLPEM() throws Exception {
        InputStream crlInS = X509SupportTest.class.getResourceAsStream(crlPEM);

        byte[] crlBytes = new byte[crlInS.available()];
        crlInS.read(crlBytes);

        Collection<X509CRL> crls = X509Support.decodeCRLs(crlBytes);
        Assert.assertNotNull(crls);
        Assert.assertEquals(crls.size(), 1);
    }

    /**
     * Test decoding a DER encoded CRL.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDecodeCRLDER() throws Exception {
        InputStream crlInS = X509SupportTest.class.getResourceAsStream(crlDER);

        byte[] crlBytes = new byte[crlInS.available()];
        crlInS.read(crlBytes);

        Collection<X509CRL> crls = X509Support.decodeCRLs(crlBytes);
        Assert.assertNotNull(crls);
        Assert.assertEquals(crls.size(), 1);
    }
    
    /**
     * Test decoding and matching EC keypair.
     *
     * @throws Exception if something goes wrong
     */
    @Test()
    public void testEC() throws Exception {
        InputStream certInS = X509SupportTest.class.getResourceAsStream(certEC);

        byte[] certBytes = new byte[certInS.available()];
        certInS.read(certBytes);

        Collection<X509Certificate> certs = X509Support.decodeCertificates(certBytes);
        Assert.assertNotNull(certs);
        Assert.assertEquals(certs.size(), 1);
        
        PublicKey pubkey = certs.iterator().next().getPublicKey();
        Assert.assertNotNull(pubkey);

        PrivateKey key = KeySupport.decodePrivateKey(KeySupportTest.class.getResourceAsStream(keyEC), null);
        Assert.assertNotNull(key);
        
        Assert.assertTrue(KeySupport.matchKeyPair(pubkey, key));
    }


    /**
     * Get the alt names from the certificate.
     * 
     * @param cert the cert to process
     * @param nameTypes set of Integers identifying which alt name types to extract
     * @return list of alt name value Objects
     */
    private List<?> getAltNames(X509Certificate cert, Set<Integer> nameTypes) {
        Integer[] array = new Integer[nameTypes.size()];
        nameTypes.toArray(array);
        return X509Support.getAltNames(cert, array);
    }
}
