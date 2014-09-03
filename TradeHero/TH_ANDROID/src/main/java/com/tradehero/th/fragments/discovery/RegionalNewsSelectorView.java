package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class RegionalNewsSelectorView extends LinearLayout
{
    @InjectView(R.id.news_region_selector) TextView mRegionSelector;

    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;

    private String mLanguageCode;
    private String mCountryCode;
    private String mCountryName;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileFetchListener;

    //<editor-fold desc="Constructors">
    public RegionalNewsSelectorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        DaggerUtils.inject(this);
        userProfileFetchListener = new UserProfileFetchListener();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (mLanguageCode != null)
        {
            mRegionSelector.setText(mCountryCode);
        }
        else
        {
            fetchAndSetUserDefaultLanguage();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        detachUserProfileFetchTask();
        super.onDetachedFromWindow();
    }

    private void fetchAndSetUserDefaultLanguage()
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
        Country country = userProfileDTO.getCountry();
        if (country != null)
        {
            mCountryName = getContext().getString(country.locationName);
            if (display)
            {
                mRegionSelector.setText(mCountryName);
            }
        }
    }
}
