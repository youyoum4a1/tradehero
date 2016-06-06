package com.androidth.general.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.exception.KnownServerErrors;
import com.androidth.general.exception.THException;
import com.androidth.general.rx.dialog.OnDialogClickEvent;
import rx.Observable;

public class SocialAlertDialogRxUtil extends AlertDialogRxUtil
{
    @NonNull public static Observable<OnDialogClickEvent> popNeedToLinkSocial(
            @NonNull Context activityContext,
            @NonNull SocialNetworkEnum socialNetwork)
    {
        return buildDefault(activityContext)
                .setTitle(activityContext.getString(
                        R.string.link,
                        socialNetwork.getName()))
                .setMessage(activityContext.getString(
                        R.string.link_description,
                        socialNetwork.getName()))
                .setPositiveButton(R.string.link_now)
                .setNegativeButton(R.string.later)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull public static Observable<OnDialogClickEvent> popErrorSocialAuth(
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

    @NonNull public static Observable<OnDialogClickEvent> popAccountAlreadyLinked(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.account_already_linked_title)
                .setMessage(R.string.account_already_linked_message)
                .setPositiveButton(R.string.ok)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull public static Observable<OnDialogClickEvent> popErrorUnlinkDefaultAccount(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.app_name)
                .setMessage(R.string.authentication_unlink_fail_message)
                .setPositiveButton(R.string.ok)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull public static Observable<OnDialogClickEvent> popConfirmUnlinkAccount(
            @NonNull final Context activityContext,
            @NonNull final SocialNetworkEnum socialNetworkEnum)
    {
        return buildDefault(activityContext)
                .setTitle(activityContext.getString(
                        R.string.authentication_unlink_confirm_dialog_title,
                        activityContext.getString(socialNetworkEnum.nameResId)))
                .setMessage(activityContext.getString(
                        R.string.authentication_unlink_confirm_dialog_message,
                        activityContext.getString(socialNetworkEnum.nameResId)))
                .setPositiveButton(R.string.authentication_unlink_confirm_dialog_button_ok)
                .setNegativeButton(R.string.cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull public static Observable<OnDialogClickEvent> popSelectOneSocialNetwork(
            @NonNull final Context activityContext)
    {
        return AlertDialogRxUtil.buildDefault(activityContext)
                .setTitle(R.string.link_select_one_social)
                .setMessage(R.string.link_select_one_social_description)
                .setPositiveButton(R.string.ok)
                .build();
    }
}
