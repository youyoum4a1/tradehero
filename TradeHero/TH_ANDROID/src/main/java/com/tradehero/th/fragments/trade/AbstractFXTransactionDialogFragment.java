package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;

public abstract class AbstractFXTransactionDialogFragment extends AbstractTransactionDialogFragment
{
    private static final String KEY_QUANTITY = AbstractFXTransactionDialogFragment.class.getName() + ".quantity";

    public static AbstractFXTransactionDialogFragment newInstance(
            @NonNull SecurityId securityId,
            @NonNull PortfolioId portfolioId,
            @NonNull QuoteDTO quoteDTO,
            boolean isBuy,
            int closeUnits)
    {
        AbstractFXTransactionDialogFragment abstractBuySellDialogFragment = isBuy ? new BuyFXDialogFragment() : new SellFXDialogFragment();
        Bundle args = new Bundle();
        AbstractFXTransactionDialogFragment.putSecurityId(args, securityId);
        AbstractFXTransactionDialogFragment.putPortfolioId(args, portfolioId);
        AbstractFXTransactionDialogFragment.putQuoteDTO(args, quoteDTO);
        AbstractFXTransactionDialogFragment.putCloseUnitsQuantity(args, closeUnits);
        abstractBuySellDialogFragment.setArguments(args);
        return abstractBuySellDialogFragment;
    }

    public static void putCloseUnitsQuantity(@NonNull Bundle args, int quantity)
    {
        args.putInt(KEY_QUANTITY, quantity);
    }

    public static int getCloseUnitsQuantity(@NonNull Bundle args)
    {
        return args.getInt(KEY_QUANTITY, 0);
    }

    protected AbstractFXTransactionDialogFragment()
    {
        super();
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mTransactionQuantity = getCloseUnitsQuantity(getArguments());
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (getCloseUnitsQuantity(getArguments()) > 0)
        {
            mSeekBar.setMax(getCloseUnitsQuantity(getArguments()));
            mSeekBar.setEnabled(getCloseUnitsQuantity(getArguments()) > 0);
            mSeekBar.setProgress(getCloseUnitsQuantity(getArguments()));
        }
        mQuickPriceButtonSet.setPercent(true);
    }

    @Override protected int getCashLeftLabelResId()
    {
        Boolean isClosing = isClosingPosition();
        return isClosing != null && isClosing
                ? R.string.buy_sell_fx_quantity_left
                : R.string.buy_sell_fx_cash_left;
    }

    public void displayQuickPriceButtonSet()
    {
        QuickPriceButtonSet buttonSetCopy = mQuickPriceButtonSet;
        if (buttonSetCopy != null)
        {
            buttonSetCopy.setEnabled(isQuickButtonEnabled());
        }
    }
}
