package com.androidth.general.api.discussion.form;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.api.discussion.key.DiscussionKey;
import com.androidth.general.api.timeline.form.PublishableFormDTO;

public class DiscussionFormDTO extends PublishableFormDTO
{
    /**
     * This stub discussion key is used to simulate an immediate post,
     * and also to keep track of the query. Leaving it null is fine.
     */
    @JsonIgnore
    @Nullable public DiscussionKey stubKey;

    public String text;
    public String langCode;
    public String url; // to post a link
    public Integer recipientUserId;
}
