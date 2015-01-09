package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;

public abstract class AbstractFXTransactionDialogFragment extends AbstractTransactionDialogFragment
{
    protected static final String KEY_QUANTITY = AbstractFXTransactionDialogFragment.class.getName() + ".quantity";

    public static AbstractFXTransactionDialogFragment newInstance(
            @NonNull SecurityId securityId,
            @NonNull PortfolioId portfolioId,
            @NonNull QuoteDTO quoteDTO,
            boolean isBuy,
            int closeUnits)
    {
        AbstractFXTransactionDialogFragment abstractBuySellDialogFragment = isBuy ? new BuyFXDialogFragment() : new SellFXDialogFragment();
        Bundle args = new Bundle();
        args.putBundle(KEY_SECURITY_ID, securityId.getArgs());
        args.putBundle(KEY_PORTFOLIO_ID, portfolioId.getArgs());
        args.putBundle(KEY_QUOTE_DTO, quoteDTO.getArgs());
        args.putInt(KEY_QUANTITY, closeUnits);
        abstractBuySellDialogFragment.setArguments(args);
        return abstractBuySellDialogFragment;
    }

    protected AbstractFXTransactionDialogFragment()
    {
        super();
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mTransactionQuantity = getArguments().getInt(KEY_QUANTITY, 0);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments().getInt(KEY_QUANTITY, 0) > 0)
        {
            mSeekBar.setMax(getArguments().getInt(KEY_QUANTITY, 0));
            mSeekBar.setEnabled(getArguments().getInt(KEY_QUANTITY, 0) > 0);
            mSeekBar.setProgress(getArguments().getInt(KEY_QUANTITY, 0));
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
