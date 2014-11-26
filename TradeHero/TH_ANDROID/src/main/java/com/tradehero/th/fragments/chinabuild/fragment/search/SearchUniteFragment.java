package com.tradehero.th.fragments.chinabuild.fragment.search;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.CompetitionListAdapter;
import com.tradehero.th.adapters.SearchUserListAdapter;
import com.tradehero.th.adapters.SecuritySearchListAdapter;
import com.tradehero.th.api.analytics.BatchAnalyticsEventForm;
import com.tradehero.th.api.analytics.SearchSecurityEventForm;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.key.SearchHotSecurityListType;
import com.tradehero.th.api.security.key.SearchSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.api.users.UserSearchResultDTOList;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.cache.CompetitionListType;
import com.tradehero.th.fragments.chinabuild.cache.CompetitionListTypeSearch;
import com.tradehero.th.fragments.chinabuild.cache.CompetitionNewCache;
import com.tradehero.th.fragments.chinabuild.data.CompetitionDataItem;
import com.tradehero.th.fragments.chinabuild.data.CompetitionInterface;
import com.tradehero.th.fragments.chinabuild.data.UserCompetitionDTO;
import com.tradehero.th.fragments.chinabuild.data.UserCompetitionDTOList;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionDetailFragment;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionUtils;
import com.tradehero.th.fragments.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.network.service.UserServiceWrapper;
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
import timber.log.Timber;

/*
   整合搜索
 */
public class SearchUniteFragment extends DashboardFragment
{
    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject CurrentUserId currentUserId;
    @Inject UserServiceWrapper userServiceWrapper;
    public DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> securityListTypeCacheListener;
    public DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> securityListTypeHotCacheListener;

    @Inject Lazy<CompetitionNewCache> competitionNewCacheLazy;
    private DTOCacheNew.Listener<CompetitionListType, UserCompetitionDTOList> competitionListCacheListenerSearch;

    @Inject Lazy<UserBaseKeyListCache> userBaseKeyListCache;
    private DTOCacheNew.Listener<UserListType, UserSearchResultDTOList> userCacheListenerSearch;

    public static final int TAB_SEARCH_STOCK = 0;
    public static final int TAB_SEARCH_COMPETITION = 1;
    public static final int TAB_SEARCH_USER = 2;

    @InjectView(R.id.tvSearch) TextView tvSearch;
    @InjectView(R.id.edtSearchInput) EditText tvSearchInput;
    @InjectView(R.id.btn_search_x) Button btnSearch_x;

    private SearchHotSecurityListType keyHot;
    private SearchSecurityListType keySearch;
    boolean isUserSearch = false;
    private String searchStr;
    private String searchCancelStr;

    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) SquarePageIndicator indicator;
    private List<View> views = new ArrayList<View>();

    public int tabSelect = 0;
    @InjectView(R.id.tvSearchTabStock) TextView tvSearchTabStock;
    @InjectView(R.id.tvSearchTabCompetition) TextView tvSearchTabCompetition;
    @InjectView(R.id.tvSearchTabUser) TextView tvSearchTabUser;
    @InjectView(R.id.viewLine0) View viewLine0;
    @InjectView(R.id.viewLine1) View viewLine1;
    @InjectView(R.id.viewLine2) View viewLine2;

    public SecurityListView listStock;
    public SecurityListView listCompetition;
    public SecurityListView listUser;
    public SecuritySearchListAdapter adapterStock;
    public CompetitionListAdapter adapterCompetition;
    public SearchUserListAdapter adapterUser;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        securityListTypeCacheListener = createSecurityListFetchListener();
        securityListTypeHotCacheListener = createSecurityListFetchListener();

        competitionListCacheListenerSearch = createCompetitionListCacheListenerSearch();
        userCacheListenerSearch = createUserBaseKeyListCacheListener();

        adapterStock = new SecuritySearchListAdapter(getActivity());
        adapterCompetition = new CompetitionListAdapter(getActivity(), CompetitionUtils.COMPETITION_PAGE_SEARCH);
        adapterUser = new SearchUserListAdapter(getActivity());
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
        hideActionBar();
        initTabPageView();
        return view;
    }

    public void initView()
    {
        searchStr = getActivity().getResources().getString(R.string.search_search);
        searchCancelStr = getActivity().getResources().getString(R.string.search_cancel);
        //if (StringUtils.isNullOrEmptyOrSpaces(getSearchString()) && !isUserSearch)
        //{
        //    if (adapter != null && adapter.getCount() == 0)
        //    {
        //        fetchHotSecuritySearchList(true);
        //    }
        //}

        tvSearchInput.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                String inputStr = editable.toString();
                if (TextUtils.isEmpty(inputStr))
                {
                    tvSearch.setText(searchCancelStr);
                }
                else
                {
                    tvSearch.setText(searchStr);
                }
            }
        });

        tvSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, android.view.KeyEvent keyEvent)
            {
                switch (actionId)
                {
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

        //listSearch.setMode(PullToRefreshBase.Mode.BOTH);
        //listSearch.setAdapter(adapter);
        //listSearch.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        //{
        //    @Override
        //    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
        //    {
        //        Timber.d("下拉刷新");
        //        if (isUserSearch && (!StringUtils.isNullOrEmpty(getSearchString())))
        //        {
        //            fetchSecuritySearchList(true);
        //        }
        //        else
        //        {
        //            fetchHotSecuritySearchList(true);
        //        }
        //    }
        //
        //    @Override
        //    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
        //    {
        //        Timber.d("上拉加载更多");
        //        if (isUserSearch)
        //        {
        //            fetchSecuritySearchListMore();
        //        }
        //        else
        //        {
        //            fetchHotSecuritySearchListMore();
        //        }
        //    }
        //});
        //
        //listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener()
        //{
        //    @Override
        //    public void onItemClick(AdapterView<?> adapterView, View view, int id, long position)
        //    {
        //        SecurityCompactDTO dto = (SecurityCompactDTO) adapter.getItem((int) position);
        //        if (dto != null)
        //        {
        //            Timber.d("list item clicked %s", dto.name);
        //            enterSecurity(dto.getSecurityId(), dto.name, dto);
        //            if (isUserSearch)
        //            {
        //                sendAnalytics(dto);
        //            }
        //        }
        //    }
        //});
        //listSearch.setEmptyView(tvResult);
    }

    public void enterSecurity(SecurityId securityId, String securityName, SecurityCompactDTO dto)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
        pushFragment(SecurityDetailFragment.class, bundle);
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroyView()
    {
        detachSecurityHotListCache();
        detachSecurityListCache();
        detachSearchCompetition();
        ButterKnife.reset(this);
        closeInputMethod();
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setOnclickListener();
    }

    protected DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> createSecurityListFetchListener()
    {
        return new TrendingSecurityListFetchListener();
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
            initAdapterSecurity(value, key);
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
            fetchSecuritySearchList(force);
        }
        else if (index == TAB_SEARCH_COMPETITION)
        {
            fetchSearchCompetition(force);
        }
        else if (index == TAB_SEARCH_USER)
        {
            fetchSearchUser(force);
        }
    }

    private void fetchSecuritySearchList(boolean force)
    {
        if (StringUtils.isNullOrEmptyOrSpaces(getSearchString())) return;
        detachSecurityListCache();
        keySearch = new SearchSecurityListType(getSearchString(), 1, 50);
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

    //private void fetchSecuritySearchListMore()
    //{
    //    if (StringUtils.isNullOrEmptyOrSpaces(getSearchString())) return;
    //    detachSecurityListCache();
    //    securityCompactListCache.get().register(keySearch, securityListTypeCacheListener);
    //    securityCompactListCache.get().getOrFetchAsync(keySearch, true);
    //}
    //
    //private void fetchHotSecuritySearchListMore()
    //{
    //    detachSecurityHotListCache();
    //    securityCompactListCache.get().register(keyHot, securityListTypeHotCacheListener);
    //    securityCompactListCache.get().getOrFetchAsync(keyHot, true);
    //}

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

    @OnClick({R.id.tvSearchTabStock, R.id.tvSearchTabCompetition, R.id.tvSearchTabUser})
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
        }
    }

    @OnClick(R.id.tvSearch)
    public void onSearchClicked()
    {
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
        views = new ArrayList<View>();
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
        getRecommandData();
    }

    public void getRecommandData()
    {
        if (adapterStock != null && adapterUser.getCount() == 0)
        {
            fetchHotSecuritySearchList(true);
        }
    }

    TradeHeroProgressBar progressBar0;
    TradeHeroProgressBar progressBar1;
    TradeHeroProgressBar progressBar2;

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
                        enterSecurity(dto.getSecurityId(), dto.name, dto);
                        if (isUserSearch)
                        {
                            sendAnalytics(dto);
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
                gotoCompetitionDetailFragment(((CompetitionDataItem) item).userCompetitionDTO);
            }
        }
    };

    public AdapterView.OnItemClickListener userItemClickListner =
    new AdapterView.OnItemClickListener()
    {
        @Override public void onItemClick(AdapterView<?> adapterView, View view, int id, long position)
        {
            UserSearchResultDTO item = adapterUser.getItem((int) position);
            if (item instanceof UserSearchResultDTO)
            {
                gotoUserDetailFragment(((UserSearchResultDTO) item).userId);
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
        listStock.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listStock.setOnItemClickListener(securityItemClickListner);

        listStock.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("下拉刷新");
                if (!StringUtils.isNullOrEmpty(getSearchString()))
                {
                    fetchUnite(tabSelect, true);
                }
                else
                {
                    fetchHotSecuritySearchList(true);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {

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
                Timber.d("下拉刷新");
                if (!StringUtils.isNullOrEmpty(getSearchString()))
                {
                    fetchUnite(tabSelect, true);
                }
                else
                {
                    listCompetition.onRefreshComplete();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {

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
        listUser.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        listUser.setOnItemClickListener(userItemClickListner);


        listUser.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("下拉刷新");
                if (!StringUtils.isNullOrEmpty(getSearchString()))
                {
                    fetchUnite(tabSelect, true);
                }
                else
                {
                    listUser.onRefreshComplete();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {

            }
        });
    }

    public PagerAdapter pageAdapter = new PagerAdapter()
    {
        @Override
        public void destroyItem(View container, int position, Object object)
        {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position)
        {
            ((ViewPager) container).addView(views.get(position));
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

    public void setSelectTabView(int index)
    {
        tabSelect = index;
        tvSearchTabStock.setTextColor(
                index == TAB_SEARCH_STOCK ? getResources().getColor(R.color.tradehero_blue) : getResources().getColor(R.color.black));
        tvSearchTabCompetition.setTextColor(
                index == TAB_SEARCH_COMPETITION ? getResources().getColor(R.color.tradehero_blue) : getResources().getColor(R.color.black));
        tvSearchTabUser.setTextColor(
                index == TAB_SEARCH_USER ? getResources().getColor(R.color.tradehero_blue) : getResources().getColor(R.color.black));
        viewLine0.setBackgroundColor(
                index == TAB_SEARCH_STOCK ? getResources().getColor(R.color.tradehero_blue) : getResources().getColor(R.color.gray_normal));
        viewLine1.setBackgroundColor(
                index == TAB_SEARCH_COMPETITION ? getResources().getColor(R.color.tradehero_blue) : getResources().getColor(R.color.gray_normal));
        viewLine2.setBackgroundColor(
                index == TAB_SEARCH_USER ? getResources().getColor(R.color.tradehero_blue) : getResources().getColor(R.color.gray_normal));
    }

    protected DTOCacheNew.Listener<CompetitionListType, UserCompetitionDTOList> createCompetitionListCacheListenerSearch()
    {
        return new CompetitionListCacheListener();
    }

    protected class CompetitionListCacheListener implements DTOCacheNew.Listener<CompetitionListType, UserCompetitionDTOList>
    {
        @Override public void onDTOReceived(@NotNull CompetitionListType key, @NotNull UserCompetitionDTOList value)
        {
            if (key instanceof CompetitionListTypeSearch)
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

    private void gotoCompetitionDetailFragment(UserCompetitionDTO userCompetitionDTO)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_DTO, userCompetitionDTO);
        pushFragment(CompetitionDetailFragment.class, bundle);
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

    protected void detachSearchUser()
    {
        userBaseKeyListCache.get().unregister(userCacheListenerSearch);
    }

    private void fetchSearchUser(boolean force)
    {
        detachSearchUser();
        SearchUserListType searchKey = new SearchUserListType(getSearchString(), 1, 50);
        userBaseKeyListCache.get().register(searchKey, userCacheListenerSearch);
        userBaseKeyListCache.get().getOrFetchAsync(searchKey, force);
    }

    private DTOCacheNew.Listener<UserListType, UserSearchResultDTOList> createUserBaseKeyListCacheListener()
    {
        return new UserBaseKeyListCacheListener();
    }

    protected class UserBaseKeyListCacheListener implements DTOCacheNew.Listener<UserListType, UserSearchResultDTOList>
    {
        public void onDTOReceived(@NotNull UserListType key, @NotNull UserSearchResultDTOList value)
        {
            Timber.d("success");
            onFinish();
            initSearchUserResult(value);
        }

        public void onErrorThrown(@NotNull UserListType key, @NotNull Throwable error)
        {
            Timber.e("Error fetching the list of securities " + key, error);
            onFinish();
        }

        public void onFinish()
        {
            listUser.onRefreshComplete();
            controlLoading(TAB_SEARCH_USER, false);
        }
    }

    public void initSearchUserResult(UserSearchResultDTOList users)
    {
        if (adapterUser != null)
        {
            adapterUser.setListData(users);
        }
    }

    private void gotoUserDetailFragment(int userId)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userId);
        pushFragment(UserMainPage.class, bundle);
    }
}
