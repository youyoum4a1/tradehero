package com.tradehero.th.models.user.follow;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogRxUtil;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.social.FollowRequest;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;

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
            @NonNull UserBaseDTO heroDTO
//            @NonNull OwnedPortfolioId applicablePortfolioId
    )
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
                .map(new PairGetSecond<>())
                .flatMap(currentUserProfile -> HeroAlertDialogRxUtil.showFollowDialog(
                        activity,
                        heroBaseInfo,
                        currentUserProfile.getFollowType(heroBaseInfo)))
                .flatMap(request -> {
                    Observable<UserProfileDTO> observable;
                    if (request.isPremium)
                    {
                        observable = followUserAssistant.launchPremiumFollowRx();
                    }
                    else
                    {
                        observable = followUserAssistant.launchFreeFollowRx();
                    }
                    return observable.map(profile -> Pair.create(request, profile));
                });
    }
}
