package com.tradehero.th.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
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
}
