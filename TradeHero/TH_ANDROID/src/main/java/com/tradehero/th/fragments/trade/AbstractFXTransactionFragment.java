package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;

public abstract class AbstractFXTransactionFragment extends AbstractTransactionFragment
{
    public static AbstractFXTransactionFragment newInstance(
            boolean isBuy,
            @NonNull Requisite requisite)
    {
        AbstractFXTransactionFragment abstractBuySellFragment = isBuy ? new BuyFXFragment() : new SellFXFragment();
        Bundle args = new Bundle();
        AbstractFXTransactionFragment.putRequisite(args, requisite);
        abstractBuySellFragment.setArguments(args);
        return abstractBuySellFragment;
    }

    protected AbstractFXTransactionFragment()
    {
        super();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
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
