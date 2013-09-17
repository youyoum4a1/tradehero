package com.tradehero.th.fragments.trending;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tradehero.common.cache.KnownCaches;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TrendingAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.application.Config;
import com.tradehero.th.http.THAsyncClientFactory;
import com.tradehero.th.models.User;
import com.tradehero.th.network.CallbackWithSpecificNotifiers;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.utills.Constants;
import com.tradehero.th.widget.trending.TrendingGridView;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TrendingFragment extends AbstractTrendingFragment
{
    private final static String TAG = TrendingFragment.class.getSimpleName();
    private final static String[] SEARCH_TYPE = { "Stocks", "People"};

    private TrendingGridView mTrendingGridView;

    private List<SecurityCompactDTO> securityCompactDTOs;
    private List<User> searchPeopleList;

    private TextView mHeaderText;
    private EditText mSearchField;
    private View mSearchContainer;
    private ImageButton mBackBtn;
    private ImageButton mSearchBtn;
    private ImageView mBullIcon;
    private RelativeLayout header;
    private SecurityService securityService;

    @Override protected String getLogTag()
    {
        return TAG;
    }

    @Override protected int getLayoutResourceId()
    {
        return R.layout.fragment_trending;
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        setHasOptionsMenu(true);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override protected void initViews(View view)
    {
        super.initViews(view);
        mTrendingGridView = (TrendingGridView) view.findViewById(R.id.trending_gridview);

        //mSearchTypeSpinner = (Spinner) view.findViewById(R.id.spinner);
        //mHeaderText = (TextView) view.findViewById(R.id.header_txt);
        //mSearchField = (EditText) view.findViewById(R.id.searh_field);
        mBullIcon = (ImageView) view.findViewById(R.id.logo_img);
        //mSearchBtn = (ImageButton) view.findViewById(R.id.btn_search);
        //mSearchBtn.setVisibility(View.VISIBLE);
        //mBackBtn = (ImageButton) view.findViewById(R.id.btn_back);
        mSearchContainer = (RelativeLayout) view.findViewById(R.id.search_container);

        // TODO header bar
        //header = (RelativeLayout) getActivity().findViewById(R.id.top_tabactivity);
        //header.setVisibility(View.GONE);
        //mHeaderText.setText(R.string.header_trending);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, SEARCH_TYPE);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //mSearchTypeSpinner.setAdapter(adapter);

        //mSearchField.addTextChangedListener(new SearchFieldWatcher());

        // HACK
        KnownCaches.getTransparentBg().clear();
        KnownCaches.getGreyGaussian().clear();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // TODO sliding menu
        //((TradeHeroTabActivity) getActivity()).showSlidingMenue(true);
        //mBackBtn.setOnClickListener(new OnClickListener()
        //{
        //    @Override
        //    public void onClick(View v)
        //    {
        //        showSearchView(false);
        //        showSearchList(false);
        //    }
        //});

        //mSearchBtn.setOnClickListener(new OnClickListener()
        //{
        //    @Override
        //    public void onClick(View v)
        //    {
        //        showSearchView(true);
        //    }
        //});

        if (securityCompactDTOs != null && securityCompactDTOs.size() > 0)
        {
            setDataAdapterToGridView(securityCompactDTOs);
        }
        //else {
        //	securityCompactDTOs = new ArrayList<Trend>();
        //	mTrendingGridView.setAdapter(new TrendingAdapter(getActivity(), securityCompactDTOs));
        //}

        //mSearchTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
        //{
        //    @Override
        //    public void onItemSelected(AdapterView<?> arg0, View arg1,
        //            int arg2, long arg3)
        //    {
        //        mSearchField.setText("");
        //    }
        //
        //    @Override
        //    public void onNothingSelected(AdapterView<?> arg0)
        //    {
        //
        //    }
        //});

        mTrendingGridView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id)
            {
                SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) parent.getItemAtPosition(position);

                THToast.show("Disabled for now");

                // TODO put back in
                //((DashboardActivity)getActivity()).pushTrendingDetailFragment(securityCompactDTO);
            }
        });

        if (mTrendingGridView != null && mTrendingGridView.getCount() == 0)
        {
            showProgressSpinner(true);
        }

        refreshGridView();
    }

    @Override public void onResume()
    {
        THLog.i(TAG, "onResume");
        super.onResume();
        // TODO move to somewhere else
        //((TradeHeroTabActivity) getActivity()).showTabs(true);
        //((App) getActivity().getApplication()).setTrend(null);
    }

    @Override public void onPause()
    {
        THLog.i(TAG, "onPause");
        super.onPause();
    }

    @Override public void onDetach()
    {
        THLog.i(TAG, "Detached from activity");
        super.onDetach();
    }

    private void setDataAdapterToGridView(List<SecurityCompactDTO> trendList)
    {
        this.securityCompactDTOs = trendList;
        if (trendingAdapter == null)
        {
            trendingAdapter = new TrendingAdapter(getActivity(), trendList);
        }
        else
        {
            // TODO implement loader pattern
        }
        mTrendingGridView.setAdapter(trendingAdapter);
        showProgressSpinner(false);
    }

    @Override protected void refreshGridView()
    {
        if (securityService == null)
        {
            securityService = NetworkEngine.createService(SecurityService.class);
        }
        securityService.getTrendingSecurities(createCallbackForTrending());
    }

    private CallbackWithSpecificNotifiers<List<SecurityCompactDTO>> createCallbackForTrending ()
    {
        return new CallbackWithSpecificNotifiers<List<SecurityCompactDTO>>()
        {
            @Override public void notifyIsQuerying(boolean isQuerying)
            {
            }

            @Override public void success(List<SecurityCompactDTO> returned, Response response)
            {
                super.success(returned, response);
                setDataAdapterToGridView(returned);
            }

            @Override public void failure(RetrofitError retrofitError)
            {
                super.failure(retrofitError);
            }
        };
    }

    @Override public boolean isRequiredToAct()
    {
        return getActionBarStatus() == null || getActionBarStatus().searchType == null || getActionBarStatus().searchText == null;
    }
}
