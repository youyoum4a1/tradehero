package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.discussion.key.PrivateMessageKey;
import com.tradehero.th.fragments.discussion.AbstractDiscussionItemView;

public class PrivateMessageBubbleView extends AbstractDiscussionItemView<PrivateMessageKey>
{
    //<editor-fold desc="Constructors">
    public PrivateMessageBubbleView(Context context)
    {
        super(context);
    }

    public PrivateMessageBubbleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PrivateMessageBubbleView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>
}
