package com.tradehero.th.fragments;

import android.support.annotation.Nullable;

public interface MovableBottom
{
    void animateShow();
    void animateHide();
    void setOnMovableBottomTranslateListener(@Nullable OnMovableBottomTranslateListener listener);
}
