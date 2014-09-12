package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.prefs.IsVisitedSettings;
import com.tradehero.th.persistence.user.UserProfileCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class SettingsReferralCodeFragment extends DashboardFragment
{
    private static final int VIEW_CLAIM = 0;
    private static final int VIEW_ALREADY_CLAIMED = 1;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject @IsVisitedSettings BooleanPreference mIsVisitedSettingsPreference;

    @InjectView(R.id.invite_code_claimed_switcher) ViewSwitcher alreadyClaimedSwitcher;
    @InjectView(R.id.settings_referral_code) TextView mReferralCode;

    @Nullable private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userProfileCacheListener = createProfileCacheListener();
        mIsVisitedSettingsPreference.set(true);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings_referral_code_layout, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchProfile();
    }

    @Override public void onStop()
    {
        detachProfileCache();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        userProfileCacheListener = null;
        super.onDestroy();
    }

    protected void fetchProfile()
    {
        detachProfileCache();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    protected void detachProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createProfileCacheListener()
    {
        return new SettingsReferralUserProfileListener();
    }

    protected class SettingsReferralUserProfileListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            mReferralCode.setText(value.referralCode);
            alreadyClaimedSwitcher.setDisplayedChild(value.alreadyClaimedInvitedDollars() ? VIEW_ALREADY_CLAIMED : VIEW_CLAIM);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
            Timber.e("Failed to fetch profile info", error);
        }
    }

    @OnClick({R.id.btn_cancel, R.id.btn_cancel_submit, R.id.btn_done})
    protected void popFragment(/*View view*/)
    {
        DashboardNavigator navigator = getDashboardNavigator();
        if (navigator != null)
        {
            navigator.popFragment();
        }
    }
}
