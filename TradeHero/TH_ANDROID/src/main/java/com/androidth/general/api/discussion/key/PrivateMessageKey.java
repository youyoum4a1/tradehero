package com.androidth.general.api.discussion.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.DiscussionType;

public class PrivateMessageKey extends DiscussionKey<PrivateMessageKey>
{
    private static final DiscussionType TYPE = DiscussionType.PRIVATE_MESSAGE;

    //<editor-fold desc="Constructors">
    public PrivateMessageKey(@NonNull Integer id)
    {
        super(id);
    }

    protected PrivateMessageKey(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @NonNull @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
