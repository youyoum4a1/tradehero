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
