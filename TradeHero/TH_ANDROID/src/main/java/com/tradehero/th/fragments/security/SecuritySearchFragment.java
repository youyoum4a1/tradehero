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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.key.SearchSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class SecuritySearchFragment extends BasePurchaseManagerFragment
    implements HasSelectedItem
{
    private final static String BUNDLE_KEY_CURRENT_SEARCH_STRING = SecuritySearchFragment.class.getName() + ".currentSearchString";
    public final static String BUNDLE_KEY_PER_PAGE = SecuritySearchFragment.class.getName() + ".perPage";

    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 15;
    public final static long DELAY_REQUEST_DATA_MILLI_SEC = 1000;

    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject Analytics analytics;

    @InjectView(R.id.search_empty_container) View searchEmptyContainer;
    @InjectView(R.id.search_empty_textview) View searchEmptyTextView;
    @InjectView(R.id.search_empty_textview_wrapper) View searchEmptyTextViewWrapper;
    @InjectView(R.id.listview) ListView listView;
    @InjectView(R.id.progress) ProgressBar mProgress;

    protected int perPage = DEFAULT_PER_PAGE;
    private FlagNearEdgeScrollListener nearEndScrollListener;

    private EditText mSearchTextField;
    protected String mSearchText;
    private SearchTextWatcher mSearchTextWatcher;

    private SecurityItemViewAdapterNew<SecurityCompactDTO> securityItemViewAdapter;
    private Map<Integer, List<SecurityCompactDTO>> pagedSecurityCompacts;
    private Map<Integer, DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList>> securitySearchListeners;
    private SecurityCompactDTO selectedItem;

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
        pagedSecurityCompacts = new HashMap<>();
        securitySearchListeners = new HashMap<>();
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
            listView.setAdapter(securityItemViewAdapter);
        }
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
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
            mSearchTextField.setText(mSearchText);
            mSearchTextField.addTextChangedListener(mSearchTextWatcher);
            mSearchTextField.setFocusable(true);
            mSearchTextField.setFocusableInTouchMode(true);
            mSearchTextField.requestFocus();
            DeviceUtil.showKeyboardDelayed(mSearchTextField);
        }
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
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();
        loadAdapterWithAvailableData();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        putSearchString(outState, mSearchText);
        putPerPage(outState, perPage);
    }

    @Override public void onDestroyView()
    {
        detachSecuritySearchCache();
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

    @Override @Nullable public SecurityCompactDTO getSelectedItem()
    {
        return selectedItem;
    }

    protected void startAnew()
    {
        detachSecuritySearchCache();
        this.pagedSecurityCompacts.clear();
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
        if ((lastPageInAdapter == null && pagedSecurityCompacts.containsKey(FIRST_PAGE)) ||
                lastPageInAdapter != null)
        {
            if (lastPageInAdapter == null)
            {
                lastPageInAdapter = FIRST_PAGE - 1;
            }

            while (pagedSecurityCompacts.containsKey(++lastPageInAdapter))
            {
                securityItemViewAdapter.addPage(lastPageInAdapter, pagedSecurityCompacts.get(lastPageInAdapter));
            }
            securityItemViewAdapter.notifyDataSetChanged();
        }
        updateVisibilities();
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
        if (!pagedSecurityCompacts.containsKey(FIRST_PAGE))
        {
            return false;
        }
        List<SecurityCompactDTO> firstPage = pagedSecurityCompacts.get(FIRST_PAGE);
        return  firstPage == null || firstPage.size() == 0;
    }

    protected boolean isBeingHandled(int page)
    {
        return hasData(page) || isRequesting(page);
    }

    protected boolean hasData(int page)
    {
        return pagedSecurityCompacts.containsKey(page);
    }

    protected boolean isLast(int page)
    {
        return hasData(page) && pagedSecurityCompacts.get(page) == null;
    }

    protected boolean isRequesting()
    {
        return securitySearchListeners.size() > 0;
    }

    protected boolean isRequesting(int page)
    {
        return securitySearchListeners.containsKey(page);
    }

    protected void detachSecuritySearchCache()
    {
        for (DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> listener : securitySearchListeners.values())
        {
            securityCompactListCache.get().unregister(listener);
        }
        securitySearchListeners.clear();
    }

    protected void detachSecuritySearchCache(int page)
    {
        DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> listener = securitySearchListeners.get(page);
        if (listener != null)
        {
            securityCompactListCache.get().unregister(listener);
        }
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
            detachSecuritySearchCache(pageToLoad);
            DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> listener = createSecurityIdListCacheListener();
            securityCompactListCache.get().register(searchSecurityListType, listener);
            securitySearchListeners.put(pageToLoad, listener);
            securityCompactListCache.get().getOrFetchAsync(searchSecurityListType);
        }
        updateVisibilities();
    }

    @NotNull public SecurityListType makeSearchSecurityListType(int page)
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
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            BuySellFragment.putApplicablePortfolioId(args, applicablePortfolioId);
        }
        getDashboardNavigator().pushFragment(BuySellFragment.class, args);
    }

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
        this.selectedItem = clicked;

        if (getArguments() != null && getArguments().containsKey(
                Navigator.BUNDLE_KEY_RETURN_FRAGMENT))
        {
            getDashboardNavigator().popFragment();
            return;
        }

        if (clicked == null)
        {
            Timber.e(new NullPointerException("clicked was null"), null);
        }
        else
        {
            pushTradeFragmentIn(clicked.getSecurityId());
        }
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

    private DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> createSecurityIdListCacheListener()
    {
        return new SecurityIdListCacheListener();
    }

    private class SecurityIdListCacheListener
            implements DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList>
    {
        @Override
        public void onDTOReceived(@NotNull SecurityListType key, @NotNull SecurityCompactDTOList value)
        {
            Timber.d("Page loaded: %d", key.getPage());
            pagedSecurityCompacts.put(key.getPage(), value);
            securitySearchListeners.remove(key.getPage());

            loadAdapterWithAvailableData();

            nearEndScrollListener.lowerEndFlag();
            if (value.size() == 0)
            {
                nearEndScrollListener.deactivateEnd();
                if (key.getPage() == FIRST_PAGE)
                {
                    securityItemViewAdapter.clear();
                }
            }
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.SearchResult_Stock));
        }

        @Override public void onErrorThrown(@NotNull SecurityListType key, @NotNull Throwable error)
        {
            securitySearchListeners.remove(key.getPage());
            nearEndScrollListener.lowerEndFlag();
            THToast.show(getString(R.string.error_fetch_security_list_info));
            Timber.e("Error fetching the list of securities " + key, error);
        }
    }
    //</editor-fold>
}