package com.tradehero.th.fragments.trade;

import com.tradehero.th.R;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.network.retrofit.MiddleCallback;
import org.jetbrains.annotations.Nullable;

public class SellDialogFragment extends AbstractTransactionDialogFragment
{
    private static final boolean IS_BUY = false;

    public SellDialogFragment()
    {
        super();
    }

    @Override protected String getLabel()
    {
        THSignedNumber sthSignedNumber = THSignedMoney
                .builder(quoteDTO.bid)
                .withOutSign()
                .currency(securityCompactDTO == null ? "-" : securityCompactDTO.getCurrencyDisplay())
                .build();
        return getString(R.string.buy_sell_dialog_sell, sthSignedNumber.toString());
    }

    @Override @Nullable protected Double getProfitOrLoss()
    {
        if (positionDTOCompactList == null || portfolioCompactDTO == null)
        {
            return null;
        }
        Double netProceeds = positionDTOCompactList.getNetSellProceedsUsd(
                mTransactionQuantity,
                quoteDTO,
                getPortfolioId(),
                true,
                portfolioCompactDTO.getProperTxnCostUsd());
        Double totalSpent = positionDTOCompactList.getSpentOnQuantity(mTransactionQuantity, getPortfolioId());
        if (netProceeds == null || totalSpent == null)
        {
            return null;
        }
        return netProceeds - totalSpent;
    }

    @Override protected int getCashLeftLabelResId()
    {
        return R.string.buy_sell_share_left;
    }

    @Override public String getCashShareLeft()
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

    @Override protected Integer getMaxValue()
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
        if (maxSellableShares != null)
        {
            // TODO see other currencies
            return maxSellableShares * quoteDTO.bid * quoteDTO.toUSDRate;
        }
        return 0;
    }

    @Override protected MiddleCallback<SecurityPositionDetailDTO> getTransactionMiddleCallback(TransactionFormDTO transactionFormDTO)
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

    public Integer getMaxSellableShares()
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
