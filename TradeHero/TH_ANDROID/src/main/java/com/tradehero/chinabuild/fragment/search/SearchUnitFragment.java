package com.tradehero.chinabuild.fragment.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.chinabuild.cache.CompetitionListType;
import com.tradehero.chinabuild.cache.CompetitionListTypeRecommand;
import com.tradehero.chinabuild.cache.CompetitionListTypeSearch;
import com.tradehero.chinabuild.cache.CompetitionNewCache;
import com.tradehero.chinabuild.data.CompetitionDataItem;
import com.tradehero.chinabuild.data.CompetitionInterface;
import com.tradehero.chinabuild.data.UserCompetitionDTOList;
import com.tradehero.chinabuild.fragment.competition.CompetitionDetailFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionMainFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionSecuritySearchFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionUtils;
import com.tradehero.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.chinabuild.saveload.SearchResultSave;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.adapters.CompetitionListAdapter;
import com.tradehero.th.adapters.SearchUserListAdapter;
import com.tradehero.th.adapters.SecuritySearchListAdapter;
import com.tradehero.th.api.analytics.BatchAnalyticsEventForm;
import com.tradehero.th.api.analytics.SearchSecurityEventForm;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.SearchHotSecurityListType;
import com.tradehero.th.api.security.key.SearchSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.api.users.UserSearchResultDTOList;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.persistence.user.UserBaseKeyListCache;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.widget.TradeHeroProgressBar;
import com.viewpagerindicator.SquarePageIndicator;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

/*
   整合搜索
 */
public class SearchUnitFragment extends DashboardFragment
{
    public static final String BUNDLE_DEFAULT_TAB_PAGE = "bundle_default_tab_page";
    public static final String BUNDLE_GO_TO_BUY_SELL_DIRECTLY = "BUNDLE_GO_TO_BUY_SELL_DIRECTLY";
    private boolean isBuySellDirectly = false;
    private String opt_type = SecurityOptActivity.TYPE_BUY;

    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject CurrentUserId currentUserId;
    @Inject UserServiceWrapper userServiceWrapper;
    public DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> securityListTypeCacheListener;
    public DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> securityListTypeHotCacheListener;

    @Inject Lazy<CompetitionNewCache> competitionNewCacheLazy;
    private DTOCacheNew.Listener<CompetitionListType, UserCompetitionDTOList> competitionListCacheListenerSearch;

    @Inject Lazy<UserBaseKeyListCache> userBaseKeyListCache;
    private DTOCacheNew.Listener<UserListType, UserSearchResultDTOList> userCacheListenerSearch;

    @Inject LeaderboardCache leaderboardCache;
    protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> leaderboardCacheListener;

    public static final int TAB_SEARCH_STOCK = 0;
    public static final int TAB_SEARCH_COMPETITION = 1;
    public static final int TAB_SEARCH_USER = 2;

    @InjectView(R.id.tvSearch) TextView tvSearch;
    @InjectView(R.id.edtSearchInput) EditText tvSearchInput;

    private SearchHotSecurityListType keyHot;
    private SearchSecurityListType keySearch;
    boolean isUserSearch = false;
    private String searchStr;
    private String searchCancelStr;

    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) SquarePageIndicator indicator;
    private List<View> views = new ArrayList<>();

    public int tabSelect = 0;
    @InjectView(R.id.tvSearchTabStock) TextView tvSearchTabStock;
    @InjectView(R.id.tvSearchTabCompetition) TextView tvSearchTabCompetition;
    @InjectView(R.id.tvSearchTabUser) TextView tvSearchTabUser;
    @InjectView(R.id.viewLine0) View viewLine0;
    @InjectView(R.id.viewLine1) View viewLine1;
    @InjectView(R.id.viewLine2) View viewLine2;
    @InjectView(R.id.viewLine00) View viewLine00;
    @InjectView(R.id.viewLine11) View viewLine11;
    @InjectView(R.id.viewLine22) View viewLine22;
    private TradeHeroProgressBar progressBar0;
    private TradeHeroProgressBar progressBar1;
    private TradeHeroProgressBar progressBar2;

    public SecurityListView listStock;
    public SecurityListView listCompetition;
    public SecurityListView listUser;
    public SecuritySearchListAdapter adapterStock;
    public CompetitionListAdapter adapterCompetition;
    public SearchUserListAdapter adapterUser;

    boolean isFirstLunch;

    private int pageSecurity = 1;
    private int pageUser = 3;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        isFirstLunch = true;
        securityListTypeCacheListener = new TrendingSecurityListFetchListener();
        securityListTypeHotCacheListener = new TrendingSecurityListFetchListener();

        competitionListCacheListenerSearch = new CompetitionListCacheListener();
        userCacheListenerSearch = new UserBaseKeyListCacheListener();
        leaderboardCacheListener = new BaseLeaderboardFragmentLeaderboardCacheListener();

        adapterStock = new SecuritySearchListAdapter(getActivity());
        adapterCompetition = new CompetitionListAdapter(getActivity(), CompetitionUtils.COMPETITION_PAGE_SEARCH);
        adapterUser = new SearchUserListAdapter(getActivity());

        if(getArguments()!=null){
            if(getArguments().containsKey(BUNDLE_GO_TO_BUY_SELL_DIRECTLY)) {
                isBuySellDirectly = getArguments().getBoolean(BUNDLE_GO_TO_BUY_SELL_DIRECTLY, false);
            }
            if(getArguments().containsKey(SecurityOptActivity.BUNDLE_FROM_TYPE)) {
                opt_type = getArguments().getString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_BUY);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        hideActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.search_unite_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        initView();
        initTabPageView();
        return view;
    }

    public void initView() {
        searchStr = getString(R.string.search_search);
        searchCancelStr = getString(R.string.search_cancel);

        tvSearchInput.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                loadHistorySearchData();
            }
        });

        tvSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputStr = editable.toString();
                if (TextUtils.isEmpty(inputStr)) {
                    tvSearch.setText(searchCancelStr);
                    loadRecommandData();
                } else {
                    tvSearch.setText(searchStr);
                }
            }
        });

        tvSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, android.view.KeyEvent keyEvent)
            {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        controlLoading(tabSelect, true);
                        fetchUnite(tabSelect, true);
                        break;
                    case EditorInfo.IME_ACTION_DONE:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setOnclickListener();
    }

    @Override
    public void onDestroyView() {
        detachSecurityHotListCache();
        detachSecurityListCache();
        detachSearchCompetition();
        detachLeaderboardCacheListener();
        ButterKnife.reset(this);
        closeInputMethod();
        super.onDestroyView();
    }

    protected class TrendingSecurityListFetchListener implements DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList>
    {
        @Override
        public void onDTOReceived(@NotNull SecurityListType key, @NotNull SecurityCompactDTOList value)
        {
            if (getActivity() == null)
            {
                return;
            }
            if (key instanceof SearchSecurityListType)
            {
                initAdapterSecurity(value, key);
                pageSecurity ++ ;
            }
            else if(key instanceof SearchHotSecurityListType)
            {
                initAdapterSecurity(value, key);
            }
            onFinish();
        }

        @Override
        public void onErrorThrown(@NotNull SecurityListType key, @NotNull Throwable error)
        {
            if (getActivity() == null)
            {
                return;
            }
            onFinish();
        }

        private void onFinish()
        {
            listStock.onRefreshComplete();
            controlLoading(TAB_SEARCH_STOCK, false);
        }
    }

    private void initAdapterSecurity(@NotNull SecurityCompactDTOList value, @NotNull SecurityListType key)
    {
        if (key instanceof SearchSecurityListType)
        {
            isUserSearch = true;
        }

        if (key.page == 1)
        {
            adapterStock.setSecurityList(value);
        }
        else
        {
            adapterStock.addItems(value);
        }

        if (value != null && value.size() > 0)
        {
            key.page += 1;
        }

        adapterStock.notifyDataSetChanged();
    }

    private void detachSecurityListCache()
    {
        if (securityListTypeCacheListener != null)
        {
            securityCompactListCache.get().unregister(securityListTypeCacheListener);
        }
    }

    private void detachSecurityHotListCache()
    {
        if (securityListTypeHotCacheListener != null)
        {
            securityCompactListCache.get().unregister(securityListTypeHotCacheListener);
        }
    }

    private void fetchUnite(int index, boolean force)
    {
        if (index == TAB_SEARCH_STOCK)
        {
            pageSecurity = 1;
            fetchSecuritySearchList(force);
        }
        else if (index == TAB_SEARCH_COMPETITION)
        {

            fetchSearchCompetition(force);
        }
        else if (index == TAB_SEARCH_USER)
        {
            pageUser = 1;
            fetchSearchUser(force);
        }
    }

    private void fetchSecuritySearchList(boolean force)
    {
        if (StringUtils.isNullOrEmptyOrSpaces(getSearchString()))
        {
            listStock.onRefreshComplete();
            return;
        }
        detachSecurityListCache();
        keySearch = new SearchSecurityListType(getSearchString(), pageSecurity, 50);
        securityCompactListCache.get().register(keySearch, securityListTypeCacheListener);
        securityCompactListCache.get().getOrFetchAsync(keySearch, force);
    }

    private void fetchHotSecuritySearchList(boolean force)
    {
        detachSecurityHotListCache();
        keyHot = new SearchHotSecurityListType(1, 50);
        securityCompactListCache.get().register(keyHot, securityListTypeHotCacheListener);
        securityCompactListCache.get().getOrFetchAsync(keyHot, force);
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

    @OnClick({R.id.tvSearchTabStock, R.id.tvSearchTabCompetition, R.id.tvSearchTabUser, R.id.tvSearch})
    public void onSearchTabClicked(View view)
    {
        int id = view.getId();
        switch (id)
        {
            case R.id.tvSearchTabStock:
                pager.setCurrentItem(TAB_SEARCH_STOCK);
                break;
            case R.id.tvSearchTabCompetition:
                pager.setCurrentItem(TAB_SEARCH_COMPETITION);
                break;
            case R.id.tvSearchTabUser:
                pager.setCurrentItem(TAB_SEARCH_USER);
                break;
            case R.id.tvSearch:
                if (TextUtils.isEmpty(getSearchString()))
                {
                    popCurrentFragment();
                    return;
                }
                if (!StringUtils.isNullOrEmptyOrSpaces(getSearchString()))
                {
                    controlLoading(tabSelect, true);
                    fetchUnite(tabSelect, true);
                }
                break;
        }
    }

    @OnClick(R.id.btn_search_x)
    public void onClearClicked()
    {
        tvSearchInput.setText("");
    }

    private void sendAnalytics(final SecurityCompactDTO dto)
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    SearchSecurityEventForm analyticsEventForm = new SearchSecurityEventForm("search",
                            DateUtils.getFormattedUtcDateFromDate(getActivity().getResources(),
                                    new Date(System.currentTimeMillis())), dto.id,
                            currentUserId.toUserBaseKey().getUserId());
                    BatchAnalyticsEventForm batchAnalyticsEventForm = new BatchAnalyticsEventForm();
                    batchAnalyticsEventForm.events = new ArrayList<>();
                    batchAnalyticsEventForm.events.add(analyticsEventForm);
                    userServiceWrapper.sendAnalytics(batchAnalyticsEventForm);
                } catch (Exception e)
                {
                    THToast.show(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void setOnclickListener()
    {
        listStock.setOnItemClickListener(securityItemClickListner);
        listUser.setOnItemClickListener(userItemClickListner);
        listCompetition.setOnItemClickListener(competitionItemClickListner);
    }

    public void initTabPageView()
    {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        views = new ArrayList<>();
        View viewTab0 = layoutInflater.inflate(R.layout.search_unite_listview_layout, null);
        initRootViewTab0(viewTab0);
        views.add(viewTab0);

        View viewTab1 = layoutInflater.inflate(R.layout.search_unite_listview_layout, null);
        initRootViewTab1(viewTab1);
        views.add(viewTab1);

        View viewTab2 = layoutInflater.inflate(R.layout.search_unite_listview_layout, null);
        initRootViewTab2(viewTab2);
        views.add(viewTab2);

        pager.setOffscreenPageLimit(5);
        pager.setAdapter(pageAdapter);
        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override public void onPageScrolled(int i, float v, int i2)
            {

            }

            @Override public void onPageSelected(int i)
            {
                setSelectTabView(i);
            }

            @Override public void onPageScrollStateChanged(int i)
            {

            }
        });
        loadRecommandData();

        if (isFirstLunch && getArguments() != null)
        {
            int index = getArguments().getInt(BUNDLE_DEFAULT_TAB_PAGE, 0);
            pager.setCurrentItem(tabSelect = index);
            isFirstLunch = false;
        }
    }

    public void loadRecommandData()
    {
        if (adapterStock != null && adapterUser.getCount() == 0)
        {
            fetchHotSecuritySearchList(true);
        }

        if (adapterUser != null && adapterUser.getCount() == 0)
        {
            fetchRecommandCompetition(false);
        }

        if (adapterCompetition != null && adapterCompetition.getCount() == 0)
        {
            fetchRecommandUser(false);
        }
    }

    public void loadHistorySearchData()
    {
        if (tabSelect == TAB_SEARCH_STOCK)
        {
            ArrayList<SecurityCompactDTO> securies = SearchResultSave.loadSearchSecurity(getActivity());
            if (securies != null)
            {
                adapterStock.setSecurityList(securies);
            }
        }
        else if (tabSelect == TAB_SEARCH_COMPETITION)
        {
            ArrayList<CompetitionDataItem> competitions = SearchResultSave.loadSearchCompetitions(getActivity());
            if (competitions != null)
            {
                adapterCompetition.setUserCompetitionDataList(competitions);
            }
        }
        else if (tabSelect == TAB_SEARCH_USER)
        {
            ArrayList<UserSearchResultDTO> users = SearchResultSave.loadSearchUsers(getActivity());
            if (users != null)
            {
                adapterUser.setListData(users);
            }
        }
    }

    public void controlLoading(int index, boolean isShow)
    {
        TradeHeroProgressBar progressBar = null;

        if (index == TAB_SEARCH_STOCK)
        {
            progressBar = progressBar0;
        }
        else if (index == TAB_SEARCH_COMPETITION)
        {
            progressBar = progressBar1;
        }
        else if (index == TAB_SEARCH_USER)
        {
            progressBar = progressBar2;
        }

        if (progressBar != null)
        {
            if (isShow)
            {
                progressBar.startLoading();
                progressBar.setVisibility(View.VISIBLE);
            }
            else
            {
                progressBar.stopLoading();
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    public AdapterView.OnItemClickListener securityItemClickListner =
            new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int id, long position)
                {
                    SecurityCompactDTO dto = (SecurityCompactDTO) adapterStock.getItem((int) position);
                    if (dto != null)
                    {
                        if(isBuySellDirectly) {
                            getActivity().finish();
                            Bundle bundle = new Bundle();
                            bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, opt_type);
                            bundle.putString(SecurityOptActivity.KEY_SECURITY_EXCHANGE, dto.getSecurityId().getExchange());
                            bundle.putString(SecurityOptActivity.KEY_SECURITY_SYMBOL, dto.getSecurityId().getSecuritySymbol());
                            bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, dto.name);
                            Intent intent = new Intent(getActivity(), SecurityOptActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, dto.getSecurityId().getArgs());
                            bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, dto.name);
                            pushFragment(SecurityDetailFragment.class, bundle);
                            if (isUserSearch) {
                                SearchResultSave.saveSearchSecurity(getActivity(), dto);
                                sendAnalytics(dto);
                            }
                        }
                    }
                }
            };

    public AdapterView.OnItemClickListener competitionItemClickListner =
            new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int id, long position)
                {
                    CompetitionInterface item = adapterCompetition.getItem((int) position);
                    if (item instanceof CompetitionDataItem)
                    {
                        SearchResultSave.saveSearchCompetitons(getActivity(), (CompetitionDataItem) item);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_DTO, ((CompetitionDataItem) item).userCompetitionDTO);
                        pushFragment(CompetitionMainFragment.class, bundle);
                    }
                }
            };

    public AdapterView.OnItemClickListener userItemClickListner =
            new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int id, long position)
                {
                    UserSearchResultDTO item = adapterUser.getItem((int) position);
                    if (item != null)
                    {
                        SearchResultSave.saveSearchUsers(getActivity(), item);
                        Bundle bundle = new Bundle();
                        bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, item.userId);
                        pushFragment(UserMainPage.class, bundle);
                    }
                }
            };

    public void initRootViewTab0(View view)
    {
        listStock = (SecurityListView) view.findViewById(R.id.listSearch);
        TextView tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);
        progressBar0 = (TradeHeroProgressBar) view.findViewById(R.id.progressbar_trade_security_search);
        listStock.setEmptyView(tvEmpty);
        listStock.setAdapter(adapterStock);
        listStock.setMode(PullToRefreshBase.Mode.BOTH);
        listStock.setOnItemClickListener(securityItemClickListner);

        listStock.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (!StringUtils.isNullOrEmpty(getSearchString()))
                {
                    fetchUnite(tabSelect, true);
                }
                else
                {
                    listStock.onRefreshComplete();
                    fetchHotSecuritySearchList(true);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                fetchSecuritySearchList(true);
            }
        });
    }

    public void initRootViewTab1(View view)
    {
        listCompetition = (SecurityListView) view.findViewById(R.id.listSearch);
        TextView tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);
        progressBar1 = (TradeHeroProgressBar) view.findViewById(R.id.progressbar_trade_security_search);
        listCompetition.setEmptyView(tvEmpty);
        listCompetition.setAdapter(adapterCompetition);
        listCompetition.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        listCompetition.setOnItemClickListener(competitionItemClickListner);

        listCompetition.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (!StringUtils.isNullOrEmpty(getSearchString()))
                {
                    fetchUnite(tabSelect, true);
                }
                else
                {
                    listCompetition.onRefreshComplete();
                    fetchRecommandCompetition(false);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                fetchSecuritySearchList(true);
            }
        });
    }

    public void initRootViewTab2(View view)
    {
        listUser = (SecurityListView) view.findViewById(R.id.listSearch);
        TextView tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);
        progressBar2 = (TradeHeroProgressBar) view.findViewById(R.id.progressbar_trade_security_search);
        listUser.setEmptyView(tvEmpty);
        listUser.setAdapter(adapterUser);
        listUser.setMode(PullToRefreshBase.Mode.BOTH);

        listUser.setOnItemClickListener(userItemClickListner);

        listUser.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (!StringUtils.isNullOrEmpty(getSearchString()))
                {
                    fetchUnite(tabSelect, true);
                }
                else
                {
                    listUser.onRefreshComplete();
                    fetchRecommandUser(false);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                fetchSearchUser(true);
            }
        });
    }

    public PagerAdapter pageAdapter = new PagerAdapter()
    {
        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }

        @Override
        public int getCount()
        {
            return (views == null) ? 0 : views.size();
        }
    };

    public static final String[] hintArray = {"股票代码／拼音／简称", "请输入比赛名称", "请输入用户姓名"};

    public void setSelectTabView(int index)
    {
        tabSelect = index;
        tvSearchTabStock.setTextColor(
                index == TAB_SEARCH_STOCK ? getResources().getColor(R.color.tradehero_blue) : getResources().getColor(R.color.black2));
        tvSearchTabCompetition.setTextColor(
                index == TAB_SEARCH_COMPETITION ? getResources().getColor(R.color.tradehero_blue) : getResources().getColor(R.color.black2));
        tvSearchTabUser.setTextColor(
                index == TAB_SEARCH_USER ? getResources().getColor(R.color.tradehero_blue) : getResources().getColor(R.color.black2));
        viewLine0.setBackgroundColor(
                index == TAB_SEARCH_STOCK ? getResources().getColor(R.color.tradehero_blue) : getResources().getColor(R.color.gray_normal));
        viewLine1.setBackgroundColor(
                index == TAB_SEARCH_COMPETITION ? getResources().getColor(R.color.tradehero_blue) : getResources().getColor(R.color.gray_normal));
        viewLine2.setBackgroundColor(
                index == TAB_SEARCH_USER ? getResources().getColor(R.color.tradehero_blue) : getResources().getColor(R.color.gray_normal));

        viewLine00.setVisibility(index == TAB_SEARCH_STOCK ? View.VISIBLE : View.INVISIBLE);
        viewLine11.setVisibility(index == TAB_SEARCH_COMPETITION ? View.VISIBLE : View.INVISIBLE);
        viewLine22.setVisibility(index == TAB_SEARCH_USER ? View.VISIBLE : View.INVISIBLE);

        tvSearchInput.setHint(hintArray[index]);
    }

    protected class CompetitionListCacheListener implements DTOCacheNew.Listener<CompetitionListType, UserCompetitionDTOList>
    {
        @Override public void onDTOReceived(@NotNull CompetitionListType key, @NotNull UserCompetitionDTOList value)
        {
            if (key instanceof CompetitionListType)
            {
                initSearchCompetition(value);
            }
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull CompetitionListType key, @NotNull Throwable error)
        {
            onFinish();
        }

        private void onFinish()
        {
            listCompetition.onRefreshComplete();
            controlLoading(TAB_SEARCH_COMPETITION, false);
        }
    }

    //搜索出来的比赛
    private void initSearchCompetition(UserCompetitionDTOList userCompetitionDTOs)
    {
        if (adapterCompetition != null)
        {
            adapterCompetition.setSearchCompetitionDtoList(userCompetitionDTOs);
        }
    }

    protected void detachSearchCompetition()
    {
        competitionNewCacheLazy.get().unregister(competitionListCacheListenerSearch);
    }

    private void fetchSearchCompetition(boolean refresh)
    {
        detachSearchCompetition();
        CompetitionListTypeSearch searchKey = new CompetitionListTypeSearch(getSearchString());
        competitionNewCacheLazy.get().register(searchKey, competitionListCacheListenerSearch);
        competitionNewCacheLazy.get().getOrFetchAsync(searchKey, refresh);
    }

    private void fetchRecommandCompetition(boolean refresh)
    {
        detachSearchCompetition();
        CompetitionListTypeRecommand searchKey = new CompetitionListTypeRecommand();
        competitionNewCacheLazy.get().register(searchKey, competitionListCacheListenerSearch);
        competitionNewCacheLazy.get().getOrFetchAsync(searchKey, refresh);
    }

    protected void detachLeaderboardCacheListener()
    {
        leaderboardCache.unregister(leaderboardCacheListener);
    }

    private void fetchRecommandUser(boolean force)
    {
        detachLeaderboardCacheListener();

        PagedLeaderboardKey key = new PagedLeaderboardKey(LeaderboardDefKeyKnowledge.SEARCH_RECOMMEND, PagedLeaderboardKey.FIRST_PAGE);
        key.perPage = 50;
        key.page = 1;
        leaderboardCache.register(key, leaderboardCacheListener);
        leaderboardCache.getOrFetchAsync(key, force);
    }

    protected void detachSearchUser()
    {
        userBaseKeyListCache.get().unregister(userCacheListenerSearch);
    }

    private void fetchSearchUser(boolean force)
    {
        detachSearchUser();
        SearchUserListType searchKey = new SearchUserListType(getSearchString(), pageUser, 50);
        userBaseKeyListCache.get().register(searchKey, userCacheListenerSearch);
        userBaseKeyListCache.get().getOrFetchAsync(searchKey, force);
    }

    protected class UserBaseKeyListCacheListener implements DTOCacheNew.Listener<UserListType, UserSearchResultDTOList>
    {
        public void onDTOReceived(@NotNull UserListType key, @NotNull UserSearchResultDTOList value)
        {
            if (key instanceof SearchUserListType)
            {
                if (((SearchUserListType) key).getPage() == 1)
                {
                    if (adapterUser != null)
                    {
                        adapterUser.setListData(value);
                    }
                }
                else
                {
                    if (adapterUser != null)
                    {
                        adapterUser.addListData(value);
                    }
                }

                pageUser++;
            }
            onFinish();
        }

        public void onErrorThrown(@NotNull UserListType key, @NotNull Throwable error)
        {
            onFinish();
        }

        public void onFinish()
        {
            listUser.onRefreshComplete();
            controlLoading(TAB_SEARCH_USER, false);
        }
    }

    protected class BaseLeaderboardFragmentLeaderboardCacheListener implements DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO>
    {
        @Override public void onDTOReceived(@NotNull LeaderboardKey key, @NotNull LeaderboardDTO value)
        {
            UserSearchResultDTOList userSearchResultDTOs = new UserSearchResultDTOList();
            LeaderboardUserDTOList list = value.users;
            if (list != null && list.size() > 0)
            {
                for (int i = 0; i < list.size(); i++)
                {
                    userSearchResultDTOs.add(new UserSearchResultDTO(list.get(i)));
                }
            }
            if (adapterUser != null)
            {
                adapterUser.setListData(userSearchResultDTOs);
            }
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull LeaderboardKey key, @NotNull Throwable error)
        {
            onFinish();
        }

        public void onFinish()
        {
        }
    }
}
