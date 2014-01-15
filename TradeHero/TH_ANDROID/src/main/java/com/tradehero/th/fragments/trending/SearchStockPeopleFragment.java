package com.tradehero.th.fragments.trending;

import android.app.Activity;
import android.graphics.drawable.Drawable;
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
import android.widget.Spinner;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.adapter.SpinnerIconAdapter;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEndScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SearchSecurityListType;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserBaseKeyList;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.persistence.user.UserBaseKeyListCache;
import com.tradehero.th.utils.DeviceUtil;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 9/18/13 Time: 12:09 PM To change this template use File | Settings | File Templates. */
public class SearchStockPeopleFragment extends DashboardFragment
{
    public final static String BUNDLE_KEY_SEARCH_STRING = SearchStockPeopleFragment.class.getName() + ".searchString";
    public final static String BUNDLE_KEY_SEARCH_TYPE = SearchStockPeopleFragment.class.getName() + ".searchType";
    public final static String BUNDLE_KEY_PAGE = SearchStockPeopleFragment.class.getName() + ".page";
    public final static String BUNDLE_KEY_PER_PAGE = SearchStockPeopleFragment.class.getName() + ".perPage";
    public static final String BUNDLE_KEY_CALLER_FRAGMENT = SearchStockPeopleFragment.class.getName() + ".nextFragment";
    private final static String TAG = SearchStockPeopleFragment.class.getSimpleName();

    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 15;
    public final static long DELAY_REQUEST_DATA_MILLI_SEC = 1000;;

    private TextView mNothingYet;
    private ListView mSearchStockListView;
    private ListView mSearchPeopleListView;
    private FlagNearEndScrollListener nearEndScrollListener;
    private ProgressBar mProgressSpinner;
    private int perPage = DEFAULT_PER_PAGE;

    private CharSequence[] dropDownTexts;
    private Drawable[] dropDownIcons;
    private Drawable[] spinnerIcons;
    private View actionBar;
    private SpinnerIconAdapter mSearchTypeSpinnerAdapter;
    private Spinner mSearchTypeSpinner;
    private TrendingSearchType mSearchType = TrendingSearchType.STOCKS;
    private EditText mSearchTextField;
    private String mSearchText;
    private SearchTextWatcher mSearchTextWatcher;

    private Timer requestDataTimer;
    private boolean isQuerying;

    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject Lazy<UserBaseKeyListCache> userBaseKeyListCache;

    private SecurityIdListCacheListener securityIdListCacheListener;
    private DTOCache.GetOrFetchTask<SecurityListType, SecurityIdList> securitySearchTask;
    private List<SecurityId> securityIds;
    private SecurityItemViewAdapter securityItemViewAdapter;
    private PeopleListCacheListener peopleListCacheListener;
    private DTOCache.GetOrFetchTask<UserListType, UserBaseKeyList> peopleSearchTask;
    private List<UserBaseKey> userBaseKeys;
    private PeopleItemViewAdapter peopleItemViewAdapter;

    private int currentlyLoadingPage;
    private int lastLoadedPage;
    private boolean shouldDisableSearchTypeOption;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        if (dropDownTexts == null || dropDownIcons == null)
        {
            dropDownTexts = new CharSequence[TrendingSearchType.values().length];
            dropDownIcons = new Drawable[TrendingSearchType.values().length];
            spinnerIcons = new Drawable[TrendingSearchType.values().length];
            for (TrendingSearchType searchType: TrendingSearchType.values())
            {
                dropDownTexts[searchType.value] = getString(searchType.searchStringResId);
                dropDownIcons[searchType.value] = getResources().getDrawable(searchType.searchDropDownDrawableResId);
                spinnerIcons[searchType.value] = getResources().getDrawable(searchType.searchDrawableResId);
            }
        }
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        collectParameters(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        collectParameters(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search_stock, container, false);
        initViews(view, inflater);
        return view;
    }

    protected void initViews(View view, LayoutInflater inflater)
    {
        mNothingYet = (TextView) view.findViewById(R.id.search_stock_nothing_yet_view);
        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);
        nearEndScrollListener = new SearchFlagNearEndScrollListener();

        securityItemViewAdapter = new SecurityItemViewAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.search_security_item);
        mSearchStockListView = (ListView) view.findViewById(R.id.trending_listview);
        if (mSearchStockListView != null)
        {
            mSearchStockListView.setAdapter(securityItemViewAdapter);
            mSearchStockListView.setOnItemClickListener(new SearchStockOnItemClickListener());
            mSearchStockListView.setOnScrollListener(nearEndScrollListener);
        }

        peopleItemViewAdapter = new PeopleItemViewAdapter(getActivity().getApplicationContext(), inflater, R.layout.search_people_item);
        mSearchPeopleListView = (ListView) view.findViewById(R.id.people_listview);
        if (mSearchPeopleListView != null)
        {
            mSearchPeopleListView.setAdapter(peopleItemViewAdapter);
            mSearchPeopleListView.setOnItemClickListener(new SearchPeopleOnItemClickListener());
            mSearchPeopleListView.setOnScrollListener(nearEndScrollListener);
        }

        securityIdListCacheListener = new SecurityIdListCacheListener();
        peopleListCacheListener = new PeopleListCacheListener();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        collectParameters(savedInstanceState);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.search_stock_people_menu, menu);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        MenuItem securitySearchElements = menu.findItem(R.id.security_search_menu_elements);

        mSearchTypeSpinnerAdapter = new SpinnerIconAdapter(
                getActivity(),
                R.layout.search_spinner_item,
                R.id.search_spinner_item_label,
                R.id.search_spinner_item_icon,
                R.id.search_spinner_item_icon,
                dropDownTexts,
                spinnerIcons,
                dropDownIcons);
        mSearchTypeSpinnerAdapter.setDropDownViewResource(R.layout.search_spinner_dropdown_item);

        mSearchTypeSpinner = (Spinner) securitySearchElements.getActionView().findViewById(R.id.spinner);
        if (mSearchTypeSpinner != null)
        {
            mSearchTypeSpinner.setVisibility(shouldDisableSearchTypeOption ? View.GONE : View.VISIBLE);
            mSearchTypeSpinner.setAdapter(mSearchTypeSpinnerAdapter);
            mSearchTypeSpinner.setOnItemSelectedListener(new SearchTypeSpinnerOnItemClickListener());
        }

        mSearchTextWatcher = new SearchTextWatcher();
        mSearchTextField = (EditText) securitySearchElements.getActionView().findViewById(R.id.search_field);
        if (mSearchTextField != null)
        {
            mSearchTextField.addTextChangedListener(mSearchTextWatcher);
        }

        populateSearchActionBar();
    }

    @Override public void onResume()
    {
        super.onResume();

        populateSearchActionBar();
        initialPopulateOnCreate();

        Bundle arguments = getArguments();
        if (arguments != null)
        {
            String callerClassName = arguments.getString(BUNDLE_KEY_CALLER_FRAGMENT);
            if (callerClassName != null && callerClassName.equalsIgnoreCase(WatchlistPositionFragment.class.getName()) && mSearchTypeSpinner != null)
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
        THLog.d(TAG, "populateSearchActionBar " + mSearchType + " " + mSearchText);
        if (mSearchTypeSpinner != null && mSearchType != null)
        {
            mSearchTypeSpinner.setSelection(mSearchType.value);
        }
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
                linkWith(userBaseKeys, true, (UserBaseKey) null);
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

    @Override public void onDestroyOptionsMenu()
    {
        if (mSearchTypeSpinner != null)
        {
            mSearchTypeSpinner.setAdapter(null);
            mSearchTypeSpinner.setOnItemSelectedListener(null);
        }
        mSearchTypeSpinner = null;
        mSearchTypeSpinnerAdapter = null;

        if (mSearchTextField != null)
        {
            mSearchTextField.removeTextChangedListener(mSearchTextWatcher);
        }
        mSearchTextField = null;
        mSearchTextWatcher = null;
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        if (mSearchStockListView != null)
        {
            mSearchStockListView.setOnItemClickListener(null);
            mSearchStockListView.setOnScrollListener(null);
        }
        securityItemViewAdapter = null;
        mSearchStockListView = null; // To break the cycle link with the adapter

        if (mSearchPeopleListView != null)
        {
            mSearchPeopleListView.setOnItemClickListener(null);
            mSearchPeopleListView.setOnScrollListener(null);
        }
        peopleItemViewAdapter = null;
        mSearchPeopleListView = null;

        cancelSearchTasks();
        securityIdListCacheListener = null;
        peopleListCacheListener = null;

        super.onDestroyView();
    }

    private void cancelSearchTasks()
    {
        if (securitySearchTask != null)
        {
            securitySearchTask.setListener(null);
        }
        securitySearchTask = null;

        if (peopleSearchTask != null)
        {
            peopleSearchTask.setListener(null);
        }
        peopleSearchTask = null;
        isQuerying = false;
    }

    protected void scheduleRequestData()
    {
        if (requestDataTimer != null)
        {
            requestDataTimer.cancel();
        }
        requestDataTimer = new Timer();
        requestDataTimer.schedule(new RequestDataTimerTask(), DELAY_REQUEST_DATA_MILLI_SEC);
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
                securityItemViewAdapter.setItems(securityIds);
            }
            if (andDisplay)
            {
                getView().post(new Runnable()
                {
                    @Override public void run()
                    {
                        securityItemViewAdapter.notifyDataSetChanged();
                        updateVisibilities();
                    }
                });
            }
        }
    }

    private void linkWith(final List<UserBaseKey> users, boolean andDisplay, UserBaseKey typeQualifier)
    {
        this.userBaseKeys = users;

        if (peopleItemViewAdapter != null)
        {
            peopleItemViewAdapter.setItems(users);
            if (andDisplay)
            {
                getView().post(new Runnable()
                {
                    @Override public void run()
                    {
                        PeopleItemViewAdapter adapterCopy = peopleItemViewAdapter;
                        if (adapterCopy != null)
                        {
                            adapterCopy.notifyDataSetChanged();
                        }

                        // All these damn HACKs are not enough to have the list update itself!
                        ListView peopleListView = mSearchPeopleListView;
                        if (peopleListView != null)
                        {
                            mSearchPeopleListView.invalidateViews();
                            mSearchPeopleListView.scrollBy(0, 0);
                            mSearchPeopleListView.refreshDrawableState();
                        }

                        updateVisibilities();
                    }
                });
            }
        }
    }

    public void setQuerying(boolean querying)
    {
        isQuerying = querying;
        updateVisibilities();
    }

    private void updateVisibilities()
    {
        if (mProgressSpinner != null)
        {
            mProgressSpinner.setVisibility(isQuerying ? View.VISIBLE : View.INVISIBLE);
        }

        if (mSearchText == null || mSearchText.length() == 0 || mSearchType == null)
        {
            if (mNothingYet != null)
            {
                mNothingYet.setVisibility(View.VISIBLE);
            }
            if (mSearchStockListView != null)
            {
                mSearchStockListView.setVisibility(View.INVISIBLE);
            }
            if (mSearchPeopleListView != null)
            {
                mSearchPeopleListView.setVisibility(View.INVISIBLE);
            }
        }
        else if (mSearchType == TrendingSearchType.STOCKS)
        {
            if (mSearchStockListView != null)
            {
                mSearchStockListView.setVisibility(View.VISIBLE);
            }
            if (mSearchPeopleListView != null)
            {
                mSearchPeopleListView.setVisibility(View.INVISIBLE);
            }
            if (securityIds == null || securityIds.size() == 0)
            {
                mNothingYet.setVisibility(View.VISIBLE);
            }
            else if (isQuerying)
            {
                mNothingYet.setVisibility(View.INVISIBLE);
            }
            else
            {
                mNothingYet.setVisibility(View.INVISIBLE);
            }
        }
        else if (mSearchType == TrendingSearchType.PEOPLE)
        {
            if (mSearchStockListView != null)
            {
                mSearchStockListView.setVisibility(View.INVISIBLE);
            }
            if (mSearchPeopleListView != null)
            {
                mSearchPeopleListView.setVisibility(View.VISIBLE);
            }
            if (userBaseKeys == null || userBaseKeys.size() == 0)
            {
                mNothingYet.setVisibility(View.VISIBLE);
            }
            else if (isQuerying)
            {
                mNothingYet.setVisibility(View.INVISIBLE);
            }
            else
            {
                mNothingYet.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            throw new IllegalArgumentException("Unhandled TrendingSearchType." + mSearchType);
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
            THLog.e(TAG, "Cannot handle null SecurityId", new IllegalArgumentException());
            return;
        }
        Bundle args = new Bundle();
        args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        navigator.pushFragment(BuySellFragment.class, args);
    }

    protected void pushUserFragmentIn(UserBaseKey userBaseKey)
    {
        if (userBaseKey == null || userBaseKey.key == null)
        {
            THLog.e(TAG, "Cannot handle null userBaseKey", new IllegalArgumentException());
            return;
        }

        //THToast.show("Disabled for now");
        // TODO put back in

        Bundle args = new Bundle();
        args.putInt(PushableTimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userBaseKey.key);

        navigator.pushFragment(PushableTimelineFragment.class, args);
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

    private class SearchStockOnItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            SecurityId clickedItem = (SecurityId) parent.getItemAtPosition(position);

            if (shouldDisableSearchTypeOption)
            {
                // pop out current fragment and push in watchlist edit fragment
                Bundle args = new Bundle();
                args.putBundle(WatchlistEditFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, clickedItem.getArgs());
                DeviceUtil.dismissKeyBoard(getActivity(), getView());
                navigator.pushFragment(WatchlistEditFragment.class, args);
            }
            else
            {
                pushTradeFragmentIn(clickedItem);
            }
        }
    }

    private class SearchPeopleOnItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            pushUserFragmentIn((UserBaseKey) parent.getItemAtPosition(position));
        }
    }

    private class SearchTypeSpinnerOnItemClickListener implements AdapterView.OnItemSelectedListener
    {
        @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
        {
            THLog.i(TAG, "onItemSelected Spinner i " + i + ", l " + l + ", view " + view + ", type " + TrendingSearchType.fromInt(i));
            TrendingSearchType newSearchType = TrendingSearchType.fromInt(i);
            boolean isChanged = newSearchType != mSearchType;
            mSearchType = newSearchType;
            updateVisibilities();
            if (isChanged)
            {
                THLog.d(TAG, "Requesting data");
                startAnew();
                requestData();
            }
        }

        @Override public void onNothingSelected(AdapterView<?> adapterView)
        {
            THLog.i(TAG, "onNothingSelected Spinner");
            mSearchType = null;
            updateVisibilities();
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
                linkWith(null, true, (UserBaseKey) null);
            }
            else
            {
                scheduleRequestData();
            }
        }
    }

    private class SecurityIdListCacheListener implements DTOCache.Listener<SecurityListType, SecurityIdList>
    {
        @Override public void onDTOReceived(SecurityListType key, SecurityIdList value)
        {
            if (lastLoadedPage + 1 != key.getPage())
            {
                throw new IllegalStateException("We just got a wrong page; last: " + lastLoadedPage + ", received page: " + key.getPage());
            }
            lastLoadedPage = key.getPage();
            currentlyLoadingPage = FIRST_PAGE - 1;
            nearEndScrollListener.lowerFlag();
            if (value == null || value.size() == 0)
            {
                nearEndScrollListener.deactivate();
            }
            else
            {
                if (securityIds != null)
                {
                    securityIds.addAll(value);
                }
                securityItemViewAdapter.setItems(securityIds);
            }
            setQuerying(false);
            securityItemViewAdapter.notifyDataSetChanged();
        }

        @Override public void onErrorThrown(SecurityListType key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_security_list_info));
            THLog.e(TAG, "Error fetching the list of securities " + key, error);
        }
    }

    private class PeopleListCacheListener implements DTOCache.Listener<UserListType, UserBaseKeyList>
    {
        @Override public void onDTOReceived(UserListType key, UserBaseKeyList value)
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
            }
            else
            {
                userBaseKeys.addAll(value);
                peopleItemViewAdapter.setItems(userBaseKeys);
            }
            setQuerying(false);
            peopleItemViewAdapter.notifyDataSetChanged();
        }

        @Override public void onErrorThrown(UserListType key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_people_list_info));
            THLog.e(TAG, "Error fetching the list of people " + key, error);
        }
    }

    private class RequestDataTimerTask extends TimerTask
    {
        @Override public void run()
        {
            View view = getView();
            if (view != null)
            {
                view.post(new Runnable()
                {
                    @Override public void run()
                    {
                        startAnew();
                        requestData();
                    }
                });
            }
        }
    }
    //</editor-fold>
}