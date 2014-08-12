package com.tradehero.th.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.PositionDTO;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class ObjectMapperWrapperTest extends BaseApiTest

{
    @Inject @ForApp ObjectMapper objectMapper;

    private InputStream positionDTOBody1Stream;

    @Before
    public void setUp() throws IOException
    {
        positionDTOBody1Stream = PositionDTO.class.getResourceAsStream(getPackagePath() + "/position/GetPositionsDTOWithAchievementBody1.json");
    }

    @Test public void mapperIsWrapper()
    {
        assertEquals(((Object) objectMapper).getClass(), ObjectMapperWrapper.class);
    }

    @Test public void mapperWorksAsRegular() throws IOException
    {
        GetPositionsDTO getPositionsDTO = objectMapper.readValue(positionDTOBody1Stream, GetPositionsDTO.class);
        assertThat(getPositionsDTO.securities.size()).isEqualTo(10);
        assertThat(getPositionsDTO.securities.get(1).id).isEqualTo(9256);
    }
}
