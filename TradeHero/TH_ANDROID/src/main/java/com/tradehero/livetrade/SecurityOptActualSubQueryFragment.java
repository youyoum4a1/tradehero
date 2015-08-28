package com.tradehero.livetrade;

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
import com.tradehero.chinabuild.fragment.security.SecurityOptMockActualQueryDelegationAdapter;
import com.tradehero.chinabuild.fragment.security.SecurityOptMockActualQueryTradeAdapter;
import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.data.LiveTradeDealQueryDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustQueryDTO;
import com.tradehero.livetrade.services.LiveTradeCallback;
import com.tradehero.livetrade.services.LiveTradeManager;
import com.tradehero.th.R;
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
    @Inject TradeServiceWrapper mTradeServiceWrapper;
    private boolean mIsShowMore = false;
    @Inject LiveTradeManager mTradeManager;
    private ProgressDialog mProgressDlg;
    private Handler mHandler = new Handler();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DaggerUtils.inject(this);
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
        mTradeManager.getLiveTradeServices().entrustQuery(new LiveTradeCallback<LiveTradeEntrustQueryDTO>() {
            @Override
            public void onSuccess(LiveTradeEntrustQueryDTO liveTradeEntrustQueryDTO) {
                mListViewAdapter2.setItems(liveTradeEntrustQueryDTO);
                mListViewAdapter2.notifyDataSetChanged();
                mProgressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorCode, String errorContent) {
                THToast.show(errorContent);
            }
        });
    }

    private void queryTradeHistroy() {
        mProgressBar1.setVisibility(View.VISIBLE);
        mTradeManager.getLiveTradeServices().dealQuery(new LiveTradeCallback<LiveTradeDealQueryDTO>() {
            @Override
            public void onSuccess(LiveTradeDealQueryDTO liveTradeDealQueryDTO) {
                mListViewAdapter1.setItems(liveTradeDealQueryDTO);
                mListViewAdapter1.notifyDataSetChanged();
                mProgressBar1.setVisibility(View.GONE);
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