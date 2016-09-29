package com.androidth.general.fragments.contestcenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.androidth.general.R;
import com.androidth.general.activities.SignUpLiveActivity;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderDTOList;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.api.competition.key.ProviderListKey;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.network.NetworkConstants;
import com.androidth.general.persistence.competition.ProviderListCacheRx;
import com.androidth.general.utils.Constants;
import com.androidth.general.utils.broadcast.GAnalyticsProvider;
import com.androidth.general.widget.OffOnViewSwitcherEvent;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContestCenterFragment extends DashboardFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;// Why?


    @Inject ProviderListCacheRx providerListCache;
    @Inject ProviderUtil providerUtil;
    @Bind(R.id.competition_list) RecyclerView competitionList;
    @Bind(R.id.hack_webview) WebView hackWebview;
    List<MultipleCompetitionData> multipleCompetitionDatas = new ArrayList<>();
    SingleCompetitionWebviewData singleCompetitionWebviewData;
    @Inject MainCompetitionFragment mainCompetitionFragment;
    private boolean hasEntered;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(R.string.dashboard_contest_center);
        setActionBarColor(getString(R.string.nav_bar_color_default));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_contest_center, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        competitionList.setLayoutManager(layoutManager);
        return view;
    }

    @Override public void onResume() {
        super.onResume();
        fetchProviderIdList();
    }

    @Override public void onStart(){
        super.onStart();
    }

    @Override public void onLiveTradingChanged(OffOnViewSwitcherEvent event) {
        super.onLiveTradingChanged(event);
    }

    @Override public void onDestroyView()
    {
        competitionList.invalidate();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private class SingleCompetitionWebviewData {
        String webViewUrl;
        SingleCompetitionWebviewData(String webViewUrl){
            this.webViewUrl = webViewUrl;
        }
    }

    private void fetchProviderIdList()
    {
        ProviderDTOList providerList = providerListCache.getCachedValue(new ProviderListKey());
        if(multipleCompetitionDatas!=null){
            multipleCompetitionDatas.clear();
        }
        if(providerList != null && providerList.size()==1){
            ProviderDTO providerDTO = providerList.get(0);
            if(providerDTO.isUserEnrolled){

                //problem with root fragment
//                Bundle args = new Bundle();
//                mainCompetitionFragment.putProviderId(args, providerDTO.getProviderId());
//                OwnedPortfolioId applicablePortfolioId = providerDTO.getAssociatedOwnedPortfolioId();
//                if (applicablePortfolioId != null) {
//                    mainCompetitionFragment.putApplicablePortfolioId(args, applicablePortfolioId);
//                }
//                mainCompetitionFragment.setArguments(args);
//                final FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(container.getId(), mainCompetitionFragment);
//                ft.commit();

                multipleCompetitionDatas.add(new MultipleCompetitionData(providerDTO.multiImageUrl, providerDTO.isUserEnrolled, providerDTO.id, providerDTO.getProviderId()));

                competitionList.setVisibility(View.VISIBLE);
                hackWebview.setVisibility(View.INVISIBLE);
                competitionList.setAdapter(new MultipleCompetitionsAdapter(multipleCompetitionDatas, getContext()));
                try{
//                    competitionList.findViewHolderForAdapterPosition(0).itemView.performClick();
                    if(!hasEntered){
                        handleCompetitionItemClicked(multipleCompetitionDatas.get(0));
                        hasEntered = true;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                String url = providerUtil.getLandingPage(providerDTO.getProviderId());
                singleCompetitionWebviewData = new SingleCompetitionWebviewData(url);
                hackWebview.setVisibility(View.VISIBLE);
                competitionList.setVisibility(View.INVISIBLE);
                WebView webView = setWebView(hackWebview);
                webView.loadUrl(singleCompetitionWebviewData.webViewUrl);
                webView.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        //Google Analytics Event
                        GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_POPUP_SCREEN);

                        Intent kycIntent = new Intent(getActivity(), SignUpLiveActivity.class);
                        kycIntent.putExtra(SignUpLiveActivity.KYC_CORRESPONDENT_PROVIDER_ID, providerDTO.id);
                        kycIntent.putExtra(SignUpLiveActivity.KYC_CORRESPONDENT_JOIN_COMPETITION, true);
                        startActivity(kycIntent);
                    }

                    return true;
                });

                GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.COMP_JOIN_PAGE);
            }
        }
        else if(providerList != null && providerList.size() > 1) {
            for (int i = 0; i < providerList.size(); i++) {
                ProviderDTO providerDTO = providerList.get(i);
                multipleCompetitionDatas.add(new MultipleCompetitionData(providerDTO.multiImageUrl, providerDTO.isUserEnrolled, providerDTO.id, providerDTO.getProviderId()));
            }
            competitionList.setVisibility(View.VISIBLE);
            hackWebview.setVisibility(View.INVISIBLE);
            competitionList.setAdapter(new MultipleCompetitionsAdapter(multipleCompetitionDatas, getContext()));
        }
        else  {
            //if providerlist is null
            hackWebview.setVisibility(View.VISIBLE);
            competitionList.setVisibility(View.INVISIBLE);
            WebView webView = setWebView(hackWebview);
            webView.loadUrl(NetworkConstants.NO_COMPETITION);

        }
    }
    private class MultipleCompetitionData{
        String imageUrl;
        boolean isEnrolled;
        int intProviderId;
        ProviderId providerId;
        MultipleCompetitionData(String imageUrl, boolean isEnrolled, int intProviderId, ProviderId providerId){
            this.imageUrl = imageUrl;
            this.isEnrolled = isEnrolled;
            this.providerId = providerId;
            this.intProviderId = intProviderId;
        }
    }
    class MultipleCompetitionsAdapter extends RecyclerView.Adapter<MultipleCompetitionViewHolder>{
        List<MultipleCompetitionData> multipleCompetitionDataList;
        Context context;
        MultipleCompetitionsAdapter(List<MultipleCompetitionData> multipleCompetitionDataList, Context context){
            this.multipleCompetitionDataList = multipleCompetitionDataList;
            this.context = context;
        }
        @Override
        public MultipleCompetitionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.competition_list_item, null);
            view.setOnClickListener(click ->{
                handleCompetitionItemClicked(multipleCompetitionDataList.get(competitionList.indexOfChild(view)));
            });
            MultipleCompetitionViewHolder multipleCompetitionViewHolder = new MultipleCompetitionViewHolder(view);
            return multipleCompetitionViewHolder;
        }

        @Override
        public void onBindViewHolder(MultipleCompetitionViewHolder holder, int position) {
            MultipleCompetitionData multipleCompetitionData = multipleCompetitionDataList.get(position);
            Picasso.with(context).load(multipleCompetitionData.imageUrl).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                    if(multipleCompetitionData.isEnrolled){
                        holder.enrolledImage.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.enrolledImage.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError() {

                }
            });
        }

        @Override
        public int getItemCount() {
            return multipleCompetitionDataList.size();
        }
    }
    class MultipleCompetitionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView enrolledImage;
        public MultipleCompetitionViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView)itemView.findViewById(R.id.competition_banner);
            this.enrolledImage = (ImageView)itemView.findViewById(R.id.enrolled_tick);
        }
    }
    private void handleCompetitionItemClicked(MultipleCompetitionData data)
    {
        //Google Analytics Event
        GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_TAB_COMP);

        if (navigator == null)
        {
            return;
        }
        if (data != null && data.isEnrolled)
        {
            multipleCompetitionDatas.clear();
            competitionList.getAdapter().notifyDataSetChanged();
            Bundle args = new Bundle();
            MainCompetitionFragment.putProviderId(args, data.providerId);
            navigator.get().pushFragment(MainCompetitionFragment.class, args);
        }
        else if(data!=null && !data.isEnrolled) {
            Intent kycIntent = new Intent(getActivity(), SignUpLiveActivity.class);
            kycIntent.putExtra(SignUpLiveActivity.KYC_CORRESPONDENT_PROVIDER_ID, data.intProviderId);
            kycIntent.putExtra(SignUpLiveActivity.KYC_CORRESPONDENT_JOIN_COMPETITION, true);
            startActivity(kycIntent);
        }

    }
    private WebView setWebView(WebView webView)
    {
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setVerticalScrollBarEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !Constants.RELEASE) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.setOnKeyListener((v1, keyCode, event) -> {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack())
            {
                webView.goBack();
                return true;
            }
            return false;
        });
        WebChromeClient webChromeClient = new WebChromeClient();
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new WebViewClient(){
            ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Loading","Please wait ...",true);
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                progressDialog.show();
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
            }

        });
        return webView;
    }
}
