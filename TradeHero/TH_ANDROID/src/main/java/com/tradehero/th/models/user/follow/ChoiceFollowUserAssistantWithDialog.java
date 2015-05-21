package com.tradehero.th.models.user.follow;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogRxUtil;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.social.FollowRequest;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.functions.Func1;

public class ChoiceFollowUserAssistantWithDialog
{
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserProfileCacheRx userProfileCache;

    @NonNull private final Activity activity;
    @NonNull protected UserBaseDTO heroBaseInfo;
    @NonNull protected final FollowUserAssistant followUserAssistant;

    //<editor-fold desc="Constructors">
    public ChoiceFollowUserAssistantWithDialog(
            @NonNull Activity activity,
            @NonNull UserBaseDTO heroDTO)
    {
        super();
        this.activity = activity;
        this.heroBaseInfo = heroDTO;
        this.followUserAssistant = new FollowUserAssistant(activity, heroDTO.getBaseKey());
        HierarchyInjector.inject(activity, this);
    }
    //</editor-fold>

    @NonNull public Observable<Pair<FollowRequest, UserProfileDTO>> launchChoiceRx()
    {
        return AppObservable.bindActivity(
                activity,
                userProfileCache.get(currentUserId.toUserBaseKey()).take(1))
                .map(new PairGetSecond<UserBaseKey, UserProfileDTO>())
                .flatMap(new Func1<UserProfileDTO, Observable<? extends FollowRequest>>()
                {
                    @Override public Observable<? extends FollowRequest> call(UserProfileDTO currentUserProfile)
                    {
                        return HeroAlertDialogRxUtil.showFollowDialog(
                                activity,
                                heroBaseInfo,
                                currentUserProfile.getFollowType(heroBaseInfo));
                    }
                })
                .flatMap(new Func1<FollowRequest, Observable<? extends Pair<FollowRequest, UserProfileDTO>>>()
                {
                    @Override public Observable<? extends Pair<FollowRequest, UserProfileDTO>> call(final FollowRequest request)
                    {
                        Observable<UserProfileDTO> observable;
                        if (request.isPremium)
                        {
                            observable = followUserAssistant.launchPremiumFollowRx();
                        }
                        else
                        {
                            observable = followUserAssistant.launchFreeFollowRx();
                        }
                        return observable.map(new Func1<UserProfileDTO, Pair<FollowRequest, UserProfileDTO>>()
                        {
                            @Override public Pair<FollowRequest, UserProfileDTO> call(UserProfileDTO profile)
                            {
                                return Pair.create(request, profile);
                            }
                        });
                    }
                });
    }
}
