package com.tradehero.th.models.share;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.Window;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.R;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareFormDTOFactory;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthenticationProvider;
import com.tradehero.th.auth.SocialAuth;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.news.ShareDialogFactory;
import com.tradehero.th.fragments.news.ShareDialogLayout;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.network.share.dto.ConnectRequired;
import com.tradehero.th.network.share.dto.SocialDialogResult;
import com.tradehero.th.network.share.dto.SocialShareResult;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.SocialAlertDialogRxUtil;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

public class SocialShareHelper
{
    @NonNull protected final Context applicationContext;
    @NonNull protected final Provider<Activity> activityHolder;
    @NonNull protected final Provider<DashboardNavigator> navigatorProvider;
    @NonNull protected final Provider<SocialSharer> socialSharerProvider;
    @NonNull protected final Map<SocialNetworkEnum, AuthenticationProvider> authenticationProviders;

    //<editor-fold desc="Constructors">
    @Inject public SocialShareHelper(
            @NonNull Context applicationContext,
            @NonNull Provider<Activity> activityHolder,
            @NonNull Provider<DashboardNavigator> navigatorProvider,
            @NonNull Provider<SocialSharer> socialSharerProvider,
            @NonNull @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> authenticationProviders)
    {
        this.applicationContext = applicationContext;
        this.activityHolder = activityHolder;
        this.navigatorProvider = navigatorProvider;
        this.socialSharerProvider = socialSharerProvider;
        this.authenticationProviders = authenticationProviders;
    }
    //</editor-fold>

    @NonNull public Observable<SocialDialogResult> show(@NonNull DTO whatToShare)
    {
        return createDialog(whatToShare)
                .flatMap(new Func1<Pair<Dialog, ShareDialogLayout>, Observable<ShareDialogLayout.UserAction>>()
                {
                    @Override public Observable<ShareDialogLayout.UserAction> call(Pair<Dialog, ShareDialogLayout> pair)
                    {
                        return pair.second.show(whatToShare)
                                .finallyDo(new Action0()
                                {
                                    @Override public void call()
                                    {
                                        pair.first.dismiss();
                                    }
                                });
                    }
                })
                .flatMap(new Func1<ShareDialogLayout.UserAction, Observable<? extends SocialDialogResult>>()
                {
                    @Override public Observable<? extends SocialDialogResult> call(ShareDialogLayout.UserAction userAction)
                    {
                        return SocialShareHelper.this.handleUserAction(userAction, whatToShare);
                    }
                });
    }

    @NonNull protected Observable<Pair<Dialog, ShareDialogLayout>> createDialog(@NonNull DTO whatToShare)
    {
        return Observable.just(ShareDialogFactory.createShareDialog(activityHolder.get()));
    }

    @NonNull protected Observable<? extends SocialDialogResult> handleUserAction(
            @NonNull ShareDialogLayout.UserAction userAction,
            @NonNull DTO whatToShare)
    {
        if (userAction instanceof ShareDialogLayout.ShareUserAction)
        {
            return share(SocialShareFormDTOFactory.createForm(
                    applicationContext,
                    ((ShareDialogLayout.ShareUserAction) userAction).shareDestination,
                    whatToShare));
        }
        if (userAction instanceof ShareDialogLayout.CancelUserAction)
        {
            return Observable.empty();
        }
        return Observable.error(new IllegalStateException("Unhandled UserAction " + userAction));
    }

    @NonNull public Observable<SocialShareResult> share(@NonNull SocialShareFormDTO socialShareFormDTO)
    {
        return socialSharerProvider.get().share(socialShareFormDTO)
                .flatMap(new Func1<SocialShareResult, Observable<SocialShareResult>>()
                {
                    @Override public Observable<SocialShareResult> call(SocialShareResult socialShareResult)
                    {
                        if (socialShareResult instanceof ConnectRequired)
                        {
                            return offerToConnect(((ConnectRequired) socialShareResult).toConnect)
                                    .flatMap(new Func1<UserProfileDTO, Observable<SocialShareResult>>()
                                    {
                                        @Override public Observable<SocialShareResult> call(UserProfileDTO userProfileDTO)
                                        {
                                            return share(socialShareFormDTO);
                                        }
                                    });
                        }
                        return Observable.just(socialShareResult);
                    }
                });
    }

    @NonNull public Observable<UserProfileDTO> offerToConnect(@NonNull List<SocialNetworkEnum> toConnect)
    {
        // HACK FIXME Connect first only
        return offerToConnect(toConnect.get(0));
    }

    @NonNull public Observable<UserProfileDTO> offerToConnect(@NonNull SocialNetworkEnum socialNetwork)
    {
        return SocialAlertDialogRxUtil.popNeedToLinkSocial(
                activityHolder.get(),
                socialNetwork)
                .flatMap(new Func1<OnDialogClickEvent, Observable<UserProfileDTO>>()
                {
                    @Override public Observable<UserProfileDTO> call(OnDialogClickEvent pair)
                    {
                        return handleNeedToLink(pair, socialNetwork);
                    }
                });
    }

    @NonNull public Observable<UserProfileDTO> handleNeedToLink(OnDialogClickEvent event, SocialNetworkEnum socialNetwork)
    {
        if (event.isPositive())
        {
            ProgressDialog progressDialog = new ProgressDialog(activityHolder.get());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(activityHolder.get().getString(
                    R.string.authentication_connecting_to,
                    activityHolder.get().getString(socialNetwork.nameResId)));
            progressDialog.show();
            AuthenticationProvider socialAuthenticationProvider = authenticationProviders.get(socialNetwork);
            return ((SocialAuthenticationProvider) socialAuthenticationProvider)
                    .socialLink(activityHolder.get())
                    .finallyDo(new Action0()
                    {
                        @Override public void call()
                        {
                            progressDialog.dismiss();
                        }
                    });
        }
        return Observable.empty();
    }
}