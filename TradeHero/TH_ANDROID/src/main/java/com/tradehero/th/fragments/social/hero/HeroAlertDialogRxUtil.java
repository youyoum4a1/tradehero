package com.tradehero.th.fragments.social.hero;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.fragments.social.FollowDialogView;
import com.tradehero.th.models.social.FollowRequest;
import com.tradehero.th.rx.dialog.AlertDialogOnSubscribe;
import com.tradehero.th.utils.AlertDialogRxUtil;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class HeroAlertDialogRxUtil extends AlertDialogRxUtil
{
    //<editor-fold desc="Constructors">
    @Inject public HeroAlertDialogRxUtil()
    {
        super();
    }
    //</editor-fold>

    @NonNull public Observable<Pair<DialogInterface, Integer>> popAlertFollowHero(
            @NonNull Context activityContext)
    {
        return Observable.create(
                AlertDialogOnSubscribe.builder(
                        createDefaultDialogBuilder(activityContext)
                                .setTitle(R.string.manage_heroes_alert_follow_title)
                                .setMessage(R.string.manage_heroes_alert_follow_message))
                        .setPositiveButton(R.string.manage_heroes_alert_follow_ok)
                        .setNegativeButton(R.string.manage_heroes_alert_follow_cancel)
                        .setCanceledOnTouchOutside(true)
                        .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popAlertUnFollowHero(
            @NonNull Context activityContext)
    {
        return Observable.create(
                AlertDialogOnSubscribe.builder(
                        createDefaultDialogBuilder(activityContext)
                                .setTitle(R.string.manage_heroes_alert_unfollow_title)
                                .setMessage(R.string.manage_heroes_alert_unfollow_message))
                        .setPositiveButton(R.string.manage_heroes_alert_unfollow_ok)
                        .setNegativeButton(R.string.manage_heroes_alert_unfollow_cancel)
                        .setCanceledOnTouchOutside(true)
                        .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popAlertNoMoreMessageFollow(
            @NonNull Context activityContext,
            @Nullable String heroName)
    {
        return Observable.create(
                AlertDialogOnSubscribe.builder(
                        createDefaultDialogBuilder(activityContext)
                                .setTitle(R.string.private_message_expired_free_message_title)
                                .setMessage(activityContext.getString(
                                        R.string.private_message_expired_free_message_description,
                                        heroName)))
                        .setPositiveButton(R.string.private_message_expired_free_message_ok)
                        .setNegativeButton(R.string.private_message_expired_free_message_cancel)
                        .setCanceledOnTouchOutside(true)
                        .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull public Observable<FollowRequest> showFollowDialog(
            @NonNull final Context context,
            @Nullable UserBaseDTO userBaseDTO,
            final int followType)
    {
        if (followType == UserProfileDTOUtil.IS_PREMIUM_FOLLOWER)
        {
            return Observable.empty();
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        FollowDialogView followDialogView = (FollowDialogView) inflater.inflate(R.layout.follow_dialog, null);
        followDialogView.display(userBaseDTO);
        followDialogView.setFollowType(followType);

        final AlertDialog mFollowDialog = new AlertDialog.Builder(context)
                .setView(followDialogView)
                .setCancelable(true)
                .create();
        mFollowDialog.setCancelable(true);
        mFollowDialog.setCanceledOnTouchOutside(true);
        mFollowDialog.show();

        return followDialogView.getRequestObservable()
                .finallyDo(mFollowDialog::dismiss);
    }
}
