package com.tradehero.th.fragments.trade;

import com.tradehero.th.R;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.utils.THSignedNumber;

public class BuyDialogFragment extends AbstractTransactionDialogFragment
{
    private static final boolean IS_BUY = true;

    protected BuyDialogFragment()
    {
        super();
    }

    @Override protected String getLabel()
    {
        String display = securityCompactDTO == null ? "-" : securityCompactDTO.currencyDisplay;

        String sPrice;
        THSignedNumber sthSignedNumber;
        sthSignedNumber =
                new THSignedNumber(THSignedNumber.TYPE_MONEY, quoteDTO.bid, THSignedNumber.WITHOUT_SIGN, "");
        sPrice = sthSignedNumber.toString();
        return getString(R.string.buy_sell_button_sell, display, sPrice);
    }

    @Override protected int getCashLeftLabelResId()
    {
        return R.string.buy_sell_cash_left;
    }

    @Override public String getCashShareLeft()
    {
        String cashLeftText = getResources().getString(R.string.na);
        if (quoteDTO != null)
        {
            Double priceRefCcy = getPriceCcy();
            if (priceRefCcy != null && portfolioCompactDTO != null)
            {
                double value = mTransactionQuantity * priceRefCcy;

                double cashAvailable = portfolioCompactDTO.cashBalance;
                THSignedNumber thSignedNumber =
                        new THSignedNumber(THSignedNumber.TYPE_MONEY, cashAvailable - value,
                                THSignedNumber.WITHOUT_SIGN, portfolioCompactDTO.currencyDisplay);
                cashLeftText = thSignedNumber.toString();
            }
        }

        return cashLeftText;
    }

    @Override protected Integer getMaxValue()
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
        if (this.userProfileDTO != null && userProfileDTO.portfolio != null)
        {
            return portfolioCompactDTO.cashBalance;
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

    public Integer getMaxPurchasableShares()
    {
        return portfolioCompactDTOUtil.getMaxPurchasableShares(portfolioCompactDTO, quoteDTO);
    }

    protected boolean hasValidInfoForBuy()
    {
        return securityId != null
                && securityCompactDTO != null
                && quoteDTO != null
                && quoteDTO.ask != null;
    }
}
