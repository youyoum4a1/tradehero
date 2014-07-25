package com.tradehero.th.models.push.baidu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.utils.JacksonConverter;
import com.tradehero.th.api.discussion.DiscussionType;
import java.io.IOException;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;

import static com.tradehero.util.TestUtil.getResourceAsByteArray;
import static org.fest.assertions.api.Assertions.assertThat;

public class BaiduPushMessageDTOTest
{
    @Inject Converter converter;
    private byte[] baiduPushDTO;

    @Before
    public void setUp() throws IOException
    {
        baiduPushDTO = getResourceAsByteArray(BaiduPushMessageDTO.class, "baidu_push_dto.json");
        converter = new JacksonConverter(new ObjectMapper());
    }

    @After
    public void tearDown()
    {
        baiduPushDTO = null;
        converter = null;
    }

    @Test public void testDeserializeAggressively() throws InterruptedException, ConversionException, IOException
    {
        for (int i=0; i < 100; ++i)
        {
            testDeserializeBaiduPushMessageDTO1();
        }
    }

    @Test public void testDeserializeBaiduPushMessageDTO1() throws IOException, ConversionException, InterruptedException
    {
        BaiduPushMessageDTO baiduMessageDTO = (BaiduPushMessageDTO) converter.fromBody(
                new TypedByteArray(JacksonConverter.MIME_TYPE, baiduPushDTO), BaiduPushMessageDTO.class);

        BaiduPushMessageDTO.BaiduPushMessageCustomContentDTO customContentDTO = new BaiduPushMessageDTO.BaiduPushMessageCustomContentDTO(
                8990773, DiscussionType.BROADCAST_MESSAGE
        );
        BaiduPushMessageDTO expectValue = new BaiduPushMessageDTO("Notification", "Tho Nguyen Truong replied: Hhjk", customContentDTO);
        assertThat(baiduMessageDTO.getCustomContentDTO().getDiscussionType()).isEqualTo(expectValue.getCustomContentDTO().getDiscussionType());
    }
}
