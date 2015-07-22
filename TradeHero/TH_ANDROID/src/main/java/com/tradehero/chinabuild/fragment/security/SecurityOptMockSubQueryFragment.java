package com.tradehero.chinabuild.fragment.security;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.trade.ClosedTradeDTOList;
import com.tradehero.th.network.service.TradeServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class SecurityOptMockSubQueryFragment extends Fragment implements View.OnClickListener{
    private TextView mClickShowMore;
    private ListView mListView1;
    private ListView mListView2;
    private LinearLayout mBelowLayout;
    private ProgressBar mProgressBar1;
    private ProgressBar mProgressBar2;
    private LinearLayout mTitleLayout;
    private SecurityOptMockQueryTradeAdapter mListViewAdapter1;
    private SecurityOptMockQueryDelegationAdapter mListViewAdapter2;
    @Inject TradeServiceWrapper mTradeServiceWrapper;
    private boolean mIsShowMore = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_opt_sub_query, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mClickShowMore = (TextView) view.findViewById(R.id.click_show_more);
        mClickShowMore.setOnClickListener(this);
        mListView1 = (ListView) view.findViewById(R.id.list_1);
        if (mListViewAdapter1 == null) {
            mListViewAdapter1 = new SecurityOptMockQueryTradeAdapter(getActivity());
        }
        mListView1.setAdapter(mListViewAdapter1);
        mListView2 = (ListView) view.findViewById(R.id.list_2);
        if (mListViewAdapter2 == null) {
            mListViewAdapter2 = new SecurityOptMockQueryDelegationAdapter(getActivity());
        }
        mListView2.setAdapter(mListViewAdapter2);
        mBelowLayout = (LinearLayout) view.findViewById(R.id.below_layout);
        mProgressBar1 = (ProgressBar) view.findViewById(R.id.loading);
        mProgressBar2 = (ProgressBar) view.findViewById(R.id.loading2);
        mTitleLayout = (LinearLayout) view.findViewById(R.id.title_layout);
        mTitleLayout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        queryTradeHistroy();
        queryDelegationHistory();
    }

    private void queryDelegationHistory() {
        mProgressBar2.setVisibility(View.VISIBLE);
        mTradeServiceWrapper.getDelegation(new Callback<ClosedTradeDTOList>() {
            @Override
            public void success(ClosedTradeDTOList list, Response response2) {
                Timber.d("lyl getDelegation size=" + list.size());
                mListViewAdapter2.setItems(list);
                mListViewAdapter2.notifyDataSetChanged();
                mProgressBar2.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void queryTradeHistroy() {
        mProgressBar1.setVisibility(View.VISIBLE);
        mTradeServiceWrapper.getTrades(new Callback<ClosedTradeDTOList>() {
            @Override
            public void success(ClosedTradeDTOList list, Response response2) {
                Timber.d("lyl getTrades size=" + list.size());
                mListViewAdapter1.setItems(list);
                mListViewAdapter1.notifyDataSetChanged();
                mProgressBar1.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {

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
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}