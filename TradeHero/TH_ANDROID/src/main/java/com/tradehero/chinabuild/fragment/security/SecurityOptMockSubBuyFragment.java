package com.tradehero.chinabuild.fragment.security;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.tradehero.chinabuild.data.QuoteDetail;
import com.tradehero.chinabuild.data.SignedQuote;
import com.tradehero.chinabuild.fragment.competition.CompetitionSecuritySearchFragment;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.common.utils.IOUtils;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.DataUtils;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;

/**
 * Buy Page
 * <p/>
 * Created by palmer on 15/7/6.
 */
public class SecurityOptMockSubBuyFragment extends Fragment implements View.OnClickListener {

    private Button buySellBtn;
    private ListView positionsLV;
    private TextView securityCodeTV;
    private EditText priceET;
    private TextView addOneTV;
    private TextView reduceOneTV;
    private LinearLayout availableLayout;
    private LinearLayout sharesLayout;
    private TextView availableCashTV;

    private EditText decisionET;
    private ImageView oneFourIV;
    private ImageView oneThirdIV;
    private ImageView halfIV;
    private ImageView allIV;

    //Dialog
    private Dialog buyConfirmDialog;
    private TextView dlgStockNameTV;
    private TextView dlgStockCodeTV;
    private TextView dlgStockPriceTV;
    private TextView dlgStockAmountTV;
    private TextView dlgStockTotalTV;
    private TextView dlgConfirmTV;
    private TextView dlgCancelTV;

    private String securityName = "";
    @Inject QuoteServiceWrapper quoteServiceWrapper;
    private QuoteDetail quoteDetail;
    private SecurityOptPositionsList securityOptPositionDTOs;
    private String securityExchange = "";
    private String securitySymbol = "";
    private int portfolioId = 0;
    private int competitionId = 0;
    private PortfolioId portfolioIdObj;

    private QuoteDTO quoteDTO;

    //Retrieve portfolio
    private PortfolioDTO portfolioDTO;
    @Inject CurrentUserId currentUserId;
    @Inject PortfolioServiceWrapper portfolioServiceWrapper;
    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject Converter converter;


    private SecurityOptPositionMockAdapter securityOptMockPositionAdapter;

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

    private boolean isRefresh = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        securityOptMockPositionAdapter = new SecurityOptPositionMockAdapter(getActivity());
        color_up = getResources().getColor(R.color.number_up);
        color_down = getResources().getColor(R.color.number_down);
        securitySymbol = getArguments().getString(SecurityOptActivity.KEY_SECURITY_SYMBOL, "");
        securityExchange = getArguments().getString(SecurityOptActivity.KEY_SECURITY_EXCHANGE, "");
        securityName = getArguments().getString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, "");
        competitionId = getArguments().getInt(CompetitionSecuritySearchFragment.BUNDLE_COMPETITION_ID, 0);
        THLog.d("competitionId " + competitionId);
        if(getArguments().containsKey(SecurityOptActivity.KEY_PORTFOLIO_ID)) {
            portfolioIdObj = getPortfolioId();
            if (competitionId != 0) {
                THLog.d("portfolioId " + portfolioId);
                portfolioId = portfolioIdObj.key;
            }
        }
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
        isRefresh = true;
        if (!TextUtils.isEmpty(securitySymbol) && !TextUtils.isEmpty(securityExchange)) {
            if(!isSHASHE()){
                priceET.setEnabled(false);
                addOneTV.setEnabled(false);
                addOneTV.setBackgroundResource(R.drawable.security_opt_add_disable);
                reduceOneTV.setEnabled(false);
                reduceOneTV.setBackgroundResource(R.drawable.security_opt_minus_disable);
            }
            quoteServiceWrapper.getQuoteDetails(securityExchange, securitySymbol, new RefreshBUYSELLCallback());
            retrieveQuoteDTO();
        }
        if (portfolioId == 0 && competitionId ==0) {
            retrieveMainPositions();
        } else if(portfolioId!=0 && competitionId!=0){
            retrieveCompetitionPositions();
        }
        //Retrieve user portfolio
        retrieveUserInformation();
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
        sharesLayout.setVisibility(View.GONE);
        availableLayout.setVisibility(View.VISIBLE);
        availableCashTV = (TextView)view.findViewById(R.id.textview_available_cash);
        buySellBtn = (Button) view.findViewById(R.id.button_security_opt_buy_sell);
        buySellBtn.setText(R.string.security_opt_buy);
        buySellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBuyConfirmDialog();
            }
        });
        positionsLV = (ListView) view.findViewById(R.id.listview_security_opt_positions);
        if (securityOptMockPositionAdapter == null) {
            securityOptMockPositionAdapter = new SecurityOptPositionMockAdapter(getActivity());
        }
        positionsLV.setAdapter(securityOptMockPositionAdapter);
        positionsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SecurityOptPositionMockDTO securityOptPositionMockDTO = securityOptMockPositionAdapter.getItem(position);
                if(securityOptPositionMockDTO!=null){
                    boolean isEmptyBefore = false;
                    if(TextUtils.isEmpty(securityExchange) && TextUtils.isEmpty(securitySymbol)){
                        isEmptyBefore = true;
                    }
                    if(TextUtils.isEmpty(securityOptPositionMockDTO.symbol) || TextUtils.isEmpty(securityOptPositionMockDTO.exchange)
                            || TextUtils.isEmpty(securityOptPositionMockDTO.name) || getActivity() == null){
                        return;
                    }
                    securitySymbol = securityOptPositionMockDTO.symbol;
                    securityName = securityOptPositionMockDTO.name;
                    securityExchange = securityOptPositionMockDTO.exchange;
                    securityCodeTV.setText(securitySymbol + " " + securityName);
                    clearAllSellBuy();
                    decisionET.setText("");
                    priceET.setText("");
                    if(!isSHASHE()){
                        priceET.setEnabled(false);
                        addOneTV.setEnabled(false);
                        addOneTV.setBackgroundResource(R.drawable.security_opt_add_disable);
                        reduceOneTV.setEnabled(false);
                        reduceOneTV.setBackgroundResource(R.drawable.security_opt_minus_disable);
                    } else {
                        priceET.setEnabled(true);
                        addOneTV.setEnabled(true);
                        addOneTV.setBackgroundResource(R.drawable.security_opt_add);
                        reduceOneTV.setEnabled(true);
                        reduceOneTV.setBackgroundResource(R.drawable.security_opt_minus);
                    }
                    if(isEmptyBefore) {
                        quoteServiceWrapper.getQuoteDetails(securityExchange, securitySymbol, new RefreshBUYSELLCallback());
                        retrieveQuoteDTO();
                    } else {
                        quoteServiceWrapper.getQuoteDetails(securityExchange, securitySymbol, new RefreshBUYSELLNoRepeatCallback());
                        retrieveQuoteDTONoRepeat();
                    }
                }
            }
        });
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

    private void showBuyConfirmDialog() {
        if (getActivity() == null) {
            return;
        }
        if (buyConfirmDialog == null) {
            buyConfirmDialog = new Dialog(getActivity());
            buyConfirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            buyConfirmDialog.setCanceledOnTouchOutside(false);
            buyConfirmDialog.setCancelable(true);
            buyConfirmDialog.setContentView(R.layout.dialog_security_opt_buy);
            dlgStockNameTV = (TextView)buyConfirmDialog.findViewById(R.id.dialog_security_name);
            dlgStockCodeTV = (TextView)buyConfirmDialog.findViewById(R.id.dialog_security_code);
            dlgStockPriceTV = (TextView)buyConfirmDialog.findViewById(R.id.dialog_security_price);
            dlgStockAmountTV = (TextView)buyConfirmDialog.findViewById(R.id.dialog_security_amount);
            dlgStockTotalTV = (TextView)buyConfirmDialog.findViewById(R.id.dialog_security_total);

            dlgCancelTV = (TextView) buyConfirmDialog.findViewById(R.id.dialog_cancel);
            dlgCancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (buyConfirmDialog != null) {
                        buyConfirmDialog.dismiss();
                    }
                }
            });
            dlgConfirmTV = (TextView)buyConfirmDialog.findViewById(R.id.dialog_confirm);
            dlgConfirmTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buyConfirmDialog.dismiss();
                    if(decisionET.getText() == null){
                        return;
                    }
                    if(TextUtils.isEmpty(decisionET.getText().toString())){
                        return;
                    }
                    if(priceET.getText() == null){
                        return;
                    }
                    if(TextUtils.isEmpty(priceET.getText().toString())){
                        return;
                    }
                    int quantity = Integer.valueOf(decisionET.getText().toString());
                    double price = Double.valueOf(priceET.getText().toString());
                    if (price <= 0) {
                        THToast.show("股票价格错误");
                        return;
                    }
                    if(quantity <= 0){
                        THToast.show("股票交易数量错误");
                        return;
                    }
                    if(quoteDTO!=null && quoteDTO.toUSDRate!=null) {
                        double totalAmount = price * quantity * quoteDTO.toUSDRate;
                        if (portfolioDTO == null || totalAmount > portfolioDTO.cashBalance) {
                            THToast.show("买入股票价格超出可用本金");
                            return;
                        }
                    }
                    if(isSHASHE()){
                        if(getActivity()!=null) {
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(SecurityOptActivity.INTENT_START_TRADING));
                        }
                        securityServiceWrapper.order(portfolioId, securityExchange, securitySymbol, quantity, price, new Callback<Response>() {
                            @Override
                            public void success(Response value, Response response) {
                                if(isSHASHE()){
                                    THToast.show("委托成功");
                                } else {
                                    THToast.show("交易成功");
                                }
                                if(portfolioId == 0){
                                    //Main postions
                                    retrieveMainPositionsNoRepeat();
                                } else {
                                    retrieveCompetitionPositionsNoRepeat();
                                }
                                retrieveUserInformation();
                                if(getActivity()!=null) {
                                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(SecurityOptMockSubSellFragment.INTENT_REFRESH_POSITION_REQUIRED));
                                }
                                onFinish();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                THException thException = new THException(error);
                                THToast.show(thException.getMessage());
                                onFinish();
                            }

                            private void onFinish(){
                                if(getActivity()!=null) {
                                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(SecurityOptActivity.INTENT_END_TRADING));
                                }
                                if(decisionET!=null){
                                    decisionET.setText("");
                                }
                            }
                        });
                    } else {
                        if(quoteDTO == null) {
                            return;
                        }
                        TransactionFormDTO transactionFormDTO = buildTransactionFormDTO();
                        if(transactionFormDTO == null){
                            return;
                        }
                        if(getActivity()!=null) {
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(SecurityOptActivity.INTENT_START_TRADING));
                        }
                        securityServiceWrapper.buy(securityExchange, securitySymbol, buildTransactionFormDTO(), new Callback<SecurityPositionDetailDTO>() {
                            @Override
                            public void success(SecurityPositionDetailDTO securityPositionDetailDTO, Response response) {
                                if(isSHASHE()){
                                    THToast.show("委托成功");
                                } else {
                                    THToast.show("交易成功");
                                }
                                if(portfolioId == 0){
                                    retrieveMainPositionsNoRepeat();
                                } else {
                                    retrieveCompetitionPositionsNoRepeat();
                                }
                                retrieveUserInformation();
                                if(getActivity()!=null) {
                                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(SecurityOptMockSubSellFragment.INTENT_REFRESH_POSITION_REQUIRED));
                                }
                                onFinish();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                THException thException = new THException(error);
                                THToast.show(thException.toString());
                                onFinish();
                            }

                            private void onFinish(){
                                if(getActivity()!=null) {
                                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(SecurityOptActivity.INTENT_END_TRADING));
                                }
                                if(decisionET!=null){
                                    decisionET.setText("");
                                }
                            }
                        });
                    }
                }
            });

        }
        if(TextUtils.isEmpty(securityName) || TextUtils.isEmpty(securitySymbol)){
            return;
        }
        if(priceET.getText()==null){
            return;
        }
        if(TextUtils.isEmpty(priceET.getText().toString())){
            return;
        }
        if(decisionET.getText()==null){
            return;
        }
        if(TextUtils.isEmpty(decisionET.getText().toString())){
            return;
        }
        dlgStockNameTV.setText(securityName);
        dlgStockCodeTV.setText(securitySymbol);
        dlgStockPriceTV.setText(priceET.getText());
        dlgStockAmountTV.setText(decisionET.getText());
        int price = (int)(Double.valueOf(priceET.getText().toString()) * Integer.valueOf(decisionET.getText().toString()));
        dlgStockTotalTV.setText(String.valueOf(price));
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
        if (priceET.getText() == null || TextUtils.isEmpty(priceET.getText().toString())) {
            if (quoteDetail.sp1 != null) {
                priceET.setText(String.format("%.2f", quoteDetail.sp1));
            } else if (quoteDetail.bp1 != null) {
                priceET.setText(String.format("%.2f", quoteDetail.bp1));
            }
        }
    }

    private void clearAllSellBuy(){
        buy1Price.setText("- -");
        buy1Amount.setText("- -");
        buy2Price.setText("- -");
        buy2Amount.setText("- -");
        buy3Price.setText("- -");
        buy3Amount.setText("- -");
        buy4Price.setText("- -");
        buy4Amount.setText("- -");
        buy5Price.setText("- -");
        buy5Amount.setText("- -");

        sell1Price.setText("- -");
        sell1Amount.setText("- -");
        sell2Price.setText("- -");
        sell2Amount.setText("- -");
        sell3Price.setText("- -");
        sell3Amount.setText("- -");
        sell4Price.setText("- -");
        sell4Amount.setText("- -");
        sell5Price.setText("- -");
        sell5Amount.setText("- -");

    }

    private String convertAmountDoubleToString(Integer value) {
        int valueNew = value / 100;
        if (valueNew > 10000) {
            double valueNewD = (double) valueNew / 10000.0;
            return DataUtils.keepTwoDecimal(valueNewD) + "万";
        } else {
            return String.valueOf(valueNew);
        }
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

    private void setBuyAmount(int percent){
        if(portfolioDTO == null || quoteDTO ==null || quoteDTO.toUSDRate == null){
            return;
        }
        double cashNeed = portfolioDTO.cashBalance;
        if(cashNeed <= 0){
            return;
        }
        double price;
        if(priceET.getText() == null){
            return;
        }
        String priceStr = priceET.getText().toString();
        if(TextUtils.isEmpty(priceStr)){
            return;
        }
        price = Double.valueOf(priceStr);
        if(price == 0){
            return;
        }
        int amount = (int)((cashNeed )/(percent * price * quoteDTO.toUSDRate));
        decisionET.setText(String.valueOf(amount));
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


    class RefreshBUYSELLCallback implements Callback<QuoteDetail> {

        @Override
        public void success(QuoteDetail quoteDetail, Response response) {
            SecurityOptMockSubBuyFragment.this.quoteDetail = quoteDetail;
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
                    RefreshBuySellHandler refreshBuySellHandler= new RefreshBuySellHandler();
                    refreshBuySellHandler.sendEmptyMessageDelayed(-1, 5000);
                }
            }
        }
    }

    class RefreshBUYSELLNoRepeatCallback implements Callback<QuoteDetail> {
        @Override
        public void success(QuoteDetail quoteDetail, Response response) {
            SecurityOptMockSubBuyFragment.this.quoteDetail = quoteDetail;
            if (securitySymbol.equals(quoteDetail.symb)) {
                setSellBuyData(quoteDetail);
            }
        }

        @Override
        public void failure(RetrofitError error) {
        }
    }


    class RefreshBuySellHandler extends Handler {
        public void handleMessage(Message msg) {
            THLog.d("download buy .... refresh" + securityName + " " + securitySymbol);
            quoteServiceWrapper.getQuoteDetails(securityExchange, securitySymbol, new RefreshBUYSELLCallback());
        }
    }

    private void setStockPrice(double open, double data, TextView textView) {
        textView.setText(String.format("%.2f", data));
        if (data >= open) {
            textView.setTextColor(color_up);
        } else {
            textView.setTextColor(color_down);
        }
    }

    private boolean isNeedToRefresh() {
        if (TextUtils.isEmpty(securityExchange) || TextUtils.isEmpty(securitySymbol)) {
            return false;
        }
        return true;
    }

    private boolean isSHASHE(){
        if (securityExchange.equalsIgnoreCase("SHA") || securityExchange.equalsIgnoreCase("SHE")) {
            return true;
        }
        return false;
    }

    private void gotoDashboard(String strFragment, Bundle bundle) {
        bundle.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, strFragment);
        ActivityHelper.launchDashboard(getActivity(), bundle);
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
        priceET.setText(DataUtils.keepTwoDecimal(value));
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
        priceET.setText(DataUtils.keepTwoDecimal(value));
    }

    private void retrieveMainPositions() {
        THLog.d("buy retrieveMainPositions");
        quoteServiceWrapper.retrieveMainPositions(new RetrievePositionsCallback());
    }

    private void retrieveMainPositionsNoRepeat(){
        quoteServiceWrapper.retrieveMainPositions(new Callback<SecurityOptPositionsList>() {
            @Override
            public void success(SecurityOptPositionsList securityOptPositionDTOs, Response response) {
                SecurityOptMockSubBuyFragment.this.securityOptPositionDTOs = securityOptPositionDTOs;
                securityOptMockPositionAdapter.addData(securityOptPositionDTOs);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void retrieveCompetitionPositions(){
        THLog.d("buy retrieveCompetitionPositions");
        quoteServiceWrapper.retrieveCompetitionPositions(portfolioId, new RetrievePositionsCallback());
    }

    private void retrieveCompetitionPositionsNoRepeat() {
        quoteServiceWrapper.retrieveCompetitionPositions(portfolioId, new Callback<SecurityOptPositionsList>() {
            @Override
            public void success(SecurityOptPositionsList securityOptPositionDTOs, Response response) {
                SecurityOptMockSubBuyFragment.this.securityOptPositionDTOs = securityOptPositionDTOs;
                securityOptMockPositionAdapter.addData(securityOptPositionDTOs);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    class RetrievePositionsCallback implements Callback<SecurityOptPositionsList> {

        @Override
        public void success(SecurityOptPositionsList securityOptPositionDTOs, Response response) {
            SecurityOptMockSubBuyFragment.this.securityOptPositionDTOs = securityOptPositionDTOs;
            securityOptMockPositionAdapter.addData(securityOptPositionDTOs);

            onFinish();
        }

        @Override
        public void failure(RetrofitError error) {
            onFinish();
        }

        private void onFinish() {
            if(isRefresh){
                RefreshPositionsHandler refreshPositionsHandler = new RefreshPositionsHandler();
                refreshPositionsHandler.sendEmptyMessageDelayed(-1, 60000);
            }
        }
    }

    class RefreshPositionsHandler extends Handler {
        public void handleMessage(Message msg) {
            if (portfolioId == 0) {
                retrieveMainPositions();
            } else {
                retrieveCompetitionPositions();
            }
        }
    }


    private void retrieveUserInformation(){
        if(portfolioId == 0){
            portfolioServiceWrapper.getMainPortfolio(currentUserId.toUserBaseKey().getUserId(), new Callback<PortfolioDTO>() {
                @Override
                public void success(PortfolioDTO portfolioDTO, Response response) {
                    SecurityOptMockSubBuyFragment.this.portfolioDTO = portfolioDTO;
                    displayAvailableBalance();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        } else {
            if(competitionId == 0){
                return;
            }
            portfolioServiceWrapper.getCompetitionPortfolio(competitionId, new Callback<PortfolioDTO>() {
                @Override
                public void success(PortfolioDTO portfolioDTO, Response response) {
                    SecurityOptMockSubBuyFragment.this.portfolioDTO = portfolioDTO;
                    displayAvailableBalance();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    private void displayAvailableBalance(){
        if(portfolioDTO == null) {
            return;
        }
        if(availableCashTV!=null) {
            int balance = (int)portfolioDTO.cashBalance;
            availableCashTV.setText("$"+String.valueOf(balance));
        }
    }

    class RefreshQuoteHandler extends Handler {
        public void handleMessage(Message msg) {
            retrieveQuoteDTO();
        }
    }
    class QuoteNoSHASHECallback implements Callback<Response> {

        @Override
        public void success(Response rawResponse, Response response) {
            try {
                byte[] bytes = IOUtils.streamToBytes(rawResponse.getBody().in());
                SignedQuote signedQuote = (SignedQuote) converter.fromBody(new TypedByteArray(rawResponse.getBody().mimeType(), bytes), SignedQuote.class);
                QuoteDTO quoteDTO = signedQuote.signedObject;
                quoteDTO.rawResponse = new String(bytes);
                SecurityOptMockSubBuyFragment.this.quoteDTO = quoteDTO;
                if (!isSHASHE() && quoteDTO.ask != null) {
                    sell1Price.setText(DataUtils.keepTwoDecimal(quoteDTO.ask));
                    buy1Price.setText(DataUtils.keepTwoDecimal(quoteDTO.ask));
                    priceET.setText(DataUtils.keepTwoDecimal(quoteDTO.ask));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            onFinish();
        }

        @Override
        public void failure(RetrofitError error) {
            onFinish();
        }

        private void onFinish() {
            if (isRefresh && !TextUtils.isEmpty(securitySymbol) && !TextUtils.isEmpty(securityExchange)) {
                RefreshQuoteHandler refreshQuoteHandler = new RefreshQuoteHandler();
                refreshQuoteHandler.sendEmptyMessageDelayed(-1, 10000);
            }

        }
    }

    class QuoteNoSHASHENoRepeatCallback implements Callback<Response> {

        @Override
        public void success(Response rawResponse, Response response) {
            try {
                byte[] bytes = IOUtils.streamToBytes(rawResponse.getBody().in());
                SignedQuote signedQuote = (SignedQuote) converter.fromBody(new TypedByteArray(rawResponse.getBody().mimeType(), bytes), SignedQuote.class);
                QuoteDTO quoteDTO = signedQuote.signedObject;
                quoteDTO.rawResponse = new String(bytes);
                SecurityOptMockSubBuyFragment.this.quoteDTO = quoteDTO;
                if (!isSHASHE() && quoteDTO.ask != null) {
                    sell1Price.setText(DataUtils.keepTwoDecimal(quoteDTO.ask));
                    buy1Price.setText(DataUtils.keepTwoDecimal(quoteDTO.ask));
                    priceET.setText(DataUtils.keepTwoDecimal(quoteDTO.ask));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void failure(RetrofitError error) {}
    }

    private void retrieveQuoteDTO(){
        quoteServiceWrapper.getQuote(securityExchange, securitySymbol, new QuoteNoSHASHECallback());
    }

    private void retrieveQuoteDTONoRepeat(){
        quoteServiceWrapper.getQuote(securityExchange, securitySymbol, new QuoteNoSHASHENoRepeatCallback());
    }

    private TransactionFormDTO buildTransactionFormDTO(){
        if (quoteDTO == null) {
            return null;
        }
        if(decisionET == null || decisionET.getText() == null || TextUtils.isEmpty(decisionET.getText().toString())){
            return null;
        }
        if(portfolioId == 0) {
            if(portfolioDTO == null) {
                return null;
            }
            int mTransactionQuantity = Integer.valueOf(decisionET.getText().toString());
            return new TransactionFormDTO(null, null, null, null, null, null, null, false, null,
                    quoteDTO.rawResponse, mTransactionQuantity, portfolioDTO.id);
        } else {
            if(portfolioIdObj == null || portfolioIdObj.key == null){
                return null;
            }
            int mTransactionQuantity = Integer.valueOf(decisionET.getText().toString());
            return new TransactionFormDTO(null, null, null, null, null, null, null, false, null,
                    quoteDTO.rawResponse, mTransactionQuantity, portfolioIdObj.key);
        }
    }


    protected PortfolioId getPortfolioId() {
        if (this.portfolioIdObj == null) {
            this.portfolioIdObj = new PortfolioId(getArguments().getBundle(SecurityOptActivity.KEY_PORTFOLIO_ID));
        }
        return portfolioIdObj;
    }

    private void enterSearchPage(){
        if(getActivity()==null){
            return;
        }
        getActivity().finish();
        Bundle bundle = new Bundle();
        if(competitionId!=0){
            if(portfolioIdObj!=null) {
                bundle.putBundle(SecurityOptActivity.KEY_PORTFOLIO_ID, portfolioIdObj.getArgs());
            }
            bundle.putInt(CompetitionSecuritySearchFragment.BUNDLE_COMPETITION_ID, competitionId);
            bundle.putBoolean(CompetitionSecuritySearchFragment.BUNDLE_GO_TO_BUY_SELL_DIRECTLY, true);
            bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_BUY);
            gotoDashboard(CompetitionSecuritySearchFragment.class.getName(), bundle);
            getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        } else {
            bundle.putBoolean(SearchUnitFragment.BUNDLE_GO_TO_BUY_SELL_DIRECTLY, true);
            bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_BUY);
            gotoDashboard(SearchUnitFragment.class.getName(), bundle);
            getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        }
    }
}
