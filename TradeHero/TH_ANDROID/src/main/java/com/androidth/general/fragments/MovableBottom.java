package com.androidth.general.fragments;

import android.support.annotation.Nullable;

public interface MovableBottom
{
    void animateShow();
    void animateHide();
    void setOnMovableBottomTranslateListener(@Nullable OnMovableBottomTranslateListener listener);
    int getHeight();

    // hack to temporary fix flicker on PositionListFragment
    void setBottomBarVisibility(int visibility);
}
