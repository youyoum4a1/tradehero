package com.tradehero.th.widget.user;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tradehero.common.graphics.GradientTransformation;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.common.graphics.ScaleKeepRatioTransformation;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.utils.THSignedNumber;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.fragments.portfolio.PortfolioRequestListener;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/10/13 Time: 6:34 PM Copyright (c) TradeHero */
public class ProfileView extends FrameLayout implements DTOView<UserProfileDTO>
{
    private static final String TAG = ProfileView.class.getName();
    private ImageView avatar;
    private ImageView background;

    private TextView roiSinceInception;
    private TextView hqSinceInception;
    private TextView plSinceInception;
    private TextView memberSince;
    private TextView totalWealth;
    private TextView additionalCash;
    private TextView cashOnHand;

    private TextView followersCount;
    private TextView heroesCount;
    private TextView tradesCount;
    private TextView exchangesCount;
    private ImageView btnDefaultPortfolio;

    @Inject protected Picasso picasso;
    private boolean initiated;
    private WeakReference<PortfolioRequestListener> portfolioRequestListener = new WeakReference<>(null);

    //<editor-fold desc="Constructors">
    public ProfileView(Context context)
    {
        this(context, null);
    }

    public ProfileView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ProfileView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        if (initiated) return;
        initiated = true;

        avatar = (ImageView) findViewById(R.id.user_profile_avatar);
        background = (ImageView) findViewById(R.id.user_profile_background_by_sketched_avatar);

        roiSinceInception = (TextView) findViewById(R.id.txt_roi);
        hqSinceInception = (TextView) findViewById(R.id.txt_hero_quotient);
        plSinceInception = (TextView) findViewById(R.id.txt_profile_tradeprofit);
        memberSince = (TextView) findViewById(R.id.txt_member_since);
        totalWealth = (TextView) findViewById(R.id.txt_total_wealth);
        additionalCash = (TextView) findViewById(R.id.txt_additional_cash);
        cashOnHand = (TextView) findViewById(R.id.txt_cash_on_hand);

        followersCount = (TextView) findViewById(R.id.user_profile_followers_count);
        heroesCount = (TextView) findViewById(R.id.user_profile_heroes_count);
        tradesCount = (TextView) findViewById(R.id.user_profile_trade_count);
        exchangesCount = (TextView) findViewById(R.id.user_profile_exchanges_count);
        btnDefaultPortfolio = (ImageView) findViewById(R.id.btn_user_profile_default_portfolio);
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

        DaggerUtils.inject(this);
    }

    @Override public void display(final UserProfileDTO dto)
    {
        if (dto == null)
        {
            return;
        }

        if (dto.picture != null)
        {
            picasso
                .load(dto.picture)
                .transform(new RoundedShapeTransformation())
                .into(avatar);


            // TODO make it less hacKy
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            background.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
            {
                @Override public boolean onPreDraw()
                {
                    if (ProfileView.this.getHeight() > 0 && background.getDrawable() == null)
                    {
                        picasso
                                .load(dto.picture)
                                .transform(new GradientTransformation())
                                .transform(new ScaleKeepRatioTransformation(displayMetrics.widthPixels, 0))
                                .resize(displayMetrics.widthPixels, ProfileView.this.getHeight())
                                .centerCrop()
                                .into(background);
                    }
                    return true;
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

            if (plSinceInception != null)
            {
                Double pl = dto.portfolio.plSinceInception;
                if (pl == null)
                {
                    pl = 0.0;
                }
                THSignedNumber thPlSinceInception = new THSignedNumber(THSignedNumber.TYPE_MONEY, pl);
                plSinceInception.setText(thPlSinceInception.toString());
                plSinceInception.setTextColor(getResources().getColor(thPlSinceInception.getColor()));
            }

            if (totalWealth != null)
            {
                totalWealth.setText(String.format("%s %,.0f", SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, dto.portfolio.totalValue));
            }

            if (additionalCash != null)
            {
                additionalCash.setText(String.format("%s %,.0f", SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, dto.portfolio.getTotalExtraCash()));
            }

            if (cashOnHand != null)
            {
                cashOnHand.setText(String.format("%s %,.0f", SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, dto.portfolio.cashBalance));
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

        if (hqSinceInception != null)
        {
            hqSinceInception.setText(R.string.na);
        }

        if (memberSince != null)
        {
            SimpleDateFormat memberSinceFormat = new SimpleDateFormat("MMMMM yyyy");
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
}
