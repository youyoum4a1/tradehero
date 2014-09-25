package com.tradehero.th.fragments.chinabuild.fragment.moreLeaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import timber.log.Timber;

public class StockGodListMoreFragment extends DashboardFragment
{
    @InjectView(R.id.rlMoreBangAll) RelativeLayout rlMoreBangAll;
    @InjectView(R.id.rlMoreBangSeason) RelativeLayout rlMoreBangSeason;
    @InjectView(R.id.rlMoreBang6Month) RelativeLayout rlMoreBang6Month;
    @InjectView(R.id.rlMoreBangExchange) RelativeLayout rlMoreBangExchange;
    @InjectView(R.id.rlMoreBangIndustry) RelativeLayout rlMoreBangIndustry;

    //private AdapterStockGod adapterStockGod;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.stock_god_list_more, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
        Timber.d("OnRusme: StockGodList More ");
    }

    private void initView()
    {

    }

    @OnClick({R.id.rlMoreBangAll, R.id.rlMoreBangSeason, R.id.rlMoreBang6Month, R.id.rlMoreBangExchange, R.id.rlMoreBangIndustry})
    public void onMoreBangClick(View view)
    {
        Bundle bundle = new Bundle();
        switch (view.getId())
        {
            case R.id.rlMoreBangIndustry://按行业类别
                gotoDashboard(LeaderboardFromIndustryFragment.class.getName());
                break;
            case R.id.rlMoreBangExchange://按交易所
                gotoDashboard(LeaderboardFromExchangeFragment.class.getName());
                break;
            case R.id.rlMoreBangAll://总盈利榜（英雄榜 MostSkill）
                bundle.putInt(StockGodListBaseFragment.BUNLDE_LEADERBOARD_KEY, LeaderboardDefKeyKnowledge.MOST_SKILLED_ID);
                gotoDashboard(StockGodListBaseFragment.class.getName(), bundle);

                break;
            case R.id.rlMoreBangSeason://季度榜
                bundle.putInt(StockGodListBaseFragment.BUNLDE_LEADERBOARD_KEY, LeaderboardDefKeyKnowledge.DAYS_90);
                gotoDashboard(StockGodListBaseFragment.class.getName(), bundle);

                break;
            case R.id.rlMoreBang6Month://半年榜
                bundle.putInt(StockGodListBaseFragment.BUNLDE_LEADERBOARD_KEY, LeaderboardDefKeyKnowledge.MONTHS_6);
                gotoDashboard(StockGodListBaseFragment.class.getName(), bundle);
                break;
        }
    }
}


