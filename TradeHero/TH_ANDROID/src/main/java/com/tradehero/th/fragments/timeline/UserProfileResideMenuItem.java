package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.THColorUtils;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class UserProfileResideMenuItem extends LinearLayout
        implements DTOView<UserProfileDTO>
{
    @InjectView(R.id.user_profile_avatar) ImageView userProfileAvatar;
    @InjectView(R.id.user_profile_display_name) TextView userDisplayName;
    @InjectView(R.id.user_profile_roi) TextView userProfileRoi;
    @InjectView(R.id.user_profile_side_menu_view) BetterViewAnimator sideMenuProfileView;

    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;
    @Inject Lazy<Picasso> picasso;

    @Nullable private Subscription userProfileSubscription;
    private UserProfileDTO userProfileDTO;

    //<editor-fold desc="Constructors">
    public UserProfileResideMenuItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        fetchAndDisplayUserProfile();
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        if (userProfileSubscription != null)
        {
            userProfileSubscription.unsubscribe();
        }
        userProfileSubscription = null;
        picasso.get().cancelRequest(userProfileAvatar);
        super.onDetachedFromWindow();
    }

    private void fetchAndDisplayUserProfile()
    {
        if (!isInEditMode() && userProfileSubscription == null)
        {
            userProfileSubscription = userProfileCache.get().get(currentUserId.toUserBaseKey())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new UserProfileFetchObserver());
        }
    }

    private class UserProfileFetchObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            display(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(new THException(e));
        }
    }

    @Override public void display(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;

        displayAvatar();

        if (userProfileDTO != null)
        {
            userDisplayName.setText(userProfileDTO.displayName);

            if (userProfileDTO.portfolio.roiSinceInception == null)
            {
                userProfileDTO.portfolio.roiSinceInception = 0.0D;
            }
            THSignedPercentage
                    .builder(userProfileDTO.portfolio.roiSinceInception * 100)
                    .format(getContext().getString(R.string.user_profile_roi))
                    .withValueColor(THColorUtils.getColorResourceIdForNumber(userProfileDTO.portfolio.roiSinceInception * 100, R.color.text_primary_inverse))
                    .build()
                    .into(userProfileRoi);
        }
        else
        {
            resetView();
        }

        sideMenuProfileView.setDisplayedChildByLayoutId(R.id.user_profile_view);
    }

    private void displayAvatar()
    {
        if (userProfileAvatar != null)
        {
            if (userProfileDTO != null && userProfileDTO.picture != null)
            {
                RequestCreator requestCreator = picasso.get().load(userProfileDTO.picture);
                Drawable errorDrawable = getErrorDrawable();
                if (errorDrawable != null)
                {
                    requestCreator.error(errorDrawable);
                }
                requestCreator.transform(userPhotoTransformation)
                        .into(userProfileAvatar, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                            }

                            @Override public void onError()
                            {
                                showDefaultUserPhoto();
                            }
                        });
            }
            else
            {
                showDefaultUserPhoto();
            }
        }
    }

    @Nullable private Drawable getErrorDrawable()
    {
        Bitmap defaultUserPhotoBitmap;
        try
        {
            defaultUserPhotoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.superman_facebook);
        }
        catch (OutOfMemoryError e)
        {
            Timber.e(e, null);
            return null;
        }
        return new BitmapDrawable(getResources(), userPhotoTransformation.transform(defaultUserPhotoBitmap));
    }

    private void resetView()
    {
        showDefaultUserPhoto();
        userDisplayName.setText("");
    }

    private void showDefaultUserPhoto()
    {
        if (userProfileAvatar != null)
        {
            picasso.get().load(R.drawable.superman_facebook)
                    .transform(userPhotoTransformation)
                    .into(userProfileAvatar);
        }
    }
}
