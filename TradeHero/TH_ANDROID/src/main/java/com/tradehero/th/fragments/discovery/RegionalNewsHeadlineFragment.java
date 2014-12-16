package com.tradehero.th.fragments.discovery;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListRegionalKey;
import com.tradehero.th.rx.ToastOnErrorAction;
import java.util.Locale;
import javax.inject.Inject;
import rx.Observable;
import rx.android.observables.AndroidObservable;
import rx.functions.Func1;
import rx.operators.OperatorLocalBroadcastRegister;
import rx.schedulers.Schedulers;

public class RegionalNewsHeadlineFragment extends NewsHeadlineFragment
{
    public static final String REGION_CHANGED = RegionalNewsHeadlineFragment.class + ".regionChanged";

    @Inject Locale locale;
    @Inject @RegionalNews CountryLanguagePreference countryLanguagePreference;
    @Inject ToastOnErrorAction toastOnErrorAction;

    @Override protected void initView(View view)
    {
        super.initView(view);

        subscriptions.add(
                AndroidObservable.bindFragment(
                        this,
                        Observable.create(new OperatorLocalBroadcastRegister(getActivity(), new IntentFilter(REGION_CHANGED)))
                                .map((Func1<Intent, NewsItemListKey>) intent -> newsItemListKeyFromPref())
                                .subscribeOn(Schedulers.io()))
                        .onErrorResumeNext(Observable.empty())
                        .subscribe(this::replaceNewsItemListView, toastOnErrorAction));
    }

    private NewsItemListRegionalKey newsItemListKeyFromPref()
    {
        CountryLanguagePairDTO countryLanguagePairDTO = countryLanguagePreference.get();
        return new NewsItemListRegionalKey(countryLanguagePairDTO.countryCode, countryLanguagePairDTO.languageCode, null, null);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (countryLanguagePreference.isSet())
        {
            newsItemListKey = newsItemListKeyFromPref();
        }
        else
        {
            newsItemListKey = new NewsItemListRegionalKey(locale.getCountry(), locale.getLanguage(), null, null);
        }
    }
}