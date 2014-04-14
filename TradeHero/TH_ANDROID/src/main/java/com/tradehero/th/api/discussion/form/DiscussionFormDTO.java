package com.tradehero.th.api.discussion.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.timeline.form.PublishableFormDTO;

/**
 * Created by xavier2 on 2014/4/11.
 */
abstract public class DiscussionFormDTO extends PublishableFormDTO
{
    public String text;
    public String langCode;
    public int inReplyToId;
    public String url; // to post a link

    public DiscussionFormDTO()
    {
        super();
    }

    @JsonProperty
    abstract public DiscussionType getInReplyToType();
}
