package com.tradehero.th.fragments.trending;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.adapter.SpinnerIconAdapter;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.adapters.TrendingAdapter;
import com.tradehero.th.widget.trending.TrendingBarListener;
import com.tradehero.th.widget.trending.TrendingBarStatusDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 9/17/13 Time: 2:36 PM To change this template use File | Settings | File Templates. */
abstract public class AbstractTrendingFragment extends SherlockFragment implements TrendingBarListener.Callback
{
    public final static String KEY_SAVE_SEARCH_STRING = "searchString";
    public final static String KEY_SAVE_SEARCH_TYPE = "searchType";
    public final static String KEY_SAVE_PAGE = "page";
    public final static String KEY_SAVE_PER_PAGE = "perPage";

    public final static int DEFAULT_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 15;

    private CharSequence[] dropDownTexts;
    private Drawable[] dropDownIcons;

    protected TrendingAdapter trendingAdapter;
    private ProgressBar mProgressSpinner;
    private View actionBar;

    private SpinnerIconAdapter mSearchTypeSpinnerAdapter;

    private TrendingBarStatusDTO actionBarStatus;

    private Spinner mSearchTypeSpinner;
    private EditText mSearchTextField;
    private TrendingBarListener actionBarListener;

    private int page = DEFAULT_PAGE;
    private int perPage = DEFAULT_PER_PAGE;

    abstract protected String getLogTag();

    abstract protected int getLayoutResourceId();

    abstract public boolean isRequiredToAct();

    abstract protected void refreshGridView();

    @Override public void onAttach(Activity activity)
    {
        THLog.i(getLogTag(), "Attached to activity");
        super.onAttach(activity);
        dropDownTexts = new CharSequence[TrendingSearchType.values().length];
        dropDownIcons = new Drawable[TrendingSearchType.values().length];

        for(TrendingSearchType searchType: TrendingSearchType.values())
        {
            dropDownTexts[searchType.getValue()] = activity.getResources().getString(TrendingSearchType.getStringResourceId(searchType));
            dropDownIcons[searchType.getValue()] = activity.getResources().getDrawable(TrendingSearchType.getDrawableResourceId(searchType));
        }

        THLog.i(getLogTag(), "onAttach texts length:" + dropDownTexts.length + " icons length " + dropDownIcons.length);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            actionBarStatus = new TrendingBarStatusDTO(
                    TrendingSearchType.fromInt(savedInstanceState.getInt(KEY_SAVE_SEARCH_TYPE, 0)),
                    savedInstanceState.getString(KEY_SAVE_SEARCH_STRING)
            );
            page = savedInstanceState.getInt(KEY_SAVE_PAGE, DEFAULT_PAGE);
            perPage = savedInstanceState.getInt(KEY_SAVE_PER_PAGE, DEFAULT_PER_PAGE);
        }
        View view = inflater.inflate(getLayoutResourceId(), container, false);
        initViews(view);
        return view;
    }

    protected void initViews(View view)
    {
        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        THLog.i(getLogTag(), "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
        conditionalCreateSearchActionBar(menu, inflater);
    }

    private void conditionalCreateSearchActionBar(Menu menu, MenuInflater inflater)
    {
        View currentBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        if (currentBarView == null || currentBarView.getId() != R.layout.trending_topbar)
        {
            createSearchActionBar(menu, inflater);
        }
    }

    private void createSearchActionBar(Menu menu, MenuInflater inflater)
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSherlockActivity().getSupportActionBar().setCustomView(R.layout.trending_topbar);

        actionBar = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBar.findViewById(R.id.header_txt)).setText(R.string.header_trending);

        THLog.i(getLogTag(), "onCreateOptionsMenu texts length:" + dropDownTexts.length + " icons length " + dropDownIcons.length);

        mSearchTypeSpinner = (Spinner) actionBar.findViewById(R.id.spinner);
        if (mSearchTypeSpinnerAdapter == null)
        {
            mSearchTypeSpinnerAdapter = new SpinnerIconAdapter(
                    getActivity(),
                    R.layout.search_spinner_item,
                    R.id.search_spinner_item_label,
                    R.id.search_spinner_item_icon,
                    dropDownTexts,
                    dropDownIcons);
        }
        mSearchTypeSpinnerAdapter.setDropDownViewResource(R.layout.search_spinner_dropdown_item);
        mSearchTypeSpinner.setAdapter(mSearchTypeSpinnerAdapter);

        if (actionBarListener == null)
        {
            actionBarListener = new TrendingBarListener();
        }

        mSearchTypeSpinner.setOnItemSelectedListener(actionBarListener); // TODO do it on resume / pause?

        mSearchTextField = (EditText) actionBar.findViewById(R.id.search_field);
        mSearchTextField.addTextChangedListener(actionBarListener);

        setAsUniqueActionBarListener(actionBarListener);
    }

    public void setAsUniqueActionBarListener(TrendingBarListener actionBarListener)
    {
        this.actionBarListener = actionBarListener;
        actionBarStatus = actionBarListener.getCurrentStatus();
        actionBarListener.clearCallbacks();
        actionBarListener.addCallback(this);
        refreshGridView();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        if (outState != null && actionBarStatus != null)
        {
            outState.putInt(KEY_SAVE_SEARCH_TYPE, actionBarStatus.searchType.getValue());
            outState.putString(KEY_SAVE_SEARCH_STRING, actionBarStatus.searchText);
            outState.putInt(KEY_SAVE_PAGE, page);
            outState.putInt(KEY_SAVE_PER_PAGE, perPage);
        }
        super.onSaveInstanceState(outState);
    }

    protected void showProgressSpinner(boolean flag)
    {
        mProgressSpinner.setVisibility(getVisibility(flag));
    }

    protected int getVisibility(boolean flag)
    {
        return flag ? View.VISIBLE : View.INVISIBLE;
    }

    @Override public void onTrendingBarChanged(TrendingBarStatusDTO trendingBarStatusDTO)
    {
        actionBarStatus = trendingBarStatusDTO;
        pushRightFragment();
    }

    protected void pushRightFragment()
    {
        ((DashboardActivity) getActivity()).conditionalSetCurrentFragmentByClass(TrendingFragmentFactory.getTrendingFragmentClass(actionBarStatus));
        makeCurrentFragmentTheActionBarListener();
    }

    protected void makeCurrentFragmentTheActionBarListener()
    {
        Fragment currentFragment = ((DashboardActivity) getActivity()).getCurrentFragment();
        ((AbstractTrendingFragment) currentFragment).setAsUniqueActionBarListener(actionBarListener);
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

    public TrendingBarStatusDTO getActionBarStatus()
    {
        return actionBarStatus;
    }
    //</editor-fold>
}
