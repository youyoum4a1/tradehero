package com.tradehero.chinabuild.fragment.trade;

import com.tradehero.th.api.security.key.TrendingAllSecurityListType;

/*
    交易－涨幅榜单
 */
public class TradeOfRisePercentFragment extends TradeOfTypeBaseFragment
{

    public int getTradeType()
    {
        return TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_RISE_PERCENT;
    }

}
