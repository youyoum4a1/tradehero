package com.tradehero.th.fragments.live.ayondo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.TradingPerQuarter;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;

public class LiveSignUpStep3AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    @Bind(R.id.info_trading_per_quarter) Spinner tradingPerQuarterSpinner;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_3, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        onDestroyViewSubscriptions.add(
                Observable.combineLatest(
                        getBrokerSituationObservable()
                                .observeOn(AndroidSchedulers.mainThread()),
                        getKYCAyondoFormOptionsObservable()
                                .observeOn(AndroidSchedulers.mainThread()),
                        new Func2<LiveBrokerSituationDTO, KYCAyondoFormOptionsDTO, Object>()
                        {
                            @Override public Object call(LiveBrokerSituationDTO liveBrokerSituationDTO, KYCAyondoFormOptionsDTO kycFormOptionsDTO)
                            {
                                LollipopArrayAdapter<String> tradingPerQuarterAdapter =
                                        new LollipopArrayAdapter<>(getActivity(), TradingPerQuarter.createTexts(getResources(),
                                                kycFormOptionsDTO.tradingPerQuarterOptions));
                                tradingPerQuarterSpinner.setAdapter(tradingPerQuarterAdapter);
                                return null;
                            }
                        })
                        .subscribe(new Action1<Object>()
                        {
                            @Override public void call(Object o)
                            {

                            }
                        }));
    }
}
