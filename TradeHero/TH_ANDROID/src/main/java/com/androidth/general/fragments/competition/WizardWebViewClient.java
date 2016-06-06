package com.androidth.general.fragments.competition;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.androidth.general.R;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class WizardWebViewClient extends WebViewClient
{
    @NonNull private final Resources resources;
    @NonNull private final OwnedPortfolioId applicablePortfolioId;
    @NonNull private final BehaviorSubject<String> urlBehavior;

    public WizardWebViewClient(
            @NonNull Resources resources,
            @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        super();
        this.resources = resources;
        this.applicablePortfolioId = applicablePortfolioId;
        this.urlBehavior = BehaviorSubject.create();
    }

    @Override public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        Uri parsedUri = Uri.parse(url);
        if (parsedUri.getScheme().equals(resources.getString(R.string.intent_scheme)))
        {
            urlBehavior.onNext(url + "&applicablePortfolioId=" + applicablePortfolioId.portfolioId);
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @NonNull public Observable<String> getUrlObservable()
    {
        return urlBehavior.asObservable();
    }
}
