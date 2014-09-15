package com.tradehero.th.fragments.leaderboard.filter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th2.R;
import com.tradehero.th.models.number.THSignedNumber;

public class MinConsistencyLeaderboardFilterValueSlider extends LeaderboardFilterValueSlider
{
    @InjectView(R.id.leaderboard_filter_max) TextView maxValueText;

    //<editor-fold desc="Constructors">
    public MinConsistencyLeaderboardFilterValueSlider(Context context)
    {
        super(context);
    }

    public MinConsistencyLeaderboardFilterValueSlider(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MinConsistencyLeaderboardFilterValueSlider(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    void setMaxValue(float maxValue)
    {
        this.maxValue = maxValue;
        this.maxValueText.setText(getMaxValueText());
    }

    protected String getMaxValueText()
    {
        THSignedNumber signedNumber = THSignedNumber.builder((double) maxValue)
                .withOutSign()
                .build();
        return signedNumber.toString();
    }

    @Override protected String getCurrentValueText()
    {
        THSignedNumber signedNumber = THSignedNumber.builder((double) currentValue)
                .withOutSign()
                .build();
        return signedNumber.toString();
    }
}
