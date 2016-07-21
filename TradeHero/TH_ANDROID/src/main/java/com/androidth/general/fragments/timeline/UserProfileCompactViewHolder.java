package com.androidth.general.fragments.timeline;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.graphics.ForUserPhoto;
import com.androidth.general.models.number.THSignedNumber;
import com.androidth.general.models.number.THSignedPercentage;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class UserProfileCompactViewHolder
{
    @BindView(R.id.user_profile_avatar) @Nullable public ImageView avatar;
    @BindView(R.id.user_profile_roi) @Nullable public TextView roiSinceInception;
    @BindView(R.id.user_profile_followers_count) @Nullable public TextView followersCount;
    @BindView(R.id.user_profile_heroes_count) @Nullable public TextView heroesCount;
    @BindView(R.id.user_profile_display_name) @Nullable public TextView displayName;

    @Inject protected Context context;
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject protected Picasso picasso;
    protected UserProfileDTO userProfileDTO;
    @NonNull final PublishSubject<ButtonType> buttonClickedSubject;
    private ClipboardManager clipboardManager;

    //<editor-fold desc="Constructors>
    public UserProfileCompactViewHolder(@NonNull Context context)
    {
        super();
        buttonClickedSubject = PublishSubject.create();
        HierarchyInjector.inject(context, this);
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
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
            followersCount.setText(THSignedNumber.builder(userProfileDTO.allFollowerCount).with000Suffix().useShortSuffix().relevantDigitCount(1).build().toString());
        }

        // Heroes Count
        if (heroesCount != null)
        {
            heroesCount.setText(THSignedNumber.builder(
                    userProfileDTO.heroIds == null
                            ? userProfileDTO.allHeroCount
                            : userProfileDTO.heroIds.size())
                    .with000Suffix()
                    .useShortSuffix()
                    .relevantDigitCount(1)
                    .build().toString());
        }

        // Display Name
        if (displayName != null)
        {
            displayName.setText(userProfileDTO.displayName);
        }
    }

    @SuppressWarnings("unused")
    @OnClick({R.id.user_profile_heroes_count, R.id.user_profile_heroes_count_wrapper}) @Nullable
    protected void notifyHeroesClicked(View view)
    {
        buttonClickedSubject.onNext(ButtonType.HEROES);
    }

    @SuppressWarnings("unused")
    @OnClick({R.id.user_profile_followers_count, R.id.user_profile_followers_count_wrapper}) @Nullable
    protected void notifyFollowersClicked(View view)
    {
        buttonClickedSubject.onNext(ButtonType.FOLLOWERS);
    }

    protected void notifyAchievementsClicked()
    {
        buttonClickedSubject.onNext(ButtonType.ACHIEVEMENTS);
    }

    @SuppressWarnings("unused")
    @OnLongClick(R.id.user_profile_avatar) @Nullable
    protected boolean onAvatarLongClicked(View view)
    {
        if (userProfileDTO != null)
        {
            ClipData clip = ClipData.newPlainText(userProfileDTO.displayName + " id", String.format("%d", userProfileDTO.id));
            clipboardManager.setPrimaryClip(clip);
            THToast.show("UserId " + userProfileDTO.id + " copied to clipboard");
            return true;
        }
        return false;
    }

    public enum ButtonType
    {
        HEROES,
        FOLLOWERS,
        ACHIEVEMENTS,
    }
}
