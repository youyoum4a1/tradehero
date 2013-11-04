package com.tradehero.th.persistence.portfolio;

import android.content.Context;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.DTOFetchAssistant;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/1/13 Time: 4:20 PM To change this template use File | Settings | File Templates. */
public class OwnedPortfolioFetchAssistant extends DTOFetchAssistant<OwnedPortfolioId, PortfolioDTO>
{
    public static final String TAG = OwnedPortfolioFetchAssistant.class.getSimpleName();

    @Inject protected Lazy<PortfolioCache> portfolioCache;
    private final Context context;

    public OwnedPortfolioFetchAssistant(Context context, List<OwnedPortfolioId> keysToFetch)
    {
        super(keysToFetch);
        this.context = context;
    }

    @Override public void onErrorThrown(OwnedPortfolioId key, Throwable error)
    {
        super.onErrorThrown(key, error);
        THToast.show(context.getString(R.string.error_fetch_portfolio_info));
    }

    @Override protected DTOCache<OwnedPortfolioId, PortfolioDTO> getCache()
    {
        return portfolioCache.get();
    }
}
