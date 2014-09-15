package com.tradehero.th.fragments.chinabuild.fragment.competition;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.adapters.SecurityListAdapter;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingAllSecurityListType;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th2.R;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-9-9. 搜索比赛专属股票列表页面
 */
public class CompetitionSecuritySearchFragment extends DashboardFragment
{

    public static final String BUNLDE_COMPETITION_ID = "bundle_competition_id";
    private int competitionId;

    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;
    public DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> securityListTypeCacheListener;
    private ProgressDialog mTransactionDialog;
    @Inject ProgressDialogUtil progressDialogUtil;

    @InjectView(R.id.tvSearch) TextView tvSearch;
    @InjectView(R.id.edtSearchInput) TextView tvSearchInput;
    @InjectView(R.id.btn_search_x) Button btnSearch_x;
    @InjectView(R.id.listSearch) SecurityListView listSearch;

    private int currentPage = 0;
    private int ITEMS_PER_PAGE = 20;
    private SecurityListAdapter adapterSecurity;

    private int trendingAllSecurityListType = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getCompetitionId();
        securityListTypeCacheListener = createSecurityListFetchListener();
    }

    public void getCompetitionId()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            competitionId = bundle.getInt(BUNLDE_COMPETITION_ID, 0);
        }
        //THToast.show("competitionId = " + competitionId);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //super.onCreateOptionsMenu(menu, inflater);
        //setHeadViewMiddleMain("搜索");
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.competition_search_layout, container, false);
        ButterKnife.inject(this, view);
        //if (adapterSecurity == null)
        //{
        //mTransactionDialog = progressDialogUtil.show(CompetitionSecuritySearchFragment.this.getActivity(),
        //        R.string.processing, R.string.alert_dialog_please_wait);
        currentPage = 0;
        fetchSecurityList();
        initListView();
        //}

        return view;
    }

    private void detachSecurityListCache()
    {
        if (securityListTypeCacheListener != null)
        {
            securityCompactListCache.get().unregister(securityListTypeCacheListener);
        }
    }

    private void fetchSecurityList()
    {
        detachSecurityListCache();
        setTradeType(TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_COMPETITION);
        SecurityListType key = new TrendingAllSecurityListType(getTradeType(), competitionId, currentPage + 1, ITEMS_PER_PAGE);
        securityCompactListCache.get().register(key, securityListTypeCacheListener);
        securityCompactListCache.get().getOrFetchAsync(key, true);
    }

    private void fetchSecuritySearchList()
    {
        if (StringUtils.isNullOrEmptyOrSpaces(getSearchString())) return;
        detachSecurityListCache();
        setTradeType(TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_SEARCH);
        SecurityListType key = new TrendingAllSecurityListType(getTradeType(), competitionId, getSearchString(), currentPage + 1, ITEMS_PER_PAGE);
        securityCompactListCache.get().register(key, securityListTypeCacheListener);
        securityCompactListCache.get().getOrFetchAsync(key, true);
    }

    @OnClick(R.id.btn_search_x)
    public void onSearchXClicked()
    {
        Timber.d("onSearchXClicked!");
        if (tvSearchInput != null)
        {
            tvSearchInput.setText("");
        }
    }

    @OnClick(R.id.tvSearch)
    public void onSearchClicked()
    {
        toSearchSecurity();
    }

    public String getSearchString()
    {
        String strSearch = tvSearchInput.getText().toString();
        if (strSearch != null && strSearch.length() > 0)
        {
            return strSearch;
        }
        else
        {
            return "";
        }
    }

    public void toSearchSecurity()
    {
        String strSearch = tvSearchInput.getText().toString();
        if (strSearch != null && strSearch.length() > 0)
        {
            clearPageCount();
            fetchSecuritySearchList();
        }
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        detachSecurityListCache();

        //ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    private void clearPageCount()
    {
        currentPage = 0;
    }

    private void initListView()
    {
        currentPage = 0;
        listSearch.setMode(PullToRefreshBase.Mode.BOTH);

        adapterSecurity = new SecurityListAdapter(getActivity(), getTradeType());
        listSearch.setAdapter(adapterSecurity);

        listSearch.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("下拉刷新");
                clearPageCount();
                if (getTradeType() == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_COMPETITION)
                {
                    fetchSecurityList();
                }
                else if (getTradeType() == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_SEARCH)
                {
                    fetchSecuritySearchList();
                }
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("上拉加载更多");
                if (getTradeType() == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_COMPETITION)
                {
                    fetchSecurityList();
                }
                else if (getTradeType() == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_SEARCH)
                {
                    fetchSecuritySearchList();
                }
            }
        });
    }

    protected DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> createSecurityListFetchListener()
    {
        return new TrendingSecurityListFetchListener();
    }

    protected class TrendingSecurityListFetchListener implements DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList>
    {
        @Override public void onDTOReceived(@NotNull SecurityListType key, @NotNull SecurityCompactDTOList value)
        {
            Timber.d("value");
            initAdapterSecurity(value, key);
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull SecurityListType key, @NotNull Throwable error)
        {
            //THToast.show(getString(R.string.error_fetch_exchange_list));
            Timber.e("Error fetching the list of security %s", key, error);
            onFinish();
        }

        private void onFinish()
        {
            listSearch.onRefreshComplete();
            if (mTransactionDialog != null)
            {
                mTransactionDialog.dismiss();
            }

        }
    }

    public void setTradeType(int tradeType)
    {
        trendingAllSecurityListType = tradeType;
    }

    public int getTradeType()
    {
        return trendingAllSecurityListType;
    }

    //</editor-fold>
    private void initAdapterSecurity(SecurityCompactDTOList list, SecurityListType key)
    {
        if (key.getPage() == PagedLeaderboardKey.FIRST_PAGE)
        {
            currentPage = 0;
            //adapterSecurity = new SecurityListAdapter(getActivity(), list, getTradeType());
            adapterSecurity.setSecurityList(list);
            listSearch.setAdapter(adapterSecurity);
            listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int id, long position)
                {
                    SecurityCompactDTO dto = (SecurityCompactDTO) adapterSecurity.getItem((int) position);
                    if (dto != null)
                    {
                        Timber.d("list item clicked %s", dto.name);
                        enterSecurity(dto.getSecurityId(), dto.name);
                    }
                }
            });
        }
        else
        {
            adapterSecurity.addItems(list);
            adapterSecurity.notifyDataSetChanged();
        }

        if (list.size() > 0)
        {
            currentPage += 1;
        }
        else
        {

        }
    }

    //进入比赛相关的股票详情，带入competitionID
    public void enterSecurity(SecurityId securityId, String securityName)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
        bundle.putInt(SecurityDetailFragment.BUNDLE_KEY_COMPETITION_ID_BUNDLE,competitionId);
        //gotoDashboard(SecurityDetailFragment.class.getName(), bundle);
        pushFragment(SecurityDetailFragment.class, bundle);
    }
}
