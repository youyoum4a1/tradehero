package com.ayondo.academy.models.level;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.ayondo.academy.api.achievement.key.UserAchievementId;
import com.ayondo.academy.api.level.UserXPAchievementDTO;
import com.ayondo.academy.fragments.achievement.AbstractAchievementDialogFragment;
import rx.Observable;
import rx.functions.Func1;

import static com.ayondo.academy.utils.broadcast.BroadcastConstants.ACHIEVEMENT_INTENT_FILTER;
import static com.ayondo.academy.utils.broadcast.BroadcastConstants.KEY_USER_ACHIEVEMENT_ID;
import static com.ayondo.academy.utils.broadcast.BroadcastConstants.KEY_XP_BROADCAST;
import static com.ayondo.academy.utils.broadcast.BroadcastConstants.XP_INTENT_FILTER;
import static rx.android.content.ContentObservable.fromLocalBroadcast;

public class UserXPAchievementUtil
{
    @NonNull public static Observable<UserXPAchievementDTO> getLocalBroadcastXp(@NonNull final Context context)
    {
        return fromLocalBroadcast(context, XP_INTENT_FILTER)
                .flatMap(new Func1<Intent, Observable<UserXPAchievementDTO>>()
                {
                    @Override public Observable<UserXPAchievementDTO> call(Intent intent)
                    {
                        if ((intent.getBundleExtra(KEY_XP_BROADCAST) != null))
                        {
                            return Observable.just(new UserXPAchievementDTO(intent.getBundleExtra(KEY_XP_BROADCAST)));
                        }
                        return Observable.empty();
                    }
                });
    }

    @NonNull public static Observable<AbstractAchievementDialogFragment> getLocalBroadcastAchievementDialog(
            @NonNull Context context,
            @NonNull final AbstractAchievementDialogFragment.Creator achievementDialogCreator)
    {
        return fromLocalBroadcast(context, ACHIEVEMENT_INTENT_FILTER)
                .flatMap(new Func1<Intent, Observable<? extends AbstractAchievementDialogFragment>>()
                {
                    @Override public Observable<? extends AbstractAchievementDialogFragment> call(Intent intent)
                    {
                        if (intent.getBundleExtra(KEY_USER_ACHIEVEMENT_ID) != null)
                        {
                            Bundle extra = intent.getBundleExtra(KEY_USER_ACHIEVEMENT_ID);
                            return achievementDialogCreator.newInstance(new UserAchievementId(extra));
                        }
                        return Observable.empty();
                    }
                });
    }
}
