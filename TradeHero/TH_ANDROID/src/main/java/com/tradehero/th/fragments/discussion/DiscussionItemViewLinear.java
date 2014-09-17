package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;

public class DiscussionItemViewLinear<T extends DiscussionKey>
        extends AbstractDiscussionCompactItemViewLinear<T>
{
    @Inject THRouter thRouter;
    @Inject CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    public DiscussionItemViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onDetachedFromWindow()
    {
        socialShareHelper.onDetach();
        super.onDetachedFromWindow();
    }

    @Override protected DiscussionItemViewHolder createViewHolder()
    {
        return new DiscussionItemViewHolder<DiscussionDTO>(getContext());
    }

    protected void handleUserClicked(UserBaseKey userClicked)
    {
        Bundle bundle = new Bundle();
        thRouter.save(bundle, userClicked);
        if (currentUserId.toUserBaseKey().equals(userClicked))
        {
            getNavigator().pushFragment(MeTimelineFragment.class, bundle);
        }
        else
        {
            getNavigator().pushFragment(PushableTimelineFragment.class, bundle);
        }
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
