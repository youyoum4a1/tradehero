package com.ayondo.academy.fragments.competition;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.WebViewClient;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.fragments.web.BaseWebViewFragment;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class WizardWebViewFragment extends BaseWebViewFragment
{
    private static final String BUNDLE_KEY_APPLICABLE_PORTFOLIO_ID = WizardWebViewFragment.class.getName() + ".applicablePortfolioId";

    private BehaviorSubject<String> urlBehavior;

    public static void putApplicablePortfolioId(@NonNull Bundle args, @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        args.putBundle(BUNDLE_KEY_APPLICABLE_PORTFOLIO_ID, applicablePortfolioId.getArgs());
    }

    @NonNull public static OwnedPortfolioId getApplicablePortfolioId(@NonNull Bundle args)
    {
        Bundle portfolioBundle = args.getBundle(BUNDLE_KEY_APPLICABLE_PORTFOLIO_ID);
        if (portfolioBundle == null)
        {
            throw new NullPointerException("Applicable Portfolio cannot be null");
        }
        return new OwnedPortfolioId(portfolioBundle);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.urlBehavior = BehaviorSubject.create();
    }

    @NonNull @Override protected WebViewClient createWebViewClient()
    {
        WizardWebViewClient webViewClient = new WizardWebViewClient(
                getActivity().getResources(),
                getApplicablePortfolioId(getArguments()));
        onDestroyViewSubscriptions.add(webViewClient.getUrlObservable()
                .subscribe(urlBehavior));
        return webViewClient;
    }

    @NonNull public Observable<String> getUrlObservable()
    {
        return urlBehavior.asObservable();
    }
}
