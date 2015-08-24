package com.tradehero.th.fragments.social.hero;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import rx.Observable;

public class HeroAlertDialogRxUtil extends AlertDialogRxUtil
{
    @NonNull public static Observable<OnDialogClickEvent> popAlertFollowHero(
            @NonNull Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.manage_heroes_alert_follow_title)
                .setMessage(R.string.manage_heroes_alert_follow_message)
                .setPositiveButton(R.string.manage_heroes_alert_follow_ok)
                .setNegativeButton(R.string.manage_heroes_alert_follow_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull public static Observable<OnDialogClickEvent> popAlertUnFollowHero(
            @NonNull Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.manage_heroes_alert_unfollow_title)
                .setMessage(R.string.manage_heroes_alert_unfollow_message)
                .setPositiveButton(R.string.manage_heroes_alert_unfollow_ok)
                .setNegativeButton(R.string.manage_heroes_alert_unfollow_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }
}
