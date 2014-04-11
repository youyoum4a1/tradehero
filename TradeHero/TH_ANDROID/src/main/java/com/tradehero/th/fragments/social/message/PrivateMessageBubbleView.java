package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;

public class PrivateMessageBubbleView extends RelativeLayout
    implements DTOView<AbstractDiscussionDTO>
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

    @Override public void display(AbstractDiscussionDTO dto)
    {
        viewHolder.linkWith(dto, true);
    }
}
