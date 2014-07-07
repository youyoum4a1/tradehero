package com.tradehero.th.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.BaseApiTest;
import com.tradehero.th.api.security.compact.EquityCompactDTO;
import com.tradehero.th.api.security.compact.WarrantDTO;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class SecurityCompactDTODeserialiserTest extends BaseApiTest
{
    @Inject ObjectMapper normalMapper;

    private InputStream securityCompactDTOBody1Stream;
    private InputStream warrantDTOBody1Stream;

    @Before
    public void setUp() throws IOException
    {
        securityCompactDTOBody1Stream = getClass().getResourceAsStream(getPackagePath() + "/SecurityCompactDTOGoogleBody1.json");
        warrantDTOBody1Stream = getClass().getResourceAsStream(getPackagePath() + "/WarrantDTOBody1.json");
    }

    @Test
    public void testNormalDeserialiseBody1() throws IOException
    {
        SecurityCompactDTO converted = normalMapper.readValue(securityCompactDTOBody1Stream, SecurityCompactDTO.class);
        assertEquals(EquityCompactDTO.class, converted.getClass());
        assertEquals("GOOG", converted.symbol);
    }

    @Test
    public void testNormalDeserialiseWarrantBody1() throws IOException
    {
        SecurityCompactDTO converted = normalMapper.readValue(warrantDTOBody1Stream, SecurityCompactDTO.class);
        assertEquals(WarrantDTO.class, converted.getClass());
        assertEquals(4.5d, ((WarrantDTO) converted).strikePrice, 0.0001d);
    }
}
