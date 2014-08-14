package com.tradehero.th.fragments;

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
import butterknife.OnItemClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.api.PagedDTOKey;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedArrayDTOAdapterNew;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

abstract public class BaseSearchFragment<
        PagedDTOKeyType extends DTOKey, // But it also needs to be a PagedDTOKey
        DTOType extends DTO,
        DTOListType extends DTO & List<DTOType>,
        ViewType extends View & DTOView<DTOType>>
        extends BasePurchaseManagerFragment
{
    private final static String BUNDLE_KEY_CURRENT_SEARCH_STRING = BaseSearchFragment.class.getName() + ".currentSearchString";
    private final static String BUNDLE_KEY_PER_PAGE = BaseSearchFragment.class.getName() + ".perPage";

    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 15;
    public final static long DELAY_REQUEST_DATA_MILLI_SEC = 1000;

    @Inject protected Analytics analytics;
    @Inject protected THRouter thRouter;

    @InjectView(R.id.search_empty_container) protected View searchEmptyContainer;
    @InjectView(R.id.search_empty_textview) protected TextView searchEmptyTextView;
    @InjectView(R.id.search_empty_textview_wrapper) protected View searchEmptyTextViewWrapper;
    @InjectView(R.id.listview) protected ListView listView;
    @InjectView(R.id.progress) protected ProgressBar mProgress;

    protected int perPage = DEFAULT_PER_PAGE;
    protected FlagNearEdgeScrollListener nearEndScrollListener;

    protected EditText mSearchTextField;
    protected String mSearchText;
    protected SearchTextWatcher mSearchTextWatcher;

    protected PagedArrayDTOAdapterNew<DTOType, ViewType> itemViewAdapter;
    protected Map<Integer, DTOListType> pagedDtos;
    protected Map<Integer, DTOCacheNew.Listener<PagedDTOKeyType, DTOListType>> dtoSearchListeners;
    protected DTOType selectedItem;

    protected Runnable requestDataTask;

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
        pagedDtos = new HashMap<>();
        dtoSearchListeners = new HashMap<>();
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
            listView.setOnScrollListener(nearEndScrollListener);
            listView.setEmptyView(searchEmptyContainer);
            listView.setAdapter(itemViewAdapter);
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
        MenuItem peopleSearchElements = menu.findItem(R.id.security_search_menu_elements);

        mSearchTextWatcher = new SearchTextWatcher();
        mSearchTextField =
                (EditText) peopleSearchElements.getActionView().findViewById(R.id.search_field);
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
        detachDtoSearchCache();
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
        itemViewAdapter = null;
        super.onDestroy();
    }

    protected void startAnew()
    {
        detachDtoSearchCache();
        this.pagedDtos.clear();
        if (nearEndScrollListener != null)
        {
            nearEndScrollListener.lowerEndFlag();
            nearEndScrollListener.activateEnd();
        }
        if (itemViewAdapter != null)
        {
            itemViewAdapter.clear();
            itemViewAdapter.notifyDataSetChanged();
        }
        updateVisibilities();
    }

    abstract protected PagedArrayDTOAdapterNew<DTOType, ViewType> createItemViewAdapter();

    protected void loadAdapterWithAvailableData()
    {
        if (itemViewAdapter == null)
        {
            itemViewAdapter = createItemViewAdapter();
            listView.setAdapter(itemViewAdapter);
        }

        Integer lastPageInAdapter = itemViewAdapter.getLastPageLoaded();
        if ((lastPageInAdapter == null && pagedDtos.containsKey(FIRST_PAGE)) ||
                lastPageInAdapter != null)
        {
            if (lastPageInAdapter == null)
            {
                lastPageInAdapter = FIRST_PAGE - 1;
            }

            while (pagedDtos.containsKey(++lastPageInAdapter))
            {
                itemViewAdapter.addPage(lastPageInAdapter, pagedDtos.get(lastPageInAdapter));
            }
            itemViewAdapter.notifyDataSetChanged();
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
        if (!pagedDtos.containsKey(FIRST_PAGE))
        {
            return false;
        }
        List<DTOType> firstPage = pagedDtos.get(FIRST_PAGE);
        return  firstPage == null || firstPage.size() == 0;
    }

    protected boolean isBeingHandled(int page)
    {
        return hasData(page) || isRequesting(page);
    }

    protected boolean hasData(int page)
    {
        return pagedDtos.containsKey(page);
    }

    protected boolean isLast(int page)
    {
        return hasData(page) && pagedDtos.get(page) == null;
    }

    protected boolean isRequesting()
    {
        return dtoSearchListeners.size() > 0;
    }

    protected boolean isRequesting(int page)
    {
        return dtoSearchListeners.containsKey(page);
    }

    abstract protected DTOCacheNew<PagedDTOKeyType, DTOListType> getSearchCache();

    protected void detachDtoSearchCache()
    {
        for (DTOCacheNew.Listener<PagedDTOKeyType, DTOListType> listener : dtoSearchListeners.values())
        {
            getSearchCache().unregister(listener);
        }
        dtoSearchListeners.clear();
    }

    protected void detachUserSearchCache(int page)
    {
        DTOCacheNew.Listener<PagedDTOKeyType, DTOListType> listener = dtoSearchListeners.get(page);
        if (listener != null)
        {
            getSearchCache().unregister(listener);
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
                    requestDtos();
                }
            };
            view.postDelayed(requestDataTask, DELAY_REQUEST_DATA_MILLI_SEC);
        }
    }

    protected void requestDtos()
    {
        Integer pageToLoad = getNextPageToRequest();
        if (pageToLoad != null && mSearchText != null && !mSearchText.isEmpty())
        {
            PagedDTOKeyType searchKey = makeSearchDtoKey(pageToLoad);
            detachUserSearchCache(pageToLoad);
            DTOCacheNew.Listener<PagedDTOKeyType, DTOListType> listener = createSearchCacheListener();
            getSearchCache().register(searchKey, listener);
            dtoSearchListeners.put(pageToLoad, listener);
            getSearchCache().getOrFetchAsync(searchKey);
        }
        updateVisibilities();
    }

    @NotNull abstract public PagedDTOKeyType makeSearchDtoKey(int page);

    protected void updateVisibilities()
    {
        mProgress.setVisibility(isRequesting() ? View.VISIBLE : View.INVISIBLE);

        boolean hasItems = (itemViewAdapter != null) && (itemViewAdapter.getCount() > 0);
        searchEmptyContainer.setVisibility(hasItems ? View.GONE : View.VISIBLE);

        searchEmptyTextViewWrapper.setVisibility(hasEmptyResult() ? View.VISIBLE : View.GONE);
    }

    //<editor-fold desc="Listeners">
    protected FlagNearEdgeScrollListener createFlagNearEdgeScrollListener()
    {
        return new SearchFlagNearEdgeScrollListener();
    }

    protected class SearchFlagNearEdgeScrollListener extends FlagNearEdgeScrollListener
    {
        @Override public void raiseEndFlag()
        {
            super.raiseEndFlag();
            requestDtos();
        }
    }

    @OnItemClick(R.id.listview)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //noinspection unchecked
        handleDtoClicked((DTOType) parent.getItemAtPosition(position));
    }

    protected void handleDtoClicked(DTOType clicked)
    {
        this.selectedItem = clicked;
    }

    protected class SearchTextWatcher implements TextWatcher
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

    protected DTOCacheNew.Listener<PagedDTOKeyType, DTOListType> createSearchCacheListener()
    {
        return new SearchCacheListener();
    }

    protected class SearchCacheListener
            implements DTOCacheNew.Listener<PagedDTOKeyType, DTOListType>
    {
        @Override
        public void onDTOReceived(@NotNull PagedDTOKeyType key, @NotNull DTOListType value)
        {
            PagedDTOKey properKey = (PagedDTOKey) key;
            Timber.d("Page loaded: %d", properKey.getPage());
            pagedDtos.put(properKey.getPage(), value);
            dtoSearchListeners.remove(properKey.getPage());

            loadAdapterWithAvailableData();

            nearEndScrollListener.lowerEndFlag();
            if (value.size() == 0)
            {
                nearEndScrollListener.deactivateEnd();
                if (properKey.getPage() == FIRST_PAGE)
                {
                    itemViewAdapter.clear();
                }
            }
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.SearchResult_Stock));
        }

        @Override public void onErrorThrown(@NotNull PagedDTOKeyType key, @NotNull Throwable error)
        {
            PagedDTOKey properKey = (PagedDTOKey) key;
            dtoSearchListeners.remove(properKey.getPage());
            nearEndScrollListener.lowerEndFlag();
            THToast.show(getString(R.string.error_fetch_people_list_info));
            Timber.e("Error fetching the list of securities " + key, error);
        }
    }
    //</editor-fold>
}