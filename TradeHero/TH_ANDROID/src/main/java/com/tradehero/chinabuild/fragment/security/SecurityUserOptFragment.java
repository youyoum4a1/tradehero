package com.tradehero.chinabuild.fragment.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.widget.TradeHeroProgressBar;

import java.util.ArrayList;

/**
 * Created by palmer on 15/6/9.
 */
public class SecurityUserOptFragment extends DashboardFragment{

    private int upColor;
    private int normalColor;
    private int downColor;
    private TradeHeroProgressBar tradeHeroProgressBar;
    private PullToRefreshListView optsLV;
    private ImageView emptyIV;

    private SecurityDetailOptAdapter adapter;
    private ArrayList<SecurityDetailOptDTO> opts = new ArrayList();

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
        emptyIV = (ImageView)view.findViewById(R.id.imgEmpty);
        optsLV = (PullToRefreshListView)view.findViewById(R.id.listOpts);
        if(adapter==null){
            adapter = new SecurityDetailOptAdapter(getActivity(), opts);
        }
        optsLV.setMode(PullToRefreshBase.Mode.BOTH);
        optsLV.setAdapter(adapter);
        optsLV.getRefreshableView().setEmptyView(emptyIV);
        optsLV.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                onFinish();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                onFinish();
            }

            private void onFinish() {
                optsLV.onRefreshComplete();
            }
        });
        setOpts();
        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
        setHeadViewMiddleSubTextColor(normalColor);
    }

    private void setOpts(){
        opts.clear();
        for(int num=0;num<20;num++){
            SecurityDetailOptDTO dto = new SecurityDetailOptDTO();
            opts.add(dto);
        }
        adapter.setData(opts);
    }


}
