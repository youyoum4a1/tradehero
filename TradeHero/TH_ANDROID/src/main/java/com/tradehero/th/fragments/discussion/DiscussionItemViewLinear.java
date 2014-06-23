package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.utils.THRouter;
import javax.inject.Inject;

public class DiscussionItemViewLinear<T extends DiscussionKey>
        extends AbstractDiscussionCompactItemViewLinear<T>
{
    @Inject THRouter thRouter;

    //<editor-fold desc="Constructors">
    public DiscussionItemViewLinear(Context context)
    {
        super(context);
    }

    public DiscussionItemViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DiscussionItemViewLinear(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        socialShareHelper.onDetach();
        super.onDetachedFromWindow();
    }

    @Override protected DiscussionItemViewHolder createViewHolder()
    {
        return new DiscussionItemViewHolder<DiscussionDTO>();
    }

    @Override protected void linkWith(AbstractDiscussionCompactDTO abstractDiscussionDTO,
            boolean andDisplay)
    {
        super.linkWith(abstractDiscussionDTO, andDisplay);
    }

    protected void handleUserClicked(UserBaseKey userClicked)
    {
        Bundle bundle = new Bundle();
        thRouter.save(bundle, userClicked);
        getNavigator().pushFragment(PushableTimelineFragment.class, bundle);
    }

    abstract protected class DiscussionItemViewMenuClickedListener
        extends AbstractDiscussionViewHolderClickedListener
            implements DiscussionItemViewHolder.OnMenuClickedListener
    {
        @Override public void onUserClicked(UserBaseKey userClicked)
        {
            handleUserClicked(userClicked);
        }
    }
}
