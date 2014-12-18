package com.tradehero.th.fragments.trade;

import com.tradehero.th.fragments.trade.view.TradeListItemView;
import dagger.Module;

@Module(
        injects = {
                BuySellFragment.class,
                AbstractBuySellFXFragment.class,
                BuySellFXFragment.class,
                AbstractTransactionDialogFragment.class,
                BuyDialogFragment.class,
                SellDialogFragment.class,
                AbstractFXTransactionDialogFragment.class,
                BuyFXDialogFragment.class,
                SellFXDialogFragment.class,
                TradeListFragment.class,
                TradeListItemView.class,
        },
        library = true,
        complete = false
)
public class FragmentTradeModule
{
}
