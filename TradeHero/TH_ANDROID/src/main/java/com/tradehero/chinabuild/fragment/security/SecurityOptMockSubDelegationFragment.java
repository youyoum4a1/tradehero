package com.tradehero.chinabuild.fragment.security;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.tradehero.chinabuild.fragment.competition.CompetitionSecuritySearchFragment;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.trade.ClosedTradeDTOList;
import com.tradehero.th.network.service.TradeServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class SecurityOptMockSubDelegationFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private RelativeLayout mDelegrationButton;
    private ListView mListView;
    private SecurityOptMockDelegationAdapter mListViewAdapter;
    private LinearLayout mNoItemLayout;
    private ProgressBar mProgressBar;
    private int mSelectedPosition = -1;
    private int mPortfolioId = 0;
    private int competitionId = 0;
    private PortfolioId portfolioIdObj;
    @Inject TradeServiceWrapper mTradeServiceWrapper;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(SecurityOptActivity.INTENT_END_TRADING)) {
                queryPendingDelegationHistory();
            }
        }
    };
    private IntentFilter intentFilter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_opt_sub_delegation, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mListView = (ListView) view.findViewById(R.id.listview_security_opt_positions);
        if (mListViewAdapter == null) {
            mListViewAdapter = new SecurityOptMockDelegationAdapter(getActivity());
        }
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(this);
        mDelegrationButton = (RelativeLayout) view.findViewById(R.id.delegration_button);
        mDelegrationButton.setEnabled(false);
        mDelegrationButton.setOnClickListener(this);
        mNoItemLayout = (LinearLayout) view.findViewById(R.id.no_item);
        mProgressBar = (ProgressBar) view.findViewById(R.id.loading);
    }

    @Override
    public void onResume() {
        super.onResume();
        competitionId = getArguments().getInt(CompetitionSecuritySearchFragment.BUNDLE_COMPETITION_ID, 0);
        if (getArguments().containsKey(SecurityOptActivity.KEY_PORTFOLIO_ID)) {
            portfolioIdObj = getPortfolioId();
            if (competitionId != 0) {
                mPortfolioId = portfolioIdObj.key;
            }
        }

        if (competitionId == 0) {
            queryPendingDelegationHistory();
        } else {
            queryPendingDelegationHistoryWP();
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction(SecurityOptActivity.INTENT_END_TRADING);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    private void queryPendingDelegationHistory() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTradeServiceWrapper.getPendingDelegation(new Callback<ClosedTradeDTOList>() {
            @Override
            public void success(ClosedTradeDTOList list, Response response2) {
                Timber.d("lyl getPendingDelegation size=" + list.size());
                mListViewAdapter.setSelectedItem(-1);
                mListViewAdapter.setItems(list);
                mListViewAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
                if (list.size() > 0) {
                    mNoItemLayout.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                } else {
                    mNoItemLayout.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void queryPendingDelegationHistoryWP() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTradeServiceWrapper.getPendingDelegationWithPortfolio(mPortfolioId, new Callback<ClosedTradeDTOList>() {
            @Override
            public void success(ClosedTradeDTOList list, Response response2) {
                Timber.d("lyl getPendingDelegationWP size=" + list.size());
                mListViewAdapter.setSelectedItem(-1);
                mListViewAdapter.setItems(list);
                mListViewAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
                if (list.size() > 0) {
                    mNoItemLayout.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                } else {
                    mNoItemLayout.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    protected PortfolioId getPortfolioId() {
        if (this.portfolioIdObj == null) {
            this.portfolioIdObj = new PortfolioId(getArguments().getBundle(SecurityOptActivity.KEY_PORTFOLIO_ID));
        }
        return portfolioIdObj;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delegration_button:
                mDelegrationButton.setEnabled(false);
                if (mListViewAdapter.getCount() > 0) {
                    mTradeServiceWrapper.deletePendingDelegation(mListViewAdapter.getItem(mSelectedPosition).id, new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            THToast.show(getString(R.string.cancel_delegation_success));
                            mSelectedPosition = -1;
                            if (competitionId == 0) {
                                queryPendingDelegationHistory();
                            } else {
                                queryPendingDelegationHistoryWP();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            THToast.show(getString(R.string.cancel_delegation_fail));
                            mSelectedPosition = -1;
                            if (competitionId == 0) {
                                queryPendingDelegationHistory();
                            } else {
                                queryPendingDelegationHistoryWP();
                            }
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Timber.d("lyl onItemClick "+position);
        if (mSelectedPosition == position) {
            mSelectedPosition = -1;
            mDelegrationButton.setEnabled(false);
        } else {
            mSelectedPosition = position;
            mDelegrationButton.setEnabled(true);
        }
        mListViewAdapter.setSelectedItem(position);
        mListViewAdapter.notifyDataSetChanged();
    }
}
