package com.tradehero.th.api.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.BaseApiTest;
import com.tradehero.th.api.competition.ProviderDTO;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class ProviderDTODeserialiserTest extends BaseApiTest
{
    @Inject protected ObjectMapper normalMapper;
    private InputStream providerDTOBody1Stream;
    private InputStream providerDTOBody1WithFakeStream;

    @Before
    public void setUp() throws IOException
    {
        providerDTOBody1Stream = getClass().getResourceAsStream(getPackagePath() + "/ProviderDTOBody1.json");
        providerDTOBody1WithFakeStream = getClass().getResourceAsStream(getPackagePath() + "/ProviderDTOBody1WithFake.json");
    }

    @Test
    public void testNormalDeserialiseBody1() throws IOException
    {
        ProviderDTO converted = normalMapper.readValue(providerDTOBody1Stream, ProviderDTO.class);
        assertEquals(ProviderDTO.class, converted.getClass());
        assertEquals(80, converted.singleRowHeightPoint, 0.1);
    }

    @Test
    public void testSetSpecifics() throws IOException
    {
        ProviderDTO converted = normalMapper.readValue(providerDTOBody1Stream, ProviderDTO.class);
        assertEquals(ProviderDTO.class, converted.getClass());
        assertThat(converted.specificResources).isNotNull();
        assertThat(converted.specificResources.helpVideoLinkBackgroundResId).isGreaterThan(1);
        assertThat(converted.specificKnowledge).isNotNull();
        assertThat(converted.specificKnowledge.includeProviderPortfolioOnWarrants).isTrue();
    }

    @Test
    public void testDoesNotCrashOnNewFields() throws IOException
    {
        ProviderDTO converted = normalMapper.readValue(providerDTOBody1WithFakeStream, ProviderDTO.class);
        assertThat(converted.id).isEqualTo(3);
    }
}
