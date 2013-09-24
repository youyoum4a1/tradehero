package com.tradehero.th.fragments.trending;

import android.app.Activity;
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
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TrendingAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.network.CallbackWithSpecificNotifiers;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.widget.trending.TrendingGridView;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TrendingFragment extends SherlockFragment
{
    private final static String TAG = TrendingFragment.class.getSimpleName();

    private View actionBar;
    private ImageView mBullIcon;
    private TextView mHeaderText;
    private ImageButton mSearchBtn;
    private SearchRequestedListener searchRequestedListener;
    private OnTradeRequestedListener tradeRequestedListener;

    private ProgressBar mProgressSpinner;
    private TrendingGridView mTrendingGridView;

    private SecurityService securityService;
    private List<SecurityCompactDTO> securityCompactDTOs;
    protected TrendingAdapter trendingAdapter;

    @Override public void onAttach(Activity activity)
    {
        THLog.i(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        THLog.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        THLog.i(TAG, "onActivityCreated");
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

                //THToast.show("Disabled for now");

                notifyTradeRequested(securityCompactDTO);
            }
        });

        if (mTrendingGridView != null && mTrendingGridView.getCount() == 0)
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
                notifySearchRequested();
            }
        });
    }

    @Override public void onResume()
    {
        THLog.i(TAG, "onResume");
        super.onResume();
    }

    @Override public void onPause()
    {
        THLog.i(TAG, "onPause");
        super.onPause();
    }

    @Override public void onDetach()
    {
        THLog.i(TAG, "onDetach");
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

    protected void refreshGridView()
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

    public void setSearchRequestedListener(SearchRequestedListener searchRequestedListener)
    {
        this.searchRequestedListener = searchRequestedListener;
    }

    public void setTradeRequestedListener(OnTradeRequestedListener tradeRequestedListener)
    {
        this.tradeRequestedListener = tradeRequestedListener;
    }

    private void notifySearchRequested()
    {
        if (this.searchRequestedListener != null)
        {
            this.searchRequestedListener.onSearchRequested();
        }
    }

    private void notifyTradeRequested(SecurityCompactDTO securityCompactDTO)
    {
        if (this.tradeRequestedListener != null)
        {
            this.tradeRequestedListener.onTradeRequested(securityCompactDTO);
        }
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
