package com.androidth.general.fragments.settings;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.api.social.ReferralCodeDTO;
import com.androidth.general.api.system.SystemStatusDTO;
import com.androidth.general.api.system.SystemStatusKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.models.number.THSignedMoney;
import com.androidth.general.models.share.SocialShareHelper;
import com.androidth.general.network.share.dto.SocialDialogResult;
import com.androidth.general.persistence.system.SystemStatusCache;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.EmptyAction1;
import javax.inject.Inject;

import butterknife.Unbinder;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class SettingsReferralCodeFragment extends BaseFragment
{
    private static final int VIEW_CLAIM = 0;
    private static final int VIEW_ALREADY_CLAIMED = 1;

    private static final int MESSAGE_2 = R.string.settings_referral_code_message_2;
    private static final int MESSAGE_3 = R.string.settings_referral_code_message_3;
    private static final int MESSAGE_4 = R.string.settings_referral_code_message_4;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject SystemStatusCache systemStatusCache;
    @Inject SocialShareHelper socialShareHelper;

    @BindView(R.id.invite_code_claimed_switcher) ViewSwitcher alreadyClaimedSwitcher;
    @BindView(R.id.txt_message_2) TextView message2;
    @BindView(R.id.txt_message_3) TextView message3;
    @BindView(R.id.txt_message_4) TextView message4;
    @BindView(R.id.btn_referral_copy) View btnCopy;
    @BindView(R.id.btn_referral_share) View btnShare;
    @BindView(R.id.settings_referral_code) TextView mReferralCode;

    private ClipboardManager clipboardManager;
    private UserProfileDTO userProfileDTO;

    private Unbinder unbinder;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_settings_referral_code_layout, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        message2.setText(Html.fromHtml(getString(MESSAGE_2)));
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        setActionBarTitle(R.string.settings_primary_referral_code);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchProfile();
        fetchSystemStatus();
    }

    @Override public void onDestroyView()
    {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        clipboardManager = null;
        super.onDetach();
    }

    protected void fetchProfile()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(this,
                userProfileCache.get(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO profile)
                            {
                                linkWith(profile);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable error)
                            {
                                SettingsReferralCodeFragment.this.handleFetchUserProfileFailed(error);
                            }
                        }));
    }

    protected void linkWith(@NonNull UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
        mReferralCode.setText(userProfileDTO.referralCode);
        alreadyClaimedSwitcher.setDisplayedChild(userProfileDTO.alreadyClaimedInvitedDollars() ? VIEW_ALREADY_CLAIMED : VIEW_CLAIM);

        btnCopy.setEnabled(true);
        btnShare.setEnabled(true);

        if (userProfileDTO.accumulatedReferralAwards != null)
        {
            THSignedMoney reward = THSignedMoney.builder(userProfileDTO.accumulatedReferralAwards.priceRefCcy)
                    .currency(userProfileDTO.accumulatedReferralAwards.currencyDisplay)
                    .build();
            message4.setText(Html.fromHtml(getString(MESSAGE_4, reward.toString())));
            message4.setVisibility(View.VISIBLE);
        }
    }

    protected void handleFetchUserProfileFailed(@NonNull Throwable e)
    {
        THToast.show(R.string.error_fetch_your_user_profile);
        Timber.e("Failed to fetch profile info", e);
    }

    protected void fetchSystemStatus()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                systemStatusCache.get(new SystemStatusKey())
                        .map(new PairGetSecond<SystemStatusKey, SystemStatusDTO>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<SystemStatusDTO>()
                        {
                            @Override public void call(SystemStatusDTO systemStatusDTO)
                            {
                                linkWith(systemStatusDTO);
                            }
                        },
                        new EmptyAction1<Throwable>()));
    }

    protected void linkWith(@NonNull SystemStatusDTO statusDTO)
    {
        if (statusDTO.friendReferralAward != null)
        {
            THSignedMoney reward = THSignedMoney.builder(statusDTO.friendReferralAward.priceRefCcy)
                    .currency(statusDTO.friendReferralAward.currencyDisplay)
                    .build();

            message3.setText(Html.fromHtml(getString(MESSAGE_3, reward.toString())));
            message3.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.btn_referral_copy)
    protected void copyToClipboard(View view)
    {
        ClipData clip = ClipData.newPlainText(getString(R.string.settings_primary_referral_code), userProfileDTO.referralCode);
        clipboardManager.setPrimaryClip(clip);
        THToast.show(getString(R.string.referral_code_copied_clipboard, userProfileDTO.referralCode));
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.btn_referral_share)
    protected void shareToSocialNetwork(View view)
    {
        onStopSubscriptions.add(socialShareHelper.show(new ReferralCodeDTO(userProfileDTO.referralCode))
                .subscribe(
                        new EmptyAction1<SocialDialogResult>(),
                        new EmptyAction1<Throwable>()));
    }

    @SuppressWarnings("UnusedParameters")
    @OnClick({R.id.btn_cancel, R.id.btn_cancel_submit, R.id.btn_done})
    protected void popFragment(View view)
    {
        if (navigator != null)
        {
            navigator.get().popFragment();
        }
    }
}
