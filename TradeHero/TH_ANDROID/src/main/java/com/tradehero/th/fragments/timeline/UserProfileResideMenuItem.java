package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
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

/** Created with IntelliJ IDEA. User: tho Date: 10/17/13 Time: 12:51 PM Copyright (c) TradeHero */
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
    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileFetchTask;

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
        detachUserProfileFetchTask();
        userProfileFetchTask = userProfileCache.get().getOrFetch(currentUserId.toUserBaseKey(), false, userProfileListener);
        userProfileFetchTask.execute();
    }

    @Override protected void onAttachedToWindow()
    {
        fetchAndDisplayUserProfile();
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachUserProfileFetchTask();
        super.onDetachedFromWindow();
    }

    private void detachUserProfileFetchTask()
    {
        if (userProfileFetchTask != null)
        {
            userProfileFetchTask.setListener(null);
        }
        userProfileFetchTask = null;
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
            if (userProfileDTO != null)
            {
                picasso.get().load(userProfileDTO.picture)
                        .transform(userPhotoTransformation)
                        .into(userProfileAvatar);

                userDisplayName.setText(userProfileDTO.displayName);

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

    private void resetView()
    {
        userProfileAvatar.setImageDrawable(null);
        userDisplayName.setText("");
    }

    private class UserProfileFetchListener implements DTOCache.Listener<UserBaseKey,UserProfileDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
        {
            display(value);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }
}
