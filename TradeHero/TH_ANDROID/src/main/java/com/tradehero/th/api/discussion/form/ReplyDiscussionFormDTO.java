package com.tradehero.th.api.discussion.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;

abstract public class ReplyDiscussionFormDTO extends DiscussionFormDTO
{
    public int inReplyToId;

    @JsonProperty
    abstract public DiscussionType getInReplyToType();

    @JsonIgnore
    abstract public DiscussionKey getInitiatingDiscussionKey();
}
