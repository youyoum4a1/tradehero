package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;

public abstract class AbstractFXTransactionDialogFragment extends AbstractTransactionFragment
{
    public static AbstractFXTransactionDialogFragment newInstance(
            boolean isBuy,
            @NonNull Requisite requisite)
    {
        AbstractFXTransactionDialogFragment abstractBuySellDialogFragment = isBuy ? new BuyFXDialogFragment() : new SellFXDialogFragment();
        Bundle args = new Bundle();
        AbstractFXTransactionDialogFragment.putRequisite(args, requisite);
        abstractBuySellDialogFragment.setArguments(args);
        return abstractBuySellDialogFragment;
    }

    protected AbstractFXTransactionDialogFragment()
    {
        super();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //mQuickPriceButtonSet.setPercent(true);
    }

    @Override protected int getCashLeftLabelResId(@Nullable PositionDTOCompact closeablePosition)
    {
        return closeablePosition != null
                ? R.string.buy_sell_fx_quantity_left
                : R.string.buy_sell_fx_cash_left;
    }

    @NonNull @Override protected THSignedNumber getFormattedPrice(double price)
    {
        return THSignedMoney
                .builder(price)
                .withOutSign()
                .relevantDigitCount(10)
                .currency("")
                .build();
    }

    public void displayQuickPriceButtonSet(@NonNull PortfolioCompactDTO portfolioCompactDTO, @NonNull QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition)
    {
        //QuickPriceButtonSet buttonSetCopy = mQuickPriceButtonSet;
        //if (buttonSetCopy != null)
        //{
        //    buttonSetCopy.setEnabled(isQuickButtonEnabled());
        //}
    }
}
