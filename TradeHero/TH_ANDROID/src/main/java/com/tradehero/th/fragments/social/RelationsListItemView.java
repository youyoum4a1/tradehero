package com.tradehero.th.fragments.social;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.social.FollowRequest;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class RelationsListItemView extends RelativeLayout
        implements DTOView<AllowableRecipientDTO>
{
    @Bind(R.id.user_name) TextView name;
    @Bind(R.id.user_profile_avatar) ImageView avatar;
    @Bind(R.id.country_logo) ImageView countryLogo;
    @Bind(R.id.user_type) TextView userType;
    @Bind(R.id.upgrade_now) TextView upgradeNow;
    private AllowableRecipientDTO allowableRecipientDTO;
    @NonNull private PublishSubject<FollowRequest> followRequestedBehavior;

    @Inject protected Lazy<Picasso> picassoLazy;
    @Inject @ForUserPhoto protected Lazy<Transformation> peopleIconTransformationLazy;
    @Inject DashboardNavigator navigator;

    //<editor-fold desc="Constructors">
    public RelationsListItemView(Context context)
    {
        super(context);
        this.followRequestedBehavior = PublishSubject.create();
    }

    public RelationsListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.followRequestedBehavior = PublishSubject.create();
    }

    public RelationsListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.followRequestedBehavior = PublishSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        ButterKnife.bind(this);
        loadDefaultPicture();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @NonNull public Observable<FollowRequest> getFollowRequestObservable()
    {
        return followRequestedBehavior.asObservable();
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.user_profile_avatar)
    protected void handleOpenProfileButtonClicked(View view)
    {
        int userId = allowableRecipientDTO.user.id;

        Bundle bundle = new Bundle();
        PushableTimelineFragment.putUserBaseKey(bundle, new UserBaseKey(userId));
        navigator.pushFragment(PushableTimelineFragment.class, bundle);
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.upgrade_now)
    protected void handleUpgradeNowButtonClicked(View view)
    {
        if (allowableRecipientDTO == null || allowableRecipientDTO.user == null)
        {
            THToast.show(R.string.error_incomplete_info_title);
        }
        else
        {
            followRequestedBehavior.onNext(new FollowRequest(allowableRecipientDTO.user.getBaseKey()));
        }
    }

    @Override public void display(AllowableRecipientDTO allowableRecipientDTO)
    {
        this.allowableRecipientDTO = allowableRecipientDTO;
        displayPicture();
        displayTitle();
        displayUpgradeNow();
        displayUserType();
        displayCountryLogo();
    }

    public void display()
    {
        displayPicture();
        displayTitle();
        displayUpgradeNow();
        displayUserType();
        displayCountryLogo();
    }

    public void displayPicture()
    {
        if (avatar != null && !isInEditMode())
        {
            loadDefaultPicture();
            if (allowableRecipientDTO != null && allowableRecipientDTO.user.picture != null)
            {
                picassoLazy.get().load(allowableRecipientDTO.user.picture)
                        .transform(peopleIconTransformationLazy.get())
                        .placeholder(avatar.getDrawable())
                        .into(avatar, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                            }

                            @Override public void onError()
                            {
                                //loadDefaultPicture();
                            }
                        });
            }
        }
    }

    protected void loadDefaultPicture()
    {
        if (avatar != null && !isInEditMode())
        {
            picassoLazy.get().load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformationLazy.get())
                    .into(avatar);
        }
    }

    public void displayTitle()
    {
        if (name != null)
        {
            if (allowableRecipientDTO != null && allowableRecipientDTO.user != null)
            {
                name.setText(allowableRecipientDTO.user.displayName);
            }
            else
            {
                name.setText(R.string.na);
            }
        }
    }

    public void displayUpgradeNow()
    {
        if (upgradeNow != null)
        {
            if (allowableRecipientDTO == null || allowableRecipientDTO.relationship == null)
            {
                upgradeNow.setVisibility(INVISIBLE);
            }
            else if (allowableRecipientDTO.relationship.isFollower)
            {
                upgradeNow.setVisibility(INVISIBLE);
            }
            else if (allowableRecipientDTO.relationship.isHero)
            {
                upgradeNow.setVisibility(
                        allowableRecipientDTO.relationship.freeFollow ? VISIBLE : INVISIBLE);
            }
            else
            {
                upgradeNow.setVisibility(INVISIBLE);
            }
        }
    }

    public void displayUserType()
    {
        if (userType != null)
        {
            userType.setText(getUserTypeText());
        }
    }

    protected String getUserTypeText()
    {
        if (allowableRecipientDTO == null || allowableRecipientDTO.relationship == null)
        {
            return getContext().getString(R.string.na);
        }

        int userTypeTextResId;
        if (allowableRecipientDTO.relationship.isFollower)
        {
            userTypeTextResId = R.string.relation_follower;
        }
        else if (allowableRecipientDTO.relationship.isHero)
        {
            userTypeTextResId = R.string.relation_following;
        }
        else
        {
            userTypeTextResId = R.string.relation_friend;
        }

        int subtitleresId = 0;

        if (allowableRecipientDTO.relationship.freeFollow)
        {
            subtitleresId = R.string.not_follow_subtitle2;
        }
        else if (allowableRecipientDTO.relationship.isHero)
        {
            subtitleresId = R.string.not_follow_premium_subtitle2;
        }

        if (subtitleresId > 0)
        {
            String userTypeText = getContext().getString(userTypeTextResId);
            String subTitle = getContext().getString(subtitleresId);
            return getContext().getString(
                    R.string.follower_item_with_subtitle,
                    userTypeText,
                    subTitle);
        }
        return getContext().getString(userTypeTextResId);
    }

    public void displayCountryLogo()
    {
        if (countryLogo != null &&
                allowableRecipientDTO != null &&
                allowableRecipientDTO.user != null &&
                allowableRecipientDTO.user.countryCode != null)
        {
            int imageResId = Country.getCountryLogo(R.drawable.default_image, allowableRecipientDTO.user.countryCode);
            countryLogo.setImageResource(imageResId);
        }
        else if (countryLogo != null)
        {
            countryLogo.setImageResource(R.drawable.default_image);
        }
    }
}
