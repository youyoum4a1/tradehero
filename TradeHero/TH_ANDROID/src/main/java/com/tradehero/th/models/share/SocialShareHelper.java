package com.ayondo.academy.models.share;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.Window;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.R;
import com.ayondo.academy.api.share.SocialShareFormDTO;
import com.ayondo.academy.api.share.SocialShareFormDTOFactory;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.auth.AuthenticationProvider;
import com.ayondo.academy.auth.SocialAuth;
import com.ayondo.academy.auth.SocialAuthenticationProvider;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.news.ShareDialogFactory;
import com.ayondo.academy.fragments.news.ShareDialogLayout;
import com.ayondo.academy.network.share.SocialSharer;
import com.ayondo.academy.network.share.dto.ConnectRequired;
import com.ayondo.academy.network.share.dto.SocialDialogResult;
import com.ayondo.academy.network.share.dto.SocialShareResult;
import com.ayondo.academy.rx.dialog.OnDialogClickEvent;
import com.ayondo.academy.rx.view.DismissDialogAction0;
import com.ayondo.academy.utils.SocialAlertDialogRxUtil;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

public class SocialShareHelper
{
    @NonNull protected final Resources resources;
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
        this.resources = applicationContext.getResources();
        this.activityHolder = activityHolder;
        this.navigatorProvider = navigatorProvider;
        this.socialSharerProvider = socialSharerProvider;
        this.authenticationProviders = authenticationProviders;
    }
    //</editor-fold>

    @NonNull public Observable<SocialDialogResult> show(@NonNull final DTO whatToShare)
    {
        return createDialog(whatToShare)
                .flatMap(new Func1<Pair<Dialog, ShareDialogLayout>, Observable<ShareDialogLayout.UserAction>>()
                {
                    @Override public Observable<ShareDialogLayout.UserAction> call(final Pair<Dialog, ShareDialogLayout> pair)
                    {
                        return pair.second.show(whatToShare)
                                .finallyDo(new DismissDialogAction0(pair.first));
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
                    resources,
                    ((ShareDialogLayout.ShareUserAction) userAction).shareDestination,
                    whatToShare));
        }
        if (userAction instanceof ShareDialogLayout.CancelUserAction)
        {
            return Observable.empty();
        }
        return Observable.error(new IllegalStateException("Unhandled UserAction " + userAction));
    }

    @NonNull public Observable<Boolean> canShare(@NonNull SocialNetworkEnum socialNetwork)
    {
        return ((SocialAuthenticationProvider) authenticationProviders.get(socialNetwork))
                .canShare(activityHolder.get());
    }

    @NonNull public Observable<SocialShareResult> share(@NonNull final SocialShareFormDTO socialShareFormDTO)
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

    @NonNull public Observable<UserProfileDTO> offerToConnect(@NonNull final SocialNetworkEnum socialNetwork)
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

    @NonNull public Observable<UserProfileDTO> handleNeedToLink(
            @NonNull OnDialogClickEvent event,
            @NonNull SocialNetworkEnum socialNetwork)
    {
        if (event.isPositive())
        {
            return handleNeedToLink(socialNetwork);
        }
        return Observable.empty();
    }

    @NonNull public Observable<UserProfileDTO> handleNeedToLink(@NonNull SocialNetworkEnum socialNetwork)
    {
        final ProgressDialog progressDialog = new ProgressDialog(activityHolder.get());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(activityHolder.get().getString(
                R.string.authentication_connecting_to,
                activityHolder.get().getString(socialNetwork.nameResId)));
        progressDialog.show();
        Action0 dismissAction = new DismissDialogAction0(progressDialog);
        return socialLink(socialNetwork)
                .finallyDo(dismissAction)
                .doOnUnsubscribe(dismissAction);
    }

    @NonNull public Observable<UserProfileDTO> socialLink(@NonNull SocialNetworkEnum socialNetwork)
    {
        AuthenticationProvider socialAuthenticationProvider = authenticationProviders.get(socialNetwork);
        return ((SocialAuthenticationProvider) socialAuthenticationProvider)
                .socialLink(activityHolder.get());
    }
}