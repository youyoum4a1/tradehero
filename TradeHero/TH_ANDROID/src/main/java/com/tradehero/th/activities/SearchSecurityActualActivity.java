package com.tradehero.th.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.tradehero.common.utils.THToast;
import com.tradehero.firmbargain.ActualSecurityDTO;
import com.tradehero.th.R;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.widget.TradeHeroProgressBar;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by palmer on 15/7/18.
 */
public class SearchSecurityActualActivity extends Activity {

    private TextView tvSearch;
    private ImageView imgSearch;
    private Button btnSearchCancel;
    private EditText edtSearchInput;

    private TradeHeroProgressBar progressBar;
    private PullToRefreshListView pullToRefreshListView;

    private int index = 1;

    private String inputStr = "";

    @Inject SecurityServiceWrapper securityServiceWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_security_actual);

        initViews();
    }

    private void initViews(){
        tvSearch = (TextView)findViewById(R.id.tvSearch);
        imgSearch = (ImageView)findViewById(R.id.imgSearch);
        btnSearchCancel = (Button)findViewById(R.id.btn_search_x);
        edtSearchInput = (EditText)findViewById(R.id.edtSearchInput);
        progressBar = (TradeHeroProgressBar)findViewById(R.id.tradeheroprogressbar);
        pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.listSearch);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        pullToRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        progressBar.setVisibility(View.GONE);
    }

    private void downloadFirstPage(){
        if(edtSearchInput.getText() == null || TextUtils.isEmpty(edtSearchInput.getText().toString())){
            return;
        }
        inputStr = edtSearchInput.getText().toString();
        index = 1;
        securityServiceWrapper.searchSecuritySHESHA(inputStr, 1, 20, new Callback<ActualSecurityDTO>() {
            @Override
            public void success(ActualSecurityDTO actualSecurityDTO, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                THException exception = new THException(error);
                THToast.show(exception.getMessage());
            }
        });
    }

    private void downloadNextPage(){

    }
}
