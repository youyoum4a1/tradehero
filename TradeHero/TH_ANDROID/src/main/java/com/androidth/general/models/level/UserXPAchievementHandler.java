package com.androidth.general.models.level;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import com.androidth.general.common.rx.LifecycleEventWithActivity;
import com.androidth.general.common.rx.LifecycleObservableUtil;
import com.androidth.general.R;
import com.androidth.general.activities.AchievementAcceptor;
import com.androidth.general.api.level.UserXPAchievementDTO;
import com.androidth.general.fragments.achievement.AbstractAchievementDialogFragment;
import com.androidth.general.utils.broadcast.BroadcastUtils;
import com.androidth.general.widget.XpToast;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

public class UserXPAchievementHandler
{
    @SuppressWarnings("FieldCanBeLocal")
    private final int TAG_XP_SUBSCRIPTION = R.color.tradehero_blue;
    @SuppressWarnings("FieldCanBeLocal")
    private final int TAG_ACHIEVEMENT_SUBSCRIPTION = R.color.tradehero_blue_focused;

    @NonNull final AbstractAchievementDialogFragment.Creator achievementDialogCreator;
    @NonNull final BroadcastUtils broadcastUtils;
    @NonNull SubscriptionList subscriptions;

    //<editor-fold desc="Constructors">
    @Inject public UserXPAchievementHandler(
            @NonNull AbstractAchievementDialogFragment.Creator achievementDialogCreator,
            @NonNull BroadcastUtils broadcastUtils)
    {
        this.achievementDialogCreator = achievementDialogCreator;
        this.broadcastUtils = broadcastUtils;
        subscriptions = new SubscriptionList();
    }
    //</editor-fold>

    public void register(@NonNull final Application application)
    {
        subscriptions.add(LifecycleObservableUtil.getObservable(application)
                .retry()
                .subscribe(
                        new Observer<LifecycleEventWithActivity>()
                        {
                            @Override public void onNext(LifecycleEventWithActivity activityEvent)
                            {
                                handle(application.getApplicationContext(), activityEvent);
                            }

                            @Override public void onCompleted()
                            {
                            }

                            @Override public void onError(Throwable e)
                            {
                                Timber.e(e, "Failed to listen to LifecycleEvent");
                            }
                        }
                ));
    }

    public void unregister()
    {
        subscriptions.unsubscribe();
        subscriptions = new SubscriptionList();
    }

    void handle(@NonNull Context context, @NonNull final LifecycleEventWithActivity activityEvent)
    {
        final XpToast xpToast;
        switch (activityEvent.event)
        {
            case START:
                xpToast = (XpToast) activityEvent.activity.findViewById(R.id.xp_toast_box);
                if (xpToast != null)
                {
                    xpToast.setTag(
                            TAG_XP_SUBSCRIPTION,
                            UserXPAchievementUtil.getLocalBroadcastXp(context)
                                    .retry()
                                    .subscribe(
                                            new Observer<UserXPAchievementDTO>()
                                            {
                                                @Override public void onNext(UserXPAchievementDTO achievementDTO)
                                                {
                                                    xpToast.showWhenReady(achievementDTO);
                                                }

                                                @Override public void onCompleted()
                                                {
                                                }

                                                @Override public void onError(Throwable e)
                                                {
                                                    broadcastUtils.nextPlease();
                                                }
                                            }));
                }

                if (activityEvent.activity instanceof AchievementAcceptor)
                {
                    activityEvent.activity.getWindow().getDecorView().setTag(
                            TAG_ACHIEVEMENT_SUBSCRIPTION,
                            UserXPAchievementUtil.getLocalBroadcastAchievementDialog(activityEvent.activity, achievementDialogCreator)
                                    .retry()
                                    .subscribe(
                                            new Observer<AbstractAchievementDialogFragment>()
                                            {
                                                @Override public void onNext(AbstractAchievementDialogFragment fragment)
                                                {
                                                    fragment.show(((FragmentActivity) activityEvent.activity).getSupportFragmentManager(),
                                                            AbstractAchievementDialogFragment.TAG);
                                                }

                                                @Override public void onCompleted()
                                                {
                                                }

                                                @Override public void onError(Throwable e)
                                                {
                                                    broadcastUtils.nextPlease();
                                                }
                                            }));
                }
                break;

            case STOP:
                xpToast = (XpToast) activityEvent.activity.findViewById(R.id.xp_toast_box);
                if (xpToast != null)
                {
                    Subscription xpSubscription = (Subscription) xpToast.getTag(TAG_XP_SUBSCRIPTION);
                    if (xpSubscription != null)
                    {
                        xpSubscription.unsubscribe();
                    }
                    xpToast.setTag(TAG_XP_SUBSCRIPTION, null);
                }

                Subscription achievementSubscription = (Subscription) activityEvent.activity.getWindow()
                        .getDecorView()
                        .getTag(TAG_ACHIEVEMENT_SUBSCRIPTION);
                if (achievementSubscription != null)
                {
                    achievementSubscription.unsubscribe();
                }
                break;

            case DESTROY:
                xpToast = (XpToast) activityEvent.activity.findViewById(R.id.xp_toast_box);
                if (xpToast != null)
                {
                    xpToast.destroy();
                }
                break;
        }
    }
}
