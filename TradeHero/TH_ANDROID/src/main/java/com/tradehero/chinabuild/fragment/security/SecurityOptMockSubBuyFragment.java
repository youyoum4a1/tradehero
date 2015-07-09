package com.tradehero.chinabuild.fragment.security;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tradehero.chinabuild.data.QuoteDetail;
import com.tradehero.th.R;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;

import java.text.DecimalFormat;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Buy Page
 *
 * Created by palmer on 15/7/6.
 */
public class SecurityOptMockSubBuyFragment extends Fragment{

    private Button buySellBtn;
    private ListView positionsLV;

    //Dialog
    private Dialog buyConfirmDialog;
    private TextView dlgStockNameTV;
    private TextView dlgStockCodeTV;
    private TextView dlgStockPriceTV;
    private TextView dlgStockAmountTV;
    private TextView dlgStockTotalTV;
    private TextView dlgConfirmTV;
    private TextView dlgCancelTV;

    private SecurityId securityId;
    @Inject QuoteServiceWrapper quoteServiceWrapper;

    private SecurityOptMockPositionAdapter securityOptMockPositionAdapter;

    private boolean isNeedRefresh = true;

    //Buy Sell Layout
    private TextView sell5Price;
    private TextView sell5Amount;
    private TextView sell4Price;
    private TextView sell4Amount;
    private TextView sell3Price;
    private TextView sell3Amount;
    private TextView sell2Price;
    private TextView sell2Amount;
    private TextView sell1Price;
    private TextView sell1Amount;

    private TextView buy5Price;
    private TextView buy5Amount;
    private TextView buy4Price;
    private TextView buy4Amount;
    private TextView buy3Price;
    private TextView buy3Amount;
    private TextView buy2Price;
    private TextView buy2Amount;
    private TextView buy1Price;
    private TextView buy1Amount;

    private int color_up;
    private int color_down;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        securityOptMockPositionAdapter = new SecurityOptMockPositionAdapter(getActivity());
        color_up = getResources().getColor(R.color.number_up);
        color_down = getResources().getColor(R.color.number_down);
        getSecurityId();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_opt_sub_buysell, container, false);
        initViews(view);
        initSellBuyViews(view);
        return view;
    }


    @Override
    public void onResume(){
        super.onResume();
        isNeedRefresh = true;
        if (securityId != null) {
            quoteServiceWrapper.getQuoteDetails(securityId, new RefreshBUYSELLCallback());
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        isNeedRefresh = false;
    }


    private void initViews(View view) {
        buySellBtn = (Button) view.findViewById(R.id.button_security_opt_buy_sell);
        buySellBtn.setText(R.string.security_opt_buy);
        buySellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBuyConfirmDialog();
            }
        });
        positionsLV = (ListView) view.findViewById(R.id.listview_security_opt_positions);
        if(securityOptMockPositionAdapter==null){
            securityOptMockPositionAdapter = new SecurityOptMockPositionAdapter(getActivity());
        }
        positionsLV.setAdapter(securityOptMockPositionAdapter);
    }

    private void showBuyConfirmDialog(){
        if(getActivity() == null){
            return;
        }
        if(buyConfirmDialog == null){
            buyConfirmDialog = new Dialog(getActivity());
            buyConfirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            buyConfirmDialog.setCanceledOnTouchOutside(false);
            buyConfirmDialog.setCancelable(true);
            buyConfirmDialog.setContentView(R.layout.dialog_security_opt_sell);


            dlgCancelTV = (TextView)buyConfirmDialog.findViewById(R.id.dialog_cancel);
            dlgCancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(buyConfirmDialog!=null){
                        buyConfirmDialog.dismiss();
                    }
                }
            });

        }
        buyConfirmDialog.show();
    }


    private void initSellBuyViews(View view) {
        sell5Price = (TextView) view.findViewById(R.id.textview_sell_price_a);
        sell5Amount = (TextView) view.findViewById(R.id.textview_sell_amount_a);
        sell4Price = (TextView) view.findViewById(R.id.textview_sell_price_b);
        sell4Amount = (TextView) view.findViewById(R.id.textview_sell_amount_b);
        sell3Price = (TextView) view.findViewById(R.id.textview_sell_price_c);
        sell3Amount = (TextView) view.findViewById(R.id.textview_sell_amount_c);
        sell2Price = (TextView) view.findViewById(R.id.textview_sell_price_d);
        sell2Amount = (TextView) view.findViewById(R.id.textview_sell_amount_d);
        sell1Price = (TextView) view.findViewById(R.id.textview_sell_price_e);
        sell1Amount = (TextView) view.findViewById(R.id.textview_sell_amount_e);

        buy1Price = (TextView) view.findViewById(R.id.textview_buy_price_a);
        buy1Amount = (TextView) view.findViewById(R.id.textview_buy_amount_a);
        buy2Price = (TextView) view.findViewById(R.id.textview_buy_price_b);
        buy2Amount = (TextView) view.findViewById(R.id.textview_buy_amount_b);
        buy3Price = (TextView) view.findViewById(R.id.textview_buy_price_c);
        buy3Amount = (TextView) view.findViewById(R.id.textview_buy_amount_c);
        buy4Price = (TextView) view.findViewById(R.id.textview_buy_price_d);
        buy4Amount = (TextView) view.findViewById(R.id.textview_buy_amount_d);
        buy5Price = (TextView) view.findViewById(R.id.textview_buy_price_e);
        buy5Amount = (TextView) view.findViewById(R.id.textview_buy_amount_e);
    }

    private void setSellBuyData(QuoteDetail quoteDetail) {
        if (quoteDetail == null || buy1Price == null || quoteDetail.open == null) {
            return;
        }
        if (quoteDetail.bp1 != null) {
            setStockPrice(quoteDetail.open, quoteDetail.bp1, buy1Price);
        }
        if (quoteDetail.bv1 != null) {
            buy1Amount.setText(convertAmountDoubleToString(quoteDetail.bv1));
        }
        if (quoteDetail.bp2 != null) {
            setStockPrice(quoteDetail.open, quoteDetail.bp2, buy2Price);
        }
        if (quoteDetail.bv2 != null) {
            buy2Amount.setText(convertAmountDoubleToString(quoteDetail.bv2));
        }
        if (quoteDetail.bp3 != null) {
            setStockPrice(quoteDetail.open, quoteDetail.bp3, buy3Price);
        }
        if (quoteDetail.bv3 != null) {
            buy3Amount.setText(convertAmountDoubleToString(quoteDetail.bv3));
        }
        if (quoteDetail.bp4 != null) {
            setStockPrice(quoteDetail.open, quoteDetail.bp4, buy4Price);
        }
        if (quoteDetail.bv4 != null) {
            buy4Amount.setText(convertAmountDoubleToString(quoteDetail.bv4));
        }
        if (quoteDetail.bp5 != null) {
            setStockPrice(quoteDetail.open, quoteDetail.bp5, buy5Price);
        }
        if (quoteDetail.bv5 != null) {
            buy5Amount.setText(convertAmountDoubleToString(quoteDetail.bv5));
        }

        if (quoteDetail.sp1 != null) {
            setStockPrice(quoteDetail.open, quoteDetail.sp1, sell1Price);
        }
        if (quoteDetail.sv1 != null) {
            sell1Amount.setText(convertAmountDoubleToString(quoteDetail.sv1));
        }
        if (quoteDetail.sp2 != null) {
            setStockPrice(quoteDetail.open, quoteDetail.sp2, sell2Price);
        }
        if (quoteDetail.sv2 != null) {
            sell2Amount.setText(convertAmountDoubleToString(quoteDetail.sv2));
        }
        if (quoteDetail.sp3 != null) {
            setStockPrice(quoteDetail.open, quoteDetail.sp3, sell3Price);
        }
        if (quoteDetail.sv3 != null) {
            sell3Amount.setText(convertAmountDoubleToString(quoteDetail.sv3));
        }
        if (quoteDetail.sp4 != null) {
            setStockPrice(quoteDetail.open, quoteDetail.sp4, sell4Price);
        }
        if (quoteDetail.sv4 != null) {
            sell4Amount.setText(convertAmountDoubleToString(quoteDetail.sv4));
        }
        if (quoteDetail.sp5 != null) {
            setStockPrice(quoteDetail.open, quoteDetail.sp5, sell5Price);
        }
        if (quoteDetail.sv5 != null) {
            sell5Amount.setText(convertAmountDoubleToString(quoteDetail.sv5));
        }

    }

    protected SecurityId getSecurityId() {
        if (this.securityId == null && getArguments().containsKey(SecurityOptActivity.KEY_SECURITY_ID)) {
            this.securityId = new SecurityId(getArguments().getBundle(SecurityOptActivity.KEY_SECURITY_ID));
        }
        return securityId;
    }

    private String convertAmountDoubleToString(Integer value){
        int valueNew = value/100;
        if(valueNew > 10000){
            double valueNewD = (double)valueNew/10000.0;
            DecimalFormat df =new DecimalFormat("#.0");
            return df.format(valueNewD) + "万";
        } else{
            return String.valueOf(valueNew);
        }
    }

    public class RefreshBUYSELLCallback implements Callback<QuoteDetail> {

        @Override
        public void success(QuoteDetail quoteDetail, Response response) {
            setSellBuyData(quoteDetail);
            onFinish();
        }

        @Override
        public void failure(RetrofitError error) {
            onFinish();
        }

        private void onFinish() {
            if (isNeedRefresh) {
                if(securityId == null){
                    return;
                }
                RefreshBuySellHandler handler = new RefreshBuySellHandler();
                handler.sendEmptyMessageAtTime(-1, 5000);
            }
        }
    }

    public class RefreshBuySellHandler extends Handler {
        public void handleMessage(Message msg) {
            quoteServiceWrapper.getQuoteDetails(securityId, new RefreshBUYSELLCallback());
        }
    }

    private void setStockPrice(double open, double data, TextView textView){
        textView.setText(String.valueOf(data));
        if(data >= open){
            textView.setTextColor(color_up);
        } else {
            textView.setTextColor(color_down);
        }
    }
}
