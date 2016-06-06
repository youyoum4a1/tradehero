package com.androidth.general.widget.animation;

import android.animation.Animator;
import android.support.annotation.NonNull;

public class AnimatorStateEvent
{
    @NonNull public final Animator animator;
    @NonNull public final AnimatorState state;

    //<editor-fold desc="Constructors">
    public AnimatorStateEvent(@NonNull Animator animator, @NonNull AnimatorState state)
    {
        this.animator = animator;
        this.state = state;
    }
    //</editor-fold>
}
