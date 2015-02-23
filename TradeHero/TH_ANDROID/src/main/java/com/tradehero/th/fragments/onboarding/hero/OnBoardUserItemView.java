package com.tradehero.th.fragments.onboarding.hero;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.api.SelectableDTO;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.fragments.onboarding.OnBoardSelectableViewLinear;
import com.tradehero.th.fragments.timeline.UserStatisticView;
import com.tradehero.th.models.number.THSignedPercentage;
import javax.inject.Inject;

public class OnBoardUserItemView extends OnBoardSelectableViewLinear<LeaderboardUserDTO>
{
    @DrawableRes private static final int DEFAULT_EXCHANGE_LOGO = R.drawable.superman_facebook;

    @Inject Picasso picasso;

    @InjectView(android.R.id.icon1) ImageView image;
    @InjectView(android.R.id.text1) TextView shortNameView;
    @InjectView(android.R.id.text2) TextView descView;
    @InjectView(R.id.lbmu_roi) TextView lbmuRoi;
    @InjectView(R.id.user_statistic_view) UserStatisticView userStatisticView;

    //<editor-fold desc="Constructors">
    public OnBoardUserItemView(Context context)
    {
        super(context);
    }

    public OnBoardUserItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OnBoardUserItemView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    //</editor-fold>

    @Override protected void onDetachedFromWindow()
    {
        if (image != null)
        {
            picasso.cancelRequest(image);
        }
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull SelectableDTO<LeaderboardUserDTO> dto)
    {
        super.display(dto);
        display(dto.value);
    }

    void display(@Nullable LeaderboardUserDTO dto)
    {
        if (image != null)
        {
            picasso.cancelRequest(image);
            if (dto == null || dto.picture == null)
            {
                image.setImageResource(DEFAULT_EXCHANGE_LOGO);
            }
            else
            {
                picasso.load(dto.picture)
                        .into(image);
            }
        }

        if (shortNameView != null)
        {
            if (dto == null)
            {
                shortNameView.setText(R.string.na);
            }
            else
            {
                shortNameView.setText(dto.displayName);
            }
        }

        if (descView != null)
        {
            if (dto == null)
            {
                descView.setText("");
            }
            else
            {
                descView.setText("TBD");
            }
        }

        if (lbmuRoi != null)
        {
            if (dto != null)
            {
                THSignedPercentage
                        .builder(dto.roiInPeriod * 100)
                        .withSign()
                        .signTypeArrow()
                        .relevantDigitCount(3)
                        .withDefaultColor()
                        .build()
                        .into(lbmuRoi);
            }
            else
            {
                lbmuRoi.setText(R.string.na);
            }

        }

        if (userStatisticView != null)
        {
            userStatisticView.setVisibility(dto == null ? GONE : VISIBLE);
            if (dto != null)
            {
                userStatisticView.display(dto);
            }
        }
    }
}
