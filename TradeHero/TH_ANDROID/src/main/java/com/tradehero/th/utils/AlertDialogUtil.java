package com.tradehero.th.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.social.FollowRequestedListener;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 11/19/13 Time: 4:38 PM To change this template use
 * File | Settings | File Templates.
 */
public class AlertDialogUtil
{
    @Inject protected Lazy<Picasso> picassoLazy;
    @Inject @ForUserPhoto protected Lazy<Transformation> peopleIconTransformationLazy;

    AlertDialog mFollowDialog;
    ProgressDialog mProgressDialog;

    @Inject public AlertDialogUtil()
    {
        super();
    }

    public DialogInterface.OnClickListener createDefaultCancelListener()
    {
        return new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        };
    }

    public AlertDialog popWithNegativeButton(final Context context, int titleResId,
            int descriptionResId, int cancelResId)
    {
        return popWithNegativeButton(context, titleResId, descriptionResId, cancelResId,
                createDefaultCancelListener());
    }

    public AlertDialog popWithNegativeButton(final Context context, int titleResId,
            int descriptionResId, int cancelResId,
            DialogInterface.OnClickListener cancelListener)
    {
        return popWithNegativeButton(context,
                context.getString(titleResId),
                context.getString(descriptionResId),
                context.getString(cancelResId),
                cancelListener);
    }

    public AlertDialog popWithNegativeButton(final Context context, String titleRes,
            String descriptionRes, String cancelRes)
    {
        return popWithNegativeButton(context, titleRes, descriptionRes, cancelRes,
                createDefaultCancelListener());
    }

    public AlertDialog popWithNegativeButton(final Context context, String titleRes,
            String descriptionRes, String cancelRes,
            DialogInterface.OnClickListener cancelListener)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(titleRes)
                .setMessage(descriptionRes)
                .setIcon(R.drawable.th_app_logo)
                .setCancelable(true)
                .setNegativeButton(cancelRes, cancelListener);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
        return alertDialog;
    }

    public AlertDialog popWithOkCancelButton(final Context context, int titleResId,
            int descriptionResId, int okResId, int cancelResId,
            final DialogInterface.OnClickListener okClickListener)
    {
        return popWithOkCancelButton(context, titleResId, descriptionResId, okResId, cancelResId,
                okClickListener, createDefaultCancelListener());
    }

    public AlertDialog popWithOkCancelButton(final Context context, int titleResId,
            int descriptionResId, int okResId, int cancelResId,
            final DialogInterface.OnClickListener okClickListener,
            final DialogInterface.OnClickListener cancelClickListener)
    {
        return popWithOkCancelButton(context,
                context.getString(titleResId),
                context.getString(descriptionResId),
                okResId,
                cancelResId,
                okClickListener,
                cancelClickListener);
    }

    public AlertDialog popWithOkCancelButton(final Context context, String title,
            String description, int okResId, int cancelResId,
            final DialogInterface.OnClickListener okClickListener)
    {
        return popWithOkCancelButton(context, title, description, okResId, cancelResId,
                okClickListener, createDefaultCancelListener());
    }

    public AlertDialog popWithOkCancelButton(final Context context, String title,
            String description, int okResId, int cancelResId,
            final DialogInterface.OnClickListener okClickListener,
            final DialogInterface.OnClickListener cancelClickListener)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(title)
                .setMessage(description)
                .setIcon(R.drawable.th_app_logo)
                .setCancelable(true)
                .setNegativeButton(cancelResId, cancelClickListener)
                .setPositiveButton(okResId, okClickListener);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
        return alertDialog;
    }

    public Dialog popTutorialContent(final Context context, int layoutResourceId)
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.tutorial_master_layout);
        ViewGroup tutorialContentView = (ViewGroup) dialog.findViewById(R.id.tutorial_content);
        LayoutInflater.from(context).inflate(layoutResourceId, tutorialContentView, true);
        ((View) tutorialContentView.getParent()).setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.95f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow()
                .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
        return dialog;
    }

    public AlertDialog popMarketClosed(final Context context, SecurityId securityId)
    {
        if (securityId == null)
        {
            return popWithNegativeButton(context,
                    R.string.alert_dialog_market_close_title,
                    R.string.alert_dialog_market_close_message_basic,
                    R.string.alert_dialog_market_close_cancel);
        }
        else
        {
            return popWithNegativeButton(context,
                    context.getString(R.string.alert_dialog_market_close_title),
                    String.format(context.getString(R.string.alert_dialog_market_close_message),
                            securityId.exchange, securityId.securitySymbol),
                    context.getString(R.string.alert_dialog_market_close_cancel));
        }
    }

    public void showFollowDialog(Context context, UserBaseDTO userBaseDTO, int followType,
            FollowRequestedListener followRequestedListener)
    {
        if (followType == UserProfileDTOUtil.IS_PREMIUM_FOLLOWER)
        {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.follow_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);

        ImageView avatar = (ImageView) view.findViewById(R.id.user_profile_avatar);
        loadUserPicture(avatar, userBaseDTO);

        TextView name = (TextView) view.findViewById(R.id.user_name);
        name.setText(userBaseDTO == null ? context.getString(R.string.loading_loading)
                : userBaseDTO.displayName);

        TextView title = (TextView) view.findViewById(R.id.title);
        switch (followType)
        {
            case UserProfileDTOUtil.IS_NOT_FOLLOWER_WANT_MSG:
                title.setText(context.getString(R.string.not_follow_msg_title, name.getText()));
                name.setVisibility(View.GONE);
                break;
            case UserProfileDTOUtil.IS_NOT_FOLLOWER:
                title.setText(R.string.not_follow_title);
                break;
            case UserProfileDTOUtil.IS_FREE_FOLLOWER:
                title.setText(R.string.free_follow_title);
                break;
        }

        if (followType == UserProfileDTOUtil.IS_FREE_FOLLOWER)
        {
            initFreeFollowDialog(view, followRequestedListener);
        }
        else if (followType == UserProfileDTOUtil.IS_NOT_FOLLOWER
                || followType == UserProfileDTOUtil.IS_NOT_FOLLOWER_WANT_MSG)
        {
            initNotFollowDialog(view, followRequestedListener);
        }

        dismissFollowDialog();

        mFollowDialog = builder.create();
        mFollowDialog.show();
    }

    private void initNotFollowDialog(View view,
            final FollowRequestedListener followRequestedListener)
    {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.free_follow_layout);
        linearLayout.setVisibility(View.GONE);

        LinearLayout freeFollow = (LinearLayout) view.findViewById(R.id.free_follow);
        freeFollow.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                dismissFollowDialog();
                followRequestedListener.freeFollowRequested();
            }
        });

        LinearLayout premiumFollow = (LinearLayout) view.findViewById(R.id.premium_follow);
        premiumFollow.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                dismissFollowDialog();
                followRequestedListener.followRequested();
            }
        });
    }

    private void dismissFollowDialog()
    {
        if (mFollowDialog != null)
        {
            mFollowDialog.dismiss();
        }
    }

    private void initFreeFollowDialog(View view,
            final FollowRequestedListener followRequestedListener)
    {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.not_follow_layout);
        linearLayout.setVisibility(View.GONE);

        Button leftButton = (Button) view.findViewById(R.id.btn_free);
        if (leftButton != null)
        {
            leftButton.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    dismissFollowDialog();
                }
            });
        }
        Button rightButton = (Button) view.findViewById(R.id.btn_premium);
        if (rightButton != null)
        {
            rightButton.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    dismissFollowDialog();
                    followRequestedListener.followRequested();
                }
            });
        }
    }

    private void loadUserPicture(ImageView imageView, UserBaseDTO userBaseDTO)
    {
        if (imageView != null)
        {
            loadDefaultPicture(imageView);
            if (userBaseDTO != null && userBaseDTO.picture != null)
            {
                picassoLazy.get().load(userBaseDTO.picture)
                        .transform(peopleIconTransformationLazy.get())
                        .placeholder(imageView.getDrawable())
                        .into(imageView);
            }
        }
    }

    protected void loadDefaultPicture(ImageView imageView)
    {
        if (imageView != null)
        {
            picassoLazy.get().load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformationLazy.get())
                    .into(imageView);
        }
    }

    public void showProgressDialog(Context context)
    {
        if (mProgressDialog != null)
        {
            mProgressDialog.dismiss();
        }
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
    }

    public void dismissProgressDialog()
    {
        if (mProgressDialog != null)
        {
            mProgressDialog.dismiss();
        }
    }

    public void showDefaultDialog(Context context, int resId)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(R.string.app_name)
                .setMessage(context.getString(resId))
                .setIcon(R.drawable.th_app_logo)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
