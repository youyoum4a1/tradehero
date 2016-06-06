package com.androidth.general.api.news.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.key.DiscussionKey;

public class NewsItemDTOKey extends DiscussionKey<NewsItemDTOKey>
{
    private static final DiscussionType TYPE = DiscussionType.NEWS;

    //<editor-fold desc="Constructors">
    public NewsItemDTOKey(@NonNull Integer id)
    {
        super(id);
    }

    public NewsItemDTOKey(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @NonNull @Override public DiscussionType getType()
    {
        return TYPE;
    }
}
