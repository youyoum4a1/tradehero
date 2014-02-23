package com.tradehero.th.fragments.timeline;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.graphics.ForUserPhotoBackground;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.THSignedNumber;
import java.text.SimpleDateFormat;
import javax.inject.Inject;

/**
 * Created by xavier on 2/6/14.
 */
public class UserProfileDetailViewHolder extends UserProfileCompactViewHolder
{
    protected View profileTop;
    protected TextView firstLastName;
    protected TextView memberSince;
    protected TextView profitFromTrades;
    protected TextView totalWealth;
    protected TextView additionalCash;
    protected TextView cashOnHand;
    protected TextView tradesCount;
    protected TextView exchangesCount;

    @Inject @ForUserPhotoBackground protected Transformation peopleBackgroundTransformation;
    private Target topBackgroundTarget;
    protected Runnable displayTopViewBackgroundRunnable;

    public UserProfileDetailViewHolder(View view)
    {
        super(view);
    }

    @Override public void initViews(View view)
    {
        super.initViews(view);
        profileTop = view.findViewById(R.id.profile_screen_user_detail_top);
        firstLastName = (TextView) view.findViewById(R.id.user_profile_first_last_name);
        memberSince = (TextView) view.findViewById(R.id.txt_member_since);
        profitFromTrades = (TextView) view.findViewById(R.id.txt_profile_tradeprofit);
        totalWealth = (TextView) view.findViewById(R.id.txt_total_wealth);
        additionalCash = (TextView) view.findViewById(R.id.txt_additional_cash);
        cashOnHand = (TextView) view.findViewById(R.id.txt_cash_on_hand);
        tradesCount = (TextView) view.findViewById(R.id.user_profile_trade_count);
        exchangesCount = (TextView) view.findViewById(R.id.user_profile_exchanges_count);
    }

    public void onAttachedToWindow()
    {
        topBackgroundTarget = new BackgroundTarget();
    }

    public void onDetachedFromWindow()
    {
        topBackgroundTarget = null;
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
                    picasso
                            .load(userProfileDTO.picture)
                                    .transform(peopleBackgroundTransformation)
                            //.transform(new GrayscaleTransformation())
                            //.transform(new FastBlurTransformation(30))
                            //.transform(new GradientTransformation(
                            //        context.getResources().getColor(R.color.profile_view_gradient_top),
                            //        context.getResources().getColor(R.color.profile_view_gradient_bottom)))
                            .resize(profileTop.getWidth(),
                                    profileTop.getHeight())
                            .centerCrop()
                            .into(topBackgroundTarget);
                }

            }
        };
        if (profileTop != null)
        {
            profileTop.post(displayTopViewBackgroundRunnable);
        }
    }

    protected void displayFirstLastName()
    {
        if (firstLastName != null)
        {
            if (userProfileDTO != null)
            {
                firstLastName.setText(context.getString(
                        R.string.user_profile_first_last_name_display,
                        userProfileDTO.firstName != null ? userProfileDTO.firstName: "",
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
                THSignedNumber thPlSinceInception = new THSignedNumber(
                        THSignedNumber.TYPE_MONEY,
                        pl,
                        true,
                        SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY,
                        THSignedNumber.TYPE_SIGN_PLUS_MINUS_ALWAYS);
                profitFromTrades.setText(thPlSinceInception.toString());
                profitFromTrades.setTextColor(context.getResources().getColor(thPlSinceInception.getColor()));
            }
            else
            {
                profitFromTrades.setText(R.string.na);
                profitFromTrades.setTextColor(context.getResources().getColor(R.color.black));
            }
        }
    }

    protected void displayTotalWealth()
    {
        if (totalWealth != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
            {
                THSignedNumber thTotalWealth = new THSignedNumber(THSignedNumber.TYPE_MONEY, userProfileDTO.portfolio.totalValue, false);
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
                THSignedNumber thAdditionalCash = new THSignedNumber(THSignedNumber.TYPE_MONEY, userProfileDTO.portfolio.getTotalExtraCash(), false);
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
                THSignedNumber thCashOnHand = new THSignedNumber(THSignedNumber.TYPE_MONEY, userProfileDTO.portfolio.cashBalance, false);
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
        if (visibility == View.VISIBLE && displayTopViewBackgroundRunnable != null && profileTop != null)
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
    }
}
