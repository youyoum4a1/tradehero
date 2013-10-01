package com.tradehero.th.fragments.trending;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TrendingAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.CallbackWithSpecificNotifiers;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.persistence.security.SecurityStoreManager;
import com.tradehero.th.widget.trending.TrendingGridView;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TrendingFragment extends DashboardFragment
{
    private final static String TAG = TrendingFragment.class.getSimpleName();

    private View actionBar;
    private ImageView mBullIcon;
    private TextView mHeaderText;
    private ImageButton mSearchBtn;

    private ProgressBar mProgressSpinner;
    private TrendingGridView mTrendingGridView;

    @Inject SecurityStoreManager securityManager;

    private List<SecurityCompactDTO> securityCompactDTOs;
    protected TrendingAdapter trendingAdapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        THLog.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        initViews(view);
        return view;
    }

    protected void initViews(View view)
    {
        mTrendingGridView = (TrendingGridView) view.findViewById(R.id.trending_gridview);
        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);
        mBullIcon = (ImageView) view.findViewById(R.id.logo_img);


        THLog.i(TAG, "onActivityCreated");

        if (securityCompactDTOs != null && securityCompactDTOs.size() > 0)
        {
            setDataAdapterToGridView(securityCompactDTOs);
        }
        else
        {
            refreshGridView();
        }

        mTrendingGridView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id)
            {
                SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) parent.getItemAtPosition(position);
                THToast.show("Disabled for now");
            }
        });

        if (mTrendingGridView.getCount() == 0)
        {
            showProgressSpinner(true);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        THLog.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
        createHeaderActionBar(menu, inflater);
    }

    private void createHeaderActionBar(Menu menu, MenuInflater inflater)
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSherlockActivity().getSupportActionBar().setCustomView(R.layout.trending_topbar);

        actionBar = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBar.findViewById(R.id.header_txt)).setText(R.string.header_trending);

        mSearchBtn = (ImageButton) actionBar.findViewById(R.id.btn_search);
        mSearchBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                handleSearchRequested();
            }
        });
    }

    private void setDataAdapterToGridView(List<SecurityCompactDTO> trendList)
    {
        this.securityCompactDTOs = trendList;
        if (trendingAdapter == null)
        {
            // trendList
            trendingAdapter = new TrendingAdapter(getActivity(), getActivity().getLayoutInflater(), TrendingAdapter.SECURITY_TRENDING_CELL_LAYOUT);
            trendingAdapter.setItems(trendList);
        }
        else
        {
            // TODO implement loader pattern
        }
        mTrendingGridView.setAdapter(trendingAdapter);
        showProgressSpinner(false);
    }

    protected void refreshGridView()
    {
        AsyncTask<Void, Void, List<SecurityCompactDTO>> refreshTask = createAsyncTaskForTrending();
        refreshTask.execute();
    }

    private AsyncTask<Void, Void, List<SecurityCompactDTO>> createAsyncTaskForTrending()
    {
        return new AsyncTask<Void, Void, List<SecurityCompactDTO>>()
        {
            @Override protected List<SecurityCompactDTO> doInBackground(Void... voids)
            {
                try
                {
                    return securityManager.getTrending(true);
                }
                catch (IOException e)
                {
                    THToast.show(R.string.error_unknown);
                    THLog.e(TAG, "Error when refreshing grid", e);
                }
                catch (RetrofitError e)
                {
                    THToast.show(R.string.error_network_connection);
                    THLog.e(TAG, "Error when refreshing grid", e);
                }
                return null;
            }

            @Override protected void onPostExecute(List<SecurityCompactDTO> securityCompactDTOs)
            {
                super.onPostExecute(securityCompactDTOs);
                setDataAdapterToGridView(securityCompactDTOs);
            }
        };
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

    public boolean isRequiredToAct()
    {
        return true;
    }

    protected void showProgressSpinner(boolean flag)
    {
        mProgressSpinner.setVisibility(getVisibility(flag));
    }

    protected int getVisibility(boolean flag)
    {
        return flag ? View.VISIBLE : View.INVISIBLE;
    }

    public void setTradeRequestedListener(OnTradeRequestedListener tradeRequestedListener)
    {
        Bundle args = new Bundle();
        // TODO put stock id
        navigator.pushFragment(TradeFragment.class);
    }

    private void handleSearchRequested()
    {
        navigator.pushFragment(SearchStockPeopleFragment.class);
    }

    private void notifyTradeRequested(SecurityCompactDTO securityCompactDTO)
    {
        // TODO
    }

    public interface SearchRequestedListener
    {
        void onSearchRequested();
    }

    public interface OnTradeRequestedListener
    {
        void onTradeRequested(SecurityCompactDTO securityCompactDTO);
    }
}
