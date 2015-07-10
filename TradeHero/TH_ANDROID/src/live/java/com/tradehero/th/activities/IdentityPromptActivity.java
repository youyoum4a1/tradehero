package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.kyc.KYCFormOptionsId;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.models.fastfill.FastFillExceptionUtil;
import com.tradehero.th.models.fastfill.FastFillUtil;
import com.tradehero.th.models.fastfill.ScannedDocument;
import com.tradehero.th.network.service.LiveServiceWrapper;
import com.tradehero.th.persistence.live.KYCFormOptionsCache;
import com.tradehero.th.persistence.prefs.LiveBrokerSituationPreference;
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
    @Inject LiveBrokerSituationPreference liveBrokerSituationPreference;
    @Inject KYCFormOptionsCache kycFormOptionsCache;
    @Inject LiveServiceWrapper liveServiceWrapper;
    @Inject Picasso picasso;

    @Bind(R.id.identity_prompt_passport) View yesButton;
    @Bind(R.id.live_powered_by) TextView livePoweredBy;
    @Bind(R.id.identity_prompt_image_specific) ImageView imgPrompt;
    @Bind(R.id.identity_prompt_specific) TextView txtPrompt;

    private Subscription fastFillSubscription;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_prompt);
        ButterKnife.bind(IdentityPromptActivity.this);
        fastFillSubscription = getBrokerSituation()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<LiveBrokerSituationDTO, Observable<LiveBrokerSituationDTO>>()
                {
                    @Override public Observable<LiveBrokerSituationDTO> call(final LiveBrokerSituationDTO situation)
                    {
                        //noinspection ConstantConditions
                        livePoweredBy.setText(situation.kycForm.getBrokerName());
                        return kycFormOptionsCache.getOne(new KYCFormOptionsId(situation.broker.id))
                                .map(new PairGetSecond<KYCFormOptionsId, KYCFormOptionsDTO>())
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(new Func1<KYCFormOptionsDTO, LiveBrokerSituationDTO>()
                                {
                                    @Override public LiveBrokerSituationDTO call(KYCFormOptionsDTO kycFormOptions)
                                    {
                                        picasso.load(kycFormOptions.getIdentityPromptInfo().image)
                                                .placeholder(R.drawable.image_identity_proof)
                                                .into(imgPrompt);
                                        txtPrompt.setText(kycFormOptions.getIdentityPromptInfo().prompt);
                                        return situation;
                                    }
                                });
                    }
                })
                .flatMap(new Func1<LiveBrokerSituationDTO, Observable<LiveBrokerSituationDTO>>()
                {
                    @Override public Observable<LiveBrokerSituationDTO> call(final LiveBrokerSituationDTO situationToUse)
                    {
                        return Observable.merge(ViewObservable.clicks(yesButton), ViewObservable.clicks(txtPrompt))
                                .flatMap(
                                        new Func1<OnClickEvent, Observable<ScannedDocument>>()
                                        {
                                            @Override public Observable<ScannedDocument> call(@NonNull OnClickEvent onClickEvent)
                                            {
                                                Observable<ScannedDocument> documentObservable = fastFillUtil.getScannedDocumentObservable()
                                                        .cache(1);
                                                fastFillUtil.fastFill(IdentityPromptActivity.this);
                                                return documentObservable;
                                            }
                                        })
                                .map(new Func1<ScannedDocument, LiveBrokerSituationDTO>()
                                {
                                    @Override public LiveBrokerSituationDTO call(ScannedDocument scannedDocument)
                                    {
                                        //noinspection ConstantConditions
                                        situationToUse.kycForm.pickFrom(scannedDocument);
                                        liveBrokerSituationPreference.set(situationToUse);
                                        return situationToUse;
                                    }
                                });
                    }
                })
                .retry(new Func2<Integer, Throwable, Boolean>()
                {
                    @Override public Boolean call(Integer integer, Throwable throwable)
                    {
                        boolean willRetry = FastFillExceptionUtil.canRetry(throwable);
                        if (willRetry)
                        {
                            Timber.e(throwable, "Error when FastFill, retrying");
                        }
                        return willRetry;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(@NonNull LiveBrokerSituationDTO situationToUse)
                            {
                                goToSignUp();
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                Timber.e(throwable, "Error when FastFill");
                                if (!FastFillExceptionUtil.canRetry(throwable))
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
        ButterKnife.unbind(this);
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

    @NonNull protected Observable<LiveBrokerSituationDTO> getBrokerSituation()
    {
        return liveServiceWrapper.getBrokerSituation()
                .filter(new Func1<LiveBrokerSituationDTO, Boolean>()
                {
                    @Override public Boolean call(LiveBrokerSituationDTO situationDTO)
                    {
                        return situationDTO.kycForm != null;
                    }
                })
                .share();
    }

    protected void goToSignUp()
    {
        startActivity(new Intent(this, SignUpLiveActivity.class));
        finish();
    }
}
