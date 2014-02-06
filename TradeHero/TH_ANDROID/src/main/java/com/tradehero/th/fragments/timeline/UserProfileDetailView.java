package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.portfolio.PortfolioRequestListener;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.THSignedNumber;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/10/13 Time: 6:34 PM Copyright (c) TradeHero */
public class UserProfileDetailView extends LinearLayout implements DTOView<UserProfileDTO>
{
    private static final String TAG = UserProfileDetailView.class.getName();
    @Inject protected Picasso picasso;
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;

    private UserProfileDTO userProfileDTO;

    @InjectView(R.id.user_profile_avatar) ImageView avatar;
    @InjectView(R.id.profile_screen_user_detail_top) LinearLayout profileTop;
    @InjectView(R.id.txt_roi) TextView roiSinceInception;
    //@InjectView(R.id.txt_hero_quotient) TextView hqSinceInception;
    @InjectView(R.id.txt_profile_tradeprofit) TextView profitFromTrades;
    @InjectView(R.id.txt_member_since) TextView memberSince;
    @InjectView(R.id.txt_total_wealth) TextView totalWealth;
    @InjectView(R.id.txt_additional_cash) TextView additionalCash;

    @InjectView(R.id.txt_cash_on_hand) TextView cashOnHand;
    @InjectView(R.id.user_profile_followers_count) TextView followersCount;
    @InjectView(R.id.user_profile_heroes_count) TextView heroesCount;
    @InjectView(R.id.user_profile_trade_count) TextView tradesCount;
    @InjectView(R.id.user_profile_exchanges_count) TextView exchangesCount;

    @InjectView(R.id.btn_user_profile_default_portfolio) ImageView btnDefaultPortfolio;
    @InjectView(R.id.user_profile_display_name) TextView displayName;
    @InjectView(R.id.user_profile_first_last_name) TextView firstLastName;

    private WeakReference<PortfolioRequestListener> portfolioRequestListener = new WeakReference<>(null);
    private Runnable displayTopViewBackgroundRunnable;

    //<editor-fold desc="Constructors">
    public UserProfileDetailView(Context context)
    {
        this(context, null);
    }

    public UserProfileDetailView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public UserProfileDetailView(Context context, AttributeSet attrs, int defStyle)
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

    @Override protected void onDetachedFromWindow()
    {
        if (btnDefaultPortfolio != null)
        {
            btnDefaultPortfolio.setOnClickListener(null);
        }
        if (profileTop != null)
        {
            profileTop.removeCallbacks(displayTopViewBackgroundRunnable);
        }
        displayTopViewBackgroundRunnable = null;
        super.onDetachedFromWindow();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        THLog.d(TAG, "onAttachedToWindow");

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

        displayTopViewBackgroundRunnable = new Runnable()
        {
            @Override public void run()
            {
                if (userProfileDTO != null && profileTop.getWidth() > 0 && profileTop.getHeight() > 0)
                {
                    picasso
                            .load(userProfileDTO.picture)
                            .transform(new GrayscaleTransformation())
                            .transform(new FastBlurTransformation(30))
                            .transform(new GradientTransformation(
                                    getResources().getColor(R.color.profile_view_gradient_top),
                                    getResources().getColor(R.color.profile_view_gradient_bottom)))
                            .resize(profileTop.getWidth(), profileTop.getHeight())
                            .centerCrop()
                            .into(topBackgroundTarget);
                }

            }
        };
        post(displayTopViewBackgroundRunnable);
    }

    @Override public void setVisibility(int visibility)
    {
        super.setVisibility(visibility);

        if (visibility == VISIBLE && displayTopViewBackgroundRunnable != null)
        {
            profileTop.post(displayTopViewBackgroundRunnable);
        }
    }

    @Override public void display(final UserProfileDTO dto)
    {
        this.userProfileDTO = dto;
        if (dto == null)
        {
            loadDefaultPicture();
            return;
        }

        if (dto.picture != null)
        {
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
        }

        if (dto.portfolio != null)
        {
            if (roiSinceInception != null)
            {
                Double roi = dto.portfolio.roiSinceInception;
                if (roi == null)
                {
                    roi = 0.0;
                }
                THSignedNumber thRoiSinceInception = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, roi*100);
                roiSinceInception.setText(thRoiSinceInception.toString());
                roiSinceInception.setTextColor(getResources().getColor(thRoiSinceInception.getColor()));
            }

            if (profitFromTrades != null)
            {
                Double pl = dto.portfolio.plSinceInception;
                if (pl == null)
                {
                    pl = 0.0;
                }
                THSignedNumber thPlSinceInception = new THSignedNumber(
                        THSignedNumber.TYPE_MONEY,
                        pl,
                        true,
                        SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY,
                        THSignedNumber.TYPE_SIGN_PLUS_MINUS_ALWAYS);
                profitFromTrades.setText(thPlSinceInception.toString());
                profitFromTrades.setTextColor(getResources().getColor(thPlSinceInception.getColor()));
            }

            if (totalWealth != null)
            {
                THSignedNumber thTotalWealth = new THSignedNumber(THSignedNumber.TYPE_MONEY, dto.portfolio.totalValue, false);
                totalWealth.setText(thTotalWealth.toString());
            }

            if (additionalCash != null)
            {
                THSignedNumber thAdditionalCash = new THSignedNumber(THSignedNumber.TYPE_MONEY, dto.portfolio.getTotalExtraCash(), false);
                additionalCash.setText(thAdditionalCash.toString());
            }

            if (cashOnHand != null)
            {
                THSignedNumber thCashOnHand = new THSignedNumber(THSignedNumber.TYPE_MONEY, dto.portfolio.cashBalance, false);
                cashOnHand.setText(thCashOnHand.toString());
            }

            if (tradesCount != null)
            {
                tradesCount.setText(Integer.toString(dto.portfolio.countTrades));
            }

            if (exchangesCount != null)
            {
                exchangesCount.setText(Integer.toString(dto.portfolio.countExchanges));
            }
        }

        //if (hqSinceInception != null)
        //{
        //    hqSinceInception.setText(R.string.na);
        //}

        if (memberSince != null)
        {
            SimpleDateFormat memberSinceFormat = new SimpleDateFormat("MMM yyyy");
            memberSince.setText(memberSinceFormat.format(dto.memberSince));
        }

        if (followersCount != null)
        {
            followersCount.setText(Integer.toString(dto.followerCount == null ? 0 : dto.followerCount));
        }

        if (heroesCount != null)
        {
            heroesCount.setText(Integer.toString(dto.heroIds == null ? 0 : dto.heroIds.size()));
        }

        if (displayName != null)
        {
            displayName.setText(dto.displayName);
        }

        if (firstLastName != null)
        {
            firstLastName.setText(getResources().getString(R.string.first_last_name_display, dto.firstName, dto.lastName));
        }
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
     * @param portfolioRequestListener
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


    private Target topBackgroundTarget = new Target()
    {
        @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
        {
            if (profileTop != null)
            {
                profileTop.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                // only available since API level 16
                // profileTop.setBackground(new BitmapDrawable(getResources(), bitmap));
            }
        }

        @Override public void onBitmapFailed(Drawable errorDrawable)
        {

        }

        @Override public void onPrepareLoad(Drawable placeHolderDrawable)
        {

        }
    };
}
