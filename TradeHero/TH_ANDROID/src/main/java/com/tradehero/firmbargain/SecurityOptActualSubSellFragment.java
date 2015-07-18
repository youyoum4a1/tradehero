package com.tradehero.firmbargain;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tradehero.chinabuild.data.QuoteDetail;
import com.tradehero.chinabuild.fragment.competition.CompetitionSecuritySearchFragment;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.chinabuild.fragment.security.SecurityOptPositionMockDTO;
import com.tradehero.chinabuild.fragment.security.SecurityOptPositionsList;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.SecurityUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.inject.Inject;

import cn.htsec.data.SecAccountInfo;
import cn.htsec.data.pkg.trade.IPackageProxy;
import cn.htsec.data.pkg.trade.TradeDataHelper;
import cn.htsec.data.pkg.trade.TradeInterface;
import cn.htsec.data.pkg.trade.TradeManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by palmer on 15/7/16.
 */
public class SecurityOptActualSubSellFragment extends Fragment implements View.OnClickListener{

    private TradeManager tradeManager;

    private Button buySellBtn;
    private ListView positionsLV;
    private TextView securityCodeTV;
    private EditText priceET;
    private TextView addOneTV;
    private TextView reduceOneTV;
    private LinearLayout availableLayout;
    private LinearLayout sharesLayout;
    private EditText decisionET;
    private ImageView oneFourIV;
    private ImageView oneThirdIV;
    private ImageView halfIV;
    private ImageView allIV;
    private TextView availableSellTV;
    private TextView totalSellTV;

    //Dialog
    private Dialog buyConfirmDialog;
    private TextView dlgStockNameTV;
    private TextView dlgStockCodeTV;
    private TextView dlgStockPriceTV;
    private TextView dlgStockAmountTV;
    private TextView dlgStockTotalTV;
    private TextView dlgConfirmTV;
    private TextView dlgCancelTV;

    private int color_up;
    private int color_down;

    private String securityExchange = "";
    private String securitySymbol = "";
    private String securityName = "";
    private int competitionId = 0;

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

    @Inject QuoteServiceWrapper quoteServiceWrapper;
    private QuoteDetail quoteDetail;
    private RefreshBuySellHandler refreshBuySellHandler = new RefreshBuySellHandler();
    private RefreshQueryPositionHandler refreshQueryPositionHandler = new RefreshQueryPositionHandler();
    private ArrayList<SecurityOptPositionActualDTO> securityOptPositionActualDTOs = new ArrayList();
    private SecurityOptPositionActualAdapter securityOptPositionActualAdapter;
    private double availableSells = 0;

    private boolean isRefresh = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tradeManager = TradeManager.getInstance(getActivity());

        color_up = getResources().getColor(R.color.number_up);
        color_down = getResources().getColor(R.color.number_down);
        securitySymbol = getArguments().getString(SecurityOptActivity.KEY_SECURITY_SYMBOL, "");
        securityExchange = getArguments().getString(SecurityOptActivity.KEY_SECURITY_EXCHANGE, "");
        securityName = getArguments().getString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, "");

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
        if (!TextUtils.isEmpty(securitySymbol) && !TextUtils.isEmpty(securityExchange)) {
            if (isSHASHE()) {
                quoteServiceWrapper.getQuoteDetails(securityExchange, securitySymbol, new RefreshBUYSELLCallback());
            }
        }
        queryPositionsRepeat();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isRefresh = false;
    }

    private void initViews(View view) {
        initSellBuyViews(view);
        availableLayout = (LinearLayout) view.findViewById(R.id.layout_available_money);
        sharesLayout = (LinearLayout) view.findViewById(R.id.layout_shares);
        sharesLayout.setVisibility(View.VISIBLE);
        availableLayout.setVisibility(View.GONE);
        availableSellTV = (TextView)view.findViewById(R.id.textview_available_sells);
        totalSellTV = (TextView)view.findViewById(R.id.textview_all_sells);
        buySellBtn = (Button) view.findViewById(R.id.button_security_opt_buy_sell);
        buySellBtn.setText(R.string.security_opt_sell);
        buySellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBuyConfirmDialog();
            }
        });
        positionsLV = (ListView) view.findViewById(R.id.listview_security_opt_positions);
        if (securityOptPositionActualAdapter == null) {
            securityOptPositionActualAdapter = new SecurityOptPositionActualAdapter(getActivity());
        }
        positionsLV.setAdapter(securityOptPositionActualAdapter);
        securityCodeTV = (TextView) view.findViewById(R.id.textview_security_code);
        securityCodeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterSearchPage();
            }
        });
        if (!TextUtils.isEmpty(securitySymbol)) {
            securityCodeTV.setText(securitySymbol + " " + securityName);
        }
        priceET = (EditText) view.findViewById(R.id.edittext_security_price);
        addOneTV = (TextView) view.findViewById(R.id.textview_security_opt_add);
        reduceOneTV = (TextView) view.findViewById(R.id.textview_security_opt_minus);
        addOneTV.setOnClickListener(this);
        reduceOneTV.setOnClickListener(this);
        decisionET = (EditText) view.findViewById(R.id.edittext_security_decision);
        oneFourIV = (ImageView) view.findViewById(R.id.security_opt_one_fourth);
        oneThirdIV = (ImageView) view.findViewById(R.id.security_opt_one_third);
        halfIV = (ImageView) view.findViewById(R.id.security_opt_half);
        allIV = (ImageView) view.findViewById(R.id.security_opt_all);
        oneFourIV.setOnClickListener(this);
        oneThirdIV.setOnClickListener(this);
        halfIV.setOnClickListener(this);
        allIV.setOnClickListener(this);
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

    private void showBuyConfirmDialog() {
        if (getActivity() == null) {
            return;
        }
        if (buyConfirmDialog == null) {
            buyConfirmDialog = new Dialog(getActivity());
            buyConfirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            buyConfirmDialog.setCanceledOnTouchOutside(false);
            buyConfirmDialog.setCancelable(true);
            buyConfirmDialog.setContentView(R.layout.dialog_security_opt_sell);
            dlgStockNameTV = (TextView) buyConfirmDialog.findViewById(R.id.dialog_security_name);
            dlgStockCodeTV = (TextView) buyConfirmDialog.findViewById(R.id.dialog_security_code);
            dlgStockPriceTV = (TextView) buyConfirmDialog.findViewById(R.id.dialog_security_price);
            dlgStockAmountTV = (TextView) buyConfirmDialog.findViewById(R.id.dialog_security_amount);
            dlgStockTotalTV = (TextView) buyConfirmDialog.findViewById(R.id.dialog_security_total);

            dlgCancelTV = (TextView) buyConfirmDialog.findViewById(R.id.dialog_cancel);
            dlgCancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (buyConfirmDialog != null) {
                        buyConfirmDialog.dismiss();
                    }
                }
            });
            dlgConfirmTV = (TextView) buyConfirmDialog.findViewById(R.id.dialog_confirm);
            dlgConfirmTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buyConfirmDialog.dismiss();
                    tradeStock();
                }

            });
        }
        if (TextUtils.isEmpty(securityName) || TextUtils.isEmpty(securitySymbol)) {
            return;
        }
        if (priceET.getText() == null) {
            return;
        }
        if (TextUtils.isEmpty(priceET.getText().toString())) {
            return;
        }
        if (decisionET.getText() == null) {
            return;
        }
        if (TextUtils.isEmpty(decisionET.getText().toString())) {
            return;
        }
        dlgStockNameTV.setText(securityName);
        dlgStockCodeTV.setText(securitySymbol);
        dlgStockPriceTV.setText(priceET.getText());
        dlgStockAmountTV.setText(decisionET.getText());
        int price = (int) (Double.valueOf(priceET.getText().toString()) * Integer.valueOf(decisionET.getText().toString()));
        dlgStockTotalTV.setText(String.valueOf(price));
        buyConfirmDialog.show();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.textview_security_opt_minus:
                reduceOne();
                break;
            case R.id.textview_security_opt_add:
                addOne();
                break;
            case R.id.security_opt_one_fourth:
                setBuyAmount(4);
                break;
            case R.id.security_opt_one_third:
                setBuyAmount(3);
                break;
            case R.id.security_opt_half:
                setBuyAmount(2);
                break;
            case R.id.security_opt_all:
                setBuyAmount(1);
                break;
        }
    }

    private void addOne() {
        if (priceET.getText() == null) {
            return;
        }
        String valueStr = priceET.getText().toString();
        if (TextUtils.isEmpty(valueStr)) {
            return;
        }
        double value = Double.valueOf(valueStr) + 0.01;
        if (quoteDetail == null) {
            return;
        }
        if ((quoteDetail.prec * 1.1) < value) {
            return;
        }
        DecimalFormat df = new DecimalFormat("#0.00");
        priceET.setText(df.format(value));
    }

    private void reduceOne() {
        if (priceET.getText() == null) {
            return;
        }
        String valueStr = priceET.getText().toString();
        if (TextUtils.isEmpty(valueStr)) {
            return;
        }
        double value = Double.valueOf(valueStr);
        if (value <= 0.01) {
            return;
        }
        value = value - 0.01;
        if (quoteDetail == null) {
            return;
        }
        if ((quoteDetail.prec * 0.9) > value) {
            return;
        }
        DecimalFormat df = new DecimalFormat("#0.00");
        priceET.setText(df.format(value));
    }

    private void setBuyAmount(int percent){
        if(availableSells <= 0){
            return;
        }
        double amount = availableSells/percent;
        if(amount < 1){
            return;
        }
        DecimalFormat df = new DecimalFormat("#0");
        decisionET.setText(df.format(amount));
        switch (percent){
            case 1:
                allIV.setImageResource(R.drawable.all);
                halfIV.setImageResource(R.drawable.half_normal);
                oneThirdIV.setImageResource(R.drawable.one_third_normal);
                oneFourIV.setImageResource(R.drawable.one_fourth_normal);
                break;
            case 2:
                allIV.setImageResource(R.drawable.all_normal);
                halfIV.setImageResource(R.drawable.half);
                oneThirdIV.setImageResource(R.drawable.one_third_normal);
                oneFourIV.setImageResource(R.drawable.one_fourth_normal);
                break;
            case 3:
                allIV.setImageResource(R.drawable.all_normal);
                halfIV.setImageResource(R.drawable.half_normal);
                oneThirdIV.setImageResource(R.drawable.one_third);
                oneFourIV.setImageResource(R.drawable.one_fourth_normal);
                break;
            case 4:
                allIV.setImageResource(R.drawable.all_normal);
                halfIV.setImageResource(R.drawable.half_normal);
                oneThirdIV.setImageResource(R.drawable.one_third_normal);
                oneFourIV.setImageResource(R.drawable.one_fourth);
                break;
        }
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
        if (priceET.getText() == null || TextUtils.isEmpty(priceET.getText().toString())) {
            if (quoteDetail.sp1 != null) {
                priceET.setText(String.valueOf(quoteDetail.sp1));
            } else if (quoteDetail.bp1 != null) {
                priceET.setText(String.valueOf(quoteDetail.bp1));
            }
        }
    }

    private void setStockPrice(double open, double data, TextView textView) {
        textView.setText(String.valueOf(data));
        if (data >= open) {
            textView.setTextColor(color_up);
        } else {
            textView.setTextColor(color_down);
        }
    }

    private String convertAmountDoubleToString(Integer value) {
        int valueNew = value / 100;
        if (valueNew > 10000) {
            double valueNewD = (double) valueNew / 10000.0;
            DecimalFormat df = new DecimalFormat("#.0");
            return df.format(valueNewD) + "万";
        } else {
            return String.valueOf(valueNew);
        }
    }

    private void enterSearchPage(){
        if(getActivity()!=null){
            getActivity().finish();
        }
        Bundle bundle = new Bundle();
        if(competitionId!=0){
            bundle.putInt(CompetitionSecuritySearchFragment.BUNLDE_COMPETITION_ID, competitionId);
            gotoDashboard(CompetitionSecuritySearchFragment.class.getName(), bundle);
            getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        } else {
            gotoDashboard(SearchUnitFragment.class.getName(), new Bundle());
            getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        }
    }

    private void gotoDashboard(String strFragment, Bundle bundle) {
        bundle.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, strFragment);
        ActivityHelper.launchDashboard(getActivity(), bundle);
    }

    private boolean isSHASHE(){
        if (securityExchange.equalsIgnoreCase("SHA") || securityExchange.equalsIgnoreCase("SHE")) {
            return true;
        }
        return false;
    }

    private boolean isNeedToRefresh() {
        if (TextUtils.isEmpty(securityExchange) || TextUtils.isEmpty(securitySymbol)) {
            return false;
        }
        if (securityExchange.equalsIgnoreCase("SHA") || securityExchange.equalsIgnoreCase("SHE")) {
            return true;
        }
        return false;
    }

    class RefreshBUYSELLCallback implements Callback<QuoteDetail> {

        @Override
        public void success(QuoteDetail quoteDetail, Response response) {
            SecurityOptActualSubSellFragment.this.quoteDetail = quoteDetail;
            if (securitySymbol.equals(quoteDetail.symb)) {
                setSellBuyData(quoteDetail);
            }
            onFinish();
        }

        @Override
        public void failure(RetrofitError error) {
            onFinish();
        }

        private void onFinish() {
            if (isNeedToRefresh()) {
                if(isRefresh) {
                    refreshBuySellHandler.sendEmptyMessageDelayed(-1, 5000);
                }
            }
        }
    }

    class RefreshBuySellHandler extends Handler {
        public void handleMessage(Message msg) {
            quoteServiceWrapper.getQuoteDetails(securityExchange, securitySymbol, new RefreshBUYSELLCallback());
        }
    }

    private void queryPositionsRepeat(){
        if(tradeManager==null || !tradeManager.isLogined() ){
            if(getActivity()!=null) {
                getActivity().finish();
            }
            return;
        }
        tradeManager.sendData(TradeInterface.ID_QUERY_POSITION, new IPackageProxy(){
            @Override
            public void onSend(TradeDataHelper helper) {
                helper.setStartPosition(0);
            }


            @Override
            public void onReceive(TradeDataHelper helper) {
                securityOptPositionActualDTOs.clear();
                int rowCount = helper.getRowCount();
                for(int i = 0; i < rowCount; i++) {
                    SecurityOptPositionActualDTO securityOptPositionActualDTO = new SecurityOptPositionActualDTO();
                    securityOptPositionActualDTO.sec_name = helper.get(i, "sec_name", "");
                    securityOptPositionActualDTO.sec_code = helper.get(i, "sec_code", null);
                    securityOptPositionActualDTO.profit = helper.get(i, "profit", 0.0);
                    securityOptPositionActualDTO.profit_ratio = helper.get(i, "profit_ratio", 0.0);
                    securityOptPositionActualDTO.buy_money = helper.get(i, "buy_money", 0.0);
                    securityOptPositionActualDTO.cost_price = helper.get(i, "cost_price", 0.0);
                    securityOptPositionActualDTO.current_amt = helper.get(i, "current_amt", 0.0);
                    securityOptPositionActualDTO.enable_amt = helper.get(i, "enable_amt", 0.0);
                    securityOptPositionActualDTOs.add(securityOptPositionActualDTO);
                }
                securityOptPositionActualAdapter.addData(securityOptPositionActualDTOs);
                displaySells();
                if (isNeedToRefresh()) {
                    if(isRefresh) {
                        refreshQueryPositionHandler.sendEmptyMessageDelayed(-1, 20000);
                    }
                }
            }

        });

    }

    class RefreshQueryPositionHandler extends Handler{
        public void handleMessage(Message msg) {
            queryPositionsRepeat();
        }
    }

    private void queryPositionsNoRepeat(){
        if(tradeManager==null || !tradeManager.isLogined() ){
            if(getActivity()!=null) {
                getActivity().finish();
            }
            return;
        }
        tradeManager.sendData(TradeInterface.ID_QUERY_POSITION, new IPackageProxy(){
            @Override
            public void onSend(TradeDataHelper helper) {
                helper.setStartPosition(0);
            }


            @Override
            public void onReceive(TradeDataHelper helper) {
                securityOptPositionActualDTOs.clear();
                int rowCount = helper.getRowCount();
                for(int i = 0; i < rowCount; i++) {
                    SecurityOptPositionActualDTO securityOptPositionActualDTO = new SecurityOptPositionActualDTO();
                    securityOptPositionActualDTO.sec_name = helper.get(i, "sec_name", "");
                    securityOptPositionActualDTO.sec_code = helper.get(i, "sec_code", null);
                    securityOptPositionActualDTO.profit = helper.get(i, "profit", 0.0);
                    securityOptPositionActualDTO.profit_ratio = helper.get(i, "profit_ratio", 0.0);
                    securityOptPositionActualDTO.buy_money = helper.get(i, "buy_money", 0.0);
                    securityOptPositionActualDTO.cost_price = helper.get(i, "cost_price", 0.0);
                    securityOptPositionActualDTO.current_amt = helper.get(i, "current_amt", 0.0);
                    securityOptPositionActualDTO.enable_amt = helper.get(i, "enable_amt", 0.0);
                    securityOptPositionActualDTOs.add(securityOptPositionActualDTO);
                }
                securityOptPositionActualAdapter.addData(securityOptPositionActualDTOs);
                displaySells();
            }
        });

    }

    private void displaySells(){
        if(totalSellTV == null || availableSellTV == null){
            return;
        }
        if(securityOptPositionActualDTOs == null){
            totalSellTV.setText("0");
            availableSellTV.setText("0");
            return;
        }
        if(TextUtils.isEmpty(securitySymbol) || TextUtils.isEmpty(securityExchange)){
            totalSellTV.setText("0");
            availableSellTV.setText("0");
            return;
        }
        for(SecurityOptPositionActualDTO securityOptPositionActualDTO : securityOptPositionActualDTOs){
            if(securityOptPositionActualDTO.sec_code.equals(securitySymbol)){
                DecimalFormat df = new DecimalFormat("#0");
                totalSellTV.setText(df.format(securityOptPositionActualDTO.current_amt));
                availableSellTV.setText(df.format(securityOptPositionActualDTO.enable_amt));
                availableSells = securityOptPositionActualDTO.enable_amt;
            }
        }
    }

    private void tradeStock(){
        if (decisionET.getText() == null) {
            return;
        }
        if (TextUtils.isEmpty(decisionET.getText().toString())) {
            return;
        }
        if (priceET.getText() == null) {
            return;
        }
        if (TextUtils.isEmpty(priceET.getText().toString())) {
            return;
        }
        final double quantity = Double.valueOf(decisionET.getText().toString());
        final double price = Double.valueOf(priceET.getText().toString());
        if (price <= 0) {
            THToast.show("股票价格错误");
            return;
        }
        if (quantity <= 0) {
            THToast.show("股票交易数量错误");
            return;
        }
        if(isSHASHE()) {
            if (quoteDetail != null && quoteDetail.prec != null) {
                if (price > (quoteDetail.prec * 1.11) || price < (quoteDetail.prec * 0.89)) {
                    THToast.show("股票价格错误");
                    return;
                }
            }
            if(tradeManager!=null){
                tradeManager.sendData(TradeInterface.ID_ENTRUST, new IPackageProxy() {

                    @Override
                    public void onSend(TradeDataHelper helper) {
                        helper.set(TradeInterface.KEY_MARKET_CODE, SecurityUtils.getMarketCodeBySymbol(securitySymbol));
                        helper.set(TradeInterface.KEY_ENTRUST_TYPE, "2");
                        SecAccountInfo secAccountInfo = tradeManager.getSecAccounts().get(0);
                        helper.set(TradeInterface.KEY_SEC_ACCOUNT, secAccountInfo.getAccount());
                        helper.set(TradeInterface.KEY_SEC_CODE, securitySymbol);
                        helper.set(TradeInterface.KEY_ENTRUST_PRICE, String.valueOf(price));
                        helper.set(TradeInterface.KEY_ENTRUST_AMT, String.valueOf(quantity));
                        helper.set(TradeInterface.KEY_MARKET_ORDER_TYPE, "");
                    }

                    @Override
                    public void onReceive(TradeDataHelper helper) {
                        String resultMsg = helper.getResultMsg();
                        if(!TextUtils.isEmpty(resultMsg)) {
                            THToast.show(resultMsg);
                        }
                        queryPositionsNoRepeat();
                    }

                    @Override
                    public void onRequestFail(String msg) {
                        THToast.show(msg);
                    }

                });
            }
        }
    }

}
