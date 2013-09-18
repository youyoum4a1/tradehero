package com.tradehero.th.fragments.trending;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TrendingAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.network.CallbackWithSpecificNotifiers;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.SecurityService;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchStockFragment extends AbstractTrendingFragment
{
    private final static String TAG = SearchStockFragment.class.getSimpleName();

    private ListView mSearchListView;
    private ProgressBar mProgressSpinner;
    private View actionBar;

    private TrendingAdapter trendingAdapter;

    private SecurityService securityService;
    private boolean isQuerying;
    private List<SecurityCompactDTO> securityList;

    @Override protected String getLogTag()
    {
        return TAG;
    }

    @Override protected int getLayoutResourceId()
    {
        return R.layout.fragment_search_stock;
    }

    @Override public void onAttach(Activity activity)
    {
        THLog.i(TAG, "Attached to activity");
        super.onAttach(activity);
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
        mSearchListView = (ListView) view.findViewById(R.id.trending_listview);
        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (securityList != null && securityList.size() > 0)
        {
            setDataAdapterToListView(securityList);
        }

        mSearchListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) parent.getItemAtPosition(position);

                THToast.show("Disabled for now");
                // TODO put back in
            }
        });

        refreshGridView();
    }

    @Override public void onDetach()
    {
        THLog.i(TAG, "Detached from activity");
        super.onDetach();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        THLog.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setDataAdapterToListView(List<SecurityCompactDTO> securityCompactDTOs)
    {
        this.securityList = securityCompactDTOs;

        if (trendingAdapter == null)
        {
            trendingAdapter = new TrendingAdapter(getActivity(), securityCompactDTOs, TrendingAdapter.SECURITY_SEARCH_CELL_LAYOUT);
        }
        else
        {
            trendingAdapter.clear();
            trendingAdapter.addAll(securityCompactDTOs);
            // TODO implement loader pattern
        }
        mSearchListView.setAdapter(trendingAdapter);
    }

    @Override protected void refreshGridView()
    {
        if (isRequiredToAct() && !isQuerying)
        {
            if (securityService == null)
            {
                securityService = NetworkEngine.createService(SecurityService.class);
            }
            securityService.searchSecurities(getActionBarStatus().searchText, getPage(), getPerPage(), createCallbackForTrending());
        }
    }

    private CallbackWithSpecificNotifiers<List<SecurityCompactDTO>> createCallbackForTrending ()
    {
        return new CallbackWithSpecificNotifiers<List<SecurityCompactDTO>>()
        {
            @Override public void notifyIsQuerying(boolean isQuerying)
            {
                SearchStockFragment.this.isQuerying = isQuerying;
            }

            @Override public void success(List<SecurityCompactDTO> returned, Response response)
            {
                super.success(returned, response);
                setDataAdapterToListView(returned);
            }

            @Override public void failure(RetrofitError retrofitError)
            {
                super.failure(retrofitError);
            }
        };
    }

    @Override public boolean isRequiredToAct()
    {
        return getActionBarStatus() != null && getActionBarStatus().searchType == TrendingSearchType.STOCKS && getActionBarStatus().searchText != null;
    }
}
