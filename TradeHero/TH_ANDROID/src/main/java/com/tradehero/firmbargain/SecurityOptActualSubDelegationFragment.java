package com.tradehero.firmbargain;

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
import cn.htsec.data.pkg.trade.IPackageProxy;
import cn.htsec.data.pkg.trade.TradeDataHelper;
import cn.htsec.data.pkg.trade.TradeInterface;
import cn.htsec.data.pkg.trade.TradeManager;
import com.tradehero.chinabuild.fragment.security.SecurityOptMockActualDelegationAdapter;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.api.trade.ClosedTradeDTO;
import com.tradehero.th.api.trade.ClosedTradeDTOList;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;
import timber.log.Timber;

public class SecurityOptActualSubDelegationFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private RelativeLayout mDelegrationButton;
    private ListView mListView;
    private SecurityOptMockActualDelegationAdapter mListViewAdapter;
    private LinearLayout mNoItemLayout;
    private int mSelectedPosition = -1;
    private TradeManager mTradeManager;
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
        mTradeManager = TradeManager.getInstance(getActivity().getApplicationContext());
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
        mTradeManager.sendData(TradeInterface.ID_QUERY_ORDERS, new IPackageProxy() {
            @Override
            public void onSend(TradeDataHelper helper) {
//                helper.set(TradeInterface.KEY_START_DATE, "20150618");
//                helper.set(TradeInterface.KEY_END_DATE, "20150717");
                helper.set(TradeInterface.KEY_WITHDRAW_FLAG, "1");
//                helper.setStartPosition(0);
            }

            @Override
            public void onReceive(TradeDataHelper helper) {
                int rowCount = helper.getRowCount();
                int responseCode = helper.getResponseCode();
                String responseMsg = helper.getResponseMsg();

                int resultCode = helper.getResultCode();
                String resultMsg = helper.getResultMsg();

                int startPosition = helper.getStartPosition();

                StringBuffer sb = new StringBuffer();
                sb.append("响应Code:" + responseCode + "\n");
                sb.append("响应Msg:" + responseMsg + "\n");
                sb.append("结果Code:" + resultCode + "\n");
                sb.append("结果Msg:" + resultMsg + "\n");
                sb.append("起始位置:" + startPosition + "\n");
                sb.append("结果行数:" + rowCount + "\n");

                List<String> keys = helper.getKeys();
                ClosedTradeDTOList list = new ClosedTradeDTOList();
                for (int i = 0; i < rowCount; i++) {
                    sb.append(i + ",");
                    ClosedTradeDTO dto = new ClosedTradeDTO();
                    for (int j = 0; j < keys.size(); j++) {
                        String key = keys.get(j);
                        sb.append(key + ":" + helper.get(i, key, null));
                        if (j != keys.size() - 1) {
                            sb.append("  ");
                        }
                        if (key.equalsIgnoreCase("sec_name")) {
                            dto.securityName = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("sec_code")) {
                            dto.securityId = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("entrust_name")) {
                            dto.entrust_name = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("entrust_price")) {
                            dto.entrust_price = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("entrust_amt")) {
                            dto.entrust_amt = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("entrust_date")) {
                            dto.entrust_date = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("entrust_time")) {
                            dto.entrust_time = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("market_code")) {
                            dto.market_code = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("sec_account")) {
                            dto.sec_account = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("withdraw_cate")) {
                            dto.withdraw_cate = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("entrust_no")) {
                            dto.entrust_no = helper.get(i, key, null);
                        }
                    }
                    sb.append("\n");
                    list.add(dto);
                }
                Timber.d("lyl "+sb.toString());

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
            public void onRequestStart() {
//                showProgress();
            }

            @Override
            public void onRequestFinish() {
//                dismissProgress();
            }

            @Override
            public void onRequestFail(String msg) {
                THToast.show(msg);
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
            mTradeManager.sendData(TradeInterface.ID_CANCEL, new IPackageProxy() {
                @Override
                public void onSend(TradeDataHelper helper) {
                    Timber.d("lyl " + mListViewAdapter.getItem(mSelectedPosition).toString());
                    helper.set(TradeInterface.KEY_MARKET_CODE, mListViewAdapter.getItem(mSelectedPosition).market_code);
                    helper.set(TradeInterface.KEY_ENTRUST_DATE, mListViewAdapter.getItem(mSelectedPosition).entrust_date);
                    helper.set(TradeInterface.KEY_WITHDRAW_CATE, mListViewAdapter.getItem(mSelectedPosition).withdraw_cate);
                    helper.set(TradeInterface.KEY_ENTRUST_NO, mListViewAdapter.getItem(mSelectedPosition).entrust_no);
                    helper.set(TradeInterface.KEY_SEC_CODE, mListViewAdapter.getItem(mSelectedPosition).securityId);
                }

                @Override
                public void onReceive(TradeDataHelper helper) {
                    int rowCount = helper.getRowCount();
                    int responseCode = helper.getResponseCode();
                    String responseMsg  = helper.getResponseMsg();

                    int resultCode = helper.getResultCode();
                    String resultMsg = helper.getResultMsg();

                    StringBuffer sb = new StringBuffer();
                    sb.append("响应Code:"+responseCode+"\n");
                    sb.append("响应Msg:"+responseMsg+"\n");
                    sb.append("结果Code:"+resultCode+"\n");
                    sb.append("结果Msg:"+resultMsg+"\n");
                    sb.append("结果行数:" + rowCount + "\n");

//                    mTvResult.setText(sb.toString());
                    Timber.d("lyl "+sb.toString());
                    queryPendingDelegationHistory();
                }

                @Override
                public void onRequestStart() {
//                    showProgress();
                }

                @Override
                public void onRequestFinish() {
//                    dismissProgress();
                }

                @Override
                public void onRequestFail(String msg) {
                    THToast.show(msg);
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
