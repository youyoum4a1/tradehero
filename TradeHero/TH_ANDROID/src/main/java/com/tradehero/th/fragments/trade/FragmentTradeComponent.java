package com.tradehero.th.fragments.trade;

import com.tradehero.th.fragments.trade.view.TradeListItemView;
import dagger.Component;

@Component
public interface FragmentTradeComponent
{
    void injectBuySellFragment(BuySellFragment target);
    void injectAbstractTransactionDialogFragment(AbstractTransactionDialogFragment target);
    void injectBuyDialogFragment(BuyDialogFragment target);
    void injectSellDialogFragment(SellDialogFragment target);
    void injectTradeListFragment(TradeListFragment target);
    void injectTradeListItemView(TradeListItemView target);
}
