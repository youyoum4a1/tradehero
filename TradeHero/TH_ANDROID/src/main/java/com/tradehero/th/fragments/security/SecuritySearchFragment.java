package com.tradehero.th.fragments.security;

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
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.key.SearchSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

public class SecuritySearchFragment
        extends BasePurchaseManagerFragment
{
    private final static String BUNDLE_KEY_CURRENT_SEARCH_STRING = SecuritySearchFragment.class.getName() + ".currentSearchString";
    public final static String BUNDLE_KEY_PER_PAGE = SecuritySearchFragment.class.getName() + ".perPage";

    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 15;
    public final static long DELAY_REQUEST_DATA_MILLI_SEC = 1000;

    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject LocalyticsSession localyticsSession;

    @InjectView(R.id.search_empty_container) View searchEmptyContainer;
    @InjectView(R.id.search_empty_textview) View searchEmptyTextView;
    @InjectView(R.id.search_empty_textview_wrapper) View searchEmptyTextViewWrapper;
    @InjectView(R.id.listview) ListView listView;
    @InjectView(R.id.progress) ProgressBar mProgress;

    private int perPage = DEFAULT_PER_PAGE;
    private FlagNearEdgeScrollListener nearEndScrollListener;

    private EditText mSearchTextField;
    private String mSearchText;
    private SearchTextWatcher mSearchTextWatcher;

    private SecurityItemViewAdapterNew<SecurityCompactDTO> securityItemViewAdapter;
    private Map<Integer, List<SecurityCompactDTO>> pagedSecurityIds;
    private Map<Integer, WeakReference<DTOCache.GetOrFetchTask<SecurityListType, SecurityIdList>>> securitySearchTasks;

    private Runnable requestDataTask;

    public static void putSearchString(Bundle args, String searchText)
    {
        args.putString(BUNDLE_KEY_CURRENT_SEARCH_STRING, searchText);
    }

    public static String getSearchString(Bundle args)
    {
        if (args != null && args.containsKey(BUNDLE_KEY_CURRENT_SEARCH_STRING))
        {
            return args.getString(BUNDLE_KEY_CURRENT_SEARCH_STRING);
        }
        return null;
    }

    public static void putPerPage(Bundle args, int perPage)
    {
        args.putInt(BUNDLE_KEY_PER_PAGE, perPage);
    }

    public static int getPerPage(Bundle args)
    {
        if (args != null && args.containsKey(BUNDLE_KEY_PER_PAGE))
        {
            return args.getInt(BUNDLE_KEY_PER_PAGE);
        }
        return DEFAULT_PER_PAGE;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        pagedSecurityIds = new HashMap<>();
        mSearchText = getSearchString(getArguments());
        mSearchText = getSearchString(savedInstanceState);
        perPage = getPerPage(getArguments());
        perPage = getPerPage(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search_stock, container, false);
        initViews(view);
        return view;
    }

    protected void initViews(View view)
    {
        ButterKnife.inject(this, view);
        nearEndScrollListener = createFlagNearEdgeScrollListener();

        if (listView != null)
        {
            listView.setOnItemClickListener(createItemClickListener());
            listView.setOnScrollListener(nearEndScrollListener);
            listView.setEmptyView(searchEmptyContainer);
        }

        securitySearchTasks = new HashMap<>();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
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
        mSearchTextField =
                (EditText) securitySearchElements.getActionView().findViewById(R.id.search_field);
        if (mSearchTextField != null)
        {
            mSearchTextField.addTextChangedListener(mSearchTextWatcher);
            mSearchTextField.setFocusable(true);
            mSearchTextField.setFocusableInTouchMode(true);
            mSearchTextField.requestFocus();
            DeviceUtil.showKeyboardDelayed(mSearchTextField);
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
        startAnew();
        requestSecurities();
    }

    private void updateSearchType()
    {
        if (listView != null)
        {
            mSearchTextField.setHint(R.string.trending_search_empty_result_for_stock);
            listView.setAdapter(securityItemViewAdapter);
        }
    }
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();
        loadAdapterWithAvailableData();
    }

    private void populateSearchActionBar()
    {
        if (mSearchTextField != null)
        {
            mSearchTextField.setText(mSearchText);
        }
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        putSearchString(outState, mSearchText);
        putPerPage(outState, perPage);
    }

    @Override public void onDestroyView()
    {
        detachSecuritySearchTasks();
        DeviceUtil.dismissKeyboard(getActivity());

        if (listView != null)
        {
            listView.setOnItemClickListener(null);
            listView.setOnScrollListener(null);
        }
        listView = null;
        nearEndScrollListener = null;

        View rootView = getView();
        if (rootView != null && requestDataTask != null)
        {
            rootView.removeCallbacks(requestDataTask);
        }

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        securityItemViewAdapter = null;
        super.onDestroy();
    }

    protected void startAnew()
    {
        detachSecuritySearchTasks();
        this.pagedSecurityIds = new HashMap<>();
        if (nearEndScrollListener != null)
        {
            nearEndScrollListener.lowerEndFlag();
            nearEndScrollListener.activateEnd();
        }
        if (securityItemViewAdapter != null)
        {
            securityItemViewAdapter.clear();
            securityItemViewAdapter.notifyDataSetChanged();
        }
        updateVisibilities();
    }

    protected SecurityItemViewAdapterNew<SecurityCompactDTO> createSecurityItemViewAdapter()
    {
        return new SecurityItemViewAdapterNew<>(
                getActivity(),
                R.layout.search_security_item);
    }

    protected void loadAdapterWithAvailableData()
    {
        if (securityItemViewAdapter == null)
        {
            securityItemViewAdapter = createSecurityItemViewAdapter();
            listView.setAdapter(securityItemViewAdapter);
        }

        Integer lastPageInAdapter = securityItemViewAdapter.getLastPageLoaded();
        if ((lastPageInAdapter == null && pagedSecurityIds.containsKey(FIRST_PAGE)) ||
                lastPageInAdapter != null)
        {
            if (lastPageInAdapter == null)
            {
                lastPageInAdapter = FIRST_PAGE - 1;
            }

            while (pagedSecurityIds.containsKey(++lastPageInAdapter))
            {
                securityItemViewAdapter.addPage(lastPageInAdapter, pagedSecurityIds.get(lastPageInAdapter));
            }
            securityItemViewAdapter.notifyDataSetChanged();
        }
    }

    protected Integer getNextPageToRequest()
    {
        Integer potential = FIRST_PAGE;
        while (isBeingHandled(potential) && !isLast(potential))
        {
            potential++;
        }
        if (isLast(potential))
        {
            potential = null;
        }
        return potential;
    }

    protected boolean hasEmptyResult()
    {
        if (!pagedSecurityIds.containsKey(FIRST_PAGE))
        {
            return false;
        }
        List<SecurityCompactDTO> firstPage = pagedSecurityIds.get(FIRST_PAGE);
        return  firstPage == null || firstPage.size() == 0;
    }

    protected boolean isBeingHandled(int page)
    {
        return hasData(page) || isRequesting(page);
    }

    protected boolean hasData(int page)
    {
        return pagedSecurityIds.containsKey(page);
    }

    protected boolean isLast(int page)
    {
        return hasData(page) && pagedSecurityIds.get(page) == null;
    }

    protected boolean isRequesting()
    {
        for (Map.Entry<Integer, WeakReference<DTOCache.GetOrFetchTask<SecurityListType, SecurityIdList>>> entry : securitySearchTasks.entrySet())
        {
            if (entry.getValue().get() != null)
            {
                return true;
            }
        }
        return false;
    }

    protected boolean isRequesting(int page)
    {
        return securitySearchTasks.containsKey(page) && securitySearchTasks.get(page).get() != null;
    }

    protected void detachSecuritySearchTasks()
    {
        for (WeakReference<DTOCache.GetOrFetchTask<SecurityListType, SecurityIdList>> weakSecuritySearchTask : securitySearchTasks.values())
        {
            DTOCache.GetOrFetchTask<SecurityListType, SecurityIdList> securitySearchTask = weakSecuritySearchTask.get();
            if (securitySearchTask != null)
            {
                securitySearchTask.setListener(null);
            }
        }
        securitySearchTasks.clear();
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
                    requestSecurities();
                }
            };
            view.postDelayed(requestDataTask, DELAY_REQUEST_DATA_MILLI_SEC);
        }
    }

    private void requestSecurities()
    {
        Integer pageToLoad = getNextPageToRequest();
        if (pageToLoad != null && mSearchText != null && !mSearchText.isEmpty())
        {
            SecurityListType searchSecurityListType = makeSearchSecurityListType(pageToLoad);
            DTOCache.GetOrFetchTask<SecurityListType, SecurityIdList> securitySearchTask = securityCompactListCache.get()
                    .getOrFetch(searchSecurityListType, createSecurityIdListCacheListener());
            securitySearchTasks.put(
                    searchSecurityListType.page,
                    new WeakReference<>(securitySearchTask));
            securitySearchTask.execute();
        }
        updateVisibilities();
    }

    public SecurityListType makeSearchSecurityListType(int page)
    {
        return new SearchSecurityListType(mSearchText, page, perPage);
    }

    private void updateVisibilities()
    {
        mProgress.setVisibility(isRequesting() ? View.VISIBLE : View.INVISIBLE);

        boolean hasItems = (securityItemViewAdapter != null) && (securityItemViewAdapter.getCount() > 0);
        searchEmptyContainer.setVisibility(hasItems ? View.GONE : View.VISIBLE);

        searchEmptyTextViewWrapper.setVisibility(hasEmptyResult() ? View.VISIBLE : View.GONE);
    }

    protected void pushTradeFragmentIn(SecurityId securityId)
    {
        Bundle args = new Bundle();
        args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        BuySellFragment.putApplicablePortfolioId(args, getApplicablePortfolioId());
        getNavigator().pushFragment(BuySellFragment.class, args);
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Listeners">
    private FlagNearEdgeScrollListener createFlagNearEdgeScrollListener()
    {
        return new SearchFlagNearEdgeScrollListener();
    }

    private class SearchFlagNearEdgeScrollListener extends FlagNearEdgeScrollListener
    {
        @Override public void raiseEndFlag()
        {
            super.raiseEndFlag();
            requestSecurities();
        }
    }

    protected AdapterView.OnItemClickListener createItemClickListener()
    {
        return new SearchOnItemClickListener();
    }

    protected class SearchOnItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Object selectedItem = parent.getItemAtPosition(position);
            if (selectedItem instanceof SecurityCompactDTO)
            {
                handleSecurityClicked((SecurityCompactDTO) selectedItem);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled clicked item " + selectedItem);
            }
        }
    }

    protected void handleSecurityClicked(SecurityCompactDTO clicked)
    {
        pushTradeFragmentIn(clicked.getSecurityId());
    }

    private class SearchTextWatcher implements TextWatcher
    {
        @Override public void afterTextChanged(Editable editable)
        {
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count)
        {
            mSearchText = charSequence.toString();
            if (mSearchText == null || mSearchText.isEmpty())
            {
                startAnew();
            }
            else
            {
                scheduleRequestData();
            }
        }
    }

    private DTOCache.Listener<SecurityListType, SecurityIdList> createSecurityIdListCacheListener()
    {
        return new SecurityIdListCacheListener();
    }

    private class SecurityIdListCacheListener
            implements DTOCache.Listener<SecurityListType, SecurityIdList>
    {
        @Override
        public void onDTOReceived(SecurityListType key, SecurityIdList value, boolean fromCache)
        {
            Timber.d("Page loaded: %d", key.getPage());
            List<SecurityCompactDTO> fleshedValues = securityCompactCache.get().get(value);
            pagedSecurityIds.put(key.getPage(), fleshedValues);
            securitySearchTasks.remove(key.getPage());

            loadAdapterWithAvailableData();

            nearEndScrollListener.lowerEndFlag();
            if (value == null || value.size() == 0)
            {
                nearEndScrollListener.deactivateEnd();
                if (key.getPage() == FIRST_PAGE)
                {
                    securityItemViewAdapter.clear();
                }
            }
            updateVisibilities();
            localyticsSession.tagEvent(LocalyticsConstants.SearchResult_Stock);
        }

        @Override public void onErrorThrown(SecurityListType key, Throwable error)
        {
            securitySearchTasks.remove(key.getPage());
            nearEndScrollListener.lowerEndFlag();
            THToast.show(getString(R.string.error_fetch_security_list_info));
            Timber.e("Error fetching the list of securities " + key, error);
        }
    }
    //</editor-fold>
}