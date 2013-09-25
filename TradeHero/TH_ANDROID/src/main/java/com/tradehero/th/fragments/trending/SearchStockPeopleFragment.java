package com.tradehero.th.fragments.trending;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.adapter.SpinnerIconAdapter;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.SearchPeopleAdapter;
import com.tradehero.th.adapters.TrendingAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.network.CallbackWithSpecificNotifiers;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.network.service.UserService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import retrofit.RetrofitError;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: xavier Date: 9/18/13 Time: 12:09 PM To change this template use File | Settings | File Templates. */
public class SearchStockPeopleFragment extends SherlockFragment implements AdapterView.OnItemSelectedListener, TextWatcher
{
    public final static String KEY_SAVE_SEARCH_STRING = "searchString";
    public final static String KEY_SAVE_SEARCH_TYPE = "searchType";
    public final static String KEY_SAVE_PAGE = "page";
    public final static String KEY_SAVE_PER_PAGE = "perPage";
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
    private BackRequestedListener backRequestedListener;
    private SpinnerIconAdapter mSearchTypeSpinnerAdapter;
    private Spinner mSearchTypeSpinner;
    private TrendingSearchType mSearchType;
    private EditText mSearchTextField;
    private String mSearchText;
    private boolean populatedOnCreate = false;

    private boolean isQuerying;
    private SecurityService securityService;
    private CallbackWithSpecificNotifiers<List<SecurityCompactDTO>> securityCallback;
    private List<SecurityCompactDTO> securityList;
    protected TrendingAdapter trendingAdapter;

    private UserService userService;
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

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.i(TAG, "onCreateView");
        if (savedInstanceState != null)
        {
            THLog.i(TAG, "onCreateView restoring savedInstance");
            mSearchType = TrendingSearchType.fromInt(savedInstanceState.getInt(KEY_SAVE_SEARCH_TYPE, 0));
            mSearchText = savedInstanceState.getString(KEY_SAVE_SEARCH_STRING);
            page = savedInstanceState.getInt(KEY_SAVE_PAGE, DEFAULT_PAGE);
            perPage = savedInstanceState.getInt(KEY_SAVE_PER_PAGE, DEFAULT_PER_PAGE);
        }
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
        mSearchStockListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) parent.getItemAtPosition(position);

                THToast.show("Disabled for now");
                // TODO put back in
            }
        });
        mSearchPeopleListView = (ListView) view.findViewById(R.id.people_listview);
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
                notifyBackRequested();
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

    protected void refreshListView()
    {
        THLog.i(TAG, "refreshListView");
        if (mSearchType == null)
        {
            // Do nothing
        }
        else if (mSearchType == TrendingSearchType.STOCKS)
        {
            if (securityService == null)
            {
                securityService = NetworkEngine.createService(SecurityService.class);
            }
            if (securityCallback == null)
            {
                securityCallback = createCallbackForStock();
            }
            isQuerying = true;
            securityService.searchSecurities(mSearchText, getPage(), getPerPage(), securityCallback);
        }
        else if (mSearchType == TrendingSearchType.PEOPLE)
        {
            if (userService == null)
            {
                userService = NetworkEngine.createService(UserService.class);
            }
            if (peopleCallback == null)
            {
                peopleCallback = createCallbackForPeople();
            }
            isQuerying = true;
            userService.searchUsers(mSearchText, getPage(), getPerPage(), peopleCallback);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled SearchType." + mSearchType);
        }
        updateVisibilities();
    }

    private void setDataAdapterToStockListView(List<SecurityCompactDTO> securityCompactDTOs)
    {
        THLog.i(TAG, "setDataAdapterToStockListView");
        this.securityList = securityCompactDTOs;

        if (trendingAdapter == null && securityCompactDTOs != null)
        {
            // The new ArrayList ensures that we can clear the adapter without clearing this.securityList
            // new ArrayList<>(securityCompactDTOs)
            trendingAdapter = new TrendingAdapter(getActivity(), getActivity().getLayoutInflater(), TrendingAdapter.SECURITY_SEARCH_CELL_LAYOUT);
        }
        else if (trendingAdapter == null && securityCompactDTOs == null)
        {
            trendingAdapter = new TrendingAdapter(getActivity(), getActivity().getLayoutInflater(), TrendingAdapter.SECURITY_SEARCH_CELL_LAYOUT);
        }
        else
        {
            //trendingAdapter.clear();
            //if (securityCompactDTOs != null)
            //{
            //    trendingAdapter.addAll(securityCompactDTOs);
            //}
            // TODO implement loader pattern
        }
        mSearchStockListView.setAdapter(trendingAdapter);
        updateVisibilities();
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
        if (outState != null)
        {
            outState.putInt(KEY_SAVE_SEARCH_TYPE, mSearchType.getValue());
            outState.putString(KEY_SAVE_SEARCH_STRING, mSearchText);
            outState.putInt(KEY_SAVE_PAGE, page);
            outState.putInt(KEY_SAVE_PER_PAGE, perPage);
        }
        super.onSaveInstanceState(outState);
    }

    private void updateVisibilities()
    {
        THLog.i(TAG, "updateVisibilities");
        mProgressSpinner.setVisibility(isQuerying ? View.VISIBLE : View.INVISIBLE);

        if (mSearchText == null || mSearchText.length() == 0 || mSearchType == null)
        {
            mNothingYet.setVisibility(View.VISIBLE);
            mSearchStockListView.setVisibility(View.INVISIBLE);
            mSearchPeopleListView.setVisibility(View.INVISIBLE);
        }
        else if (mSearchType == TrendingSearchType.STOCKS)
        {
            mSearchStockListView.setVisibility(View.VISIBLE);
            mSearchPeopleListView.setVisibility(View.INVISIBLE);
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
            mSearchStockListView.setVisibility(View.INVISIBLE);
            mSearchPeopleListView.setVisibility(View.VISIBLE);
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

    //<editor-fold desc="Accessors">
    public int getPage()
    {
        return page;
    }

    public int getPerPage()
    {
        return perPage;
    }
    //</editor-fold>

    private CallbackWithSpecificNotifiers<List<SecurityCompactDTO>> createCallbackForStock()
    {
        return new CallbackWithSpecificNotifiers<List<SecurityCompactDTO>>()
        {
            @Override public void notifyIsQuerying(boolean isQuerying)
            {
                SearchStockPeopleFragment.this.isQuerying = isQuerying;
            }

            @Override public void success(List<SecurityCompactDTO> returned, Response response)
            {
                super.success(returned, response);
                setDataAdapterToStockListView(returned);
            }

            @Override public void failure(RetrofitError retrofitError)
            {
                super.failure(retrofitError);
                updateVisibilities();
            }
        };
    }

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

    public void setBackRequestedListener(BackRequestedListener backRequestedListener)
    {
        this.backRequestedListener = backRequestedListener;
    }

    private void notifyBackRequested()
    {
        if (this.backRequestedListener != null)
        {
            this.backRequestedListener.onBackRequested();
        }
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

    public interface BackRequestedListener
    {
        void onBackRequested();
    }

}
