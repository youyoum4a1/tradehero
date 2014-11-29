package com.tradehero.th.models.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.R;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.news.ShareDialogFactory;
import com.tradehero.th.fragments.news.ShareDialogLayout;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.utils.AlertDialogUtil;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;

public class SocialShareHelper
{
    @NonNull protected final Context applicationContext;
    @NonNull protected final Provider<Activity> activityHolder;
    @NonNull protected final Provider<DashboardNavigator> navigatorProvider;
    @NonNull protected final ShareDialogFactory shareDialogFactory;
    @NonNull protected final AlertDialogUtil alertDialogUtil;
    @NonNull protected final Provider<SocialSharer> socialSharerProvider;

    @Nullable protected OnMenuClickedListener menuClickedListener;

    protected Dialog shareDialog;
    protected SocialSharer currentSocialSharer;
    protected AlertDialog popOfferConnectDialog;

    protected SocialShareFormDTO formWaitingToConnect;

    //<editor-fold desc="Constructors">
    @Inject public SocialShareHelper(
            @NonNull Context applicationContext,
            @NonNull Provider<Activity> activityHolder,
            @NonNull Provider<DashboardNavigator> navigatorProvider,
            @NonNull ShareDialogFactory shareDialogFactory,
            @NonNull AlertDialogUtil alertDialogUtil,
            @NonNull Provider<SocialSharer> socialSharerProvider)
    {
        this.applicationContext = applicationContext;
        this.activityHolder = activityHolder;
        this.navigatorProvider = navigatorProvider;
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
    public void setMenuClickedListener(@Nullable OnMenuClickedListener menuClickedListener)
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

    protected void notifyShareMenuRequestedClicked(@NonNull SocialShareFormDTO shareFormDTO)
    {
        OnMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onShareRequestedClicked(shareFormDTO);
        }
    }

    protected void notifyConnectRequired(@NonNull SocialShareFormDTO shareFormDTO,
            @NonNull List<SocialNetworkEnum> toConnect)
    {
        OnMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onConnectRequired(shareFormDTO, toConnect);
        }
    }

    protected void notifyShared(@NonNull SocialShareFormDTO shareFormDTO,
            @NonNull SocialShareResultDTO shareResultDTO)
    {
        cancelFormWaiting();
        OnMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onShared(shareFormDTO, shareResultDTO);
        }
    }

    protected void notifyShareFailed(@NonNull SocialShareFormDTO shareFormDTO, @NonNull Throwable error)
    {
        cancelFormWaiting();
        OnMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onShareFailed(shareFormDTO, error);
        }
    }
    //</editor-fold>

    public void share(@NonNull DTO whatToShare)
    {
        cancelFormWaiting();
        dismissShareDialog();
        shareDialog = shareDialogFactory.createShareDialog(
                activityHolder.get(),
                whatToShare,
                createShareMenuClickedListener());
    }

    @NonNull protected ShareDialogLayout.OnShareMenuClickedListener createShareMenuClickedListener()
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

        @Override public void onShareRequestedClicked(@NonNull SocialShareFormDTO socialShareFormDTO)
        {
            dismissShareDialog();
            notifyShareMenuRequestedClicked(socialShareFormDTO);
            share(socialShareFormDTO);
        }
    }

    public void share(@NonNull SocialShareFormDTO socialShareFormDTO)
    {
        cancelFormWaiting();
        formWaitingToConnect = socialShareFormDTO;
        detachSocialSharer();
        currentSocialSharer = socialSharerProvider.get();
        currentSocialSharer.setSharedListener(createSharedListener());
        currentSocialSharer.share(socialShareFormDTO);
    }

    @NonNull protected SocialSharer.OnSharedListener createSharedListener()
    {
        return new SocialShareHelperSharedListener();
    }

    protected class SocialShareHelperSharedListener implements SocialSharer.OnSharedListener
    {
        @Override public void onConnectRequired(@NonNull SocialShareFormDTO shareFormDTO, @NonNull List<SocialNetworkEnum> toConnect)
        {
            notifyConnectRequired(shareFormDTO, toConnect);
            offerToConnect(toConnect);
        }

        @Override public void onShared(@NonNull SocialShareFormDTO shareFormDTO,
                @NonNull SocialShareResultDTO socialShareResultDTO)
        {
            notifyShared(shareFormDTO, socialShareResultDTO);
        }

        @Override public void onShareFailed(@NonNull SocialShareFormDTO shareFormDTO, @NonNull Throwable throwable)
        {
            notifyShareFailed(shareFormDTO, throwable);
        }
    }

    public void offerToConnect(@NonNull List<SocialNetworkEnum> toConnect)
    {
        // HACK FIXME Connect first only
        offerToConnect(toConnect.get(0));
    }

    public void offerToConnect(@NonNull SocialNetworkEnum socialNetwork)
    {
        detachOfferConnectDialog();
        alertDialogUtil.popWithOkCancelButton(
                activityHolder.get(),
                activityHolder.get().getString(R.string.link, socialNetwork.getName()),
                activityHolder.get().getString(R.string.link_description, socialNetwork.getName()),
                R.string.link_now,
                R.string.later,
                createConnectDialogListener(socialNetwork),
                createNoConnectDialogListener());
    }

    @NonNull protected DialogInterface.OnClickListener createNoConnectDialogListener()
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

    @NonNull protected DialogInterface.OnClickListener createConnectDialogListener(SocialNetworkEnum socialNetwork)
    {
        return new SocialShareHelperConnectClickListener(socialNetwork);
    }

    protected class SocialShareHelperConnectClickListener implements DialogInterface.OnClickListener
    {
        private final SocialNetworkEnum socialNetwork;

        //<editor-fold desc="Constructors">
        public SocialShareHelperConnectClickListener(
                SocialNetworkEnum socialNetwork)
        {
            this.socialNetwork = socialNetwork;
        }
        //</editor-fold>

        @Override public void onClick(DialogInterface dialog, int which)
        {
            // TODO use new SocialLinkHelper
            detachOfferConnectDialog();
            Bundle args = new Bundle();
            SettingsFragment.putSocialNetworkToConnect(args, socialNetwork);
            navigatorProvider.get().pushFragment(SettingsFragment.class, args);
        }
    }

    public interface OnMenuClickedListener
    {
        void onCancelClicked();
        void onShareRequestedClicked(@NonNull SocialShareFormDTO socialShareFormDTO);
        void onConnectRequired(@NonNull SocialShareFormDTO shareFormDTO, @NonNull List<SocialNetworkEnum> toConnect);
        void onShared(@NonNull SocialShareFormDTO shareFormDTO, @NonNull SocialShareResultDTO socialShareResultDTO);
        void onShareFailed(@NonNull SocialShareFormDTO shareFormDTO, @NonNull Throwable throwable);
    }
}