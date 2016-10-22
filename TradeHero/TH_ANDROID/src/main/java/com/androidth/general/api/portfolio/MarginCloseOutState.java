package com.androidth.general.api.portfolio;

import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.StringRes;
import com.androidth.general.R;

public enum MarginCloseOutState
{
    HEALTHY(
            R.dimen.position_margin_close_out_zero,
            R.dimen.position_margin_close_out_ok,
            R.color.position_margin_close_out_ok_start,
            R.color.position_margin_close_out_ok_end,
            R.string.position_margin_close_out_ok),
    WARN(
            R.dimen.position_margin_close_out_ok,
            R.dimen.position_margin_close_out_warn,
            R.color.position_margin_close_out_warn_start,
            R.color.position_margin_close_out_warn_end,
            R.string.position_margin_close_out_warn),
    DANGER(
            R.dimen.position_margin_close_out_warn,
            R.dimen.position_margin_close_out_danger_one,
            R.color.position_margin_close_out_danger_start,
            R.color.position_margin_close_out_danger_end,
            R.string.position_margin_close_out_danger),
    ;

    @DimenRes public final int lowerBoundResId;
    @DimenRes public final int upperBoundResId;
    @ColorRes public final int colorResIdPulseStart;
    @ColorRes public final int colorResIdPulseEnd;
    @StringRes public final int labelResId;

    MarginCloseOutState(
            @DimenRes int lowerBoundResId,
            @DimenRes int upperBoundResId,
            @ColorRes int colorResIdPulseStart,
            @ColorRes int colorResIdPulseEnd,
            @StringRes int labelResId)
    {
        this.lowerBoundResId = lowerBoundResId;
        this.upperBoundResId = upperBoundResId;
        this.colorResIdPulseStart = colorResIdPulseStart;
        this.colorResIdPulseEnd = colorResIdPulseEnd;
        this.labelResId = labelResId;
    }
}
