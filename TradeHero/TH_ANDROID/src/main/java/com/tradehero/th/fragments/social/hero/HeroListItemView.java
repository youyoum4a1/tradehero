package com.tradehero.th.fragments.social.hero;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import javax.inject.Inject;
import timber.log.Timber;

public class HeroListItemView extends RelativeLayout
        implements DTOView<HeroDTO>
{
    public static final int RES_ID_ACTIVE = R.drawable.image_icon_validation_valid;
    public static final int RES_ID_INACTIVE = R.drawable.buyscreen_info;
    public static final int RES_ID_CROSS_RED = R.drawable.cross_red;

    @InjectView(R.id.follower_profile_picture) ImageView userIcon;
    @InjectView(R.id.hero_title) TextView title;
    @InjectView(R.id.hero_date_info) TextView dateInfo;
    @InjectView(R.id.ic_status) ImageView statusIcon;

    private HeroDTO heroDTO;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;
    @Inject Lazy<Picasso> picasso;
    @Inject UserBaseDTOUtil userBaseDTOUtil;

    private WeakReference<OnHeroStatusButtonClickedListener> heroStatusButtonClickedListener = new WeakReference<>(null);

    //<editor-fold desc="Constructors">
    public HeroListItemView(Context context)
    {
        super(context);
    }

    public HeroListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HeroListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        DaggerUtils.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        picasso.get().load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(userIcon);
        Timber.d("HeroListItemView onAttachedToWindow hashCode:%d", this.hashCode());
    }

    @OnClick(R.id.ic_status) void onStatusIconClicked()
    {
        OnHeroStatusButtonClickedListener heroStatusButtonClickedListener = HeroListItemView.this.heroStatusButtonClickedListener.get();
        if (heroStatusButtonClickedListener != null)
        {
            heroStatusButtonClickedListener.onHeroStatusButtonClicked(HeroListItemView.this, heroDTO);
        }
    }

    //<editor-fold desc="Reset views">
    private void resetIcons()
    {
        resetStatusIcon();

        resetUserIcon();
    }

    private void resetUserIcon()
    {
        picasso.get().cancelRequest(userIcon);
        userIcon.setImageDrawable(null);
    }

    private void resetStatusIcon()
    {
        picasso.get().cancelRequest(statusIcon);
        statusIcon.setImageDrawable(null);
    }
    //</editor-fold>

    @Override protected void onDetachedFromWindow()
    {
        resetIcons();

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @OnClick(R.id.follower_profile_picture) void onFollowerProfilePictureClicked(View v)
    {
        if (heroDTO != null)
        {
            Bundle bundle = new Bundle();
            DashboardNavigator navigator =
                    ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
            bundle.putInt(PushableTimelineFragment.BUNDLE_KEY_SHOW_USER_ID, heroDTO.id);
            navigator.pushFragment(PushableTimelineFragment.class, bundle);
        }
    }

    public void setHeroStatusButtonClickedListener(OnHeroStatusButtonClickedListener heroStatusButtonClickedListener)
    {
        this.heroStatusButtonClickedListener = new WeakReference<>(heroStatusButtonClickedListener);
    }

    public void display(HeroDTO heroDTO)
    {
        displayDefaultUserIcon();
        linkWith(heroDTO, true);
    }

    public void linkWith(HeroDTO heroDTO, boolean andDisplay)
    {
        this.heroDTO = heroDTO;
        if (andDisplay)
        {
            displayUserIcon();
            displayTitle();
            displayDateInfo();
            displayStatus();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayUserIcon();
        displayTitle();
        displayDateInfo();
        displayStatus();
    }

    public void displayUserIcon()
    {
        if (heroDTO != null)
        {
            resetUserIcon();
            picasso.get().load(heroDTO.picture)
                    .transform(peopleIconTransformation)
                    .error(R.drawable.superman_facebook)
                    .into(userIcon, new Callback()
                    {
                        @Override public void onSuccess()
                        {

                        }

                        @Override public void onError()
                        {
                            displayDefaultUserIcon();
                        }
                    });
        }
        else
        {
            displayDefaultUserIcon();
        }
    }

    public void displayDefaultUserIcon()
    {
        picasso.get().load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(userIcon);
    }

    public void displayTitle()
    {
        title.setText(userBaseDTOUtil.getLongDisplayName(getContext(), heroDTO));
    }

    public void displayDateInfo()
    {
        if (heroDTO != null)
        {
            SimpleDateFormat df = new SimpleDateFormat(
                    getResources().getString(R.string.manage_heroes_datetime_format));
            if (heroDTO.active && heroDTO.followingSince != null)
            {
                dateInfo.setText(String.format(
                        getResources().getString(R.string.manage_heroes_following_since),
                        df.format(heroDTO.followingSince)));
            }
            else if (!heroDTO.active && heroDTO.stoppedFollowingOn != null)
            {
                dateInfo.setText(String.format(
                        getResources().getString(R.string.manage_heroes_not_following_since),
                        df.format(heroDTO.stoppedFollowingOn)));
            }
            else
            {
                dateInfo.setText(R.string.na);
            }
        }
        else
        {
            dateInfo.setText(R.string.na);
        }
    }

    public void displayStatus()
    {
        statusIcon.setImageResource(RES_ID_CROSS_RED);
    }

    //</editor-fold>

    public static interface OnHeroStatusButtonClickedListener
    {
        void onHeroStatusButtonClicked(HeroListItemView heroListItemView, HeroDTO heroDTO);
    }
}
