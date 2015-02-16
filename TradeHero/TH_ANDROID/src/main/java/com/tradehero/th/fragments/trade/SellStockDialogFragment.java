package com.tradehero.th.fragments.trade;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.metrics.events.SharingOptionsEvent;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.app.AppObservable;

public class SellStockDialogFragment extends AbstractStockTransactionDialogFragment
{
    private static final boolean IS_BUY = false;

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    public SellStockDialogFragment()
    {
        super();
    }

    @Override protected void setBuyEventFor(SharingOptionsEvent.Builder builder)
    {
        builder.setBuyEvent(IS_BUY);
    }

    @Override protected String getLabel()
    {
        if (quoteDTO.bid == null)
        {
            return getString(R.string.na);
        }
        THSignedNumber sthSignedNumber = THSignedMoney
                .builder(quoteDTO.bid)
                .withOutSign()
                .currency(securityCompactDTO == null ? "-" : securityCompactDTO.currencyDisplay)
                .build();
        return getString(R.string.buy_sell_dialog_sell, sthSignedNumber.toString());
    }

    @Override @Nullable protected Double getProfitOrLossUsd()
    {
        if (positionDTOCompactList == null || portfolioCompactDTO == null)
        {
            return null;
        }
        Double netProceedsUsd = positionDTOCompactList.getNetSellProceedsUsd(
                mTransactionQuantity,
                quoteDTO,
                getPortfolioId(),
                true,
                portfolioCompactDTO.getProperTxnCostUsd());
        Double totalSpentUsd = positionDTOCompactList.getSpentOnQuantityUsd(mTransactionQuantity, portfolioCompactDTO);
        if (netProceedsUsd == null || totalSpentUsd == null)
        {
            return null;
        }
        return netProceedsUsd - totalSpentUsd;
    }

    @Override @Nullable protected Boolean isClosingPosition()
    {
        if (positionDTOCompact == null)
        {
            // This means we have incomplete information
            return null;
        }
        return positionDTOCompact.positionStatus != null
                && positionDTOCompact.positionStatus.equals(PositionStatus.LONG);
    }

    @Override @NonNull public String getCashShareLeft()
    {
        return getRemainingWhenSell();
    }

    @Override @Nullable protected Integer getMaxValue()
    {
        return getMaxSellableShares();
    }

    @Override protected boolean hasValidInfo()
    {
        return hasValidInfoForSell();
    }

    @Override protected boolean isQuickButtonEnabled()
    {
        return quoteDTO != null && quoteDTO.bid != null && quoteDTO.toUSDRate != null;
    }

    @Override protected double getQuickButtonMaxValue()
    {
        Integer maxSellableShares = getMaxSellableShares();
        if (maxSellableShares != null && quoteDTO != null && quoteDTO.bid != null)
        {
            // TODO see other currencies
            return maxSellableShares * quoteDTO.bid * quoteDTO.toUSDRate;
        }
        return 0;
    }

    @Override protected Subscription getTransactionSubscription(TransactionFormDTO transactionFormDTO)
    {
        final ProgressDialog progressDialog = ProgressDialog.show(
                getActivity(),
                getActivity().getString(R.string.processing),
                getActivity().getString(R.string.alert_dialog_please_wait),
                true);

        return AppObservable.bindFragment(
                this,
                securityServiceWrapper.doTransactionRx(securityId, transactionFormDTO, IS_BUY))
                .finallyDo(new DismissDialogAction0(progressDialog))
                .subscribe(new BuySellObserver(IS_BUY));
    }

    @Override public Double getPriceCcy()
    {
        if (quoteDTO == null)
        {
            return null;
        }

        return quoteDTO.getPriceRefCcy(portfolioCompactDTO, IS_BUY);
    }

    protected boolean hasValidInfoForSell()
    {
        return securityId != null
                && securityCompactDTO != null
                && quoteDTO != null
                && quoteDTO.bid != null;
    }
}
