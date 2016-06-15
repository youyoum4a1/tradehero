package com.android.common;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by ayushnvijay on 6/15/16.
 */
public class LinearLayoutBehavior extends CoordinatorLayout.Behavior<LinearLayout> {
    public LinearLayoutBehavior(Context context, AttributeSet attrs){

    }
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency){
        return dependency instanceof Snackbar.SnackbarLayout;
    }
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency){
        float translationY =  Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }
}
