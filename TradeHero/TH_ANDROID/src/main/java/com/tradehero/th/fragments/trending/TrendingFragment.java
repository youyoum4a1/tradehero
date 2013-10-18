package com.tradehero.th.fragments.trending;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.trending.TrendingAdapter;
import com.tradehero.th.adapters.trending.TrendingFilterPagerAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.api.security.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.TrendingPriceSecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;
import com.tradehero.th.api.security.TrendingVolumeSecurityListType;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.trade.AbstractTradeFragment;
import com.tradehero.th.fragments.trade.TradeFragment;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.widget.trending.TrendingGridView;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

public class TrendingFragment extends DashboardFragment implements DTOCache.Listener<SecurityListType, List<SecurityId>>
{
    private final static String TAG = TrendingFragment.class.getSimpleName();

    private View actionBar;
    private ImageView mBullIcon;
    private TextView mHeaderText;
    private ImageButton mSearchBtn;

    private ViewPager mFilterViewPager;
    private TrendingFilterPagerAdapter mTrendingFilterPagerAdapter;

    private ProgressBar mProgressSpinner;
    private TrendingGridView mTrendingGridView;

    private int filterPageSelected = 0;
    private boolean isQuerying;
    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    private  AsyncTask<Void, Void, List<SecurityId>> trendingTask;
    private List<SecurityCompactDTO> securityCompactDTOs;
    protected TrendingAdapter trendingAdapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        THLog.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        initViews(view);
        return view;
    }

    protected void initViews(View view)
    {
        mTrendingGridView = (TrendingGridView) view.findViewById(R.id.trending_gridview);

        trendingAdapter = new TrendingAdapter(getActivity(), getActivity().getLayoutInflater(), TrendingAdapter.SECURITY_TRENDING_CELL_LAYOUT);
        mTrendingGridView.setAdapter(trendingAdapter);

        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);
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
            mTrendingFilterPagerAdapter.setOnResumedListener(new TrendingFilterSelectorFragment.OnResumedListener()
            {
                @Override public void onResumed(final Fragment fragment)
                {
                    getView().post(new Runnable()
                    {
                        @Override public void run()
                        {
                            resizeViewPager(fragment);
                        }
                    });
                }
            });
        }
        mFilterViewPager = (ViewPager) view.findViewById(R.id.trending_filter_pager);

        if (mFilterViewPager != null)
        {
            mFilterViewPager.setAdapter(mTrendingFilterPagerAdapter);
            // TODO listen to view pager changes
            mFilterViewPager.setOnPageChangeListener(createFilterPageChangeListener());
        }

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

        if (mTrendingGridView.getCount() == 0)
        {
            showProgressSpinner(true);
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
        trendingAdapter.notifyDataSetChanged();
        displayFilterPager();
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
            mTrendingFilterPagerAdapter.setOnResumedListener(null);
        }
        mTrendingFilterPagerAdapter = null;

        mFilterViewPager = null;
        super.onDestroyView();
    }

    private void setDataAdapterToGridView(List<SecurityCompactDTO> securityCompactDTOs)
    {
        this.securityCompactDTOs = securityCompactDTOs;
        trendingAdapter.setItems(securityCompactDTOs);
        trendingAdapter.notifyDataSetChanged();
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
        switch (filterPageSelected)
        {
            case TrendingFilterSelectorBasicFragment.POSITION_IN_PAGER:
                securityListType = new TrendingBasicSecurityListType();
                break;
            case TrendingFilterSelectorVolumeFragment.POSITION_IN_PAGER:
                securityListType = new TrendingVolumeSecurityListType();
                break;
            case TrendingFilterSelectorPriceFragment.POSITION_IN_PAGER:
                securityListType = new TrendingPriceSecurityListType();
                break;
            default:
                THLog.d(TAG, "getSecurityListType: Unhandled filterPageSelector: " + filterPageSelected);
        }

        // TODO set exchange selection

        return securityListType;
    }

    @Override public void onDTOReceived(SecurityListType key, List<SecurityId> value)
    {
        setDataAdapterToGridView(securityCompactCache.get().getOrFetch(value));
    }

    protected void showProgressSpinner(boolean flag)
    {
        mProgressSpinner.setVisibility(getVisibility(flag));
    }

    protected int getVisibility(boolean flag)
    {
        return flag ? View.VISIBLE : View.INVISIBLE;
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

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return true;
    }
    //</editor-fold>
}
