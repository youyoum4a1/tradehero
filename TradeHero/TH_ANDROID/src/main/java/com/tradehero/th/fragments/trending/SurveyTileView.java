package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.thm.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.graphics.ForExtraTileBackground;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

public final class SurveyTileView extends ImageView
{
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<Picasso> picasso;
    @Inject @ForExtraTileBackground Transformation backgroundTransformation;

    private UserProfileDTO userProfileDTO;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    //<editor-fold desc="Constructors">
    public SurveyTileView(Context context)
    {
        super(context);
    }

    public SurveyTileView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SurveyTileView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        DaggerUtils.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        // Coz this view's content will never change
        if (userProfileDTO == null )
        {
            detachUserProfileCache();
            userProfileCacheListener = createUserProfileCacheListener();
            userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
            userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
        }
        else
        {
            linkWith(userProfileDTO, true);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        detachUserProfileCache();
        super.onDetachedFromWindow();
        // intended not to canceling user profile fetch task free resource since survey tile is static content
    }

    protected void detachUserProfileCache()
    {
        if (userProfileCacheListener != null)
        {
            userProfileCache.get().unregister(userProfileCacheListener);
        }
        userProfileCacheListener = null;
    }

    private void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;

        if (andDisplay)
        {
            picasso.get()
                    .load(userProfileDTO.activeSurveyImageURL)
                    .placeholder(R.drawable.white_rounded_background_xml)
                    .transform(backgroundTransformation)
                    .fit()
                    .into(this);
        }
    }

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>()
        {
            @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
            {
                linkWith(value, true);
            }

            @Override public void onErrorThrown(UserBaseKey key, Throwable error)
            {

            }
        };
    }
}
