package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.timeline.form.PublishableFormDTO;
import android.support.annotation.Nullable;

public class DiscussionFormDTO extends PublishableFormDTO
{
    /**
     * This stub discussion key is used to simulate an immediate post,
     * and also to keep track of the query. Leaving it null is fine.
     */
    @Nullable public DiscussionKey stubKey;

    public String text;
    public String langCode;
    public String url; // to post a link
    public Integer recipientUserId;

    public DiscussionFormDTO()
    {
        super();
    }
}
