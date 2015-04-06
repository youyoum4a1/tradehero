package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;

public abstract class AbstractStockTransactionDialogFragment extends AbstractTransactionDialogFragment
{
    public static AbstractStockTransactionDialogFragment newInstance(
            @NonNull SecurityId securityId,
            @NonNull PortfolioId portfolioId,
            @NonNull QuoteDTO quoteDTO,
            boolean isBuy)
    {
        AbstractStockTransactionDialogFragment abstractBuySellDialogFragment = isBuy ? new BuyStockDialogFragment() : new SellStockDialogFragment();
        Bundle args = new Bundle();
        AbstractStockTransactionDialogFragment.putSecurityId(args, securityId);
        AbstractStockTransactionDialogFragment.putPortfolioId(args, portfolioId);
        AbstractStockTransactionDialogFragment.putQuoteDTO(args, quoteDTO);
        abstractBuySellDialogFragment.setArguments(args);
        return abstractBuySellDialogFragment;
    }

    protected AbstractStockTransactionDialogFragment()
    {
        super();
    }

    @Override protected int getCashLeftLabelResId()
    {
        Boolean isClosing = isClosingPosition();
        return isClosing != null && isClosing
                ? R.string.buy_sell_share_left
                : R.string.buy_sell_cash_left;
    }

    @NonNull @Override protected THSignedNumber getFormattedPrice(double price)
    {
        return THSignedMoney
                .builder(price)
                .withOutSign()
                .currency(securityCompactDTO == null ? "-" : securityCompactDTO.currencyDisplay)
                .build();
    }

    public void displayQuickPriceButtonSet()
    {
        QuickPriceButtonSet buttonSetCopy = mQuickPriceButtonSet;
        if (buttonSetCopy != null)
        {
            buttonSetCopy.setEnabled(isQuickButtonEnabled());
            buttonSetCopy.setMaxPrice(getQuickButtonMaxValue());
        }
    }
}
