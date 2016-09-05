package com.androidth.general.fragments.competition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.androidth.general.R;
import com.androidth.general.activities.SignUpLiveActivity;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderDTOList;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.api.competition.key.ProviderListKey;
import com.androidth.general.fragments.web.BaseWebViewIntentFragment;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.intent.THIntent;
import com.androidth.general.models.intent.THIntentPassedListener;
import com.androidth.general.models.intent.competition.ProviderPageIntent;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.persistence.competition.ProviderListCacheRx;
import com.androidth.general.utils.ImageUtils;
import com.androidth.general.utils.broadcast.BroadcastUtils;
import com.androidth.general.utils.route.THRouter;
import com.squareup.picasso.Picasso;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;

import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Routable({
        "providers-enroll/:enrollProviderId",
        "providers-enroll/:enrollProviderId/pages/:encodedUrl",
})
public class CompetitionWebViewFragment extends BaseWebViewIntentFragment
{
    @RouteProperty("enrollProviderId") protected Integer enrollProviderId;
    @RouteProperty("encodedUrl") protected String encodedUrl;
    @Inject THRouter thRouter;
    @Inject ProviderUtil providerUtil;
    @Inject BroadcastUtils broadcastUtils;
    @Inject ProviderCacheRx providerCache;
    @Inject ProviderListCacheRx providerListCache;
    protected ProviderId providerId;
    protected Integer providerIdInteger;
    private static final String BUNDLE_KEY_PROVIDER_ID = CompetitionWebViewFragment.class.getName() + ".providerId";

    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        thRouter.inject(this);



    }

    @Override public void onResume(){

        super.onResume();
        CompetitionWebViewFragment.putIsOptionMenuVisible(getArguments(), true);
        String url = CompetitionWebViewFragment.getUrl(getArguments());
        //webView.stopLoading();
        //webView.loadUrl(url);
        //webView.onResume();
        //loadUrl(url);
    }


    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        super.webView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                Intent kycIntent = new Intent(getActivity(), SignUpLiveActivity.class);
                kycIntent.putExtra(SignUpLiveActivity.KYC_CORRESPONDENT_PROVIDER_ID, providerIdInteger);
                    kycIntent.putExtra(SignUpLiveActivity.KYC_CORRESPONDENT_JOIN_COMPETITION, true);
                startActivity(kycIntent);
                //webView.onPause();
            }

            return true;
        });

    }
    @Override public void onStart(){
        super.onStart();
        setActionBarTitle("Competition");
        displayActionBarTitle();


    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        thIntentPassedListener = createCompetitionTHIntentPassedListener();
        setThIntentPassedListener(thIntentPassedListener);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //inflater.inflate(R.menu.competition_webview_menu, menu);
        //displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }
    protected static int getProviderId(@NonNull Bundle args)
    {

        return args.getInt(SignUpLiveActivity.KYC_CORRESPONDENT_PROVIDER_ID, 0);
    }

    private void displayActionBarTitle() {
        ProviderDTOList providerList = providerListCache.getCachedValue(new ProviderListKey());
        if (providerList != null) {
            ProviderDTO providerDTO = null;
            for(int i =0 ; i < providerList.size(); i++){
                Integer id = providerList.get(i).id;
                if(providerIdInteger==id){
                    providerDTO = providerList.get(i);
                    break;
                }
            }
            if(providerDTO!=null){
                setActionBarTitle("");
                setActionBarColor(providerDTO.hexColor);
                setActionBarImage(providerDTO.navigationLogoUrl);
                /*Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                String color = providerDTO.hexColor;
                if(providerDTO.hexColor.startsWith("#")){
                    color = providerDTO.hexColor.substring(1);
                }
                color = "0D"+color;
                color = color.startsWith("#") ? color : "#".concat(color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && color.length()>0)
                    window.setStatusBarColor(Color.parseColor(color));*/
            }
        }
    }

    public boolean setActionBarImage(String url){

        return ImageUtils.setActionBarImage(getSupportActionBar(), getActivity(), url);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.webview_done:
                navigator.get().popFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override @NonNull protected String getLoadingUrl()
    {
        String loadingUrl = super.getLoadingUrl();
        if (loadingUrl == null)
        {
            return providerUtil.getLandingPage(providerId);
        } else {
            Uri uri = Uri.parse(loadingUrl);
            if(uri.getQueryParameter("providerId")!=null){
                providerIdInteger = Integer.parseInt(uri.getQueryParameter("providerId"));
            }else{
                providerIdInteger = 0;
            }
        }

        return loadingUrl;
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        broadcastUtils.nextPlease();
    }

    //<editor-fold desc="Intent Listener">
    protected THIntentPassedListener createCompetitionTHIntentPassedListener()
    {
        return new CompetitionTHIntentPassedListener();
    }

    protected class CompetitionTHIntentPassedListener implements THIntentPassedListener
    {
        @Override public void onIntentPassed(THIntent thIntent)
        {
            if (thIntent instanceof ProviderPageIntent)
            {
                Timber.d("Intent is ProviderPageIntent");
                Timber.d("Passing on %s", ((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                loadUrl(((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
            }
            else
            {
                Timber.w("Unhandled intent %s", thIntent);
            }
        }
    }
    //</editor-fold>
}
