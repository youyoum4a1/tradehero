package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;
import com.tradehero.th.utils.metrics.AnalyticsConstants;

public abstract class AbstractFXTransactionDialogFragment extends AbstractTransactionDialogFragment
{
    @InjectView(R.id.quick_price_button_set) protected QuickPriceButtonSet mQuickPriceButtonSet;

    private String mPriceSelectionMethod = AnalyticsConstants.DefaultPriceSelectionMethod;

    public static AbstractFXTransactionDialogFragment newInstance(
            @NonNull SecurityId securityId,
            @NonNull PortfolioId portfolioId,
            @NonNull QuoteDTO quoteDTO,
            boolean isBuy)
    {
        AbstractFXTransactionDialogFragment abstractBuySellDialogFragment = isBuy ? new BuyFXDialogFragment() : new SellFXDialogFragment();
        Bundle args = new Bundle();
        args.putBundle(KEY_SECURITY_ID, securityId.getArgs());
        args.putBundle(KEY_PORTFOLIO_ID, portfolioId.getArgs());
        args.putBundle(KEY_QUOTE_DTO, quoteDTO.getArgs());
        abstractBuySellDialogFragment.setArguments(args);
        return abstractBuySellDialogFragment;
    }

    protected AbstractFXTransactionDialogFragment()
    {
        super();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // TODO FX
        return inflater.inflate(R.layout.security_buy_sell_dialog, container, false);
    }

    protected void initViews()
    {
        super.initViews();
        mQuickPriceButtonSet.setListener(createQuickButtonSetListener());
        mProfitLossView.setVisibility(View.GONE);
    }

    public void displayQuickPriceButtonSet()
    {
        QuickPriceButtonSet buttonSetCopy = mQuickPriceButtonSet;
        if (buttonSetCopy != null)
        {
            buttonSetCopy.setEnabled(isQuickButtonEnabled());
            buttonSetCopy.setMaxPrice(getQuickButtonMaxValue());
        }
    }
}
