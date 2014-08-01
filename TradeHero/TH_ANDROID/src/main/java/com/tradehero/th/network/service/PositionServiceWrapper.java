package com.tradehero.th.network.service;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PagedOwnedPortfolioId;
import com.tradehero.th.api.portfolio.PerPagedOwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class PositionServiceWrapper
{
    @NotNull private final PositionService positionService;
    @NotNull private final PositionServiceAsync positionServiceAsync;

    @Inject public PositionServiceWrapper(
            @NotNull PositionService positionService,
            @NotNull PositionServiceAsync positionServiceAsync)
    {
        super();
        this.positionService = positionService;
        this.positionServiceAsync = positionServiceAsync;
    }

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

    @NotNull public MiddleCallback<GetPositionsDTO> getPositions(
            @NotNull OwnedPortfolioId ownedPortfolioId,
            @Nullable Callback<GetPositionsDTO> callback)
    {
        MiddleCallback<GetPositionsDTO> middleCallback = new BaseMiddleCallback<>(callback);
        if (ownedPortfolioId instanceof PerPagedOwnedPortfolioId)
        {
            PerPagedOwnedPortfolioId perPagedOwnedPortfolioId = (PerPagedOwnedPortfolioId) ownedPortfolioId;
            this.positionServiceAsync.getPositions(
                    perPagedOwnedPortfolioId.userId,
                    perPagedOwnedPortfolioId.portfolioId,
                    perPagedOwnedPortfolioId.page,
                    perPagedOwnedPortfolioId.perPage,
                    middleCallback);
        }
        else if (ownedPortfolioId instanceof PagedOwnedPortfolioId)
        {
            PagedOwnedPortfolioId pagedOwnedPortfolioId = (PagedOwnedPortfolioId) ownedPortfolioId;
            this.positionServiceAsync.getPositions(
                    pagedOwnedPortfolioId.userId,
                    pagedOwnedPortfolioId.portfolioId,
                    pagedOwnedPortfolioId.page,
                    null,
                    middleCallback);
        }
        else
        {
            this.positionServiceAsync.getPositions(
                    ownedPortfolioId.userId,
                    ownedPortfolioId.portfolioId,
                    null,
                    null,
                    middleCallback);
        }
        return middleCallback;
    }
    //</editor-fold>
}
