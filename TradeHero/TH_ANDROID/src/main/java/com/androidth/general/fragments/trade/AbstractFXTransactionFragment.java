package com.androidth.general.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import com.androidth.general.models.number.THSignedMoney;
import com.androidth.general.models.number.THSignedNumber;

public abstract class AbstractFXTransactionFragment extends AbstractBuySellPopupDialogFragment
{
    public static AbstractFXTransactionFragment newInstance(
            boolean isBuy,
            @NonNull Requisite requisite)
    {
        AbstractFXTransactionFragment abstractBuySellDialogFragment = isBuy ? new BuyFXFragment() : new SellFXFragment();
        Bundle args = new Bundle();
        AbstractFXTransactionFragment.putRequisite(args, requisite);
        abstractBuySellDialogFragment.setArguments(args);
        return abstractBuySellDialogFragment;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    protected AbstractFXTransactionFragment()
    {
        super();
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
}
