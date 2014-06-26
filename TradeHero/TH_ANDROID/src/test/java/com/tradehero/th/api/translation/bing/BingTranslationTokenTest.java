package com.tradehero.th.api.translation.bing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.BaseApiTest;
import com.tradehero.th.api.translation.TranslationToken;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricMavenTestRunner.class)
public class BingTranslationTokenTest extends BaseApiTest
{
    @Inject ObjectMapper normalMapper;

    private InputStream bingTranslationTokenBody1Stream;

    @Before public void setUp()
    {
        bingTranslationTokenBody1Stream = getClass().getResourceAsStream(getPackagePath() + "/BingTranslationTokenBody1.json");
    }

    @Test public void testDeserialiseBingToken() throws IOException
    {
        TranslationToken token = normalMapper.readValue(bingTranslationTokenBody1Stream, TranslationToken.class);
        assertTrue(token instanceof BingTranslationToken);
        assertEquals("http%3a%2f%2fschemas.xmlsoap.org%2fws%2f2005%2f05%2fidentity%2fclaims%2fnameidentifier=678176493872198431482632&http%3a%2f%2fschemas.microsoft.com%2faccesscontrolservice%2f2010%2f07%2fclaims%2fidentityprovider=https%3a%2f%2fdatamarket.accesscontrol.windows.net%2f&Audience=http%3a%2f%2fapi.microsofttranslator.com&ExpiresOn=1403752024&Issuer=https%3a%2f%2fdatamarket.accesscontrol.windows.net%2f&HMACSHA256=UBI8fuInv%2fsztiwOiTDmV4Lsm2ftT937APAaP%2bpq2Bg%3d",
                ((BingTranslationToken) token).accessToken);
        assertEquals("600", ((BingTranslationToken) token).getExpiresIn());
    }

    @Test public void testExpiresCorrect() throws IOException
    {
        TranslationToken token = normalMapper.readValue(bingTranslationTokenBody1Stream, TranslationToken.class);
        assertEquals(600, ((BingTranslationToken) token).getExpiresInSeconds());
    }

    @Test public void testExpiresCorrectAfter1Second() throws IOException, InterruptedException
    {
        TranslationToken token = normalMapper.readValue(bingTranslationTokenBody1Stream, TranslationToken.class);
        Thread.sleep(800);
        assertEquals(599, ((BingTranslationToken) token).getExpiresInSeconds());
    }
}
