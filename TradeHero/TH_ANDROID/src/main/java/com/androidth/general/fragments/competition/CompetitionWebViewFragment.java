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
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.fragments.web.BaseWebViewIntentFragment;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.intent.THIntent;
import com.androidth.general.models.intent.THIntentPassedListener;
import com.androidth.general.models.intent.competition.ProviderPageIntent;
import com.androidth.general.persistence.competition.ProviderCacheRx;
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

    protected ProviderId providerId;
    protected Integer providerIdInteger;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        thRouter.inject(this);

        if (enrollProviderId != null)
        {
            providerId = new ProviderId(enrollProviderId);
        }

        if (encodedUrl != null)
        {
            CompetitionWebViewFragment.putUrl(getArguments(), Uri.decode(encodedUrl));
        }
        else if (providerId != null)
        {
            CompetitionWebViewFragment.putUrl(getArguments(), providerUtil.getLandingPage(
                    providerId));
        }
        CompetitionWebViewFragment.putIsOptionMenuVisible(getArguments(), true);


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
            }

            return false;
        });
    }
    @Override public void onStart(){
        super.onStart();


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
    private void displayActionBarTitle()
    {   ProviderDTO providerDTO = providerCache.getCachedValue(new ProviderId(getProviderId(getArguments())));
        if(providerDTO==null) {
            providerCache.get(new ProviderId())
                    .map(new PairGetSecond<ProviderId, ProviderDTO>())
                    .startWith(Observable.just(providerDTO))
                    .onErrorReturn(throwable -> {
                        if (providerDTO == null) {
                            THToast.show(R.string.error_fetch_provider_info);
                        }
                        setActionBarTitle("");
                        setActionBarColor(providerDTO.hexColor);
                        setActionBarImage(providerDTO.navigationLogoUrl);
                        return providerDTO;
                    });
        }
        if (providerDTO == null || providerDTO.name == null)
        {
            setActionBarTitle("");
        }
        else
        {
            setActionBarTitle("");
            setActionBarColor(providerDTO.hexColor);
            setActionBarImage(providerDTO.navigationLogoUrl);
        }
    }
    public boolean setActionBarImage(String url){
        try {
            ActionBar actionBar = getSupportActionBar();
            ImageView imageView = new ImageView(getContext());
            Observable<Bitmap> observable = Observable.defer(()->{
                try {
                    return Observable.just(Picasso.with(getContext()).load(url).get());
                } catch (IOException e) {
                    e.printStackTrace();
                    return Observable.error(e);
                }
            });

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(bitmap -> {
                int height = (int)(actionBar.getHeight()*0.6);
                int bitmapHt = bitmap.getHeight();
                int bitmapWd = bitmap.getWidth();
                int width = height * (bitmapWd / bitmapHt);
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                imageView.setImageBitmap(bitmap);
                ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                actionBar.setCustomView(imageView, layoutParams);
                actionBar.setElevation(5);
                actionBar.setDisplayOptions(actionBar.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
            }, throwable -> {
                Log.e("Error",""+throwable.getMessage());
            });

            return true;
        }
        catch (Exception e){
            return false;
        }
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
