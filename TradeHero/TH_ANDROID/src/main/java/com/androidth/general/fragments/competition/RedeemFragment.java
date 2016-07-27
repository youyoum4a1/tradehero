package com.androidth.general.fragments.competition;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.network.service.ProviderServiceRx;
import com.androidth.general.utils.ExceptionUtils;
import com.androidth.general.utils.StringUtils;
import com.androidth.general.utils.route.THRouter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ayushnvijay on 6/28/16.
 */

@Routable({
        "redeem-provider-code/:providerId"
})

public class RedeemFragment extends DashboardFragment {

    @RouteProperty("providerId") Integer routedProviderId;

    @Bind(R.id.redeem_server_response)
    TextView redeemServerResponse;
    @Bind(R.id.redemption_code)
    TextInputEditText redeemCode;
    @Bind(R.id.redeem_button)
    AppCompatButton redeemButton;

    @Inject
    THRouter thRouter;

    @Inject
    ProviderServiceRx serviceRx;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.competition_redeem_fragment, container, false);
    }


    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        thRouter.inject(this);
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
        redeemButton.setOnClickListener(buttonListener -> {
            if(redeemCode.getText()!=null && redeemCode.getText().length()!=0){
                validate(redeemCode.getText().toString());
            }
        });


    }

    @Override public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater)
    {
        //dont need to call super here because we need actionbar to remain as it is
    }

    private void validate(String str){
        serviceRx.validatedRedeemCode(routedProviderId, str).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(response->{
            redeemServerResponse.setTextColor(Color.parseColor("#66B535"));
            redeemServerResponse.setVisibility(View.VISIBLE);
            redeemServerResponse.setText(response);
        }, error->{

            String errorMessage = ExceptionUtils.getStringElementFromThrowable(error, "Message");

            redeemServerResponse.setTextColor(Color.parseColor("#B71C1C"));
            redeemServerResponse.setVisibility(View.VISIBLE);
            redeemServerResponse.setText(errorMessage);
        });
    }
}

