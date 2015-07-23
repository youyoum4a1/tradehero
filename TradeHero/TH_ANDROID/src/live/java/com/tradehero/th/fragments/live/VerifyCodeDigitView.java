package com.tradehero.th.fragments.live;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.models.sms.SMSSentConfirmationDTO;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.utils.DeviceUtil;
import rx.Observable;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func4;
import rx.internal.util.SubscriptionList;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class VerifyCodeDigitView extends ScrollView
        implements DTOView<VerifyCodeDigitView.Requisite>
{
    @Bind({
            R.id.verify_code_1,
            R.id.verify_code_2,
            R.id.verify_code_3,
            R.id.verify_code_4
    }) EditText[] codeViews;
    @Bind(R.id.btn_verify_phone) View buttonVerify;
    @Bind(R.id.btn_send_code) View buttonResend;
    @Bind(R.id.sms_sent_description) TextView sentDescription;
    @Bind(R.id.sms_sent_status) TextView sentStatus;

    @NonNull private final BehaviorSubject<Requisite> requisiteSubject;
    @NonNull private final BehaviorSubject<String> typedCodeSubject;
    @NonNull private final PublishSubject<UserAction> userActionSubject;
    private SubscriptionList clickSubscriptions;

    //<editor-fold desc="Constructors">
    public VerifyCodeDigitView(Context context)
    {
        super(context);
        requisiteSubject = BehaviorSubject.create();
        typedCodeSubject = BehaviorSubject.create();
        userActionSubject = PublishSubject.create();
    }

    public VerifyCodeDigitView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        requisiteSubject = BehaviorSubject.create();
        typedCodeSubject = BehaviorSubject.create();
        userActionSubject = PublishSubject.create();
    }

    public VerifyCodeDigitView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        requisiteSubject = BehaviorSubject.create();
        typedCodeSubject = BehaviorSubject.create();
        userActionSubject = PublishSubject.create();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerifyCodeDigitView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        requisiteSubject = BehaviorSubject.create();
        typedCodeSubject = BehaviorSubject.create();
        userActionSubject = PublishSubject.create();
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
        DeviceUtil.showKeyboardDelayed(codeViews[0]);
        codeViews[0].requestFocus();
        clickSubscriptions = new SubscriptionList();
        clickSubscriptions.add(getTypedCodeObservable(codeViews)
                .filter(new Func1<String, Boolean>()
                {
                    @Override public Boolean call(String givenCode)
                    {
                        boolean done = !givenCode.contains(" ");
                        buttonVerify.setEnabled(done);
                        return done;
                    }
                })
                .subscribe(
                        new Action1<String>()
                        {
                            @Override public void call(String code)
                            {
                                typedCodeSubject.onNext(code);
                            }
                        },
                        new TimberAndToastOnErrorAction1("Failed to listen to typed code")));
    }

    @Override protected void onDetachedFromWindow()
    {
        if (clickSubscriptions != null)
        {
            clickSubscriptions.unsubscribe();
        }
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(Requisite dto)
    {
        requisiteSubject.onNext(dto);
        if (sentDescription != null)
        {
            sentDescription.setText(getResources().getString(R.string.sms_verification_description, dto.targetDevice));
        }
        if (sentStatus != null)
        {
            sentStatus.setText(getResources().getString(
                    R.string.sms_verification_status,
                    getResources().getString(dto.confirmationDTO.getStatusStringRes())));
        }
        if (buttonResend != null)
        {
            buttonResend.setEnabled(dto.confirmationDTO.isFinalStatus());
        }
    }

    @NonNull protected Observable<String> getTypedCodeObservable(@NonNull TextView[] codeViews)
    {
        return Observable.combineLatest(
                WidgetObservable.text(codeViews[0]).doOnNext(createCodeViewHandler(1)),
                WidgetObservable.text(codeViews[1]).doOnNext(createCodeViewHandler(2)),
                WidgetObservable.text(codeViews[2]).doOnNext(createCodeViewHandler(3)),
                WidgetObservable.text(codeViews[3]).doOnNext(createCodeViewHandler(0)),
                new Func4<OnTextChangeEvent, OnTextChangeEvent, OnTextChangeEvent, OnTextChangeEvent, String>()
                {
                    @Override public String call(
                            OnTextChangeEvent onTextChangeEvent1,
                            OnTextChangeEvent onTextChangeEvent2,
                            OnTextChangeEvent onTextChangeEvent3,
                            OnTextChangeEvent onTextChangeEvent4)
                    {
                        return "" +
                                (onTextChangeEvent1.text().length() >= 1 ? onTextChangeEvent1.text().charAt(0) : ' ') +
                                (onTextChangeEvent2.text().length() >= 1 ? onTextChangeEvent2.text().charAt(0) : ' ') +
                                (onTextChangeEvent3.text().length() >= 1 ? onTextChangeEvent3.text().charAt(0) : ' ') +
                                (onTextChangeEvent4.text().length() >= 1 ? onTextChangeEvent4.text().charAt(0) : ' ');
                    }
                });
    }

    @NonNull protected Action1<OnTextChangeEvent> createCodeViewHandler(final int indexNextFocus)
    {
        return new Action1<OnTextChangeEvent>()
        {
            @Override public void call(OnTextChangeEvent onTextChangeEvent)
            {
                if (onTextChangeEvent.text().length() > 0)
                {
                    codeViews[indexNextFocus].requestFocus();
                }
            }
        };
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_verify_phone)
    protected void onVerifyClicked(View button)
    {
        clickSubscriptions.add(typedCodeSubject.take(1).subscribe(
                new Action1<String>()
                {
                    @Override public void call(String code)
                    {
                        userActionSubject.onNext(new UserActionVerify(code));
                    }
                },
                new TimberOnErrorAction1("Failed to pass on typed code")));
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_send_code)
    protected void onResendClicked(View button)
    {
        userActionSubject.onNext(new UserActionResend());
    }

    @SuppressWarnings("unused")
    @OnClick(android.R.id.closeButton)
    protected void onCloseClicked(View button)
    {
        userActionSubject.onNext(new UserActionDismiss());
    }

    @NonNull public Observable<UserAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    public static class Requisite
    {
        @NonNull public final String targetDevice;
        @NonNull public final String expectedCode;
        @NonNull public final SMSSentConfirmationDTO confirmationDTO;

        public Requisite(@NonNull String targetDevice, @NonNull String expectedCode, @NonNull SMSSentConfirmationDTO confirmationDTO)
        {
            this.targetDevice = targetDevice;
            this.expectedCode = expectedCode;
            this.confirmationDTO = confirmationDTO;
        }
    }

    public interface UserAction
    {
    }

    public static class UserActionVerify implements UserAction
    {
        @NonNull public final String code;

        public UserActionVerify(@NonNull String code)
        {
            this.code = code;
        }
    }

    public static class UserActionResend implements UserAction
    {
    }

    public static class UserActionDismiss implements UserAction
    {
    }
}
