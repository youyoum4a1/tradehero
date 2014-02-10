package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.WrapperListAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.widget.FlagNearEndScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.loaders.PagedDTOCacheLoader;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import javax.inject.Inject;

abstract public class SecurityListFragment extends BasePurchaseManagerFragment
{
    private final static String TAG = SecurityListFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_PAGE = SecurityListFragment.class.getName() + ".page";

    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 20;
    public static final int DEFAULT_VISIBLE_THRESHOLD = 20;

    public final static float MIN_FLING_VELOCITY_Y_FOR_HIDE_FILTER = 1000f;

    @InjectView(R.id.progress_spinner) ProgressBar mProgressSpinner;
    @InjectView(R.id.filter_text) @Optional EditText filterText;
    @InjectView(R.id.trending_gridview) AbsListView securityListView;

    protected TextWatcher filterTextWatcher;
    protected FlagNearEndScrollListener listViewScrollListener;
    protected GestureDetector listViewGesture;

    protected int perPage = DEFAULT_PER_PAGE;
    protected SecurityItemViewAdapter<SecurityCompactDTO> securityItemViewAdapter;
    protected int firstVisiblePosition = 0;

    @Inject protected SecurityCompactCache securityCompactCache;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.filterTextWatcher = new SecurityListOnFilterTextWatcher();
    }

    @Override protected void initViews(View view)
    {
        ButterKnife.inject(this, view);

        if (mProgressSpinner != null)
        {
            mProgressSpinner.setVisibility(View.GONE);
        }

        listViewScrollListener = new SecurityListFlagNearEndScrollListener(DEFAULT_VISIBLE_THRESHOLD);

        // TODO this part is tricky, we have multiple screens using the same kind of data, list of security item data,
        // trending screen is the special one, which will use wrapped adapter which provides security item data AND extra tiles such as survey,
        // earn credit ..., the others which is competition, search stock/people will use normal adapter, which only provides security item data
        // if listview is set to original one, the trending screen will only display normal security item
        ListAdapter adapter = createSecurityItemViewAdapter();
        securityItemViewAdapter = (SecurityItemViewAdapter<SecurityCompactDTO>)
                (adapter instanceof WrapperListAdapter ? ((WrapperListAdapter) adapter).getWrappedAdapter() : adapter);

        if (securityListView != null)
        {
            securityListView.setOnItemClickListener(createOnItemClickListener());
            securityListView.setOnScrollListener(listViewScrollListener);
            securityListView.setAdapter(adapter);
            listViewGesture = new GestureDetector(getActivity(), new SecurityListOnGestureListener());
            securityListView.setOnTouchListener(new SecurityListOnTouchListener());
        }

        if (this.filterText != null)
        {
            this.filterText.addTextChangedListener(this.filterTextWatcher);
        }
    }

    abstract protected ListAdapter createSecurityItemViewAdapter();

    abstract protected AdapterView.OnItemClickListener createOnItemClickListener();

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        prepareSecurityLoader();
    }

    @Override public void onResume()
    {
        //THLog.d(TAG, "onResume");
        super.onResume();
        securityListView.setSelection(Math.min(firstVisiblePosition, securityListView.getCount()));
        if (listViewScrollListener != null)
        {
            listViewScrollListener.lowerFlag();
            listViewScrollListener.activate();
        }
        //load();
    }

    @Override public void onPause()
    {
        firstVisiblePosition = securityListView.getFirstVisiblePosition();
        if (listViewScrollListener != null)
        {
            listViewScrollListener.deactivate();
        }
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        //THLog.d(TAG, "onDestroyView");
        if (securityListView != null)
        {
            securityListView.setOnItemClickListener(null);
            securityListView.setOnScrollListener(null);
            securityListView.setOnTouchListener(null);
        }
        securityListView = null;
        securityItemViewAdapter = null;
        listViewScrollListener = null;
        queryingChangedListener = null;
        noMorePagesChangedListener = null;

        if (this.filterText != null)
        {
            this.filterText.removeTextChangedListener(this.filterTextWatcher);
        }
        this.filterText = null;

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.filterTextWatcher = null;
        super.onDestroy();
    }

    protected void prepareSecurityLoader()
    {
        getActivity().getSupportLoaderManager().initLoader(getSecurityIdListLoaderId(), null, new SecurityListLoaderCallback());
    }

    abstract public int getSecurityIdListLoaderId();

    protected void postIfCan(final Runnable runnable)
    {
        View fragmentView = getView();
        if (fragmentView != null)
        {
            fragmentView.post(runnable);
        }
    }

    protected void forceInitialLoad()
    {
        Loader loader = getActivity().getSupportLoaderManager().getLoader(getSecurityIdListLoaderId());
        SecurityListPagedLoader pagedLoader = (SecurityListPagedLoader) loader;
        pagedLoader.setQueryKey(getInitialSecurityListType());
        if (this.listViewScrollListener != null)
        {
            this.listViewScrollListener.activate();
            this.listViewScrollListener.raiseFlag();
        }
        loader.forceLoad();
    }

    protected void load()
    {
        Loader loader = getActivity().getSupportLoaderManager().getLoader(getSecurityIdListLoaderId());
        loader.startLoading();
    }

    protected void loadNextPage()
    {
        Loader loader = getActivity().getSupportLoaderManager().getLoader(getSecurityIdListLoaderId());
        SecurityListPagedLoader pagedLoader = (SecurityListPagedLoader) loader;
        pagedLoader.loadNextPage();
        loader.startLoading();
    }

    public SecurityListType getInitialSecurityListType()
    {
        return getSecurityListType(FIRST_PAGE - 1);
    }

    abstract public SecurityListType getSecurityListType(int page);

    protected void showProgressSpinner(boolean flag)
    {
        if (mProgressSpinner != null)
        {
            mProgressSpinner.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        }
    }

    //<editor-fold desc="Listeners">
    protected class SecurityListFlagNearEndScrollListener extends FlagNearEndScrollListener
    {
        public final String TAG = SecurityListFlagNearEndScrollListener.class.getSimpleName();

        public SecurityListFlagNearEndScrollListener(int visibleThreshold)
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

    private class SecurityListOnGestureListener extends GestureDetector.SimpleOnGestureListener
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

    protected class SecurityListOnTouchListener implements View.OnTouchListener
    {
        @Override public boolean onTouch(View view, MotionEvent motionEvent)
        {
            return listViewGesture.onTouchEvent(motionEvent);
        }
    }

    protected PagedDTOCacheLoader.OnQueryingChangedListener queryingChangedListener = new PagedDTOCacheLoader.OnQueryingChangedListener()
    {
        @Override public void onQueryingChanged(boolean querying)
        {
            postIfCan(new Runnable()
            {
                @Override public void run()
                {
                    SecurityListPagedLoader securityListPagedLoader =
                            (SecurityListPagedLoader) (Loader) getActivity().getSupportLoaderManager().getLoader(getSecurityIdListLoaderId());
                    showProgressSpinner(securityListPagedLoader.isQuerying());
                }
            });
        }
    };

    protected PagedDTOCacheLoader.OnNoMorePagesChangedListener noMorePagesChangedListener = new PagedDTOCacheLoader.OnNoMorePagesChangedListener()
    {
        @Override public void onNoMorePagesChanged(boolean noMorePages)
        {
            if (listViewScrollListener != null && !noMorePages)
            {
                // There are more pages, so we want to raise the flag  when at the end.
                listViewScrollListener.lowerFlag();
            }
            else if (listViewScrollListener != null)
            {
                listViewScrollListener.deactivate();
            }
        }
    };

    protected class SecurityListLoaderCallback implements LoaderManager.LoaderCallbacks<SecurityIdList>
    {
        @Override public Loader<SecurityIdList> onCreateLoader(int id, Bundle args)
        {
            if (id == getSecurityIdListLoaderId())
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
            if (securityItemViewAdapter != null)
            {
                // It may have been nullified if coming out
                securityItemViewAdapter.setItems(securityCompactCache.get(securityIds));
                securityItemViewAdapter.notifyDataSetChanged();
            }
            if (listViewScrollListener != null)
            {
                listViewScrollListener.lowerFlag();
            }
        }

        @Override public void onLoaderReset(Loader<SecurityIdList> securityIdListLoader)
        {
            THLog.d(TAG, "SecurityListLoaderCallback.onLoaderReset");
            // TODO
        }
    }
    //</editor-fold>

    protected class SecurityListOnFilterTextWatcher implements TextWatcher
    {
        @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {

        }

        @Override public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {
            THLog.d(TAG, "Text: " + charSequence);
            securityItemViewAdapter.getFilter().filter(charSequence);
        }

        @Override public void afterTextChanged(Editable editable)
        {

        }
    }
}
