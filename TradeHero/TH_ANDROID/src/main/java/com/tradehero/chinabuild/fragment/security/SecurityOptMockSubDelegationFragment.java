package com.tradehero.chinabuild.fragment.security;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

public class SecurityOptMockSubDelegationFragment extends Fragment implements View.OnClickListener{
    private RelativeLayout mDelegrationButton;
    private ListView mListView;
    private SecurityOptMockDelegationAdapter mListViewAdapter;
    @Inject
    TradeServiceWrapper mTradeServiceWrapper;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTradeServiceWrapper.getPendingDelegation(new Callback<ClosedTradeDTOList>() {
            @Override
            public void success(ClosedTradeDTOList list, Response response2) {
                Timber.d("lyl getPendingDelegation size=" + list.size());
                mListViewAdapter.setItems(list);
                mListViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_opt_sub_delegation, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mDelegrationButton = (RelativeLayout) view.findViewById(R.id.delegration_button);
        mDelegrationButton.setOnClickListener(this);
        mListView = (ListView) view.findViewById(R.id.listview_security_opt_positions);
        if (mListViewAdapter == null) {
            mListViewAdapter = new SecurityOptMockDelegationAdapter(getActivity());
        }
        mListView.setAdapter(mListViewAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delegration_button:
                THToast.show("click delegration_button");
                break;
        }
    }

    @Override
    public void onDestroyView() {
        mDelegrationButton = null;
        mListViewAdapter = null;
        mListView = null;
        super.onDestroyView();
    }
}
