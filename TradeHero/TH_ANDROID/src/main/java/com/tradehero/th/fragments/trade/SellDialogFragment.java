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

public class SellDialogFragment extends AbstractTransactionDialogFragment
{
    private static final boolean IS_BUY = false;

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    public SellDialogFragment()
    {
        super();
    }

    @Override protected void setBuyEventFor(SharingOptionsEvent.Builder builder)
    {
        builder.setBuyEvent(IS_BUY);
    }

    @Override protected String getLabel()
    {
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

    @Override protected int getCashLeftLabelResId()
    {
        return R.string.buy_sell_share_left;
    }

    @Override @NonNull public String getCashShareLeft()
    {
        String shareLeftText = getResources().getString(R.string.na);
        if (quoteDTO != null)
        {
            Double priceRefCcy = getPriceCcy();
            if (priceRefCcy != null && portfolioCompactDTO != null)
            {
                Integer maxSellableShares = getMaxSellableShares();
                if (maxSellableShares != null && maxSellableShares != 0)
                {
                    shareLeftText = THSignedNumber.builder(maxSellableShares - mTransactionQuantity)
                            .relevantDigitCount(1)
                            .withOutSign()
                            .build().toString();
                }
            }
        }
        return shareLeftText;
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
        if ((quoteDTO == null || quoteDTO.bid == null || quoteDTO.toUSDRate == null))
        {
            return false;
        }
        return true;
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

    @Nullable public Integer getMaxSellableShares()
    {
        return positionDTOCompactList == null ? null :
                positionDTOCompactList.getMaxSellableShares(
                        this.quoteDTO,
                        this.portfolioCompactDTO);
    }

    protected boolean hasValidInfoForSell()
    {
        return securityId != null
                && securityCompactDTO != null
                && quoteDTO != null
                && quoteDTO.bid != null;
    }
}
