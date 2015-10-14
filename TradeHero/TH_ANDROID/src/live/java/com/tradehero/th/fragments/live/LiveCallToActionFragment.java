package com.tradehero.th.fragments.live;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.activities.IdentityPromptActivity;
import com.tradehero.th.activities.SignUpLiveActivity;
import com.tradehero.th.api.kyc.KYCFormUtil;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.fastfill.FastFillUtil;
import com.tradehero.th.network.service.LiveServiceWrapper;
import com.tradehero.th.rx.TimberOnErrorAction1;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class LiveCallToActionFragment extends DashboardFragment
{
    @Inject DashboardNavigator navigator;
    @Inject LiveServiceWrapper liveServiceWrapper;
    @Inject FastFillUtil fastFill;

    @Bind(R.id.go_live_widget) View goLiveButton;
    @Bind(R.id.live_description) TextView liveDescription;
    @Bind(R.id.live_powered_by) TextView livePoweredBy;

    PublishSubject<View> laterClickedSubject = PublishSubject.create();

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_live_action_screen, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        onDestroyViewSubscriptions.add(
                getBrokerSituationToUse()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<LiveBrokerSituationDTO>()
                                {
                                    @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                                    {
                                        if (liveBrokerSituationDTO.kycForm != null)
                                        {
                                            liveDescription.setText(KYCFormUtil.getCallToActionText(liveBrokerSituationDTO.kycForm));
                                            livePoweredBy.setText(liveBrokerSituationDTO.kycForm.getBrokerNameResId());
                                        }
                                    }
                                },
                                new TimberOnErrorAction1("Failed to listen to brokerSituationToUse in LiveCallToActionFragment")));
    }

    @Override public void onStart()
    {
        super.onStart();
        onStopSubscriptions.add(ViewObservable.clicks(goLiveButton)
                .flatMap(new Func1<OnClickEvent, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(OnClickEvent onClickEvent)
                    {
                        return fastFill.isAvailable(getActivity()).distinctUntilChanged().take(1);
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean fastFillAvailable)
                            {
                                navigator.launchActivity(fastFillAvailable
                                        ? IdentityPromptActivity.class
                                        : SignUpLiveActivity.class);
                            }
                        },
                        new TimberOnErrorAction1("Failed to listen to goLiveButton in LiveCallToActionFragment")));
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @NonNull protected Observable<LiveBrokerSituationDTO> getBrokerSituationToUse()
    {
        return liveServiceWrapper.getBrokerSituation().share();
    }

    public Observable<View> getOnLaterClickedSubscribtion()
    {
        return laterClickedSubject.asObservable();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.live_button_later)
    public void onLaterButtonClicked(View v)
    {
        laterClickedSubject.onNext(v);
    }
}
