package com.tradehero.th.fragments.social.hero;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.thm.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.social.HeroPayoutDTO;
import com.tradehero.th.utils.SecurityUtils;
import java.text.SimpleDateFormat;

public class HeroPayoutListItemView extends RelativeLayout implements DTOView<HeroPayoutDTO>
{
    private TextView payoutDate;
    private TextView payoutAmount;

    private HeroPayoutDTO heroPayoutDTO;

    //<editor-fold desc="Constructors">
    public HeroPayoutListItemView(Context context)
    {
        super(context);
    }

    public HeroPayoutListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HeroPayoutListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
    }

    private void initViews()
    {
        payoutDate = (TextView) findViewById(R.id.payout_date);
        payoutAmount = (TextView) findViewById(R.id.payout_amount);
    }

    public void display(HeroPayoutDTO payoutDTO)
    {
        linkWith(payoutDTO, true);
    }

    public void linkWith(HeroPayoutDTO payoutDTO, boolean andDisplay)
    {
        this.heroPayoutDTO = payoutDTO;
        if (andDisplay)
        {
            displayPayoutDate();
            displayPayoutAmount();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayPayoutDate();
        displayPayoutAmount();
    }

    public void displayPayoutDate()
    {
        if (payoutDate != null)
        {
            if (heroPayoutDTO != null)
            {
                SimpleDateFormat sdf = new SimpleDateFormat(getContext().getString(R.string.manage_followers_payout_hero_datetime_format));
                payoutDate.setText(sdf.format(heroPayoutDTO.payoutDateTimeUtc));
            }
            else
            {
                payoutDate.setText(R.string.na);
            }
        }
    }

    public void displayPayoutAmount()
    {
        if (payoutAmount != null)
        {
            if (heroPayoutDTO != null)
            {
                payoutAmount.setText(String.format("%s %,.2f", SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, heroPayoutDTO.usd_NetValueToHero));
            }
            else
            {
                payoutAmount.setText(R.string.na);
            }
        }
    }
    //</editor-fold>
}
