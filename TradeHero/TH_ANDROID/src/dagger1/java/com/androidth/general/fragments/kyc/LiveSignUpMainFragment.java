package com.androidth.general.fragments.kyc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;

import com.android.common.SlidingTabLayout;
import com.androidth.general.R;
import com.androidth.general.activities.SignUpLiveActivity;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.kyc.StepStatus;
import com.androidth.general.common.persistence.prefs.BooleanPreference;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.fragments.kyc.adapter.SignUpLivePagerAdapterFactory;
import com.androidth.general.fragments.kyc.adapter.SignUpLiveAyondoPagerAdapter;
import com.androidth.general.network.service.LiveServiceWrapper;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.persistence.prefs.LiveBrokerSituationPreference;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.utils.route.THRouter;
import com.androidth.general.widget.LiveRewardWidget;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

@Routable({
        "enrollchallenge/:providerId"
})
public class LiveSignUpMainFragment extends BaseFragment
{
    @RouteProperty("providerId") protected Integer enrollProviderId;
    @Inject ProviderCacheRx providerCacheRx;
    @Inject SignUpLivePagerAdapterFactory signUpLivePagerAdapterFactory;
    @Inject Toolbar toolbar;
    @Inject LiveBrokerSituationPreference liveBrokerSituationPreference;
    @Inject LiveServiceWrapper liveServiceWrapper;
    @Inject THRouter thRouter;
    @Inject @com.androidth.general.persistence.prefs.ShowCallToActionFragmentPreference
    BooleanPreference showCallToActionFragment;
    static boolean isToJoinCompetition = false;

    @Bind(R.id.android_tabs) protected SlidingTabLayout tabLayout;
    @Bind(R.id.pager) protected ViewPager viewPager;
    @Bind(R.id.live_reward_widget) protected LiveRewardWidget liveRewardWidget;

    public static String notificationLogoUrl;
    public static String hexColor;
    private boolean isEnrolled;

    public static void putProviderId(@NonNull Bundle args, int providerId)
    {
        args.putInt(SignUpLiveActivity.KYC_CORRESPONDENT_PROVIDER_ID, providerId);
    }

    public static void isToJoinCompetition(boolean flag)
    {
        isToJoinCompetition = flag;
    }

    private static int getProviderId(@NonNull Bundle args)
    {
        return args.getInt(SignUpLiveActivity.KYC_CORRESPONDENT_PROVIDER_ID, 0);
    }


    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        thRouter.inject(this);
        inflater.inflate(R.menu.settings_menu, menu);
        providerCacheRx.get(new ProviderId(getProviderId(getArguments())))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Pair<ProviderId, ProviderDTO>>() {
            @Override
            public void call(Pair<ProviderId, ProviderDTO> providerIdProviderDTOPair) {
                ProviderDTO providerDTO = providerIdProviderDTOPair.second;
                if(providerDTO.isUserEnrolled)
                    notificationLogoUrl = providerDTO.advertisements.get(0).bannerImageUrl;

                else notificationLogoUrl = providerDTO.navigationLogoUrl;

                isEnrolled = providerDTO.isUserEnrolled;
                hexColor = providerDTO.hexColor;
                setActionBarTitle("");
                setActionBarColor(providerDTO.hexColor);
                setActionBarImage(notificationLogoUrl);
            }
        }, new TimberOnErrorAction1("Live Signup provider error"));
    }

    private void setActionBarImage(String url){
        setActionBarCustomImage(getActivity(), url, true);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_main, container, false);
    }

    @Override public void onViewCreated(final View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        showCallToActionFragment.set(false);

        tabLayout.setDistributeEvenly(true);
        tabLayout.setCustomTabView(R.layout.th_sign_up_tab_indicator, android.R.id.title);
        tabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.transparent));

//        signUpLivePagerAdapterFactory.create(
//                getChildFragmentManager(),
//                getArguments()).publish();

        SignUpLiveAyondoPagerAdapter adapter = new SignUpLiveAyondoPagerAdapter(getChildFragmentManager(), getArguments());

        //Jeff
        if(isToJoinCompetition){
            liveRewardWidget.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            adapter.setShowFirstStepOnly(true);
        }

        ConnectableObservable<SignUpLiveAyondoPagerAdapter> pagerAdapterObservable =
                ConnectableObservable.just(adapter).publish();

        onDestroyViewSubscriptions.add(
                pagerAdapterObservable
                        .doOnNext(new Action1<PagerAdapter>()
                        {
                            @Override public void call(PagerAdapter pagerAdapter)
                            {
                                viewPager.setAdapter(pagerAdapter);
                                if(!isToJoinCompetition){
                                    //if already joined, show the second page
                                    viewPager.setCurrentItem(1);
                                }
                                tabLayout.setDistributeEvenly(true);
                                tabLayout.setViewPager(viewPager);
                            }
                        })
                        .flatMap(pagerAdapter -> liveBrokerSituationPreference.getLiveBrokerSituationDTOObservable())
                        .filter(situationDTO -> situationDTO.kycForm != null)
                        .throttleLast(3, TimeUnit.SECONDS)
                        .distinctUntilChanged()
                        .doOnError(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if(throwable!=null){
                                    new TimberOnErrorAction1(throwable.getMessage());
                                }else{
                                    new TimberOnErrorAction1("Live signup main fragment error");
                                }
                            }
                        })
                        .subscribe());
//                        .flatMap(situationDTO -> {
//                            //noinspection ConstantConditions
//                            situationDTO.broker
//                            return liveServiceWrapper.applyToLiveBroker(situationDTO.kycFormbroker.id, situationDTO.kycForm)
//                                    .doOnNext(stepStatusesDTO -> {
//                                        KYCForm form = KYCFormUtil.from(situationDTO.kycForm);
//                                        form.setStepStatuses(stepStatusesDTO.stepStatuses);
//                                        liveBrokerSituationPreference.set(new LiveBrokerSituationDTO(situationDTO.broker, form));
//                                    })
//                                    ;
//                        })
//                        .subscribe(
//                                updatedSteps -> {
//                                    updatePageIndicator(updatedSteps.stepStatuses);
//                                },
//                                new TimberOnErrorAction1("Error on updating step status")));

        onDestroyViewSubscriptions.add(pagerAdapterObservable
                .flatMap(new Func1<PagerAdapter, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(PagerAdapter pagerAdapter)
                    {
                        if (pagerAdapter instanceof PrevNextObservable)
                        {
                            return ((PrevNextObservable) pagerAdapter).getPrevNextObservable();
                        }
                        return Observable.empty();
                    }
                })
                .subscribe(
                        next -> viewPager.setCurrentItem(viewPager.getCurrentItem() + (next ? 1 : -1)),
                        new TimberOnErrorAction1("Failed to listen to prev / next buttons")));

        pagerAdapterObservable.connect();
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
    }

    private void updatePageIndicator(List<StepStatus> stepStatusList)
    {
        int childCount = tabLayout.getTabStrip().getChildCount();
        int stepSize = stepStatusList.size();
        int completeCount = 0;
        for (int i = 0; i < childCount && i < stepSize; i++)
        {
            Checkable textView = (Checkable) tabLayout.getTabStrip().getChildAt(i);
            StepStatus step = stepStatusList.get(i);
            boolean isComplete = step.equals(StepStatus.COMPLETE);
            textView.setChecked(isComplete);
            completeCount += isComplete ? 1 : 0;
        }
        liveRewardWidget.setRewardStep(completeCount);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null)
        {
            for (Fragment fragment : fragments)
            {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public static void registerAliases(THRouter router)
    {
        router.registerAlias("competition-nagaWarrants", "enrollchallenge/55");
    }
}