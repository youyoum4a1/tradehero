package com.tradehero.th.api.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.competition.ProviderCompactDTO;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class ProviderCompactDTODeserialiserTest
    extends ProviderCompactDTODeserialiserTestBase<ProviderCompactDTO>
{
    @Inject protected ObjectMapper normalMapper;

    @Before public void setUp() throws IOException
    {
        super.setUp();
    }

    @Override protected ProviderCompactDTO readValue(InputStream stream) throws IOException
    {
        return normalMapper.readValue(stream, ProviderCompactDTO.class);
    }

    @Test public void testNormalDeserialiseBody1() throws IOException
    {
        ProviderCompactDTO converted = baseTestNormalDeserialiseBody1();
        assertEquals(ProviderCompactDTO.class, converted.getClass());
    }

    @Test public void testSetSpecifics() throws IOException
    {
        ProviderCompactDTO converted = baseTestSetSpecifics();
        assertEquals(ProviderCompactDTO.class, converted.getClass());
    }
}
