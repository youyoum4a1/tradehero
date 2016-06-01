package com.ayondo.academy.fragments.trending;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.ayondo.academy.R;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.models.graphics.ForExtraTileBackground;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import com.ayondo.academy.rx.TimberOnErrorAction1;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public final class SurveyTileView extends ImageView
{
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<Picasso> picasso;
    @Inject @ForExtraTileBackground Transformation backgroundTransformation;

    private UserProfileDTO userProfileDTO;
    private Subscription userProfileCacheSubscription;

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
        HierarchyInjector.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        // Coz this view's content will never change
        if (!isInEditMode() && userProfileDTO == null)
        {
            detachUserProfileCache();
            userProfileCacheSubscription = userProfileCache.get().getOne(currentUserId.toUserBaseKey())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<Pair<UserBaseKey, UserProfileDTO>>()
                            {
                                @Override public void call(Pair<UserBaseKey, UserProfileDTO> pair)
                                {
                                    linkWith(pair.second);
                                }
                            },
                            new TimberOnErrorAction1("Failed to load user profile for survey tile"));
        }
        else
        {
            linkWith(userProfileDTO);
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
        if (userProfileCacheSubscription != null)
        {
            userProfileCacheSubscription.unsubscribe();
        }
        userProfileCacheSubscription = null;
    }

    private void linkWith(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
        picasso.get()
                .load(userProfileDTO.activeSurveyImageURL)
                .placeholder(R.drawable.white_rounded_background_xml)
                //.transform(backgroundTransformation)
                .fit()
                .into(this);
    }
}
