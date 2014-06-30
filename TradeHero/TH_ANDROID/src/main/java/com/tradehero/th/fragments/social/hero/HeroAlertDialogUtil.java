package com.tradehero.th.fragments.social.hero;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.tradehero.thm.R;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;

public class HeroAlertDialogUtil extends AlertDialogUtil
{
    @Inject public HeroAlertDialogUtil()
    {
        super();
    }

    public AlertDialog popAlertFollowHero(Context context, DialogInterface.OnClickListener okClickListener)
    {
        return popWithOkCancelButton(
                context,
                R.string.manage_heroes_alert_follow_title,
                R.string.manage_heroes_alert_follow_message,
                R.string.manage_heroes_alert_follow_ok,
                R.string.manage_heroes_alert_follow_cancel,
                okClickListener);
    }

    public AlertDialog popAlertUnfollowHero(Context context, DialogInterface.OnClickListener okClickListener)
    {
        return popWithOkCancelButton(
                context,
                R.string.manage_heroes_alert_unfollow_title,
                R.string.manage_heroes_alert_unfollow_message,
                R.string.manage_heroes_alert_unfollow_ok,
                R.string.manage_heroes_alert_unfollow_cancel,
                okClickListener);
    }

    public AlertDialog popAlertNoMoreMessageFollow(Context context, DialogInterface.OnClickListener okClickListener, String heroName)
    {
        return popWithOkCancelButton(
                context,
                context.getString(R.string.private_message_expired_free_message_title),
                context.getString(R.string.private_message_expired_free_message_description, heroName),
                R.string.private_message_expired_free_message_ok,
                R.string.private_message_expired_free_message_cancel,
                okClickListener);
    }
}
