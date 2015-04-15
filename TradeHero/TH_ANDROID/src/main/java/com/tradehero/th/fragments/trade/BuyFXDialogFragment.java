package com.tradehero.th.fragments.trade;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTOCompactUtil;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.metrics.events.SharingOptionsEvent;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;

public class BuyFXDialogFragment extends AbstractFXTransactionDialogFragment
{
    private static final boolean IS_BUY = true;

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    public BuyFXDialogFragment()
    {
        super();
    }

    @Override protected void setBuyEventFor(SharingOptionsEvent.Builder builder)
    {
        builder.setBuyEvent(IS_BUY);
    }

    @Override protected String getLabel()
    {
        if (quoteDTO.ask == null)
        {
            return getString(R.string.na);
        }
        THSignedNumber bThSignedNumber = getFormattedPrice(quoteDTO.ask);
        return getString(R.string.buy_sell_dialog_buy, bThSignedNumber.toString());
    }

    @Override @Nullable protected Double getProfitOrLossUsd()
    {
        if (positionDTOList == null || portfolioCompactDTO == null)
        {
            return null;
        }
        Integer shareCount = PositionDTOCompactUtil.getShareCountIn(positionDTOList, portfolioCompactDTO.getPortfolioId());
        if (shareCount != null && shareCount >= 0)
        {
            return null;
        }

        Double total = PositionDTOCompactUtil.getUnRealizedPLRefCcy(positionDTOList, quoteDTO, portfolioCompactDTO);
        if (total == null || shareCount == null)
        {
            return null;
        }
        return (total * (double) mTransactionQuantity / Math.abs((double) shareCount));
    }

    @Override @Nullable protected Boolean isClosingPosition()
    {
        if (positionDTOCompact == null)
        {
            return null;
        }
        return positionDTOCompact.positionStatus != null
                && positionDTOCompact.positionStatus.equals(PositionStatus.SHORT);
    }

    @Override @NonNull public String getCashShareLeft()
    {
        return getRemainingWhenBuy();
    }

    @Override @Nullable protected Integer getMaxValue()
    {
        return getMaxPurchasableShares();
    }

    @Override protected boolean hasValidInfo()
    {
        return hasValidInfoForBuy();
    }

    @Override protected boolean isQuickButtonEnabled()
    {
        return quoteDTO != null && quoteDTO.ask != null;
    }

    @Override protected double getQuickButtonMaxValue()
    {
        if (portfolioCompactDTO != null)
        {
            return portfolioCompactDTO.cashBalanceRefCcy;
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
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(new DismissDialogAction0(progressDialog))
                .doOnUnsubscribe(new DismissDialogAction0(progressDialog))
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

    protected boolean hasValidInfoForBuy()
    {
        return securityId != null
                && securityCompactDTO != null
                && quoteDTO != null
                && quoteDTO.ask != null;
    }
}
