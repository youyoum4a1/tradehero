package com.androidth.general.fragments.onboarding;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.exception.THException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class OnBoardHeaderLinearView extends LinearLayout
{
    @Bind(R.id.btn_retry) protected TextView buttonRetry;

    @NonNull protected PublishSubject<Boolean> clickedRetrySubject;

    //<editor-fold desc="Constructors">
    public OnBoardHeaderLinearView(Context context)
    {
        super(context);
        this.clickedRetrySubject = PublishSubject.create();
    }

    public OnBoardHeaderLinearView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.clickedRetrySubject = PublishSubject.create();
    }

    public OnBoardHeaderLinearView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.clickedRetrySubject = PublishSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    public void displayRetry(boolean failed)
    {
        buttonRetry.setEnabled(failed);
        buttonRetry.setText(failed ? R.string.on_board_tap_retry : R.string.on_board_retry_success);
        buttonRetry.setVisibility(failed ? VISIBLE : GONE);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_retry)
    protected void onButtonRetryClicked(View view)
    {
        buttonRetry.setText(R.string.on_board_retrying);
        buttonRetry.setEnabled(false);
        clickedRetrySubject.onNext(true);
    }

    @NonNull public Func1<Observable<? extends Throwable>, Observable<?>> isRetryClickedAfterFailed()
    {
        return new Func1<Observable<? extends Throwable>, Observable<?>>()
        {
            @Override public Observable<?> call(Observable<? extends Throwable> errors)
            {
                return errors
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Func1<Throwable, Observable<?>>()
                        {
                            @Override public Observable<?> call(Throwable throwable)
                            {
                                displayRetry(true);
                                THToast.show(throwable instanceof THException ? (THException) throwable : new THException(throwable));
                                return clickedRetrySubject.asObservable();
                            }
                        });
            }
        };
    }
}
