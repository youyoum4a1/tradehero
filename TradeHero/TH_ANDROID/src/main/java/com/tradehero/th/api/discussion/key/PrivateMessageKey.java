package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import android.support.annotation.NonNull;

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

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
