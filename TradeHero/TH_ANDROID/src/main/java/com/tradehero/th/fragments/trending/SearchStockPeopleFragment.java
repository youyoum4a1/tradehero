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
import android.widget.ImageButton;
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
import com.tradehero.th.R;
import com.tradehero.th.api.security.SearchSecurityListType;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserBaseKeyList;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.persistence.user.UserBaseKeyListCache;
import com.tradehero.th.persistence.user.UserSearchResultCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 9/18/13 Time: 12:09 PM To change this template use File | Settings | File Templates. */
public class SearchStockPeopleFragment extends DashboardFragment
        implements AdapterView.OnItemSelectedListener, TextWatcher
{
    public final static String BUNDLE_KEY_SEARCH_STRING = SearchStockPeopleFragment.class.getName() + ".searchString";
    public final static String BUNDLE_KEY_SEARCH_TYPE = SearchStockPeopleFragment.class.getName() + ".searchType";
    public final static String BUNDLE_KEY_PAGE = SearchStockPeopleFragment.class.getName() + ".page";
    public final static String BUNDLE_KEY_PER_PAGE = SearchStockPeopleFragment.class.getName() + ".perPage";
    private final static String TAG = SearchStockPeopleFragment.class.getSimpleName();

    public final static int DEFAULT_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 15;
    public final static long DELAY_REQUEST_DATA_MILLI_SEC = 1000;

    private TextView mNothingYet;
    private ListView mSearchStockListView;
    private ListView mSearchPeopleListView;
    private ProgressBar mProgressSpinner;

    private CharSequence[] dropDownTexts;
    private Drawable[] dropDownIcons;
    private Drawable[] spinnerIcons;
    private View actionBar;
    private ImageButton mBackBtn;
    private SpinnerIconAdapter mSearchTypeSpinnerAdapter;
    private Spinner mSearchTypeSpinner;
    private TrendingSearchType mSearchType = TrendingSearchType.STOCKS;
    private EditText mSearchTextField;
    private String mSearchText;

    private Timer requestDataTimer;
    private boolean isQuerying;

    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    private DTOCache.Listener<SecurityListType, SecurityIdList> securitySearchListener;
    private DTOCache.GetOrFetchTask<SecurityIdList> securitySearchTask;
    private List<SecurityCompactDTO> securityList;
    private SecurityItemViewAdapterArray securityItemViewAdapter;

    @Inject Lazy<UserBaseKeyListCache> userBaseKeyListCache;
    @Inject Lazy<UserSearchResultCache> userSearchResultCache;
    private DTOCache.Listener<UserListType, UserBaseKeyList>  peopleSearchListener;
    private DTOCache.GetOrFetchTask<UserBaseKeyList> peopleSearchTask;
    private List<UserSearchResultDTO> userDTOList;
    private PeopleItemViewAdapter peopleItemViewAdapter;

    private int page = DEFAULT_PAGE;
    private int perPage = DEFAULT_PER_PAGE;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        if (dropDownTexts == null || dropDownIcons == null)
        {
            dropDownTexts = new CharSequence[TrendingSearchType.values().length];
            dropDownIcons = new Drawable[TrendingSearchType.values().length];
            spinnerIcons = new Drawable[TrendingSearchType.values().length];
            for(TrendingSearchType searchType: TrendingSearchType.values())
            {
                dropDownTexts[searchType.getValue()] = getString(searchType.getSearchStringResId());
                dropDownIcons[searchType.getValue()] = getResources().getDrawable(searchType.getSearchDropDownDrawableResId());
                spinnerIcons[searchType.getValue()] = getResources().getDrawable(searchType.getSearchDrawableResId());
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

        if (securityItemViewAdapter == null)
        {
            securityItemViewAdapter = new SecurityItemViewAdapterArray(getActivity(), getActivity().getLayoutInflater(), R.layout.search_security_item);
        }
        mSearchStockListView = (ListView) view.findViewById(R.id.trending_listview);
        if (mSearchStockListView != null)
        {
            mSearchStockListView.setAdapter(securityItemViewAdapter);
            mSearchStockListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    pushTradeFragmentIn((SecurityCompactDTO) parent.getItemAtPosition(position));
                }
            });
        }

        if (peopleItemViewAdapter == null)
        {
            peopleItemViewAdapter = new PeopleItemViewAdapter(getActivity().getApplicationContext(), inflater, R.layout.search_people_item);
        }
        mSearchPeopleListView = (ListView) view.findViewById(R.id.people_listview);
        if (mSearchPeopleListView != null)
        {
            mSearchPeopleListView.setAdapter(peopleItemViewAdapter);
            mSearchPeopleListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    pushUserFragmentIn((UserSearchResultDTO) parent.getItemAtPosition(position));
                }
            });
        }
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

        mSearchTypeSpinner = (Spinner) securitySearchElements.getActionView().findViewById(R.id.spinner);

        if (mSearchTypeSpinnerAdapter == null)
        {
            mSearchTypeSpinnerAdapter = new SpinnerIconAdapter(
                    getActivity(),
                    R.layout.search_spinner_item,
                    R.id.search_spinner_item_label,
                    R.id.search_spinner_item_icon,
                    R.id.search_spinner_item_icon,
                    dropDownTexts,
                    spinnerIcons,
                    dropDownIcons);
        }
        mSearchTypeSpinnerAdapter.setDropDownViewResource(R.layout.search_spinner_dropdown_item);

        if (mSearchTypeSpinner != null)
        {
            mSearchTypeSpinner.setVisibility(View.VISIBLE);
            mSearchTypeSpinner.setAdapter(mSearchTypeSpinnerAdapter);
            mSearchTypeSpinner.setOnItemSelectedListener(this);
        }

        mSearchTextField = (EditText) securitySearchElements.getActionView().findViewById(R.id.search_field);
        if (mSearchTextField != null)
        {
            mSearchTextField.addTextChangedListener(this);
        }

        populateSearchActionBar();
    }

    @Override public void onResume()
    {
        super.onResume();
        populateSearchActionBar();
        initialPopulateOnCreate();
    }

    private void populateSearchActionBar()
    {
        THLog.d(TAG, "populateSearchActionBar " + mSearchType + " " + mSearchText);
        if (mSearchTypeSpinner != null && mSearchType != null)
        {
            mSearchTypeSpinner.setSelection(mSearchType.getValue());
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
            if (securityList != null && securityList.size() > 0)
            {
                linkWith(securityList, true, (SecurityCompactDTO) null);
            }
            else
            {
                requestData();
            }
        }
        else if (mSearchType == TrendingSearchType.PEOPLE)
        {
            if (userDTOList != null && userDTOList.size() > 0)
            {
                linkWith(userDTOList, true, (UserSearchResultDTO) null);
            }
            else
            {
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
        }
        if (mBackBtn != null)
        {
            mBackBtn.setOnClickListener(null);
        }
        if (mSearchTextField != null)
        {
            mSearchTextField.removeTextChangedListener(this);
        }
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        if (mSearchStockListView != null)
        {
            mSearchStockListView.setOnItemClickListener(null);
        }
        if (mSearchPeopleListView != null)
        {
            mSearchPeopleListView.setOnItemClickListener(null);
        }

        cancelSearchTasks();
        securitySearchListener = null;
        peopleSearchListener = null;

        mSearchStockListView = null; // To break the cycle link with the adapter
        mSearchPeopleListView = null;
        super.onDestroyView();
    }

    protected void scheduleRequestDataIfNotInCache()
    {
        if (!updateWithCacheOnly())
        {
            scheduleRequestData();
        }
    }

    protected boolean updateWithCacheOnly()
    {
        if (mSearchType == null)
        {
            return true;
        }
        else if (mSearchType == TrendingSearchType.STOCKS)
        {
            return updateSecuritiesWithCacheOnly(makeSearchSecurityListType());
        }
        else if (mSearchType == TrendingSearchType.PEOPLE)
        {
            return updatePeopleWithCacheOnly(makeSearchUserListType());
        }
        else
        {
            throw new IllegalArgumentException("Unhandled SearchType." + mSearchType);
        }
    }

    private boolean updateSecuritiesWithCacheOnly(SecurityListType searchSecurityListType)
    {
        SecurityIdList securityIds = securityCompactListCache.get().get(searchSecurityListType);
        if (securityIds != null)
        {
            linkWith(securityCompactCache.get().get(securityIds), true, (SecurityCompactDTO) null);
            return true;
        }
        return false;
    }

    private boolean updatePeopleWithCacheOnly(UserListType searchUserListType)
    {
        UserBaseKeyList userBaseKeys = userBaseKeyListCache.get().get(searchUserListType);
        if (userBaseKeys != null)
        {
            linkWith(userSearchResultCache.get().get(userBaseKeys), true, (UserSearchResultDTO) null);
            return true;
        }
        return false;
    }

    protected void scheduleRequestData()
    {
        if (requestDataTimer != null)
        {
            requestDataTimer.cancel();
        }
        requestDataTimer = new Timer();
        requestDataTimer.schedule(new TimerTask()
        {
            @Override public void run()
            {
                getView().post(new Runnable()
                {
                    @Override public void run()
                    {
                        requestData();
                    }
                });
            }
        }, DELAY_REQUEST_DATA_MILLI_SEC);
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
            SecurityListType searchSecurityListType = makeSearchSecurityListType();
            if (!updateSecuritiesWithCacheOnly(makeSearchSecurityListType()))
            {
                if (securitySearchListener == null)
                {
                    securitySearchListener = new DTOCache.Listener<SecurityListType, SecurityIdList>()
                    {
                        @Override public void onDTOReceived(SecurityListType key, SecurityIdList value)
                        {
                            setQuerying(false);
                            try
                            {
                                linkWith(securityCompactCache.get().getOrFetch(value), true, (SecurityCompactDTO) null);
                            }
                            catch (Throwable error)
                            {
                                onErrorThrown(key, error);
                            }
                        }

                        @Override public void onErrorThrown(SecurityListType key, Throwable error)
                        {
                            THToast.show(getString(R.string.error_fetch_security_list_info));
                            THLog.e(TAG, "Error fetching the list of securities " + key, error);
                        }
                    };
                }
                cancelSearchTasks();
                setQuerying(true);
                securitySearchTask = securityCompactListCache.get().getOrFetch(searchSecurityListType, securitySearchListener);
                securitySearchTask.execute();
            }
        }
    }

    private void requestPeople()
    {
        if (mSearchText != null && !mSearchText.isEmpty())
        {
            UserListType searchUserListType = makeSearchUserListType();
            if (!updatePeopleWithCacheOnly(makeSearchUserListType()))
            {
                if (peopleSearchListener == null)
                {
                    peopleSearchListener = new DTOCache.Listener<UserListType, UserBaseKeyList>()
                    {
                        @Override public void onDTOReceived(UserListType key, UserBaseKeyList value)
                        {
                            THLog.i(TAG, "onDTOReceived UserBaseKeyList");
                            setQuerying(false);
                            linkWith(userSearchResultCache.get().get(value), true, (UserSearchResultDTO) null);
                        }

                        @Override public void onErrorThrown(UserListType key, Throwable error)
                        {
                            THToast.show(getString(R.string.error_fetch_people_list_info));
                            THLog.e(TAG, "Error fetching the list of people " + key, error);
                        }
                    };
                }
                cancelSearchTasks();
                setQuerying(true);
                peopleSearchTask = userBaseKeyListCache.get().getOrFetch(searchUserListType, peopleSearchListener);
                peopleSearchTask.execute();
            }
        }
    }

    private void cancelSearchTasks()
    {
        if (securitySearchTask != null)
        {
            securitySearchTask.forgetListener(true);
        }
        securitySearchTask = null;

        if (peopleSearchTask != null)
        {
            peopleSearchTask.forgetListener(true);
        }
        peopleSearchTask = null;
        isQuerying = false;
    }

    private void linkWith(List<SecurityCompactDTO> securityCompactDTOs, boolean andDisplay, SecurityCompactDTO typeQualifier)
    {
        this.securityList = securityCompactDTOs;

        if (securityItemViewAdapter != null)
        {
            if (securityCompactDTOs == null)
            {
                securityItemViewAdapter.setItems(null);
            }
            else
            {
                List<SecurityId> securityIds = new ArrayList<>();
                for (SecurityCompactDTO securityCompactDTO : securityCompactDTOs)
                {
                    securityIds.add(securityCompactDTO.getSecurityId());
                }
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

    private void linkWith(final List<UserSearchResultDTO> users, boolean andDisplay, UserSearchResultDTO typeQualifier)
    {
        this.userDTOList = users;

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
            if (securityList == null || securityList.size() == 0)
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
            if (userDTOList == null || userDTOList.size() == 0)
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
            args.putInt(BUNDLE_KEY_PAGE, page);
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
            page = args.getInt(BUNDLE_KEY_PAGE, DEFAULT_PAGE);
        }
    }

    private void pushTradeFragmentIn(SecurityCompactDTO securityCompactDTO)
    {
        if (securityCompactDTO == null || securityCompactDTO.getSecurityId() == null)
        {
            THLog.e(TAG, "Cannot handle null " + securityCompactDTO, new IllegalArgumentException());
            return;
        }
        Bundle args = new Bundle();
        args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityCompactDTO.getSecurityId().getArgs());
        navigator.pushFragment(BuySellFragment.class, args);
    }

    protected void pushUserFragmentIn(UserSearchResultDTO userSearchResultDTO)
    {
        if (userSearchResultDTO == null || userSearchResultDTO.userId == null)
        {
            THLog.e(TAG, "Cannot handle null " + userSearchResultDTO, new IllegalArgumentException());
            return;
        }

        //THToast.show("Disabled for now");
        // TODO put back in

        Bundle args = new Bundle();
        args.putInt(PushableTimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userSearchResultDTO.userId);

        navigator.pushFragment(PushableTimelineFragment.class, args);
    }

    //<editor-fold desc="Accessors">
    public int getPage()
    {
        return page;
    }

    public int getPerPage()
    {
        return perPage;
    }

    public SecurityListType makeSearchSecurityListType()
    {
        return new SearchSecurityListType(mSearchText, page, perPage);
    }

    public UserListType makeSearchUserListType()
    {
        return new SearchUserListType(mSearchText, page, perPage);
    }
    //</editor-fold>

    //<editor-fold desc="TextWatcher">
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
            linkWith(null, true, (SecurityCompactDTO) null);
            linkWith(null, true, (UserSearchResultDTO) null);
        }
        else
        {
            scheduleRequestDataIfNotInCache();
        }
    }
    //</editor-fold>

    //<editor-fold desc="AdapterView.OnItemSelectedListener">
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        THLog.i(TAG, "onItemSelected Spinner i " + i + ", l " + l + ", view " + view + ", type " + TrendingSearchType.fromInt(i));
        TrendingSearchType newSearchType = TrendingSearchType.fromInt(i);
        boolean isChanged = newSearchType != mSearchType;
        mSearchType = newSearchType;
        updateVisibilities();
        if (isChanged)
        {
            requestData();
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView)
    {
        THLog.i(TAG, "onNothingSelected Spinner");
        mSearchType = null;
        updateVisibilities();
    }
    //</editor-fold>

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
