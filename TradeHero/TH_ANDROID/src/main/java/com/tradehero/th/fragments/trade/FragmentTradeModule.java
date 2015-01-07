package com.tradehero.th.fragments.trade;

import com.tradehero.th.fragments.trade.view.TradeListItemView;
import dagger.Module;

@Module(
        injects = {
                BuySellStockFragment.class,
                BuySellFXFragment.class,
                BuyStockDialogFragment.class,
                SellStockDialogFragment.class,
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
