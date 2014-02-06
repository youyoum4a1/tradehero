package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.tradehero.common.graphics.FastBlurTransformation;
import com.tradehero.common.graphics.GradientTransformation;
import com.tradehero.common.graphics.GrayscaleTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.THSignedNumber;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.fragments.portfolio.PortfolioRequestListener;
import java.lang.ref.WeakReference;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/17/13 Time: 12:51 PM Copyright (c) TradeHero */
public class UserProfileCompactView extends RelativeLayout implements DTOView<UserProfileDTO>
{
    @InjectView(R.id.user_profile_compact_avatar) ImageView avatar;
    @InjectView(R.id.user_profile_compact_roi) TextView roiSinceInception;
    @InjectView(R.id.user_profile_compact_followers_count) TextView followersCount;
    @InjectView(R.id.user_profile_compact_heroes_count) TextView heroesCount;
    @InjectView(R.id.user_profile_compact_display_name) TextView username;
    @InjectView(R.id.btn_user_profile_default_portfolio) ImageView btnDefaultPortfolio;

    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject protected Picasso picasso;

    private WeakReference<PortfolioRequestListener> portfolioRequestListener = new WeakReference<>(null);

    //<editor-fold desc="Constructors">
    public UserProfileCompactView(Context context)
    {
        this(context, null);
    }

    public UserProfileCompactView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public UserProfileCompactView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
        init();
    }

    private void init()
    {
        if (btnDefaultPortfolio != null)
        {
            btnDefaultPortfolio.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    pushDefaultPortfolio();
                }
            });
        }
    }

    @Override public void display(UserProfileDTO dto)
    {
        if (dto == null || dto.picture == null)
        {
            loadDefaultPicture();
            return;
        }

        picasso
                .load(dto.picture)
                .transform(peopleIconTransformation)
                .into(avatar, new Callback()
                {
                    @Override public void onSuccess()
                    {

                    }

                    @Override public void onError()
                    {
                        loadDefaultPicture();
                    }
                });

        picasso
                .load(dto.picture)
                .transform(new GrayscaleTransformation())
                .transform(new FastBlurTransformation(30))
                .transform(new GradientTransformation(
                        getResources().getColor(R.color.profile_view_gradient_top),
                        getResources().getColor(R.color.profile_view_gradient_bottom))).into(fakeTarget);

        if (dto.portfolio != null && dto.portfolio.roiSinceInception != null)
        {
            THSignedNumber thRoiSinceInception = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, dto.portfolio.roiSinceInception * 100);
            roiSinceInception.setText(thRoiSinceInception.toString());
            roiSinceInception.setTextColor(getResources().getColor(thRoiSinceInception.getColor()));
        }

        followersCount.setText(Integer.toString(dto.followerCount));
        heroesCount.setText(Integer.toString(dto.heroIds.size()));
        username.setText(dto.displayName);
    }

    private void loadDefaultPicture()
    {
        picasso
                .load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(avatar);
    }

    /**
     * Listeners should be strongly referenced elsewhere
     */
    public void setPortfolioRequestListener(PortfolioRequestListener portfolioRequestListener)
    {
        this.portfolioRequestListener = new WeakReference<>(portfolioRequestListener);
    }

    private void pushDefaultPortfolio()
    {
        PortfolioRequestListener listener = portfolioRequestListener.get();
        if (listener != null)
        {
            listener.onDefaultPortfolioRequested();
        }
    }


    private Target fakeTarget = new Target()
    {
        @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
        {

        }

        @Override public void onBitmapFailed(Drawable errorDrawable)
        {

        }

        @Override public void onPrepareLoad(Drawable placeHolderDrawable)
        {

        }
    };
}
