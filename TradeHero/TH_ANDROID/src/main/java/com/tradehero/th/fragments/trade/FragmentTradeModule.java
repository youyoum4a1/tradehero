package com.tradehero.th.fragments.trade;

import com.tradehero.th.fragments.trade.quote.FragmentTradeQuoteModule;
import com.tradehero.th.fragments.trade.view.TradeListItemView;
import dagger.Module;

@Module(
        includes = {
                FragmentTradeQuoteModule.class
        },
        injects = {
                BuySellFragment.class,
                AbstractTransactionDialogFragment.class,
                BuyDialogFragment.class,
                SellDialogFragment.class,
                TradeListFragment.class,
                TradeListItemView.class,
        },
        library = true,
        complete = false
)
public class FragmentTradeModule
{
}
