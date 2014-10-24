package com.tradehero.th.fragments.chinabuild.fragment.trade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.MyTradePositionListAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.data.PositionInterface;
import com.tradehero.th.fragments.chinabuild.data.SecurityPositionItem;
import com.tradehero.th.fragments.chinabuild.data.THSharePreferenceManager;
import com.tradehero.th.fragments.chinabuild.data.WatchPositionItem;
import com.tradehero.th.fragments.chinabuild.fragment.ShareDialogFragment;
import com.tradehero.th.fragments.chinabuild.fragment.portfolio.PositionDetailFragment;
import com.tradehero.th.fragments.chinabuild.fragment.search.SearchFragment;
import com.tradehero.th.fragments.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.prefs.ShareDialogKey;
import com.tradehero.th.persistence.prefs.ShareDialogROIValueKey;
import com.tradehero.th.persistence.prefs.ShareDialogTotalValueKey;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

/*
    交易－我的交易
 */
public class TradeOfMineFragment extends DashboardFragment
{

    @Nullable protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> fetchGetPositionsDTOListener;
    private DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> userWatchlistPositionFetchListener;
    private DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO> portfolioFetchListener;

    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    private DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> portfolioCompactListFetchListener;

    @Inject Lazy<GetPositionsCache> getPositionsCache;
    @Inject UserWatchlistPositionCache userWatchlistPositionCache;
    @Inject PortfolioCache portfolioCache;
    @Inject CurrentUserId currentUserId;

    @InjectView(R.id.tvWatchListItemROI) TextView tvItemROI;
    @InjectView(R.id.tvWatchListItemAllAmount) TextView tvItemAllAmount;
    @InjectView(R.id.tvWatchListItemDynamicAmount) TextView tvItemDynamicAmount;
    @InjectView(R.id.tvWatchListItemCash) TextView tvItemCash;

    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.tradeMyPositionList) SecurityListView listView;
    @InjectView(R.id.llEmpty) LinearLayout llEmpty;
    @InjectView(R.id.btnEmptyAction) Button btnEmptyAction;

    @InjectView(R.id.rlListAll) RelativeLayout rlListAll;
    @InjectView(R.id.llPositionHeadItem) LinearLayout llPositionHeadItem;
    @InjectView(R.id.tvPositionHead) TextView tvPositionHead;

    private OwnedPortfolioId shownPortfolioId;
    private PortfolioDTO shownPortfolioDTO;
    protected GetPositionsDTOKey getPositionsDTOKey;
    protected GetPositionsDTO getPositionsDTO;

    private MyTradePositionListAdapter adapter;
    @Inject @ShareDialogKey BooleanPreference mShareDialogKeyPreference;
    @Inject @ShareDialogTotalValueKey BooleanPreference mShareDialogTotalValueKeyPreference;
    @Inject @ShareDialogROIValueKey BooleanPreference mShareDialogROIValueKeyPreference;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    @Inject Analytics analytics;


    private static long time_stamp = -1;
    private final long duration_showing_dialog = 120000;
    private boolean availableShowDialog = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new MyTradePositionListAdapter(getActivity());
        fetchGetPositionsDTOListener = createGetPositionsCacheListener();
        userWatchlistPositionFetchListener = createWatchlistListener();
        portfolioFetchListener = createPortfolioCacheListener();
        portfolioCompactListFetchListener = createPortfolioCompactListFetchListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.trade_of_mine, container, false);
        ButterKnife.inject(this, view);
        if (adapter.getCount() == 0)
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.progress);
        }
        else
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.rlListAll);
        }

        initView();
        fetchPortfolio();
        llPositionHeadItem.setVisibility(View.GONE);
        return view;
    }

    public void initView()
    {
        listView.setEmptyView(llEmpty);
        //listView.setAdapter((ListAdapter)adapter);
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("下拉刷新");
                refreshData(true);
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int id, long position)
            {
                PositionInterface item = adapter.getItem((int) position);
                dealSecurityItem(item);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override public void onScrollStateChanged(AbsListView absListView, int i)
            {

            }

            @Override public void onScroll(AbsListView absListView, int i, int i2, int i3)
            {
                Timber.d("onScroll i = " + i + " i2 = " + i2 + "i3 = " + i3);
                if (i == 0)
                {
                    llPositionHeadItem.setVisibility(View.GONE);
                }
                else
                {
                    llPositionHeadItem.setVisibility(View.VISIBLE);
                    tvPositionHead.setText(adapter.getHeadText(i));
                }
            }
        });
    }

    public void dealSecurityItem(PositionInterface item)
    {
        if (item instanceof SecurityPositionItem)
        {
            enterSecurity(((SecurityPositionItem) item).security.getSecurityId(), ((SecurityPositionItem) item).security.name,
                    ((SecurityPositionItem) item).position);
        }
        else if (item instanceof WatchPositionItem)
        {
            enterSecurity(((WatchPositionItem) item).watchlistPosition.securityDTO.getSecurityId(),
                    ((WatchPositionItem) item).watchlistPosition.securityDTO.name);
        }
    }

    public void enterSecurity(SecurityId securityId, String securityName, PositionDTO positionDTO)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
        PositionDetailFragment.putPositionDTOKey(bundle, positionDTO.getPositionDTOKey());
        if (shownPortfolioId != null)
        {
            PositionDetailFragment.putApplicablePortfolioId(bundle, shownPortfolioId);
        }
        gotoDashboard(PositionDetailFragment.class, bundle);
    }

    public void enterSecurity(SecurityId securityId, String securityName)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
        gotoDashboard(SecurityDetailFragment.class.getName(), bundle);
    }

    @Override public void onStart(){
        availableShowDialog = true;
        super.onStart();
    }

    @Override public void onStop()
    {
        availableShowDialog = false;
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        //ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        fetchGetPositionsDTOListener = null;
        portfolioFetchListener = null;
        //userWatchlistPositionRefreshListener = null;
        userWatchlistPositionFetchListener = null;
        portfolioCompactListFetchListener = null;
        super.onDestroy();
    }

    @Override public void onResume()
    {
        Timber.d("------> Analytics TradeOfMineFragment onResume");
        analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_MINE_TRADE));
        refreshData(false);
        super.onResume();
    }

    @OnClick(R.id.btnEmptyAction)
    public void onEmptyActionClicked()
    {
        gotoDashboard(SearchFragment.class.getName());
    }

    public void refreshData(boolean force)
    {
        fetchPortfolioCompactList(force);
        fetchWatchPositionList(force);
    }

    protected void fetchSimplePage(boolean force)
    {
        //if (getPositionsDTOKey != null && getPositionsDTOKey.isValid())
        if (getPositionsDTOKey == null && shownPortfolioId != null)
        {
            getPositionsDTOKey = new OwnedPortfolioId(shownPortfolioId.userId, shownPortfolioId.portfolioId);
        }
        if (getPositionsDTOKey != null)
        {
            detachGetPositionsTask();
            fetchGetPositionsDTOListener = createGetPositionsCacheListener();
            getPositionsCache.get().register(getPositionsDTOKey, fetchGetPositionsDTOListener);
            getPositionsCache.get().getOrFetchAsync(getPositionsDTOKey, force);
        }
    }

    private void fetchPortfolioCompactList(boolean force)
    {
        detachPortfolioCompactListCache();
        portfolioCompactListCache.register(currentUserId.toUserBaseKey(), portfolioCompactListFetchListener);
        portfolioCompactListCache.getOrFetchAsync(currentUserId.toUserBaseKey(), force);
    }

    protected void fetchPortfolio()
    {
        if (shownPortfolioId == null) return;
        if (portfolioFetchListener == null) return;
        detachPortfolioFetchTask();
        portfolioCache.register(shownPortfolioId, portfolioFetchListener);
        portfolioCache.getOrFetchAsync(shownPortfolioId);
    }

    protected void fetchWatchPositionList(boolean force)
    {
        detachUserWatchlistFetchTask();
        userWatchlistPositionCache.register(currentUserId.toUserBaseKey(), userWatchlistPositionFetchListener);
        userWatchlistPositionCache.getOrFetchAsync(currentUserId.toUserBaseKey(), force);
    }

    protected void detachGetPositionsTask()
    {
        getPositionsCache.get().unregister(fetchGetPositionsDTOListener);
    }

    protected void detachUserWatchlistFetchTask()
    {
        userWatchlistPositionCache.unregister(userWatchlistPositionFetchListener);
    }

    protected void detachPortfolioFetchTask()
    {
        portfolioCache.unregister(portfolioFetchListener);
    }

    private void detachPortfolioCompactListCache()
    {
        portfolioCompactListCache.unregister(portfolioCompactListFetchListener);
    }

    @NotNull protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> createGetPositionsCacheListener()
    {
        return new GetPositionsListener();
    }

    protected class GetPositionsListener
            implements DTOCacheNew.HurriedListener<GetPositionsDTOKey, GetPositionsDTO>
    {
        @Override public void onPreCachedDTOReceived(
                @NotNull GetPositionsDTOKey key,
                @NotNull GetPositionsDTO value)
        {
            initPositionSecurity(value);
            finish();
        }

        @Override public void onDTOReceived(
                @NotNull GetPositionsDTOKey key,
                @NotNull GetPositionsDTO value)
        {
            initPositionSecurity(value);
            finish();
        }

        @Override public void onErrorThrown(
                @NotNull GetPositionsDTOKey key,
                @NotNull Throwable error)
        {
            finish();
        }

        public void finish()
        {
            listView.onRefreshComplete();
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.rlListAll);
        }
    }

    protected DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> createWatchlistListener()
    {
        return new WatchlistPositionFragmentSecurityIdListCacheListener();
    }

    protected class WatchlistPositionFragmentSecurityIdListCacheListener implements DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull WatchlistPositionDTOList value)
        {
            Timber.d("");
            initWatchList(value);
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            onFinish();
        }

        private void onFinish()
        {
            listView.onRefreshComplete();
            fetchSimplePage(false);
        }
    }

    //protected DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> createRefreshWatchlistListener()
    //{
    //    return new RefreshWatchlisListener();
    //}
    //
    //protected class RefreshWatchlisListener implements DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList>
    //{
    //    @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull WatchlistPositionDTOList value)
    //    {
    //        //watchlistPositionListView.onRefreshComplete();
    //        //displayWatchlist(value);
    //        Timber.d("");
    //    }
    //
    //    @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
    //    {
    //        //watchlistPositionListView.onRefreshComplete();
    //        //if (watchListAdapter == null || watchListAdapter.getCount() <= 0)
    //        //{
    //        //    THToast.show(getString(R.string.error_fetch_portfolio_watchlist));
    //        //}
    //    }
    //}

    protected DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO> createPortfolioCacheListener()
    {
        return new WatchlistPositionFragmentPortfolioCacheListener();
    }

    protected class WatchlistPositionFragmentPortfolioCacheListener implements DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO>
    {
        @Override public void onDTOReceived(@NotNull OwnedPortfolioId key, @NotNull PortfolioDTO value)
        {
            shownPortfolioDTO = value;
            displayProfolioDTO(shownPortfolioDTO);
        }

        @Override public void onErrorThrown(@NotNull OwnedPortfolioId key, @NotNull Throwable error)
        {
            //THToast.show(R.string.error_fetch_portfolio_info);
        }
    }

    protected DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> createPortfolioCompactListFetchListener()
    {
        return new BasePurchaseManagementPortfolioCompactListFetchListener();
    }

    protected class BasePurchaseManagementPortfolioCompactListFetchListener implements DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList>
    {
        protected BasePurchaseManagementPortfolioCompactListFetchListener()
        {
            // no unexpected creation
        }

        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull PortfolioCompactDTOList value)
        {
            prepareApplicableOwnedPortolioId(value.getDefaultPortfolio());
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            //THToast.show(R.string.error_fetch_portfolio_list_info);
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.rlListAll);
        }
    }

    protected void prepareApplicableOwnedPortolioId(@Nullable PortfolioCompactDTO defaultIfNotInArgs)
    {
        if (defaultIfNotInArgs != null)
        {
            shownPortfolioId = defaultIfNotInArgs.getOwnedPortfolioId();
        }
        fetchPortfolio();
        fetchSimplePage(false);
    }

    private void displayProfolioDTO(PortfolioDTO cached)
    {
        if (cached != null && cached.roiSinceInception != null)
        {
            THSignedNumber roi = THSignedPercentage.builder(cached.roiSinceInception * 100)
                    .withSign()
                    .signTypeArrow()
                    .build();
            tvItemROI.setText(roi.toString());
            tvItemROI.setTextColor(getResources().getColor(roi.getColorResId()));
        }

        String valueString = String.format("%s %,.0f", cached.getNiceCurrency(), cached.totalValue);
        tvItemAllAmount.setText(valueString);
        //总资产数达到15w
        if (cached.totalValue > 150000 && getActivity()!=null && availableShowDialog)
        {
            int userId = currentUserId.toUserBaseKey().getUserId();
            if(THSharePreferenceManager.isShareDialogMoreThanFifteenAvailable(userId, getActivity())){
                    mShareDialogKeyPreference.set(false);
                    mShareDialogTotalValueKeyPreference.set(false);
                    mShareSheetTitleCache.set(getString(R.string.share_amount_total_value_summary,
                            currentUserId.get().toString()));
                    ShareDialogFragment.showDialog(getActivity().getSupportFragmentManager(),
                            getString(R.string.share_amount_total_value_title), getString(R.string.share_amount_total_value_summary,
                            currentUserId.get().toString()), THSharePreferenceManager.PROPERTY_MORE_THAN_FIFTEEN, userId);
                    time_stamp = System.currentTimeMillis();
            }else{
                if (cached.totalValue > 250000 && (System.currentTimeMillis()-time_stamp)>duration_showing_dialog){
                    if(THSharePreferenceManager.isShareDialogMoreThanTwentyFiveAvailable(userId, getActivity())){
                          mShareDialogKeyPreference.set(false);
                          mShareDialogTotalValueKeyPreference.set(false);
                          mShareSheetTitleCache.set(getString(R.string.share_amount_total_value_summary25,
                                 currentUserId.get().toString()));
                          ShareDialogFragment.showDialog(getActivity().getSupportFragmentManager(),
                                 getString(R.string.share_amount_total_value_title25), getString(R.string.share_amount_total_value_summary25,
                                 currentUserId.get().toString()), THSharePreferenceManager.PROPERTY_MORE_THAN_TWENTY_FIVE, userId);
                        time_stamp = -1;
                    }
                }
            }
        }

        Double pl = cached.plSinceInception;
        if (pl == null)
        {
            pl = 0.0;
        }
        THSignedNumber thPlSinceInception = THSignedMoney.builder(pl)
                .withSign()
                .signTypePlusMinusAlways()
                .currency(cached.getNiceCurrency())
                .build();
        tvItemDynamicAmount.setText(thPlSinceInception.toString());
        tvItemDynamicAmount.setTextColor(thPlSinceInception.getColor());

        String vsCash = String.format("%s %,.0f", cached.getNiceCurrency(), cached.cashBalance);
        tvItemCash.setText(vsCash);
    }

    //自选股列表显示
    private void initWatchList(WatchlistPositionDTOList watchList)
    {
        if (watchList != null)
        {
            int sizeWatchList = watchList.size();
            if (sizeWatchList > 0)
            {
                ArrayList<WatchPositionItem> list = new ArrayList<WatchPositionItem>();
                for (int i = 0; i < sizeWatchList; i++)
                {
                    list.add(new WatchPositionItem(watchList.get(i)));
                }
                adapter.setWatchPositionList(list);
            }
        }
    }

    private void initPositionSecurity(GetPositionsDTO psList)
    {
        if (psList != null && psList.openPositionsCount > 0)
        {
            ArrayList<SecurityPositionItem> list = new ArrayList<SecurityPositionItem>();
            List<PositionDTO> listData = psList.getOpenPositions();
            int sizePosition = listData.size();
            for (int i = 0; i < sizePosition; i++)
            {
                SecurityCompactDTO securityCompactDTO = psList.getSecurityCompactDTO(listData.get(i));
                if (securityCompactDTO != null)
                {
                    list.add(new SecurityPositionItem(securityCompactDTO, listData.get(i)));
                    //持有股票收益率涨副超过 10% 弹窗提示分享
                    if (listData.get(i).getROISinceInception() * 100 > 10)
                    {
                        if (mShareDialogKeyPreference.get() && mShareDialogROIValueKeyPreference.get())
                        {
                            mShareDialogKeyPreference.set(false);
                            mShareDialogROIValueKeyPreference.set(false);
                            mShareSheetTitleCache.set(getString(
                                    R.string.share_amount_roi_value_summary, "10%", currentUserId.get().toString(),
                                    String.valueOf(listData.get(i).id)));
                            ShareDialogFragment.showDialog(getActivity().getSupportFragmentManager(),
                                    getString(R.string.share_amount_roi_value_title), getString(
                                    R.string.share_amount_roi_value_summary, "10%", currentUserId.get().toString(),
                                    String.valueOf(listData.get(i).id)));
                        }
                    }
                }
            }
            adapter.setSecurityPositionList(list);
        }

        if (psList != null && psList.closedPositionsCount > 0)
        {
            ArrayList<SecurityPositionItem> list = new ArrayList<SecurityPositionItem>();
            List<PositionDTO> listData = psList.getClosedPositions();
            int sizePosition = listData.size();
            for (int i = 0; i < sizePosition; i++)
            {
                SecurityCompactDTO securityCompactDTO = psList.getSecurityCompactDTO(listData.get(i));
                if (securityCompactDTO != null)
                {
                    list.add(new SecurityPositionItem(securityCompactDTO, listData.get(i)));
                }
            }
            if (adapter != null)
            {
                adapter.setSecurityPositionListClosed(list);
            }
        }
    }
}
