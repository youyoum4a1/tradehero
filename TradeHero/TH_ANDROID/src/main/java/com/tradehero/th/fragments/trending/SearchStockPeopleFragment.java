package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEndScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.key.SearchSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserBaseKeyList;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.security.SecurityItemViewAdapter;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.persistence.user.UserBaseKeyListCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 9/18/13 Time: 12:09 PM To change this template use File | Settings | File Templates. */
public final class SearchStockPeopleFragment extends DashboardFragment
{
    public final static String BUNDLE_KEY_SEARCH_STRING = SearchStockPeopleFragment.class.getName() + ".searchString";
    public final static String BUNDLE_KEY_SEARCH_TYPE = SearchStockPeopleFragment.class.getName() + ".searchType";
    public final static String BUNDLE_KEY_PAGE = SearchStockPeopleFragment.class.getName() + ".page";
    public final static String BUNDLE_KEY_PER_PAGE = SearchStockPeopleFragment.class.getName() + ".perPage";
    public final static String BUNDLE_KEY_CALLER_FRAGMENT = SearchStockPeopleFragment.class.getName() + ".nextFragment";

    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 15;
    public final static long DELAY_REQUEST_DATA_MILLI_SEC = 1000;

    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject Lazy<UserBaseKeyListCache> userBaseKeyListCache;
    @Inject LocalyticsSession localyticsSession;

    @InjectView(R.id.search_empty_view) TextView searchEmptyView;
    @InjectView(R.id.listview) ListView listView;
    @InjectView(R.id.progress) ProgressBar mProgress;

    private int perPage = DEFAULT_PER_PAGE;
    private FlagNearEndScrollListener nearEndScrollListener;

    private TrendingSearchType mSearchType = TrendingSearchType.STOCKS;
    private EditText mSearchTextField;
    private String mSearchText;
    private SearchTextWatcher mSearchTextWatcher;

    private boolean isQuerying;

    private SecurityIdListCacheListener securityIdListCacheListener;
    private PeopleListCacheListener peopleListCacheListener;

    private SecurityItemViewAdapter<SecurityCompactDTO> securityItemViewAdapter;
    private PeopleItemViewAdapter peopleItemViewAdapter;

    private DTOCache.GetOrFetchTask<SecurityListType, SecurityIdList> securitySearchTask;
    private DTOCache.GetOrFetchTask<UserListType, UserBaseKeyList> peopleSearchTask;

    private List<SecurityId> securityIds;
    private List<UserBaseKey> userBaseKeys;

    private int currentlyLoadingPage;
    private int lastLoadedPage;
    private boolean shouldDisableSearchTypeOption;
    private MenuItem currentSearchMode;
    private MenuItem searchPeople;
    private MenuItem searchStock;
    private Runnable requestDataTask;

    private Object selectedItem;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        collectParameters(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        collectParameters(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search_stock, container, false);
        ButterKnife.inject(this, view);
        initViews(view, inflater);
        return view;
    }

    protected void initViews(View view, LayoutInflater inflater)
    {
        nearEndScrollListener = new SearchFlagNearEndScrollListener();
        securityIdListCacheListener = new SecurityIdListCacheListener();
        peopleListCacheListener = new PeopleListCacheListener();

        securityItemViewAdapter = new SimpleSecurityItemViewAdapter(getActivity(), inflater, R.layout.search_security_item);
        peopleItemViewAdapter = new PeopleItemViewAdapter(getActivity(), inflater, R.layout.search_people_item);

        if (listView != null)
        {
            listView.setAdapter(securityItemViewAdapter);
            listView.setOnItemClickListener(new SearchOnItemClickListener());
            listView.setOnScrollListener(nearEndScrollListener);
            listView.setEmptyView(searchEmptyView);
        }
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        collectParameters(savedInstanceState);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        if (!shouldDisableSearchTypeOption)
        {
            inflater.inflate(R.menu.search_stock_people_menu, menu);

            currentSearchMode = menu.findItem(R.id.current_search_mode);
            searchPeople = menu.findItem(R.id.search_people);
            searchStock = menu.findItem(R.id.search_stock);
        }

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME);
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_stock_menu, menu);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        MenuItem securitySearchElements = menu.findItem(R.id.security_search_menu_elements);

        mSearchTextWatcher = new SearchTextWatcher();
        mSearchTextField = (EditText) securitySearchElements.getActionView().findViewById(R.id.search_field);
        if (mSearchTextField != null)
        {
            mSearchTextField.addTextChangedListener(mSearchTextWatcher);
        }

        if (mSearchType == TrendingSearchType.PEOPLE && searchPeople != null)
        {
            searchPeople.setChecked(true);
        }
        else if (mSearchType == TrendingSearchType.STOCKS && searchStock != null)
        {
            searchStock.setChecked(true);
        }
        updateSearchType();

        populateSearchActionBar();
    }

    @Override public void onDestroyOptionsMenu()
    {
        if (mSearchTextField != null)
        {
            mSearchTextField.removeTextChangedListener(mSearchTextWatcher);
        }
        mSearchTextField = null;
        mSearchTextWatcher = null;
        super.onDestroyOptionsMenu();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.search_people:
            case R.id.search_stock:
                boolean checkedBefore = item.isChecked();
                item.setChecked(true);
                if (!checkedBefore)
                {
                    onSearchTypeChanged();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSearchTypeChanged()
    {
        updateSearchType();
        updateVisibilities();
        startAnew();
        requestData();
    }

    private void updateSearchType()
    {
        // check current search type
        mSearchType = TrendingSearchType.STOCKS;
        if (searchPeople != null && searchPeople.isChecked())
        {
            mSearchType = TrendingSearchType.PEOPLE;
        }
        else if (searchStock != null && searchStock.isChecked())
        {
            mSearchType = TrendingSearchType.STOCKS;
        }
        if (currentSearchMode != null)
        {
            currentSearchMode.setIcon(mSearchType.searchDrawableResId);
        }

        if (listView != null)
        {
            if (mSearchType == TrendingSearchType.PEOPLE)
            {
                listView.setAdapter(peopleItemViewAdapter);
            }
            else if (mSearchType == TrendingSearchType.STOCKS)
            {
                listView.setAdapter(securityItemViewAdapter);
            }
        }
    }
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();

        populateSearchActionBar();
        initialPopulateOnCreate();

        Bundle arguments = getArguments();
        if (arguments != null)
        {
            String callerClassName = arguments.getString(BUNDLE_KEY_CALLER_FRAGMENT);
            if (callerClassName != null && callerClassName.equalsIgnoreCase(WatchlistPositionFragment.class.getName()))
            {
                shouldDisableSearchTypeOption = true;
            }
        }
    }

    protected void startAnew()
    {
        this.lastLoadedPage = FIRST_PAGE - 1;
        this.currentlyLoadingPage = FIRST_PAGE - 1;
        this.securityIds = new ArrayList<>();
        this.userBaseKeys = new ArrayList<>();
        if (nearEndScrollListener != null)
        {
            nearEndScrollListener.lowerFlag();
            nearEndScrollListener.activate();
        }
    }

    private void populateSearchActionBar()
    {
        if (mSearchTextField != null)
        {
            mSearchTextField.setText(mSearchText);
        }
    }

    private void initialPopulateOnCreate()
    {
        if (mSearchType == TrendingSearchType.STOCKS)
        {
            if (securityIds != null && securityIds.size() > 0)
            {
                linkWith(securityIds, true, (SecurityId) null);
            }
            else
            {
                startAnew();
                requestData();
            }
        }
        else if (mSearchType == TrendingSearchType.PEOPLE)
        {
            if (userBaseKeys != null && userBaseKeys.size() > 0)
            {
                linkWith(userBaseKeys, true);
            }
            else
            {
                startAnew();
                requestData();
            }
        }
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        putParameters(outState);
    }

    @Override public void onDestroyView()
    {
        DeviceUtil.dismissKeyboard(getActivity());

        if (listView != null)
        {
            listView.setOnItemClickListener(null);
            listView.setOnScrollListener(null);
        }

        View rootView = getView();
        if (rootView != null && requestDataTask != null)
        {
            rootView.removeCallbacks(requestDataTask);
        }

        securityItemViewAdapter = null;
        peopleItemViewAdapter = null;
        listView = null;

        cancelSearchTasks();
        securityIdListCacheListener = null;
        peopleListCacheListener = null;

        super.onDestroyView();
    }

    private void cancelSearchTasks()
    {
        detachSecuritySearchTask();
        detachPeopleSearchTask();
        isQuerying = false;
    }

    protected void detachSecuritySearchTask()
    {
        if (securitySearchTask != null)
        {
            securitySearchTask.setListener(null);
        }
        securitySearchTask = null;
    }

    protected void detachPeopleSearchTask()
    {
        if (peopleSearchTask != null)
        {
            peopleSearchTask.setListener(null);
        }
        peopleSearchTask = null;
    }

    protected void scheduleRequestData()
    {
        View view = getView();
        if (view != null)
        {
            if (requestDataTask != null)
            {
                view.removeCallbacks(requestDataTask);
            }

            requestDataTask = new Runnable()
            {
                @Override public void run()
                {
                    startAnew();
                    requestData();
                }
            };
            view.postDelayed(requestDataTask, DELAY_REQUEST_DATA_MILLI_SEC);
        }
    }

    protected void loadNewPage(int newPage)
    {
        if (newPage != lastLoadedPage + 1)
        {
            Timber.e("Will not load newPage %d, lastLoadedPage %d", newPage, lastLoadedPage, new Exception());
        }
        if (currentlyLoadingPage != FIRST_PAGE - 1 && currentlyLoadingPage != newPage)
        {
            Timber.e("This page is already loading another one %d, will not load %d", currentlyLoadingPage, newPage, new Exception());
        }
        cancelSearchTasks();
        currentlyLoadingPage = newPage;
        requestData();
    }

    protected void requestData()
    {
        if (mSearchType == null)
        {
            // Do nothing
        }
        else if (mSearchType == TrendingSearchType.STOCKS)
        {
            requestSecurities();
        }
        else if (mSearchType == TrendingSearchType.PEOPLE)
        {
            requestPeople();
        }
        else
        {
            throw new IllegalArgumentException("Unhandled SearchType." + mSearchType);
        }
    }

    private void requestSecurities()
    {
        if (mSearchText != null && !mSearchText.isEmpty())
        {
            cancelSearchTasks();
            SecurityListType searchSecurityListType = makeSearchSecurityListType(lastLoadedPage + 1);
            setQuerying(true);
            currentlyLoadingPage = lastLoadedPage + 1;
            securitySearchTask = securityCompactListCache.get().getOrFetch(searchSecurityListType, securityIdListCacheListener);
            securitySearchTask.execute();
        }
    }

    private void requestPeople()
    {
        if (mSearchText != null && !mSearchText.isEmpty())
        {
            cancelSearchTasks();
            UserListType searchUserListType = makeSearchUserListType(lastLoadedPage + 1);
            setQuerying(true);
            currentlyLoadingPage = lastLoadedPage + 1;
            peopleSearchTask = userBaseKeyListCache.get().getOrFetch(searchUserListType, peopleListCacheListener);
            peopleSearchTask.execute();
        }
    }

    private void linkWith(List<SecurityId> securityIds, boolean andDisplay, SecurityId typeQualifier)
    {
        this.securityIds = securityIds;

        if (securityItemViewAdapter != null)
        {
            if (securityIds == null)
            {
                securityItemViewAdapter.setItems(null);
            }
            else
            {
                securityItemViewAdapter.setItems(securityCompactCache.get().get(securityIds));
            }
            securityItemViewAdapter.notifyDataSetChanged();
            updateVisibilities();
        }
    }

    private void linkWith(final List<UserBaseKey> users, boolean andDisplay)
    {
        this.userBaseKeys = users;

        if (peopleItemViewAdapter != null)
        {
            peopleItemViewAdapter.setItems(users);
            if (peopleItemViewAdapter != null)
            {
                peopleItemViewAdapter.notifyDataSetChanged();
            }
            updateVisibilities();
        }
    }

    public void setQuerying(boolean querying)
    {
        isQuerying = querying;
        updateVisibilities();
    }

    private void updateVisibilities()
    {
        if (mProgress != null)
        {
            mProgress.setVisibility(isQuerying ? View.VISIBLE : View.INVISIBLE);
        }
    }

    protected void putParameters(Bundle args)
    {
        if (args != null)
        {
            args.putString(BUNDLE_KEY_SEARCH_TYPE, mSearchType.name());
            args.putString(BUNDLE_KEY_SEARCH_STRING, mSearchText);
            args.putInt(BUNDLE_KEY_PAGE, lastLoadedPage);
            args.putInt(BUNDLE_KEY_PER_PAGE, perPage);
        }
    }

    protected void collectParameters(Bundle args)
    {
        if (args == null)
        {
            args = getArguments();
        }

        if (args != null)
        {
            mSearchType = TrendingSearchType.valueOf(args.getString(BUNDLE_KEY_SEARCH_TYPE, TrendingSearchType.STOCKS.name()));
            mSearchText = args.getString(BUNDLE_KEY_SEARCH_STRING);
            perPage = args.getInt(BUNDLE_KEY_PER_PAGE, DEFAULT_PER_PAGE);
            lastLoadedPage = args.getInt(BUNDLE_KEY_PAGE, FIRST_PAGE);
        }
    }

    private void pushTradeFragmentIn(SecurityId securityId)
    {
        if (securityId == null)
        {
            Timber.e("Cannot handle null SecurityId", new IllegalArgumentException());
            return;
        }
        Bundle args = new Bundle();
        args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        getNavigator().pushFragment(BuySellFragment.class, args);
    }

    protected void pushUserFragmentIn(UserBaseKey userBaseKey)
    {
        if (userBaseKey == null || userBaseKey.key == null)
        {
            Timber.e("Cannot handle null userBaseKey", new IllegalArgumentException());
            return;
        }

        //THToast.show("Disabled for now");
        // TODO put back in

        Bundle args = new Bundle();
        args.putInt(PushableTimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userBaseKey.key);

        getNavigator().pushFragment(PushableTimelineFragment.class, args);
    }

    //<editor-fold desc="Accessors">
    public int getCurrentlyLoadingPage()
    {
        return currentlyLoadingPage;
    }

    public int getLastLoadedPage()
    {
        return lastLoadedPage;
    }

    public int getPerPage()
    {
        return perPage;
    }

    public Object getSelectedItem()
    {
        return selectedItem;
    }

    public SecurityListType makeSearchSecurityListType(int page)
    {
        return new SearchSecurityListType(mSearchText, page, perPage);
    }

    public UserListType makeSearchUserListType(int page)
    {
        return new SearchUserListType(mSearchText, page, perPage);
    }
    //</editor-fold>

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Listeners">
    private class SearchFlagNearEndScrollListener extends FlagNearEndScrollListener
    {
        @Override public void raiseFlag()
        {
            super.raiseFlag();
            loadNewPage(lastLoadedPage + 1);
        }
    }

    private class SearchOnItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            selectedItem = parent.getItemAtPosition(position);

            if (getArguments() != null && getArguments().containsKey(Navigator.BUNDLE_KEY_RETURN_FRAGMENT))
            {
                getNavigator().popFragment();
                return;
            }

            if (selectedItem instanceof SecurityCompactDTO)
            {
                SecurityCompactDTO clickedItem = (SecurityCompactDTO) selectedItem;

                if (shouldDisableSearchTypeOption)
                {
                    // pop out current fragment and push in watchlist edit fragment
                    // TODO remove this hack
                    Bundle args = new Bundle();
                    args.putBundle(WatchlistEditFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, clickedItem.getSecurityId().getArgs());
                    args.putString(Navigator.BUNDLE_KEY_RETURN_FRAGMENT, WatchlistPositionFragment.class.getName());
                    getNavigator().pushFragment(WatchlistEditFragment.class, args);
                }
                else
                {
                    pushTradeFragmentIn(clickedItem.getSecurityId());
                }
            }
            else if (selectedItem instanceof UserBaseKey)
            {
                pushUserFragmentIn((UserBaseKey) selectedItem);
            }
        }
    }

    private class SearchTextWatcher implements TextWatcher
    {
        @Override public void afterTextChanged(Editable editable)
        {
        }

        @Override public void beforeTextChanged(CharSequence charSequence, int start, int count, int after)
        {
        }

        @Override public void onTextChanged(CharSequence charSequence, int start, int before, int count)
        {
            mSearchText = charSequence.toString();
            if (mSearchText == null || mSearchText.isEmpty())
            {
                linkWith(null, true, (SecurityId) null);
                linkWith(null, true);
            }
            else
            {
                scheduleRequestData();
            }
        }
    }

    private class SecurityIdListCacheListener implements DTOCache.Listener<SecurityListType, SecurityIdList>
    {
        @Override public void onDTOReceived(SecurityListType key, SecurityIdList value, boolean fromCache)
        {
            if (lastLoadedPage + 1 != key.getPage())
            {
                throw new IllegalStateException("We just got a wrong page; last: " + lastLoadedPage + ", received page: " + key.getPage());
            }
            Timber.d("Page loaded: %d", lastLoadedPage);
            lastLoadedPage = key.getPage();
            currentlyLoadingPage = FIRST_PAGE - 1;
            nearEndScrollListener.lowerFlag();
            if (value == null || value.size() == 0)
            {
                nearEndScrollListener.deactivate();
                if (lastLoadedPage == FIRST_PAGE)
                {
                    securityItemViewAdapter.setItems(null);
                    searchEmptyView.setText(R.string.trending_search_no_stock_found);
                }
            }
            else
            {
                if (securityIds != null)
                {
                    securityIds.addAll(value);
                }
                securityItemViewAdapter.setItems(securityCompactCache.get().get(securityIds));
            }
            setQuerying(false);
            securityItemViewAdapter.notifyDataSetChanged();

            localyticsSession.tagEvent(LocalyticsConstants.SearchResult_Stock);
        }

        @Override public void onErrorThrown(SecurityListType key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_security_list_info));
            Timber.e("Error fetching the list of securities " + key, error);
        }
    }

    private class PeopleListCacheListener implements DTOCache.Listener<UserListType, UserBaseKeyList>
    {
        @Override public void onDTOReceived(UserListType key, UserBaseKeyList value, boolean fromCache)
        {
            SearchUserListType castedKey = (SearchUserListType) key;
            if (lastLoadedPage + 1 != castedKey.page)
            {
                throw new IllegalStateException("We just got a wrong page; last: " + lastLoadedPage + ", received page: " + castedKey.page);
            }
            lastLoadedPage = castedKey.page;
            currentlyLoadingPage = FIRST_PAGE - 1;
            nearEndScrollListener.lowerFlag();
            if (value == null || value.size() == 0)
            {
                nearEndScrollListener.deactivate();
                if (lastLoadedPage == FIRST_PAGE)
                {
                    peopleItemViewAdapter.setItems(null);
                    searchEmptyView.setText(R.string.trending_search_no_people_found);
                }
            }
            else
            {
                userBaseKeys.addAll(value);
                peopleItemViewAdapter.setItems(userBaseKeys);
            }
            setQuerying(false);
            peopleItemViewAdapter.notifyDataSetChanged();

            localyticsSession.tagEvent(LocalyticsConstants.SearchResult_User);
        }

        @Override public void onErrorThrown(UserListType key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_people_list_info));
            Timber.e("Error fetching the list of people " + key, error);
        }
    }
    //</editor-fold>
}