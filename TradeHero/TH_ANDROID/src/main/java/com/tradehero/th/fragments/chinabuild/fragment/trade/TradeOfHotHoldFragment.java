package com.tradehero.th.fragments.chinabuild.fragment.trade;

import com.tradehero.th.api.security.key.TrendingAllSecurityListType;

/*
    交易－热门持有
 */
public class TradeOfHotHoldFragment extends TradeOfTypeBaseFragment
{

    public int getTradeType()
    {
        return TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_HOLD;
    }

}
