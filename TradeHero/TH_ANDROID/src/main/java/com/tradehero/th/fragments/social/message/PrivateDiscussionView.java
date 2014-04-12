package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.th.R;
import com.tradehero.th.fragments.discussion.DiscussionListAdapter;
import com.tradehero.th.fragments.discussion.DiscussionView;

/**
 * Created by xavier2 on 2014/4/12.
 */
public class PrivateDiscussionView extends DiscussionView
{
    //<editor-fold desc="Constructors">
    public PrivateDiscussionView(Context context)
    {
        super(context);
    }

    public PrivateDiscussionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PrivateDiscussionView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected DiscussionListAdapter createDiscussionListAdapter()
    {
        return new PrivateDiscussionListAdapter(
                getContext(),
                LayoutInflater.from(getContext()),
                R.layout.private_message_bubble_mine,
                R.layout.private_message_bubble_other);
    }

    @Override protected void setLoading()
    {
        super.setLoading();
        if (discussionStatus != null)
        {
            discussionStatus.setVisibility(View.VISIBLE);
        }
    }

    @Override protected void setLoaded()
    {
        super.setLoaded();
        if (discussionStatus != null)
        {
            discussionStatus.setVisibility(View.GONE);
        }
    }
}
