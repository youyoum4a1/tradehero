package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.tradehero.chinabuild.fragment.competition.CompetitionSecuritySearchFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.ActualSecurityDTO;
import com.tradehero.livetrade.ActualSecurityListDTO;
import com.tradehero.livetrade.SearchSecurityListAdapter;
import com.tradehero.th.R;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.TradeHeroProgressBar;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Actual Search Stock Page
 *
 * Created by palmer on 15/7/18.
 */
public class SearchSecurityActualActivity extends Activity {

    private TextView tvSearch;
    private Button btnSearchCancel;
    private EditText edtSearchInput;

    private TradeHeroProgressBar progressBar;
    private PullToRefreshListView pullToRefreshListView;

    private int index = 1;

    private String inputStr = "";

    @Inject SecurityServiceWrapper securityServiceWrapper;
    private SearchSecurityListAdapter searchSecurityListAdapter;

    private String searchStr;
    private String searchCancelStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerUtils.inject(this);

        setContentView(R.layout.activity_search_security_actual);
        searchStr = getString(R.string.search_search);
        searchCancelStr = getString(R.string.search_cancel);
        initViews();
    }

    private void initViews(){
        tvSearch = (TextView)findViewById(R.id.tvSearch);
        btnSearchCancel = (Button)findViewById(R.id.btn_search_x);
        edtSearchInput = (EditText)findViewById(R.id.edtSearchInput);
        progressBar = (TradeHeroProgressBar)findViewById(R.id.tradeheroprogressbar);
        btnSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtSearchInput!=null){
                    edtSearchInput.setText("");
                }
            }
        });
        tvSearch.setText("搜索");
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edtSearchInput.getText())){
                    finish();
                } else {
                    downloadFirstPage();
                }
            }
        });
        pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.listSearch);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        searchSecurityListAdapter = new SearchSecurityListAdapter(this);
        pullToRefreshListView.setAdapter(searchSecurityListAdapter);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                downloadNextPage();
            }
        });
        pullToRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(searchSecurityListAdapter!=null) {
                    finish();
                    int index = (position-1);
                    ActualSecurityDTO actualSecurityDTO = searchSecurityListAdapter.getItem(index);
                    enterSecurityOptActualPage(actualSecurityDTO.name, actualSecurityDTO.exchange, actualSecurityDTO.symbol);
                }
            }
        });
        progressBar.setVisibility(View.GONE);
        tvSearch.setText(searchCancelStr);
        edtSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputStr = editable.toString();
                if(TextUtils.isEmpty(inputStr)){
                    tvSearch.setText(searchCancelStr);
                } else {
                    tvSearch.setText(searchStr);
                    downloadFirstPage();
                }
            }
        });
    }

    private void downloadFirstPage(){
        if(edtSearchInput.getText() == null || TextUtils.isEmpty(edtSearchInput.getText().toString())){
            return;
        }
        if(progressBar!=null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startLoading();
        }
        inputStr = edtSearchInput.getText().toString();
        index = 1;
        securityServiceWrapper.searchSecuritySHESHA(inputStr, index, 20, new Callback<ActualSecurityListDTO>() {
            @Override
            public void success(ActualSecurityListDTO actualSecurityListDTO, Response response) {
                searchSecurityListAdapter.setData(actualSecurityListDTO);
                if(actualSecurityListDTO == null || actualSecurityListDTO.size() < 20){
                    pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                } else {
                    pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                }
                onFinish();
            }

            @Override
            public void failure(RetrofitError error) {
                THException exception = new THException(error);
                THToast.show(exception.getMessage());
                pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                onFinish();
            }

            private void onFinish(){
                if(progressBar!=null) {
                    progressBar.stopLoading();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void downloadNextPage(){
        if(TextUtils.isEmpty(inputStr)){
            return;
        }
        index++;
        securityServiceWrapper.searchSecuritySHESHA(inputStr, index, 20, new Callback<ActualSecurityListDTO>() {
            @Override
            public void success(ActualSecurityListDTO actualSecurityListDTO, Response response) {
                searchSecurityListAdapter.addData(actualSecurityListDTO);
                onFinish();
            }

            @Override
            public void failure(RetrofitError error) {
                THException exception = new THException(error);
                THToast.show(exception.getMessage());
                index--;
                onFinish();
            }

            private void onFinish(){
                pullToRefreshListView.onRefreshComplete();
            }
        });
    }

    private void enterSecurityOptActualPage(String securityName, String securityExchange, String securitySymbol){
        Bundle bundle = new Bundle();
        bundle.putBoolean(SecurityOptActivity.KEY_IS_FOR_ACTUAL, true);
        bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_BUY);
        bundle.putString(SecurityOptActivity.KEY_SECURITY_EXCHANGE, securityExchange);
        bundle.putString(SecurityOptActivity.KEY_SECURITY_SYMBOL, securitySymbol);
        bundle.putInt(CompetitionSecuritySearchFragment.BUNDLE_COMPETITION_ID, 0);
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
        Intent intent = new Intent(this, SecurityOptActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }
}
