package com.tradehero.chinabuild.buyWhat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.chinabuild.fragment.security.BasePurchaseManagerFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.chinabuild.mainTab.MainTabBuyWhatAdapter;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class FollowBuyFragment extends BasePurchaseManagerFragment implements AdapterView.OnItemClickListener {
    private SecurityListView mListView;
    private MainTabBuyWhatAdapter mListViewAdapter;
    private int currentPage = 0;
    private int ITEMS_PER_PAGE = 10;
    @Inject LeaderboardCache leaderboardCache;
    protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> leaderboardCacheListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.follow_buy_fragment_layout, container, false);
        initViews(view);
        leaderboardCacheListener = new BaseLeaderboardFragmentLeaderboardCacheListener();
        fetchLeaderboard();
        return view;
    }

    private void initViews(View view) {
        mListView = (SecurityListView) view.findViewById(R.id.list);
        if (mListViewAdapter == null) {
            mListViewAdapter = new MainTabBuyWhatAdapter(getActivity());
        }
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchLeaderboard();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchLeaderboardMore();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewRight0(R.drawable.search);
        setHeadViewMiddleMain(getString(R.string.tab_main_buy_what));
    }

    protected void fetchLeaderboard() {
        detachLeaderboardCacheListener();
        PagedLeaderboardKey key = new PagedLeaderboardKey(new LeaderboardDefKey(LeaderboardDefKeyKnowledge.BUY_WHAT).key, PagedLeaderboardKey.FIRST_PAGE);
        key.perPage = ITEMS_PER_PAGE;
        key.page = 1;
        leaderboardCache.register(key, leaderboardCacheListener);
        leaderboardCache.getOrFetchAsync(key, true);
    }

    protected void fetchLeaderboardMore() {
        detachLeaderboardCacheListener();
        PagedLeaderboardKey key = new PagedLeaderboardKey(new LeaderboardDefKey(LeaderboardDefKeyKnowledge.BUY_WHAT).key, currentPage + 1);
        key.perPage = ITEMS_PER_PAGE;
        leaderboardCache.register(key, leaderboardCacheListener);
        leaderboardCache.getOrFetchAsync(key, true);
    }

    protected void detachLeaderboardCacheListener() {
        leaderboardCache.unregister(leaderboardCacheListener);
    }

    @Override
    public void onClickHeadRight0() {
        pushFragment(SearchUnitFragment.class, new Bundle());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        goToMockTrade(mListViewAdapter.getItem(position - 1));
    }

    private void goToMockTrade(LeaderboardUserDTO dto){
        Bundle bundle = new Bundle();
        bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_BUY);
        bundle.putString(SecurityOptActivity.KEY_SECURITY_EXCHANGE, dto.exchange);
        bundle.putString(SecurityOptActivity.KEY_SECURITY_SYMBOL, dto.symbol);
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, dto.securityName);
        Intent intent = new Intent(getActivity(), SecurityOptActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    protected class BaseLeaderboardFragmentLeaderboardCacheListener implements DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> {
        @Override
        public void onDTOReceived(@NotNull LeaderboardKey key, @NotNull LeaderboardDTO value) {
            setListData(key, value.users);
            if (((PagedLeaderboardKey) key).page == PagedLeaderboardKey.FIRST_PAGE) {
                mListView.setMode(PullToRefreshBase.Mode.BOTH);
            }
            onFinish();
        }

        @Override
        public void onErrorThrown(@NotNull LeaderboardKey key, @NotNull Throwable error) {
            if (((PagedLeaderboardKey) key).page == PagedLeaderboardKey.FIRST_PAGE) {
                mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
            onFinish();
        }

        public void onFinish() {
            mListView.onRefreshComplete();
        }
    }

    private void setListData(LeaderboardKey key, LeaderboardUserDTOList listData) {
        if (((PagedLeaderboardKey) key).page == PagedLeaderboardKey.FIRST_PAGE) {
            currentPage = 0;
            Timber.d("lyl setListData size="+listData.size());
            Timber.d("lyl "+listData.get(0).toString());
            mListViewAdapter.setItems(listData);
        } else {
            mListViewAdapter.addItems(listData);
        }

        //如果返回数据已经为空了，说明没有了下一页。
        if (listData.size() > 0) {
            currentPage += 1;
        }
        mListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        detachLeaderboardCacheListener();
        super.onPause();
    }
}
