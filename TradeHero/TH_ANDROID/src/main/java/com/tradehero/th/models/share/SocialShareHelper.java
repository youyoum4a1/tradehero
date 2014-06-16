package com.tradehero.th.models.share;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareFormDTOWithEnum;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.news.ShareDialogFactory;
import com.tradehero.th.fragments.news.ShareDialogLayout;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;
import javax.inject.Provider;

public class SocialShareHelper
{
    protected final Context applicationContext;
    protected final CurrentActivityHolder currentActivityHolder;
    protected final ShareDialogFactory shareDialogFactory;
    protected final AlertDialogUtil alertDialogUtil;
    protected final Provider<SocialSharer> socialSharerProvider;

    protected OnMenuClickedListener menuClickedListener;

    protected Dialog shareDialog;
    protected SocialSharer currentSocialSharer;
    protected AlertDialog popOfferConnectDialog;

    protected SocialShareFormDTO formWaitingToConnect;

    //<editor-fold desc="Constructors">
    @Inject public SocialShareHelper(
            Context applicationContext,
            CurrentActivityHolder currentActivityHolder,
            ShareDialogFactory shareDialogFactory,
            AlertDialogUtil alertDialogUtil,
            Provider<SocialSharer> socialSharerProvider)
    {
        this.applicationContext = applicationContext;
        this.currentActivityHolder = currentActivityHolder;
        this.shareDialogFactory = shareDialogFactory;
        this.alertDialogUtil = alertDialogUtil;
        this.socialSharerProvider = socialSharerProvider;
    }
    //</editor-fold>

    public void onDetach()
    {
        setMenuClickedListener(null);
        dismissShareDialog();
        detachSocialSharer();
        detachOfferConnectDialog();
        cancelFormWaiting();
    }

    protected void dismissShareDialog()
    {
        Dialog shareDialogCopy = shareDialog;
        if (shareDialogCopy != null)
        {
            shareDialogCopy.dismiss();
        }
        shareDialog = null;
    }

    protected void detachSocialSharer()
    {
        SocialSharer socialSharerCopy = currentSocialSharer;
        if (socialSharerCopy != null)
        {
            socialSharerCopy.setSharedListener(null);
        }
        currentSocialSharer = null;
    }

    protected void detachOfferConnectDialog()
    {
        popOfferConnectDialog = null;
    }

    protected void cancelFormWaiting()
    {
        formWaitingToConnect = null;
    }

    //<editor-fold desc="Listener Handling">
    public void setMenuClickedListener(OnMenuClickedListener menuClickedListener)
    {
        this.menuClickedListener = menuClickedListener;
    }

    protected void notifyShareMenuCancelClicked()
    {
        cancelFormWaiting();
        OnMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onCancelClicked();
        }
    }

    protected void notifyShareMenuRequestedClicked(SocialShareFormDTO shareFormDTO)
    {
        OnMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onShareRequestedClicked(shareFormDTO);
        }
    }

    protected void notifyConnectRequired(SocialShareFormDTO shareFormDTO)
    {
        OnMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onConnectRequired(shareFormDTO);
        }
    }

    protected void notifyShared(SocialShareFormDTO shareFormDTO,
            SocialShareResultDTO shareResultDTO)
    {
        cancelFormWaiting();
        OnMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onShared(shareFormDTO, shareResultDTO);
        }
    }

    protected void notifyShareFailed(SocialShareFormDTO shareFormDTO, Throwable error)
    {
        cancelFormWaiting();
        OnMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onShareFailed(shareFormDTO, error);
        }
    }
    //</editor-fold>

    public boolean canShare(AbstractDiscussionCompactDTO discussionToShare)
    {
        return discussionToShare != null;
    }

    public void share(AbstractDiscussionCompactDTO discussionToShare)
    {
        if (canShare(discussionToShare))
        {
            cancelFormWaiting();
            dismissShareDialog();
            shareDialog = shareDialogFactory.createShareDialog(currentActivityHolder.getCurrentContext(), discussionToShare,
                    createShareMenuClickedListener());
        }
    }

    protected ShareDialogLayout.OnShareMenuClickedListener createShareMenuClickedListener()
    {
        return new SocialShareHelperShareMenuClickedListener();
    }

    protected class SocialShareHelperShareMenuClickedListener
            implements ShareDialogLayout.OnShareMenuClickedListener
    {
        @Override public void onCancelClicked()
        {
            dismissShareDialog();
            notifyShareMenuCancelClicked();
        }

        @Override public void onShareRequestedClicked(SocialShareFormDTO socialShareFormDTO)
        {
            dismissShareDialog();
            notifyShareMenuRequestedClicked(socialShareFormDTO);
            share(socialShareFormDTO);
        }
    }

    public void shareWaitingForm()
    {
        share(formWaitingToConnect);
    }

    public void share(SocialShareFormDTO socialShareFormDTO)
    {
        cancelFormWaiting();
        formWaitingToConnect = socialShareFormDTO;
        detachSocialSharer();
        currentSocialSharer = socialSharerProvider.get();
        currentSocialSharer.setSharedListener(createSharedListener());
        currentSocialSharer.share(socialShareFormDTO);
    }

    protected SocialSharer.OnSharedListener createSharedListener()
    {
        return new SocialShareHelperSharedListener();
    }

    protected class SocialShareHelperSharedListener implements SocialSharer.OnSharedListener
    {
        @Override public void onConnectRequired(SocialShareFormDTO shareFormDTO)
        {
            notifyConnectRequired(shareFormDTO);
            offerToConnect((SocialShareFormDTOWithEnum) shareFormDTO);
        }

        @Override public void onShared(SocialShareFormDTO shareFormDTO,
                SocialShareResultDTO socialShareResultDTO)
        {
            notifyShared(shareFormDTO, socialShareResultDTO);
        }

        @Override public void onShareFailed(SocialShareFormDTO shareFormDTO, Throwable throwable)
        {
            notifyShareFailed(shareFormDTO, throwable);
        }
    }

    public void offerToConnect(SocialShareFormDTOWithEnum shareFormDTO)
    {
        detachOfferConnectDialog();
        final SocialNetworkEnum socialNetwork = shareFormDTO.getSocialNetworkEnum();
        alertDialogUtil.popWithOkCancelButton(
                currentActivityHolder.getCurrentContext(),
                currentActivityHolder.getCurrentActivity().getString(R.string.link, socialNetwork.getName()),
                currentActivityHolder.getCurrentActivity().getString(R.string.link_description, socialNetwork.getName()),
                R.string.link_now,
                R.string.later,
                createConnectDialogListener(socialNetwork),
                createNoConnectDialogListener());
    }

    protected DialogInterface.OnClickListener createNoConnectDialogListener()
    {
        return new SocialShareHelperNoConnectClickListener();
    }

    protected class SocialShareHelperNoConnectClickListener implements DialogInterface.OnClickListener
    {
        @Override public void onClick(DialogInterface dialog, int which)
        {
            notifyShareMenuCancelClicked();
        }
    }

    protected DialogInterface.OnClickListener createConnectDialogListener(SocialNetworkEnum socialNetwork)
    {
        return new SocialShareHelperConnectClickListener(socialNetwork);
    }

    protected class SocialShareHelperConnectClickListener implements DialogInterface.OnClickListener
    {
        private final SocialNetworkEnum socialNetwork;

        public SocialShareHelperConnectClickListener(
                SocialNetworkEnum socialNetwork)
        {
            this.socialNetwork = socialNetwork;
        }

        @Override public void onClick(DialogInterface dialog, int which)
        {
            detachOfferConnectDialog();
            Bundle args = new Bundle();
            SettingsFragment.putSocialNetworkToConnect(args, socialNetwork);
            ((DashboardActivity) currentActivityHolder.getCurrentActivity()).getDashboardNavigator()
                    .pushFragment(SettingsFragment.class, args);
        }
    }

    public interface OnMenuClickedListener
    {
        void onCancelClicked();
        void onShareRequestedClicked(SocialShareFormDTO socialShareFormDTO);
        void onConnectRequired(SocialShareFormDTO shareFormDTO);
        void onShared(SocialShareFormDTO shareFormDTO, SocialShareResultDTO socialShareResultDTO);
        void onShareFailed(SocialShareFormDTO shareFormDTO, Throwable throwable);
    }
}