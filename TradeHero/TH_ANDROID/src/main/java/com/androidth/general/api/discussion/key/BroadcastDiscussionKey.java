package com.androidth.general.api.discussion.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.DiscussionType;

public class BroadcastDiscussionKey extends DiscussionKey<BroadcastDiscussionKey>
{
    private static final DiscussionType TYPE = DiscussionType.BROADCAST_MESSAGE;

    //<editor-fold desc="Constructors">
    public BroadcastDiscussionKey(@NonNull Integer id)
    {
        super(id);
    }

    protected BroadcastDiscussionKey(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @NonNull @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
