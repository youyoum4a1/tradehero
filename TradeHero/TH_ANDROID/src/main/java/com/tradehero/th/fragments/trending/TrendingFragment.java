package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEndScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeDTOList;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.TrendingSecurityListType;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trending.filter.TrendingFilterSelectorView;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeBasicDTO;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTO;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTOFactory;
import com.tradehero.th.loaders.PagedDTOCacheLoader;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.persistence.market.ExchangeListCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class TrendingFragment extends DashboardFragment
{
    private final static String TAG = TrendingFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_TRENDING_FILTER_TYPE_DTO = TrendingFragment.class.getName() + ".trendingFilterTypeDTO";
    public static final String BUNDLE_KEY_PAGE = TrendingFragment.class.getName() + ".page";

    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 20;
    public final static int SECURITY_ID_LIST_LOADER_ID = 0;
    public static final int DEFAULT_VISIBLE_THRESHOLD = 20;

    public final static float MIN_FLING_VELOCITY_Y_FOR_HIDE_FILTER = 1000f;

    private TrendingFilterSelectorView filterSelectorView;
    private TrendingOnFilterTypeChangedListener onFilterTypeChangedListener;
    private TrendingFilterTypeDTO trendingFilterTypeDTO;
    @Inject TrendingFilterTypeDTOFactory trendingFilterTypeDTOFactory;
    private TrendingOnQueryingChangedListener queryingChangedListener;
    private TrendingOnNoMorePagesChangedListener noMorePagesChangedListener;

    @Inject protected Lazy<ExchangeListCache> exchangeListCache;
    private List<ExchangeDTO> exchangeDTOs;
    private DTOCache.Listener<ExchangeListType, ExchangeDTOList> exchangeListTypeCacheListener;
    private DTOCache.GetOrFetchTask<ExchangeListType, ExchangeDTOList> exchangeListCacheFetchTask;

    private ProgressBar mProgressSpinner;
    private AbsListView mTrendingGridView;
    private FlagNearEndScrollListener gridScrollListener;
    private GestureDetector gridViewGesture;

    private int perPage = DEFAULT_PER_PAGE;
    protected SecurityItemViewAdapter securityItemViewAdapter;
    protected int firstVisiblePosition = 0;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Saved instance takes precedence
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY_TRENDING_FILTER_TYPE_DTO))
        {
            this.trendingFilterTypeDTO = this.trendingFilterTypeDTOFactory.create(savedInstanceState.getBundle(BUNDLE_KEY_TRENDING_FILTER_TYPE_DTO));
        }
        else if (getArguments() != null && getArguments().containsKey(BUNDLE_KEY_TRENDING_FILTER_TYPE_DTO))
        {
            this.trendingFilterTypeDTO = this.trendingFilterTypeDTOFactory.create(getArguments().getBundle(BUNDLE_KEY_TRENDING_FILTER_TYPE_DTO));
        }
        else
        {
            this.trendingFilterTypeDTO = new TrendingFilterTypeBasicDTO();
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //THLog.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        initViews(view);
        return view;
    }

    protected void initViews(View view)
    {
        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);
        if (mProgressSpinner != null)
        {
            mProgressSpinner.setVisibility(View.GONE);
        }

        gridScrollListener = new TrendingFlagNearEndScrollListener(DEFAULT_VISIBLE_THRESHOLD);
        queryingChangedListener = new TrendingOnQueryingChangedListener();
        noMorePagesChangedListener = new TrendingOnNoMorePagesChangedListener();
        securityItemViewAdapter = new SecurityItemViewAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.trending_security_item);
        mTrendingGridView = (AbsListView) view.findViewById(R.id.trending_gridview);
        if (mTrendingGridView != null)
        {
            mTrendingGridView.setOnScrollListener(gridScrollListener);
            mTrendingGridView.setAdapter(securityItemViewAdapter);
            mTrendingGridView.setOnItemClickListener(new OnSecurityViewClickListener());
            gridViewGesture = new GestureDetector(getActivity(), new TrendingOnGestureListener());
            mTrendingGridView.setOnTouchListener(new TrendingOnTouchListener());
        }

        this.onFilterTypeChangedListener = new TrendingOnFilterTypeChangedListener();
        this.filterSelectorView = (TrendingFilterSelectorView) view.findViewById(R.id.trending_filter_selector_view);
        if (this.filterSelectorView != null)
        {
            this.filterSelectorView.apply(this.trendingFilterTypeDTO);
            this.filterSelectorView.setChangedListener(this.onFilterTypeChangedListener);
        }

        fetchExchangeList();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //THLog.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setTitle(R.string.header_trending);

        inflater.inflate(R.menu.trending_menu, menu);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(SECURITY_ID_LIST_LOADER_ID, null, new TrendingLoaderCallback());
    }

    @Override public void onResume()
    {
        //THLog.d(TAG, "onResume");
        super.onResume();
        mTrendingGridView.setSelection(Math.min(firstVisiblePosition, mTrendingGridView.getCount()));
        if (gridScrollListener != null)
        {
            gridScrollListener.lowerFlag();
            gridScrollListener.activate();
        }
        //load();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_search:
                pushSearchIn();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onPause()
    {
        firstVisiblePosition = mTrendingGridView.getFirstVisiblePosition();
        if (gridScrollListener != null)
        {
            gridScrollListener.deactivate();
        }
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        //THLog.d(TAG, "onDestroyView");
        if (mTrendingGridView != null)
        {
            mTrendingGridView.setOnItemClickListener(null);
            mTrendingGridView.setOnScrollListener(null);
        }
        mTrendingGridView = null;
        gridScrollListener = null;
        queryingChangedListener = null;
        noMorePagesChangedListener = null;

        this.onFilterTypeChangedListener = null;

        if (filterSelectorView != null)
        {
            filterSelectorView.setChangedListener(null);
        }
        filterSelectorView = null;

        if (exchangeListCacheFetchTask != null)
        {
            exchangeListCacheFetchTask.setListener(null);
        }
        exchangeListCacheFetchTask = null;
        exchangeListTypeCacheListener = null;

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        securityItemViewAdapter = null;
        super.onDestroy();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        THLog.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (this.trendingFilterTypeDTO != null)
        {
            outState.putBundle(BUNDLE_KEY_TRENDING_FILTER_TYPE_DTO, this.trendingFilterTypeDTO.getArgs());
        }
    }

    private void postIfCan(final Runnable runnable)
    {
        View fragmentView = getView();
        if (fragmentView != null)
        {
            fragmentView.post(runnable);
        }
    }

    private void fetchExchangeList()
    {
        if (exchangeListCacheFetchTask != null)
        {
            exchangeListCacheFetchTask.setListener(null);
        }
        if (exchangeListTypeCacheListener == null)
        {
            exchangeListTypeCacheListener = new TrendingExchangeListListener();
        }
        exchangeListCacheFetchTask = exchangeListCache.get().getOrFetch(new ExchangeListType(), exchangeListTypeCacheListener);
        exchangeListCacheFetchTask.execute();
    }

    private void linkWith(ExchangeDTOList exchangeDTOs, boolean andDisplay)
    {
        if (exchangeDTOs == null)
        {
            this.exchangeDTOs = null;
        }
        else
        {
            // We keep only those included in Trending
            this.exchangeDTOs = new ArrayList<>();
            for (ExchangeDTO exchangeDTO: exchangeDTOs)
            {
                if (exchangeDTO.isIncludedInTrending)
                {
                    this.exchangeDTOs.add(exchangeDTO);
                }
            }
        }

        if (filterSelectorView != null)
        {
            filterSelectorView.setUpExchangeSpinner(exchangeDTOs);
            filterSelectorView.apply(trendingFilterTypeDTO);
        }

        if (andDisplay)
        {
        }
    }

    protected void forceInitialLoad()
    {
        Loader loader = getActivity().getSupportLoaderManager().getLoader(SECURITY_ID_LIST_LOADER_ID);
        SecurityListPagedLoader pagedLoader = (SecurityListPagedLoader) loader;
        pagedLoader.setQueryKey(getInitialSecurityListType());
        if (this.gridScrollListener != null)
        {
            this.gridScrollListener.activate();
            this.gridScrollListener.raiseFlag();
        }
        loader.forceLoad();
    }

    protected void load()
    {
        Loader loader = getActivity().getSupportLoaderManager().getLoader(SECURITY_ID_LIST_LOADER_ID);
        loader.startLoading();
    }

    protected void loadNextPage()
    {
        Loader loader = getActivity().getSupportLoaderManager().getLoader(SECURITY_ID_LIST_LOADER_ID);
        SecurityListPagedLoader pagedLoader = (SecurityListPagedLoader) loader;
        pagedLoader.loadNextPage();
        loader.startLoading();
    }

    public TrendingSecurityListType getInitialSecurityListType()
    {
        return getSecurityListType(FIRST_PAGE - 1);
    }

    public TrendingSecurityListType getSecurityListType(int page)
    {
        return trendingFilterTypeDTO.getSecurityListType(getUsableExchangeName(), page, perPage);
    }

    protected String getUsableExchangeName()
    {
        if (trendingFilterTypeDTO != null && trendingFilterTypeDTO.exchange != null && trendingFilterTypeDTO.exchange.name != null)
        {
            return trendingFilterTypeDTO.exchange.name;
        }
        return TrendingSecurityListType.ALL_EXCHANGES;
    }

    protected void showProgressSpinner(boolean flag)
    {
        if (mProgressSpinner != null)
        {
            mProgressSpinner.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void pushSearchIn()
    {
        navigator.pushFragment(SearchStockPeopleFragment.class);
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="Listeners">
    private class TrendingFlagNearEndScrollListener extends FlagNearEndScrollListener
    {
        public final String TAG = TrendingFlagNearEndScrollListener.class.getSimpleName();

        public TrendingFlagNearEndScrollListener(int visibleThreshold)
        {
            super(visibleThreshold);
        }

        @Override public void raiseFlag()
        {
            super.raiseFlag();
            loadNextPage();
        }

        @Override public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            super.onScrollStateChanged(view, scrollState);
        }
    }

    private class OnSecurityViewClickListener implements OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            SecurityId securityId = (SecurityId) parent.getItemAtPosition(position);
            Bundle args = new Bundle();
            args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
            // TODO use other positions
            navigator.pushFragment(BuySellFragment.class, args);

            // startActivity(new SecurityBuyIntent(securityId)); // Example using external navigation
        }
    }

    private class TrendingOnGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            //if (mFilterViewPager != null && Math.abs(velocityY) > MIN_FLING_VELOCITY_Y_FOR_HIDE_FILTER)
            //{
            //    mFilterViewPager.setVisibility(View.GONE);
            //}
            //THLog.d(TAG, "VelocityY " + velocityY);
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private class TrendingOnTouchListener implements View.OnTouchListener
    {
        @Override public boolean onTouch(View view, MotionEvent motionEvent)
        {
            return gridViewGesture.onTouchEvent(motionEvent);
        }
    }

    private class TrendingOnFilterTypeChangedListener implements TrendingFilterSelectorView.OnFilterTypeChangedListener
    {
        @Override public void onFilterTypeChanged(TrendingFilterTypeDTO trendingFilterTypeDTO)
        {
            TrendingFragment.this.trendingFilterTypeDTO = trendingFilterTypeDTO;
            // TODO
            forceInitialLoad();
        }
    }

    private class TrendingOnQueryingChangedListener implements PagedDTOCacheLoader.OnQueryingChangedListener
    {
        @Override public void onQueryingChanged(boolean querying)
        {
            postIfCan(new Runnable()
            {
                @Override public void run()
                {
                    SecurityListPagedLoader securityListPagedLoader =
                            (SecurityListPagedLoader) (Loader) getActivity().getSupportLoaderManager().getLoader(SECURITY_ID_LIST_LOADER_ID);
                    showProgressSpinner(securityListPagedLoader.isQuerying());
                }
            });
        }
    }

    private class TrendingOnNoMorePagesChangedListener implements PagedDTOCacheLoader.OnNoMorePagesChangedListener
    {
        @Override public void onNoMorePagesChanged(boolean noMorePages)
        {
            if (gridScrollListener != null && !noMorePages)
            {
                // There are more pages, so we want to raise the flag  when at the end.
                gridScrollListener.lowerFlag();
            }
            else if (gridScrollListener != null)
            {
                gridScrollListener.deactivate();
            }
        }
    }

    private class TrendingLoaderCallback implements LoaderManager.LoaderCallbacks<SecurityIdList>
    {
        @Override public Loader<SecurityIdList> onCreateLoader(int id, Bundle args)
        {
            if (id == SECURITY_ID_LIST_LOADER_ID)
            {
                SecurityListPagedLoader loader = new SecurityListPagedLoader(getActivity());
                loader.setQueryingChangedListenerWeak(queryingChangedListener);
                loader.setNoMorePagesChangedListenerWeak(noMorePagesChangedListener);
                return loader;
            }
            throw new IllegalStateException("Unhandled loader id " + id);
        }

        @Override public void onLoadFinished(Loader<SecurityIdList> securityIdListLoader, SecurityIdList securityIds)
        {
            securityItemViewAdapter.setItems(securityIds);
            securityItemViewAdapter.notifyDataSetChanged();
            if (gridScrollListener != null)
            {
                gridScrollListener.lowerFlag();
            }
        }

        @Override public void onLoaderReset(Loader<SecurityIdList> securityIdListLoader)
        {
            THLog.d(TAG, "TrendingLoaderCallback.onLoaderReset");
            // TODO
        }
    }

    private class TrendingExchangeListListener implements DTOCache.Listener<ExchangeListType, ExchangeDTOList>
    {
        @Override public void onDTOReceived(ExchangeListType key, ExchangeDTOList value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(ExchangeListType key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_exchange_list_info));
            THLog.e(TAG, "Error fetching the list of exchanges " + key, error);
        }
    };
    //</editor-fold>
}
