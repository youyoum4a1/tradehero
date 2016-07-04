package com.androidth.general.fragments.live;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.fragments.base.BaseDialogFragment;
import com.androidth.general.fragments.live.ayondo.LiveSignUpStep1AyondoFragment;
import com.androidth.general.models.sms.SMSId;
import com.androidth.general.models.sms.SMSRequestFactory;
import com.androidth.general.models.sms.SMSSentConfirmationDTO;
import com.androidth.general.models.sms.empty.EmptySMSSentConfirmationDTO;
import com.androidth.general.models.sms.twilio.TwilioSMSId;
import com.androidth.general.network.service.LiveServiceWrapper;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.dialog.OnDialogClickEvent;
import com.androidth.general.utils.AlertDialogRxUtil;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;

public class VerifyEmailDialogFragment extends BaseDialogFragment
{
    private static final String BUNDLE_KEY_VERIFIED_EMAIL = VerifyEmailDialogFragment.class.getName() + ".verifiedEmail";
    private static final String KEY_BUNDLE_EMAIL_ADDRESS = VerifyEmailDialogFragment.class.getName() + ".emailAddress";
    private static final String KEY_BUNDLE_USER_ID = VerifyEmailDialogFragment.class.getName() + ".userId";
    private static final String KEY_BUNDLE_PROVIDER_ID = VerifyEmailDialogFragment.class.getName() + ".providerId";

    private static final long DEFAULT_POLL_INTERVAL_MILLISEC = 1000;

    @Inject LiveServiceWrapper liveServiceWrapper;
    @Inject Picasso picasso;

    @Bind(R.id.email_sent_go_to_inbox_button) Button goToInboxButton;
    @Bind(R.id.email_sent_resend_button) Button resendButton;
    @Bind(R.id.email_sent_ask_resend) TextView resendDescription;
    @Bind(R.id.email_sent_description) TextView sentDescription;
    @Bind(R.id.email_sent_recipient) TextView recipient;
    @Bind(R.id.email_sent_banner) ImageView bannerImageView;

//    private BehaviorSubject<SMSSentConfirmationDTO> mSMSConfirmationSubject;

//    private int mDialingPrefix;
//    private String mExpectedCode;
    private String emailAddress;
    private int userId, providerId;
    private SubscriptionList onDestroyViewSubscriptions;
//    private String mFormattedNumber;
    private Subscription emailSubscription;

//    public static String getFormattedPhoneNumber(int dialingPrefix, String phoneNumber)
//    {
//        //TODO perhaps, move this to a helper class
//        return "+" + dialingPrefix + phoneNumber;
//    }

    private static VerifyEmailDialogFragment newInstance(int userId, String emailAddress, int providerId)
    {
        Bundle b = new Bundle();
        b.putString(KEY_BUNDLE_EMAIL_ADDRESS, emailAddress);
        b.putInt(KEY_BUNDLE_USER_ID, userId);
        b.putInt(KEY_BUNDLE_PROVIDER_ID, providerId);
        VerifyEmailDialogFragment fragment = new VerifyEmailDialogFragment();
        fragment.setArguments(b);
        return fragment;
    }

    public static VerifyEmailDialogFragment show(int requestCode, Fragment targetFragment, int userId, String emailAddress, int providerId)
    {
        VerifyEmailDialogFragment vdf = newInstance(userId, emailAddress, providerId);
        vdf.setTargetFragment(targetFragment, requestCode);
        vdf.show(targetFragment.getChildFragmentManager(), vdf.getClass().getName());
        return vdf;
    }

    private static void crateVerifiedBundle(@NonNull Intent i, int userId, String emailAddress, int providerId)
    {
        Bundle b = new Bundle();
        b.putInt(KEY_BUNDLE_USER_ID, userId);
        b.putInt(KEY_BUNDLE_PROVIDER_ID, providerId);
        b.putString(KEY_BUNDLE_EMAIL_ADDRESS, emailAddress);
        i.putExtra(BUNDLE_KEY_VERIFIED_EMAIL, b);
    }

    @Nullable public static String getVerifiedFromIntent(Intent data)
    {
        Bundle b = data.getBundleExtra(BUNDLE_KEY_VERIFIED_EMAIL);
        int uId = b.getInt(KEY_BUNDLE_USER_ID);
        String emailAd = b.getString(KEY_BUNDLE_EMAIL_ADDRESS);
        return emailAd;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        emailAddress = bundle.getString(KEY_BUNDLE_EMAIL_ADDRESS);
        userId = bundle.getInt(KEY_BUNDLE_USER_ID);
        providerId = bundle.getInt(KEY_BUNDLE_PROVIDER_ID);

//        mPhoneNumber = bundle.getString(KEY_BUNDLE_PHONE_NUMBER);
//        mFormattedNumber = getFormattedPhoneNumber(mDialingPrefix, mPhoneNumber);

//        mSMSConfirmationSubject = BehaviorSubject.create();

//        emailSubscription = getEmailSubscription();
//        if (emailSubscription == null)
//        {
//            emailSubscription = createEmailSubscription();
//        }

        emailSubscription = createEmailSubscription();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.dialog_fragment_verify_email, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        sentDescription.setText("A confirmation email has been sent to your mailbox. Click on the confirmation link in the email to complete verification");
        resendDescription.setText("Didn't receive a confirmation email?");
        recipient.setText(emailAddress);

//        picasso.load()

//        onDestroyViewSubscriptions = new SubscriptionList();
//
//        onDestroyViewSubscriptions.add(
//                mSMSConfirmationSubject
//                        .subscribe(new Action1<SMSSentConfirmationDTO>()
//                                   {
//                                       @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
//                                       {
//                                           sentStatus.setText(getResources().getString(
//                                                   R.string.sms_verification_status,
//                                                   getResources().getString(
//                                                           smsSentConfirmationDTO.getStatusStringRes())));
//
//                                           buttonResend.setEnabled(smsSentConfirmationDTO.isFinalStatus());
//                                       }
//                                   },
//                                new TimberOnErrorAction1("Failed on listening to sms update")
//                        ));
//
//        onDestroyViewSubscriptions.add(
//                getTypedCodeObservable(codeViews)
//                        .filter(new Func1<String, Boolean>()
//                        {
//                            @Override public Boolean call(String givenCode)
//                            {
//                                boolean done = !givenCode.contains(" ");
//                                buttonVerify.setEnabled(done);
//                                return done;
//                            }
//                        })
//                        .subscribe(
//                                new Action1<String>()
//                                {
//                                    @Override public void call(String code)
//                                    {
//                                        validateAgainstExpected(code);
//                                    }
//                                },
//                                new TimberAndToastOnErrorAction1("Failed to listen to typed code")));
//
//        onDestroyViewSubscriptions.add(
//                ViewObservable.clicks(goToInboxButton)
//                        .withLatestFrom(getTypedCodeObservable(codeViews), new Func2<OnClickEvent, String, String>()
//                        {
//                            @Override public String call(OnClickEvent onClickEvent, String s)
//                            {
//                                return s;
//                            }
//                        })
//                        .subscribe(new Action1<String>()
//                        {
//                            @Override public void call(String code)
//                            {
//                                validateAgainstExpected(code);
//                            }
//                        })
//        );
    }

    protected Subscription createEmailSubscription()
    {
        return liveServiceWrapper.verifyEmail(userId, emailAddress, providerId).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {

                Log.v(getTag(), "JEFF SUCCESS EMAIL "+aBoolean);
            }
        },
                new TimberOnErrorAction1("Failed on sending sms message"));




//                sendMessage(
//                SMSRequestFactory.create(
//                        mFormattedNumber,
//                        getString(R.string.sms_verification_sms_content, mExpectedCode)))
//                .doOnNext(new Action1<SMSSentConfirmationDTO>()
//                {
//                    @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
//                    {
//                        if (smsSentConfirmationDTO.getSMSId() instanceof TwilioSMSId)
//                        {
//                            LiveSignUpStep1AyondoFragment liveSignUpStep1AyondoFragment;
//
//                            if (getParentFragment() instanceof LiveSignUpStep1AyondoFragment)
//                            {
//                                liveSignUpStep1AyondoFragment = (LiveSignUpStep1AyondoFragment) getParentFragment();
//                                liveSignUpStep1AyondoFragment.setSmsId(((TwilioSMSId) smsSentConfirmationDTO.getSMSId()).id);
//                            }
//                        }
//                    }
//                })
//                .flatMap(new Func1<SMSSentConfirmationDTO, Observable<SMSSentConfirmationDTO>>()
//                {
//                    @Override public Observable<SMSSentConfirmationDTO> call(SMSSentConfirmationDTO smsSentConfirmationDTO)
//                    {
//                        return createRepeatableSMSConfirmation(smsSentConfirmationDTO.getSMSId())
//                                .startWith(smsSentConfirmationDTO);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .onErrorResumeNext(new Func1<Throwable, Observable<? extends SMSSentConfirmationDTO>>()
//                {
//                    @Override public Observable<? extends SMSSentConfirmationDTO> call(final Throwable throwable)
//                    {
//                        String message = throwable.getMessage();
//                        if (TextUtils.isEmpty(message))
//                        {
//                            message = getString(R.string.sms_verification_send_fail);
//                        }
//                        return AlertDialogRxUtil.build(getActivity())
//                                .setTitle(R.string.sms_verification_send_fail_title)
//                                .setMessage(message)
//                                .setNegativeButton(R.string.ok)
//                                .build()
//                                .flatMap(new Func1<OnDialogClickEvent, Observable<SMSSentConfirmationDTO>>()
//                                {
//                                    @Override public Observable<SMSSentConfirmationDTO> call(OnDialogClickEvent clickEvent)
//                                    {
//                                        return Observable.error(throwable);
//                                    }
//                                });
//                    }
//                })
//                .startWith(new EmptySMSSentConfirmationDTO(mFormattedNumber, "Fake", R.string.sms_verification_button_empty_submitting))
//                .subscribe(new Action1<SMSSentConfirmationDTO>()
//                           {
//                               @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
//                               {
//                                   mSMSConfirmationSubject.onNext(smsSentConfirmationDTO);
//                               }
//                           },
//                        new TimberOnErrorAction1("Failed on sending sms message"));
    }

//    @Nullable protected Subscription getEmailSubscription()
//    {
//        String id = null;
//
//        if (getParentFragment() instanceof LiveSignUpStep1AyondoFragment)
//        {
//            LiveSignUpStep1AyondoFragment liveSignUpStep1AyondoFragment = (LiveSignUpStep1AyondoFragment) getParentFragment();
//            id = liveSignUpStep1AyondoFragment.getSmsId();
//        }
//
//        //TODO make sure it's Twilio's SMS ID
//        if (!TextUtils.isEmpty(id))
//        {
//            return createRepeatableSMSConfirmation(new TwilioSMSId(id))
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .onErrorResumeNext(new Func1<Throwable, Observable<? extends SMSSentConfirmationDTO>>()
//                    {
//                        @Override public Observable<? extends SMSSentConfirmationDTO> call(final Throwable throwable)
//                        {
//                            String message = throwable.getMessage();
//                            if (TextUtils.isEmpty(message))
//                            {
//                                message = getString(R.string.sms_verification_send_fail);
//                            }
//                            return AlertDialogRxUtil.build(getActivity())
//                                    .setTitle(R.string.sms_verification_send_fail_title)
//                                    .setMessage(message)
//                                    .setNegativeButton(R.string.ok)
//                                    .build()
//                                    .flatMap(new Func1<OnDialogClickEvent, Observable<SMSSentConfirmationDTO>>()
//                                    {
//                                        @Override public Observable<SMSSentConfirmationDTO> call(OnDialogClickEvent clickEvent)
//                                        {
//                                            return Observable.error(throwable);
//                                        }
//                                    });
//                        }
//                    })
//                    .startWith(new EmptySMSSentConfirmationDTO(mFormattedNumber, "Fake", R.string.sms_verification_button_empty_checking))
//                    .subscribe(new Action1<SMSSentConfirmationDTO>()
//                               {
//                                   @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
//                                   {
//                                       mSMSConfirmationSubject.onNext(smsSentConfirmationDTO);
//                                   }
//                               },
//                            new TimberOnErrorAction1("Failed on checking sms message status"));
//        }
//        else
//        {
//            return null;
//        }
//    }

    @Override public void onDestroy()
    {
        emailSubscription.unsubscribe();
        super.onDestroy();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.email_sent_resend_button)
    protected void onResendEmailClicked(Button button)
    {
//        button.setEnabled(false);
        if (emailSubscription != null)
        {
            emailSubscription.unsubscribe();
        }

        emailSubscription = createEmailSubscription();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.email_sent_go_to_inbox_button)
    protected void onGoToInboxClicked(Button button)
    {
        Log.v(getTag(),"Jeff Go to inbox");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        getActivity().startActivity(intent);
//        button.setEnabled(false);
//        if (emailSubscription != null)
//        {
//            emailSubscription.unsubscribe();
//        }
//
//        emailSubscription = createEmailSubscription();
    }

    @SuppressWarnings("unused")
    @OnClick(android.R.id.closeButton)
    protected void onCloseClicked(View button)
    {
        dismiss();
    }

//    private void validateAgainstExpected(String toBeValidated)
//    {
//        if (toBeValidated.equals(mExpectedCode))
//        {
//            dismissWithResult();
//        }
//        else
//        {
//            THToast.show(R.string.sms_verification_not_match);
//        }
//    }

    private void dismissWithResult()
    {
        Intent i = new Intent();
        VerifyEmailDialogFragment.crateVerifiedBundle(i, userId, emailAddress, providerId);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        dismiss();
    }

//    @NonNull protected Observable<String> getTypedCodeObservable(@NonNull TextView[] codeViews)
//    {
//        return Observable.combineLatest(
//                WidgetObservable.text(codeViews[0]).doOnNext(createCodeViewHandler(1)),
//                WidgetObservable.text(codeViews[1]).doOnNext(createCodeViewHandler(2)),
//                WidgetObservable.text(codeViews[2]).doOnNext(createCodeViewHandler(3)),
//                WidgetObservable.text(codeViews[3]).doOnNext(createCodeViewHandler(0)),
//                new Func4<OnTextChangeEvent, OnTextChangeEvent, OnTextChangeEvent, OnTextChangeEvent, String>()
//                {
//                    @Override public String call(
//                            OnTextChangeEvent onTextChangeEvent1,
//                            OnTextChangeEvent onTextChangeEvent2,
//                            OnTextChangeEvent onTextChangeEvent3,
//                            OnTextChangeEvent onTextChangeEvent4)
//                    {
//                        return "" +
//                                (onTextChangeEvent1.text().length() >= 1 ? onTextChangeEvent1.text().charAt(0) : ' ') +
//                                (onTextChangeEvent2.text().length() >= 1 ? onTextChangeEvent2.text().charAt(0) : ' ') +
//                                (onTextChangeEvent3.text().length() >= 1 ? onTextChangeEvent3.text().charAt(0) : ' ') +
//                                (onTextChangeEvent4.text().length() >= 1 ? onTextChangeEvent4.text().charAt(0) : ' ');
//                    }
//                });
//    }
//
//    @NonNull protected Action1<OnTextChangeEvent> createCodeViewHandler(final int indexNextFocus)
//    {
//        return new Action1<OnTextChangeEvent>()
//        {
//            @Override public void call(OnTextChangeEvent onTextChangeEvent)
//            {
//                if (onTextChangeEvent.text().length() > 0)
//                {
//                    codeViews[indexNextFocus].requestFocus();
//                }
//            }
//        };
//    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
//        onDestroyViewSubscriptions.unsubscribe();
        super.onDestroyView();
    }

//    @NonNull protected Observable<SMSSentConfirmationDTO> createRepeatableSMSConfirmation(@NonNull final SMSId smsId)
//    {
//        return smsServiceWrapper.getMessageStatus(smsId)
//                .delaySubscription(DEFAULT_POLL_INTERVAL_MILLISEC, TimeUnit.MILLISECONDS)
//                .flatMap(new Func1<SMSSentConfirmationDTO, Observable<SMSSentConfirmationDTO>>()
//                {
//                    @Override public Observable<SMSSentConfirmationDTO> call(SMSSentConfirmationDTO smsSentConfirmationDTO)
//                    {
//                        if (smsSentConfirmationDTO.isFinalStatus())
//                        {
//                            return Observable.just(smsSentConfirmationDTO);
//                        }
//                        return createRepeatableSMSConfirmation(smsId);
//                    }
//                });
//    }
}
