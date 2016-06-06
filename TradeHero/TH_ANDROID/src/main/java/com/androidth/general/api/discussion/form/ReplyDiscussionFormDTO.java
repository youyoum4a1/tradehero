package com.androidth.general.api.discussion.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.key.DiscussionKey;

abstract public class ReplyDiscussionFormDTO extends DiscussionFormDTO
{
    public int inReplyToId;

    @JsonProperty
    abstract public DiscussionType getInReplyToType();

    @JsonIgnore
    abstract public DiscussionKey getInitiatingDiscussionKey();
}
