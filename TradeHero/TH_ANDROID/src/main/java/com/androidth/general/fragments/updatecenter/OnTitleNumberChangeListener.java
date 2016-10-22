package com.androidth.general.fragments.updatecenter;

import android.support.annotation.NonNull;

public interface OnTitleNumberChangeListener
{
    void onTitleNumberChanged(@NonNull UpdateCenterTabType tabType, int number);
}
