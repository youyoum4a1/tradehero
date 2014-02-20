package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.persistence.position.PositionCache;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by xavier on 2/5/14.
 */
public class TradeListFragment extends AbstractTradeListFragment<PositionDTO>
{
    public static final String BUNDLE_KEY_OWNED_POSITION_ID_BUNDLE = TradeListFragment.class.getName() + ".ownedPositionId";

    protected OwnedPositionId ownedPositionId;
    @Inject protected Lazy<PositionCache> positionCache;

    @Override protected void createAdapter()
    {
        adapter = new TradeListItemAdapter(getActivity(), getActivity().getLayoutInflater());
    }

    @Override public void onResume()
    {
        super.onResume();
        Bundle args = getArguments();
        if (args != null)
        {
            Bundle ownedPositionIdBundle = args.getBundle(BUNDLE_KEY_OWNED_POSITION_ID_BUNDLE);
            if (ownedPositionIdBundle != null)
            {
                linkWith(new OwnedPositionId(ownedPositionIdBundle), true);
            }
            else
            {
                Timber.d("ownedPositionIdBundle is null");
            }
        }
        else
        {
            Timber.d("args is null");
        }
    }

    public void linkWith(OwnedPositionId ownedPositionId, boolean andDisplay)
    {
        this.ownedPositionId = ownedPositionId;
        linkWith(positionCache.get().get(ownedPositionId), andDisplay);
        fetchTrades();

        if (andDisplay)
        {
            display();
        }
    }
}
