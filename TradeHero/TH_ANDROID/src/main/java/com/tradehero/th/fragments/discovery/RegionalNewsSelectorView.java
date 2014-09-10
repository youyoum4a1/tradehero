package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnFocusChanged;
import com.android.internal.util.Predicate;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.common.widget.InstantAutoCompleteTextView;
import com.tradehero.th.R;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.CollectionUtils;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DeviceUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RegionalNewsSelectorView extends LinearLayout
{
    @InjectView(R.id.news_region_selector) TextView mRegionSelector;
    @InjectView(R.id.country_filter_text_box) InstantAutoCompleteTextView mCountryFilter;
    @OnFocusChanged(R.id.country_filter_text_box) void handleCountryFilterFocusChanged(boolean hasFocus)
    {
        if (hasFocus)
        {
            mCountryFilter.showAllSuggestions();
            mCountryFilter.showDropDown();
        }
    }

    @InjectView(R.id.discovery_news_carousel_spinner_wrapper) BetterViewAnimator mRegionSelectorWrapper;
    private UserProfileDTO userProfileDTO;

    @OnClick(R.id.news_region_selector) void handleRegionSelectorClick()
    {
        int currentDisplayedChild = mRegionSelectorWrapper.getDisplayedChildLayoutId();
        if (currentDisplayedChild == mRegionSelector.getId())
        {
            mCountryFilter.setText(null);
            mRegionSelectorWrapper.setDisplayedChildByLayoutId(mCountryFilter.getId());
            mCountryFilter.requestFocus();
            DeviceUtil.showKeyboardDelayed(mCountryFilter);
        }
    }

    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject NewsServiceWrapper mNewsServiceWrapper;

    private String mLanguageCode;
    private String mCountryCode;
    private String mCountryName;

    private CountryAdapter mCountryAdapter;
    private MiddleCallback<PaginatedDTO<CountryLanguagePairDTO>> mCountryLanguageFetchMiddleCallback;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileFetchListener;

    //<editor-fold desc="Constructors">
    public RegionalNewsSelectorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        DaggerUtils.inject(this);
        userProfileFetchListener = new UserProfileFetchListener();
        mCountryAdapter = new CountryAdapter(getContext(), R.layout.country_item_view);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);

        mCountryFilter.setAdapter(mCountryAdapter);
        mCountryFilter.setThreshold(0);
        mCountryFilter.addTextChangedListener(new TextWatcher()
        {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override public void afterTextChanged(Editable s)
            {
                if (s.toString().length() == 0)
                {
                    cancelSearch();
                }
            }
        });
        mCountryFilter.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                CountryLanguagePairDTO countryLanguagePair = (CountryLanguagePairDTO) parent.getItemAtPosition(position);
                mRegionSelector.setText(countryLanguagePair.name);
                cancelSearch();
                sendRegionalNewsChangedEvent(countryLanguagePair);
            }
        });
    }

    private void sendRegionalNewsChangedEvent(CountryLanguagePairDTO countryLanguagePair)
    {
        Intent regionalNewsChangedIntent = new Intent(RegionalNewsHeadlineFragment.REGION_CHANGED);
        regionalNewsChangedIntent.putExtra(CountryLanguagePairDTO.BUNDLE_KEY_COUNTRY_CODE, countryLanguagePair.countryCode);
        regionalNewsChangedIntent.putExtra(CountryLanguagePairDTO.BUNDLE_KEY_LANGUAGE_CODE, countryLanguagePair.languageCode);

        LocalBroadcastManager.getInstance(getContext())
                .sendBroadcast(regionalNewsChangedIntent);
    }

    private void cancelSearch()
    {
        mRegionSelectorWrapper.setDisplayedChildByLayoutId(mRegionSelector.getId());
        DeviceUtil.dismissKeyboard(mCountryFilter);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (mLanguageCode != null)
        {
            mRegionSelector.setText(mCountryCode);
        }
        fetchAndSetUserRegional();
    }

    @Override protected void onDetachedFromWindow()
    {
        mCountryLanguageFetchMiddleCallback.setPrimaryCallback(null);
        detachUserProfileFetchTask();
        super.onDetachedFromWindow();
    }

    private void fetchAndSetUserRegional()
    {
        UserBaseKey currentUserBaseKey = currentUserId.toUserBaseKey();
        detachUserProfileFetchTask();
        userProfileCache.register(currentUserBaseKey, userProfileFetchListener);
        userProfileCache.getOrFetchAsync(currentUserBaseKey);
    }

    private void detachUserProfileFetchTask()
    {
        if (userProfileFetchListener != null)
        {
            userProfileCache.unregister(userProfileFetchListener);
        }
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

    private class UserProfileFetchListener implements DTOCacheNew.HurriedListener<UserBaseKey,UserProfileDTO>
    {
        @Override public void onPreCachedDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            onDTOReceived(key, value);
        }

        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(new THException(error));
        }
    }

    private void linkWith(UserProfileDTO userProfileDTO, boolean display)
    {
        this.userProfileDTO = userProfileDTO;

        mCountryLanguageFetchMiddleCallback = mNewsServiceWrapper.getCountryLanguagePairs(new CountryLanguageFetchCallback());
    }

    private class CountryLanguageFetchCallback implements Callback<PaginatedDTO<CountryLanguagePairDTO>>
    {
        @Override public void success(PaginatedDTO<CountryLanguagePairDTO> countryLanguagePairDTOPaginatedDTO, Response response)
        {
            linkWith(countryLanguagePairDTOPaginatedDTO.getData(), true);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
        }
    }

    private void linkWith(List<CountryLanguagePairDTO> data, boolean andDisplay)
    {
        mCountryAdapter.clear();
        mCountryAdapter.addAll(data);
        if (andDisplay)
        {
            Collection<CountryLanguagePairDTO> userCountryLanguagePairs =
                    CollectionUtils.filter(new ArrayList<>(data), new Predicate<CountryLanguagePairDTO>()
                    {
                        @Override public boolean apply(CountryLanguagePairDTO countryLanguagePairDTO)
                        {
                            return countryLanguagePairDTO.countryCode.equalsIgnoreCase(userProfileDTO.countryCode);
                        }
                    });
            if (!userCountryLanguagePairs.isEmpty())
            {
                CountryLanguagePairDTO singleCountryLanguagePair = userCountryLanguagePairs.iterator().next();
                mCountryName = singleCountryLanguagePair.name;
                if (andDisplay)
                {
                    mRegionSelector.setText(mCountryName);
                }
            }
            mCountryAdapter.notifyDataSetChanged();
        }
    }
}
