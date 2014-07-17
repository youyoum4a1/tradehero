package com.tradehero.th.fragments.social;

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
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.api.users.UserSearchResultDTOList;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trending.PeopleItemViewAdapter;
import com.tradehero.th.persistence.user.UserBaseKeyListCache;
import com.tradehero.th.persistence.user.UserSearchResultCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.THRouter;
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

public class PeopleSearchFragment extends BasePurchaseManagerFragment
    implements HasSelectedItem
{
    private final static String BUNDLE_KEY_CURRENT_SEARCH_STRING = PeopleSearchFragment.class.getName() + ".currentSearchString";
    public final static String BUNDLE_KEY_PER_PAGE = PeopleSearchFragment.class.getName() + ".perPage";

    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 15;
    public final static long DELAY_REQUEST_DATA_MILLI_SEC = 1000;

    @Inject Lazy<UserSearchResultCache> userSearchResultCache;
    @Inject Lazy<UserBaseKeyListCache> userBaseKeyListCache;
    @Inject Analytics analytics;
    @Inject THRouter thRouter;

    @InjectView(R.id.search_empty_container) View searchEmptyContainer;
    @InjectView(R.id.search_empty_textview) View searchEmptyTextView;
    @InjectView(R.id.search_empty_textview_wrapper) View searchEmptyTextViewWrapper;
    @InjectView(R.id.listview) ListView listView;
    @InjectView(R.id.progress) ProgressBar mProgress;

    protected int perPage = DEFAULT_PER_PAGE;
    private FlagNearEdgeScrollListener nearEndScrollListener;

    protected EditText mSearchTextField;
    protected String mSearchText;
    private SearchTextWatcher mSearchTextWatcher;

    private PeopleItemViewAdapter peopleItemViewAdapterItemViewAdapter;
    private Map<Integer, UserSearchResultDTOList> pagedPeopleIds;
    private Map<Integer, DTOCacheNew.Listener<UserListType, UserSearchResultDTOList>> userSearchListeners;
    private UserBaseKey selectedItem;

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
        pagedPeopleIds = new HashMap<>();
        userSearchListeners = new HashMap<>();
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
            listView.setAdapter(peopleItemViewAdapterItemViewAdapter);
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
            mSearchTextField.setHint(R.string.trending_search_empty_result_for_people);
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
        detachPeopleSearchCache();
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
        peopleItemViewAdapterItemViewAdapter = null;
        super.onDestroy();
    }

    @Override @Nullable public UserBaseKey getSelectedItem()
    {
        return selectedItem;
    }

    protected void startAnew()
    {
        detachPeopleSearchCache();
        this.pagedPeopleIds.clear();
        if (nearEndScrollListener != null)
        {
            nearEndScrollListener.lowerEndFlag();
            nearEndScrollListener.activateEnd();
        }
        if (peopleItemViewAdapterItemViewAdapter != null)
        {
            peopleItemViewAdapterItemViewAdapter.clear();
            peopleItemViewAdapterItemViewAdapter.notifyDataSetChanged();
        }
        updateVisibilities();
    }

    protected PeopleItemViewAdapter createPeopleItemViewAdapter()
    {
        return new PeopleItemViewAdapter(
                getActivity(),
                R.layout.search_people_item);
    }

    protected void loadAdapterWithAvailableData()
    {
        if (peopleItemViewAdapterItemViewAdapter == null)
        {
            peopleItemViewAdapterItemViewAdapter = createPeopleItemViewAdapter();
            listView.setAdapter(peopleItemViewAdapterItemViewAdapter);
        }

        Integer lastPageInAdapter = peopleItemViewAdapterItemViewAdapter.getLastPageLoaded();
        if ((lastPageInAdapter == null && pagedPeopleIds.containsKey(FIRST_PAGE)) ||
                lastPageInAdapter != null)
        {
            if (lastPageInAdapter == null)
            {
                lastPageInAdapter = FIRST_PAGE - 1;
            }

            while (pagedPeopleIds.containsKey(++lastPageInAdapter))
            {
                peopleItemViewAdapterItemViewAdapter.addPage(lastPageInAdapter, pagedPeopleIds.get(lastPageInAdapter).createKeys());
            }
            peopleItemViewAdapterItemViewAdapter.notifyDataSetChanged();
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
        if (!pagedPeopleIds.containsKey(FIRST_PAGE))
        {
            return false;
        }
        List<UserSearchResultDTO> firstPage = pagedPeopleIds.get(FIRST_PAGE);
        return  firstPage == null || firstPage.size() == 0;
    }

    protected boolean isBeingHandled(int page)
    {
        return hasData(page) || isRequesting(page);
    }

    protected boolean hasData(int page)
    {
        return pagedPeopleIds.containsKey(page);
    }

    protected boolean isLast(int page)
    {
        return hasData(page) && pagedPeopleIds.get(page) == null;
    }

    protected boolean isRequesting()
    {
        return userSearchListeners.size() > 0;
    }

    protected boolean isRequesting(int page)
    {
        return userSearchListeners.containsKey(page);
    }

    protected void detachPeopleSearchCache()
    {
        for (DTOCacheNew.Listener<UserListType, UserSearchResultDTOList> listener : userSearchListeners.values())
        {
            userBaseKeyListCache.get().unregister(listener);
        }
        userSearchListeners.clear();
    }

    protected void detachUserSearchCache(int page)
    {
        DTOCacheNew.Listener<UserListType, UserSearchResultDTOList> listener = userSearchListeners.get(page);
        if (listener != null)
        {
            userBaseKeyListCache.get().unregister(listener);
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
            SearchUserListType searchUserListType = makeSearchUserListType(pageToLoad);
            detachUserSearchCache(pageToLoad);
            DTOCacheNew.Listener<UserListType, UserSearchResultDTOList> listener = createUserBaseKeyListCacheListener();
            userBaseKeyListCache.get().register(searchUserListType, listener);
            userSearchListeners.put(pageToLoad, listener);
            userBaseKeyListCache.get().getOrFetchAsync(searchUserListType);
        }
        updateVisibilities();
    }

    @NotNull public SearchUserListType makeSearchUserListType(int page)
    {
        return new SearchUserListType(mSearchText, page, perPage);
    }

    private void updateVisibilities()
    {
        mProgress.setVisibility(isRequesting() ? View.VISIBLE : View.INVISIBLE);

        boolean hasItems = (peopleItemViewAdapterItemViewAdapter != null) && (peopleItemViewAdapterItemViewAdapter.getCount() > 0);
        searchEmptyContainer.setVisibility(hasItems ? View.GONE : View.VISIBLE);

        searchEmptyTextViewWrapper.setVisibility(hasEmptyResult() ? View.VISIBLE : View.GONE);
    }

    protected void pushTimelineFragmentIn(UserBaseKey userBaseKey)
    {
        Bundle args = new Bundle();
        thRouter.save(args, userBaseKey);
        getDashboardNavigator().pushFragment(PushableTimelineFragment.class, args);
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
            if (selectedItem instanceof UserBaseKey)
            {
                handlePersonClicked((UserBaseKey) selectedItem);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled clicked item " + selectedItem);
            }
        }
    }

    protected void handlePersonClicked(UserBaseKey clicked)
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
            pushTimelineFragmentIn(clicked);
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

    private DTOCacheNew.Listener<UserListType, UserSearchResultDTOList> createUserBaseKeyListCacheListener()
    {
        return new UserBaseKeyListCacheListener();
    }

    private class UserBaseKeyListCacheListener
            implements DTOCacheNew.Listener<UserListType, UserSearchResultDTOList>
    {
        @Override
        public void onDTOReceived(@NotNull UserListType key, @NotNull UserSearchResultDTOList value)
        {
            SearchUserListType properKey = (SearchUserListType) key;
            Timber.d("Page loaded: %d", properKey.page);
            pagedPeopleIds.put(properKey.page, value);
            userSearchListeners.remove(properKey.page);

            loadAdapterWithAvailableData();

            nearEndScrollListener.lowerEndFlag();
            if (value.size() == 0)
            {
                nearEndScrollListener.deactivateEnd();
                if (properKey.page == FIRST_PAGE)
                {
                    peopleItemViewAdapterItemViewAdapter.clear();
                }
            }
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.SearchResult_Stock));
        }

        @Override public void onErrorThrown(@NotNull UserListType key, @NotNull Throwable error)
        {
            SearchUserListType properKey = (SearchUserListType) key;
            userSearchListeners.remove(properKey.page);
            nearEndScrollListener.lowerEndFlag();
            THToast.show(getString(R.string.error_fetch_people_list_info));
            Timber.e("Error fetching the list of securities " + key, error);
        }
    }
    //</editor-fold>
}