package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;

public abstract class AbstractFXTransactionDialogFragment extends AbstractTransactionDialogFragment
{
    @InjectView(R.id.quick_price_button_set) protected QuickPriceButtonSet mQuickPriceButtonSet;

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
        abstractBuySellDialogFragment.setArguments(args);
        mTransactionQuantity = closeUnits;
        return abstractBuySellDialogFragment;
    }

    protected AbstractFXTransactionDialogFragment()
    {
        super();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // TODO FX
        return inflater.inflate(R.layout.fx_buy_sell_dialog, container, false);
    }

    protected void initViews()
    {
        super.initViews();
        mQuickPriceButtonSet.setListener(createQuickButtonSetListener());
    }

    public void displayQuickPriceButtonSet()
    {
        QuickPriceButtonSet buttonSetCopy = mQuickPriceButtonSet;
        if (buttonSetCopy != null)
        {
            buttonSetCopy.setFX(true);
            buttonSetCopy.setEnabled(isQuickButtonEnabled());
            buttonSetCopy.setMaxPrice(getQuickButtonMaxValue());
        }
    }

    @Nullable
    public Integer getMaxSellableShares()
    {
        return positionDTOCompactList == null ? null :
                positionDTOCompactList.getMaxSellableShares(
                        this.quoteDTO,
                        this.portfolioCompactDTO);
    }

    @Override
    protected QuickPriceButtonSet.OnQuickPriceButtonSelectedListener createQuickButtonSetListener() {
        return priceSelected -> {
            float i = 1f;
            switch ((int)priceSelected)
            {
                case 5000:
                    i = 0.25f;
                    break;
                case 10000:
                    i = 0.5f;
                    break;
                case 25000:
                    i = 0.75f;
                    break;
            }
            if (quoteDTO != null)
            {
                linkWithQuantity((int) Math.floor(i * getMaxValue()), true);
            }

            Integer selectedQuantity = mTransactionQuantity;
            mTransactionQuantity = selectedQuantity != null ? selectedQuantity : 0;
            updateTransactionDialog();
        };
    }
}
