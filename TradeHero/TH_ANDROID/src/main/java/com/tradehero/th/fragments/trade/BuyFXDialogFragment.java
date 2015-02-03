package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.utils.metrics.events.SharingOptionsEvent;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.app.AppObservable;

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
        THSignedNumber bThSignedNumber = THSignedMoney
                .builder(quoteDTO.ask)
                .withOutSign()
                .relevantDigitCount(10)
                .currency("")
                .build();
        return getString(R.string.buy_sell_dialog_buy, bThSignedNumber.toString());
    }

    @Override @Nullable protected Double getProfitOrLossUsd()
    {
        if (positionDTOCompactList == null || portfolioCompactDTO == null)
        {
            return null;
        }
        Integer shareCount = positionDTOCompactList.getShareCountIn(portfolioCompactDTO.getPortfolioId());
        if (shareCount != null && shareCount >= 0)
        {
            return null;
        }

        Double total = positionDTOCompactList.getUnRealizedPLRefCcy(quoteDTO, portfolioCompactDTO);
        if (total == null || shareCount == null)
        {
            return null;
        }
        return (total * (double) mTransactionQuantity / Math.abs((double) shareCount));
    }

    @Override @Nullable protected Boolean isClosingPosition()
    {
        if (securityPositionDetailDTO == null)
        {
            // This means we have incomplete information
            return null;
        }
        return positionDTOCompact != null
                && positionDTOCompact.positionStatus != null
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
        return AppObservable.bindFragment(
                this,
                securityServiceWrapper.doTransactionRx(securityId, transactionFormDTO, IS_BUY))
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
