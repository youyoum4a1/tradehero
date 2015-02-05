package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
import rx.Observable;

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

    @NonNull @Override protected DiscussionItemViewHolder createViewHolder()
    {
        return new DiscussionItemViewHolder<>(getContext());
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

    @NonNull @Override protected Observable<DiscussionActionButtonsView.UserAction> handleUserAction(
            DiscussionActionButtonsView.UserAction userAction)
    {
        if (userAction instanceof AbstractDiscussionItemViewHolder.PlayerUserAction)
        {
            handleUserClicked(((AbstractDiscussionItemViewHolder.PlayerUserAction) userAction).userClicked);
            return Observable.empty();
        }
        return super.handleUserAction(userAction);
    }
}
