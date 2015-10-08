package com.tradehero.th.fragments.trade;

import dagger.Module;

@Module(
        injects = {
                BuySellStockFragment.class,
                FXMainFragment.class,
                FXMainFragment.class,
                FXInfoFragment.class,
                BuyStockFragment.class,
                SellStockFragment.class,
                BuyFXFragment.class,
                SellFXFragment.class,
                StockActionBarRelativeLayout.class,
                StockDetailActionBarRelativeLayout.class,
                TradeListFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentTradeModule
{
}
