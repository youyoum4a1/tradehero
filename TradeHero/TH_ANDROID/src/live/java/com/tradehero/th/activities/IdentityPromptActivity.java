package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.models.fastfill.FastFillException;
import com.tradehero.th.models.fastfill.FastFillUtil;
import com.tradehero.th.models.fastfill.ScannedDocument;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.models.kyc.KYCFormFactory;
import com.tradehero.th.persistence.prefs.KYCFormPreference;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

public class IdentityPromptActivity extends BaseActivity
{
    @Inject FastFillUtil fastFillUtil;
    @Inject KYCFormPreference kycFormPreference;

    @InjectView(R.id.identity_prompt_yes) View yesButton;

    private Subscription fastFillSubscription;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_prompt);
        ButterKnife.inject(this);
        fastFillSubscription = Observable.combineLatest(
                getFormToUse(),
                ViewObservable.clicks(yesButton).flatMap(
                        new Func1<OnClickEvent, Observable<ScannedDocument>>()
                        {
                            @Override public Observable<ScannedDocument> call(@NonNull OnClickEvent onClickEvent)
                            {
                                Observable<ScannedDocument> documentObservable = fastFillUtil.getScannedDocumentObservable()
                                        .cache(1);
                                fastFillUtil.fastFill(IdentityPromptActivity.this);
                                return documentObservable;
                            }
                        }),
                new Func2<KYCForm, ScannedDocument, KYCForm>()
                {
                    @Override public KYCForm call(@NonNull KYCForm formToUse, @NonNull ScannedDocument scannedDocument)
                    {
                        formToUse.pickFrom(scannedDocument);
                        kycFormPreference.set(formToUse);
                        return formToUse;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<KYCForm>()
                        {
                            @Override public void call(@NonNull KYCForm formToUse)
                            {
                                goToSignUp();
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                Timber.e(throwable, "Error when FastFill");
                                if (!(throwable instanceof FastFillException) || !((FastFillException) throwable).canRetry())
                                {
                                    THToast.show(R.string.fast_fill_not_available);
                                    goToSignUp();
                                }
                            }
                        });
    }

    @Override protected void onDestroy()
    {
        fastFillSubscription.unsubscribe();
        ButterKnife.reset(this);
        super.onDestroy();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        fastFillUtil.onActivityResult(this, requestCode, resultCode, data);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.identity_prompt_no)
    public void onNoClicked()
    {
        startActivity(new Intent(this, SignUpLiveActivity.class));
        finish();
    }

    @NonNull protected Observable<KYCForm> getFormToUse()
    {
        return KYCFormFactory.createDefaultForm()
                .map(new Func1<KYCForm, KYCForm>()
                {
                    @Override public KYCForm call(@NonNull KYCForm defaultForm)
                    {
                        KYCForm savedForm = kycFormPreference.get();
                        return savedForm.getClass().equals(defaultForm.getClass()) ? savedForm : defaultForm;
                    }
                });
    }

    protected void goToSignUp()
    {
        startActivity(new Intent(this, SignUpLiveActivity.class));
        finish();
    }
}
