package com.tradehero.th.models.push.baidu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.discussion.DiscussionType;

public class BaiduPushMessageDTO
{
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("custom_content")
    private BaiduPushMessageCustomContentDTO customContentDTO;

    public BaiduPushMessageDTO()
    {
        super();
    }

    public BaiduPushMessageDTO(String title, String description, BaiduPushMessageCustomContentDTO customContentDTO)
    {
        this.title = title;
        this.description = description;
        this.customContentDTO = customContentDTO;
    }

    public int getId()
    {
        return customContentDTO != null ? customContentDTO.id : 0;
    }

    public DiscussionType getDiscussionType()
    {
        return customContentDTO.discussionType;
    }

    public String getDescription()
    {
        return description;
    }

    public String getTitle()
    {
        return title;
    }

    public BaiduPushMessageCustomContentDTO getCustomContentDTO()
    {
        return customContentDTO;
    }

    public static class BaiduPushMessageCustomContentDTO
    {
        @JsonProperty("i")
        private int id;

        @JsonProperty(value = "discussion-type",required = false)
        private DiscussionType discussionType;

        public BaiduPushMessageCustomContentDTO()
        {
            super();
        }

        public BaiduPushMessageCustomContentDTO(int id, DiscussionType discussionType)
        {
            this.id = id;
            this.discussionType = discussionType;
        }

        public int getId()
        {
            return id;
        }

        public DiscussionType getDiscussionType()
        {
            return discussionType;
        }

        @Override public boolean equals(Object o)
        {
            if (o instanceof BaiduPushMessageCustomContentDTO)
            {
                BaiduPushMessageCustomContentDTO target = (BaiduPushMessageCustomContentDTO) o;
                return target.id == id && target.discussionType == discussionType;
            }
            return false;
        }
    }
}
