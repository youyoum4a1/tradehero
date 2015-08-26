package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;

public abstract class AbstractStockTransactionFragment extends AbstractTransactionFragment
{
    public static AbstractStockTransactionFragment newInstance(
            boolean isBuy,
            @NonNull Requisite requisite)
    {
        AbstractStockTransactionFragment abstractBuySellDialogFragment = isBuy ? new BuyStockFragment() : new SellStockFragment();
        Bundle args = new Bundle();
        AbstractStockTransactionFragment.putRequisite(args, requisite);
        abstractBuySellDialogFragment.setArguments(args);
        return abstractBuySellDialogFragment;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected AbstractStockTransactionFragment()
    {
        super();
    }

    @Override protected int getCashLeftLabelResId(@Nullable PositionDTOCompact closeablePosition)
    {
        return closeablePosition != null
                ? R.string.buy_sell_share_left
                : R.string.buy_sell_cash_left;
    }

    @NonNull @Override protected THSignedNumber getFormattedPrice(double price)
    {
        return THSignedMoney
                .builder(price)
                .withOutSign()
                //.currency(usedDTO.securityCompactDTO == null ? "-" : usedDTO.securityCompactDTO.currencyDisplay)
                .build();
    }

    public void displayQuickPriceButtonSet(@NonNull PortfolioCompactDTO portfolioCompactDTO, @NonNull QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition)
    {
        //QuickPriceButtonSet buttonSetCopy = mQuickPriceButtonSet;
        //if (buttonSetCopy != null)
        //{
        //    buttonSetCopy.setEnabled(isQuickButtonEnabled());
        //    buttonSetCopy.setMaxPrice(getQuickButtonMaxValue(portfolioCompactDTO, quoteDTO, closeablePosition));
        //}
    }
}
