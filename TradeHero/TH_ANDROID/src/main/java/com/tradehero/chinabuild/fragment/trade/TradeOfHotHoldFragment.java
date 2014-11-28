package com.tradehero.chinabuild.fragment.trade;

import com.tradehero.th.api.security.key.TrendingAllSecurityListType;

/*
    交易－热门持有
 */
public class TradeOfHotHoldFragment extends TradeOfTypeBaseFragment
{
    @Override
    public void onResume(){
        super.onResume();
        showGuideView();
    }

    public int getTradeType()
    {
        return TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_HOLD;
    }

}
