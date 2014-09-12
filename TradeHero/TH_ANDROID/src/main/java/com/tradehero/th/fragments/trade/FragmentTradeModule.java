package com.tradehero.th.fragments.trade;

import com.tradehero.th.fragments.trade.view.TradeListItemView;
import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                FreshQuoteHolder.class,
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
