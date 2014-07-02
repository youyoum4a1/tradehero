package com.tradehero.th.fragments.timeline;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.graphics.ForUserPhotoBackground;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.THSignedNumber;
import com.tradehero.thm.R;
import java.text.SimpleDateFormat;
import javax.inject.Inject;

public class UserProfileDetailViewHolder extends UserProfileCompactViewHolder
{
    @InjectView(R.id.profile_screen_user_detail_top) @Optional protected View profileTop;
    @InjectView(R.id.user_profile_first_last_name) @Optional protected TextView firstLastName;
    @InjectView(R.id.txt_member_since) @Optional protected TextView memberSince;
    @InjectView(R.id.txt_profile_tradeprofit) @Optional protected TextView profitFromTrades;
    @InjectView(R.id.txt_total_wealth) @Optional protected TextView totalWealth;
    @InjectView(R.id.txt_additional_cash) @Optional protected TextView additionalCash;
    @InjectView(R.id.txt_cash_on_hand) @Optional protected TextView cashOnHand;
    @InjectView(R.id.user_profile_trade_count_wrapper) @Optional protected View tradesCountWrapper;
    @InjectView(R.id.user_profile_trade_count) @Optional protected TextView tradesCount;
    @InjectView(R.id.user_profile_exchanges_count_wrapper) @Optional protected View
            exchangesCountWrapper;
    @InjectView(R.id.user_profile_exchanges_count) @Optional protected TextView exchangesCount;

    @Inject @ForUserPhotoBackground protected Transformation peopleBackgroundTransformation;
    private Target topBackgroundTarget;
    private Target topDefaultBackgroundTarget;
    protected Runnable displayTopViewBackgroundRunnable;

    public UserProfileDetailViewHolder(View view)
    {
        super(view);
    }

    @Override public void initViews(View view)
    {
        super.initViews(view);
        topBackgroundTarget = new BackgroundTarget();
        topDefaultBackgroundTarget = new DefaultBackgroundTarget();
    }

    @Override public void detachViews()
    {
        super.detachViews();
        topBackgroundTarget = null;
        topDefaultBackgroundTarget = null;
        if (profileTop != null)
        {
            profileTop.removeCallbacks(displayTopViewBackgroundRunnable);
        }
    }

    @Override public void display(final UserProfileDTO dto)
    {
        super.display(dto);
        displayFirstLastName();
        displayMemberSince();
        displayProfitFromTrades();
        displayTotalWealth();
        displayAdditionalCash();
        displayCashOnHand();
        displayExchangesCount();
        displayTradesCount();
        loadBgPicture();
    }

    protected void loadBgPicture()
    {
        displayTopViewBackgroundRunnable = new Runnable()
        {
            @Override public void run()
            {
                if (userProfileDTO != null &&
                        profileTop != null &&
                        profileTop.getWidth() > 0 &&
                        profileTop.getHeight() > 0 &&
                        topBackgroundTarget != null)
                {
                    loadDefaultBG();
                    if (userProfileDTO.picture != null)
                    {
                        picasso.load(userProfileDTO.picture)
                                .transform(peopleBackgroundTransformation)
                                .resize(profileTop.getWidth(), profileTop.getHeight())
                                .centerCrop()
                                .into(topBackgroundTarget);
                    }
                }
            }
        };
        if (profileTop != null)
        {
            profileTop.post(displayTopViewBackgroundRunnable);
        }
    }

    public void loadDefaultBG()
    {
        if (profileTop != null
                && topDefaultBackgroundTarget != null
                && profileTop.getWidth() > 0
                && profileTop.getHeight() > 0)
        {
            picasso.load(R.drawable.superman_facebook)
                    .transform(peopleBackgroundTransformation)
                    .resize(profileTop.getWidth(), profileTop.getHeight())
                    .centerCrop()
                    .into(topDefaultBackgroundTarget);
        }
    }

    protected void displayFirstLastName()
    {
        if (firstLastName != null)
        {
            if (userProfileDTO != null)
            {
                firstLastName.setText(context.getString(R.string.user_profile_first_last_name_display,
                        userProfileDTO.firstName != null ? userProfileDTO.firstName : "",
                        userProfileDTO.lastName != null ? userProfileDTO.lastName : ""));
            }
            else
            {
                firstLastName.setText(R.string.na);
            }
        }
    }

    protected void displayMemberSince()
    {
        if (memberSince != null)
        {
            if (userProfileDTO != null && userProfileDTO.memberSince != null)
            {
                SimpleDateFormat memberSinceFormat = new SimpleDateFormat("MMM yyyy");
                memberSince.setText(memberSinceFormat.format(userProfileDTO.memberSince));
            }
            else
            {
                memberSince.setText(R.string.na);
            }
        }
    }

    protected void displayProfitFromTrades()
    {
        if (profitFromTrades != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
            {
                Double pl = userProfileDTO.portfolio.plSinceInception;
                if (pl == null)
                {
                    pl = 0.0;
                }
                THSignedNumber thPlSinceInception =
                        new THSignedNumber(THSignedNumber.TYPE_MONEY, pl, THSignedNumber.WITH_SIGN,
                                SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY,
                                THSignedNumber.TYPE_SIGN_PLUS_MINUS_ALWAYS);
                profitFromTrades.setText(thPlSinceInception.toString());
                profitFromTrades.setTextColor(
                        context.getResources().getColor(thPlSinceInception.getColor()));
                if (profitValue != null)
                {
                    profitValue.setText(thPlSinceInception.toString());
                    profitValue.setTextColor(context.getResources().getColor(thPlSinceInception.getColor()));
                }
            }
            else
            {
                profitFromTrades.setText(R.string.na);
                profitFromTrades.setTextColor(context.getResources().getColor(R.color.black));
                if (profitValue != null)
                {
                    profitValue.setText(R.string.na);
                    profitValue.setTextColor(context.getResources().getColor(R.color.black));
                }
            }
        }
    }

    protected void displayTotalWealth()
    {
        if (totalWealth != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
            {
                THSignedNumber thTotalWealth =
                        new THSignedNumber(THSignedNumber.TYPE_MONEY, userProfileDTO.portfolio.totalValue,
                                THSignedNumber.WITHOUT_SIGN);
                totalWealth.setText(thTotalWealth.toString());
            }
            else
            {
                totalWealth.setText(R.string.na);
            }
        }
    }

    protected void displayAdditionalCash()
    {
        if (additionalCash != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
            {
                THSignedNumber thAdditionalCash = new THSignedNumber(THSignedNumber.TYPE_MONEY,
                        userProfileDTO.portfolio.getTotalExtraCash(), THSignedNumber.WITHOUT_SIGN);
                additionalCash.setText(thAdditionalCash.toString());
            }
            else
            {
                additionalCash.setText(R.string.na);
            }
        }
    }

    protected void displayCashOnHand()
    {
        if (cashOnHand != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
            {
                THSignedNumber thCashOnHand =
                        new THSignedNumber(THSignedNumber.TYPE_MONEY, userProfileDTO.portfolio.cashBalance,
                                THSignedNumber.WITHOUT_SIGN);
                cashOnHand.setText(thCashOnHand.toString());
            }
            else
            {
                cashOnHand.setText(R.string.na);
            }
        }
    }

    protected void displayExchangesCount()
    {
        if (exchangesCount != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
            {
                exchangesCount.setText(Integer.toString(userProfileDTO.portfolio.countExchanges));
            }
            else
            {
                exchangesCount.setText(R.string.na);
            }
        }
    }

    protected void displayTradesCount()
    {
        if (tradesCount != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
            {
                tradesCount.setText(Integer.toString(userProfileDTO.portfolio.countTrades));
            }
            else
            {
                tradesCount.setText(R.string.na);
            }
        }
    }

    public void setVisibility(int visibility)
    {
        if (visibility == View.VISIBLE
                && displayTopViewBackgroundRunnable != null
                && profileTop != null)
        {
            profileTop.post(displayTopViewBackgroundRunnable);
        }
    }

    protected class BackgroundTarget implements Target
    {
        @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
        {
            if (profileTop != null)
            {
                profileTop.setBackgroundDrawable(new BitmapDrawable(context.getResources(), bitmap));
            }
        }

        @Override public void onBitmapFailed(Drawable errorDrawable)
        {
        }

        @Override public void onPrepareLoad(Drawable placeHolderDrawable)
        {
        }
    }

    protected class DefaultBackgroundTarget extends BackgroundTarget
    {
    }
}
