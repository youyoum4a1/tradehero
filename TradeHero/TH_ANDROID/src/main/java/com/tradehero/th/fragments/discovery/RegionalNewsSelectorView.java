package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
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
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.rx.ToastOnErrorAction;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Subscription;

public class RegionalNewsSelectorView extends LinearLayout
{
    @InjectView(R.id.discovery_news_carousel_spinner_wrapper) Spinner mCountryDropdown;
    private Subscription countryLanguagePairsSubscription;

    @OnItemSelected(R.id.discovery_news_carousel_spinner_wrapper) void handleItemSelected(
            AdapterView<?> adapterView, View view, int position, long id)
    {
        sendRegionalNewsChangedEvent((CountryLanguagePairDTO) adapterView.getItemAtPosition(position));
    }

    @Inject NewsServiceWrapper mNewsServiceWrapper;
    @Inject Provider<ToastOnErrorAction> toastOnErrorAction;

    private String mLanguageCode;
    private String mCountryCode;
    private String mCountryName;

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
        Intent regionalNewsChangedIntent = new Intent(RegionalNewsHeadlineFragment.REGION_CHANGED);
        regionalNewsChangedIntent.putExtra(CountryLanguagePairDTO.BUNDLE_KEY_COUNTRY_CODE, countryLanguagePair.countryCode);
        regionalNewsChangedIntent.putExtra(CountryLanguagePairDTO.BUNDLE_KEY_LANGUAGE_CODE, countryLanguagePair.languageCode);

        LocalBroadcastManager.getInstance(getContext())
                .sendBroadcast(regionalNewsChangedIntent);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        countryLanguagePairsSubscription = mNewsServiceWrapper.getCountryLanguagePairsRx()
                .map(PaginatedDTO::getData)
                .doOnError(toastOnErrorAction.get())
                .onErrorResumeNext(Observable.empty())
                .subscribe(mCountryAdapter::setItems);
    }

    @Override protected void onDetachedFromWindow()
    {
        countryLanguagePairsSubscription.unsubscribe();

        super.onDetachedFromWindow();
    }

    //<editor-fold desc="Save & Restore view state">
    @Override protected Parcelable onSaveInstanceState()
    {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState toSave = new SavedState(parcelable);
        toSave.languageCode = mLanguageCode;
        toSave.countryCode = mCountryCode;
        toSave.countryName = mCountryName;
        return toSave;
    }

    @Override protected void onRestoreInstanceState(Parcelable state)
    {
        if (!(state instanceof SavedState))
        {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.mLanguageCode = ss.languageCode;
        this.mCountryCode = ss.countryCode;
        this.mCountryName = ss.countryName;
    }

    static class SavedState extends BaseSavedState
    {
        String languageCode;
        String countryCode;
        String countryName;

        public SavedState(Parcelable superState)
        {
            super(superState);
        }

        public SavedState(Parcel parcel)
        {
            super(parcel);
            languageCode = parcel.readString();
            countryCode = parcel.readString();
            countryName = parcel.readString();
        }

        @Override public void writeToParcel(Parcel dest, int flags)
        {
            super.writeToParcel(dest, flags);
            dest.writeString(languageCode);
            dest.writeString(countryCode);
            dest.writeString(countryName);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>()
        {
            @Override public SavedState createFromParcel(Parcel parcel)
            {
                return new SavedState(parcel);
            }

            @Override public SavedState[] newArray(int size)
            {
                return new SavedState[size];
            }
        };
    }
    //</editor-fold>
}
