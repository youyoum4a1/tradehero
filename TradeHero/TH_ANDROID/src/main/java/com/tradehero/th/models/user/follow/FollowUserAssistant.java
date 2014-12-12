package com.tradehero.th.models.user.follow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Observable;

public class FollowUserAssistant extends SimpleFollowUserAssistant
{
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserProfileCacheRx userProfileCache;
    @Inject protected THBillingInteractorRx billingInteractorRx;

    @NonNull protected final OwnedPortfolioId applicablePortfolioId;
    @Nullable protected Integer requestCode;

    //<editor-fold desc="Constructors">
    public FollowUserAssistant(
            @NonNull Context context,
            @NonNull UserBaseKey heroId,
            @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        super(context, heroId);
        this.applicablePortfolioId = applicablePortfolioId;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<UserProfileDTO> launchPremiumFollowRx()
    {
        return userProfileCache.get(currentUserId.toUserBaseKey())
                .take(1)
                .flatMap(pair -> {
                    if (pair.second.ccBalance > 0)
                    {
                        return super.launchPremiumFollowRx();
                    }
                    else
                    {
                        //noinspection unchecked
                        return billingInteractorRx.purchaseAndPremiumFollowAndClear(heroId)
                                .materialize()
                                .dematerialize();
                    }
                });
    }
}
