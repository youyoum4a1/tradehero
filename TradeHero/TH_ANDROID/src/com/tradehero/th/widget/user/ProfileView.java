package com.tradehero.th.widget.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.tradehero.common.graphics.GaussianTransformation;
import com.tradehero.common.graphics.GradientTransformation;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserProfileDTO;

/** Created with IntelliJ IDEA. User: tho Date: 9/10/13 Time: 6:34 PM Copyright (c) TradeHero */
public class ProfileView extends FrameLayout implements DTOView<UserProfileDTO>
{
    private ImageView avatar;
    private ImageView background;

    //<editor-fold desc="Constructors">
    public ProfileView(Context context)
    {
        super(context);
        init();
    }

    public ProfileView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ProfileView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        avatar = (ImageView) findViewById(R.id.user_profile_avatar);
        background = (ImageView) findViewById(R.id.user_profile_background_by_sketched_avatar);
    }

    @Override public void display(UserProfileDTO dto)
    {
        if (dto.picture != null)
        {
            Picasso.with(getContext())
                .load(dto.picture)
                .transform(new RoundedShapeTransformation())
                .into(avatar);

            Picasso.with(getContext())
                .load(dto.picture)
                .transform(new GradientTransformation())
                .into(background);
        }
    }

    private void displayUserStatistic()
    {
        //TextView heroesCount = (TextView) getView().findViewById(R.id.user_profile_heroes_count);
        //heroesCount.setText(profile.heroIds.size());
        //
        //TextView followersCount = (TextView) getView().findViewById(R.id.user_profile_followers_count);
        //followersCount.setText(profile.followerCount);
        //
        //TextView tradesCount = (TextView) getView().findViewById(R.id.user_profile_trade_count);
        //tradesCount.setText(profile.tradesSharedCount_FB);
        //
        //TextView exchangesCount = (TextView) getView().findViewById(R.id.user_profile_exchanges_count);
        //exchangesCount.setText(profile.);
        //TextView heroesCount = (TextView) getView().findViewById(R.id.user_profile_heroes_count);

    }
}
