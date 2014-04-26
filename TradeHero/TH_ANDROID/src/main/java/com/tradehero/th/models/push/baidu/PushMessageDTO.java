package com.tradehero.th.models.push.baidu;

import com.tradehero.th.api.discussion.DiscussionType;

/**
 * Created by wangliang on 14-4-26.
 */
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
