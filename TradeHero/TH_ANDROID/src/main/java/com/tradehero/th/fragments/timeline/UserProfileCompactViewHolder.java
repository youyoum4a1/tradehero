package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class UserProfileCompactViewHolder
{
    @InjectView(R.id.user_profile_avatar) @Optional public ImageView avatar;
    @InjectView(R.id.user_profile_roi) @Optional public TextView roiSinceInception;
    @InjectView(R.id.user_profile_followers_count) @Optional public TextView followersCount;
    @InjectView(R.id.user_profile_heroes_count) @Optional public TextView heroesCount;
    @InjectView(R.id.user_profile_display_name) @Optional public TextView displayName;

    @Inject protected Context context;
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject protected Picasso picasso;
    protected UserProfileDTO userProfileDTO;
    @NonNull final PublishSubject<ButtonType> buttonClickedSubject;

    //<editor-fold desc="Constructors>
    public UserProfileCompactViewHolder(@NonNull Context context)
    {
        super();
        buttonClickedSubject = PublishSubject.create();
        HierarchyInjector.inject(context, this);
    }
    //</editor-fold>

    @NonNull public Observable<ButtonType> getButtonClickedObservable()
    {
        return buttonClickedSubject.asObservable();
    }

    public void display(@NonNull UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;

        // Avatar
        if (avatar != null)
        {
            picasso.load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformation)
                    .into(avatar);
            if (userProfileDTO.picture != null)
            {
                picasso.load(userProfileDTO.picture)
                        .transform(peopleIconTransformation)
                        .placeholder(avatar.getDrawable())
                        .into(avatar);
            }
        }

        // ROI Since Inception
        if (roiSinceInception != null)
        {
            if (userProfileDTO.portfolio != null)
            {
                double roi = userProfileDTO.portfolio.roiSinceInception != null ? userProfileDTO.portfolio.roiSinceInception : 0;
                THSignedPercentage
                        .builder(roi * 100)
                        .withSign()
                        .withDefaultColor()
                        .defaultColorForBackground()
                        .signTypePlusMinusAlways()
                        .build()
                        .into(roiSinceInception);
            }
            else
            {
                roiSinceInception.setText(R.string.na);
            }
        }

        // Followers Count
        if (followersCount != null)
        {
            followersCount.setText(THSignedNumber.builder(userProfileDTO.allFollowerCount).build().toString());
        }

        // Heroes Count
        if (heroesCount != null)
        {
            heroesCount.setText(THSignedNumber.builder(
                    userProfileDTO.heroIds == null
                            ? userProfileDTO.allHeroCount
                            : userProfileDTO.heroIds.size())
                    .build().toString());
        }

        // Display Name
        if (displayName != null)
        {
            displayName.setText(userProfileDTO.displayName);
        }
    }

    @SuppressWarnings("unused")
    @OnClick({R.id.user_profile_heroes_count, R.id.user_profile_heroes_count_wrapper}) @Optional
    protected void notifyHeroesClicked(View view)
    {
        buttonClickedSubject.onNext(ButtonType.HEROES);
    }

    @SuppressWarnings("unused")
    @OnClick({R.id.user_profile_followers_count, R.id.user_profile_followers_count_wrapper}) @Optional
    protected void notifyFollowersClicked(View view)
    {
        buttonClickedSubject.onNext(ButtonType.FOLLOWERS);
    }

    protected void notifyAchievementsClicked()
    {
        buttonClickedSubject.onNext(ButtonType.ACHIEVEMENTS);
    }

    public enum ButtonType
    {
        HEROES,
        FOLLOWERS,
        ACHIEVEMENTS,
    }
}
