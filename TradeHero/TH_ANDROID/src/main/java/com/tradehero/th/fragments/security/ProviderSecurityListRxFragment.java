package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.trade.BuySellStockFragment;

public abstract class ProviderSecurityListRxFragment<ViewType extends View & DTOView<SecurityCompactDTO>> extends SecurityListRxFragment<ViewType>
{
    private static final String BUNDLE_PROVIDER_ID_KEY = ProviderSecurityListRxFragment.class.getName()+".providerId";
    protected ProviderId providerId;

    public static void putProviderId(@NonNull Bundle bundle, @NonNull ProviderId providerId)
    {
        bundle.putBundle(BUNDLE_PROVIDER_ID_KEY, providerId.getArgs());
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
        {
            this.providerId = new ProviderId(getArguments().getBundle(BUNDLE_PROVIDER_ID_KEY));
        }
    }

    @Override protected void handleDtoClicked(SecurityCompactDTO clicked)
    {
        super.handleDtoClicked(clicked);
        Bundle args = new Bundle();
        BuySellStockFragment.putSecurityId(args, clicked.getSecurityId());
        BuySellStockFragment.putApplicablePortfolioId(args, getApplicablePortfolioId());
        args.putBundle(BuySellStockFragment.BUNDLE_KEY_PROVIDER_ID_BUNDLE, providerId.getArgs());
        navigator.get().pushFragment(BuySellStockFragment.class, args);
    }
}
