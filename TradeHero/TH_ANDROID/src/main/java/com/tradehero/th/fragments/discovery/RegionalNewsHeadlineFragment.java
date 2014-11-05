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
    @Inject @RegionalNews CountryLanguagePreference countryLanguagePreference;

    private BroadcastReceiver regionChangeBroadcastReceiver;

    @Override protected Observable<NewsItemListKey> createNewsItemListKeyObservable()
    {
        return super.createNewsItemListKeyObservable()
                .mergeWith(createNewsItemListRegionalKeyObservable());
    }

    private Observable<NewsItemListKey> createNewsItemListRegionalKeyObservable()
    {
        // observable of the UI event which user change the region
        return Observable
                .create((Observable.OnSubscribe<NewsItemListKey>) subscriber -> {
                    if (regionChangeBroadcastReceiver == null)
                    {
                        regionChangeBroadcastReceiver = new RegionalKeyBroadcastReceiver(subscriber);
                        LocalBroadcastManager.getInstance(getActivity())
                                .registerReceiver(regionChangeBroadcastReceiver, new IntentFilter(REGION_CHANGED));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext((t1) -> {
                    if (!(((NewsItemListRegionalKey)t1).countryCode.equalsIgnoreCase(((NewsItemListRegionalKey)newsItemListKey).countryCode)
                            && ((NewsItemListRegionalKey)t1).languageCode.equalsIgnoreCase(((NewsItemListRegionalKey)newsItemListKey).languageCode)))
                    {
                        activateNewsListView(t1);
                    }
                });
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
            CountryLanguagePairDTO countryLanguagePairDTO = countryLanguagePreference.get();

            subscriber.onNext(new NewsItemListRegionalKey(countryLanguagePairDTO.countryCode, countryLanguagePairDTO.languageCode, null, null));
        }
    }
}
