package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.PrivateDiscussionDTO;
import com.tradehero.th.api.discussion.key.PrivateMessageKey;
import com.tradehero.th.fragments.discussion.AbstractDiscussionItemView;

public class PrivateMessageBubbleView extends AbstractDiscussionItemView<PrivateMessageKey>
{
    PrivateMessageBubbleViewHolder viewHolder;

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

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        viewHolder = new PrivateMessageBubbleViewHolder();
        viewHolder.initView(this);
    }

    public void display(PrivateDiscussionDTO discussionDTO)
    {
        display(discussionDTO.getDiscussionKey());
        viewHolder.linkWith(discussionDTO, true);
    }
}
