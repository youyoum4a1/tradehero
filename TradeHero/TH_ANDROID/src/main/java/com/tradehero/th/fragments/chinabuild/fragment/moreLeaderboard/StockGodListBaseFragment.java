package com.tradehero.th.fragments.chinabuild.fragment.moreLeaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.fragments.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th2.R;
import com.tradehero.th.adapters.LeaderboardListAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class StockGodListBaseFragment extends DashboardFragment
{

    public static final String BUNLDE_LEADERBOARD_KEY = "bundle_leaderboard_key";

    @Inject LeaderboardCache leaderboardCache;
    protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> leaderboardCacheListener;

    @InjectView(R.id.listBang) SecurityListView listBang;
    private LeaderboardListAdapter adapter;

    private int currentPage = 0;
    private int ITEMS_PER_PAGE = 50;

    private int leaderboard_key = 0;//所有榜单根据key来判断 30day，60day，6months 。。。

    //public StockGodListBaseFragment(int leaderboard_key)
    //{
    //    this.leaderboard_key = leaderboard_key;
    //}




    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
        {
            leaderboard_key = args.getInt(BUNLDE_LEADERBOARD_KEY);
        }
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
        return view;
    }

    @Override public void onStop()
    {
        detachLeaderboardCacheListener();

        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private void initView()
    {
        listBang.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
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
                LeaderboardUserDTO dto = (LeaderboardUserDTO)adapter.getItem((int)position);
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
        bundle.putInt(PortfolioFragment.BUNLDE_SHOW_PROFILE_USER_ID,userDTO.id);
        gotoDashboard(PortfolioFragment.class, bundle);

    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
        this.leaderboardCacheListener = createLeaderboardCacheListener();
        fetchLeaderboard();
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
        leaderboardCache.register(key, leaderboardCacheListener);
        leaderboardCache.getOrFetchAsync(key);
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
            //linkWith(value, true);
            //Timber.d("value:" + value);
            setListData(key, value.users);
        }

        @Override public void onErrorThrown(@NotNull LeaderboardKey key, @NotNull Throwable error)
        {
            Timber.e("Failed to leaderboard", error);
            //THToast.show(R.string.error_fetch_leaderboard_info);
        }
    }

    private void setListData(LeaderboardKey key, LeaderboardUserDTOList listData)
    {
        if (((PagedLeaderboardKey) key).page == PagedLeaderboardKey.FIRST_PAGE)
        {
            currentPage = 0;
            adapter = new LeaderboardListAdapter(getActivity(), listData, getLeaderboardDTO().key);
            listBang.setAdapter(adapter);
        }
        else
        {
            if (adapter != null)
            {
                adapter.addItems(listData);
            }
        }
        listBang.onRefreshComplete();

        //如果返回数据已经为空了，说明没有了下一页。
        if (listData.size() > 0)
        {
            currentPage += 1;
        }
        else
        {

        }
        adapter.notifyDataSetChanged();
    }
}
