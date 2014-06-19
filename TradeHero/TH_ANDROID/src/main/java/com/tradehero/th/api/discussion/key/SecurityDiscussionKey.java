package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;

public class SecurityDiscussionKey extends DiscussionKey
{
    private static final DiscussionType TYPE = DiscussionType.SECURITY;

    //<editor-fold desc="Constructors">
    public SecurityDiscussionKey(Integer id)
    {
        super(id);
    }

    public SecurityDiscussionKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
