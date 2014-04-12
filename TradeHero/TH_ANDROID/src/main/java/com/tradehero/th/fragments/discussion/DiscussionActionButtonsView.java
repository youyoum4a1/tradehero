package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;

/**
 * TODO create share action buttons for discussion item view (timeline, news, comment)
 * Created by thonguyen on 12/4/14.
 */
public class DiscussionActionButtonsView extends LinearLayout
{
    @InjectView(R.id.timeline_action_button_comment) TextView comment;
    @InjectView(R.id.timeline_action_button_more) TextView more;

    //<editor-fold desc="Constructors">
    public DiscussionActionButtonsView(Context context)
    {
        super(context);
    }

    public DiscussionActionButtonsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DiscussionActionButtonsView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }
}
