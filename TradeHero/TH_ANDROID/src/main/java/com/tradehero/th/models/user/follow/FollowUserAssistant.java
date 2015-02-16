package com.tradehero.th.models.user.follow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

public class FollowUserAssistant extends SimpleFollowUserAssistant
{
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserProfileCacheRx userProfileCache;
    @Inject protected THBillingInteractorRx billingInteractorRx;

//    @NonNull protected final OwnedPortfolioId applicablePortfolioId;
    @Nullable protected Integer requestCode;

    //<editor-fold desc="Constructors">
    public FollowUserAssistant(
            @NonNull Context context,
            @NonNull UserBaseKey heroId
//            @NonNull OwnedPortfolioId applicablePortfolioId
    )
    {
        super(context, heroId);
//        this.applicablePortfolioId = applicablePortfolioId;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<UserProfileDTO> launchPremiumFollowRx()
    {
        return userProfileCache.get(currentUserId.toUserBaseKey())
                .take(1)
                .flatMap(new Func1<Pair<UserBaseKey, UserProfileDTO>, Observable<? extends UserProfileDTO>>()
                {
                    @Override public Observable<? extends UserProfileDTO> call(Pair<UserBaseKey, UserProfileDTO> pair)
                    {
                        if (pair.second.ccBalance > 0)
                        {
                            return FollowUserAssistant.super.launchPremiumFollowRx();
                        }
                        else
                        {
                            //noinspection unchecked
                            return billingInteractorRx.purchaseAndPremiumFollowAndClear(heroId)
                                    .flatMap(new Func1<PurchaseResult, Observable<UserProfileDTO>>()
                                    {
                                        @Override public Observable<UserProfileDTO> call(PurchaseResult result)
                                        {
                                            return userProfileCache.get(currentUserId.toUserBaseKey())
                                                    .take(1)
                                                    .map(new PairGetSecond<UserBaseKey, UserProfileDTO>());
                                        }
                                    });
                        }
                    }
                });
    }
}
