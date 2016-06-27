package com.androidth.general.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.kyc.CountryDocumentTypes;
import com.androidth.general.api.kyc.IdentityPromptInfoDTO;
import com.androidth.general.api.kyc.KYCFormUtil;
import com.androidth.general.api.kyc.LiveAvailabilityDTO;
import com.androidth.general.api.live.LiveBrokerSituationDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.models.fastfill.FastFillExceptionUtil;
import com.androidth.general.models.fastfill.FastFillUtil;
import com.androidth.general.models.fastfill.IdentityScannedDocumentType;
import com.androidth.general.models.fastfill.ScannedDocument;
import com.androidth.general.network.service.LiveServiceWrapper;
import com.androidth.general.network.service.ProviderServiceWrapper;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.persistence.prefs.LiveBrokerSituationPreference;
import com.androidth.general.persistence.security.SecurityCompositeListCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.ReplaceWithFunc1;
import com.androidth.general.utils.route.THRouter;
import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.neovisionaries.i18n.CountryCode;
import com.squareup.picasso.Picasso;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;

import java.util.ArrayList;
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
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

//@Routable(IdentityPromptActivity.ROUTER_KYC_SCHEME + ":brokerId")
//@Routable({
//        "enrollchallenge/:providerId",
//        //IdentityPromptActivity.ROUTER_KYC_SCHEME + ":brokerId"
//})
public class IdentityPromptActivity extends BaseActivity
{
    public static final String ROUTER_KYC_SCHEME = "kyc/";
    @Inject FastFillUtil fastFillUtil;
    @Inject LiveBrokerSituationPreference liveBrokerSituationPreference;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject LiveServiceWrapper liveServiceWrapper;
    @Inject Picasso picasso;

    @Inject protected ProviderCacheRx providerCacheRx;

    @Bind(R.id.identity_prompt_image_passport)
    ImageView imgPassport;

    @Bind(R.id.identity_prompt_passport)
    Button scanPassport;

    @Bind(R.id.identity_prompt_image_specific)
    ImageView imgSpecific;

    @Bind(R.id.identity_prompt_specific)
    Button scanSpecificId;

    @Bind(R.id.dummy_action_bar)
    ImageView imgActionBar;

    //@RouteProperty("brokerId") int routedBrokerId;
    //@RouteProperty("providerId") protected int providerId;

    protected int providerId;
    protected ProviderDTO providerDTO;
    String countryCode;

    private Subscription fastFillSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_prompt);
        ButterKnife.bind(IdentityPromptActivity.this);

        providerId = getIntent().getIntExtra(SignUpLiveActivity.KYC_CORRESPONDENT_PROVIDER_ID, 0);
        providerDTO = providerCacheRx.getCachedValue(new ProviderId(providerId));

        countryCode = userProfileCache.getCachedValue(currentUserId.toUserBaseKey()).countryCode;
        if(providerDTO.providerCountries.length == 1)
        {
            countryCode = providerDTO.providerCountries[0];
        }

//        picasso.load(providerDTO.navigationLogoUrl)
//                .into(imgActionBar);

        String color = providerDTO.hexColor.startsWith("#") ? providerDTO.hexColor : "#".concat(providerDTO.hexColor);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(color));

        imgActionBar.setBackground(colorDrawable);

        // temp not working, need check


//        final Observable<ScannedDocument> documentObservable =
//                fastFillUtil.getScannedDocumentObservable().throttleLast(300, TimeUnit.MILLISECONDS); //HACK
//
//        fastFillSubscription = getBrokerSituation()
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext(situationDTO -> {
//                    String text = getString(situationDTO.kycForm.getBrokerNameResId());
//                    livePoweredBy.setText(text);
//                })
//                .flatMap(situation -> {
//                    //noinspection ConstantConditions
//                    return liveServiceWrapper.getAvailability()
//                            .flatMap(liveAvailabilityDTO -> liveServiceWrapper.getIdentityPromptInfo(liveAvailabilityDTO.getRequestorCountry()))
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .doOnNext(identityPromptInfoDTO -> {
//                                if (identityPromptInfoDTO != null)
//                                {
//                                    picasso.load(identityPromptInfoDTO.image)
//                                            .placeholder(identityPromptInfoDTO.country.logoId)
//                                            .into(imgPrompt);
//                                    scanSpecificId.setText(identityPromptInfoDTO.prompt);
//                                    imgPrompt.setVisibility(View.VISIBLE);
//                                    scanSpecificId.setVisibility(View.VISIBLE);
//                                }
//                                else
//                                {
//                                    imgPrompt.setVisibility(View.GONE);
//                                    scanSpecificId.setVisibility(View.GONE);
//                                }
//                            })
//                            .map(new Func1<IdentityPromptInfoDTO, LiveBrokerSituationDTO>()
//                            {
//                                @Override public LiveBrokerSituationDTO call(IdentityPromptInfoDTO ignored)
//                                {
//                                    return situation;
//                                }
//                            });
//                })
//                .take(1)
//                .flatMap(new Func1<LiveBrokerSituationDTO, Observable<LiveBrokerSituationDTO>>()
//                {
//                    @Override
//                    public Observable<LiveBrokerSituationDTO> call(final LiveBrokerSituationDTO situationToUse)
//                    {
//                        return Observable.merge(
//                                ViewObservable.clicks(scanPassport)
//                                        .map(new ReplaceWithFunc1<OnClickEvent, IdentityScannedDocumentType>(IdentityScannedDocumentType.PASSPORT)),
//                                ViewObservable.clicks(scanSpecificId)
//                                        .map(new ReplaceWithFunc1<OnClickEvent, IdentityScannedDocumentType>(
//                                                IdentityScannedDocumentType.IDENTITY_CARD)))
//                                .flatMap(
//                                        new Func1<IdentityScannedDocumentType, Observable<ScannedDocument>>()
//                                        {
//                                            @Override
//                                            public Observable<ScannedDocument> call(IdentityScannedDocumentType identityScannedDocumentType)
//                                            {
//                                                CountryCode code = null;
//                                                if (identityScannedDocumentType.equals(IdentityScannedDocumentType.IDENTITY_CARD)
//                                                        && situationToUse.kycForm.getCountry() != null)
//                                                {
//                                                    code = CountryCode.getByCode(situationToUse.kycForm.getCountry().toString());
//                                                }
//
//                                                fastFillUtil.fastFill(IdentityPromptActivity.this, identityScannedDocumentType, code);
//
//                                                return documentObservable;
//                                            }
//                                        })
//                                .map(new Func1<ScannedDocument, LiveBrokerSituationDTO>()
//                                {
//                                    @Override
//                                    public LiveBrokerSituationDTO call(ScannedDocument scannedDocument)
//                                    {
//                                        //noinspection ConstantConditions
//                                        situationToUse.kycForm.pickFrom(scannedDocument);
//                                        liveBrokerSituationPreference.set(situationToUse);
//                                        return situationToUse;
//                                    }
//                                });
//                    }
//                })
//                .retry(new Func2<Integer, Throwable, Boolean>()
//                {
//                    @Override
//                    public Boolean call(Integer integer, Throwable throwable)
//                    {
//                        boolean willRetry = FastFillExceptionUtil.canRetry(throwable);
//                        if (willRetry)
//                        {
//                            Timber.e(throwable, "Error when FastFill, retrying");
//                        }
//                        return willRetry;
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .take(1)
//                .subscribe(
//                        new Action1<LiveBrokerSituationDTO>()
//                        {
//                            @Override
//                            public void call(@NonNull LiveBrokerSituationDTO situationToUse)
//                            {
//                                goToSignUp();
//                            }
//                        },
//                        new Action1<Throwable>()
//                        {
//                            @Override
//                            public void call(Throwable throwable)
//                            {
//                                Timber.e(throwable, "Error when FastFill");
//                                if (!FastFillExceptionUtil.canRetry(throwable))
//                                {
//                                    THToast.show(R.string.unable_to_capture_value_from_image);
////                                    goToSignUp();
//                                }
//                            }
//                        });

        liveServiceWrapper.documentsForCountry(countryCode).subscribe(new Action1<ArrayList<CountryDocumentTypes>>() {
            @Override
            public void call(ArrayList<CountryDocumentTypes> countryDocumentTypes) {
                imgPassport.setVisibility(View.GONE);
                scanPassport.setVisibility(View.GONE);
                imgSpecific.setVisibility(View.GONE);
                scanSpecificId.setVisibility(View.GONE);

                for (CountryDocumentTypes T : countryDocumentTypes)
                {
                    if(T.documentTypeId == 1)
                    {
                        imgSpecific.setVisibility(View.VISIBLE);
                        scanSpecificId.setVisibility(View.VISIBLE);

                        scanSpecificId.setText(T.displayName);

                        String url = "http://portalvhdskgrrf4wksb8vq.blob.core.windows.net/country-flags/" + countryCode.toLowerCase() + "_64.png";
                        //picasso.load(url)
                        //        .into(imgSpecific);
                    }

                    if(T.documentTypeId == 2)
                    {
                        imgPassport.setVisibility(View.VISIBLE);
                        scanPassport.setVisibility(View.VISIBLE);

                        scanPassport.setText(T.displayName);
                        String url = "https://portalvhdskgrrf4wksb8vq.blob.core.windows.net/country-passport/" + countryCode.toLowerCase() + ".png";
                        //picasso.load(url)
                        //        .into(imgPassport);
                    }
                }
            }
        });
    }


    @Override
    protected void onDestroy()
    {
        //fastFillSubscription.unsubscribe();
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

    @NonNull @RxLogObservable
    protected Observable<LiveBrokerSituationDTO> getBrokerSituation()
    {
        return Observable.combineLatest(
                liveServiceWrapper.getBrokerSituation()
                        .filter(situationDTO -> situationDTO.kycForm != null),
                userProfileCache.getOne(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<>()),
                (situationDTO, currentUserProfile) -> {
                    if (KYCFormUtil.fillInBlanks(situationDTO.kycForm, currentUserProfile))
                    {
                        liveBrokerSituationPreference.set(situationDTO);
                    }
                    return situationDTO;
                })
                .share();
    }

    protected void goToSignUp()
    {
        Intent kycIntent = new Intent(this, SignUpLiveActivity.class);
        kycIntent.putExtra(SignUpLiveActivity.KYC_CORRESPONDENT_PROVIDER_ID, getIntent().getIntExtra(SignUpLiveActivity.KYC_CORRESPONDENT_PROVIDER_ID, 0));
        startActivity(kycIntent);
        finish();
    }
}
