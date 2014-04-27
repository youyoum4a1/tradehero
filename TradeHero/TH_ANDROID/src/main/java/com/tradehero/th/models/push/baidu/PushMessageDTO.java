package com.tradehero.th.models.push.baidu;

import com.tradehero.th.api.discussion.DiscussionType;

public class PushMessageDTO
{
    public String title;
    public String description;
    public int id;
    public DiscussionType discussionType;

    public PushMessageDTO()
    {
    }

    public PushMessageDTO(String title,  String description,
            DiscussionType discussionType,int id)
    {
        this.title = title;
        this.id = id;
        this.description = description;
        this.discussionType = discussionType;
    }
}
