package com.tradehero.firmbargain;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.htsec.TradeModule;
import cn.htsec.data.pkg.trade.IPackageProxy;
import cn.htsec.data.pkg.trade.TradeDataHelper;
import cn.htsec.data.pkg.trade.TradeInterface;
import cn.htsec.data.pkg.trade.TradeManager;
import com.tradehero.chinabuild.fragment.security.SecurityOptMockActualQueryDelegationAdapter;
import com.tradehero.chinabuild.fragment.security.SecurityOptMockActualQueryTradeAdapter;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.trade.ClosedTradeDTO;
import com.tradehero.th.api.trade.ClosedTradeDTOList;
import com.tradehero.th.network.service.TradeServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class SecurityOptActualSubQueryFragment extends Fragment  implements View.OnClickListener{
    private TextView mClickShowMore;
    private ListView mListView1;
    private ListView mListView2;
    private LinearLayout mBelowLayout;
    private LinearLayout mTitleLayout;
    private ProgressBar mProgressBar1;
    private ProgressBar mProgressBar2;
    private TextView mStockBankTransferButton;
    private SecurityOptMockActualQueryTradeAdapter mListViewAdapter1;
    private SecurityOptMockActualQueryDelegationAdapter mListViewAdapter2;
    @Inject
    TradeServiceWrapper mTradeServiceWrapper;
    private boolean mIsShowMore = false;
    private TradeManager mTradeManager;
    private ProgressDialog mProgressDlg;
    private Handler mHandler = new Handler();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DaggerUtils.inject(this);
        mTradeManager = TradeManager.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_opt_actual_sub_query, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mClickShowMore = (TextView) view.findViewById(R.id.click_show_more);
        mClickShowMore.setOnClickListener(this);
        mListView1 = (ListView) view.findViewById(R.id.list_1);
        if (mListViewAdapter1 == null) {
            mListViewAdapter1 = new SecurityOptMockActualQueryTradeAdapter(getActivity());
        }
        mListView1.setAdapter(mListViewAdapter1);
        mListView2 = (ListView) view.findViewById(R.id.list_2);
        if (mListViewAdapter2 == null) {
            mListViewAdapter2 = new SecurityOptMockActualQueryDelegationAdapter(getActivity());
        }
        mListView2.setAdapter(mListViewAdapter2);
        mBelowLayout = (LinearLayout) view.findViewById(R.id.below_layout);
        mTitleLayout = (LinearLayout) view.findViewById(R.id.title_layout);
        mTitleLayout.setOnClickListener(this);
        mProgressBar1 = (ProgressBar) view.findViewById(R.id.loading);
        mProgressBar2 = (ProgressBar) view.findViewById(R.id.loading2);
        mStockBankTransferButton = (TextView) view.findViewById(R.id.stock_bank_transfer);
        mStockBankTransferButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        queryTradeHistroy();
        queryDelegationHistory();
    }

    private void queryDelegationHistory() {
        mProgressBar2.setVisibility(View.VISIBLE);
        mTradeManager.sendData(TradeInterface.ID_QUERY_ORDERS, new IPackageProxy() {
            @Override
            public void onSend(TradeDataHelper helper) {
//                helper.set(TradeInterface.KEY_START_DATE, "20150618");
//                helper.set(TradeInterface.KEY_END_DATE, "20150717");
                helper.set(TradeInterface.KEY_WITHDRAW_FLAG, "0");
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
                        } else if (key.equalsIgnoreCase("entrust_status_name")) {
                            dto.entrust_status_name = helper.get(i, key, null);
                        }
                    }
                    sb.append("\n");
                    list.add(dto);
                }
                Timber.d("lyl "+sb.toString());

                Timber.d("lyl getDelegation size=" + list.size());
                mListViewAdapter2.setItems(list);
                mListViewAdapter2.notifyDataSetChanged();
                mProgressBar2.setVisibility(View.GONE);
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

    private void queryTradeHistroy() {
        mProgressBar1.setVisibility(View.VISIBLE);
            mTradeManager.sendData(TradeInterface.ID_QUERY_BARGAINS, new IPackageProxy() {

            @Override
            public void onSend(TradeDataHelper helper) {
//                helper.set(TradeInterface.KEY_START_DATE, "20150701");
//                helper.set(TradeInterface.KEY_END_DATE, "20150718");
//                helper.setStartPosition(0);
            }

            @Override
            public void onReceive(TradeDataHelper helper) {
                int rowCount = helper.getRowCount();
                int responseCode = helper.getResponseCode();
                String responseMsg  = helper.getResponseMsg();

                int resultCode = helper.getResultCode();
                String resultMsg = helper.getResultMsg();

                int startPosition = helper.getStartPosition();

                StringBuffer sb = new StringBuffer();
                sb.append("响应Code:"+responseCode+"\n");
                sb.append("响应Msg:"+responseMsg+"\n");
                sb.append("结果Code:"+resultCode+"\n");
                sb.append("结果Msg:"+resultMsg+"\n");
                sb.append("起始位置:"+startPosition+"\n");
                sb.append("结果行数:"+rowCount+"\n");

                List<String> keys = helper.getKeys();
                ClosedTradeDTOList list = new ClosedTradeDTOList();
                for(int i = 0; i < rowCount; i++)
                {
                    sb.append(i+",");
                    ClosedTradeDTO dto = new ClosedTradeDTO();
                    for(int j = 0; j < keys.size(); j++)
                    {
                        String key = keys.get(j);
                        sb.append(key+":"+helper.get(i, key, null));
                        if(j != keys.size() - 1)
                        {
                            sb.append("  ");
                        }
                        if (key.equalsIgnoreCase("sec_name")) {
                            dto.securityName = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("sec_code")) {
                            dto.securityId = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("entrust_name")) {
                            dto.entrust_name = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("business_price")) {
                            dto.business_price = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("business_amt")) {
                            dto.business_amt = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("business_date")) {
                            dto.business_date = helper.get(i, key, null);
                        } else if (key.equalsIgnoreCase("business_time")) {
                            dto.business_time = helper.get(i, key, null);
                        }
                    }
                    sb.append("\n");
                    list.add(dto);
                }
                Timber.d("lyl "+sb.toString());
                Timber.d("lyl ID_QUERY_BARGAINS size=" + list.size());
                mListViewAdapter1.setItems(list);
                mListViewAdapter1.notifyDataSetChanged();
                mProgressBar1.setVisibility(View.GONE);
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
            case R.id.click_show_more:
            case R.id.title_layout:
                mIsShowMore = !mIsShowMore;
                mListViewAdapter1.setShowMore(mIsShowMore);
                mListViewAdapter1.notifyDataSetChanged();
                mBelowLayout.setVisibility(mIsShowMore ? View.GONE : View.VISIBLE);
                break;
            case R.id.stock_bank_transfer:
                Intent intent = new Intent(getActivity(), TradeModule.class);
                Bundle bundle = new Bundle();
                //银证转账界面
                bundle.putString(TradeModule.EXTRA_KEY_PAGETYPE, TradeModule.PAGETYPE_TRANSFER);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
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