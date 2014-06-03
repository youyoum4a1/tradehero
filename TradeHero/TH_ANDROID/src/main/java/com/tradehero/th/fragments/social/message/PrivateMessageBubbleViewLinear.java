package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.discussion.PrivateDiscussionDTO;
import com.tradehero.th.api.discussion.key.PrivateMessageKey;
import com.tradehero.th.fragments.discussion.DiscussionItemViewLinear;
import com.tradehero.th.fragments.discussion.DiscussionItemViewHolder;

public class PrivateMessageBubbleViewLinear extends DiscussionItemViewLinear<PrivateMessageKey>
{
    //<editor-fold desc="Constructors">
    public PrivateMessageBubbleViewLinear(Context context)
    {
        super(context);
    }

    public PrivateMessageBubbleViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PrivateMessageBubbleViewLinear(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected DiscussionItemViewHolder createViewHolder()
    {
        return new DiscussionItemViewHolder<PrivateDiscussionDTO>();
    }
}
