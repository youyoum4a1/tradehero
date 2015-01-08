package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOUtil;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import com.tradehero.th.fragments.trade.BuySellFXFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import javax.inject.Inject;

public class ProviderFxListFragment extends ProviderSecurityListFragment
{
    @Inject Context dummyContext;
    @Inject SecurityCompactDTOUtil securityCompactDTOUtil;

    @Override protected ListAdapter createSecurityItemViewAdapter()
    {
        return new SimpleSecurityItemViewAdapter(
                getActivity(),
                R.layout.trending_fx_item);
    }

    @Override protected AdapterView.OnItemClickListener createOnItemClickListener()
    {
        return new OnFxViewClickListener();
    }

    private class OnFxViewClickListener implements AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) parent.getItemAtPosition(position);

            Bundle args = new Bundle();
            BuySellFXFragment.putSecurityId(args, securityCompactDTO.getSecurityId());

            OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();

            if (ownedPortfolioId != null)
            {
                BuySellFXFragment.putApplicablePortfolioId(args, ownedPortfolioId);
            }

            navigator.get().pushFragment(BuySellFXFragment.class, args);
        }
    }
}
