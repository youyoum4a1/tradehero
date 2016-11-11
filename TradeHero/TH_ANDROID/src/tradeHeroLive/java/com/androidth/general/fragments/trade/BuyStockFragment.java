package com.androidth.general.fragments.trade;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.android.internal.util.Predicate;
import com.androidth.general.R;
import com.androidth.general.api.live.LiveViewProvider;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.portfolio.PortfolioId;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.position.PositionDTOCompact;
import com.androidth.general.api.position.PositionDTOList;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.TransactionFormDTO;
import com.androidth.general.fragments.security.LiveQuoteDTO;
import com.androidth.general.models.number.THSignedNumber;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.view.DismissDialogAction0;
import com.androidth.general.utils.LiveConstants;
import com.androidth.general.utils.metrics.events.SharingOptionsEvent;

import javax.inject.Inject;

import retrofit.RetrofitError;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class BuyStockFragment extends AbstractStockTransactionFragment {
    private static final boolean IS_BUY = true;

    @SuppressWarnings("UnusedDeclaration")
    @Inject
    Context doNotRemoveOrItFails;

    public BuyStockFragment() {
        super();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected void setBuyEventFor(SharingOptionsEvent.Builder builder) {
        builder.setBuyEvent(IS_BUY);
    }

    @Override
    protected String getLabel(@NonNull LiveQuoteDTO quoteDTO) {
        if (quoteDTO.getAskPrice() == null) {
            return getString(R.string.na);
        }
        THSignedNumber bThSignedNumber = getFormattedPrice(quoteDTO.getAskPrice());
        return bThSignedNumber.toString();
    }

    @Override
    @Nullable
    protected Double getProfitOrLossUsd(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable LiveQuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition,
            @Nullable Integer quantity) {
        return null;
    }

    @Override
    @NonNull
    public String getCashShareLeft(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull LiveQuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition, int quantity) {
        return getRemainingWhenBuy(portfolioCompactDTO, quoteDTO, closeablePosition, quantity);
    }

    public String getCashShareLeft(Double tradeValue, @NonNull PortfolioCompactDTO portfolioCompactDTO)
    {
        Double remaining = portfolioCompactDTO.cashBalance - tradeValue;
        THSignedNumber thSignedNumber = THSignedNumber
                .builder(remaining)
                .withOutSign()
//                        .currency(portfolioCompactDTO.currencyDisplay)//disable currency
                .build();

        return thSignedNumber.toString();
    }


    @Override @Nullable protected Integer getMaxValue(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull LiveQuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition)
    {
        return getMaxPurchasableShares(portfolioCompactDTO, quoteDTO, closeablePosition);
    }

//    @Override protected int getCashShareLabel()
//    {
//        return R.string.buy_sell_cash_available;
//    }
//
//    @Override protected Boolean isBuyTransaction()
//    {
//        return true;
//    }

    @Override protected boolean hasValidInfo()
    {
        return hasValidInfoForBuy();
    }

    @Override protected Subscription getTransactionSubscription(TransactionFormDTO transactionFormDTO)
    {
        final ProgressDialog progressDialog = ProgressDialog.show(
                getActivity(),
                getActivity().getString(R.string.processing),
                getActivity().getString(R.string.alert_dialog_please_wait),
                true);
        LiveConstants.isInLiveMode = true; // TODO pls remove, only use for testing
        LiveConstants.hasLiveAccount = true; // TODO pls remove, only using for dev
        if(LiveConstants.isInLiveMode)
        {
            SecurityId sid = requisite.securityId;
            Log.d("BuyStockFragment.java", "getTransactionSubscription requisite.securityId.ayondoId: " + sid.getAyondoId() + " securityId.id: " + sid.getSecurityIdNumber());
            try {
                return AppObservable.bindSupportFragment(
                        this,
                        live1BServiceWrapper.doTransactionRx(requisite.securityId, transactionFormDTO, IS_BUY))
                        .observeOn(AndroidSchedulers.mainThread())
                        .finallyDo(new DismissDialogAction0(progressDialog))
                        .doOnUnsubscribe(new DismissDialogAction0(progressDialog))
                        .doOnError(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if (throwable != null) {
                                    if (throwable instanceof RetrofitError) {
                                        try {
                                            RetrofitError error = (RetrofitError) throwable;
                                            Log.d("BuyStockFragment.java", error.getResponse() + " " + error.toString() + " --URL--> " + error.getResponse().getUrl());
                                            if (error.getResponse() != null && error.getResponse().getStatus() == 302)
                                            {
                                                LiveViewProvider.showTradeHubLogin(BuyStockFragment.this, throwable);
                                                dismiss();
                                            }
                                            else if (error.getResponse() != null && error.getResponse().getStatus() == 404)
                                                Toast.makeText(getContext(), "Error connecting to service: " + error.getResponse() + " --body-- " + error.getBody().toString(), Toast.LENGTH_LONG).show();
                                            else {
                                                Toast.makeText(getContext(), "Error in stock purchase: " + error.getResponse() + " --body-- " + error.getBody().toString(), Toast.LENGTH_LONG).show();
                                                Log.d("BuyStockFragment.java", "Error: " + error.getResponse() + " " + error.getBody().toString() + " --URL--> " + error.getResponse().getUrl());
                                            }
                                        }
                                        catch (Exception ex)
                                        {
                                            Toast.makeText(getContext(),"Unknown error when calling server: " + ex.toString(), Toast.LENGTH_LONG);
                                        }
                                    }
                                }
                            }
                        })
                        .subscribe(new LiveBuySellObserver(currentUserId.get(), requisite.securityId.getAyondoId()));

            }
            catch (IllegalStateException ex) {
                Toast.makeText(getContext(), "Error connecting to service: " + ex.toString(), Toast.LENGTH_LONG).show();
                return null;
            }
        }

        return AppObservable.bindSupportFragment(
                this,
                securityServiceWrapper.doTransactionRx(requisite.securityId, transactionFormDTO, IS_BUY))
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(new DismissDialogAction0(progressDialog))
                .doOnUnsubscribe(new DismissDialogAction0(progressDialog))
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.v(getTag(), "!!!Buy error: "+throwable.getLocalizedMessage());
                    }
                })
                .subscribe(new BuySellObserver(requisite.securityId, transactionFormDTO, IS_BUY));
    }

    @Nullable @Override public Double getPriceCcy(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable LiveQuoteDTO quoteDTO)
    {
        if (quoteDTO == null)
        {
            return null;
        }

        return quoteDTO.getPriceRefCcy(portfolioCompactDTO, IS_BUY);
    }

    @NonNull @Override protected Predicate<PositionDTO> getCloseablePositionPredicate(
            @NonNull PositionDTOList positionDTOs,
            @NonNull final PortfolioId portfolioId)
    {
        return new Predicate<PositionDTO>()
        {
            @Override public boolean apply(PositionDTO positionDTO)
            {
                return positionDTO.portfolioId.equals(portfolioId.key)
                        && positionDTO.shares != null
                        && positionDTO.shares < 0;
            }
        };
    }

//    @Override protected void initSecurityRelatedInfo(@Nullable SecurityCompactDTO securityCompactDTO)
//    {
//        setActionBarTitle(getString(R.string.transaction_title_buy,
//                securityCompactDTO != null ? securityCompactDTO.getExchangeSymbol() : getString(R.string.stock)));
//    }

    protected boolean hasValidInfoForBuy()
    {
        return usedDTO.securityCompactDTO != null
                && usedDTO.quoteDTO != null
                && usedDTO.quoteDTO.getAskPrice() != null;
    }

}
