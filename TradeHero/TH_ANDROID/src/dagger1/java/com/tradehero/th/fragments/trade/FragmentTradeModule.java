package com.tradehero.th.fragments.trade;

import com.tradehero.th.fragments.trade.view.TradeListItemView;
import dagger.Module;

@Module(
        injects = {
                BuySellStockFragment.class,
                FXMainFragment.class,
                FXMainFragment.class,
                FXInfoFragment.class,
                BuyStockFragment.class,
                SellStockFragment.class,
                BuyFXDialogFragment.class,
                SellFXDialogFragment.class,
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
