package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.key.DiscussionKey;

/**
 * Created by thonguyen on 10/4/14.
 */
public class DiscussionItemView extends LinearLayout
    implements DTOView<DiscussionKey>
{
    private DiscussionKey discussionKey;

    //<editor-fold desc="Constructors">
    public DiscussionItemView(Context context)
    {
        super(context);
    }

    public DiscussionItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DiscussionItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void display(DiscussionKey discussionKey)
    {
        this.discussionKey = discussionKey;
    }
}
