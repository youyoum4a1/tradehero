package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;

public class PrivateMessageKey extends DiscussionKey
{
    private static final DiscussionType TYPE = DiscussionType.PRIVATE_MESSAGE;

    //<editor-fold desc="Constructors">
    public PrivateMessageKey(Integer id)
    {
        super(id);
    }

    protected PrivateMessageKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
