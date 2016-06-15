package com.androidth.general.rx;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import rx.functions.Action1;

/**
 * Created by ayushnvijay on 6/14/16.
 */
public class SnackbarOnErrorAction1 implements Action1<Throwable> {

    @Nullable
    private final String message;
    private View view;
    private int duration;

    public SnackbarOnErrorAction1( View view,String message, int duration){
        this.message = message;
        this.view = view;
        this.duration = duration;
    }

    @Override
    public void call(Throwable throwable) {
        if(message!=null){
            Snackbar snack = Snackbar.make(view,message,duration);
            snack.show();

        }
        else {
            Snackbar snack = Snackbar.make(view,"Error",duration);
            snack.show();
        }
    }
}
