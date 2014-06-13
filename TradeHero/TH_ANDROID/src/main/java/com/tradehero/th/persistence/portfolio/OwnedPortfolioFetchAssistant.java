package com.tradehero.th.persistence.portfolio;

import android.content.Context;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.DTOFetchAssistant;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

public class OwnedPortfolioFetchAssistant extends DTOFetchAssistant<OwnedPortfolioId, PortfolioDTO>
{
    @Inject protected Lazy<PortfolioCache> portfolioCache;
    private final Context context;

    public OwnedPortfolioFetchAssistant(Context context, List<OwnedPortfolioId> keysToFetch)
    {
        super(keysToFetch);
        this.context = context;
        DaggerUtils.inject(this);
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
