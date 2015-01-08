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

public class SellFXDialogFragment extends AbstractFXTransactionDialogFragment
{
    private static final boolean IS_BUY = false;

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    public SellFXDialogFragment()
    {
        super();
    }

    @Override protected void setBuyEventFor(SharingOptionsEvent.Builder builder)
    {
        builder.setBuyEvent(IS_BUY);
    }

    @Override protected String getLabel()
    {
        THSignedNumber sthSignedNumber = THSignedMoney
                .builder(quoteDTO.bid)
                .withOutSign()
                .relevantDigitCount(10)
                .currency("")
                .build();
        return getString(R.string.buy_sell_dialog_sell, sthSignedNumber.toString());
    }

    @Override @Nullable protected Double getProfitOrLossUsd()
    {
        if (positionDTOCompactList == null || portfolioCompactDTO == null)
        {
            return null;
        }
        if(positionDTOCompactList.getMaxSellableShares(quoteDTO,portfolioCompactDTO).intValue() <= 0)
        {
            return null;
        }
        Double netProceedsUsd = positionDTOCompactList.getNetSellProceedsUsd(
                mTransactionQuantity,
                quoteDTO,
                getPortfolioId(),
                true,
                portfolioCompactDTO.getProperTxnCostUsd());
        Double totalSpentUsd = positionDTOCompactList.getSpentOnQuantityUsd(mTransactionQuantity, portfolioCompactDTO);
        if (netProceedsUsd == null || totalSpentUsd == null)
        {
            return null;
        }
        return netProceedsUsd - totalSpentUsd;
    }

    @Override protected int getCashLeftLabelResId()
    {
        Integer maxSellableShares = getMaxSellableShares();
        if (maxSellableShares == null || maxSellableShares <= 0)
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
        String shareLeftText = getResources().getString(R.string.na);
        if (quoteDTO != null || portfolioCompactDTO != null)
        {
            Double priceRefCcy = getPriceCcy();
            if (priceRefCcy != null && portfolioCompactDTO != null)
            {
                Integer maxSellableShares = getMaxSellableShares();
                if (maxSellableShares != null && maxSellableShares > 0)
                {
                    shareLeftText = THSignedNumber.builder(maxSellableShares - mTransactionQuantity)
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
                        shareLeftText = thSignedNumber.toString();
                    }
                }
            }
        }
        return shareLeftText;
    }

    @Override @Nullable protected Integer getMaxValue()
    {
        if (positionDTOCompactList == null || quoteDTO == null || portfolioCompactDTO == null)
        {
            return null;
        }
        Integer maxSellableShares = getMaxSellableShares();
        if (maxSellableShares == null || maxSellableShares <= 0)
        {
            return getMaxPurchasableShares();
        }
        return maxSellableShares;
    }

    @Override protected boolean hasValidInfo()
    {
        return hasValidInfoForSell();
    }

    @Override protected boolean isQuickButtonEnabled()
    {
        if ((quoteDTO == null || quoteDTO.bid == null || quoteDTO.toUSDRate == null))
        {
            return false;
        }
        return true;
    }

    @Override protected double getQuickButtonMaxValue()
    {
        Integer maxSellableShares = getMaxSellableShares();
        if (maxSellableShares != null && quoteDTO != null && quoteDTO.bid != null)
        {
            // TODO see other currencies
            return maxSellableShares * quoteDTO.bid * quoteDTO.toUSDRate;
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

    protected boolean hasValidInfoForSell()
    {
        return securityId != null
                && securityCompactDTO != null
                && quoteDTO != null
                && quoteDTO.bid != null;
    }

    @Override protected void updateConfirmButton(boolean forceDisable)
    {
        if (forceDisable)
        {
            mConfirm.setEnabled(false);
        }
        else
        {
            mConfirm.setEnabled(hasValidInfo() && mTransactionQuantity != 0);
        }
    }

    @Nullable public Integer getMaxPurchasableShares()
    {
        return portfolioCompactDTOUtil.getMaxPurchasableSharesForFX(
                portfolioCompactDTO,
                quoteDTO, false);
    }
}
