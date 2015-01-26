package com.tradehero.th.api.article;

import android.support.annotation.NonNull;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;

public class ArticleInfoDTOKey extends DiscussionKey<ArticleInfoDTOKey>
{
    //<editor-fold desc="Constructors">
    public ArticleInfoDTOKey(@NonNull Integer key)
    {
        super(key);
    }
    //</editor-fold>

    @Override @NonNull public DiscussionType getType()
    {
        return DiscussionType.ARTICLE;
    }
}
