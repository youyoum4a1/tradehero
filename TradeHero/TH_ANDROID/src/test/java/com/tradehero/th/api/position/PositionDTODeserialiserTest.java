package com.tradehero.th.api.position;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PositionDTODeserialiserTest
{
    private InputStream positionDTOBody1Stream;
    private String positionDTOBody1;

    @Before
    public void setUp() throws IOException
    {
        positionDTOBody1 = new String(IOUtils.streamToBytes(getClass().getResourceAsStream("/com/tradehero/th/api/position/PositionDTOBody1.json")));
        positionDTOBody1Stream = getClass().getResourceAsStream("/com/tradehero/th/api/position/PositionDTOBody1.json");
    }

    @Test
    public void testNormalDeserialiseBody1() throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        PositionDTO converted = objectMapper.readValue(positionDTOBody1Stream, PositionDTO.class);
        assertEquals(PositionDTO.class, converted.getClass());
        assertEquals(239284, converted.userId);
    }

    @Test
    public void testModuleDeserialiseBody1() throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new PositionDTOFactory().createPositionDTOModule());

        PositionDTO converted = objectMapper.readValue(positionDTOBody1Stream, PositionDTO.class);
        assertEquals(PositionDTO.class, converted.getClass());
        assertEquals(239284, converted.userId);
    }
}
