package com.tradehero.th.models.push.baidu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.discussion.DiscussionType;

public class BaiduPushMessageDTO
{
    @JsonProperty("title")
    public String title;

    @JsonProperty("description")
    public String description;

    @JsonProperty("custom_content")
    public BaiduPushMessageCustomContentDTO customContentDTO;

    public int getId()
    {
        return customContentDTO != null ? customContentDTO.id : 0;
    }

    public DiscussionType getDiscussionType()
    {
        return customContentDTO.discussionType;
    }

    public static class BaiduPushMessageCustomContentDTO
    {
        @JsonProperty("i")
        public int id;

        @JsonProperty(value = "discussion-type",required = false)
        public DiscussionType discussionType;
    }
}
