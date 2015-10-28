package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PagedOwnedPortfolioId;
import com.tradehero.th.api.portfolio.PerPagedOwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

@Singleton public class PositionServiceWrapper
{
    @NonNull private final PositionServiceRx positionServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public PositionServiceWrapper(
            @NonNull PositionServiceRx positionServiceRx)
    {
        super();
        this.positionServiceRx = positionServiceRx;
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
}
