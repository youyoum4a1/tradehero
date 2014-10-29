package com.tradehero.th.fragments.discovery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListRegionalKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.network.service.UserServiceWrapper;
import java.util.Locale;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegionalNewsHeadlineFragment extends NewsHeadlineFragment
{
    public static final String REGION_CHANGED = RegionalNewsHeadlineFragment.class + ".regionChanged";

    @Inject Locale locale;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject CurrentUserId currentUserId;

    private BroadcastReceiver regionChangeBroadcastReceiver;

    @Override protected Observable<NewsItemListKey> createNewsItemListKeyObservable()
    {
        return super.createNewsItemListKeyObservable()
                .mergeWith(createNewsItemListRegionalKeyObservable());
    }

    private Observable<NewsItemListKey> createNewsItemListRegionalKeyObservable()
    {
        // observable of the UI event which user change the region
        Observable<NewsItemListKey> regionalKeyManuallyChangedObservable = Observable.create(new Observable.OnSubscribe<NewsItemListKey>()
        {
            @Override public void call(Subscriber<? super NewsItemListKey> subscriber)
            {
                if (regionChangeBroadcastReceiver == null)
                {
                    regionChangeBroadcastReceiver = new RegionalKeyBroadcastReceiver(subscriber);
                    LocalBroadcastManager.getInstance(getActivity())
                            .registerReceiver(regionChangeBroadcastReceiver, new IntentFilter(REGION_CHANGED));
                }
            }
        });

        // observable of whenever userProfileDTO is available
        Observable<NewsItemListRegionalKey> regionalKeyByUserProfileLanguageObservable = userServiceWrapper.getUserRx(currentUserId.toUserBaseKey())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(userProfileDTO -> new NewsItemListRegionalKey(userProfileDTO.countryCode, locale.getLanguage(), null, null));

        return Observable.concat(regionalKeyByUserProfileLanguageObservable, regionalKeyManuallyChangedObservable)
                .doOnNext(this::activateNewsListView); // whenever the key is changed by above 2 factors, need to reload the whole list
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        newsItemListKey = new NewsItemListRegionalKey(locale.getCountry(), locale.getLanguage(), null, null);
    }

    @Override public void onDestroy()
    {
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(regionChangeBroadcastReceiver);
        super.onDestroy();
    }

    private class RegionalKeyBroadcastReceiver extends BroadcastReceiver
    {
        private final Subscriber<? super NewsItemListKey> subscriber;

        public RegionalKeyBroadcastReceiver(
                Subscriber<? super NewsItemListKey> subscriber)
        {
            this.subscriber = subscriber;
        }

        @Override public void onReceive(Context context, Intent intent)
        {
            String countryCode = intent.getStringExtra(CountryLanguagePairDTO.BUNDLE_KEY_COUNTRY_CODE);
            String languageCode = intent.getStringExtra(CountryLanguagePairDTO.BUNDLE_KEY_LANGUAGE_CODE);

            subscriber.onNext(new NewsItemListRegionalKey(countryCode, languageCode, null, null));
        }
    }
}
