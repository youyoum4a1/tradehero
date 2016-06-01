package com.ayondo.academy.fragments.trade;

import com.ayondo.academy.fragments.trade.view.TradeListItemView;
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
                TradeListItemView.class,
        },
        library = true,
        complete = false
)
public class FragmentTradeModule
{
}
