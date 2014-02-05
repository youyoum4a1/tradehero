package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.persistence.position.PositionCache;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by xavier on 2/5/14.
 */
public class TradeListFragment extends AbstractTradeListFragment<PositionDTO>
{
    public static final String TAG = TradeListFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_OWNED_POSITION_ID_BUNDLE = TradeListFragment.class.getName() + ".ownedPositionId";

    protected OwnedPositionId ownedPositionId;
    @Inject protected Lazy<PositionCache> positionCache;

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
                THLog.d(TAG, "ownedPositionIdBundle is null");
            }
        }
        else
        {
            THLog.d(TAG, "args is null");
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
