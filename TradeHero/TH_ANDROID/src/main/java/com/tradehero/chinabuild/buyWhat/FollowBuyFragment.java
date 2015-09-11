package com.tradehero.chinabuild.buyWhat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.chinabuild.fragment.security.BasePurchaseManagerFragment;
import com.tradehero.chinabuild.mainTab.MainTabBuyWhatAdapter;
import com.tradehero.th.R;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.utils.DaggerUtils;
import timber.log.Timber;

public class FollowBuyFragment extends BasePurchaseManagerFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView mListView;
    private MainTabBuyWhatAdapter mListViewAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.follow_buy_fragment_layout, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mListView = (ListView) view.findViewById(R.id.list);
        if (mListViewAdapter == null) {
            mListViewAdapter = new MainTabBuyWhatAdapter(getActivity());
        }
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewRight0(R.drawable.search);
        setHeadViewMiddleMain(getString(R.string.tab_main_buy_what));
    }

    @Override
    public void onResume() {
        super.onResume();
        queryPendingDelegationHistory();
    }

    @Override
    public void onClickHeadRight0() {
        pushFragment(SearchUnitFragment.class, new Bundle());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void queryPendingDelegationHistory() {
//        mProgressBar.setVisibility(View.VISIBLE);
//        mSelectedPosition = -1;
//        mTradeManager.getLiveTradeServices().pendingEntrustQuery(new LiveTradeCallback<LiveTradePendingEntrustQueryDTO>() {
//            @Override
//            public void onSuccess(LiveTradePendingEntrustQueryDTO liveTradePendingEntrustQueryDTO) {
//                mListViewAdapter.setSelectedItem(-1);
//                mListViewAdapter.setItems(liveTradePendingEntrustQueryDTO);
//                mListViewAdapter.notifyDataSetChanged();
//                mProgressBar.setVisibility(View.GONE);
//                if (liveTradePendingEntrustQueryDTO.positions.size() > 0) {
//                    mNoItemLayout.setVisibility(View.GONE);
//                    mListView.setVisibility(View.VISIBLE);
//                } else {
//                    mNoItemLayout.setVisibility(View.VISIBLE);
//                    mListView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onError(String errorCode, String errorContent) {
//                THToast.show(errorContent);
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Timber.d("lyl onItemClick "+position);
        goToMockTrade();
    }

    private void goToMockTrade(){
        Bundle bundle = new Bundle();
        bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_BUY);
        Intent intent = new Intent(getActivity(), SecurityOptActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }
}
