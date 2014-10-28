package com.tradehero.th.fragments.chinabuild.fragment.moreLeaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
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
import com.tradehero.th.fragments.chinabuild.data.THSharePreferenceManager;
import com.tradehero.th.fragments.chinabuild.fragment.ShareDialogFragment;
import com.tradehero.th.fragments.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

import javax.inject.Inject;

public class StockGodListBaseFragment extends DashboardFragment
{

    public static final String BUNLDE_LEADERBOARD_KEY = "bundle_leaderboard_key";

    @Inject LeaderboardCache leaderboardCache;
    protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> leaderboardCacheListener;

    @InjectView(R.id.listBang) SecurityListView listBang;
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.imgEmpty) ImageView imgEmpty;

    @Inject Analytics analytics;

    @Inject CurrentUserId currentUserId;

    private LeaderboardListAdapter adapter;

    private int currentPage = 0;
    private int ITEMS_PER_PAGE = 50;

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
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.progress);
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
                if(leaderboard_key == LeaderboardDefKeyKnowledge.WEALTH)
                {
                    enterPortfolio(dto);
                }
                else if(leaderboard_key == LeaderboardDefKeyKnowledge.MOST_SKILLED_ID
                        ||leaderboard_key == LeaderboardDefKeyKnowledge.DAYS_90
                        ||leaderboard_key == LeaderboardDefKeyKnowledge.MONTHS_6)
                {
                    enterPortfolio2(dto);
                }
                else if(leaderboard_key == LeaderboardDefKeyKnowledge.MOST_SKILLED_ID
                        ||leaderboard_key == LeaderboardDefKeyKnowledge.DAYS_90
                        ||leaderboard_key == LeaderboardDefKeyKnowledge.MONTHS_6)
                {
                    enterPortfolio2(dto);
                }
                else
                {
                    enterPortfolio(dto);
                }
            }
        });
    }
    /*
进入个人主页
*/
    private void enterMainPage(LeaderboardUserDTO userDTO)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userDTO.id);
        gotoDashboard(UserMainPage.class, bundle);
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

    /*
进入持仓页面2 打开二级页面
*/
    private void enterPortfolio2(LeaderboardUserDTO userDTO)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(PortfolioFragment.BUNLDE_SHOW_PROFILE_USER_ID, userDTO.id);
        pushFragment(PortfolioFragment.class, bundle);
    }

    @Override public void onPause()
    {
        super.onPause();
        if(listBang!=null)
        {
            listBang.onRefreshComplete();
        }
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
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
        @Override public void onDTOReceived(@NotNull LeaderboardKey key, @NotNull LeaderboardDTO value)
        {
            setListData(key, value.users);
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull LeaderboardKey key, @NotNull Throwable error)
        {
            Timber.e("Failed to leaderboard", error);
            onFinish();
        }

        public void onFinish()
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listBang);
            listBang.onRefreshComplete();
        }
    }

    private void setListData(LeaderboardKey key, LeaderboardUserDTOList listData)
    {
        if (((PagedLeaderboardKey) key).page == PagedLeaderboardKey.FIRST_PAGE)
        {
            currentPage = 0;
            adapter.setListData(listData);
            adapter.setLeaderboardType(getLeaderboardDTO().key);

        }
        else
        {
            adapter.addItems(listData);
        }


        //如果返回数据已经为空了，说明没有了下一页。
        if (listData.size() > 0)
        {
            currentPage += 1;
        }
        adapter.notifyDataSetChanged();
    }

    private void showLoginContinuousDialog(){
        if(THSharePreferenceManager.Login_Continuous_Time >=3){
            if (leaderboard_key == LeaderboardDefKeyKnowledge.DAYS_ROI) {
                int userId = currentUserId.toUserBaseKey().getUserId();
                if (THSharePreferenceManager.isShareDialogLoginContinually(userId, getActivity())) {
                    ShareDialogFragment.showDialog(getActivity().getSupportFragmentManager(),
                            getString(R.string.login_continuous), getString(R.string.login_continuous), THSharePreferenceManager.PROPERTY_MORE_THAN_TWENTY_FIVE, userId);
                    THSharePreferenceManager.isLoginContinuallyShowed = true;
                }
            }
        }
    }
}
