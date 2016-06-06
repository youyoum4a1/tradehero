package com.androidth.general.models.discussion;

import android.support.annotation.NonNull;
import com.androidth.general.common.text.ClickableTagProcessor;
import com.androidth.general.common.text.SecurityTagProcessor;
import com.androidth.general.common.text.UserTagProcessor;
import com.androidth.general.api.discussion.AbstractDiscussionCompactDTO;
import rx.Observable;
import timber.log.Timber;

public class UserDiscussionActionFactory
{
    @NonNull public static Observable<UserDiscussionAction> createObservable(
            @NonNull AbstractDiscussionCompactDTO discussionCompactDTO,
            @NonNull ClickableTagProcessor.UserAction userAction)
    {
        if (userAction instanceof UserTagProcessor.ProfileUserAction)
        {
            return Observable.just((UserDiscussionAction) new PlayerUserAction(
                    discussionCompactDTO,
                    ((UserTagProcessor.ProfileUserAction) userAction).userBaseKey));
        }
        else if (userAction instanceof SecurityTagProcessor.SecurityUserAction)
        {
            return Observable.just((UserDiscussionAction) new SecurityUserAction(
                    discussionCompactDTO,
                    ((SecurityTagProcessor.SecurityUserAction) userAction).securityId));
        }
        Timber.e(new Exception(), "Unhandled UserAction: " + userAction);
        return Observable.empty();
    }
}
