package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import org.jetbrains.annotations.NotNull;

public class SecurityDiscussionKey extends DiscussionKey<SecurityDiscussionKey>
{
    private static final DiscussionType TYPE = DiscussionType.SECURITY;

    //<editor-fold desc="Constructors">
    public SecurityDiscussionKey(@NotNull Integer id)
    {
        super(id);
    }

    public SecurityDiscussionKey(@NotNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
