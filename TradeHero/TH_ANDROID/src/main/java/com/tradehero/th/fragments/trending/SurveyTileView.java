package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
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
            userProfileCache.get().getOrFetch(currentUserId.toUserBaseKey(), false, userProfileCallback)
                    .execute();
        }
        else
        {
            linkWith(userProfileDTO, true);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        // intended not to canceling user profile fetch task free resource since survey tile is static content
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

    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileCallback = new DTOCache.Listener<UserBaseKey, UserProfileDTO>()
    {
        @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {

        }
    };
}
