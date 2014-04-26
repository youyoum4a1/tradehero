package com.tradehero.th.fragments.social.hero;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
        implements DTOView<HeroDTO>, View.OnClickListener
{
    public static final int RES_ID_ACTIVE = R.drawable.image_icon_validation_valid;
    public static final int RES_ID_INACTIVE = R.drawable.buyscreen_info;
    public static final int RES_ID_CROSS_RED = R.drawable.cross_red;

    private ImageView userIcon;
    private TextView title;
    private TextView dateInfo;
    private ImageView statusIcon;

    private HeroDTO heroDTO;
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject Lazy<Picasso> picasso;
    private WeakReference<OnHeroStatusButtonClickedListener> heroStatusButtonClickedListener =
            new WeakReference<>(null);
    @Inject UserBaseDTOUtil userBaseDTOUtil;

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
        initViews();
        DaggerUtils.inject(this);
        Timber.d("HeroListItemView onFinishInflate hashCode:%d", this.hashCode());
    }

    private void initViews()
    {
        userIcon = (ImageView) findViewById(R.id.follower_profile_picture);
        title = (TextView) findViewById(R.id.hero_title);
        dateInfo = (TextView) findViewById(R.id.hero_date_info);
        statusIcon = (ImageView) findViewById(R.id.ic_status);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (userIcon != null)
        {
            picasso.get().load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformation)
                    .into(userIcon);
        }
        if (statusIcon != null)
        {
            statusIcon.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    OnHeroStatusButtonClickedListener heroStatusButtonClickedListener =
                            HeroListItemView.this.heroStatusButtonClickedListener.get();
                    if (heroStatusButtonClickedListener != null)
                    {
                        heroStatusButtonClickedListener.onHeroStatusButtonClicked(
                                HeroListItemView.this, heroDTO);
                    }
                }
            });
        }
        userIcon.setOnClickListener(this);
        Timber.d("HeroListItemView onAttachedToWindow hashCode:%d", this.hashCode());
    }

    private void resetIcon()
    {
        if (statusIcon != null)
        {
            statusIcon.setImageDrawable(null);
        }
        if (userIcon != null)
        {
            userIcon.setImageDrawable(null);
        }
    }

    private void removeCallback()
    {
        if (statusIcon != null)
        {
            statusIcon.setOnClickListener(null);
        }
        if (userIcon != null)
        {
            userIcon.setOnClickListener(null);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        resetIcon();
        removeCallback();
        //ButterKnife.reset(this);
        Timber.d("HeroListItemView onDetachedFromWindow hashCode:%d", this.hashCode());

        super.onDetachedFromWindow();
    }

    @Override public void onClick(View v)
    {
        if (v.getId() == R.id.follower_profile_picture)
        {
            if (heroDTO != null)
            {
                handleUserIconClicked();
            }
        }
    }

    private void handleUserIconClicked()
    {
        Bundle bundle = new Bundle();
        DashboardNavigator navigator =
                ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
        bundle.putInt(PushableTimelineFragment.BUNDLE_KEY_SHOW_USER_ID, heroDTO.id);
        navigator.pushFragment(PushableTimelineFragment.class, bundle);
    }

    public void setHeroStatusButtonClickedListener(
            OnHeroStatusButtonClickedListener heroStatusButtonClickedListener)
    {
        this.heroStatusButtonClickedListener = new WeakReference<>(heroStatusButtonClickedListener);
    }

    public HeroDTO getHeroDTO()
    {
        return heroDTO;
    }

    public void display(HeroDTO heroDTO)
    {
        //resetIcon();
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
        if (userIcon != null)
        {
            if (heroDTO != null)
            {
                picasso.get().load(heroDTO.picture)
                        .transform(peopleIconTransformation)
                        // TODO if this view is reused, userIcon.getDrawable() may returns the different drawable
                        .placeholder(userIcon.getDrawable())
                        .into(userIcon, new Callback()
                        {
                            @Override
                            public void onSuccess()
                            {
                            }

                            @Override
                            public void onError()
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
    }

    public void displayDefaultUserIcon()
    {
        if (userIcon != null)
        {
            picasso.get().load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformation)
                    .into(userIcon);
        }
    }

    public void displayTitle()
    {
        if (title != null)
        {
            title.setText(userBaseDTOUtil.getLongDisplayName(getContext(), heroDTO));
        }
    }

    public void displayDateInfo()
    {
        if (dateInfo != null)
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
    }

    public void displayStatus()
    {
        if (statusIcon != null)
        {
            //statusIcon.setImageResource(
            //        (heroDTO != null && heroDTO.active) ? RES_ID_ACTIVE : RES_ID_INACTIVE);

            statusIcon.setImageResource(RES_ID_CROSS_RED);
        }
    }

    //</editor-fold>

    public static interface OnHeroStatusButtonClickedListener
    {
        void onHeroStatusButtonClicked(HeroListItemView heroListItemView, HeroDTO heroDTO);
    }
}
