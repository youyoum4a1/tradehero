package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.SherlockActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.RecommendListAdapter;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.fragments.chinabuild.data.*;
import com.tradehero.th.fragments.social.friend.FollowFriendsForm;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ABCLogger;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by palmer on 14-10-29.
 */
public class RecommendStocksActivity extends SherlockActivity implements View.OnClickListener{

    @InjectView(R.id.button_recommend_follow)Button followBtn;
    @InjectView(R.id.tvHeadLeft)TextView tvHeadLeft;
    @InjectView(R.id.tvHeadRight0)TextView tvHeadRight;
    @InjectView(R.id.tvHeadMiddleMain)TextView tvHeadMiddleMain;
    @InjectView(R.id.pulltorefreshlistview_recommend_stock_hero)PullToRefreshListView recommendPRLV;
    @InjectView(R.id.progressbar_recommend_loading)ProgressBar loadingPB;
    @InjectView(R.id.imageview_recommend_download_failed)ImageView downloadFailedIV;

    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject ProgressDialogUtil progressDialogUtil;

    @Inject UserProfileCache userProfileCache;

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
        checkFollowingItems();
    }


    @Override
    public void onBackPressed(){
        disableAllBtns();
        THSharePreferenceManager.setRecommendedStock(userId, this);
        gotoNextActivity();
    }

    public void checkFollowingItems(){
        if(listAdapter.getSecuritiesSelected().size()==0 && listAdapter.getHeroesSelected().size()==0){
            followBtn.setEnabled(false);
        }else{
            followBtn.setEnabled(true);
        }
    }


    private void gotoNextActivity(){
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
            THSharePreferenceManager.setRecommendedStock(userId, this);
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
        followBtn.setEnabled(false);
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
            checkFollowingItems();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            recommendPRLV.onRefreshComplete();
            dismissLoadingProgressBar();
            isDownloading = false;
            downloadFailedIV.setVisibility(View.VISIBLE);
            recommendPRLV.setEmptyView(downloadFailedIV);
            checkFollowingItems();
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
        if(listAdapter.getHeroesSelected().size()>0){
            progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.recommend_uploading);
            uploadHeroes();
            return;
        }
        if(listAdapter.getSecuritiesSelected().size()>0){
            progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.recommend_uploading);
            uploadStocks();
            return;
        }
    }

    private void uploadHeroes(){
        ArrayList<Integer> heroIds = listAdapter.getHeroesSelected();
        FollowFriendsForm followFriendsForm = new FollowFriendsForm();
        followFriendsForm.userIds = new ArrayList<>();
        for (Integer heroId : heroIds)
        {
            followFriendsForm.userIds.add(heroId);
        }
        userServiceWrapper.get().followBatchFree(followFriendsForm, new Callback<UserProfileDTO>() {
            @Override
            public void success(UserProfileDTO userProfileDTO, Response response) {
                ABCLogger.d("upload recommended heroes successfully");
                if(listAdapter.getSecuritiesSelected().size()>0){
                    uploadStocks();
                }else{
                    THSharePreferenceManager.setRecommendedStock(userId, RecommendStocksActivity.this);
                    progressDialogUtil.dismiss(RecommendStocksActivity.this);
                    gotoNextActivity();
                }
                userProfileCache.put(new UserBaseKey(userProfileDTO.id), userProfileDTO);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                ABCLogger.d("upload recommended heroes failed");
                progressDialogUtil.dismiss(RecommendStocksActivity.this);
                gotoNextActivity();
            }
        });
    }

    private void uploadStocks(){
        ArrayList<Integer> stocks = listAdapter.getSecuritiesSelected();
        FollowStockForm followStockForm = new FollowStockForm();
        followStockForm.securityIds = new ArrayList<>();
        for(Integer stockId:stocks){
            followStockForm.securityIds.add(stockId);
        }
            userServiceWrapper.get().followStocks(followStockForm, new Callback<List<WatchlistPositionDTO>>() {
            @Override
            public void success(List<WatchlistPositionDTO> o, Response response) {
                ABCLogger.d("upload recommended stock successfully");
                THSharePreferenceManager.setRecommendedStock(userId, RecommendStocksActivity.this);
                progressDialogUtil.dismiss(RecommendStocksActivity.this);
                gotoNextActivity();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                ABCLogger.d("upload recommended stock failed");
                ABCLogger.d(retrofitError.toString());
                progressDialogUtil.dismiss(RecommendStocksActivity.this);
                gotoNextActivity();
            }
        });
    }

}
