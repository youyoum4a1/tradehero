package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.widget.FlagNearEndScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.trending.SecurityItemViewAdapter;
import com.tradehero.th.loaders.PagedDTOCacheLoader;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;

abstract public class SecurityListFragment extends BasePurchaseManagerFragment
{
    private final static String TAG = SecurityListFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_PAGE = SecurityListFragment.class.getName() + ".page";

    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 20;
    public static final int DEFAULT_VISIBLE_THRESHOLD = 20;

    public final static float MIN_FLING_VELOCITY_Y_FOR_HIDE_FILTER = 1000f;

    protected PagedDTOCacheLoader.OnQueryingChangedListener queryingChangedListener;
    protected PagedDTOCacheLoader.OnNoMorePagesChangedListener noMorePagesChangedListener;

    protected ProgressBar mProgressSpinner;
    protected AbsListView securityListView;
    protected FlagNearEndScrollListener listViewScrollListener;
    protected GestureDetector listViewGesture;

    protected int perPage = DEFAULT_PER_PAGE;
    protected SecurityItemViewAdapter securityItemViewAdapter;
    protected int firstVisiblePosition = 0;

    @Override protected void initViews(View view)
    {
        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);
        if (mProgressSpinner != null)
        {
            mProgressSpinner.setVisibility(View.GONE);
        }

        listViewScrollListener = new SecurityListFlagNearEndScrollListener(DEFAULT_VISIBLE_THRESHOLD);
        queryingChangedListener = new SecurityListOnQueryingChangedListener();
        noMorePagesChangedListener = new SecurityListOnNoMorePagesChangedListener();
        securityItemViewAdapter = createSecurityItemViewAdapter();
        securityListView = (AbsListView) view.findViewById(R.id.trending_gridview);
        if (securityListView != null)
        {
            securityListView.setOnScrollListener(listViewScrollListener);
            securityListView.setAdapter(securityItemViewAdapter);
            listViewGesture = new GestureDetector(getActivity(), new SecurityListOnGestureListener());
            securityListView.setOnTouchListener(new SecurityListOnTouchListener());
        }
    }

    abstract protected SecurityItemViewAdapter createSecurityItemViewAdapter();

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(getSecurityIdListLoaderId(), null, new SecurityListLoaderCallback());
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

        super.onDestroyView();
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

    protected class SecurityListOnQueryingChangedListener implements PagedDTOCacheLoader.OnQueryingChangedListener
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
    }

    protected class SecurityListOnNoMorePagesChangedListener implements PagedDTOCacheLoader.OnNoMorePagesChangedListener
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
    }

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
                securityItemViewAdapter.setItems(securityIds);
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
}
