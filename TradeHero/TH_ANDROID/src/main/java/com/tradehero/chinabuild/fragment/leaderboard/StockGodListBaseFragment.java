package com.tradehero.chinabuild.fragment.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.chinabuild.fragment.ShareDialogFragment;
import com.tradehero.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LeaderboardListAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.widget.TradeHeroProgressBar;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class StockGodListBaseFragment extends DashboardFragment
{

    public static final String BUNLDE_LEADERBOARD_KEY = "bundle_leaderboard_key";

    @Inject LeaderboardCache leaderboardCache;
    protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> leaderboardCacheListener;

    @InjectView(R.id.listBang) SecurityListView listBang;
    @InjectView(R.id.tradeheroprogressbar_heros) TradeHeroProgressBar progressBar;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.imgEmpty) ImageView imgEmpty;

    @Inject Analytics analytics;

    @Inject CurrentUserId currentUserId;

    private LeaderboardListAdapter adapter;

    private int currentPage = 0;
    private int ITEMS_PER_PAGE = 20;

    private int leaderboard_key = 0;//所有榜单根据key来判断 30day，60day，6months 。。。

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new LeaderboardListAdapter(getActivity());
        Bundle args = getArguments();
        if (args != null)
        {
            leaderboard_key = args.getInt(BUNLDE_LEADERBOARD_KEY);
        }
        leaderboardCacheListener = createLeaderboardCacheListener();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        setHeadViewMiddleMain(LeaderboardDefKeyKnowledge.getLeaderboardName(getLeaderboardDTO()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.stock_god_list, container, false);
        ButterKnife.inject(this, view);


        initView();

        if (adapter.getCount() == 0)
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.tradeheroprogressbar_heros);
            progressBar.startLoading();
        }
        else
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listBang);
        }
        showLoginContinuousDialog();
        return view;
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        detachLeaderboardCacheListener();
        //ButterKnife.reset(this);
        super.onDestroyView();
    }

    private void initView()
    {
        listBang.setAdapter(adapter);
        listBang.setMode(PullToRefreshBase.Mode.BOTH);
        listBang.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                fetchLeaderboard();
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                fetchLeaderboardMore();
            }
        });

        listBang.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long position)
            {
                analytics.addEvent(new MethodEvent(AnalyticsConstants.LEADERBOARD_USER_CLICKED_POSITION, ""+position));
                LeaderboardUserDTO dto = (LeaderboardUserDTO) adapter.getItem((int) position);
                enterPortfolio(dto);
            }
        });
    }


    /*
进入持仓页面
 */
    private void enterPortfolio(LeaderboardUserDTO userDTO)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(PortfolioFragment.BUNLDE_SHOW_PROFILE_USER_ID, userDTO.id);
        gotoDashboard(PortfolioFragment.class, bundle);
    }

    @Override public void onPause()
    {
        super.onPause();
        if(listBang!=null)
        {
            listBang.onRefreshComplete();
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        if (adapter != null && adapter.getCount() == 0)
        {
            fetchLeaderboard();
        }
    }

    public LeaderboardDefKey getLeaderboardDTO()
    {
        return new LeaderboardDefKey(leaderboard_key);
    }

    protected void fetchLeaderboard()
    {
        detachLeaderboardCacheListener();
        PagedLeaderboardKey key = new PagedLeaderboardKey(getLeaderboardDTO().key, PagedLeaderboardKey.FIRST_PAGE);
        key.perPage = ITEMS_PER_PAGE;
        key.page = 1;
        leaderboardCache.register(key, leaderboardCacheListener);
        leaderboardCache.getOrFetchAsync(key,true);
    }

    protected void fetchLeaderboardMore()
    {
        detachLeaderboardCacheListener();
        PagedLeaderboardKey key = new PagedLeaderboardKey(getLeaderboardDTO().key, currentPage + 1);
        key.perPage = ITEMS_PER_PAGE;
        leaderboardCache.register(key, leaderboardCacheListener);
        leaderboardCache.getOrFetchAsync(key);
    }

    protected void detachLeaderboardCacheListener()
    {
        leaderboardCache.unregister(leaderboardCacheListener);
    }

    protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> createLeaderboardCacheListener()
    {
        return new BaseLeaderboardFragmentLeaderboardCacheListener();
    }

    protected class BaseLeaderboardFragmentLeaderboardCacheListener implements DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO>
    {
        @Override public void onDTOReceived(@NotNull LeaderboardKey key, @NotNull LeaderboardDTO value) {
            setListData(key, value.users);
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull LeaderboardKey key, @NotNull Throwable error) {
            onFinish();
        }

        public void onFinish() {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listBang);
            listBang.onRefreshComplete();
            progressBar.stopLoading();
        }
    }

    private void setListData(LeaderboardKey key, LeaderboardUserDTOList listData) {
        if (((PagedLeaderboardKey) key).page == PagedLeaderboardKey.FIRST_PAGE) {
            currentPage = 0;
            adapter.setListData(listData);
            adapter.setLeaderboardType(getLeaderboardDTO().key);

        } else {
            adapter.addItems(listData);
        }


        //如果返回数据已经为空了，说明没有了下一页。
        if (listData.size() > 0) {
            currentPage += 1;
        }
        adapter.notifyDataSetChanged();
    }

    private void showLoginContinuousDialog(){
        int loginTimes = THSharePreferenceManager.Login_Continuous_Time;
        if(loginTimes >=3){
            if (leaderboard_key == LeaderboardDefKeyKnowledge.DAYS_ROI) {
                int userId = currentUserId.toUserBaseKey().getUserId();
                if (THSharePreferenceManager.isShareDialogLoginContinually(userId, getActivity())) {
                    ShareDialogFragment.showDialog(getActivity().getSupportFragmentManager(),
                            getString(R.string.login_continuous, String.valueOf(loginTimes)), getString(R.string.login_continuous_share, String.valueOf(loginTimes)),
                            THSharePreferenceManager.LOGIN_CONTINUALLY, userId);
                    THSharePreferenceManager.isLoginContinuallyShowed = true;
                }
            }
        }
    }
}
