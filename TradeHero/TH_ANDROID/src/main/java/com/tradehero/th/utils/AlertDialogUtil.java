package com.tradehero.th.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABUserInteractor;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 11/19/13 Time: 4:38 PM To change this template use
 * File | Settings | File Templates.
 */
public class AlertDialogUtil
{
    @Inject protected Lazy<Picasso> picassoLazy;
    @Inject @ForUserPhoto protected Lazy<Transformation> peopleIconTransformationLazy;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject Lazy<UserProfileCache> userProfileCacheLazy;

    AlertDialog mFollowDialog;
    protected THIABUserInteractor userInteractor;
    private MiddleCallback<UserProfileDTO> freeFollowMiddleCallback;

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

    public void showFollowDialog(Activity activity, UserProfileDTO userProfileDTO, int followType,
            UserBaseKey shownUserBaseKey)
    {
        if (followType == UserProfileDTOUtil.IS_PREMIUM_FOLLOWER)
        {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.follow_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);

        ImageView avatar = (ImageView) view.findViewById(R.id.user_profile_avatar);
        loadUserPicture(avatar, userProfileDTO);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(followType == UserProfileDTOUtil.IS_FREE_FOLLOWER
                ? R.string.free_follow_title
                : R.string.not_follow_title);

        TextView name = (TextView) view.findViewById(R.id.user_name);
        name.setText(userProfileDTO == null ? activity.getString(R.string.loading_loading)
                : userProfileDTO.displayName);

        if (followType == UserProfileDTOUtil.IS_FREE_FOLLOWER)
        {
            initFreeFollowDialog(view, shownUserBaseKey);
        }
        else if (followType == UserProfileDTOUtil.IS_NOT_FOLLOWER)
        {
            initNotFollowDialog(view, shownUserBaseKey);
        }

        if (mFollowDialog != null)
        {
            mFollowDialog.dismiss();
        }

        mFollowDialog = builder.create();
        mFollowDialog.show();
    }

    private void initNotFollowDialog(View view, final UserBaseKey shownUserBaseKey)
    {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.free_follow_layout);
        linearLayout.setVisibility(View.GONE);

        LinearLayout freeFollow = (LinearLayout) view.findViewById(R.id.free_follow);
        freeFollow.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                Timber.d("lyl free follow");
                detachFreeFollowMiddleCallback();
                freeFollowMiddleCallback = userServiceWrapperLazy.get().freeFollow(shownUserBaseKey, new FreeFollowCallback());
                //if (mFollowDialog != null)
                //{
                //    mFollowDialog.dismiss();
                //}
            }
        });

        LinearLayout premiumFollow = (LinearLayout) view.findViewById(R.id.premium_follow);
        premiumFollow.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                Timber.d("lyl premium follow");
                userInteractor = new PushableTimelineTHIABUserInteractor();
                userInteractor.followHero(shownUserBaseKey);
                if (mFollowDialog != null)
                {
                    mFollowDialog.dismiss();
                }
            }
        });
    }

    private void detachFreeFollowMiddleCallback()
    {
        if (freeFollowMiddleCallback != null)
        {
            freeFollowMiddleCallback.setPrimaryCallback(null);
        }
        freeFollowMiddleCallback = null;
    }

    private class FreeFollowCallback implements Callback<UserProfileDTO>
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            // do nothing for now
            //Timber.d("lyl %s", userProfileDTO.toString());
            if (mFollowDialog != null)
            {
                mFollowDialog.dismiss();
            }
            userProfileCacheLazy.get().invalidate(userProfileDTO.getBaseKey());

        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            //Timber.d("fail");
            if (mFollowDialog != null)
            {
                mFollowDialog.dismiss();
            }
        }
    }

    private void initFreeFollowDialog(View view, final UserBaseKey shownUserBaseKey)
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
                    //Timber.d("free");
                    if (mFollowDialog != null)
                    {
                        mFollowDialog.dismiss();
                    }
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
                    //Timber.d("premium");
                    userInteractor = new PushableTimelineTHIABUserInteractor();
                    userInteractor.followHero(shownUserBaseKey);
                    if (mFollowDialog != null)
                    {
                        mFollowDialog.dismiss();
                    }
                }
            });
        }
    }

    private void loadUserPicture(ImageView imageView, UserProfileDTO userProfileDTO)
    {
        if (imageView != null)
        {
            loadDefaultPicture(imageView);
            if (userProfileDTO != null && userProfileDTO.picture != null)
            {
                picassoLazy.get().load(userProfileDTO.picture)
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

    public class PushableTimelineTHIABUserInteractor extends THIABUserInteractor
    {
        public final String TAG = PushableTimelineTHIABUserInteractor.class.getName();

        public PushableTimelineTHIABUserInteractor()
        {
            super();
        }

        @Override protected void handleShowProductDetailsMilestoneComplete()
        {
            super.handleShowProductDetailsMilestoneComplete();
            //displayFollowButton();
        }

        @Override protected void handlePurchaseReportSuccess(THIABPurchase reportedPurchase,
                UserProfileDTO updatedUserProfile)
        {
            super.handlePurchaseReportSuccess(reportedPurchase, updatedUserProfile);
            //displayFollowButton();
        }

        @Override protected void createFollowCallback()
        {
            this.followCallback = new UserInteractorFollowHeroCallback(heroListCache.get(),
                    userProfileCache.get())
            {
                @Override public void success(UserProfileDTO userProfileDTO, Response response)
                {
                    super.success(userProfileDTO, response);
                    //displayFollowButton();
                }
            };
        }
    }
}
