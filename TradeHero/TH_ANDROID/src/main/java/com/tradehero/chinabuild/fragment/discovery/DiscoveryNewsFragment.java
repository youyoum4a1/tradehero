package com.tradehero.chinabuild.fragment.discovery;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import android.view.Menu;
import android.view.MenuInflater;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.tradehero.chinabuild.data.NewsDTO;
import com.tradehero.chinabuild.data.NewsDTOSet;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.NewsItemAdapter;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * News from third parties.
 * Created by palmer on 15/1/15.
 */
public class DiscoveryNewsFragment extends DashboardFragment {

    @Inject Lazy<NewsServiceWrapper> newsServiceWrapper;

    private int currPageNumber = 1;
    private final int numberPerPage = 20;

    private NewsItemAdapter newsItemAdapter;
    private List<NewsDTO> newsDTOList = new ArrayList<>();

    private TradeHeroProgressBar tradeHeroProgressBar;
    private PullToRefreshListView pullToRefreshListView;

    @Inject Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discovery_news, container, false);
        ButterKnife.inject(this, view);
        tradeHeroProgressBar = (TradeHeroProgressBar)view.findViewById(R.id.tradeheroprogressbar_discovery_news_loading);
        showLoadingProgressBar();
        pullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.securitylistview_discovery_news_list);
        newsItemAdapter = new NewsItemAdapter(getActivity(), newsDTOList);
        pullToRefreshListView.getRefreshableView().setAdapter(newsItemAdapter);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>(){

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                retrieveNews();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                retrieveNewsMore();
            }
        } );
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                NewsDTO newsDTO = newsDTOList.get(position-1);
                if(newsDTO!=null){
                    analytics.addEvent(new MethodEvent(AnalyticsConstants.DISCOVERY_NEWS_ITEM, String.valueOf(position)));
                    Bundle bundle = new Bundle();
                    bundle.putLong(NewsDetailFragment.KEY_BUNDLE_NEWS_ID, newsDTO.id);
                    bundle.putString(NewsDetailFragment.KEY_BUNDLE_NEWS_TITLE, newsDTO.title);
                    gotoDashboard(NewsDetailFragment.class, bundle);
                }
            }
        });

        retrieveNews();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.discovery_square_news);
    }

    private void retrieveNews(){
        currPageNumber = 1;
        newsServiceWrapper.get().retrieveNews(currPageNumber, numberPerPage, new Callback<NewsDTOSet>() {
            @Override
            public void success(NewsDTOSet newsDTOSet, Response response) {
                if(newsDTOSet == null || newsDTOSet.data == null){
                    return;
                }
                refreshNewsDTOSet(newsDTOSet.data);
                onFinish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                THException exception = new THException(retrofitError);
                THToast.show(exception.getMessage());
                onFinish();
            }

            private void onFinish(){
                if(tradeHeroProgressBar!=null && pullToRefreshListView!=null){
                    tradeHeroProgressBar.stopLoading();
                    tradeHeroProgressBar.setVisibility(View.GONE);
                    pullToRefreshListView.setVisibility(View.VISIBLE);
                    pullToRefreshListView.onRefreshComplete();
                }
                if(newsDTOList.isEmpty()){
                    pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }else {
                    pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                }
            }

        });
    }

    private void retrieveNewsMore(){
        newsServiceWrapper.get().retrieveNews(currPageNumber + 1, numberPerPage, new Callback<NewsDTOSet>() {
            @Override
            public void success(NewsDTOSet newsDTOSet, Response response) {
                if(newsDTOSet == null || newsDTOSet.data == null){
                    return;
                }
                addMoreNewsDTOSet(newsDTOSet.data);
                currPageNumber++;
                onFinish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                THException exception = new THException(retrofitError);
                THToast.show(exception.getMessage());
                onFinish();
            }

            private void onFinish(){
                if(pullToRefreshListView!=null){
                    pullToRefreshListView.onRefreshComplete();
                }
            }
        });
    }

    private void addMoreNewsDTOSet(List<NewsDTO> newsMoreDTOList){
        for(NewsDTO newsDTO: newsMoreDTOList){
            if(isNotExist(newsDTO)){
                this.newsDTOList.add(newsDTO);
            }
        }
        newsItemAdapter.addNewsDTOSet(this.newsDTOList);
        if(newsItemAdapter!=null) {
            newsItemAdapter.notifyDataSetChanged();
        }
    }

    private void refreshNewsDTOSet(List<NewsDTO> newsRefreshDTOList){
        this.newsDTOList.clear();
        this.newsDTOList.addAll(newsRefreshDTOList);
        newsItemAdapter.addNewsDTOSet(this.newsDTOList);
        if(newsItemAdapter!=null) {
            newsItemAdapter.notifyDataSetChanged();
        }
    }

    private boolean isNotExist(NewsDTO newsDTO){
        for(NewsDTO oldNewsDTO: newsDTOList){
            if(newsDTO.id == oldNewsDTO.id){
                return false;
            }
        }
        return true;
    }

    private void showLoadingProgressBar(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                tradeHeroProgressBar.setVisibility(View.VISIBLE);
                tradeHeroProgressBar.startLoading();
            }
        });
    }

}
