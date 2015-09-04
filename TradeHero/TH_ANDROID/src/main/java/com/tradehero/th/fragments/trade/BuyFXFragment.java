package com.tradehero.th.fragments.trade;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.internal.util.Predicate;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.metrics.events.SharingOptionsEvent;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;

public class BuyFXFragment extends AbstractFXTransactionFragment
{
    private static final boolean IS_BUY = true;

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Override protected void setBuyEventFor(SharingOptionsEvent.Builder builder)
    {
        builder.setBuyEvent(IS_BUY);
    }

    @Override protected String getLabel(@NonNull QuoteDTO quoteDTO)
    {
        if (quoteDTO.ask == null)
        {
            return getString(R.string.na);
        }
        THSignedNumber bThSignedNumber = getFormattedPrice(quoteDTO.ask);
        return bThSignedNumber.toString();
    }

    @Override @Nullable protected Double getProfitOrLossUsd(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition,
            @Nullable Integer quantity)
    {
        if (portfolioCompactDTO == null
                || quoteDTO == null
                || quoteDTO.ask == null)
        {
            return null;
        }
        if (closeablePosition == null || closeablePosition.averagePriceSecCcy == null || quantity == null)
        {
            return null;
        }

        return -quantity * quoteDTO.toUSDRate * (quoteDTO.ask - closeablePosition.averagePriceSecCcy);
    }

    @Override @NonNull public String getCashShareLeft(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition, int quantity)
    {
        return getRemainingWhenBuy(portfolioCompactDTO, quoteDTO, closeablePosition, quantity);
    }

    @Override @Nullable protected Integer getMaxValue(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition)
    {
        return getMaxPurchasableShares(portfolioCompactDTO, quoteDTO, closeablePosition);
    }

    @Override protected boolean hasValidInfo()
    {
        return hasValidInfoForBuy();
    }

    @Override protected Subscription getTransactionSubscription(TransactionFormDTO transactionFormDTO)
    {
        final ProgressDialog progressDialog = ProgressDialog.show(
                getActivity(),
                getActivity().getString(R.string.processing),
                getActivity().getString(R.string.alert_dialog_please_wait),
                true);

        return AppObservable.bindSupportFragment(
                this,
                securityServiceWrapper.doTransactionRx(requisite.securityId, transactionFormDTO, IS_BUY))
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(new DismissDialogAction0(progressDialog))
                .doOnUnsubscribe(new DismissDialogAction0(progressDialog))
                .subscribe(new BuySellObserver(requisite.securityId, transactionFormDTO, IS_BUY));
    }

    @Nullable @Override public Double getPriceCcy(@Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable QuoteDTO quoteDTO)
    {
        if (quoteDTO == null)
        {
            return null;
        }

        return quoteDTO.getPriceRefCcy(portfolioCompactDTO, IS_BUY);
    }

    @NonNull @Override protected Predicate<PositionDTO> getCloseablePositionPredicate(
            @NonNull PositionDTOList positionDTOs,
            @NonNull final PortfolioId portfolioId)
    {
        return new Predicate<PositionDTO>()
        {
            @Override public boolean apply(PositionDTO positionDTO)
            {
                return positionDTO.portfolioId == portfolioId.key
                        && positionDTO.shares != null
                        && positionDTO.shares < 0;
            }
        };
    }

    @Override protected void initSecurityRelatedInfo(@Nullable SecurityCompactDTO securityCompactDTO)
    {
        setActionBarTitle(getString(R.string.transaction_title_buy,
                securityCompactDTO != null ? securityCompactDTO.getExchangeSymbol() : getString(R.string.fx)));
    }

    protected boolean hasValidInfoForBuy()
    {
        return usedDTO.securityCompactDTO != null
                && usedDTO.quoteDTO != null
                && usedDTO.quoteDTO.ask != null;
    }
}
