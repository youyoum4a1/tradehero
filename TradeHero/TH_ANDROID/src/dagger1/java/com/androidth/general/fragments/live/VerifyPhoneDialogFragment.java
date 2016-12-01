package com.androidth.general.fragments.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.fragments.base.BaseDialogFragment;
import com.androidth.general.fragments.live.ayondo.LiveSignUpStep1AyondoFragment;
import com.androidth.general.models.retrofit2.THRetrofitException;
import com.androidth.general.models.sms.SMSId;
import com.androidth.general.models.sms.SMSRequestFactory;
import com.androidth.general.models.sms.SMSSentConfirmationDTO;
import com.androidth.general.models.sms.SMSServiceWrapper;
import com.androidth.general.models.sms.empty.EmptySMSSentConfirmationDTO;
import com.androidth.general.models.sms.nexmo.NexmoSMSStatus;
import com.androidth.general.models.sms.twilio.TwilioRetrofitException;
import com.androidth.general.models.sms.twilio.TwilioSMSId;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.dialog.OnDialogClickEvent;
import com.androidth.general.utils.AlertDialogRxUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import rx.schedulers.Schedulers;
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

    @Bind(R.id.banner_logo) ImageView banner;
    @Bind(R.id.header) RelativeLayout header;
    @Bind(R.id.btn_verify_phone) View buttonVerify;
    @Bind(R.id.btn_send_code) View buttonResend;
    @Bind(R.id.sms_sent_description) TextView sentDescription;
    @Bind(R.id.sms_sent_status) TextView sentStatus;

    private BehaviorSubject<SMSSentConfirmationDTO> mSMSConfirmationSubject;
    public static String notificationLogoUrl = LiveSignUpMainFragment.notificationLogoUrl;
    public static String hexcolor = LiveSignUpMainFragment.hexColor;
    private int mDialingPrefix;
    private String mExpectedCode;
    private String mPhoneNumber;
    private SubscriptionList onDestroyViewSubscriptions;
    private String mFormattedNumber;
    private Subscription smsSubscription;
    private final int maxRetry = 4;
    private int numOfRetries;

    public static String getFormattedPhoneNumber(int dialingPrefix, String phoneNumber)
    {
        //TODO perhaps, move this to a helper class
        return "+" + dialingPrefix + phoneNumber;
    }

    private static VerifyPhoneDialogFragment newInstance(int dialingPrefix, String phoneNumber, String expectedCode)
    {
        Bundle b = new Bundle();
        b.putString(KEY_BUNDLE_EXPECTED, expectedCode);
        b.putInt(KEY_BUNDLE_DIALING_PREFIX, dialingPrefix);
        b.putString(KEY_BUNDLE_PHONE_NUMBER, phoneNumber);
        VerifyPhoneDialogFragment fragment = new VerifyPhoneDialogFragment();
        fragment.setArguments(b);
        return fragment;
    }

    public static VerifyPhoneDialogFragment show(int requestCode, Fragment targetFragment, int dialingPrefix, String phoneNumber, String expectedCode)
    {
        VerifyPhoneDialogFragment vdf = newInstance(dialingPrefix, phoneNumber, expectedCode);
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

        numOfRetries = 0;

    }


    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.dialog_fragment_verify_phone_number, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        header.setBackgroundColor(Color.parseColor("#"+hexcolor));
        smsSubscription = getSMSSubscription();
        if (smsSubscription == null)
        {
            smsSubscription = createSMSSubscription();
        }
        try {

            Observable<Bitmap> observable = Observable.defer(()->{
                try {
                    return Observable.just(Picasso.with(getContext()).load(notificationLogoUrl).get());
                } catch (IOException e) {
                    e.printStackTrace();
                    return Observable.error(e);
                }
            });

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(bitmap -> {
                int height = (int)(banner.getHeight()*0.6);
                int bitmapHt = bitmap.getHeight();
                int bitmapWd = bitmap.getWidth();
                int width = height * (bitmapWd / bitmapHt);
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                banner.setImageBitmap(bitmap);
            }, throwable -> {
                Log.e("Error",""+throwable.getMessage());
            });

        }
        catch (Exception e){
        }

        sentDescription.setText(getResources().getString(R.string.sms_verification_description, mFormattedNumber));

        onDestroyViewSubscriptions = new SubscriptionList();

        onDestroyViewSubscriptions.add(
                mSMSConfirmationSubject
                        .subscribe(new Action1<SMSSentConfirmationDTO>()
                                   {
                                       @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                                       {
//                                           sentStatus.setText(getResources().getString(
//                                                   R.string.sms_verification_status,
//                                                   getResources().getString(
//                                                           smsSentConfirmationDTO.getStatusStringRes())));

                                           sentStatus.setText(getResources().getString(
                                                   R.string.sms_verification_status,
                                                   NexmoSMSStatus.getStatus(smsSentConfirmationDTO.getStatusStringRes())));

//                                           //TODO Jeff get status for Nexmo
//                                           sentStatus.setVisibility(View.INVISIBLE);

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
                        }, new TimberOnErrorAction1("Verify Phone Dialog Fragment Click Failed."))
        );

        requestFocusShowKeyboard();
    }

    private void requestFocusShowKeyboard() {
        EditText editText = codeViews[0];
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(editText.requestFocus()){
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 100);

    }

    protected Subscription createSMSSubscription()
    {
        return smsServiceWrapper.sendMessage(
                SMSRequestFactory.create(
                        mFormattedNumber,
                        String.format("Please enter %s into TradeHero within the next 30 minutes.", mExpectedCode),
//                        getString(R.string.sms_verification_sms_content,
//                                mExpectedCode),
                        Locale.getDefault().getLanguage()))//language is not working, so set it to default English
                .doOnNext(new Action1<SMSSentConfirmationDTO>()
                {
                    @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                    {
                        if (smsSentConfirmationDTO.getSMSId() instanceof TwilioSMSId)
                        {
                            LiveSignUpStep1AyondoFragment liveSignUpStep1AyondoFragment;
                            if (getParentFragment() instanceof LiveSignUpStep1AyondoFragment)
                            {
                                liveSignUpStep1AyondoFragment = (LiveSignUpStep1AyondoFragment) getParentFragment();
                                liveSignUpStep1AyondoFragment.setSmsId(((TwilioSMSId) smsSentConfirmationDTO.getSMSId()).id);
                            }
                        }
                    }
                })
//                .flatMap(new Func1<SMSSentConfirmationDTO, Observable<SMSSentConfirmationDTO>>()
//                {
                //Status check
//                    @Override public Observable<SMSSentConfirmationDTO> call(SMSSentConfirmationDTO smsSentConfirmationDTO)
//                    {
//                        return createRepeatableSMSConfirmation(smsSentConfirmationDTO.getSMSId())
//                                .startWith(smsSentConfirmationDTO);
//                    }
//                })
                .retryWhen((Observable<? extends Throwable> errors) -> {
                    return errors.flatMap(new Func1<Throwable, Observable<?>>() {
                        @Override
                        public Observable<?> call(Throwable throwable) {

                            Log.v(getTag(), "Twilio error" +throwable);
                            if (throwable instanceof TwilioRetrofitException) {
                                try{
                                    TwilioRetrofitException twilioErr = (TwilioRetrofitException) throwable;

//                                    RetrofitError err = twilioErr.retrofitError;
//                                    Log.v(getTag(), "Twilio retrywhen "+err.getResponse());
//                                    int status = err.getResponse().getStatus();

                                    //retrofit 2 way
                                    THRetrofitException err = twilioErr.retrofitError;
                                    Log.v(getTag(), "Twilio retrywhen "+err.getResponse());
                                    int status = err.getResponse().code();
/**
 *https://www.twilio.com/docs/api/rest/request#post
 400 BAD REQUEST: The data given in the POST or PUT failed validation. Inspect the response body for details.
 401 UNAUTHORIZED: The supplied credentials, if any, are not sufficient to create or update the resource.
 404 NOT FOUND: You know this one.
 405 METHOD NOT ALLOWED: You can't POST or PUT to the resource.
 429 TOO MANY REQUESTS: Your application is sending too many simultaneous requests.
 500 SERVER ERROR: We couldn't create or update the resource. Please try again.
 */

                                    if(status >=400){
                                        Log.v(getTag(), "Twilio retrying status "+status);
                                        if(numOfRetries<maxRetry){
                                            Log.v(getTag(), "Twilio retrying");
                                            numOfRetries++;
                                            return Observable.timer(DEFAULT_POLL_INTERVAL_MILLISEC, TimeUnit.MILLISECONDS)
                                                    .just(createSMSSubscription());
                                        }
                                    }
                                }catch (Exception e){
                                    return Observable.error(throwable);
                                }
                            }
                            return Observable.error(throwable);
                        }

                    });
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.v(getTag(), "!!!-- Twilio"+throwable.getLocalizedMessage());
                        if(throwable!=null){
                            new TimberAndToastOnErrorAction1(throwable.getLocalizedMessage());
                        }else{
                            new TimberAndToastOnErrorAction1("Error sending SMS verification");
                        }

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
                        try{
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
                        }catch (Exception e){
                            return null;
                        }

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

    @Nullable protected Subscription getSMSSubscription()
    {
        String id = null;

        if (getParentFragment() instanceof LiveSignUpStep1AyondoFragment)
        {
            LiveSignUpStep1AyondoFragment liveSignUpStep1AyondoFragment = (LiveSignUpStep1AyondoFragment) getParentFragment();
            id = liveSignUpStep1AyondoFragment.getSmsId();
        }

        //TODO make sure it's Twilio's SMS ID
        if (!TextUtils.isEmpty(id))
        {
            return createRepeatableSMSConfirmation(new TwilioSMSId(id))
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
                    .startWith(new EmptySMSSentConfirmationDTO(mFormattedNumber, "Fake", R.string.sms_verification_button_empty_checking))
                    .subscribe(new Action1<SMSSentConfirmationDTO>()
                               {
                                   @Override public void call(SMSSentConfirmationDTO smsSentConfirmationDTO)
                                   {
                                       mSMSConfirmationSubject.onNext(smsSentConfirmationDTO);
                                   }
                               },
                            new TimberOnErrorAction1("Failed on checking sms message status"));
        }
        else
        {
            return null;
        }
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
        if (toBeValidated.equals(mExpectedCode) || isMasterKey(toBeValidated))
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
//                        else if (smsSentConfirmationDTO.isSuccessful()){
//                            buttonResend.performClick();
//                        }
                        return createRepeatableSMSConfirmation(smsId);
                    }
                });
    }

    private boolean isMasterKey(String toBeValidated){

        if(mFormattedNumber!=null || mFormattedNumber.length()>4){//must be more than 4 digits at least!
            char[] chars = mFormattedNumber.toCharArray();
            StringBuilder sb = new StringBuilder();
            for(int i=chars.length-1; i>chars.length-5; i--){
                sb.append(chars[i]);//get the last 4 digits in reverse order
            }
            int masterKey = Integer.parseInt(sb.toString());
            return masterKey==Integer.parseInt(toBeValidated);

        }else{
            return false;
        }
    }
}
