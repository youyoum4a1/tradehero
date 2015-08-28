package com.tradehero.livetrade;

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
import com.tradehero.chinabuild.fragment.security.SecurityOptMockActualDelegationAdapter;
import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.data.LiveTradeEntrustCancelDTO;
import com.tradehero.livetrade.data.LiveTradePendingEntrustQueryDTO;
import com.tradehero.livetrade.data.subData.PendingEntrustQueryDTO;
import com.tradehero.livetrade.services.LiveTradeCallback;
import com.tradehero.livetrade.services.LiveTradeManager;
import com.tradehero.th.R;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.utils.DaggerUtils;

import javax.inject.Inject;

import timber.log.Timber;

public class SecurityOptActualSubDelegationFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private RelativeLayout mDelegrationButton;
    private ListView mListView;
    private SecurityOptMockActualDelegationAdapter mListViewAdapter;
    private LinearLayout mNoItemLayout;
    private int mSelectedPosition = -1;
    @Inject LiveTradeManager mTradeManager;
    private ProgressBar mProgressBar;
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
        View view = inflater.inflate(R.layout.security_opt_sub_actual_delegation, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mListView = (ListView) view.findViewById(R.id.listview_security_opt_positions);
        if (mListViewAdapter == null) {
            mListViewAdapter = new SecurityOptMockActualDelegationAdapter(getActivity());
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
        mSelectedPosition = -1;
        mTradeManager.getLiveTradeServices().pendingEntrustQuery(new LiveTradeCallback<LiveTradePendingEntrustQueryDTO>() {
            @Override
            public void onSuccess(LiveTradePendingEntrustQueryDTO liveTradePendingEntrustQueryDTO) {
                mListViewAdapter.setSelectedItem(-1);
                mListViewAdapter.setItems(liveTradePendingEntrustQueryDTO);
                mListViewAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
                if (liveTradePendingEntrustQueryDTO.positions.size() > 0) {
                    mNoItemLayout.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                } else {
                    mNoItemLayout.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorCode, String errorContent) {
                THToast.show(errorContent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delegration_button:
                mDelegrationButton.setEnabled(false);
                cancelOrder();
                break;
        }
    }

    private void cancelOrder() {
        if (mListViewAdapter.getCount() > 0) {
            PendingEntrustQueryDTO dto = mListViewAdapter.getItem(mSelectedPosition);
            mTradeManager.getLiveTradeServices().entrustCancel(dto.marketCode, dto.entrustDate, dto.withdrawCate, dto.entrustNo, dto.securityId, new LiveTradeCallback<LiveTradeEntrustCancelDTO>() {
                @Override
                public void onSuccess(LiveTradeEntrustCancelDTO liveTradeEntrustCancelDTO) {
                    mSelectedPosition = -1;
                    queryPendingDelegationHistory();
                }

                @Override
                public void onError(String errorCode, String errorContent) {
                    THToast.show(errorContent);
                }
            });
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

//    protected void showProgress() {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (mProgressDlg == null) {
//                    mProgressDlg = new ProgressDialog(getActivity());
//                    mProgressDlg.setMessage("数据请求中...");
//                }
//                if (!mProgressDlg.isShowing()) {
//                    mProgressDlg.show();
//                }
//            }
//        });
//    }
//
//    protected void dismissProgress() {
//        mHandler.post(new Runnable(){
//
//            @Override
//            public void run() {
//                if(mProgressDlg != null && mProgressDlg.isShowing()) {
//                    mProgressDlg.dismiss();
//                }
//            }
//
//        });
//    }
}
