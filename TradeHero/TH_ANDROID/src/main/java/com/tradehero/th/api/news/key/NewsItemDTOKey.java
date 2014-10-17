package com.tradehero.th.api.news.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import org.jetbrains.annotations.NotNull;

public class NewsItemDTOKey extends DiscussionKey
{
    private static final DiscussionType TYPE = DiscussionType.NEWS;

    //<editor-fold desc="Constructors">
    public NewsItemDTOKey(@NotNull Integer id)
    {
        super(id);
    }

    public NewsItemDTOKey(@NotNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
