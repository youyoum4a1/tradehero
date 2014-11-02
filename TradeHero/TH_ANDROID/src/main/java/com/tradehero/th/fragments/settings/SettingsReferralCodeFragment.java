package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class SettingsReferralCodeFragment extends DashboardFragment
{
    private static final int VIEW_CLAIM = 0;
    private static final int VIEW_ALREADY_CLAIMED = 1;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;

    @InjectView(R.id.invite_code_claimed_switcher) ViewSwitcher alreadyClaimedSwitcher;
    @InjectView(R.id.settings_referral_code) TextView mReferralCode;

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

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected void fetchProfile()
    {
        AndroidObservable.bindFragment(this,
                userProfileCache.get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createProfileCacheObserver());
    }

    protected Observer<Pair<UserBaseKey, UserProfileDTO>> createProfileCacheObserver()
    {
        return new SettingsReferralUserProfileObserver();
    }

    protected class SettingsReferralUserProfileObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            mReferralCode.setText(pair.second.referralCode);
            alreadyClaimedSwitcher.setDisplayedChild(pair.second.alreadyClaimedInvitedDollars() ? VIEW_ALREADY_CLAIMED : VIEW_CLAIM);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
            Timber.e("Failed to fetch profile info", e);
        }
    }

    @OnClick({R.id.btn_cancel, R.id.btn_cancel_submit, R.id.btn_done})
    protected void popFragment(/*View view*/)
    {
        if (navigator != null)
        {
            navigator.get().popFragment();
        }
    }
}
