package com.tradehero.th.fragments.settings;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.ReferralCodeShareFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.news.ShareDialogFactory;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.observers.EmptyObserver;
import timber.log.Timber;

public class SettingsReferralCodeFragment extends DashboardFragment
{
    private static final int VIEW_CLAIM = 0;
    private static final int VIEW_ALREADY_CLAIMED = 1;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject SocialServiceWrapper socialServiceWrapper;
    @Inject ShareDialogFactory shareDialogFactory;
    @Inject AlertDialogUtil alertDialogUtil;

    @InjectView(R.id.invite_code_claimed_switcher) ViewSwitcher alreadyClaimedSwitcher;
    @InjectView(R.id.btn_referral_copy) View btnCopy;
    @InjectView(R.id.btn_referral_share) View btnShare;
    @InjectView(R.id.settings_referral_code) TextView mReferralCode;

    private  ClipboardManager clipboardManager;
    @Nullable private Subscription profileCacheSubscription;
    private UserProfileDTO userProfileDTO;
    @Nullable private Subscription shareReqCodeSubscription;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings_referral_code_layout, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        setActionBarTitle(R.string.settings_primary_referral_code);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchProfile();
    }

    @Override public void onStop()
    {
        unsubscribe(profileCacheSubscription);
        profileCacheSubscription = null;
        unsubscribe(shareReqCodeSubscription);
        shareReqCodeSubscription = null;
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected void fetchProfile()
    {
        unsubscribe(profileCacheSubscription);
        profileCacheSubscription = AndroidObservable.bindFragment(this,
                userProfileCache.get(currentUserId.toUserBaseKey()))
                .subscribe(createProfileCacheObserver());
    }

    @NonNull protected Observer<Pair<UserBaseKey, UserProfileDTO>> createProfileCacheObserver()
    {
        return new SettingsReferralUserProfileObserver();
    }

    protected class SettingsReferralUserProfileObserver extends EmptyObserver<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            linkWith(pair.second);
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
            Timber.e("Failed to fetch profile info", e);
        }
    }

    protected void linkWith(@NonNull UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
        mReferralCode.setText(userProfileDTO.referralCode);
        alreadyClaimedSwitcher.setDisplayedChild(userProfileDTO.alreadyClaimedInvitedDollars() ? VIEW_ALREADY_CLAIMED : VIEW_CLAIM);

        btnCopy.setEnabled(true);
        btnShare.setEnabled(true);
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.btn_referral_copy)
    protected void copyToClipboard(View view)
    {
        ClipData clip = ClipData.newPlainText(getString(R.string.settings_primary_referral_code), userProfileDTO.referralCode);
        clipboardManager.setPrimaryClip(clip);
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.btn_referral_share)
    protected void shareToSocialNetwork(View view)
    {
        // TODO
        //shareDialog = shareDialogFactory.createShareDialog(
        //        getActivity(),
        //        discussionToShare,
        //        createShareMenuClickedListener());
    }

    protected void effectShare(@NonNull SocialNetworkEnum socialNetworkEnum)
    {
        alertDialogUtil.showProgressDialog(getActivity(), getString(R.string.referral_code_sharing_to_networks));
        unsubscribe(shareReqCodeSubscription);
        shareReqCodeSubscription = AndroidObservable.bindFragment(
                this,
                socialServiceWrapper.shareRx(new ReferralCodeShareFormDTO(socialNetworkEnum)))
        .subscribe(createShareObserver());
    }

    @NonNull protected Observer<UserProfileDTO> createShareObserver()
    {
        return new ShareObserver();
    }

    protected class ShareObserver implements Observer<UserProfileDTO>
    {
        @Override public void onNext(UserProfileDTO args)
        {
            linkWith(args);
        }

        @Override public void onCompleted()
        {
            alertDialogUtil.dismissProgressDialog();
        }

        @Override public void onError(Throwable e)
        {
            alertDialogUtil.dismissProgressDialog();
            THToast.show(R.string.error_share_referral_code_on_network);
            Timber.e(e, "Failed to share on social networks");
        }
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
