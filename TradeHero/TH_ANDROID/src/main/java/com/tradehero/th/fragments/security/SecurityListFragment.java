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
import android.widget.ProgressBar;
import android.widget.WrapperListAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.common.widget.FlagNearEndScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.loaders.PagedDTOCacheLoader;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;

abstract public class SecurityListFragment extends BasePurchaseManagerFragment
{
    public static final String BUNDLE_KEY_PAGE = SecurityListFragment.class.getName() + ".page";

    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 20;
    public static final int DEFAULT_VISIBLE_THRESHOLD = 20;

    public final static float MIN_FLING_VELOCITY_Y_FOR_HIDE_FILTER = 1000f;

    @InjectView(R.id.progress) ProgressBar mProgressSpinner;
    @InjectView(R.id.filter_text) @Optional EditText filterText;
    //@InjectView(R.id.trending_gridview)
    private AbsListView securityListView;

    protected TextWatcher filterTextWatcher;
    protected FlagNearEndScrollListener listViewScrollListener;
    protected GestureDetector listViewGesture;

    protected int perPage = DEFAULT_PER_PAGE;
    protected SecurityItemViewAdapter<SecurityCompactDTO> securityItemViewAdapter;
    protected int firstVisiblePosition = 0;

    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject CurrentUserId currentUserId;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        filterTextWatcher = new SecurityListOnFilterTextWatcher();
    }

    @Override protected void initViews(View view)
    {
        ButterKnife.inject(this, view);

        securityListView = (AbsListView) view.findViewById(R.id.trending_gridview);

        showProgressSpinner(false);

        listViewScrollListener = new SecurityListFlagNearEndScrollListener(DEFAULT_VISIBLE_THRESHOLD);

        // TODO this part is tricky, we have multiple screens using the same kind of data, list of security item data,
        // trending screen is the special one, which will use wrapped adapter which provides security item data AND extra tiles such as survey,
        // earn credit ..., the others which is competition, search stock/people will use normal adapter, which only provides security item data
        // if listview is set to original one, the trending screen will only display normal security item
        ListAdapter adapter = createSecurityItemViewAdapter();

        // TODO ListView should not have to care about whether its ListAdapter is wrapped or not
        if (adapter instanceof WrapperListAdapter)
        {
            securityItemViewAdapter = (SecurityItemViewAdapter<SecurityCompactDTO>) ((WrapperListAdapter) adapter).getWrappedAdapter();
        }
        else
        {
            securityItemViewAdapter = (SecurityItemViewAdapter<SecurityCompactDTO>) adapter;
        }

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

    @Override public void onStop()
    {
        // It comes at the end, so that the listView does not update the view
        //DeviceUtil.dismissKeyboard(getActivity());

        super.onStop();
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
        filterTextWatcher = null;

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

    public AbsListView getSecurityListView()
    {
        return securityListView;
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
            handleSecurityItemReceived(securityIds);

            if (listViewScrollListener != null)
            {
                listViewScrollListener.lowerFlag();
            }
        }

        @Override public void onLoaderReset(Loader<SecurityIdList> securityIdListLoader)
        {
            // TODO
        }
    }

    protected void handleSecurityItemReceived(SecurityIdList securityIds)
    {
        if (securityItemViewAdapter != null)
        {
            // It may have been nullified if coming out
            securityItemViewAdapter.setItems(securityCompactCache.get().get(securityIds));
            securityItemViewAdapter.notifyDataSetChanged();
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
            securityItemViewAdapter.getFilter().filter(charSequence);
        }

        @Override public void afterTextChanged(Editable editable)
        {

        }
    }
}
