package com.tradehero.th.fragments.leaderboard.filter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;

/**
 * Created by xavier on 2/12/14.
 */
public class LeaderboardFilterSliderContainer extends LinearLayout
{
    public static final String TAG = LeaderboardFilterSliderContainer.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public LeaderboardFilterSliderContainer(Context context)
    {
        super(context);
    }

    public LeaderboardFilterSliderContainer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardFilterSliderContainer(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @InjectView(R.id.leaderboard_filter_monthly_activity_container) protected LeaderboardFilterValueSlider monthlyActivityView;
    @InjectView(R.id.leaderboard_filter_win_ratio_container) protected LeaderboardFilterValueSlider winRatioView;
    @InjectView(R.id.leaderboard_filter_holding_period_container) protected LeaderboardFilterValueSlider holdingPeriodView;

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public PerPagedFilteredLeaderboardKey getFilteredLeaderboardKey()
    {
        return new PerPagedFilteredLeaderboardKey(
                null, // To be replaced with Key
                0, // Page but we don't care
                0, // PerPage but we don't care
                (float) winRatioView.getCurrentValue(),
                (float) monthlyActivityView.getCurrentValue(),
                (float) holdingPeriodView.getCurrentValue(),
                null, // MinSharpeRatio but we don't care
                null // MaxPosRoiVolatility but we don't care
        );
    }
}
