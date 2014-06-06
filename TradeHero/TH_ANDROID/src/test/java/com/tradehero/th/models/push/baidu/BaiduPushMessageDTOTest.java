package com.tradehero.th.models.push.baidu;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.TestConstants;
import com.tradehero.th.api.discussion.DiscussionType;
import java.io.IOException;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;

import static com.tradehero.util.TestUtil.getResourceAsByteArray;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class BaiduPushMessageDTOTest
{
    @Inject Converter converter;

    @Before
    public void setUp() throws IOException
    {
    }

    @Test public void testDeserializeBaiduPushMessageDTO() throws IOException, ConversionException
    {
        byte[] baiduPushDTO1 = getResourceAsByteArray(BaiduPushMessageDTO.class, "baidu_push_dto_1.json");
        TypedByteArray typedInput = new TypedByteArray(TestConstants.JSON_MIME_UTF8, baiduPushDTO1);

        BaiduPushMessageDTO baiduMessageDTO = (BaiduPushMessageDTO) converter.fromBody(typedInput, BaiduPushMessageDTO.class);

        BaiduPushMessageDTO expectValue = new BaiduPushMessageDTO();
        expectValue.title = "notification1";
        expectValue.description = "description1";
        expectValue.customContentDTO = new BaiduPushMessageDTO.BaiduPushMessageCustomContentDTO();
        expectValue.customContentDTO.id = 8438430;
        expectValue.customContentDTO.discussionType = DiscussionType.COMMENT;

        assertThat(baiduMessageDTO).isEqualsToByComparingFields(expectValue);
    }
}
