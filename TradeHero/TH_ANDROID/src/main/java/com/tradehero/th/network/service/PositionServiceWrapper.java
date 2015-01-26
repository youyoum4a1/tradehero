package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PagedOwnedPortfolioId;
import com.tradehero.th.api.portfolio.PerPagedOwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class PositionServiceWrapper
{
    @NonNull private final PositionServiceRx positionServiceRx;
    @NonNull private final Lazy<SecurityPositionDetailCacheRx> securityPositionDetailCacheRx;

    //<editor-fold desc="Constructors">
    @Inject public PositionServiceWrapper(
            @NonNull PositionServiceRx positionServiceRx,
            @NonNull Lazy<SecurityPositionDetailCacheRx> securityPositionDetailCacheRx)
    {
        super();
        this.positionServiceRx = positionServiceRx;
        this.securityPositionDetailCacheRx = securityPositionDetailCacheRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get One User Portfolio Positions List">
    @NonNull public Observable<GetPositionsDTO> getPositionsRx(@NonNull OwnedPortfolioId ownedPortfolioId)
    {
        Observable<GetPositionsDTO> returned;
        if (ownedPortfolioId instanceof PerPagedOwnedPortfolioId)
        {
            PerPagedOwnedPortfolioId perPagedOwnedPortfolioId = (PerPagedOwnedPortfolioId) ownedPortfolioId;
            returned = this.positionServiceRx.getPositions(
                    perPagedOwnedPortfolioId.userId,
                    perPagedOwnedPortfolioId.portfolioId,
                    perPagedOwnedPortfolioId.page,
                    perPagedOwnedPortfolioId.perPage);
        }
        else if (ownedPortfolioId instanceof PagedOwnedPortfolioId)
        {
            PagedOwnedPortfolioId pagedOwnedPortfolioId = (PagedOwnedPortfolioId) ownedPortfolioId;
            returned = this.positionServiceRx.getPositions(
                    pagedOwnedPortfolioId.userId,
                    pagedOwnedPortfolioId.portfolioId,
                    pagedOwnedPortfolioId.page,
                    null);
        }
        else
        {
            returned = this.positionServiceRx.getPositions(
                    ownedPortfolioId.userId,
                    ownedPortfolioId.portfolioId,
                    null,
                    null);
        }
        return returned;
    }
    //</editor-fold>

    @NonNull public Observable<PositionDTOCompactList> getSecurityPositions(@NonNull SecurityId securityId)
    {
        return positionServiceRx.getPositions(
                securityId.getExchange(),
                securityId.getPathSafeSymbol())
                .onErrorResumeNext(securityPositionDetailCacheRx.get().get(securityId)
                        .map(pair -> {
                            if (pair.second.positions != null)
                            {
                                return pair.second.positions;
                            }
                            return new PositionDTOCompactList();
                        }));
    }
}
