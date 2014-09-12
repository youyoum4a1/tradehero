package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.discussion.PrivateDiscussionDTO;
import com.tradehero.th.api.discussion.key.PrivateMessageKey;
import com.tradehero.th.fragments.discussion.DiscussionItemViewHolder;
import com.tradehero.th.fragments.discussion.DiscussionItemViewLinear;

public class PrivateMessageBubbleViewLinear extends DiscussionItemViewLinear<PrivateMessageKey>
{
    //<editor-fold desc="Constructors">
    public PrivateMessageBubbleViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected DiscussionItemViewHolder createViewHolder()
    {
        return new DiscussionItemViewHolder<PrivateDiscussionDTO>(getContext());
    }
}
