package com.tradehero.th.fragments.trending;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import com.tradehero.common.adapter.SpinnerIconAdapter;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PeopleItemViewAdapter;
import com.tradehero.th.adapters.trending.SecurityItemViewAdapter;
import com.tradehero.th.api.security.SearchSecurityListType;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserBaseKeyList;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.TradeFragment;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.persistence.user.UserBaseKeyListCache;
import com.tradehero.th.persistence.user.UserSearchResultCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 9/18/13 Time: 12:09 PM To change this template use File | Settings | File Templates. */
public class SearchStockPeopleFragment extends DashboardFragment
        implements AdapterView.OnItemSelectedListener, TextWatcher,
        BaseFragment.ArgumentsChangeListener
{
    public final static String BUNDLE_KEY_SEARCH_STRING = SearchStockPeopleFragment.class.getName() + ".searchString";
    public final static String BUNDLE_KEY_SEARCH_TYPE = SearchStockPeopleFragment.class.getName() + ".searchType";
    public final static String BUNDLE_KEY_PAGE = SearchStockPeopleFragment.class.getName() + ".page";
    public final static String BUNDLE_KEY_PER_PAGE = SearchStockPeopleFragment.class.getName() + ".perPage";
    private final static String TAG = SearchStockPeopleFragment.class.getSimpleName();

    public final static int DEFAULT_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 15;

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
    private TrendingSearchType mSearchType;
    private EditText mSearchTextField;
    private String mSearchText;
    private boolean populatedOnCreate = false; // TODO consider get rid of it

    private Bundle desiredArguments;
    private boolean isQuerying;

    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    private DTOCache.Listener<SecurityListType, SecurityIdList> securitySearchListener;
    private DTOCache.GetOrFetchTask<SecurityIdList> securitySearchTask;
    private List<SecurityCompactDTO> securityList;
    private SecurityItemViewAdapter securityItemViewAdapter;

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
        THLog.i(TAG, "onAttach");
        super.onAttach(activity);

        if (dropDownTexts == null || dropDownIcons == null)
        {
            dropDownTexts = new CharSequence[TrendingSearchType.values().length];
            dropDownIcons = new Drawable[TrendingSearchType.values().length];
            spinnerIcons = new Drawable[TrendingSearchType.values().length];
            for(TrendingSearchType searchType: TrendingSearchType.values())
            {
                dropDownTexts[searchType.getValue()] = activity.getResources().getString(TrendingSearchType.getStringResourceId(searchType));
                dropDownIcons[searchType.getValue()] = activity.getResources().getDrawable(TrendingSearchType.getDropDownDrawableResourceId(searchType));
                spinnerIcons[searchType.getValue()] = activity.getResources().getDrawable(TrendingSearchType.getDrawableResourceId(searchType));
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
        THLog.i(TAG, "onCreateView");
        collectParameters(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search_stock, container, false);
        initViews(view, inflater);
        populatedOnCreate = false;
        return view;
    }

    protected void initViews(View view, LayoutInflater inflater)
    {
        mNothingYet = (TextView) view.findViewById(R.id.search_stock_nothing_yet_view);
        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);

        if (securityItemViewAdapter == null)
        {
            securityItemViewAdapter = new SecurityItemViewAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.search_stock_item);
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
        THLog.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        initialPopulateOnCreate();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        THLog.i(TAG, "onCreateOptionsMenu mSearchType " + mSearchType + ", mSearchText " + mSearchText);
        super.onCreateOptionsMenu(menu, inflater);
        createSearchActionBar(menu, inflater);
        initialPopulateOnCreate();
    }

    private void createSearchActionBar(Menu menu, MenuInflater inflater)
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSherlockActivity().getSupportActionBar().setCustomView(R.layout.topbar_trending_search);

        actionBar = getSherlockActivity().getSupportActionBar().getCustomView();
        mBackBtn = (ImageButton) actionBar.findViewById(R.id.btn_back);
        mSearchTypeSpinner = (Spinner) actionBar.findViewById(R.id.spinner);
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
        mSearchTypeSpinner.setAdapter(mSearchTypeSpinnerAdapter);
        if (mSearchType != null)
        {
            mSearchTypeSpinner.setSelection(mSearchType.getValue());
        }

        mBackBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                navigator.popFragment();
            }
        });

        mSearchTypeSpinner.setOnItemSelectedListener(this); // TODO do it on resume / pause?

        mSearchTextField = (EditText) actionBar.findViewById(R.id.search_field);
        mSearchTextField.setText(mSearchText);
        mSearchTextField.addTextChangedListener(this);
        //mSearchTextField.requestFocus();
        //InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //if (inputMethodManager != null)
        //{
        //    inputMethodManager.showSoftInput(mSearchTextField, InputMethodManager.SHOW_IMPLICIT);
        //}
    }

    private void initialPopulateOnCreate() // TODO review content
    {
        THLog.i(TAG, "initialPopulateOnCreate populatedOnCreate " + (populatedOnCreate ? "true" : "false"));
        if (populatedOnCreate)
        {
            return;
        }

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
            populatedOnCreate = true;
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
            populatedOnCreate = true;
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        securityItemViewAdapter.notifyDataSetChanged();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        THLog.i(TAG, "onSaveInstanceState");
        putParameters(outState);
        super.onSaveInstanceState(outState);
    }

    @Override public void onDestroyOptionsMenu()
    {
        THLog.d(TAG, "onDestroyOptionsMenu");
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
        THLog.d(TAG, "onDestroyView");
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

    protected void requestData()
    {
        THLog.i(TAG, "requestData");
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
            if (securitySearchListener == null)
            {
                securitySearchListener = new DTOCache.Listener<SecurityListType, SecurityIdList>()
                {
                    @Override public void onDTOReceived(SecurityListType key, SecurityIdList value)
                    {
                        THLog.i(TAG, "onDTOReceived SecurityIdList");
                        setQuerying(false);
                        linkWith(securityCompactCache.get().getOrFetch(value), true, (SecurityCompactDTO) null);
                    }
                };
            }
            cancelSearchTasks();
            setQuerying(true);
            securitySearchTask = securityCompactListCache.get().getOrFetch(makeSearchSecurityListType(), securitySearchListener);
            securitySearchTask.execute();
        }
    }

    private void requestPeople()
    {
        if (mSearchText != null && !mSearchText.isEmpty())
        {
            UserListType searchUserListType = makeSearchUserListType();
            UserBaseKeyList userBaseKeys = userBaseKeyListCache.get().get(searchUserListType);
            if (userBaseKeys != null)
            {
                linkWith(userSearchResultCache.get().get(userBaseKeys), true, (UserSearchResultDTO) null);
            }
            else
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
                    };
                }
                cancelSearchTasks();
                setQuerying(true);
                peopleSearchTask = userBaseKeyListCache.get().getOrFetch(makeSearchUserListType(), peopleSearchListener);
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
        THLog.i(TAG, "linkWith");
        this.securityList = securityCompactDTOs;

        securityItemViewAdapter.setItems(securityCompactDTOs);
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

        //mSearchStockListView.invalidate();
    }

    private void linkWith(final List<UserSearchResultDTO> users, boolean andDisplay, UserSearchResultDTO typeQualifier)
    {
        this.userDTOList = users;

        peopleItemViewAdapter.setItems(users);
        if (andDisplay)
        {
            getView().post(new Runnable()
            {
                @Override public void run()
                {
                    peopleItemViewAdapter.notifyDataSetChanged();

                    // All these damn HACKs are not enough to have the list update itself!
                    mSearchPeopleListView.invalidateViews();
                    mSearchPeopleListView.scrollBy(0, 0);
                    mSearchPeopleListView.refreshDrawableState();

                    updateVisibilities();
                }
            });
        }
    }

    public void setQuerying(boolean querying)
    {
        isQuerying = querying;
        updateVisibilities();
    }

    private void updateVisibilities()
    {
        THLog.i(TAG, "updateVisibilities");
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
            args.putInt(BUNDLE_KEY_SEARCH_TYPE, mSearchType.getValue());
            args.putString(BUNDLE_KEY_SEARCH_STRING, mSearchText);
            args.putInt(BUNDLE_KEY_PAGE, page);
            args.putInt(BUNDLE_KEY_PER_PAGE, perPage);
        }
    }

    protected void collectParameters(Bundle args)
    {
        if (args != null)
        {
            mSearchType = TrendingSearchType.fromInt(args.getInt(BUNDLE_KEY_SEARCH_TYPE, 0));
            mSearchText = args.getString(BUNDLE_KEY_SEARCH_STRING);
            page = args.getInt(BUNDLE_KEY_PAGE, DEFAULT_PAGE);
            perPage = args.getInt(BUNDLE_KEY_PER_PAGE, DEFAULT_PER_PAGE);
        }
    }

    private void pushTradeFragmentIn(SecurityCompactDTO securityCompactDTO)
    {
        if (securityCompactDTO == null || securityCompactDTO.getSecurityId() == null)
        {
            THLog.e(TAG, "Cannot handle null " + securityCompactDTO, new IllegalArgumentException());
            return;
        }
        navigator.pushFragment(TradeFragment.class, securityCompactDTO.getSecurityId().getArgs());
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
        args.putInt(UserBaseKey.BUNDLE_KEY_KEY, userSearchResultDTO.userId);

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

    public SearchSecurityListType makeSearchSecurityListType()
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
        getClass();
    }

    @Override public void beforeTextChanged(CharSequence charSequence, int start, int count, int after)
    {
        getClass();
    }

    @Override public void onTextChanged(CharSequence charSequence, int start, int before, int count)
    {
        mSearchText = charSequence.toString();
        if (mSearchText == null || mSearchText.length() == 0)
        {
            linkWith(null, true, (SecurityCompactDTO) null);
            linkWith(null, true, (UserSearchResultDTO) null);
        }
        else
        {
            requestData();
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
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="BaseFragment.ArgumentsChangeListener">
    @Override public void onArgumentsChanged(Bundle args)
    {
        desiredArguments = args;
    }
    //</editor-fold>
}
