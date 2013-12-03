package com.tradehero.th.fragments.social.hero;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.users.UserBaseUtil;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 12:28 PM To change this template use File | Settings | File Templates. */
public class HeroListItemView extends RelativeLayout implements DTOView<HeroDTO>
{
    public static final String TAG = HeroListItemView.class.getName();
    public static final int RES_ID_ACTIVE = R.drawable.image_icon_validation_valid;
    public static final int RES_ID_INACTIVE = R.drawable.buyscreen_info;

    private ImageView userIcon;
    private TextView title;
    private TextView dateInfo;
    private ImageView statusIcon;

    private HeroDTO heroDTO;
    @Inject Lazy<Picasso> picasso;
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
        initViews();
        DaggerUtils.inject(this);
        if (userIcon != null)
        {
            picasso.get().load(R.drawable.superman_facebook)
                    .transform(new RoundedShapeTransformation())
                    .into(userIcon);
        }
    }

    private void initViews()
    {
        userIcon = (ImageView) findViewById(R.id.user_icon);
        title = (TextView) findViewById(R.id.hero_title);
        dateInfo = (TextView) findViewById(R.id.hero_date_info);
        statusIcon = (ImageView) findViewById(R.id.ic_status);
    }

    @Override protected void onAttachedToWindow()
    {
        if (statusIcon != null)
        {
            statusIcon.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    OnHeroStatusButtonClickedListener heroStatusButtonClickedListener = HeroListItemView.this.heroStatusButtonClickedListener.get();
                    if (heroStatusButtonClickedListener != null)
                    {
                        heroStatusButtonClickedListener.onHeroStatusButtonClicked(HeroListItemView.this, heroDTO);
                    }
                }
            });
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (statusIcon != null)
        {
            statusIcon.setOnClickListener(null);
        }
    }

    public void setHeroStatusButtonClickedListener(OnHeroStatusButtonClickedListener heroStatusButtonClickedListener)
    {
        this.heroStatusButtonClickedListener = new WeakReference<>(heroStatusButtonClickedListener);
    }

    public HeroDTO getHeroDTO()
    {
        return heroDTO;
    }

    public void display(HeroDTO heroDTO)
    {
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
                             .transform(new RoundedShapeTransformation())
                             .into(userIcon);
            }
        }
    }

    public void displayTitle()
    {
        if (title != null)
        {
            title.setText(UserBaseUtil.getLongDisplayName(getContext(), heroDTO));
        }
    }

    public void displayDateInfo()
    {
        if (dateInfo != null)
        {
            if (heroDTO != null)
            {
                SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.manage_heroes_datetime_format));
                if (heroDTO.active && heroDTO.followingSince != null)
                {
                    dateInfo.setText(String.format(getResources().getString(R.string.manage_heroes_following_since), df.format(heroDTO.followingSince)));
                }
                else if (!heroDTO.active && heroDTO.stoppedFollowingOn != null)
                {
                    dateInfo.setText(String.format(getResources().getString(R.string.manage_heroes_not_following_since), df.format(heroDTO.stoppedFollowingOn)));
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
            statusIcon.setImageResource((heroDTO != null && heroDTO.active) ? RES_ID_ACTIVE : RES_ID_INACTIVE);
        }
    }
    //</editor-fold>

    public static interface OnHeroStatusButtonClickedListener
    {
        void onHeroStatusButtonClicked(HeroListItemView heroListItemView, HeroDTO heroDTO);
    }
}
