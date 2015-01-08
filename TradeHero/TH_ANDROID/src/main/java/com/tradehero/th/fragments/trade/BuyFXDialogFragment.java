package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.utils.metrics.events.SharingOptionsEvent;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

public class BuyFXDialogFragment extends AbstractFXTransactionDialogFragment
{
    private static final boolean IS_BUY = true;

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    public BuyFXDialogFragment()
    {
        super();
    }

    @Override protected void setBuyEventFor(SharingOptionsEvent.Builder builder)
    {
        builder.setBuyEvent(IS_BUY);
    }

    @Override protected String getLabel()
    {
        THSignedNumber bThSignedNumber = THSignedMoney
                .builder(quoteDTO.ask)
                .withOutSign()
                .relevantDigitCount(10)
                .currency("")
                .build();
        return getString(R.string.buy_sell_dialog_buy, bThSignedNumber.toString());
    }

    @Override @Nullable protected Double getProfitOrLossUsd()
    {
        if (positionDTOCompactList == null || portfolioCompactDTO == null)
        {
            return null;
        }
        if(positionDTOCompactList.getShareCountIn(portfolioCompactDTO.getPortfolioId()).intValue() >= 0)
        {
            return null;
        } 

        double total = positionDTOCompactList.getUnRealizedPLRefCcy(quoteDTO,portfolioCompactDTO,positionDTOCompactList);
        double result = (total * (double)mTransactionQuantity / Math.abs((double)positionDTOCompactList.getShareCountIn(portfolioCompactDTO.getPortfolioId()).intValue()));
        return result;
    }

    @Override protected int getCashLeftLabelResId()
    {
        Integer maxSellableShares = getMaxSellableShares();
        if (maxSellableShares == null || maxSellableShares >= 0)
        {
            return R.string.buy_sell_fx_cash_left;
        }
        else
        {
            return R.string.buy_sell_fx_quantity_left;
        }
    }

    @Override @NonNull public String getCashShareLeft()
    {
        String cashLeftText = getResources().getString(R.string.na);
        if (quoteDTO != null && portfolioCompactDTO != null)
        {
            Double priceRefCcy = getPriceCcy();
            if (priceRefCcy != null && portfolioCompactDTO != null)
            {
                Integer maxSellableShares = getMaxSellableShares();
                if (maxSellableShares != null && maxSellableShares < 0)
                {
                    cashLeftText = THSignedNumber.builder(-maxSellableShares - mTransactionQuantity)
                            .relevantDigitCount(1)
                            .withOutSign()
                            .build().toString();
                }
                else
                {
                    double availableRefCcy;
                    if (portfolioCompactDTO.marginAvailableRefCcy != null
                            && portfolioCompactDTO.leverage != null)
                    {
                        availableRefCcy = portfolioCompactDTO.marginAvailableRefCcy * portfolioCompactDTO.leverage;
                    }
                    else
                    {
                        Timber.e(new IllegalStateException(), "Unable to proper collect leverage as FX, %s", portfolioCompactDTO);
                        availableRefCcy = portfolioCompactDTO.cashBalanceRefCcy;
                    }

                    if (priceRefCcy != null)
                    {
                        double value = mTransactionQuantity * priceRefCcy;
                        THSignedNumber thSignedNumber = THSignedMoney
                                .builder((availableRefCcy - value)/portfolioCompactDTO.leverage)
                                .withOutSign()
                                .currency(portfolioCompactDTO.currencyDisplay)
                                .build();
                        cashLeftText = thSignedNumber.toString();
                    }
                }
            }
        }

        return cashLeftText;
    }

    @Override @Nullable protected Integer getMaxValue()
    {
        if (positionDTOCompactList == null || quoteDTO == null || portfolioCompactDTO == null)
        {
            return null;
        }
        Integer maxSellableShares = getMaxSellableShares();
        if (maxSellableShares != null && maxSellableShares < 0)
        {
            return -maxSellableShares;
        }
        else
        {
            return getMaxPurchasableShares();
        }
    }

    @Override protected boolean hasValidInfo()
    {
        return hasValidInfoForBuy();
    }

    @Override protected boolean isQuickButtonEnabled()
    {
        if (quoteDTO == null || quoteDTO.ask == null)
        {
            return false;
        }
        return true;
    }

    @Override protected double getQuickButtonMaxValue()
    {
        if (portfolioCompactDTO != null)
        {
            return portfolioCompactDTO.cashBalanceRefCcy;
        }
        return 0;
    }

    @Override protected Subscription getTransactionSubscription(TransactionFormDTO transactionFormDTO)
    {
        return AndroidObservable.bindFragment(
                this,
                securityServiceWrapper.doTransactionRx(securityId, transactionFormDTO, IS_BUY))
                .subscribe(new BuySellObserver(IS_BUY));
    }

    @Override public Double getPriceCcy()
    {
        if (quoteDTO == null)
        {
            return null;
        }

        return quoteDTO.getPriceRefCcy(portfolioCompactDTO, IS_BUY);
    }

    protected boolean hasValidInfoForBuy()
    {
        return securityId != null
                && securityCompactDTO != null
                && quoteDTO != null
                && quoteDTO.ask != null;
    }

    @Nullable public Integer getMaxPurchasableShares()
    {
        return portfolioCompactDTOUtil.getMaxPurchasableSharesForFX(
                portfolioCompactDTO,
                quoteDTO, true);
    }
}
