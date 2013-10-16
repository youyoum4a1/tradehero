package com.tradehero.th.widget.user;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.GradientTransformation;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.THSignedNumber;

/** Created with IntelliJ IDEA. User: tho Date: 9/10/13 Time: 6:34 PM Copyright (c) TradeHero */
public class ProfileView extends FrameLayout implements DTOView<UserProfileDTO>
{
    private ImageView avatar;
    private ImageView background;

    private TextView roiSinceInception;
    private TextView plSinceInception;


    private TextView followersCount;
    private TextView heroesCount;
    private TextView tradesCount;
    private TextView exchangesCount;

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

        roiSinceInception = (TextView) findViewById(R.id.txt_ROI);
        plSinceInception = (TextView) findViewById(R.id.txt_profile_tradeprofit);

        followersCount = (TextView) findViewById(R.id.user_profile_followers_count);
        heroesCount = (TextView) findViewById(R.id.user_profile_heroes_count);
        tradesCount = (TextView) findViewById(R.id.user_profile_trade_count);
        exchangesCount = (TextView) findViewById(R.id.user_profile_exchanges_count);
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

        THSignedNumber thRoiSinceInception = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, dto.portfolio.roiSinceInception*100);
        roiSinceInception.setText(thRoiSinceInception.toString());
        roiSinceInception.setTextColor(getResources().getColor(thRoiSinceInception.getColor()));

        THSignedNumber thPlSinceInception = new THSignedNumber(THSignedNumber.TYPE_MONEY, dto.portfolio.plSinceInception);
        plSinceInception.setText(thPlSinceInception.toString());
        plSinceInception.setTextColor(getResources().getColor(thPlSinceInception.getColor()));

        followersCount.setText(Integer.toString(dto.followerCount));
        heroesCount.setText(Integer.toString(dto.heroIds.size()));
        tradesCount.setText(Integer.toString(dto.portfolio.countTrades));
        exchangesCount.setText(Integer.toString(dto.portfolio.countExchanges));
    }
}
