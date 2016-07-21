package com.androidth.general.fragments.onboarding.hero;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.Unbinder;

import com.squareup.picasso.Picasso;
import com.androidth.general.common.api.SelectableDTO;
import com.androidth.general.R;
import com.androidth.general.api.leaderboard.LeaderboardDTO;
import com.androidth.general.api.leaderboard.LeaderboardUserDTO;
import com.androidth.general.fragments.onboarding.OnBoardSelectableViewLinear;
import com.androidth.general.fragments.timeline.UserStatisticView;
import com.androidth.general.models.number.THSignedPercentage;
import javax.inject.Inject;

public class OnBoardUserItemView extends OnBoardSelectableViewLinear<LeaderboardUserDTO, OnBoardUserItemView.DTO>
{
    @DrawableRes private static final int DEFAULT_EXCHANGE_LOGO = R.drawable.superman_facebook;

    @Inject Picasso picasso;

    @BindView(android.R.id.icon1) ImageView image;
    @BindView(android.R.id.text1) TextView shortNameView;
    @BindView(android.R.id.text2) TextView descView;
    @BindView(R.id.lbmu_roi) TextView lbmuRoi;
    @BindView(R.id.user_statistic_view) UserStatisticView userStatisticView;

    private Unbinder unbinder;

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
        unbinder.unbind();
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull DTO dto)
    {
        super.display(dto);

        if (image != null)
        {
            picasso.cancelRequest(image);
            if (dto.value.picture == null)
            {
                image.setImageResource(DEFAULT_EXCHANGE_LOGO);
            }
            else
            {
                picasso.load(dto.value.picture)
                        .into(image);
            }
        }

        if (shortNameView != null)
        {
            shortNameView.setText(dto.value.displayName);
        }

        if (descView != null)
        {
            descView.setText(dto.value.criteria);
        }

        if (lbmuRoi != null)
        {
            lbmuRoi.setText(dto.lbmuRoi);
        }

        if (userStatisticView != null)
        {
            userStatisticView.setVisibility(VISIBLE);
            userStatisticView.display(dto.userStatisticsDto);
        }
    }

    public static class DTO extends SelectableDTO<LeaderboardUserDTO>
    {
        @NonNull public final UserStatisticView.DTO userStatisticsDto;
        @NonNull public final Spanned lbmuRoi;

        public DTO(@NonNull Resources resources,
                @NonNull LeaderboardUserDTO leaderboardUserDTO,
                @Nullable LeaderboardDTO mostSkilledLeaderboardDTO)
        {
            this(resources, leaderboardUserDTO, DEFAULT_SELECTED, mostSkilledLeaderboardDTO);
        }

        public DTO(@NonNull Resources resources,
                @NonNull LeaderboardUserDTO leaderboardUserDTO,
                boolean selected,
                @Nullable LeaderboardDTO mostSkilledLeaderboardDTO)
        {
            super(leaderboardUserDTO, selected);
            this.userStatisticsDto = new UserStatisticView.DTO(resources, leaderboardUserDTO, mostSkilledLeaderboardDTO);
            this.lbmuRoi = THSignedPercentage
                    .builder(leaderboardUserDTO.roiInPeriod * 100)
                    .withSign()
                    .signTypeArrow()
                    .relevantDigitCount(3)
                    .withDefaultColor()
                    .build()
                    .createSpanned();
        }
    }
}
