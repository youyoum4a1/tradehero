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
import com.neovisionaries.i18n.CountryCode;
import com.squareup.picasso.Picasso;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.IdentityPromptInfoDTO;
import com.tradehero.th.api.kyc.KYCFormUtil;
import com.tradehero.th.api.kyc.LiveAvailabilityDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.fastfill.FastFillExceptionUtil;
import com.tradehero.th.models.fastfill.FastFillUtil;
import com.tradehero.th.models.fastfill.IdentityScannedDocumentType;
import com.tradehero.th.models.fastfill.ScannedDocument;
import com.tradehero.th.network.service.LiveServiceWrapper;
import com.tradehero.th.persistence.prefs.LiveBrokerSituationPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.ReplaceWithFunc1;
import java.util.concurrent.TimeUnit;
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

@Routable(IdentityPromptActivity.ROUTER_KYC_SCHEME + ":brokerId")
public class IdentityPromptActivity extends BaseActivity
{
    public static final String ROUTER_KYC_SCHEME = "kyc/";
    @Inject FastFillUtil fastFillUtil;
    @Inject LiveBrokerSituationPreference liveBrokerSituationPreference;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject LiveServiceWrapper liveServiceWrapper;
    @Inject Picasso picasso;

    @Bind(R.id.identity_prompt_passport)
    View scanPassport;
    @Bind(R.id.live_powered_by)
    TextView livePoweredBy;
    @Bind(R.id.identity_prompt_image_specific)
    ImageView imgPrompt;
    @Bind(R.id.identity_prompt_specific)
    TextView scanSpecificId;

    @RouteProperty("brokerId") int routedBrokerId;

    private Subscription fastFillSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_prompt);
        ButterKnife.bind(IdentityPromptActivity.this);

        final Observable<ScannedDocument> documentObservable =
                fastFillUtil.getScannedDocumentObservable().throttleLast(300, TimeUnit.MILLISECONDS); //HACK

        fastFillSubscription = getBrokerSituation()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<LiveBrokerSituationDTO>()
                {
                    @Override
                    public void call(LiveBrokerSituationDTO situationDTO)
                    {
                        String text = getString(situationDTO.kycForm.getBrokerNameResId());
                        livePoweredBy.setText(text);
                    }
                })
                .flatMap(new Func1<LiveBrokerSituationDTO, Observable<LiveBrokerSituationDTO>>()
                {
                    @Override
                    public Observable<LiveBrokerSituationDTO> call(final LiveBrokerSituationDTO situation)
                    {
                        //noinspection ConstantConditions
                        return liveServiceWrapper.getAvailability()
                                .flatMap(new Func1<LiveAvailabilityDTO, Observable<IdentityPromptInfoDTO>>()
                                {
                                    @Override public Observable<IdentityPromptInfoDTO> call(LiveAvailabilityDTO liveAvailabilityDTO)
                                    {
                                        return liveServiceWrapper.getIdentityPromptInfo(liveAvailabilityDTO.getRequestorCountry());
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Action1<IdentityPromptInfoDTO>()
                                {
                                    @Override public void call(IdentityPromptInfoDTO identityPromptInfoDTO)
                                    {
                                        if (identityPromptInfoDTO != null)
                                        {
                                            picasso.load(identityPromptInfoDTO.image)
                                                    .placeholder(identityPromptInfoDTO.country.logoId)
                                                    .into(imgPrompt);
                                            scanSpecificId.setText(identityPromptInfoDTO.prompt);
                                            imgPrompt.setVisibility(View.VISIBLE);
                                            scanSpecificId.setVisibility(View.VISIBLE);
                                        }
                                        else
                                        {
                                            imgPrompt.setVisibility(View.GONE);
                                            scanSpecificId.setVisibility(View.GONE);
                                        }
                                    }
                                })
                                .map(new Func1<IdentityPromptInfoDTO, LiveBrokerSituationDTO>()
                                {
                                    @Override public LiveBrokerSituationDTO call(IdentityPromptInfoDTO ignored)
                                    {
                                        return situation;
                                    }
                                });
                    }
                })
                .take(1)
                .flatMap(new Func1<LiveBrokerSituationDTO, Observable<LiveBrokerSituationDTO>>()
                {
                    @Override
                    public Observable<LiveBrokerSituationDTO> call(final LiveBrokerSituationDTO situationToUse)
                    {
                        return Observable.merge(
                                ViewObservable.clicks(scanPassport)
                                        .map(new ReplaceWithFunc1<OnClickEvent, IdentityScannedDocumentType>(IdentityScannedDocumentType.PASSPORT)),
                                ViewObservable.clicks(scanSpecificId)
                                        .map(new ReplaceWithFunc1<OnClickEvent, IdentityScannedDocumentType>(
                                                IdentityScannedDocumentType.IDENTITY_CARD)))
                                .flatMap(
                                        new Func1<IdentityScannedDocumentType, Observable<ScannedDocument>>()
                                        {
                                            @Override
                                            public Observable<ScannedDocument> call(IdentityScannedDocumentType identityScannedDocumentType)
                                            {
                                                CountryCode code = null;
                                                if (identityScannedDocumentType.equals(IdentityScannedDocumentType.IDENTITY_CARD)
                                                        && situationToUse.kycForm.getCountry() != null)
                                                {
                                                    code = CountryCode.getByCode(situationToUse.kycForm.getCountry().toString());
                                                }

                                                fastFillUtil.fastFill(IdentityPromptActivity.this, identityScannedDocumentType, code);

                                                return documentObservable;
                                            }
                                        })
                                .map(new Func1<ScannedDocument, LiveBrokerSituationDTO>()
                                {
                                    @Override
                                    public LiveBrokerSituationDTO call(ScannedDocument scannedDocument)
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
                    @Override
                    public Boolean call(Integer integer, Throwable throwable)
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
                .take(1)
                .subscribe(
                        new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override
                            public void call(@NonNull LiveBrokerSituationDTO situationToUse)
                            {
                                goToSignUp();
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override
                            public void call(Throwable throwable)
                            {
                                Timber.e(throwable, "Error when FastFill");
                                if (!FastFillExceptionUtil.canRetry(throwable))
                                {
                                    THToast.show(R.string.unable_to_capture_value_from_image);
                                    goToSignUp();
                                }
                            }
                        });
    }

    @Override
    protected void onDestroy()
    {
        fastFillSubscription.unsubscribe();
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        fastFillUtil.onActivityResult(this, requestCode, resultCode, data);
    }

    @OnClick(android.R.id.closeButton)
    public void onCloseClicked()
    {
        onBackPressed();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.identity_prompt_no)
    public void onNoClicked(View view)
    {
        goToSignUp();
    }

    @NonNull
    protected Observable<LiveBrokerSituationDTO> getBrokerSituation()
    {
        return Observable.combineLatest(
                liveServiceWrapper.getBrokerSituation()
                        .filter(new Func1<LiveBrokerSituationDTO, Boolean>()
                        {
                            @Override
                            public Boolean call(LiveBrokerSituationDTO situationDTO)
                            {
                                return situationDTO.kycForm != null;
                            }
                        }),
                userProfileCache.getOne(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>()),
                new Func2<LiveBrokerSituationDTO, UserProfileDTO, LiveBrokerSituationDTO>()
                {
                    @Override
                    public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO, UserProfileDTO currentUserProfile)
                    {
                        if (KYCFormUtil.fillInBlanks(situationDTO.kycForm, currentUserProfile))
                        {
                            liveBrokerSituationPreference.set(situationDTO);
                        }
                        return situationDTO;
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
