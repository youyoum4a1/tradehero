package com.tradehero.th.billing;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Observable;

public class THBillingRequisitePreparer
{
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final PortfolioCompactListCacheRx portfolioCompactListCache;
    @NonNull private final Observable<Pair<UserProfileDTO, PortfolioCompactDTOList>> requisiteObservable;

    //<editor-fold desc="Constructors">
    @Inject public THBillingRequisitePreparer(
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull PortfolioCompactListCacheRx portfolioCompactListCache)
    {
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.requisiteObservable = createRequisiteObservable();
    }
    //</editor-fold>

    protected Observable<Pair<UserProfileDTO, PortfolioCompactDTOList>> createRequisiteObservable()
    {
        return Observable.combineLatest(
                userProfileCache.get(currentUserId.toUserBaseKey()),
                portfolioCompactListCache.get(currentUserId.toUserBaseKey()),
                (pair1, pair2) -> Pair.create(pair1.second, pair2.second));
    }

    @NonNull public Observable<Pair<UserProfileDTO, PortfolioCompactDTOList>> getRequisiteObservable()
    {
        return requisiteObservable.asObservable();
    }

    public void getNext()
    {
        userProfileCache.get(currentUserId.toUserBaseKey());
        portfolioCompactListCache.get(currentUserId.toUserBaseKey());
    }
}
