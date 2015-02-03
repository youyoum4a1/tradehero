package com.tradehero.th.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.misc.exception.KnownServerErrors;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.rx.dialog.AlertDialogOnSubscribe;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class SocialAlertDialogRxUtil extends AlertDialogRxUtil
{
    //<editor-fold desc="Constructors">
    @Inject public SocialAlertDialogRxUtil(@NonNull VersionUtils versionUtils)
    {
        super(versionUtils);
    }
    //</editor-fold>

    @NonNull public Observable<Pair<DialogInterface, Integer>> popNeedToLinkSocial(
            @NonNull Context activityContext,
            @NonNull SocialNetworkEnum socialNetwork)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(activityContext.getString(
                                R.string.link,
                                socialNetwork.getName()))
                        .setMessage(activityContext.getString(
                                R.string.link_description,
                                socialNetwork.getName())))
                .setPositiveButton(R.string.link_now)
                .setNegativeButton(R.string.later)
                .setCanceledOnTouchOutside(true)
                .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popErrorSocialAuth(
            @NonNull final Context activityContext,
            @NonNull final Throwable error)
    {
        THException reprocessed = new THException(error);
        String errorMessage = reprocessed.getMessage();
        if (errorMessage != null
                && (KnownServerErrors.isAccountAlreadyLinked(errorMessage)
                || KnownServerErrors.isAccountAlreadyRegistered(errorMessage)))
        {
            return popAccountAlreadyLinked(activityContext);
        }
        else
        {
            return popErrorMessage(activityContext, error);
        }
    }


    @NonNull public Observable<Pair<DialogInterface, Integer>> popAccountAlreadyLinked(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.account_already_linked_title)
                        .setMessage(R.string.account_already_linked_message))
                .setPositiveButton(R.string.ok)
                .setCanceledOnTouchOutside(true)
                .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popErrorUnlinkDefaultAccount(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.authentication_unlink_fail_message))
                .setPositiveButton(R.string.ok)
                .setCanceledOnTouchOutside(true)
                .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popConfirmUnlinkAccount(
            @NonNull final Context activityContext,
            @NonNull final SocialNetworkEnum socialNetworkEnum)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(activityContext.getString(
                                R.string.authentication_unlink_confirm_dialog_title,
                                activityContext.getString(socialNetworkEnum.nameResId)))
                        .setMessage(activityContext.getString(
                                R.string.authentication_unlink_confirm_dialog_message,
                                activityContext.getString(socialNetworkEnum.nameResId))))
                .setPositiveButton(R.string.authentication_unlink_confirm_dialog_button_ok)
                .setNegativeButton(R.string.cancel)
                .setCanceledOnTouchOutside(true)
                .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }
}
