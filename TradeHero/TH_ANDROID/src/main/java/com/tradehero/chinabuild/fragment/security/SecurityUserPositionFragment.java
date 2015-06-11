package com.tradehero.chinabuild.fragment.security;

import android.content.Intent;
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
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.th.R;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.base.Application;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.widget.TradeHeroProgressBar;
import com.tradehero.th.wxapi.WXEntryActivity;

import java.util.ArrayList;

/**
 * Created by palmer on 15/6/9.
 */
public class SecurityUserPositionFragment extends DashboardFragment {

    private int upColor;
    private int normalColor;
    private int downColor;
    private TradeHeroProgressBar tradeHeroProgressBar;
    private PullToRefreshListView positionsLV;
    private ImageView emptyIV;

    private SecurityPostionAdapter adapter;
    private ArrayList<SecurityUserPositionDTO> opts = new ArrayList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        upColor = getResources().getColor(R.color.bar_up);
        normalColor = getResources().getColor(R.color.bar_normal);
        downColor = getResources().getColor(R.color.bar_down);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("一个股票(666666)");
        setHeadViewMiddleSub("999 +10.10% +10.10");
        setHeadViewMiddleSubTextColor(upColor);
        setHeadViewRight0(R.drawable.search);
        setHeadViewRight1(R.drawable.share);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_position, container, false);
        tradeHeroProgressBar = (TradeHeroProgressBar)view.findViewById(R.id.tradeheroprogressbar_position);
        emptyIV = (ImageView)view.findViewById(R.id.imgEmpty);
        positionsLV = (PullToRefreshListView)view.findViewById(R.id.listPositions);
        if(adapter==null){
            adapter = new SecurityPostionAdapter(getActivity(), opts);
        }
        positionsLV.setMode(PullToRefreshBase.Mode.BOTH);
        positionsLV.setAdapter(adapter);
        positionsLV.getRefreshableView().setEmptyView(emptyIV);
        positionsLV.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                onFinish();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                onFinish();
            }

            private void onFinish() {
                positionsLV.onRefreshComplete();
            }
        });
        setOpts();
        return view;
    }

    private void setOpts(){
        opts.clear();
        for(int num=0;num<20;num++){
            SecurityUserPositionDTO dto = new SecurityUserPositionDTO();
            opts.add(dto);
        }
        adapter.setData(opts);
    }

    private void enterWechatSharePage(){
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.title = "一个股票涨涨涨";
        weChatDTO.type = WeChatMessageType.Trade;
        Intent gotoShareToWeChatIntent = new Intent(getActivity(), WXEntryActivity.class);
        gotoShareToWeChatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        WXEntryActivity.putWeChatDTO(gotoShareToWeChatIntent, weChatDTO);
        Application.context().startActivity(gotoShareToWeChatIntent);
    }

    private void enterSearchPage(){
        pushFragment(SearchUnitFragment.class, new Bundle());
    }

    @Override
    public void onClickHeadRight0() {
        enterSearchPage();
    }

    @Override
    public void onClickHeadRight1() {
        enterWechatSharePage();
    }

}