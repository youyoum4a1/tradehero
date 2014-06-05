package com.tradehero.th.api.position;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.utils.IOUtils;
import com.tradehero.th.api.BaseApiTest;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PositionDTODeserialiserTest extends BaseApiTest
{
    private ObjectMapper normalMapper;
    private ObjectMapper moduleMapper;

    private InputStream positionDTOBody1Stream;
    private String positionDTOBody1;
    private InputStream positionInPeriodDTOBody1Stream;

    @Before
    public void setUp() throws IOException
    {
        normalMapper = new ObjectMapper();
        normalMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        moduleMapper = new ObjectMapper();
        moduleMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        moduleMapper.registerModule(new PositionDTOJacksonModule(new PositionDTODeserialiser()));

        positionDTOBody1 = new String(IOUtils.streamToBytes(getClass().getResourceAsStream(getPackagePath() + "/PositionDTOBody1.json")));
        positionDTOBody1Stream = getClass().getResourceAsStream(getPackagePath() + "/PositionDTOBody1.json");
        positionInPeriodDTOBody1Stream = getClass().getResourceAsStream(getPackagePath() + "/PositionInPeriodDTOBody1.json");
    }

    @Test
    public void testNormalDeserialiseBody1() throws IOException
    {
        PositionDTO converted = normalMapper.readValue(positionDTOBody1Stream, PositionDTO.class);
        assertEquals(PositionDTO.class, converted.getClass());
        assertEquals(239284, converted.userId);
    }

    @Test
    public void testModuleDeserialiseBody1() throws IOException
    {
        PositionDTO converted = moduleMapper.readValue(positionDTOBody1Stream, PositionDTO.class);
        assertEquals(PositionDTO.class, converted.getClass());
        assertEquals(239284, converted.userId);
    }

    @Test
    public void testNormalDeserialiseInPeriodBody1() throws IOException
    {
        PositionDTO converted = normalMapper.readValue(positionInPeriodDTOBody1Stream, PositionInPeriodDTO.class);
        assertEquals(PositionInPeriodDTO.class, converted.getClass());
        assertEquals(367963, converted.userId);
    }

    @Test
    public void testModuleDeserialiseInPeriodPreciseBody1() throws IOException
    {
        PositionDTO converted = moduleMapper.readValue(positionInPeriodDTOBody1Stream, PositionInPeriodDTO.class);
        assertEquals(PositionInPeriodDTO.class, converted.getClass());
        assertEquals(367963, converted.userId);
        assertEquals(791140.44, (double) ((PositionInPeriodDTO) converted).marketValueEndPeriodRefCcy, 0);
    }

    @Test
    public void testModuleDeserialiseInPeriodVagueBody1() throws IOException
    {
        PositionDTO converted = moduleMapper.readValue(positionInPeriodDTOBody1Stream, PositionDTO.class);
        assertEquals(PositionInPeriodDTO.class, converted.getClass());
        assertEquals(367963, converted.userId);
        assertEquals(791140.44, (double) ((PositionInPeriodDTO) converted).marketValueEndPeriodRefCcy, 0);
    }
}
