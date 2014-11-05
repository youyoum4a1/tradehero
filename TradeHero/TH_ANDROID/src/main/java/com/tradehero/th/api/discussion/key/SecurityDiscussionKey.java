package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import android.support.annotation.NonNull;

public class SecurityDiscussionKey extends DiscussionKey<SecurityDiscussionKey>
{
    private static final DiscussionType TYPE = DiscussionType.SECURITY;

    //<editor-fold desc="Constructors">
    public SecurityDiscussionKey(@NonNull Integer id)
    {
        super(id);
    }

    public SecurityDiscussionKey(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
