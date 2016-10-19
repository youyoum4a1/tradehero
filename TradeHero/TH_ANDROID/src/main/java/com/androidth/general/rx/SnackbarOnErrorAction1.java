package com.androidth.general.rx;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import retrofit.RetrofitError;
import rx.functions.Action1;

public class SnackbarOnErrorAction1 implements Action1<Throwable> {

    @Nullable
    private final String message;
    private View view;
    private int duration;

    public SnackbarOnErrorAction1(View view, String message, int duration){
        this.view = view;
        this.message = message;
        this.duration = duration;
    }

    @Override
    public void call(Throwable throwable) {
        Snackbar snackbar;
        if(throwable!=null){
            String errorMessage = throwable.getLocalizedMessage();
            if(throwable instanceof RetrofitError){
                RetrofitError retrofitError = (RetrofitError) throwable;
                if(retrofitError.getResponse()!=null){
                    int status = retrofitError.getResponse().getStatus();
                    switch(status){
                        case 401:
                            errorMessage = "Invalid username/password";
                            break;
                        default:
                            errorMessage = "Pleas try again!";
                            break;
                    }
                }
            }
            snackbar = Snackbar.make(view, errorMessage, duration);
        }else{
            snackbar = Snackbar.make(view, message, duration);
        }
        snackbar.show();
    }
}
