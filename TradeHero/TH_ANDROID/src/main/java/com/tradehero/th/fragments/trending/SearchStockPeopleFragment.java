package com.tradehero.th.fragments.trending;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
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
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.SearchPeopleAdapter;
import com.tradehero.th.adapters.TrendingAdapter;
import com.tradehero.th.api.security.SearchSecurityListType;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.trade.TradeFragment;
import com.tradehero.th.network.CallbackWithSpecificNotifiers;
import com.tradehero.th.network.service.UserService;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.persistence.security.SecuritySearchQuery;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 9/18/13 Time: 12:09 PM To change this template use File | Settings | File Templates. */
public class SearchStockPeopleFragment extends DashboardFragment
        implements AdapterView.OnItemSelectedListener, TextWatcher, DTOCache.Listener<SecurityListType, List<SecurityId>>
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
    private boolean populatedOnCreate = false;

    private boolean isQuerying;

    @Inject Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    private AsyncTask<Void, Void, List<SecurityId>> securitySearchTask;
    private List<SecurityCompactDTO> securityList;
    private TrendingAdapter trendingAdapter;

    @Inject UserService userService;
    private CallbackWithSpecificNotifiers<List<UserSearchResultDTO>> peopleCallback;
    private List<UserSearchResultDTO> userDTOList;
    private SearchPeopleAdapter searchPeopleAdapter;

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

        setHasOptionsMenu(true);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getStatus(getArguments());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.i(TAG, "onCreateView");
        getStatus(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search_stock, container, false);
        initViews(view);
        populatedOnCreate = false;
        return view;
    }

    protected void initViews(View view)
    {
        mNothingYet = (TextView) view.findViewById(R.id.search_stock_nothing_yet_view);
        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);
        mSearchStockListView = (ListView) view.findViewById(R.id.trending_listview);

        trendingAdapter = new TrendingAdapter(getActivity(), getActivity().getLayoutInflater(), TrendingAdapter.SECURITY_SEARCH_CELL_LAYOUT);
        if (mSearchStockListView != null)
        {
            mSearchStockListView.setAdapter(trendingAdapter);
            mSearchStockListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) parent.getItemAtPosition(position);
                    navigator.pushFragment(TradeFragment.class, securityCompactDTO.getSecurityId().getArgs());
                }
            });
        }

        mSearchPeopleListView = (ListView) view.findViewById(R.id.people_listview);
        if (mSearchPeopleListView != null)
        {
            mSearchPeopleListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    UserSearchResultDTO userSearchResultDTO = (UserSearchResultDTO) parent.getItemAtPosition(position);

                    THToast.show("Disabled for now");
                    // TODO put back in
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
        getSherlockActivity().getSupportActionBar().setCustomView(R.layout.trending_search_topbar);

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

    private void initialPopulateOnCreate()
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
                setDataAdapterToStockListView(securityList);
            }
            else
            {
                refreshListView();
            }
            populatedOnCreate = true;
        }
        else if (mSearchType == TrendingSearchType.PEOPLE)
        {
            if (userDTOList != null && userDTOList.size() > 0)
            {
                setDataAdapterToPeopleListView(userDTOList);
            }
            else
            {
                refreshListView();
            }
            populatedOnCreate = true;
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        trendingAdapter.notifyDataSetChanged();
    }

    protected void refreshListView()
    {
        THLog.i(TAG, "refreshListView");
        if (mSearchType == null)
        {
            // Do nothing
        }
        else if (mSearchType == TrendingSearchType.STOCKS)
        {
            if (mSearchText != null && !mSearchText.isEmpty())
            {
                if (securitySearchTask != null)
                {
                    securitySearchTask.cancel(false);
                    securitySearchTask = null;
                }
                isQuerying = true;
                securitySearchTask = securityCompactListCache.get().getOrFetch(makeSearchSecurityListType(), false, this);
                securitySearchTask.execute();
            }
        }
        else if (mSearchType == TrendingSearchType.PEOPLE)
        {
            if (mSearchText != null && !mSearchText.isEmpty())
            {
                if (peopleCallback == null)
                {
                    peopleCallback = createCallbackForPeople();
                }
                isQuerying = true;
                userService.searchUsers(mSearchText, getPage(), getPerPage(), peopleCallback);
            }
        }
        else
        {
            throw new IllegalArgumentException("Unhandled SearchType." + mSearchType);
        }
        updateVisibilities();
    }

    @Override public void onDTOReceived(SecurityListType key, List<SecurityId> value)
    {
        THLog.i(TAG, "onDTOReceived");
        isQuerying = false;
        setDataAdapterToStockListView(securityCompactCache.get().getOrFetch(value));
    }

    private void setDataAdapterToStockListView(List<SecurityCompactDTO> securityCompactDTOs)
    {
        THLog.i(TAG, "setDataAdapterToStockListView");
        this.securityList = securityCompactDTOs;

        trendingAdapter.setItems(securityCompactDTOs);
        trendingAdapter.notifyDataSetChanged();
        //getView().forceLayout();
        mSearchStockListView.invalidate();
        //freshDrawableState();
        //getView().invalidate();
        updateVisibilities();
        //THLog.d(TAG, "Is ui thread " + (Looper.getMainLooper().getThread() == Thread.currentThread() ? "true" : "false"));
    }

    private void setDataAdapterToPeopleListView(List<UserSearchResultDTO> users)
    {
        this.userDTOList = users;

        if (searchPeopleAdapter == null && users != null)
        {
            searchPeopleAdapter = new SearchPeopleAdapter(getActivity(), users);
        }
        else if (searchPeopleAdapter == null && users == null)
        {
            searchPeopleAdapter = new SearchPeopleAdapter(getActivity(), new ArrayList<UserSearchResultDTO>());
        }
        else
        {
            searchPeopleAdapter.clear();
            if (users != null)
            {
                searchPeopleAdapter.addAll(users);
            }
            // TODO implement loader pattern
        }
        mSearchPeopleListView.setAdapter(searchPeopleAdapter);
        updateVisibilities();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        THLog.i(TAG, "onSaveInstanceState");
        putStatus(outState);
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
        if (securitySearchTask != null)
        {
            securitySearchTask.cancel(false);
        }
        securitySearchTask = null;
        mSearchStockListView = null; // To break the cycle link with the adapter
        mSearchPeopleListView = null;
        super.onDestroyView();
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

    protected void putStatus(Bundle args)
    {
        if (args != null)
        {
            args.putInt(BUNDLE_KEY_SEARCH_TYPE, mSearchType.getValue());
            args.putString(BUNDLE_KEY_SEARCH_STRING, mSearchText);
            args.putInt(BUNDLE_KEY_PAGE, page);
            args.putInt(BUNDLE_KEY_PER_PAGE, perPage);
        }
    }

    protected void getStatus(Bundle args)
    {
        if (args != null)
        {
            mSearchType = TrendingSearchType.fromInt(args.getInt(BUNDLE_KEY_SEARCH_TYPE, 0));
            mSearchText = args.getString(BUNDLE_KEY_SEARCH_STRING);
            page = args.getInt(BUNDLE_KEY_PAGE, DEFAULT_PAGE);
            perPage = args.getInt(BUNDLE_KEY_PER_PAGE, DEFAULT_PER_PAGE);
        }
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

    public SecuritySearchQuery getSecuritySearchQuery()
    {
        return new SecuritySearchQuery(mSearchText, page, perPage);
    }

    public SearchSecurityListType makeSearchSecurityListType()
    {
        return new SearchSecurityListType(mSearchText, page, perPage);
    }
    //</editor-fold>

    private CallbackWithSpecificNotifiers<List<UserSearchResultDTO>> createCallbackForPeople ()
    {
        return new CallbackWithSpecificNotifiers<List<UserSearchResultDTO>>()
        {
            @Override public void notifyIsQuerying(boolean isQuerying)
            {
                SearchStockPeopleFragment.this.isQuerying= isQuerying;
            }

            @Override public void success(List<UserSearchResultDTO> returned, Response response)
            {
                super.success(returned, response);
                setDataAdapterToPeopleListView(returned);
            }

            @Override public void failure(RetrofitError retrofitError)
            {
                super.failure(retrofitError);
                updateVisibilities();
            }
        };
    }

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
        if (mSearchText == null || mSearchText.length() == 0)
        {
            setDataAdapterToStockListView(null);
            setDataAdapterToPeopleListView(null);
        }
        else
        {
            refreshListView();
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
            refreshListView();
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView)
    {
        THLog.i(TAG, "onNothingSelected Spinner");
        mSearchType = null;
        updateVisibilities();
    }
    //</editor-fold>
}
