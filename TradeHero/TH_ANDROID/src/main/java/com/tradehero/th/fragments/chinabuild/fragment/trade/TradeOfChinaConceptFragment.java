package com.tradehero.th.fragments.chinabuild.fragment.trade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th2.R;
import com.tradehero.th.api.security.key.TrendingAllSecurityListType;

public class TradeOfChinaConceptFragment extends TradeOfTypeBaseFragment
{
    public int getTradeType()
    {
        return TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_CHINA_CONCEPT;
    }

    public String getStrExchangeName()
    {
        return "ChinaConcept";
    }

    @Override
    public View getRootView(LayoutInflater inflater, ViewGroup container)
    {
        return inflater.inflate(R.layout.trade_of_china_concept, container, false);
    }
}
