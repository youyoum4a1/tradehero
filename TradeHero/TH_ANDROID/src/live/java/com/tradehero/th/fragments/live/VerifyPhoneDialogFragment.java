package com.tradehero.th.fragments.live;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.models.sms.SMSId;
import com.tradehero.th.models.sms.SMSRequestFactory;
import com.tradehero.th.models.sms.SMSSentConfirmationDTO;
import com.tradehero.th.models.sms.SMSServiceWrapper;
import com.tradehero.th.models.sms.empty.EmptySMSSentConfirmationDTO;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func4;
import rx.internal.util.SubscriptionList;
import rx.subjects.BehaviorSubject;

public class VerifyPhoneDialogFragment extends BaseDialogFragment
{
    private static final String KEY_BUNDLE_EXPECTED = VerifyPhoneDialogFragment.class.getName() + ".expectedCode";
    private static final String KEY_BUNDLE_DIALING_PREFIX = VerifyPhoneDialogFragment.class.getName() + ".dialingPrefix";
    private static final String KEY_BUNDLE_PHONE_NUMBER = VerifyPhoneDialogFragment.class.getName() + ".phoneNumber";

    private static final String BUNDLE_KEY_VERIFIED_NUMBER = VerifyPhoneDialogFragment.class.getName() + ".verifiedNumber";

    private static final long DEFAULT_POLL_INTERVAL_MILLISEC = 1000;

    @Inject SMSServiceWrapper smsServiceWrapper;

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

    private BehaviorSubject<SMSSentConfirmationDTO> mSMSConfirmationSubject;

    private int mDialingPrefix;
    private String mExpectedCode;
    private String mPhoneNumber;
    private SubscriptionList onDestroyViewSubscriptions;
    private String mFormattedNumber;
    private Subscription smsSubscription;

    public static String getFormattedPhoneNumber(int dialingPrefix, String phoneNumber)
    {
        //TODO perhaps, move this to a helper class
        return "+" + dialingPrefix + phoneNumber;
    }

    private static VerifyPhoneDialogFragment newInstance(int dialingPrefix, String phoneNumber)
    {
        String expectedCode = String.format("%04d", Math.abs(new Random(System.nanoTime()).nextInt() % 10000));
        Bundle b = new Bundle();
        b.putString(KEY_BUNDLE_EXPECTED, expectedCode);
        b.putInt(KEY_BUNDLE_DIALING_PREFIX, dialingPrefix);
        b.putString(KEY_BUNDLE_PHONE_NUMBER, phoneNumber);
        VerifyPhoneDialogFragment fragment = new VerifyPhoneDialogFragment();
        fragment.setArguments(b);
        return fragment;
    }

    public static VerifyPhoneDialogFragment show(int requestCode, Fragment targetFragment, int dialingPrefix, String phoneNumber)
    {
        VerifyPhoneDialogFragment vdf = newInstance(dialingPrefix, phoneNumber);
        vdf.setTargetFragment(targetFragment, requestCode);
        vdf.show(targetFragment.getChildFragmentManager(), vdf.getClass().getName());
        return vdf;
    }

    private static void crateVerifiedBundle(@NonNull Intent i, int dialingPrefix, String phoneNumber)
    {
        Bundle b = new Bundle();
        b.putInt(KEY_BUNDLE_DIALING_PREFIX, dialingPrefix);
        b.putString(KEY_BUNDLE_PHONE_NUMBER, phoneNumber);
        i.putExtra(BUNDLE_KEY_VERIFIED_NUMBER, b);
    }

    @Nullable public static Pair<Integer, String> getVerifiedFromIntent(Intent data)
    {
        Bundle b = data.getBundleExtra(BUNDLE_KEY_VERIFIED_NUMBER);
        int dialingPrefix = b.getInt(KEY_BUNDLE_DIALING_PREFIX);
        String phoneNumber = b.getString(KEY_BUNDLE_PHONE_NUMBER);
        return Pair.create(dialingPrefix, phoneNumber);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mExpectedCode = bundle.getString(KEY_BUNDLE_EXPECTED);
        mDialingPrefix = bundle.getInt(KEY_BUNDLE_DIALING_PREFIX);
        mPhoneNumber = bundle.getString(KEY_BUNDLE_PHONE_NUMBER);
        mFormattedNumber = getFormattedPhoneNumber(mDialingPrefix, mPhoneNumber);

        mSMSConfirmationSubject = BehaviorSubject.create();
        smsSubscription = createSMSSubscription();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.dialog_fragment_verify_phone_number, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        sentDescription.setText(getResources().getString(R.string.sms_verification_description, mFormattedNumber));

        onDestroyViewSubscriptions = new SubscriptionList();

        onDestroyViewSubscriptions.add(
                mSMSConfirmationSubject
                        .subscribe(new Action1<SMSSentConfirmationDTO>()
                                   {
                                       @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                                       {
                                           sentStatus.setText(getResources().getString(
                                                   R.string.sms_verification_status,
                                                   getResources().getString(
                                                           smsSentConfirmationDTO.getStatusStringRes())));

                                           buttonResend.setEnabled(smsSentConfirmationDTO.isFinalStatus());
                                       }
                                   },
                                new TimberOnErrorAction1("Failed on listening to sms update")
                        ));

        onDestroyViewSubscriptions.add(
                getTypedCodeObservable(codeViews)
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
                                        validateAgainstExpected(code);
                                    }
                                },
                                new TimberAndToastOnErrorAction1("Failed to listen to typed code")));

        onDestroyViewSubscriptions.add(
                ViewObservable.clicks(buttonVerify)
                        .withLatestFrom(getTypedCodeObservable(codeViews), new Func2<OnClickEvent, String, String>()
                        {
                            @Override public String call(OnClickEvent onClickEvent, String s)
                            {
                                return s;
                            }
                        })
                        .subscribe(new Action1<String>()
                        {
                            @Override public void call(String code)
                            {
                                validateAgainstExpected(code);
                            }
                        })
        );
    }

    protected Subscription createSMSSubscription()
    {
        return smsServiceWrapper.sendMessage(
                SMSRequestFactory.create(
                        mFormattedNumber,
                        getString(R.string.sms_verification_sms_content, mExpectedCode)))
                .flatMap(new Func1<SMSSentConfirmationDTO, Observable<SMSSentConfirmationDTO>>()
                {
                    @Override public Observable<SMSSentConfirmationDTO> call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                    {
                        return createRepeatableSMSConfirmation(smsSentConfirmationDTO.getSMSId())
                                .startWith(smsSentConfirmationDTO);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends SMSSentConfirmationDTO>>()
                {
                    @Override public Observable<? extends SMSSentConfirmationDTO> call(final Throwable throwable)
                    {
                        String message = throwable.getMessage();
                        if (TextUtils.isEmpty(message))
                        {
                            message = getString(R.string.sms_verification_send_fail);
                        }
                        return AlertDialogRxUtil.build(getActivity())
                                .setTitle(R.string.sms_verification_send_fail_title)
                                .setMessage(message)
                                .setNegativeButton(R.string.ok)
                                .build()
                                .flatMap(new Func1<OnDialogClickEvent, Observable<SMSSentConfirmationDTO>>()
                                {
                                    @Override public Observable<SMSSentConfirmationDTO> call(OnDialogClickEvent clickEvent)
                                    {
                                        return Observable.error(throwable);
                                    }
                                });
                    }
                })
                .startWith(new EmptySMSSentConfirmationDTO(mFormattedNumber, "Fake", R.string.sms_verification_button_empty_submitting))
                .subscribe(new Action1<SMSSentConfirmationDTO>()
                           {
                               @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                               {
                                   mSMSConfirmationSubject.onNext(smsSentConfirmationDTO);
                               }
                           },
                        new TimberOnErrorAction1("Failed on sending sms message"));
    }

    @Override public void onDestroy()
    {
        smsSubscription.unsubscribe();
        super.onDestroy();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_send_code)
    protected void onResendClicked(View button)
    {
        buttonResend.setEnabled(false);
        if (smsSubscription != null)
        {
            smsSubscription.unsubscribe();
        }

        smsSubscription = createSMSSubscription();
    }

    @SuppressWarnings("unused")
    @OnClick(android.R.id.closeButton)
    protected void onCloseClicked(View button)
    {
        dismiss();
    }

    private void validateAgainstExpected(String toBeValidated)
    {
        if (toBeValidated.equals(mExpectedCode))
        {
            dismissWithResult();
        }
        else
        {
            THToast.show(R.string.sms_verification_not_match);
        }
    }

    private void dismissWithResult()
    {
        Intent i = new Intent();
        VerifyPhoneDialogFragment.crateVerifiedBundle(i, mDialingPrefix, mPhoneNumber);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        dismiss();
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

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        onDestroyViewSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @NonNull protected Observable<SMSSentConfirmationDTO> createRepeatableSMSConfirmation(@NonNull final SMSId smsId)
    {
        return smsServiceWrapper.getMessageStatus(smsId)
                .delaySubscription(DEFAULT_POLL_INTERVAL_MILLISEC, TimeUnit.MILLISECONDS)
                .flatMap(new Func1<SMSSentConfirmationDTO, Observable<SMSSentConfirmationDTO>>()
                {
                    @Override public Observable<SMSSentConfirmationDTO> call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                    {
                        if (smsSentConfirmationDTO.isFinalStatus())
                        {
                            return Observable.just(smsSentConfirmationDTO);
                        }
                        return createRepeatableSMSConfirmation(smsId);
                    }
                });
    }
}
