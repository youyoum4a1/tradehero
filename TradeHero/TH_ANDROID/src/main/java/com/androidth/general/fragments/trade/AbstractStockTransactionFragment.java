package com.androidth.general.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;

import com.androidth.general.models.number.THSignedNumber;

public abstract class AbstractStockTransactionFragment extends AbstractTransactionFragment
{
    public static AbstractStockTransactionFragment newInstance(
            boolean isBuy,
            @NonNull Requisite requisite)
    {
        AbstractStockTransactionFragment abstractBuySellFragment = isBuy ? new BuyStockFragment() : new SellStockFragment();
        Bundle args = new Bundle();
        AbstractStockTransactionFragment.putRequisite(args, requisite);
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
