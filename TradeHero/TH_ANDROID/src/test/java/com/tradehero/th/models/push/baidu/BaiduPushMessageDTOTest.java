package com.tradehero.th.models.push.baidu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.utils.IOUtils;
import com.tradehero.th.api.discussion.DiscussionType;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class BaiduPushMessageDTOTest
{
    private String baiduMessageDTOContent1;

    @Before
    public void setUp() throws IOException
    {
        baiduMessageDTOContent1 = new String(IOUtils.streamToBytes(getClass().getResourceAsStream("/baidu_push_dto_1.json")));
    }

    @Test public void testDeserializeBaiduPushMessageDTO() throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        BaiduPushMessageDTO baiduMessageDTO = objectMapper.readValue(baiduMessageDTOContent1, BaiduPushMessageDTO.class);

        BaiduPushMessageDTO expectValue = new BaiduPushMessageDTO();
        expectValue.title = "notification1";
        expectValue.description = "description1";
        expectValue.customContentDTO = new BaiduPushMessageDTO.BaiduPushMessageCustomContentDTO();
        expectValue.customContentDTO.id = 8438430;
        expectValue.customContentDTO.discussionType = DiscussionType.COMMENT;

        assertThat(baiduMessageDTO).isEqualsToByComparingFields(expectValue);
    }
}
