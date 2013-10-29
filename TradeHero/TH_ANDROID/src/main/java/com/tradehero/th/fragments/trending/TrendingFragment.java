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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.trending.SecurityItemViewAdapter;
import com.tradehero.th.adapters.trending.TrendingFilterPagerAdapter;
import com.tradehero.th.api.market.ExchangeStringId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.api.security.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.TrendingPriceSecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;
import com.tradehero.th.api.security.TrendingVolumeSecurityListType;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.trade.AbstractTradeFragment;
import com.tradehero.th.fragments.trade.TradeFragment;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

public class TrendingFragment extends DashboardFragment
        implements DTOCache.Listener<SecurityListType, SecurityIdList>,
        TrendingFilterPagerAdapter.OnPositionedExchangeSelectionChangedListener,
        TrendingFilterSelectorFragment.OnPreviousNextListener
{
    private final static String TAG = TrendingFragment.class.getSimpleName();

    public final static String BUNDLE_KEY_FILTER_PAGE = TrendingFragment.class.getName() + ".filterPage";
    public final static String BUNDLE_KEY_SELECTED_EXCHANGE_NAMES = TrendingFragment.class.getName() + ".selectedExchangeNames";

    public final static float MIN_FLING_VELOCITY_Y_FOR_HIDE_FILTER = 1000f;

    private View actionBar;
    private ImageView mBullIcon;
    private TextView mHeaderText;
    private ImageButton mSearchBtn;

    private ViewPager mFilterViewPager;
    private TrendingFilterPagerAdapter mTrendingFilterPagerAdapter;
    private int filterPagerEndHeight = 80;
    private RelativeLayout.LayoutParams filterLayoutParams;

    private ProgressBar mProgressSpinner;
    private AbsListView mTrendingGridView;
    private float gridCumulativeScrollY = 0;
    private float awayBy = 0;
    private boolean filterVisibleBeforeFling = true;
    private GestureDetector gridViewGesture;

    private int filterPageSelected = 0;
    private ExchangeStringId[] selectedExchangeStringIds = new ExchangeStringId[TrendingFilterPagerAdapter.FRAGMENT_COUNT];
    private boolean isQuerying;
    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    private DTOCache.GetOrFetchTask<SecurityIdList> trendingTask;
    private List<SecurityCompactDTO> securityCompactDTOs;
    protected SecurityItemViewAdapter securityItemViewAdapter;
    protected TrendingFilterSelectorFragment.OnResumedListener trendingFilterSelectorResumedListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        THLog.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.i(TAG, "onCreateView");
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
        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);

        securityItemViewAdapter = new SecurityItemViewAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.trending_grid_item);
        mTrendingGridView = (AbsListView) view.findViewById(R.id.trending_gridview);
        initTrendingGidView();

        mBullIcon = (ImageView) view.findViewById(R.id.logo_img);

        THLog.i(TAG, "onActivityCreated");

        if (securityCompactDTOs != null && securityCompactDTOs.size() > 0)
        {
            setDataAdapterToGridView(securityCompactDTOs);
        }
        else
        {
            refreshGridView();
        }

        if (mTrendingFilterPagerAdapter == null)
        {
            mTrendingFilterPagerAdapter = new TrendingFilterPagerAdapter(getActivity(), getFragmentManager());
            trendingFilterSelectorResumedListener = new TrendingFilterSelectorFragment.OnResumedListener()
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
            };
            mTrendingFilterPagerAdapter.setOnResumedListener(trendingFilterSelectorResumedListener);
            mTrendingFilterPagerAdapter.setOnPreviousNextListener(this);
            mTrendingFilterPagerAdapter.setOnPositionedExchangeSelectionChangedListener(this);
        }
        mFilterViewPager = (ViewPager) view.findViewById(R.id.trending_filter_pager);

        if (mFilterViewPager != null)
        {
            mFilterViewPager.setAdapter(mTrendingFilterPagerAdapter);
            mFilterViewPager.setOnPageChangeListener(createFilterPageChangeListener());
        }
    }

    private void initTrendingGidView()
    {
        if (mTrendingGridView != null)
        {
            mTrendingGridView.setAdapter(securityItemViewAdapter);

            mTrendingGridView.setOnItemClickListener(new OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) parent.getItemAtPosition(position);
                    Bundle args = securityCompactDTO.getSecurityId().getArgs();
                    // TODO use other positions
                    args.putInt(AbstractTradeFragment.BUNDLE_KEY_POSITION_INDEX, AbstractTradeFragment.DEFAULT_POSITION_INDEX);
                    navigator.pushFragment(TradeFragment.class, securityCompactDTO.getSecurityId().getArgs());
                }
            });

            gridViewGesture = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener()
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
            });

            mTrendingGridView.setOnTouchListener(new View.OnTouchListener()
            {
                @Override public boolean onTouch(View view, MotionEvent motionEvent)
                {
                    return gridViewGesture.onTouchEvent(motionEvent);
                }
            });

            mTrendingGridView.setOnScrollListener(new AbsListView.OnScrollListener()
            {
                @Override public void onScrollStateChanged(AbsListView absListView, int i)
                {
                    setGridViewScrollState(i);
                }

                @Override public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                {
                }
            });

            if (mTrendingGridView.getCount() == 0)
            {
                showProgressSpinner(true);
            }
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        THLog.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
        createHeaderActionBar(menu, inflater);
    }

    private void createHeaderActionBar(Menu menu, MenuInflater inflater)
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSherlockActivity().getSupportActionBar().setCustomView(R.layout.topbar_trending);

        actionBar = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBar.findViewById(R.id.header_txt)).setText(R.string.header_trending);

        mSearchBtn = (ImageButton) actionBar.findViewById(R.id.btn_search);
        if (mSearchBtn != null)
        {
            mSearchBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    navigator.pushFragment(SearchStockPeopleFragment.class);
                }
            });
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        securityItemViewAdapter.notifyDataSetChanged();
        displayFilterPager();
        filterPagerEndHeight =(int) getResources().getDimension(R.dimen.trending_filter_view_pager_height);
        if (mFilterViewPager != null)
        {
            filterLayoutParams = (RelativeLayout.LayoutParams) mFilterViewPager.getLayoutParams();
            setAwayBy(0);
        }
    }

    @Override public void onPause()
    {
        setAwayBy(0);
        super.onPause();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
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

    @Override public void onDestroyOptionsMenu()
    {
        THLog.d(TAG, "onDestroyOptionsMenu");
        if (mSearchBtn != null)
        {
            mSearchBtn.setOnClickListener(null);
        }
        mSearchBtn = null;
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        THLog.d(TAG, "onDestroyView");
        if (mTrendingGridView != null)
        {
            mTrendingGridView.setOnItemClickListener(null);
        }
        mTrendingGridView = null;

        if (trendingTask != null)
        {
            trendingTask.cancel(false);
        }
        trendingTask = null;

        if (mTrendingFilterPagerAdapter != null)
        {
            mTrendingFilterPagerAdapter.setOnPreviousNextListener(null);
            mTrendingFilterPagerAdapter.setOnResumedListener(null);
            mTrendingFilterPagerAdapter.setOnPositionedExchangeSelectionChangedListener(null);
        }
        mTrendingFilterPagerAdapter = null;

        mFilterViewPager = null;
        super.onDestroyView();
    }

    private void setDataAdapterToGridView(List<SecurityCompactDTO> securityCompactDTOs)
    {
        this.securityCompactDTOs = securityCompactDTOs;
        securityItemViewAdapter.setItems(securityCompactDTOs);
        securityItemViewAdapter.notifyDataSetChanged();
        showProgressSpinner(false);
    }

    protected void refreshGridView()
    {
        if (trendingTask != null)
        {
            trendingTask.cancel(false);
            trendingTask = null;
        }
        isQuerying = true;
        trendingTask = securityCompactListCache.get().getOrFetch(getSecurityListType(), false, this);
        trendingTask.execute();
    }

    public void displayFilterPager()
    {
        if (mTrendingFilterPagerAdapter != null)
        {
            getView().post(new Runnable()
            {
                @Override public void run()
                {
                    mTrendingFilterPagerAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public TrendingSecurityListType getSecurityListType()
    {
        TrendingSecurityListType securityListType = null;

        String usableExchangeName = null;
        if (selectedExchangeStringIds[filterPageSelected] != null)
        {
            usableExchangeName = selectedExchangeStringIds[filterPageSelected].key;
            if (usableExchangeName.isEmpty())
            {
                usableExchangeName = null;
            }
        }

        switch (filterPageSelected)
        {
            case TrendingFilterSelectorBasicFragment.POSITION_IN_PAGER:
                securityListType = usableExchangeName == null ?
                        new TrendingBasicSecurityListType() :
                        new TrendingBasicSecurityListType(usableExchangeName);
                break;
            case TrendingFilterSelectorVolumeFragment.POSITION_IN_PAGER:
                securityListType = usableExchangeName == null ?
                        new TrendingVolumeSecurityListType() :
                        new TrendingVolumeSecurityListType(usableExchangeName);
                break;
            case TrendingFilterSelectorPriceFragment.POSITION_IN_PAGER:
                securityListType = usableExchangeName == null ?
                        new TrendingPriceSecurityListType() :
                        new TrendingPriceSecurityListType(usableExchangeName);
                break;
            default:
                THLog.d(TAG, "getSecurityListType: Unhandled filterPageSelector: " + filterPageSelected);
                securityListType = new TrendingBasicSecurityListType();
        }

        // TODO set exchange selection

        return securityListType;
    }

    //<editor-fold desc="DTOCache.Listener<SecurityListType, SecurityIdList>">
    @Override public void onDTOReceived(SecurityListType key, SecurityIdList value)
    {
        // To make sure we do not update to some old stuff
        if (key.equals(getSecurityListType()))
        {
            try
            {
                setDataAdapterToGridView(securityCompactCache.get().getOrFetch(value));
            }
            catch (Throwable throwable)
            {
                onErrorThrown(key, throwable);
            }
        }
    }

    @Override public void onErrorThrown(SecurityListType key, Throwable error)
    {
        THToast.show(getString(R.string.error_fetch_security_list_info));
        THLog.e(TAG, "Error fetching the list " + key, error);
    }

    //</editor-fold>

    protected void showProgressSpinner(boolean flag)
    {
        if (mProgressSpinner != null)
        {
            mProgressSpinner.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private ViewPager.OnPageChangeListener createFilterPageChangeListener()
    {
        return new ViewPager.OnPageChangeListener()
        {
            @Override public void onPageScrolled(int i, float v, int i2)
            {
                // TODO
            }

            @Override public void onPageSelected(int i)
            {
                filterPageSelected = i;
                refreshGridView();
            }

            @Override public void onPageScrollStateChanged(int i)
            {
                // TODO
            }
        };
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

    //<editor-fold desc="TrendingFilterSelectorFragment.OnPreviousNextListener">
    @Override public void onNextRequested()
    {
        if (mFilterViewPager != null)
        {
            mFilterViewPager.setCurrentItem((mFilterViewPager.getCurrentItem() + 1) % mFilterViewPager.getChildCount());
        }
    }

    @Override public void onPreviousRequested()
    {
        if (mFilterViewPager != null)
        {
            mFilterViewPager.setCurrentItem((mFilterViewPager.getCurrentItem() - 1) % mFilterViewPager.getChildCount());
        }
    }
    //</editor-fold>

    //<editor-fold desc="TrendingFilterPagerAdapter.OnPositionedExchangeSelectionChangedListener">
    @Override public void onExchangeSelectionChanged(int fragmentPosition, ExchangeStringId exchangeId)
    {
        selectedExchangeStringIds[fragmentPosition] = exchangeId;
        refreshGridView();
    }
    //</editor-fold>

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
}
