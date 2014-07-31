package com.tradehero.th.fragments.social.hero;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.fragments.social.FollowDialogView;
import com.tradehero.th.models.social.FollowDialogCombo;
import com.tradehero.th.models.social.OnFollowRequestedListener;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeroAlertDialogUtil extends AlertDialogUtil
{
    //<editor-fold desc="Constructors">
    @Inject public HeroAlertDialogUtil()
    {
        super();
    }
    //</editor-fold>

    public AlertDialog popAlertFollowHero(
            @NotNull Context context,
            @Nullable DialogInterface.OnClickListener okClickListener)
    {
        return popWithOkCancelButton(
                context,
                R.string.manage_heroes_alert_follow_title,
                R.string.manage_heroes_alert_follow_message,
                R.string.manage_heroes_alert_follow_ok,
                R.string.manage_heroes_alert_follow_cancel,
                okClickListener);
    }

    public AlertDialog popAlertUnfollowHero(
            @NotNull Context context,
            @Nullable DialogInterface.OnClickListener okClickListener)
    {
        return popWithOkCancelButton(
                context,
                R.string.manage_heroes_alert_unfollow_title,
                R.string.manage_heroes_alert_unfollow_message,
                R.string.manage_heroes_alert_unfollow_ok,
                R.string.manage_heroes_alert_unfollow_cancel,
                okClickListener);
    }

    public AlertDialog popAlertNoMoreMessageFollow(
            @NotNull Context context,
            @Nullable DialogInterface.OnClickListener okClickListener,
            @Nullable String heroName)
    {
        return popWithOkCancelButton(
                context,
                context.getString(R.string.private_message_expired_free_message_title),
                context.getString(R.string.private_message_expired_free_message_description, heroName),
                R.string.private_message_expired_free_message_ok,
                R.string.private_message_expired_free_message_cancel,
                okClickListener);
    }

    @Nullable public FollowDialogCombo showFollowDialog(
            @NotNull final Context context,
            @Nullable UserBaseDTO userBaseDTO,
            final int followType,
            @NotNull final OnFollowRequestedListener followRequestedListener)
    {
        if (followType == UserProfileDTOUtil.IS_PREMIUM_FOLLOWER)
        {
            return null;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        FollowDialogView followDialogView = (FollowDialogView) inflater.inflate(R.layout.follow_dialog, null);
        followDialogView.setFollowType(followType);
        followDialogView.display(userBaseDTO);

        final AlertDialog mFollowDialog = new AlertDialog.Builder(context)
                .setView(followDialogView)
                .setCancelable(true)
                .create();
        mFollowDialog.setCancelable(true);
        mFollowDialog.setCanceledOnTouchOutside(true);
        mFollowDialog.show();

        followDialogView.setFollowRequestedListener(new MiddleFollowRequestedListener(
                followRequestedListener,
                followType,
                mFollowDialog
        ));

        return new FollowDialogCombo(mFollowDialog, followDialogView);
    }

    private static class MiddleFollowRequestedListener implements OnFollowRequestedListener
    {
        @NotNull private final OnFollowRequestedListener followRequestedListener;
        private final int followType;
        @NotNull private final AlertDialog mFollowDialog;

        //<editor-fold desc="Constructors">
        private MiddleFollowRequestedListener(
                @NotNull OnFollowRequestedListener followRequestedListener,
                int followType,
                @NotNull AlertDialog mFollowDialog)
        {
            this.followRequestedListener = followRequestedListener;
            this.followType = followType;
            this.mFollowDialog = mFollowDialog;
        }
        //</editor-fold>

        @Override public void freeFollowRequested(@NotNull UserBaseKey heroId)
        {
            onFinish();
            if (followType != UserProfileDTOUtil.IS_FREE_FOLLOWER)
            {
                followRequestedListener.freeFollowRequested(heroId);
            }
        }

        @Override public void premiumFollowRequested(@NotNull UserBaseKey heroId)
        {
            onFinish();
            followRequestedListener.premiumFollowRequested(heroId);
        }

        private void onFinish()
        {
            mFollowDialog.dismiss();
        }
    }
}
