package com.tradehero.th.fragments.trade;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.utils.metrics.events.SharingOptionsEvent;

import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BuyDialogFragment extends AbstractTransactionDialogFragment
{
    private static final boolean IS_BUY = true;

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    public BuyDialogFragment()
    {
        super();
    }

    @Override protected void setBuyEventFor(SharingOptionsEvent.Builder builder)
    {
        builder.setBuyEvent(IS_BUY);
    }

    @Override protected String getLabel()
    {
        THSignedNumber bThSignedNumber = THSignedMoney
                .builder(quoteDTO.ask)
                .withOutSign()
                .currency(securityCompactDTO == null ? "-" : securityCompactDTO.currencyDisplay)
                .build();
        return getString(R.string.buy_sell_dialog_buy, bThSignedNumber.toString());
    }

    @Override @Nullable protected Double getProfitOrLossUsd()
    {
        return null;
    }

    @Override protected int getCashLeftLabelResId()
    {
        return R.string.buy_sell_cash_left;
    }

    @Override @NonNull public String getCashShareLeft()
    {
        String cashLeftText = getResources().getString(R.string.na);
        if (quoteDTO != null)
        {
            Double priceRefCcy = getPriceCcy();
            if (priceRefCcy != null && portfolioCompactDTO != null)
            {
                double value = mTransactionQuantity * priceRefCcy;

                double cashAvailable = portfolioCompactDTO.cashBalance;
                THSignedNumber thSignedNumber = THSignedMoney
                        .builder(cashAvailable - value)
                        .withOutSign()
                        .currency(portfolioCompactDTO.currencyDisplay)
                        .build();
                cashLeftText = thSignedNumber.toString();
            }
        }

        return cashLeftText;
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
        if (quoteDTO == null || quoteDTO.ask == null)
        {
            return false;
        }
        return true;
    }

    @Override protected double getQuickButtonMaxValue()
    {
        if (portfolioCompactDTO != null)
        {
            return portfolioCompactDTO.cashBalance;
        }
        return 0;
    }

    @Override protected MiddleCallback<SecurityPositionTransactionDTO> getTransactionMiddleCallback(TransactionFormDTO transactionFormDTO)
    {
        return securityServiceWrapper.doTransaction(
                securityId, transactionFormDTO, IS_BUY,
                new BuySellCallback(IS_BUY));
    }

    @Override public Double getPriceCcy()
    {
        if (quoteDTO == null)
        {
            return null;
        }

        return quoteDTO.getPriceRefCcy(portfolioCompactDTO, IS_BUY);
    }

    @Nullable public Integer getMaxPurchasableShares()
    {
        return portfolioCompactDTOUtil.getMaxPurchasableShares(
                portfolioCompactDTO,
                quoteDTO);
    }

    protected boolean hasValidInfoForBuy()
    {
        return securityId != null
                && securityCompactDTO != null
                && quoteDTO != null
                && quoteDTO.ask != null;
    }
}
