package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.THSignedNumber;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class UserProfileResideMenuItem extends LinearLayout
        implements DTOView<UserProfileDTO>
{
    @InjectView(R.id.user_profile_avatar) ImageView userProfileAvatar;
    @InjectView(R.id.user_profile_display_name) TextView userDisplayName;
    @InjectView(R.id.user_profile_roi) TextView userProfileRoi;
    @InjectView(R.id.user_profile_side_menu_view) BetterViewAnimator sideMenuProfileView;

    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;
    @Inject Lazy<Picasso> picasso;

    private UserProfileDTO userProfileDTO;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileListener;

    //<editor-fold desc="Constructors">
    public UserProfileResideMenuItem(Context context)
    {
        super(context);
    }

    public UserProfileResideMenuItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);

        userProfileListener = new UserProfileFetchListener();
    }

    private void fetchAndDisplayUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    @Override protected void onAttachedToWindow()
    {
        userProfileListener = new UserProfileFetchListener();
        fetchAndDisplayUserProfile();
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachUserProfileCache();
        userProfileListener = null;
        super.onDetachedFromWindow();
    }

    private void detachUserProfileCache()
    {
        if (userProfileListener != null)
        {
            userProfileCache.get().unregister(userProfileListener);
        }
    }

    @Override public void display(UserProfileDTO dto)
    {
        linkWith(dto, true);

        sideMenuProfileView.setDisplayedChildByLayoutId(R.id.user_profile_view);
    }

    private void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;

        if (andDisplay)
        {
            displayAvatar();

            if (userProfileDTO != null)
            {
                userDisplayName.setText(userProfileDTO.displayName);

                if (userProfileDTO.portfolio.roiSinceInception == null)
                {
                    userProfileDTO.portfolio.roiSinceInception = 0.0D;
                }
                THSignedNumber thRoiSinceInception = new THSignedNumber(
                        THSignedNumber.TYPE_PERCENTAGE,
                        userProfileDTO.portfolio.roiSinceInception * 100);

                userProfileRoi.setText(thRoiSinceInception.toString());
                userProfileRoi.setTextColor(getResources().getColor(thRoiSinceInception.getColor()));
            }
            else
            {
                resetView();
            }
        }
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

    private class UserProfileFetchListener implements DTOCacheNew.Listener<UserBaseKey,UserProfileDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
        {
            display(value);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }
}
