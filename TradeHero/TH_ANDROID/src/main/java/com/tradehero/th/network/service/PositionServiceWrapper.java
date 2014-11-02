package com.tradehero.th.network.service;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PagedOwnedPortfolioId;
import com.tradehero.th.api.portfolio.PerPagedOwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class PositionServiceWrapper
{
    @NotNull private final PositionService positionService;
    @NotNull private final PositionServiceRx positionServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public PositionServiceWrapper(
            @NotNull PositionService positionService,
            @NotNull PositionServiceRx positionServiceRx)
    {
        super();
        this.positionService = positionService;
        this.positionServiceRx = positionServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get One User Portfolio Positions List">
    @NotNull public GetPositionsDTO getPositions(@NotNull OwnedPortfolioId ownedPortfolioId)
    {
        GetPositionsDTO returned;
        if (ownedPortfolioId instanceof PerPagedOwnedPortfolioId)
        {
            PerPagedOwnedPortfolioId perPagedOwnedPortfolioId = (PerPagedOwnedPortfolioId) ownedPortfolioId;
            returned = this.positionService.getPositions(
                    perPagedOwnedPortfolioId.userId,
                    perPagedOwnedPortfolioId.portfolioId,
                    perPagedOwnedPortfolioId.page,
                    perPagedOwnedPortfolioId.perPage);
        }
        else if (ownedPortfolioId instanceof PagedOwnedPortfolioId)
        {
            PagedOwnedPortfolioId pagedOwnedPortfolioId = (PagedOwnedPortfolioId) ownedPortfolioId;
            returned = this.positionService.getPositions(
                    pagedOwnedPortfolioId.userId,
                    pagedOwnedPortfolioId.portfolioId,
                    pagedOwnedPortfolioId.page,
                    null);

        }
        else
        {
            returned = this.positionService.getPositions(
                    ownedPortfolioId.userId,
                    ownedPortfolioId.portfolioId,
                    null,
                    null);
        }
        return returned;
    }

    @NotNull public Observable<GetPositionsDTO> getPositionsRx(@NotNull OwnedPortfolioId ownedPortfolioId)
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
