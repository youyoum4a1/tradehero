package com.tradehero.th.api.discussion.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.timeline.form.PublishableFormDTO;

abstract public class DiscussionFormDTO extends PublishableFormDTO
{
    /**
     * This stub discussion key is used to simulate an immediate post,
     * and also to keep track of the query. Leaving it null is fine.
     */
    public DiscussionKey stubKey;

    public String text;
    public String langCode;
    public int inReplyToId;
    public String url; // to post a link
    public Integer recipientUserId;

    public DiscussionFormDTO()
    {
        super();
    }

    @JsonProperty
    abstract public DiscussionType getInReplyToType();
}
