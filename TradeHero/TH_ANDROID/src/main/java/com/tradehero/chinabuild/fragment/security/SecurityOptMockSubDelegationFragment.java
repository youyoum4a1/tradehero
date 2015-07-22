package com.tradehero.chinabuild.fragment.security;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
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
    @Inject
    TradeServiceWrapper mTradeServiceWrapper;

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
        queryPendingDelegationHistory();
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
                            queryPendingDelegationHistory();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            THToast.show(getString(R.string.cancel_delegation_fail));
                            queryPendingDelegationHistory();
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
