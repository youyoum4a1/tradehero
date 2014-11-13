package com.tradehero.th.fragments.discovery;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListRegionalKey;
import java.util.Locale;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.operators.OperatorLocalBroadcastRegister;
import rx.schedulers.Schedulers;

public class RegionalNewsHeadlineFragment extends NewsHeadlineFragment
{
    public static final String REGION_CHANGED = RegionalNewsHeadlineFragment.class + ".regionChanged";

    @Inject Locale locale;
    @Inject @RegionalNews CountryLanguagePreference countryLanguagePreference;
    @Override protected Observable<NewsItemListKey> createNewsItemListKeyObservable()
    {
        return super.createNewsItemListKeyObservable()
                .mergeWith(createNewsItemListRegionalKeyObservable());
    }

    private Observable<NewsItemListKey> createNewsItemListRegionalKeyObservable()
    {
        return Observable.create(new OperatorLocalBroadcastRegister(getActivity(), new IntentFilter(REGION_CHANGED)))
                .map((Func1<Intent, NewsItemListKey>) intent -> {
                    CountryLanguagePairDTO countryLanguagePairDTO = countryLanguagePreference.get();
                    return new NewsItemListRegionalKey(countryLanguagePairDTO.countryCode, countryLanguagePairDTO.languageCode, null, null);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::replaceNewsItemListView);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        newsItemListKey = new NewsItemListRegionalKey(locale.getCountry(), locale.getLanguage(), null, null);
    }
}
