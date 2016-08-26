package com.androidth.general.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;

import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.models.number.THSignedNumber;

public abstract class AbstractStockTransactionFragment extends AbstractBuySellPopupDialogFragment
{
    public static AbstractStockTransactionFragment newInstance(
            boolean isBuy,
            @NonNull Requisite requisite, String topBarColor)
    {
        AbstractStockTransactionFragment abstractBuySellFragment = isBuy ? new BuyStockFragment() : new SellStockFragment();
        Bundle args = new Bundle();
        AbstractStockTransactionFragment.putRequisite(args, requisite);
        args.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR, topBarColor);
        abstractBuySellFragment.setArguments(args);
        return abstractBuySellFragment;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected AbstractStockTransactionFragment()
    {
        super();
    }

    @NonNull @Override protected THSignedNumber getFormattedPrice(double price)
    {
        return THSignedNumber
                .builder(price)
                .withOutSign()
                .build();
    }
}
