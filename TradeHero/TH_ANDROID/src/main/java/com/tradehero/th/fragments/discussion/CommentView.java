package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.DiscussionDTO;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/12/14 Time: 5:47 PM Copyright (c) TradeHero
 */
public class CommentView extends LinearLayout
        implements DTOView<DiscussionDTO>
{
    public CommentView(Context context)
    {
        super(context);
    }

    public CommentView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CommentView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override public void display(DiscussionDTO dto)
    {

    }
}
