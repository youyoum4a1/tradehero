package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEndScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeStringId;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class TrendingFragment extends DashboardFragment
{
    private final static String TAG = TrendingFragment.class.getSimpleName();

    public final static String BUNDLE_KEY_FILTER_PAGE = TrendingFragment.class.getName() + ".filterPage";
    public final static String BUNDLE_KEY_SELECTED_EXCHANGE_NAMES = TrendingFragment.class.getName() + ".selectedExchangeNames";
    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 30;

    public final static float MIN_FLING_VELOCITY_Y_FOR_HIDE_FILTER = 1000f;

    private ViewPager mFilterViewPager;
    private TrendingFilterPagerAdapter mTrendingFilterPagerAdapter;
    private int filterPagerEndHeight = 80;
    private RelativeLayout.LayoutParams filterLayoutParams;
    private TrendingOnPreviousNextListener onPreviousNextListener;
    private TrendingOnPositionedExchangeSelectionChangedListener exchangeSelectionChangedListener;

    private ProgressBar mProgressSpinner;
    private AbsListView mTrendingGridView;
    private FlagNearEndScrollListener gridScrollListener;
    private float gridCumulativeScrollY = 0;
    private float awayBy = 0;
    private boolean filterVisibleBeforeFling = true;
    private GestureDetector gridViewGesture;

    private int filterPageSelected = 0;
    private ExchangeStringId[] selectedExchangeStringIds = new ExchangeStringId[TrendingFilterSelectorUtil.FRAGMENT_COUNT];
    private boolean querying;
    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;
    private DTOCache.GetOrFetchTask<SecurityIdList> trendingTask;
    private TrendingSecurityListCacheListener trendingCacheListener;
    private List<SecurityId> securityIds;
    private int currentlyLoadingPage;
    private int lastLoadedPage;
    private int perPage = DEFAULT_PER_PAGE;
    protected SecurityItemViewAdapter securityItemViewAdapter;
    protected int firstVisiblePosition = 0;
    protected TrendingFilterSelectorFragment.OnResumedListener trendingFilterSelectorResumedListener;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            filterPageSelected = savedInstanceState.getInt(BUNDLE_KEY_FILTER_PAGE, filterPageSelected);
            String[] exchangeNames = savedInstanceState.getStringArray(BUNDLE_KEY_SELECTED_EXCHANGE_NAMES);
            for (int i = 0; i < selectedExchangeStringIds.length; i++)
            {
                selectedExchangeStringIds[i] = new ExchangeStringId(exchangeNames[i] == null ? "" : exchangeNames[i]);
            }
        }

        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        initViews(view);
        return view;
    }

    protected void initViews(View view)
    {
        trendingCacheListener = new TrendingSecurityListCacheListener();

        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);
        if (mProgressSpinner != null)
        {
            mProgressSpinner.setVisibility(View.GONE);
        }

        gridScrollListener = new TrendingFlagNearEndScrollListener();
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

        this.mTrendingFilterPagerAdapter = new TrendingFilterPagerAdapter(getActivity(), getFragmentManager());
        this.trendingFilterSelectorResumedListener = new TrendingOnResumedListener();
        this.mTrendingFilterPagerAdapter.setOnResumedListener(this.trendingFilterSelectorResumedListener);
        this.onPreviousNextListener = new TrendingOnPreviousNextListener();
        this.mTrendingFilterPagerAdapter.setOnPreviousNextListener(this.onPreviousNextListener);
        this.exchangeSelectionChangedListener = new TrendingOnPositionedExchangeSelectionChangedListener();
        this.mTrendingFilterPagerAdapter.setOnPositionedExchangeSelectionChangedListener(this.exchangeSelectionChangedListener);

        mFilterViewPager = (ViewPager) view.findViewById(R.id.trending_filter_pager);
        if (mFilterViewPager != null)
        {
            mFilterViewPager.setAdapter(mTrendingFilterPagerAdapter);
            mFilterViewPager.setOnPageChangeListener(new TrendingFilterOnPageChangeListener());
            mFilterViewPager.setCurrentItem(TrendingFilterSelectorBasicFragment.POSITION_IN_PAGER);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        THLog.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setTitle(R.string.header_trending);

        inflater.inflate(R.menu.trending_menu, menu);
    }

    @Override public void onResume()
    {
        super.onResume();
        displayFilterPager();
        filterPagerEndHeight = (int) getResources().getDimension(R.dimen.trending_filter_view_pager_height);
        if (mFilterViewPager != null)
        {
            filterLayoutParams = (RelativeLayout.LayoutParams) mFilterViewPager.getLayoutParams();
            setAwayBy(0);
        }
        securityItemViewAdapter.setItems(securityIds);
        if (securityIds == null || securityIds.size() == 0)
        {
            startAnew();
        }
        mTrendingGridView.setSelection(Math.min(firstVisiblePosition, mTrendingGridView.getCount()));
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
        setAwayBy(0);
        firstVisiblePosition = mTrendingGridView.getFirstVisiblePosition();
        if (gridScrollListener != null)
        {
            gridScrollListener.deactivate();
        }
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        THLog.d(TAG, "onDestroyView");
        if (mTrendingGridView != null)
        {
            mTrendingGridView.setOnItemClickListener(null);
            mTrendingGridView.setOnScrollListener(null);
        }
        mTrendingGridView = null;
        gridScrollListener = null;

        if (trendingTask != null)
        {
            trendingTask.forgetListener(true);
        }
        trendingTask = null;
        trendingCacheListener = null;

        if (mTrendingFilterPagerAdapter != null)
        {
            mTrendingFilterPagerAdapter.setOnPreviousNextListener(null);
            mTrendingFilterPagerAdapter.setOnResumedListener(null);
            mTrendingFilterPagerAdapter.setOnPositionedExchangeSelectionChangedListener(null);
        }
        mTrendingFilterPagerAdapter = null;
        onPreviousNextListener = null;
        exchangeSelectionChangedListener = null;

        if (mFilterViewPager != null)
        {
            mFilterViewPager.setOnPageChangeListener(null);
        }
        mFilterViewPager = null;
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
        outState.putInt(BUNDLE_KEY_FILTER_PAGE, filterPageSelected);
        String[] exchangeNames = new String[selectedExchangeStringIds.length];
        int index = 0;
        for (ExchangeStringId exchangeStringId: selectedExchangeStringIds)
        {
            exchangeNames[index++] = exchangeStringId == null ? null : exchangeStringId.key;
        }
        outState.putStringArray(BUNDLE_KEY_SELECTED_EXCHANGE_NAMES, exchangeNames);
    }

    public boolean isQuerying()
    {
        return querying;
    }

    protected void setQuerying(boolean querying)
    {
        this.querying = querying;
        showProgressSpinner(this.querying);
    }

    protected void startAnew()
    {
        this.lastLoadedPage = FIRST_PAGE - 1;
        this.currentlyLoadingPage = FIRST_PAGE - 1;
        this.securityIds = new ArrayList<>();
        if (gridScrollListener != null)
        {
            gridScrollListener.lowerFlag();
            gridScrollListener.activate();
        }

    }

    protected void loadNewPage(int newPage)
    {
        if (newPage != lastLoadedPage + 1)
        {
            THLog.e(TAG, "Will not load newPage " + newPage + ", lastLoadedPage " + lastLoadedPage, new Exception());
        }
        if (currentlyLoadingPage != FIRST_PAGE - 1 && currentlyLoadingPage != newPage)
        {
            THLog.e(TAG, "This page is already loading another one " + currentlyLoadingPage + ", will not load " + newPage, new Exception());
        }
        if (trendingTask != null)
        {
            trendingTask.forgetListener(true);
        }
        setQuerying(true);
        currentlyLoadingPage = newPage;
        trendingTask = securityCompactListCache.get().getOrFetch(getSecurityListType(newPage), false, trendingCacheListener);
        trendingTask.execute();
    }

    public void displayFilterPager()
    {
        getView().post(new Runnable()
        {
            @Override public void run()
            {
                if (mTrendingFilterPagerAdapter != null)
                {
                    mTrendingFilterPagerAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public TrendingSecurityListType getSecurityListType(int page)
    {
        return TrendingFilterSelectorUtil.getSecurityListType(filterPageSelected, getUsableExchangeName(), page, perPage);
    }

    protected String getUsableExchangeName()
    {
        String usableExchangeName = null;
        if (selectedExchangeStringIds[filterPageSelected] != null)
        {
            usableExchangeName = selectedExchangeStringIds[filterPageSelected].key;
        }

        if (usableExchangeName == null || usableExchangeName.isEmpty())
        {
            usableExchangeName = TrendingSecurityListType.ALL_EXCHANGES;
        }
        return usableExchangeName;
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

    private void resizeViewPager(Fragment fragment)
    {
        if (mFilterViewPager != null && fragment != null && fragment.getView() != null)
        {
            ViewGroup.LayoutParams pagerParams = mFilterViewPager.getLayoutParams();
            if (pagerParams != null)
            {
                pagerParams.height = fragment.getView().getHeight();
                mFilterViewPager.setLayoutParams(pagerParams);
            }
        }
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return true;
    }
    //</editor-fold>

    private boolean trendingListIsAtTop()
    {
        return mTrendingGridView == null ||
                mTrendingGridView.getChildCount() == 0 ||
                mTrendingGridView.getChildAt(0).getTop() == 0;
    }

    // Filter pager visible state control

    private void setGridViewScrollState(int gridViewScrollState)
    {
        if (gridViewScrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
        {
            // Just started scrolling with touch
            this.gridCumulativeScrollY = awayBy; // So that it starts at this position
        }
        else if (gridViewScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
        {
            if (awayBy < filterPagerEndHeight || filterVisibleBeforeFling || trendingListIsAtTop())
            {
                // Stopped scrolling as the filter pager is fully or partially visible
                // Restore to fully visible
                setAwayBy(0);
            }
            else
            {
                // Stopped scrolling as the filter pager is fully out of view
                // Leave away
                setAwayBy(filterPagerEndHeight);
            }
        }
    }

    private void addTouchScrollY(float touchScrollY)
    {
        this.gridCumulativeScrollY += touchScrollY;
        this.gridCumulativeScrollY = Math.max(0, Math.min(filterPagerEndHeight, this.gridCumulativeScrollY));
        setAwayBy(this.gridCumulativeScrollY);
    }

    private void setAwayBy(float awayBy)
    {
        this.awayBy = awayBy;
        setFilterPagerAway();
    }

    private void setFilterPagerAway()
    {
        if (mFilterViewPager != null)
        {
            filterLayoutParams.setMargins(0, (int) -awayBy, 0, 0);
            mFilterViewPager.setLayoutParams(filterLayoutParams);

            // TODO Test before changing visibility, could be more efficient
            mFilterViewPager.setVisibility(awayBy == filterPagerEndHeight ? View.GONE : View.VISIBLE);
        }
    }

    //<editor-fold desc="Listeners">
    private class TrendingFlagNearEndScrollListener extends FlagNearEndScrollListener
    {
        @Override public void raiseFlag()
        {
            super.raiseFlag();
            loadNewPage(lastLoadedPage + 1);
        }
    }

    private class TrendingSecurityListCacheListener implements DTOCache.Listener<SecurityListType, SecurityIdList>
    {
        @Override public void onDTOReceived(SecurityListType key, SecurityIdList value)
        {
            THLog.d(TAG, "Received key " + key);
            if (lastLoadedPage + 1 != key.page)
            {
                throw new IllegalStateException("We just got a wrong page; last: " + lastLoadedPage + ", received page: " + key.page);
            }
            lastLoadedPage = key.page;
            currentlyLoadingPage = FIRST_PAGE - 1;
            gridScrollListener.lowerFlag();
            if (value == null || value.size() == 0)
            {
                gridScrollListener.deactivate();
            }
            else
            {
                securityIds.addAll(value);
                securityItemViewAdapter.setItems(securityIds);
            }
            setQuerying(false);
            securityItemViewAdapter.notifyDataSetChanged();
        }

        @Override public void onErrorThrown(SecurityListType key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_security_list_info));
            THLog.e(TAG, "Error fetching the list " + key, error);
            currentlyLoadingPage = FIRST_PAGE - 1;
            gridScrollListener.lowerFlag();
            setQuerying(false);
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
        }
    }

    private class TrendingOnGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            if ((gridCumulativeScrollY < filterPagerEndHeight && distanceY > 0) || // In an attempt to bypass useless updates
                    (gridCumulativeScrollY > 0 && distanceY < 0))
            {
                //THLog.d(TAG, "Scroll " + distanceY);
                addTouchScrollY(distanceY);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if (velocityY < 0 || velocityY > MIN_FLING_VELOCITY_Y_FOR_HIDE_FILTER)
            {
                filterVisibleBeforeFling = (awayBy < filterPagerEndHeight);
                setAwayBy(filterPagerEndHeight);
            }
            else if (velocityY > 0)
            {
                setAwayBy(0);
            }
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

    private class TrendingOnResumedListener implements TrendingFilterSelectorFragment.OnResumedListener
    {
        @Override public void onResumed(final Fragment fragment)
        {
            View viewOnResume = getView();
            if (viewOnResume != null) // It may be null after coming out of sleep
            {
                viewOnResume.post(new Runnable()
                {
                    @Override public void run()
                    {
                        resizeViewPager(fragment);
                    }
                });
            }
        }
    }

    private class TrendingOnPreviousNextListener implements TrendingFilterSelectorFragment.OnPreviousNextListener
    {
        @Override public void onNextRequested()
        {
            if (mFilterViewPager != null)
            {
                mFilterViewPager.setCurrentItem((mFilterViewPager.getCurrentItem() + 1) % TrendingFilterSelectorUtil.FRAGMENT_COUNT /*mFilterViewPager.getChildCount()*/);
            }
        }

        @Override public void onPreviousRequested()
        {
            if (mFilterViewPager != null)
            {
                mFilterViewPager.setCurrentItem((mFilterViewPager.getCurrentItem() - 1) % TrendingFilterSelectorUtil.FRAGMENT_COUNT /*mFilterViewPager.getChildCount()*/);
            }
        }
    }

    private class TrendingOnPositionedExchangeSelectionChangedListener implements TrendingFilterPagerAdapter.OnPositionedExchangeSelectionChangedListener
    {
        @Override public void onExchangeSelectionChanged(int fragmentPosition, ExchangeStringId exchangeId)
        {
            selectedExchangeStringIds[fragmentPosition] = exchangeId;
            if (fragmentPosition == filterPageSelected)
            {
                startAnew();
                loadNewPage(FIRST_PAGE);
            }
        }
    }

    private class TrendingFilterOnPageChangeListener implements ViewPager.OnPageChangeListener
    {
        @Override public void onPageScrolled(int i, float v, int i2)
        {
            // TODO
        }

        @Override public void onPageSelected(int position)
        {
            filterPageSelected = position;
            startAnew();
            loadNewPage(FIRST_PAGE);
        }

        @Override public void onPageScrollStateChanged(int state)
        {
            // TODO
        }
    }
    //</editor-fold>
}
