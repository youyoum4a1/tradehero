package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.ForExtraTileBackground;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

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
        if (userProfileDTO == null )
        {
            detachUserProfileCache();
            userProfileCacheSubscription = userProfileCache.get().get(currentUserId.toUserBaseKey())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createUserProfileCacheObserver());
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
        if (userProfileCacheSubscription != null)
        {
            userProfileCacheSubscription.unsubscribe();
        }
        userProfileCacheSubscription = null;
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

    private Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileCacheObserver()
    {
        return new Observer<Pair<UserBaseKey,UserProfileDTO>>()
        {
            @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
            {
                linkWith(pair.second, true);
            }

            @Override public void onCompleted()
            {
            }

            @Override public void onError(Throwable e)
            {
            }
        };
    }
}
