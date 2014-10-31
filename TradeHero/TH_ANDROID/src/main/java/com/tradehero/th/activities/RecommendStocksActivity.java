package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.RecommendListAdapter;
import com.tradehero.th.fragments.chinabuild.data.RecommendHero;
import com.tradehero.th.fragments.chinabuild.data.RecommendItems;
import com.tradehero.th.fragments.chinabuild.data.RecommendStock;
import com.tradehero.th.fragments.chinabuild.data.THSharePreferenceManager;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.ABCLogger;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.ArrayList;


/**
 * Created by palmer on 14-10-29.
 */
public class RecommendStocksActivity extends Activity implements View.OnClickListener{

    @InjectView(R.id.button_recommend_follow)Button followBtn;
    @InjectView(R.id.tvHeadLeft)TextView tvHeadLeft;
    @InjectView(R.id.tvHeadRight0)TextView tvHeadRight;
    @InjectView(R.id.tvHeadMiddleMain)TextView tvHeadMiddleMain;
    @InjectView(R.id.pulltorefreshlistview_recommend_stock_hero)PullToRefreshListView recommendPRLV;
    @InjectView(R.id.progressbar_recommend_loading)ProgressBar loadingPB;
    @InjectView(R.id.imageview_recommend_download_failed)ImageView downloadFailedIV;

    @Inject Lazy<UserServiceWrapper> userServiceWrapper;

    public final static String LOGIN_USER_ID = "login_user_id";

    private String jumpStr = "";
    private String titleStr = "";

    private int userId = -1;

    private ArrayList<RecommendStock> securities = new ArrayList<RecommendStock>();
    private ArrayList<RecommendHero> heroes = new ArrayList<RecommendHero>();

    private RecommendListAdapter listAdapter;

    private boolean isDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommmend_stock);

        DaggerUtils.inject(this);
        ButterKnife.inject(this);

        userId = getIntent().getIntExtra(LOGIN_USER_ID, -1);

        initViews();

        showLoadingProgressBar();
        gotoDownloadRecommendItems();
    }

    private void initViews(){
        jumpStr = getResources().getString(R.string.recommend_next);
        titleStr = getResources().getString(R.string.recommend_title);
        tvHeadLeft.setVisibility(View.GONE);
        tvHeadRight.setVisibility(View.VISIBLE);
        tvHeadRight.setText(jumpStr);
        tvHeadRight.setOnClickListener(this);
        tvHeadMiddleMain.setVisibility(View.VISIBLE);
        tvHeadMiddleMain.setText(titleStr);

        recommendPRLV.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listAdapter = new RecommendListAdapter(this,securities,heroes);
        recommendPRLV.setAdapter(listAdapter);
        recommendPRLV.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>(){

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                gotoDownloadRecommendItems();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        followBtn.setOnClickListener(this);
    }


    @Override
    public void onBackPressed(){
        gotoNextActivity();
    }


    private void gotoNextActivity(){
        if(userId >= 0){
            THSharePreferenceManager.setRecommendedStock(userId, this);
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if(viewId == R.id.tvHeadRight0){
            disableAllBtns();
            gotoNextActivity();
            return;
        }
        if(viewId == R.id.button_recommend_follow){
            uploadSecuritiesAndHeroes();
            return;
        }
    }

    private void disableAllBtns(){
        tvHeadRight.setClickable(false);
        followBtn.setClickable(false);
    }

    public void gotoDownloadRecommendItems(){
        if(isDownloading){
            return;
        }
        userServiceWrapper.get().downloadRecommendItems(new DownloadRecommendItemsCallback());
    }

    private class DownloadRecommendItemsCallback implements Callback<RecommendItems>{

        @Override
        public void success(RecommendItems recommendItems, Response response) {
            recommendPRLV.onRefreshComplete();
            securities.clear();
            heroes.clear();
            for(RecommendStock stock: recommendItems.securities){
                securities.add(stock);
                ABCLogger.d(stock.toString());
            }
            for(RecommendHero hero: recommendItems.users){
                heroes.add(hero);
                ABCLogger.d(hero.toString());
            }
            listAdapter.setRecommendItems(securities, heroes);
            listAdapter.notifyDataSetChanged();
            dismissLoadingProgressBar();
            isDownloading = false;
            downloadFailedIV.setVisibility(View.VISIBLE);
            recommendPRLV.setEmptyView(downloadFailedIV);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            recommendPRLV.onRefreshComplete();
            dismissLoadingProgressBar();
            isDownloading = false;
            downloadFailedIV.setVisibility(View.VISIBLE);
            recommendPRLV.setEmptyView(downloadFailedIV);
        }
    }

    private void showLoadingProgressBar(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                loadingPB.setVisibility(View.VISIBLE);
            }
        });
    }

    private void dismissLoadingProgressBar(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                loadingPB.setVisibility(View.GONE);
            }
        });
    }

    private void uploadSecuritiesAndHeroes(){
        if(listAdapter.getHeroesSelected().size()==0 && listAdapter.getSecuritiesSelected().size()==0){
            THToast.show(R.string.recommend_select_one_item);
            return;
        }
    }

}
