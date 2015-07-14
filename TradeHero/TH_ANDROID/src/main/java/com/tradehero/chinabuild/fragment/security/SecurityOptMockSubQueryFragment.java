package com.tradehero.chinabuild.fragment.security;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

/**
 * Created by palmer on 15/7/6.
 */
public class SecurityOptMockSubQueryFragment extends Fragment implements View.OnClickListener{
    private TextView mClickShowMore;
    private ListView mListView1;
    private ListView mListView2;
    private SecurityOptMockQueryTradeAdapter mListViewAdapter1;
    private SecurityOptMockQueryDelegationAdapter mListViewAdapter2;
    @Inject TradeServiceWrapper mTradeServiceWrapper;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("lyl onCreate");
        mTradeServiceWrapper.getTrades(new Callback<ClosedTradeDTOList>() {
            @Override
            public void success(ClosedTradeDTOList list, Response response2) {
                Timber.d("lyl getTrades size=" + list.size());
                mListViewAdapter1.setItems(list);
                mListViewAdapter1.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        mTradeServiceWrapper.getDelegation(new Callback<ClosedTradeDTOList>() {
            @Override
            public void success(ClosedTradeDTOList list, Response response2) {
                Timber.d("lyl getDelegation size=" + list.size());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
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
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}