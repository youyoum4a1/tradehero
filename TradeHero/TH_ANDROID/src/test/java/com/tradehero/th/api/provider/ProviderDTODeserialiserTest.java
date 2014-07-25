package com.tradehero.th.api.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.competition.ProviderDTO;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class ProviderDTODeserialiserTest
    extends ProviderCompactDTODeserialiserTestBase<ProviderDTO>
{
    @Inject protected ObjectMapper normalMapper;

    @Before public void setUp() throws IOException
    {
        super.setUp();
    }

    @Override protected ProviderDTO readValue(InputStream stream) throws IOException
    {
        return normalMapper.readValue(stream, ProviderDTO.class);
    }

    @Test public void testNormalDeserialiseBody1() throws IOException
    {
        ProviderDTO converted = baseTestNormalDeserialiseBody1();
        assertEquals(ProviderDTO.class, converted.getClass());
    }

    @Test public void testSetSpecifics() throws IOException
    {
        ProviderDTO converted = baseTestSetSpecifics();
        assertEquals(ProviderDTO.class, converted.getClass());
    }
}
