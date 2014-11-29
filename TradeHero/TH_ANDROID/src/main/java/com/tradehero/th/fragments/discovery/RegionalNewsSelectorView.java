package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemSelected;
import com.tradehero.th.R;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.ToastOnErrorAction;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class RegionalNewsSelectorView extends LinearLayout
{
    @InjectView(R.id.discovery_news_carousel_spinner_wrapper) Spinner mCountryDropdown;
    private Subscription countryLanguagePairsSubscription;

    @SuppressWarnings("UnusedDeclaration")
    @OnItemSelected(R.id.discovery_news_carousel_spinner_wrapper)
    void handleItemSelected(AdapterView<?> adapterView, View view, int position, long id)
    {
        sendRegionalNewsChangedEvent((CountryLanguagePairDTO) adapterView.getItemAtPosition(position));
    }

    @Inject NewsServiceWrapper mNewsServiceWrapper;
    @Inject CurrentUserId currentUserId;
    @Inject Provider<ToastOnErrorAction> toastOnErrorAction;
    @Inject UserProfileCacheRx userProfileCacheRx;
    @Inject @RegionalNews CountryLanguagePreference countryLanguagePreference;

    private CountryAdapter mCountryAdapter;

    //<editor-fold desc="Constructors">
    public RegionalNewsSelectorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
        mCountryAdapter = new CountryAdapter(getContext(), R.layout.country_item_view);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);

        mCountryDropdown.setAdapter(mCountryAdapter);
    }

    private void sendRegionalNewsChangedEvent(CountryLanguagePairDTO countryLanguagePair)
    {
        countryLanguagePreference.set(countryLanguagePair);
        Intent regionalNewsChangedIntent = new Intent(RegionalNewsHeadlineFragment.REGION_CHANGED);
        LocalBroadcastManager.getInstance(getContext())
                .sendBroadcast(regionalNewsChangedIntent);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        // observable of whenever userProfileDTO is available
        Observable<String> countryCodeObservable = userProfileCacheRx.get(currentUserId.toUserBaseKey())
                .map(userProfileDTOPair -> userProfileDTOPair.second.countryCode);
        countryLanguagePairsSubscription = mNewsServiceWrapper.getCountryLanguagePairsRx()
                .map(PaginatedDTO::getData)
                .zipWith(countryCodeObservable, Pair::create)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(countryLanguageListPair -> {
                            List<CountryLanguagePairDTO> countryLanguagePairDTOs = countryLanguageListPair.first;
                            String userCountryCode = countryLanguageListPair.second;
                            CountryLanguagePairDTO savedCountryLanguagePair = countryLanguagePreference.get();
                            if (savedCountryLanguagePair.countryCode == null)
                            {
                                savedCountryLanguagePair.countryCode = userCountryCode;
                                countryLanguagePreference.set(savedCountryLanguagePair);
                            }
                            for (int i = 0; i < countryLanguagePairDTOs.size(); ++i)
                            {
                                if (savedCountryLanguagePair.languageCode.equals(countryLanguagePairDTOs.get(i).languageCode) &&
                                        savedCountryLanguagePair.countryCode.equals(countryLanguagePairDTOs.get(i).countryCode))
                                {
                                    mCountryDropdown.setSelection(i);
                                    break;
                                }
                            }
                            mCountryAdapter.setItems(countryLanguageListPair.first);
                        },
                        toastOnErrorAction.get());
    }

    @Override protected void onDetachedFromWindow()
    {
        countryLanguagePairsSubscription.unsubscribe();

        super.onDetachedFromWindow();
    }
}
