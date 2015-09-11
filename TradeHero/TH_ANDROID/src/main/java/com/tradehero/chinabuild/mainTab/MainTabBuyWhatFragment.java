package com.tradehero.chinabuild.mainTab;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.tradehero.chinabuild.buyWhat.FollowBuyFragment;
import com.tradehero.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.chinabuild.fragment.leaderboard.StockGodListBaseFragment;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.th.R;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;

public class MainTabBuyWhatFragment extends AbsBaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView mQueryBtn;
    private ImageView mNewSuggestBtn;
    private ImageView mFollowChanceBtn;
    private ImageView mHotStockBtn;
    private ImageView mWinRateBtn;
    private ListView mListView;
    private MainTabBuyWhatAdapter mListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_tab_fragment_stockgod_new_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mQueryBtn = (ImageView) view.findViewById(R.id.query_btn);
        mQueryBtn.setOnClickListener(this);
        mNewSuggestBtn = (ImageView) view.findViewById(R.id.new_suggest_icon);
        mNewSuggestBtn.setOnClickListener(this);
        mFollowChanceBtn = (ImageView) view.findViewById(R.id.follow_chance_icon);
        mFollowChanceBtn.setOnClickListener(this);
        mHotStockBtn = (ImageView) view.findViewById(R.id.hot_stock_icon);
        mHotStockBtn.setOnClickListener(this);
        mWinRateBtn = (ImageView) view.findViewById(R.id.win_rate_icon);
        mWinRateBtn.setOnClickListener(this);

        mListView = (ListView) view.findViewById(R.id.list);
        if (mListViewAdapter == null) {
            mListViewAdapter = new MainTabBuyWhatAdapter(getActivity());
        }
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query_btn:
                gotoDashboard(SearchUnitFragment.class.getName(), new Bundle());
                break;
            case R.id.new_suggest_icon:

                break;
            case R.id.follow_chance_icon:
                gotoDashboard(FollowBuyFragment.class.getName());
                break;
            case R.id.hot_stock_icon:
                Bundle args = new Bundle();
                args.putInt(StockGodListBaseFragment.BUNLDE_LEADERBOARD_KEY, LeaderboardDefKeyKnowledge.POPULAR);
                gotoDashboard(StockGodListBaseFragment.class.getName(), args);
                break;
            case R.id.win_rate_icon:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        goToMockTrade();
    }

    private void goToMockTrade(){
        Bundle bundle = new Bundle();
        bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_BUY);
        Intent intent = new Intent(getActivity(), SecurityOptActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }
}
