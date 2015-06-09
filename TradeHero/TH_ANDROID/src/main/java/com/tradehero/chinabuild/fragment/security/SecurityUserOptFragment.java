package com.tradehero.chinabuild.fragment.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.widget.TradeHeroProgressBar;

/**
 * Created by palmer on 15/6/9.
 */
public class SecurityUserOptFragment extends DashboardFragment{

    private int upColor;
    private int normalColor;
    private int downColor;
    private TradeHeroProgressBar tradeHeroProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        upColor = getResources().getColor(R.color.security_detail_bar_up);
        normalColor = getResources().getColor(R.color.security_detail_bar_normal);
        downColor = getResources().getColor(R.color.security_detail_bar_down);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("一个股票(666666)");
        setHeadViewMiddleSub("999 +10.10% +10.10");
        setHeadViewMiddleSubTextColor(upColor);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_opts, container, false);
        tradeHeroProgressBar = (TradeHeroProgressBar)view.findViewById(R.id.tradeheroprogressbar_opt);
        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
        setHeadViewMiddleSubTextColor(normalColor);
    }

}
