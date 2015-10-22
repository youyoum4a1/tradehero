package com.tradehero.chinabuild.fragment.stockRecommend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.StockRecommendListAdapter;
import com.tradehero.th.api.stockRecommend.StockRecommendDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class StockRecommendFragment extends DashboardFragment {
    private ProgressBar mProgress;
    private SecurityListView mListView;
    private StockRecommendListAdapter stockRecommendListAdapter;
    @Inject
    Lazy<UserTimelineServiceWrapper> timelineServiceWrapper;
    @Inject
    CurrentUserId currentUserId;
    private int mFreshCount = 10;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_recommend, container, false);
        ButterKnife.inject(this, view);

        initView(view);
        fetchStockRecommendList();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getString(R.string.stock_recommend));
    }

    private void initView(View view) {
        mProgress = (ProgressBar) view.findViewById(R.id.progress);
        mListView = (SecurityListView) view.findViewById(R.id.list);
        if (stockRecommendListAdapter == null) {
            stockRecommendListAdapter = new StockRecommendListAdapter(getActivity());
        }
        mListView.setAdapter(stockRecommendListAdapter);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // Nothing.
                fetchStockRecommendList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // Nothing.
                fetchMore();
            }
        });
    }

    private void fetchMore() {
        mFreshCount = mFreshCount + 10;
        fetchStockRecommendList();
    }

    private void fetchStockRecommendList() {//maxId=-1 for default init page
        timelineServiceWrapper.get().getTimelineStockRecommend(currentUserId.toUserBaseKey(), mFreshCount, -1, -1, new Callback<StockRecommendDTOList>() {
            @Override
            public void success(StockRecommendDTOList stockRecommendDTOList, Response response) {
                stockRecommendListAdapter.setItems(stockRecommendDTOList);
                stockRecommendListAdapter.notifyDataSetChanged();
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                finish();
            }

            private void finish() {
                mProgress.setVisibility(View.INVISIBLE);
                mListView.onRefreshComplete();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}
