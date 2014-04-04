package com.tradehero.th.fragments.discussion.stock;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.DiscussionKey;

/**
 * Created by thonguyen on 4/4/14.
 */
public class SecurityDiscussionItemView extends LinearLayout
    implements DTOView<DiscussionKey>
{
    private DiscussionKey discussionKey;

    //<editor-fold desc="Constructors">
    public SecurityDiscussionItemView(Context context)
    {
        super(context);
    }

    public SecurityDiscussionItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SecurityDiscussionItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    @Override public void display(DiscussionKey discussionKey)
    {
        this.discussionKey = discussionKey;
    }
}
