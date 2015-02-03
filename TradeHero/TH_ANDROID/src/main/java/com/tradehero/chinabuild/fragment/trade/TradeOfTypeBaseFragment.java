package com.tradehero.chinabuild.fragment.trade;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.chinabuild.fragment.competition.CompetitionUtils;
import com.tradehero.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.activities.MainActivity;
import com.tradehero.th.adapters.SecurityListAdapter;
import com.tradehero.th.adapters.SpinnerExchangeIconAdapter;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingAllSecurityListType;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.market.ExchangeCompactListCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

import javax.inject.Inject;

public class TradeOfTypeBaseFragment extends DashboardFragment
{
    @Inject Lazy<ExchangeCompactListCache> exchangeCompactListCache;
    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;

    public DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> securityListTypeCacheListener;

    public SpinnerExchangeIconAdapter spinnerIconAdapter;
    public AdapterView.OnItemSelectedListener spinnerSelectListener;

    @InjectView(R.id.llSpinner) LinearLayout llSpinner;
    @InjectView(R.id.spinnerExchange) Spinner spinnerExchange;
    @InjectView(R.id.listSecurity) SecurityListView listSecurity;
    @InjectView(R.id.tradeheroprogressbar_hothold) TradeHeroProgressBar pbHotHold;

    private SecurityListAdapter adapterSecurity;

    private ExchangeCompactDTOList exchangeCompactDTOs;
    public static final int DEFAULT_POSITION = 1;

    private int currentPage = 0;
    private int ITEMS_PER_PAGE = 20;
    private String strExchangeName;
    private int currentPosition = 0;

    @Inject Analytics analytics;

    private int tradehero_blue;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapterSecurity = new SecurityListAdapter(getActivity(), getTradeType());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = getRootView(inflater, container);
        ButterKnife.inject(this, view);
        //exchangeListTypeCacheListener = createExchangeListTypeFetchListener();
        spinnerSelectListener = createSpinnerItemSelectListener();
        securityListTypeCacheListener = createSecurityListFetchListener();
        listSecurity.setAdapter(adapterSecurity);
        initView();

        if (getTradeType() == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_HOLD)
        {
            analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_HOLD_PARTIES));
        }
        if (getTradeType() == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_RISE_PERCENT)
        {
            analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_RISE_PARTIES));
        }

        tradehero_blue = getActivity().getResources().getColor(R.color.tradehero_blue);
        return view;
    }

    public View getRootView(LayoutInflater inflater, ViewGroup container)
    {
        return inflater.inflate(R.layout.trade_of_type_base_layout, container, false);
    }

    protected void initView()
    {
        //fetchExchangeList();
        initSpinnerView(CompetitionUtils.getExchangeList());
        initListView();
    }

    public PullToRefreshBase.Mode getRefreshMode()
    {
        return PullToRefreshBase.Mode.BOTH;
    }

    private void initListView()
    {

        listSecurity.setMode(getRefreshMode());

        listSecurity.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("下拉刷新");
                //fetchSecurityList(currentPosition);
                fetchSecurityList(currentPosition, true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("上拉加载更多");
                fetchSecurityList();
            }
        });

        listSecurity.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int id, long position)
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

    private void fetchSecurityList()
    {
        detachSecurityListCache();
        SecurityListType key = new TrendingAllSecurityListType(getTradeType(), getStrExchangeName(), currentPage + 1, ITEMS_PER_PAGE);
        securityCompactListCache.get().register(key, securityListTypeCacheListener);
        securityCompactListCache.get().getOrFetchAsync(key, true);
    }

    private void fetchSecurityList(int position, boolean force)
    {
        currentPosition = position;
        strExchangeName = "";
        currentPage = 0;
        if (position > 0)
        {
            strExchangeName = exchangeCompactDTOs.get(position - 1).name;
        }
        detachSecurityListCache();
        SecurityListType key = new TrendingAllSecurityListType(getTradeType(), getStrExchangeName(), currentPage + 1, ITEMS_PER_PAGE);
        securityCompactListCache.get().register(key, securityListTypeCacheListener);
        securityCompactListCache.get().getOrFetchAsync(key, force);
    }

    private void showLoadingProgress()
    {
        Handler handler = new Handler();
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (pbHotHold != null)
                {
                    pbHotHold.setVisibility(View.VISIBLE);
                    pbHotHold.startLoading();
                }
            }
        });
    }

    private void dismissLoadingProgress()
    {
        Handler handler = new Handler();
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (pbHotHold != null && pbHotHold.getVisibility() == View.VISIBLE)
                {
                    pbHotHold.stopLoading();
                    pbHotHold.setVisibility(View.GONE);
                }
            }
        });
    }

    public String getStrExchangeName()
    {
        return strExchangeName;
    }

    public int getTradeType()
    {
        return TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_HOLD;
    }

    private void detachSecurityListCache()
    {
        if (securityListTypeCacheListener != null)
        {
            securityCompactListCache.get().unregister(securityListTypeCacheListener);
        }
    }

    //private void fetchExchangeList()
    //{
    //    detachExchangeListCache();
    //    ExchangeListType key = new ExchangeListType();
    //    exchangeCompactListCache.get().register(key, exchangeListTypeCacheListener);
    //    exchangeCompactListCache.get().getOrFetchAsync(key,false);
    //}

    //protected void detachExchangeListCache()
    //{
    //    if (exchangeListTypeCacheListener != null)
    //    {
    //        exchangeCompactListCache.get().unregister(exchangeListTypeCacheListener);
    //    }
    //}

    protected DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> createSecurityListFetchListener()
    {
        return new TrendingSecurityListFetchListener();
    }

    protected class TrendingSecurityListFetchListener implements DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList>
    {
        @Override
        public void onDTOReceived(@NotNull SecurityListType key, @NotNull SecurityCompactDTOList value)
        {
            initAdapterSecurity(value, key);
            onFinished();
            showGuideView();
        }

        @Override
        public void onErrorThrown(@NotNull SecurityListType key, @NotNull Throwable error)
        {
            Timber.e("Error fetching the list of security %s", key, error);
            onFinished();
        }

        public void onFinished()
        {
            dismissLoadingProgress();
            listSecurity.onRefreshComplete();
        }
    }

    protected AdapterView.OnItemSelectedListener createSpinnerItemSelectListener()
    {
        return new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
            {
                if (view != null)
                {
                    TextView selectedTV = (TextView) view.findViewById(R.id.tvSpinnerItemName);
                    selectedTV.setTextColor(tradehero_blue);
                }
                getExchangSecurity(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        };
    }

    ////<editor-fold desc="Exchange List Listener">
    //protected DTOCacheNew.Listener<ExchangeListType, ExchangeCompactDTOList> createExchangeListTypeFetchListener()
    //{
    //    return new TrendingExchangeListTypeFetchListener();
    //}
    //
    //protected class TrendingExchangeListTypeFetchListener implements DTOCacheNew.Listener<ExchangeListType, ExchangeCompactDTOList>
    //{
    //    @Override
    //    public void onDTOReceived(@NotNull ExchangeListType key, @NotNull ExchangeCompactDTOList value)
    //    {
    //        Timber.d("Filter exchangeListTypeCacheListener onDTOReceived");
    //        initSpinnerView(value);
    //    }
    //
    //    @Override
    //    public void onErrorThrown(@NotNull ExchangeListType key, @NotNull Throwable error)
    //    {
    //        THToast.show(getString(R.string.error_fetch_exchange_list));
    //        Timber.e("Error fetching the list of exchanges %s", key, error);
    //    }
    //}

    //</editor-fold>
    private void initAdapterSecurity(SecurityCompactDTOList list, SecurityListType key)
    {
        if (key.getPage() == PagedLeaderboardKey.FIRST_PAGE)
        {
            currentPage = 0;
            //adapterSecurity = new SecurityListAdapter(getActivity(), list, getTradeType());
            adapterSecurity.setSecurityList(list);
            listSecurity.setAdapter(adapterSecurity);
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

    public void enterSecurity(SecurityId securityId, String securityName)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
        gotoDashboard(SecurityDetailFragment.class.getName(), bundle);
    }

    private void initSpinnerView(ExchangeCompactDTOList value)
    {
        exchangeCompactDTOs = value;
        int sizeList = value.size();
        String[] strExchangeList = new String[sizeList + 1];
        int[] countryList = new int[sizeList + 1];
        strExchangeList[0] = "全部证券";
        countryList[0] = R.drawable.default_image;
        for (int i = 1; i < sizeList + 1; i++)
        {
            strExchangeList[i] = value.get(i - 1).desc;
            countryList[i] = value.get(i - 1).getCountryCodeFlagResId();
        }
        spinnerIconAdapter = new SpinnerExchangeIconAdapter(getActivity(), strExchangeList, countryList);
        spinnerExchange.setAdapter(spinnerIconAdapter);
        spinnerExchange.setOnItemSelectedListener(spinnerSelectListener);
        spinnerExchange.setSelection(DEFAULT_POSITION);
    }

    private int currentSelection = -1;

    private void getExchangSecurity(int position)
    {
        if (getTradeType() == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_HOLD)
        {
            analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_HOLD_PARTIES));
        }
        if (getTradeType() == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_RISE_PERCENT)
        {
            analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_RISE_PARTIES));
        }

        if (adapterSecurity != null && adapterSecurity.getCount() == 0 || currentSelection != position)
        {
            showLoadingProgress();
            fetchSecurityList(position, true);
            currentSelection = position;
        }

        Timber.d("WINDYLOG: fetchSecurityList " + getTradeType());
    }

    @Override public void onPause()
    {
        super.onPause();
        if (listSecurity != null)
        {
            listSecurity.onRefreshComplete();
        }
    }

    @Override
    public void onStop()
    {
        //detachExchangeListCache();
        detachSecurityListCache();
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        securityListTypeCacheListener = null;
        super.onDestroyView();
    }

    protected void showGuideView()
    {
        if (!THSharePreferenceManager.isGuideAvailable(getActivity(), THSharePreferenceManager.GUIDE_STOCK_DETAIL))
        {
            return;
        }
        if (getTradeType() == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_HOLD)
        {
            if (adapterSecurity == null)
            {
                return;
            }
            if (adapterSecurity.getCount() > 0)
            {
                ((MainActivity) getActivity()).showGuideView(MainActivity.GUIDE_TYPE_STOCK_DETAIL);
            }
        }
    }
}
